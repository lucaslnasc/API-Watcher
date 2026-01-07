package com.apiwatcher.monitoring.infrastructure.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.apiwatcher.monitoring.domain.events.ApiRegisteredEvent;
import com.apiwatcher.monitoring.domain.events.HealthCheckEvent;
import com.apiwatcher.monitoring.infrastructure.timeseries.document.ApiRegistrationHistory;
import com.apiwatcher.monitoring.infrastructure.timeseries.document.HealthCheckHistory;
import com.apiwatcher.monitoring.infrastructure.timeseries.repository.ApiRegistrationHistoryRepository;
import com.apiwatcher.monitoring.infrastructure.timeseries.repository.HealthCheckHistoryRepository;

@Service
public class HistoryEventService {

  private static final Logger logger = LoggerFactory.getLogger(HistoryEventService.class);

  private final ApiRegistrationHistoryRepository apiRegistrationHistoryRepository;
  private final HealthCheckHistoryRepository healthCheckHistoryRepository;

  public HistoryEventService(
      ApiRegistrationHistoryRepository apiRegistrationHistoryRepository,
      HealthCheckHistoryRepository healthCheckHistoryRepository) {
    this.apiRegistrationHistoryRepository = apiRegistrationHistoryRepository;
    this.healthCheckHistoryRepository = healthCheckHistoryRepository;
  }

  /**
   * Persiste evento de registro de API
   */
  public void saveApiRegistration(ApiRegisteredEvent event) {
    try {
      ApiRegistrationHistory history = new ApiRegistrationHistory(
          event.getApiId(),
          event.getName(),
          event.getUrl(),
          event.getHttpMethod(),
          event.getExpectedStatusCode(),
          event.getLatencyThresholdMs(),
          event.getOccurredOn(),
          event.getEventId(),
          event.getEventType());

      apiRegistrationHistoryRepository.save(history);
      logger.info("✅ Histórico de registro salvo no MongoDB: API={}, EventId={}",
          event.getName(), event.getEventId());

    } catch (Exception e) {
      logger.error("❌ Erro ao salvar histórico de registro no MongoDB: {}", e.getMessage(), e);
    }
  }

  /**
   * Persiste evento de health check
   */
  public void saveHealthCheck(HealthCheckEvent event) {
    try {
      HealthCheckHistory history = new HealthCheckHistory(
          event.getApiId(),
          event.getApiName(),
          event.getApiUrl(),
          event.isSuccess(),
          event.getStatusCode(),
          event.getLatencyMs(),
          event.getErrorMessage(),
          event.isExceededThreshold(),
          event.getThresholdMs(),
          event.getCheckedAt(),
          event.getEventId(),
          event.getEventType());

      healthCheckHistoryRepository.save(history);
      String status = event.isSuccess() ? "✅ OK" : "❌ FALHA";
      logger.debug("{} Health check salvo no MongoDB: API={}, Latency={}ms",
          status, event.getApiName(), event.getLatencyMs());
    } catch (Exception e) {
      logger.error("❌ Erro ao salvar health check no MongoDB: {}", e.getMessage(), e);
    }
  }
}
