package com.apiwatcher.monitoring.application.usecase;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.apiwatcher.monitoring.domain.events.HealthCheckEvent;
import com.apiwatcher.monitoring.domain.model.CheckResult;
import com.apiwatcher.monitoring.domain.model.MonitoredApi;
import com.apiwatcher.monitoring.domain.repository.MonitoredApiRepository;
import com.apiwatcher.shared.events.EventPublisher;

/**
 * Caso de uso: Executar health check nas APIs monitoradas.
 */
@Service
public class ExecuteHealthCheckUseCase {

  private final EventPublisher eventPublisher;

  private static final Logger logger = LoggerFactory.getLogger(ExecuteHealthCheckUseCase.class);

  private final MonitoredApiRepository repository;
  private final RestTemplate restTemplate;

  public ExecuteHealthCheckUseCase(MonitoredApiRepository repository,
      EventPublisher eventPublisher) {
    this.repository = repository;
    this.restTemplate = new RestTemplate();
    this.eventPublisher = eventPublisher;
  }

  public List<CheckResult> execute() {
    logger.info("[HEALTH-CHECK] Executando health check de todas as APIs ativas");

    List<MonitoredApi> apis = repository.findAllActive();
    List<CheckResult> results = new ArrayList<>();

    for (MonitoredApi api : apis) {
      CheckResult result = checkApi(api);
      results.add(result);
      logResult(api, result);
    }

    logger.info("[HEALTH-CHECK] Concluido: {} APIs verificadas", results.size());
    return results;
  }

  private CheckResult checkApi(MonitoredApi api) {
    try {
      long startTime = System.currentTimeMillis();

      // Faz a requisição HTTP
      var response = restTemplate.getForEntity(api.getUrl(), String.class);

      long latencyMs = System.currentTimeMillis() - startTime;
      int statusCode = response.getStatusCode().value();

      CheckResult result;
      // Verifica se o status code é o esperado
      if (statusCode == api.getExpectedStatusCode()) {
        return CheckResult.success(api.getId(), statusCode, latencyMs);
      } else {
        String errorMsg = String.format("Status esperado: %d, recebido: %d",
            api.getExpectedStatusCode(), statusCode);
        result = CheckResult.failure(api.getId(), statusCode, latencyMs, errorMsg);
      }

      // Publica Evento no Kafka
      publishHealthCheckEvent(result, api);

      return result;

    } catch (Exception e) {
      logger.error("Erro ao verificar API {}: {}", api.getName(), e.getMessage());
      CheckResult result = CheckResult.error(api.getId(), e.getMessage());

      // Publica evento mesmo em caso de erro
      publishHealthCheckEvent(result, api);

      return result;
    }
  }

  private void logResult(MonitoredApi api, CheckResult result) {
    if (result.isHealthy()) {
      logger.info("[OK] {} - {}ms", api.getName(), result.getLatencyMs());

      if (result.exceededThreshold(api.getLatencyThresholdMs())) {
        logger.warn("[ALERTA] {} - Latencia acima do threshold: {}ms > {}ms",
            api.getName(), result.getLatencyMs(), api.getLatencyThresholdMs());
      }
    } else {
      logger.error("[FALHA] {} - {}", api.getName(), result.getErrorMessage());
    }
  }

  // Adicione este novo método privado:
  /**
   * Publica evento de health check no Kafka.
   */
  private void publishHealthCheckEvent(CheckResult result, MonitoredApi api) {
    try {
      HealthCheckEvent event = HealthCheckEvent.from(
          result,
          api.getName(),
          api.getUrl(),
          api.getLatencyThresholdMs());

      eventPublisher.publish(event);
    } catch (Exception e) {
      // Não deixa falha na publicação do evento derrubar o health check
      logger.error("[KAFKA-ERROR] Erro ao publicar evento de health check: {}", e.getMessage());
    }
  }
}
