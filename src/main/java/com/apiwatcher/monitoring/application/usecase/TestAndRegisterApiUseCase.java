package com.apiwatcher.monitoring.application.usecase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.apiwatcher.monitoring.domain.model.MonitoredApi;
import com.apiwatcher.monitoring.domain.repository.MonitoredApiRepository;
import com.apiwatcher.monitoring.infrastructure.http.dto.TestApiResponse;
import com.apiwatcher.shared.exceptions.DomainException;

/**
 * Caso de uso: Testar API e cadastrar automaticamente com threshold
 * inteligente.
 */
@Service
public class TestAndRegisterApiUseCase {

  private static final Logger logger = LoggerFactory.getLogger(TestAndRegisterApiUseCase.class);

  private final MonitoredApiRepository repository;
  private final RestTemplate restTemplate;

  public TestAndRegisterApiUseCase(MonitoredApiRepository repository) {
    this.repository = repository;
    this.restTemplate = new RestTemplate();
  }

  /**
   * Testa a API primeiro e retorna resultado + sugestões
   */
  public TestApiResponse testApi(String url, String httpMethod) {
    logger.info("🔍 Testando API: {} [{}]", url, httpMethod);

    try {
      long startTime = System.currentTimeMillis();

      // Faz requisição de teste
      var response = restTemplate.getForEntity(url, String.class);

      long latencyMs = System.currentTimeMillis() - startTime;
      int statusCode = response.getStatusCode().value();

      logger.info("✅ Teste bem-sucedido: {}ms - Status {}", latencyMs, statusCode);

      return TestApiResponse.success(statusCode, latencyMs);

    } catch (Exception e) {
      logger.error("❌ Erro ao testar API: {}", e.getMessage());
      return TestApiResponse.error(e.getMessage());
    }
  }

  /**
   * Testa E cadastra automaticamente com threshold inteligente
   */
  @Transactional
  public ApiTestAndRegistrationResult testAndRegister(String name, String url, String httpMethod) {
    logger.info("🚀 Testando e cadastrando API: {} - {}", name, url);

    // Verifica se já existe
    if (repository.existsByUrl(url)) {
      throw new DomainException("Já existe uma API cadastrada com esta URL: " + url);
    }

    // 1. Testa primeiro
    TestApiResponse testResult = testApi(url, httpMethod);

    if (!testResult.success()) {
      logger.warn("⚠️ API falhou no teste, mas será cadastrada com valores padrão");
    }

    // 2. Cadastra com threshold automático
    MonitoredApi api = new MonitoredApi(
        name,
        url,
        httpMethod,
        testResult.suggestedExpectedStatusCode(),
        testResult.suggestedThreshold());

    MonitoredApi saved = repository.save(api);

    logger.info("✅ API cadastrada com threshold automático de {}ms", testResult.suggestedThreshold());

    return new ApiTestAndRegistrationResult(testResult, saved);
  }

  /**
   * Resultado combinado do teste + cadastro
   */
  public record ApiTestAndRegistrationResult(
      TestApiResponse testResult,
      MonitoredApi registeredApi) {
  }
}
