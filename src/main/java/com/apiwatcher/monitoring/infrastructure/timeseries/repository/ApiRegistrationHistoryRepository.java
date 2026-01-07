package com.apiwatcher.monitoring.infrastructure.timeseries.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.apiwatcher.monitoring.infrastructure.timeseries.document.ApiRegistrationHistory;

/**
 * Repositório MongoDB para histórico de registro de APIs.
 */
@Repository
public interface ApiRegistrationHistoryRepository extends MongoRepository<ApiRegistrationHistory, String> {

  /**
   * Busca todos os registros de uma API específica
   */
  List<ApiRegistrationHistory> findByApiId(String apiId);

  /**
   * Busca registros em um período específico
   */
  List<ApiRegistrationHistory> findByRegisteredAtBetween(LocalDateTime start, LocalDateTime end);

  /**
   * Busca os N registros mais recentes
   */
  List<ApiRegistrationHistory> findTop10ByOrderByRegisteredAtDesc();
}
