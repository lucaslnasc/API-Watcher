package com.apiwatcher.monitoring.infrastructure.persistence;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import com.apiwatcher.monitoring.domain.model.MonitoredApi;
import com.apiwatcher.monitoring.domain.repository.MonitoredApiRepository;

/**
 * Implementação do repositório usando JPA com cache Redis.
 * 
 * Estratégia de Cache:
 * - findAllActive(): Cache de 5 minutos (consulta frequente pelos health
 * checks)
 * - findById(): Cache de 15 minutos (dados raramente mudam)
 * - findAll(): Cache de 10 minutos (consulta menos frequente)
 * - save/delete: Invalidam todos os caches relacionados
 */
@Component
public class MonitoredApiRepositoryImpl implements MonitoredApiRepository {

  private static final Logger log = LoggerFactory.getLogger(MonitoredApiRepositoryImpl.class);

  private final MonitoredApiJpaRepository jpaRepository;

  public MonitoredApiRepositoryImpl(MonitoredApiJpaRepository jpaRepository) {
    this.jpaRepository = jpaRepository;
  }

  @Override
  @CacheEvict(value = { "monitored-apis", "monitored-api-by-id", "all-apis" }, allEntries = true)
  public MonitoredApi save(MonitoredApi api) {
    log.debug("Salvando API e invalidando caches: {}", api.getName());
    MonitoredApiEntity entity = toEntity(api);
    MonitoredApiEntity saved = jpaRepository.save(entity);
    return toDomain(saved);
  }

  @Override
  @Cacheable(value = "monitored-api-by-id", key = "#id")
  public Optional<MonitoredApi> findById(String id) {
    log.debug("Buscando API por ID (cache miss): {}", id);
    return jpaRepository.findById(id).map(this::toDomain);
  }

  @Override
  @Cacheable(value = "monitored-apis", key = "'active'")
  public List<MonitoredApi> findAllActive() {
    log.debug("Buscando APIs ativas (cache miss)");
    return jpaRepository.findAllActive()
        .stream()
        .map(this::toDomain)
        .collect(Collectors.toList());
  }

  @Override
  @Cacheable(value = "all-apis", key = "'all'")
  public List<MonitoredApi> findAll() {
    log.debug("Buscando todas as APIs (cache miss)");
    return jpaRepository.findAll()
        .stream()
        .map(this::toDomain)
        .collect(Collectors.toList());
  }

  @Override
  @CacheEvict(value = { "monitored-apis", "monitored-api-by-id", "all-apis" }, allEntries = true)
  public void deleteById(String id) {
    log.debug("Deletando API e invalidando caches: {}", id);
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
