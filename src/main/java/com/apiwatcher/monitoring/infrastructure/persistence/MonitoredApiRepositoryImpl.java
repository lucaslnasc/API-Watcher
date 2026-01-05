package com.apiwatcher.monitoring.infrastructure.persistence;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.apiwatcher.monitoring.domain.model.MonitoredApi;
import com.apiwatcher.monitoring.domain.repository.MonitoredApiRepository;

/**
 * Implementação do repositório usando JPA.
 * Adapta entre Domain Model e JPA Entity.
 */
@Component
public class MonitoredApiRepositoryImpl implements MonitoredApiRepository {

  private final MonitoredApiJpaRepository jpaRepository;

  public MonitoredApiRepositoryImpl(MonitoredApiJpaRepository jpaRepository) {
    this.jpaRepository = jpaRepository;
  }

  @Override
  public MonitoredApi save(MonitoredApi api) {
    MonitoredApiEntity entity = toEntity(api);
    MonitoredApiEntity saved = jpaRepository.save(entity);
    return toDomain(saved);
  }

  @Override
  public Optional<MonitoredApi> findById(String id) {
    return jpaRepository.findById(id).map(this::toDomain);
  }

  @Override
  public List<MonitoredApi> findAllActive() {
    return jpaRepository.findAllActive()
        .stream()
        .map(this::toDomain)
        .collect(Collectors.toList());
  }

  @Override
  public List<MonitoredApi> findAll() {
    return jpaRepository.findAll()
        .stream()
        .map(this::toDomain)
        .collect(Collectors.toList());
  }

  @Override
  public void deleteById(String id) {
    jpaRepository.deleteById(id);
  }

  @Override
  public boolean existsByUrl(String url) {
    return jpaRepository.existsByUrl(url);
  }

  // Mappers
  private MonitoredApiEntity toEntity(MonitoredApi domain) {
    MonitoredApiEntity entity = new MonitoredApiEntity();
    entity.setId(domain.getId());
    entity.setName(domain.getName());
    entity.setUrl(domain.getUrl());
    entity.setHttpMethod(domain.getHttpMethod());
    entity.setExpectedStatusCode(domain.getExpectedStatusCode());
    entity.setLatencyThresholdMs(domain.getLatencyThresholdMs());
    entity.setActive(domain.isActive());
    entity.setCreatedAt(domain.getCreatedAt());
    entity.setUpdatedAt(domain.getUpdatedAt());
    return entity;
  }

  private MonitoredApi toDomain(MonitoredApiEntity entity) {
    return new MonitoredApi(
        entity.getId(),
        entity.getName(),
        entity.getUrl(),
        entity.getHttpMethod(),
        entity.getExpectedStatusCode(),
        entity.getLatencyThresholdMs(),
        entity.getActive(),
        entity.getCreatedAt(),
        entity.getUpdatedAt());
  }
}
