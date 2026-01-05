package com.apiwatcher.monitoring.infrastructure.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA Repository.
 * Fornece operações CRUD prontas para uso.
 */
@Repository
public interface MonitoredApiJpaRepository extends JpaRepository<MonitoredApiEntity, String> {

  @Query("SELECT m FROM MonitoredApiEntity m WHERE m.active = true")
  List<MonitoredApiEntity> findAllActive();

  boolean existsByUrl(String url);
}
