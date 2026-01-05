package com.apiwatcher.shared.events;

/**
 * Interface para publicação de eventos de domínio.
 * Desacopla o domínio da infraestrutura de mensageria (Kafka).
 */
public interface EventPublisher {

  /**
   * Publica um evento de domínio.
   * 
   * @param event O evento a ser publicado
   */
  void publish(DomainEvent event);
}
