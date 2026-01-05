package com.apiwatcher.monitoring.domain.repository;

import java.util.List;
import java.util.Optional;

import com.apiwatcher.monitoring.domain.model.MonitoredApi;

/**
 * Interface do repositório (porta) definida no domínio.
 * A implementação real estará na camada de infraestrutura.
 */
public interface MonitoredApiRepository {

  MonitoredApi save(MonitoredApi api);

  Optional<MonitoredApi> findById(String id);

  List<MonitoredApi> findAllActive();

  List<MonitoredApi> findAll();

  void deleteById(String id);

  boolean existsByUrl(String url);
}
