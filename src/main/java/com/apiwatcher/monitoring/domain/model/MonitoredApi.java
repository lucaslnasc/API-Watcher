package com.apiwatcher.monitoring.domain.model;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

import com.apiwatcher.shared.exceptions.DomainException;

/**
 * Entidade de domínio: API a ser monitorada.
 * Representa o conceito central do bounded context de Monitoring.
 */
public class MonitoredApi {

  private String id;
  private String name;
  private String url;
  private String httpMethod;
  private Integer expectedStatusCode;
  private Integer latencyThresholdMs;
  private boolean active;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  // Construtor para criação
  public MonitoredApi(String name, String url, String httpMethod, Integer expectedStatusCode,
      Integer latencyThresholdMs) {
    validateName(name);
    validateUrl(url);
    validateHttpMethod(httpMethod);
    validateStatusCode(expectedStatusCode);
    validateLatencyThreshold(latencyThresholdMs);

    this.id = UUID.randomUUID().toString();
    this.name = name;
    this.url = url;
    this.httpMethod = httpMethod.toUpperCase();
    this.expectedStatusCode = expectedStatusCode;
    this.latencyThresholdMs = latencyThresholdMs;
    this.active = true;
    this.createdAt = LocalDateTime.now();
    this.updatedAt = LocalDateTime.now();
  }

  // Construtor para reconstrução (repository)
  public MonitoredApi(String id, String name, String url, String httpMethod, Integer expectedStatusCode,
      Integer latencyThresholdMs, boolean active, LocalDateTime createdAt, LocalDateTime updatedAt) {
    this.id = id;
    this.name = name;
    this.url = url;
    this.httpMethod = httpMethod;
    this.expectedStatusCode = expectedStatusCode;
    this.latencyThresholdMs = latencyThresholdMs;
    this.active = active;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  // Métodos de negócio
  public void deactivate() {
    this.active = false;
    this.updatedAt = LocalDateTime.now();
  }

  public void activate() {
    this.active = true;
    this.updatedAt = LocalDateTime.now();
  }

  public void updateThreshold(Integer newThresholdMs) {
    validateLatencyThreshold(newThresholdMs);
    this.latencyThresholdMs = newThresholdMs;
    this.updatedAt = LocalDateTime.now();
  }

  // Validações
  private void validateName(String name) {
    if (name == null || name.trim().isEmpty()) {
      throw new DomainException("Nome da API não pode ser vazio");
    }
    if (name.length() > 100) {
      throw new DomainException("Nome da API não pode ter mais de 100 caracteres");
    }
  }

  private void validateUrl(String url) {
    if (url == null || url.trim().isEmpty()) {
      throw new DomainException("URL não pode ser vazia");
    }
    if (!url.startsWith("http://") && !url.startsWith("https://")) {
      throw new DomainException("URL deve começar com http:// ou https://");
    }
  }

  private void validateHttpMethod(String method) {
    if (method == null || method.trim().isEmpty()) {
      throw new DomainException("Método HTTP não pode ser vazio");
    }
    String upperMethod = method.toUpperCase();
    if (!upperMethod.matches("GET|POST|PUT|DELETE|PATCH|HEAD|OPTIONS")) {
      throw new DomainException("Método HTTP inválido: " + method);
    }
  }

  private void validateStatusCode(Integer statusCode) {
    if (statusCode == null) {
      throw new DomainException("Status code esperado não pode ser nulo");
    }
    if (statusCode < 100 || statusCode > 599) {
      throw new DomainException("Status code deve estar entre 100 e 599");
    }
  }

  private void validateLatencyThreshold(Integer threshold) {
    if (threshold == null) {
      throw new DomainException("Threshold de latência não pode ser nulo");
    }
    if (threshold < 0) {
      throw new DomainException("Threshold de latência não pode ser negativo");
    }
  }

  // Getters
  public String getId() {
    return id;
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

  public Integer getExpectedStatusCode() {
    return expectedStatusCode;
  }

  public Integer getLatencyThresholdMs() {
    return latencyThresholdMs;
  }

  public boolean isActive() {
    return active;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    MonitoredApi that = (MonitoredApi) o;
    return Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
