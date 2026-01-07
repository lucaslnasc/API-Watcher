package com.apiwatcher.monitoring.infrastructure.timeseries.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.apiwatcher.monitoring.infrastructure.timeseries.document.HealthCheckHistory;

@Repository
public interface HealthCheckHistoryRepository extends MongoRepository<HealthCheckHistory, String> {
  /**
   * Busca histórico de uma API específica
   */
  List<HealthCheckHistory> findByApiIdOrderByCheckedAtDesc(String apiId);

  /**
   * Busca histórico de uma API em um período
   */
  List<HealthCheckHistory> findByApiIdAndCheckedAtBetweenOrderByCheckedAtDesc(
      String apiId,
      LocalDateTime start,
      LocalDateTime end);

  /**
   * Busca apenas checks que falharam
   */
  List<HealthCheckHistory> findByApiIdAndSuccessFalseOrderByCheckedAtDesc(String apiId);

  /**
   * Busca checks que excederam o threshold
   */
  List<HealthCheckHistory> findByApiIdAndExceededThresholdTrueOrderByCheckedAtDesc(String apiId);

  /**
   * Busca últimos N checks de uma API
   */
  List<HealthCheckHistory> findTop50ByApiIdOrderByCheckedAtDesc(String apiId);

  /**
   * Conta quantos checks falharam no período
   */
  @Query(value = "{'apiId': ?0, 'success': false, 'checkedAt': {$gte: ?1, $lte: ?2}}", count = true)
  long countFailuresByApiIdInPeriod(String apiId, LocalDateTime start, LocalDateTime end);

  /**
   * Calcula latência média no período
   */
  @Query(value = "{'apiId': ?0, 'checkedAt': {$gte: ?1, $lte: ?2}}")
  List<HealthCheckHistory> findByApiIdInPeriod(String apiId, LocalDateTime start, LocalDateTime end);
}
