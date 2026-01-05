package com.apiwatcher.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.apiwatcher.monitoring.application.usecase.ExecuteHealthCheckUseCase;

@Component
public class HealthCheckScheduler {

  private static final Logger logger = LoggerFactory.getLogger(HealthCheckScheduler.class);

  private final ExecuteHealthCheckUseCase executeHealthCheckUseCase;

  public HealthCheckScheduler(ExecuteHealthCheckUseCase executeHealthCheckUseCase) {
    this.executeHealthCheckUseCase = executeHealthCheckUseCase;
  }

  /**
   * Executa health check periódico nas APIs monitoradas.
   * Intervalo configurável via application.yml
   * (scheduler.health-check.fixed-rate)
   */
  @Scheduled(fixedRateString = "${scheduler.health-check.fixed-rate}", initialDelayString = "${scheduler.health-check.initial-delay}")
  public void executeHealthCheck() {
    logger.info("🔍 Iniciando health check periódico das APIs...");

    try {
      executeHealthCheckUseCase.execute();
      logger.info("✅ Health check periódico concluído com sucesso");
    } catch (Exception e) {
      logger.error("❌ Erro ao executar health check periódico: {}", e.getMessage(), e);
    }
  }
}
