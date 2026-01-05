package com.apiwatcher.monitoring.application.usecase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.apiwatcher.monitoring.domain.model.MonitoredApi;
import com.apiwatcher.monitoring.domain.repository.MonitoredApiRepository;
import com.apiwatcher.shared.exceptions.DomainException;

/**
 * Caso de uso: Registrar nova API para monitoramento.
 */
@Service
public class RegisterApiUseCase {

  private static final Logger logger = LoggerFactory.getLogger(RegisterApiUseCase.class);

  private final MonitoredApiRepository repository;

  public RegisterApiUseCase(MonitoredApiRepository repository) {
    this.repository = repository;
  }

  @Transactional
  public MonitoredApi execute(String name, String url, String httpMethod, Integer expectedStatusCode,
      Integer latencyThresholdMs) {
    logger.info("Registrando nova API: {} - {}", name, url);

    // Verifica se URL já existe
    if (repository.existsByUrl(url)) {
      throw new DomainException("Já existe uma API cadastrada com esta URL: " + url);
    }

    // Cria e valida a entidade de domínio
    MonitoredApi api = new MonitoredApi(name, url, httpMethod, expectedStatusCode, latencyThresholdMs);

    // Persiste
    MonitoredApi saved = repository.save(api);

    logger.info("API registrada com sucesso: ID={}", saved.getId());
    return saved;
  }
}
