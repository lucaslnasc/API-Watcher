package com.apiwatcher.monitoring.infrastructure.messaging;

import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import com.apiwatcher.shared.events.DomainEvent;
import com.apiwatcher.shared.events.EventPublisher;

/**
 * Implementação do EventPublisher usando Kafka.
 * 
 * Esta classe é um ADAPTER (Hexagonal Architecture):
 * - O domínio conhece apenas a interface EventPublisher
 * - A infraestrutura (Kafka) fica isolada aqui
 * - Se trocarmos Kafka por RabbitMQ, só mudamos este adapter
 * 
 * Como funciona:
 * 1. Recebe um DomainEvent
 * 2. Determina o tópico baseado no tipo do evento
 * 3. Envia para o Kafka usando KafkaTemplate
 * 4. Loga sucesso ou erro
 */
@Component
public class KafkaEventPublisher implements EventPublisher {
  private static final Logger logger = LoggerFactory.getLogger(KafkaEventPublisher.class);

  // Nome dos tópicos Kafka onde os eventos serão publicados
  private static final String TOPIC_HEALTH_CHECK = "health-check";
  private static final String TOPIC_API_REGISTERED = "api-registered";

  private final KafkaTemplate<String, Object> kafkaTemplate;

  public KafkaEventPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
    this.kafkaTemplate = kafkaTemplate;
  }

  @Override
  public void publish(DomainEvent event) {
    String topic = determineTopicFromEventType(event.getEventType());
    String key = extractKeyFromEvent(event);

    logger.info("[KAFKA-SEND] Publicando evento: {} no topico: {}", event.getEventType(), topic);

    // Envio assíncrono para o Kafka
    CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(topic, key, event);

    // Callback para sucesso ou erro
    future.whenComplete((result, exception) -> {
      if (exception == null) {
        logger.info("[KAFKA-SUCCESS] Evento publicado: {} [partition={}, offset={}]",
            event.getEventType(),
            result.getRecordMetadata().partition(),
            result.getRecordMetadata().offset());
      } else {
        logger.error("[KAFKA-ERROR] Erro ao publicar evento: {} - Erro: {}",
            event.getEventType(),
            exception.getMessage(),
            exception);
      }
    });
  }

  /**
   * Determina em qual tópico o evento deve ser publicado.
   * 
   * Estratégia: Um tópico por tipo de evento relacionado.
   * - health-check.* → health-check
   * - api.* → api-registered
   */
  private String determineTopicFromEventType(String eventType) {
    if (eventType.startsWith("health-check")) {
      return TOPIC_HEALTH_CHECK;
    } else if (eventType.startsWith("api")) {
      return TOPIC_API_REGISTERED;
    }

    // Fallback: tópico genérico
    return "domain-events";
  }

  /**
   * Extrai a chave do evento.
   * 
   * A chave é importante para:
   * - Garantir ordem de processamento (mesma chave = mesma partition)
   * - Facilitar compactação de logs
   * - Distribuição balanceada entre partitions
   * 
   * No nosso caso: usamos o eventId como chave
   */
  private String extractKeyFromEvent(DomainEvent event) {
    return event.getEventId();
  }
}
