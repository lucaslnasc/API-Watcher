package com.apiwatcher.monitoring.infrastructure.timeseries.document;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Documento MongoDB: Histórico de registro de APIs.
 * 
 * Time-Series Collection para auditoria de novas APIs cadastradas.
 */
@Document(collection = "api_registrations")
public class ApiRegistrationHistory {

  @Id
  private String id;

  @Indexed
  private String apiId;

  private String name;
  private String url;
  private String httpMethod;
  private int expectedStatusCode;
  private int latencyThresholdMs;

  @Indexed
  private LocalDateTime registeredAt;

  // Metadados do evento
  private String eventId;
  private String eventType;

  public ApiRegistrationHistory() {
  }

  public ApiRegistrationHistory(
      String apiId,
      String name,
      String url,
      String httpMethod,
      int expectedStatusCode,
      int latencyThresholdMs,
      LocalDateTime registeredAt,
      String eventId,
      String eventType) {
    this.apiId = apiId;
    this.name = name;
    this.url = url;
    this.httpMethod = httpMethod;
    this.expectedStatusCode = expectedStatusCode;
    this.latencyThresholdMs = latencyThresholdMs;
    this.registeredAt = registeredAt;
    this.eventId = eventId;
    this.eventType = eventType;
  }

  // Getters e Setters

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getApiId() {
    return apiId;
  }

  public void setApiId(String apiId) {
    this.apiId = apiId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getHttpMethod() {
    return httpMethod;
  }

  public void setHttpMethod(String httpMethod) {
    this.httpMethod = httpMethod;
  }

  public int getExpectedStatusCode() {
    return expectedStatusCode;
  }

  public void setExpectedStatusCode(int expectedStatusCode) {
    this.expectedStatusCode = expectedStatusCode;
  }

  public int getLatencyThresholdMs() {
    return latencyThresholdMs;
  }

  public void setLatencyThresholdMs(int latencyThresholdMs) {
    this.latencyThresholdMs = latencyThresholdMs;
  }

  public LocalDateTime getRegisteredAt() {
    return registeredAt;
  }

  public void setRegisteredAt(LocalDateTime registeredAt) {
    this.registeredAt = registeredAt;
  }

  public String getEventId() {
    return eventId;
  }

  public void setEventId(String eventId) {
    this.eventId = eventId;
  }

  public String getEventType() {
    return eventType;
  }

  public void setEventType(String eventType) {
    this.eventType = eventType;
  }

  @Override
  public String toString() {
    return "ApiRegistrationHistory[id=%s, apiId=%s, name=%s, url=%s, registeredAt=%s"
        .formatted(id, apiId, name, url, registeredAt);
  }
}
