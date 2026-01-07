package com.apiwatcher.monitoring.domain.events;

import com.apiwatcher.monitoring.domain.model.MonitoredApi;
import com.apiwatcher.shared.events.DomainEvent;

/**
 * Evento de domínio: Nova API registrada para monitoramento.
 * 
 * Publicado quando uma API é cadastrada no sistema.
 * 
 * Pode ser usado para:
 * - Notificar equipes sobre novas APIs monitoradas
 * - Registrar auditoria
 * - Inicializar dashboards automaticamente
 */
public class ApiRegisteredEvent extends DomainEvent {

  private final String apiId;
  private final String name;
  private final String url;
  private final String httpMethod;
  private final int expectedStatusCode;
  private final int latencyThresholdMs;

  public static ApiRegisteredEvent from(MonitoredApi api) {
    return new ApiRegisteredEvent(
        api.getId(),
        api.getName(),
        api.getUrl(),
        api.getHttpMethod(),
        api.getExpectedStatusCode(),
        api.getLatencyThresholdMs());
  }

  private ApiRegisteredEvent(
      String apiId,
      String name,
      String url,
      String httpMethod,
      int expectedStatusCode,
      int latencyThresholdMs) {
    super();
    this.apiId = apiId;
    this.name = name;
    this.url = url;
    this.httpMethod = httpMethod;
    this.expectedStatusCode = expectedStatusCode;
    this.latencyThresholdMs = latencyThresholdMs;
  }

  @Override
  public String getEventType() {
    return "api.registered";
  }

  // Getters
  public String getApiId() {
    return apiId;
  }

  public String getName() {
    return name;
  }

  public String getUrl() {
    return url;
  }

  public String getHttpMethod() {
    return httpMethod;
  }

  public int getExpectedStatusCode() {
    return expectedStatusCode;
  }

  public int getLatencyThresholdMs() {
    return latencyThresholdMs;
  }

  @Override
  public String toString() {
    return String.format(
        "ApiRegisteredEvent[eventId=%s, name=%s, url=%s, threshold=%dms]",
        getEventId(),
        name,
        url,
        latencyThresholdMs);
  }
}
