package com.apiwatcher.monitoring.infrastructure.http;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.apiwatcher.monitoring.application.usecase.ExecuteHealthCheckUseCase;
import com.apiwatcher.monitoring.application.usecase.TestAndRegisterApiUseCase;
import com.apiwatcher.monitoring.domain.model.MonitoredApi;
import com.apiwatcher.monitoring.domain.repository.MonitoredApiRepository;
import com.apiwatcher.monitoring.infrastructure.http.dto.ApiResponse;
import com.apiwatcher.monitoring.infrastructure.http.dto.TestApiRequest;
import com.apiwatcher.monitoring.infrastructure.http.dto.TestApiResponse;

import jakarta.validation.Valid;

/**
 * REST Controller para gerenciamento de APIs monitoradas.
 */
@RestController
@RequestMapping("/api/monitoring")
public class MonitoringController {

  private final ExecuteHealthCheckUseCase executeHealthCheckUseCase;
  private final TestAndRegisterApiUseCase testAndRegisterApiUseCase;
  private final MonitoredApiRepository repository;

  public MonitoringController(
      ExecuteHealthCheckUseCase executeHealthCheckUseCase,
      TestAndRegisterApiUseCase testAndRegisterApiUseCase,
      MonitoredApiRepository repository) {
    this.executeHealthCheckUseCase = executeHealthCheckUseCase;
    this.testAndRegisterApiUseCase = testAndRegisterApiUseCase;
    this.repository = repository;
  }

  /**
   * GET /api/monitoring/apis - Listar todas as APIs
   */
  @GetMapping("/apis")
  public ResponseEntity<List<ApiResponse>> listApis(@RequestParam(required = false) Boolean active) {
    List<MonitoredApi> apis;

    if (active != null && active) {
      apis = repository.findAllActive();
    } else {
      apis = repository.findAll();
    }

    List<ApiResponse> response = apis.stream()
        .map(ApiResponse::from)
        .collect(Collectors.toList());

    return ResponseEntity.ok(response);
  }

  /**
   * GET /api/monitoring/apis/{id} - Buscar API por ID
   */
  @GetMapping("/apis/{id}")
  public ResponseEntity<ApiResponse> getApi(@PathVariable String id) {
    return repository.findById(id)
        .map(ApiResponse::from)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  /**
   * DELETE /api/monitoring/apis/{id} - Remover API
   */
  @DeleteMapping("/apis/{id}")
  public ResponseEntity<Void> deleteApi(@PathVariable String id) {
    if (repository.findById(id).isEmpty()) {
      return ResponseEntity.notFound().build();
    }

    repository.deleteById(id);
    return ResponseEntity.noContent().build();
  }

  /**
   * POST /api/monitoring/health-check - Executar health check manual
   */
  @PostMapping("/health-check")
  public ResponseEntity<String> executeHealthCheck() {
    var results = executeHealthCheckUseCase.execute();
    return ResponseEntity.ok(String.format("Health check executado: %d APIs verificadas", results.size()));
  }

  /**
   * POST /api/monitoring/test - Testar API (sem cadastrar)
   * Retorna latência medida e threshold sugerido
   */
  @PostMapping("/test")
  public ResponseEntity<TestApiResponse> testApi(@Valid @RequestBody TestApiRequest request) {
    TestApiResponse result = testAndRegisterApiUseCase.testApi(request.url(), request.httpMethod());
    return ResponseEntity.ok(result);
  }

  /**
   * POST /api/monitoring/test-and-register - Testar E cadastrar automaticamente
   * Descobre o threshold ideal baseado na resposta real
   */
  @PostMapping("/test-and-register")
  public ResponseEntity<Object> testAndRegisterApi(@Valid @RequestBody TestApiRequest request) {
    var result = testAndRegisterApiUseCase.testAndRegister(
        request.name(),
        request.url(),
        request.httpMethod());

    return ResponseEntity.status(HttpStatus.CREATED).body(new Object() {
      public final TestApiResponse test = result.testResult();
      public final ApiResponse api = ApiResponse.from(result.registeredApi());
    });
  }
}
