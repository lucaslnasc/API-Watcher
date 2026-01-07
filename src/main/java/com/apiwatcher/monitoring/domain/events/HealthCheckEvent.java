package com.apiwatcher.monitoring.domain.events;

import java.time.LocalDateTime;

import com.apiwatcher.monitoring.domain.model.CheckResult;
import com.apiwatcher.shared.events.DomainEvent;

/**
 * Evento de domínio: Health check executado em uma API.
 * 
 * Este evento é publicado sempre que um health check é realizado,
 * seja bem-sucedido ou com falha.
 * 
 * Será consumido para:
 * - Persistir histórico no MongoDB
 * - Gerar alertas (futuro)
 * - Atualizar métricas (Prometheus)
 */
public class HealthCheckEvent extends DomainEvent {

  // Identificação
  private final String apiId;
  private final String apiName;
  private final String apiUrl;

  // Resultado do Check
  private final boolean success;
  private final int statusCode;
  private final long latencyMs;
  private final String errorMessage;

  // Metadados
  private final LocalDateTime checkedAt;
  private final boolean exceededThreshold;
  private final int thresholdMs;

  /**
   * Cria evento a partir de um CheckResult
   */
  public static HealthCheckEvent from(CheckResult checkResult, String apiName, String apiUrl, int thresholdMs) {
    return new HealthCheckEvent(
        checkResult.getApiId(),
        apiName,
        apiUrl,
        checkResult.isSuccess(),
        checkResult.getStatusCode(),
        checkResult.getLatencyMs(),
        checkResult.getErrorMessage(),
        checkResult.getCheckedAt(),
        checkResult.exceededThreshold(thresholdMs),
        thresholdMs);
  }

  private HealthCheckEvent(
      String apiId,
      String apiName,
      String apiUrl,
      boolean success,
      int statusCode,
      long latencyMs,
      String errorMessage,
      LocalDateTime checkedAt,
      boolean exceededThreshold,
      int thresholdMs) {
    super();
    this.apiId = apiId;
    this.apiName = apiName;
    this.apiUrl = apiUrl;
    this.success = success;
    this.statusCode = statusCode;
    this.latencyMs = latencyMs;
    this.errorMessage = errorMessage;
    this.checkedAt = checkedAt;
    this.exceededThreshold = exceededThreshold;
    this.thresholdMs = thresholdMs;
  }

  @Override
  public String getEventType() {
    return "health-check.executed";
  }

  // Métodos auxiliares para categorização
  public boolean isHealthy() {
    return success && statusCode >= 200 && statusCode < 300;
  }

  public boolean isCritical() {
    return !success || statusCode >= 500;
  }

  public HealthStatus getStatus() {
    if (!success) {
      return HealthStatus.DOWN;
    }
    if (exceededThreshold) {
      return HealthStatus.DEGRADED;
    }
    return HealthStatus.UP;
  }

  // Getters
  public String getApiId() {
    return apiId;
  }

  public String getApiName() {
    return apiName;
  }

  public String getApiUrl() {
    return apiUrl;
  }

  public boolean isSuccess() {
    return success;
  }

  public int getStatusCode() {
    return statusCode;
  }

  public long getLatencyMs() {
    return latencyMs;
  }

  public String getErrorMessage() {
    return errorMessage;
  }

  public LocalDateTime getCheckedAt() {
    return checkedAt;
  }

  public boolean isExceededThreshold() {
    return exceededThreshold;
  }

  public int getThresholdMs() {
    return thresholdMs;
  }

  @Override
  public String toString() {
    return String.format(
        "HealthCheckEvent[eventId=%s, apiName=%s, status=%s, latency=%dms, timestamp=%s]",
        getEventId(),
        apiName,
        getStatus(),
        latencyMs,
        checkedAt);
  }

  /**
   * Enum para status de saúde da API
   */
  public enum HealthStatus {
    UP, // Funcionando normalmente
    DEGRADED, // Funcionando mas com latência alta
    DOWN // Com falha
  }
}
