package com.apiwatcher.monitoring.infrastructure.messaging;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.apiwatcher.monitoring.infrastructure.timeseries.document.ApiRegistrationHistory;
import com.apiwatcher.monitoring.infrastructure.timeseries.document.HealthCheckHistory;
import com.apiwatcher.monitoring.infrastructure.timeseries.repository.ApiRegistrationHistoryRepository;
import com.apiwatcher.monitoring.infrastructure.timeseries.repository.HealthCheckHistoryRepository;

@Component
public class KafkaEventConsumer {
  private static final Logger logger = LoggerFactory.getLogger(KafkaEventConsumer.class);

  private final ApiRegistrationHistoryRepository apiRegistrationRepository;
  private final HealthCheckHistoryRepository healthCheckRepository;

  public KafkaEventConsumer(
      ApiRegistrationHistoryRepository apiRegistrationRepository,
      HealthCheckHistoryRepository healthCheckRepository) {
    this.apiRegistrationRepository = apiRegistrationRepository;
    this.healthCheckRepository = healthCheckRepository;
  }

  /**
   * Consome eventos de registro de API
   */
  @KafkaListener(topics = "api-registered", groupId = "${spring.kafka.consumer.group-id}", containerFactory = "kafkaListenerContainerFactory")
  public void consumeApiRegisteredEvent(
      @Payload Map<String, Object> eventData,
      @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
      @Header(KafkaHeaders.RECEIVED_PARTITION) int partition) {

    String eventId = (String) eventData.get("eventId");
    logger.info("[KAFKA-RECEIVE] Evento recebido: topic={}, partition={}, eventId={}",
        topic, partition, eventId);

    try {
      ApiRegistrationHistory history = new ApiRegistrationHistory(
          (String) eventData.get("apiId"),
          (String) eventData.get("name"),
          (String) eventData.get("url"),
          (String) eventData.get("httpMethod"),
          (Integer) eventData.get("expectedStatusCode"),
          (Integer) eventData.get("latencyThresholdMs"),
          parseLocalDateTime(eventData.get("occurredOn")),
          eventId,
          (String) eventData.get("eventType"));

      apiRegistrationRepository.save(history);
      logger.info("[MONGODB] Historico de registro salvo: API={}, EventId={}",
          eventData.get("name"), eventId);

    } catch (Exception e) {
      logger.error("[MONGODB-ERROR] Erro ao processar evento de registro: {}", e.getMessage(), e);
    }
  }

  /**
   * Consome eventos de health check
   */
  @KafkaListener(topics = "health-check", groupId = "${spring.kafka.consumer.group-id}", containerFactory = "kafkaListenerContainerFactory")
  public void consumeHealthCheck(
      @Payload Map<String, Object> eventData,
      @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
      @Header(KafkaHeaders.RECEIVED_PARTITION) int partition) {

    String eventId = (String) eventData.get("eventId");
    String apiName = (String) eventData.get("apiName");
    Boolean success = (Boolean) eventData.get("success");

    logger.debug("[KAFKA-RECEIVE] Health check recebido: topic={}, api={}, success={}",
        topic, apiName, success);

    try {
      HealthCheckHistory history = new HealthCheckHistory(
          (String) eventData.get("apiId"),
          apiName,
          (String) eventData.get("apiUrl"),
          success,
          (Integer) eventData.get("statusCode"),
          ((Number) eventData.get("latencyMs")).longValue(),
          (String) eventData.get("errorMessage"),
          (Boolean) eventData.get("exceededThreshold"),
          (Integer) eventData.get("thresholdMs"),
          parseLocalDateTime(eventData.get("checkedAt")),
          eventId,
          (String) eventData.get("eventType"));

      healthCheckRepository.save(history);

      String status = success ? "[OK]" : "[FALHA]";
      logger.debug("{} Health check salvo no MongoDB: API={}", status, apiName);

    } catch (Exception e) {
      logger.error("[MONGODB-ERROR] Erro ao processar health check: {}", e.getMessage(), e);
    }
  }

  /**
   * Converte o array de LocalDateTime do JSON para LocalDateTime
   */
  @SuppressWarnings("unchecked")
  private LocalDateTime parseLocalDateTime(Object dateArray) {
    if (dateArray instanceof List) {
      List<Integer> parts = (List<Integer>) dateArray;
      return LocalDateTime.of(
          parts.get(0), // year
          parts.get(1), // month
          parts.get(2), // day
          parts.get(3), // hour
          parts.get(4), // minute
          parts.get(5), // second
          parts.size() > 6 ? parts.get(6) : 0 // nano
      );
    }
    return LocalDateTime.now();
  }
}
