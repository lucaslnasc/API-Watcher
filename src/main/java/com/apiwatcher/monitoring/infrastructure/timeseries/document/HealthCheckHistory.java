package com.apiwatcher.monitoring.infrastructure.timeseries.document;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "health_checks")
@CompoundIndex(name = "api_time_idx", def = "{'apiId': 1, 'checkedAt': -1}")
public class HealthCheckHistory {

  @Id
  private String id;

  // Identicação da API
  @Indexed
  private String apiId;
  private String apiName;
  private String apiUrl;

  // Resultado do Health Check
  private boolean success;
  private int statusCode;
  private long latencyMs;
  private String errorMessage;

  // Análise de Thresholds
  private boolean exceededThreshold;
  private int thresholdMs;

  // Timestamp
  @Indexed
  private LocalDateTime checkedAt;

  // Metadados do evento
  private String eventId;
  private String eventType;

  public HealthCheckHistory() {
  }

  public HealthCheckHistory(
      String apiId,
      String apiName,
      String apiUrl,
      boolean success,
      int statusCode,
      long latencyMs,
      String errorMessage,
      boolean exceededThreshold,
      int thresholdMs,
      LocalDateTime checkedAt,
      String eventId,
      String eventType) {
    this.apiId = apiId;
    this.apiName = apiName;
    this.apiUrl = apiUrl;
    this.success = success;
    this.statusCode = statusCode;
    this.latencyMs = latencyMs;
    this.errorMessage = errorMessage;
    this.exceededThreshold = exceededThreshold;
    this.thresholdMs = thresholdMs;
    this.checkedAt = checkedAt;
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

  public String getApiName() {
    return apiName;
  }

  public void setApiName(String apiName) {
    this.apiName = apiName;
  }

  public String getApiUrl() {
    return apiUrl;
  }

  public void setApiUrl(String apiUrl) {
    this.apiUrl = apiUrl;
  }

  public boolean isSuccess() {
    return success;
  }

  public void setSuccess(boolean success) {
    this.success = success;
  }

  public int getStatusCode() {
    return statusCode;
  }

  public void setStatusCode(int statusCode) {
    this.statusCode = statusCode;
  }

  public long getLatencyMs() {
    return latencyMs;
  }

  public void setLatencyMs(long latencyMs) {
    this.latencyMs = latencyMs;
  }

  public String getErrorMessage() {
    return errorMessage;
  }

  public void setErrorMessage(String errorMessage) {
    this.errorMessage = errorMessage;
  }

  public boolean isExceededThreshold() {
    return exceededThreshold;
  }

  public void setExceededThreshold(boolean exceededThreshold) {
    this.exceededThreshold = exceededThreshold;
  }

  public int getThresholdMs() {
    return thresholdMs;
  }

  public void setThresholdMs(int thresholdMs) {
    this.thresholdMs = thresholdMs;
  }

  public LocalDateTime getCheckedAt() {
    return checkedAt;
  }

  public void setCheckedAt(LocalDateTime checkedAt) {
    this.checkedAt = checkedAt;
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
    return "HealthCheckHistory[id=%s, apiName=%s, success=%s, latency=%dms, checkedAt=%s]"
        .formatted(id, apiName, success, latencyMs, checkedAt);
  }
}
