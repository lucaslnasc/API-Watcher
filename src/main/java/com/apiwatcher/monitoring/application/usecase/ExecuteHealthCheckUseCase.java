package com.apiwatcher.monitoring.application.usecase;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.apiwatcher.monitoring.domain.model.CheckResult;
import com.apiwatcher.monitoring.domain.model.MonitoredApi;
import com.apiwatcher.monitoring.domain.repository.MonitoredApiRepository;

/**
 * Caso de uso: Executar health check nas APIs monitoradas.
 */
@Service
public class ExecuteHealthCheckUseCase {

  private static final Logger logger = LoggerFactory.getLogger(ExecuteHealthCheckUseCase.class);

  private final MonitoredApiRepository repository;
  private final RestTemplate restTemplate;

  public ExecuteHealthCheckUseCase(MonitoredApiRepository repository) {
    this.repository = repository;
    this.restTemplate = new RestTemplate();
  }

  public List<CheckResult> execute() {
    logger.info("🔍 Executando health check de todas as APIs ativas");

    List<MonitoredApi> apis = repository.findAllActive();
    List<CheckResult> results = new ArrayList<>();

    for (MonitoredApi api : apis) {
      CheckResult result = checkApi(api);
      results.add(result);
      logResult(api, result);
    }

    logger.info("✅ Health check concluído: {} APIs verificadas", results.size());
    return results;
  }

  private CheckResult checkApi(MonitoredApi api) {
    try {
      long startTime = System.currentTimeMillis();

      // Faz a requisição HTTP
      var response = restTemplate.getForEntity(api.getUrl(), String.class);

      long latencyMs = System.currentTimeMillis() - startTime;
      int statusCode = response.getStatusCode().value();

      // Verifica se o status code é o esperado
      if (statusCode == api.getExpectedStatusCode()) {
        return CheckResult.success(api.getId(), statusCode, latencyMs);
      } else {
        String errorMsg = String.format("Status esperado: %d, recebido: %d",
            api.getExpectedStatusCode(), statusCode);
        return CheckResult.failure(api.getId(), statusCode, latencyMs, errorMsg);
      }

    } catch (Exception e) {
      logger.error("Erro ao verificar API {}: {}", api.getName(), e.getMessage());
      return CheckResult.error(api.getId(), e.getMessage());
    }
  }

  private void logResult(MonitoredApi api, CheckResult result) {
    if (result.isHealthy()) {
      logger.info("✅ {} - OK ({}ms)", api.getName(), result.getLatencyMs());

      if (result.exceededThreshold(api.getLatencyThresholdMs())) {
        logger.warn("⚠️ {} - Latência acima do threshold: {}ms > {}ms",
            api.getName(), result.getLatencyMs(), api.getLatencyThresholdMs());
      }
    } else {
      logger.error("❌ {} - FALHOU: {}", api.getName(), result.getErrorMessage());
    }
  }
}
