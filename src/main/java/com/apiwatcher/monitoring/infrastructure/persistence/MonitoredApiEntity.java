package com.apiwatcher.monitoring.infrastructure.persistence;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Entidade JPA para persistência em PostgreSQL.
 * Adaptador entre o Domain Model e o banco de dados.
 */
@Entity
@Table(name = "monitored_apis")
public class MonitoredApiEntity {

  @Id
  private String id;

  @Column(nullable = false, length = 100)
  private String name;

  @Column(nullable = false, unique = true, length = 500)
  private String url;

  @Column(nullable = false, length = 10)
  private String httpMethod;

  @Column(nullable = false)
  private Integer expectedStatusCode;

  @Column(nullable = false)
  private Integer latencyThresholdMs;

  @Column(nullable = false)
  private Boolean active;

  @Column(nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @Column(nullable = false)
  private LocalDateTime updatedAt;

  // Construtores
  public MonitoredApiEntity() {
  }

  // Getters e Setters
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
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

  public Integer getExpectedStatusCode() {
    return expectedStatusCode;
  }

  public void setExpectedStatusCode(Integer expectedStatusCode) {
    this.expectedStatusCode = expectedStatusCode;
  }

  public Integer getLatencyThresholdMs() {
    return latencyThresholdMs;
  }

  public void setLatencyThresholdMs(Integer latencyThresholdMs) {
    this.latencyThresholdMs = latencyThresholdMs;
  }

  public Boolean getActive() {
    return active;
  }

  public void setActive(Boolean active) {
    this.active = active;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(LocalDateTime updatedAt) {
    this.updatedAt = updatedAt;
  }
}
