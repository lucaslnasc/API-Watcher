package com.apiwatcher.shared.events;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Classe base para todos os eventos de domínio.
 * Eventos representam fatos que já aconteceram no sistema.
 */
public abstract class DomainEvent {

  private final String eventId;
  private final LocalDateTime occurredOn;

  protected DomainEvent() {
    this.eventId = UUID.randomUUID().toString();
    this.occurredOn = LocalDateTime.now();
  }

  public String getEventId() {
    return eventId;
  }

  public LocalDateTime getOccurredOn() {
    return occurredOn;
  }

  /**
   * Nome do evento para roteamento no Kafka
   */
  public abstract String getEventType();
}
