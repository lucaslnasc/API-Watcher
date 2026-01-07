# 📊 API Watcher

Sistema de monitoramento de APIs focado em **confiabilidade, observabilidade e resiliência**.

## 🎯 Objetivo

Projeto educacional para estudo avançado de:

- **Domain-Driven Design (DDD)**
- **Event-Driven Architecture**
- **Observabilidade (Prometheus + Grafana)**
- **Resiliência (Circuit Breaker + Retry)**
- **Arquitetura distribuída**

---

## 🚀 Funcionalidades

### ✅ Fase 1 - Monitoramento Inteligente (Concluída)

- [x] **Cadastro inteligente de APIs** com teste automático e threshold calculado
- [x] **Health check periódico** configurável (padrão: 60 segundos)
- [x] **Detecção de problemas**: status code incorreto e latência excessiva
- [x] **Persistência em PostgreSQL** com Hibernate + JPA
- [x] **Scheduler configurável** via `application.yml`
- [x] **Validações em múltiplas camadas** (DTO + Domain)
- [x] **Arquitetura DDD** com Bounded Contexts
- [x] **REST API** completa para gestão

### ✅ Fase 2 - Event-Driven Architecture (Concluída)

- [x] **Apache Kafka** configurado e funcionando
- [x] **Producer**: Publicação de eventos (API cadastrada, Health check)
- [x] **Consumer**: Consumo e processamento de eventos
- [x] **MongoDB Time-Series**: Histórico completo de eventos
- [x] **Event-Driven Architecture**: Fluxo assíncrono completo
- [x] **Domain Events**: `ApiRegisteredEvent` e `HealthCheckEvent`

### 🔄 Roadmap

- **Fase 3**: Circuit Breaker + Retry Pattern + Observabilidade (Prometheus + Grafana)
- **Fase 4**: API REST para consulta de histórico (MongoDB)
- **Fase 5**: Alertas (Slack, Email, Webhooks)
- **Fase 6**: Testes automatizados + CI/CD

---

## 🛠️ Tecnologias

| Camada               | Tecnologia                      |
| -------------------- | ------------------------------- |
| **Backend**          | Spring Boot 3.2, Java 21        |
| **Banco Relacional** | PostgreSQL 16                   |
| **Banco NoSQL**      | MongoDB 7 (Time-Series)         |
| **Cache**            | Redis 7                         |
| **Mensageria**       | Apache Kafka                    |
| **Resiliência**      | Resilience4j                    |
| **Observabilidade**  | Micrometer, Prometheus, Grafana |
| **Containers**       | Docker, Docker Compose          |
| **Testes**           | JUnit 5, Testcontainers         |

---

## 📦 Estrutura do Projeto

```
api-watcher/
├── src/main/java/com/apiwatcher/
│   ├── config/                 # Configurações
│   │   ├── KafkaProducerConfig.java
│   │   └── KafkaConsumerConfig.java
│   ├── monitoring/             # Bounded Context: Monitoramento
│   │   ├── domain/            # Camada de domínio (entidades, regras)
│   │   │   ├── model/        # MonitoredApi, CheckResult
│   │   │   ├── events/       # Domain Events (Kafka)
│   │   │   └── repository/   # Interfaces de repositório
│   │   ├── application/       # Casos de uso (orquestração)
│   │   │   └── usecase/      # RegisterApi, ExecuteHealthCheck, TestAndRegister
│   │   └── infrastructure/    # Adaptadores (HTTP, Persistência, Messaging)
│   │       ├── http/         # Controllers e DTOs
│   │       ├── persistence/  # JPA Entities e Repositories (PostgreSQL)
│   │       ├── messaging/    # Kafka Producer & Consumer
│   │       └── timeseries/   # MongoDB Documents & Repositories
│   ├── scheduler/             # Agendamento de tarefas
│   └── shared/                # Código compartilhado
│       ├── events/           # Event-driven interfaces
│       └── exceptions/       # Tratamento global de erros
├── docker/                    # Infraestrutura
│   ├── docker-compose.yml
│   ├── postgres/
│   ├── kafka/
│   ├── mongodb/
│   ├── prometheus/
│   └── grafana/
└── docs/                      # Documentação
```

---

## 🏗️ Arquitetura Implementada

### Clean Architecture + DDD

- ✅ **Clean Architecture** com separação clara de camadas
- ✅ **Domain-Driven Design (DDD)** com Bounded Contexts
- ✅ **Hexagonal Architecture** (Ports & Adapters)
- ✅ **Repository Pattern** com abstração de persistência
- ✅ **CQRS Pattern** (Command/Query separation)

### Event-Driven Architecture

- ✅ **Apache Kafka** como message broker
- ✅ **Domain Events** publicados assincronamente
- ✅ **Event Sourcing** parcial (histórico no MongoDB)
- ✅ **Producer/Consumer Pattern**
- ✅ **Event-driven communication** entre componentes

### Padrões e Práticas

- ✅ **Use Cases** para orquestração de lógica de negócio
- ✅ **Value Objects** imutáveis (CheckResult)
- ✅ **Domain Events** (ApiRegisteredEvent, HealthCheckEvent)
- ✅ **DTO Pattern** para isolamento de camadas
- ✅ **Validação em múltiplas camadas** (DTO + Domain)
- ✅ **Async Processing** com Kafka

### Stack Tecnológica

- ✅ **Spring Boot 3.2** com Java 21
- ✅ **PostgreSQL 16** (dados relacionais)
- ✅ **MongoDB 7** (time-series / histórico)
- ✅ **Apache Kafka** (mensageria)
- ✅ **JPA/Hibernate** para persistência
- ✅ **Docker Compose** para infraestrutura
- ✅ **Scheduler** configurável
- ✅ **Resilience4j** (preparado para Circuit Breaker)

### Fluxo de Dados

```
┌─────────────────┐
│   REST API      │
│  (Controller)   │
└────────┬────────┘
         │
         ▼
┌─────────────────┐      ┌──────────────┐
│   Use Cases     │─────▶│ PostgreSQL   │
│  (Application)  │      │ (API Config) │
└────────┬────────┘      └──────────────┘
         │
         ▼
┌─────────────────┐
│ Event Publisher │
│     (Kafka)     │
└────────┬────────┘
         │
         ▼
┌─────────────────┐      ┌──────────────┐
│ Event Consumer  │─────▶│   MongoDB    │
│  (Kafka Listen) │      │  (History)   │
└─────────────────┘      └──────────────┘
```

### Próximas Implementações

- 🔄 Circuit Breaker + Retry nos health checks
- 🔄 API REST para consulta de histórico
- 🔄 Métricas com Prometheus + Grafana
- 🔄 Distributed Tracing
- 🔄 Alertas em tempo real

---

## ▶️ Como Rodar

### 1. Pré-requisitos

- **Docker** e **Docker Compose**
- **Java 21**
- **Maven 3.9+**

### 2. Subir a infraestrutura

```bash
cd docker
docker-compose up -d
```

Serviços disponíveis:

- **PostgreSQL**: `localhost:5433` (usuário: `api`, senha: `api`, db: `apiwatcher`)
- MongoDB: `localhost:27017`
- Redis: `localhost:6379`
- Kafka: `localhost:9092`
- Prometheus: `http://localhost:9090`
- Grafana: `http://localhost:3000` (admin/admin)

### 3. Rodar a aplicação

```bash
mvn spring-boot:run
```

### 4. Acessar

- **API**: http://localhost:8080
- **Actuator**: http://localhost:8080/actuator
- **Metrics**: http://localhost:8080/actuator/prometheus

---

## 🎯 Como Usar

### Cadastrar API para monitoramento

#### Opção 1: Teste automático + cadastro (Recomendado ⭐)

O sistema **testa a URL** e calcula o threshold ideal automaticamente:

```bash
POST http://localhost:8080/api/monitoring/test-and-register
Content-Type: application/json

{
  "name": "GitHub API",
  "url": "https://api.github.com",
  "httpMethod": "GET"
}
```

**Resposta:**

```json
{
  "test": {
    "success": true,
    "statusCode": 200,
    "latencyMs": 341,
    "suggestedThreshold": 511,
    "recommendation": "API respondeu em 341ms. Threshold sugerido: 511ms (50% de margem)"
  },
  "api": {
    "id": "64c01422-f45e-4f55-ae4c-df1add79454f",
    "name": "GitHub API",
    "latencyThresholdMs": 511,
    "active": true
  }
}
```

#### Opção 2: Apenas testar (sem cadastrar)

```bash
POST http://localhost:8080/api/monitoring/test
Content-Type: application/json

{
  "url": "https://api.github.com",
  "httpMethod": "GET"
}
```

### Listar APIs monitoradas

```bash
GET http://localhost:8080/api/monitoring/apis
GET http://localhost:8080/api/monitoring/apis?active=true
```

### Forçar health check manual

```bash
POST http://localhost:8080/api/monitoring/health-check
```

### Remover API

```bash
DELETE http://localhost:8080/api/monitoring/apis/{id}
```

### Consultar histórico no MongoDB

```bash
# Conectar ao MongoDB
docker exec -it apiwatcher-mongo mongosh -u admin -p admin

# Usar o banco de dados
use apiwatcher

# Ver APIs registradas
db.api_registrations.find().pretty()

# Ver últimos 5 health checks
db.health_checks.find().sort({checkedAt: -1}).limit(5).pretty()

# Ver health checks de uma API específica
db.health_checks.find({apiName: "GitHub API"}).pretty()

# Ver apenas falhas
db.health_checks.find({success: false}).pretty()

# Contar total de checks
db.health_checks.countDocuments()
```

---

## 🏗️ Arquitetura Implementada

### Arquitetura

- ✅ **Clean Architecture** com separação clara de camadas
- ✅ **Domain-Driven Design (DDD)** com Bounded Contexts
- ✅ **Hexagonal Architecture** (Ports & Adapters)
- ✅ **Repository Pattern** com abstração de persistência

### Padrões e Práticas

- ✅ **Use Cases** para orquestração de lógica de negócio
- ✅ **Value Objects** imutáveis (CheckResult)
- ✅ **Domain Events** (preparado para Event-Driven)
- ✅ **DTO Pattern** para isolamento de camadas
- ✅ **Validação em múltiplas camadas** (DTO + Domain)

### Tecnologias

- ✅ **Spring Boot 3.2** com Java 21
- ✅ **JPA/Hibernate** para persistência
- ✅ **PostgreSQL** para dados relacionais
- ✅ **Docker Compose** para infraestrutura
- ✅ **Scheduler** configurável

### Próximas Implementações (Fase 2)

- 🔄 Event-Driven Architecture com Kafka
- 🔄 Time-Series Database (MongoDB)
- 🔄 Circuit Breaker Pattern
- 🔄 Distributed Tracing
- 🔄

````

API configurada: latencyThresholdMs = 511ms

Resultado:
├─ 340ms → ✅ OK
├─ 650ms → ⚠️ ALERTA: Latência acima do threshold
└─ 1200ms → ⚠️ ALERTA: API muito lenta

```

### 2. Detecção de Problemas de Disponibilidade

```

API configurada: expectedStatusCode = 200

Resultado:
├─ 200 → ✅ OK
├─ 404 → ❌ FALHA: Status incorreto
├─ 503 → ❌ FALHA: Serviço indisponível
└─ Timeout → ❌ ERRO: Não foi possível conectar

````

### 3. Monitoramento Automático

O scheduler executa health check **a cada 60 segundos** (configurável):

```yaml
# src/main/resources/application.yml
scheduler:
  health-check:
    fixed-rate: 60000 # 60 segundos
    initial-delay: 5000 # Aguarda 5s antes de começar
```

---

## 📚 Documentação

- [Arquitetura C4](docs/architecture/)
- [Guia Redis](docs/guides/redis-guide.md)
- [Guia Kafka](docs/guides/kafka-guide.md)
- [Guia MongoDB](docs/guides/mongodb-guide.md)
- [ADRs (Decisões Arquiteturais)](docs/adr/)

---

## 🧪 Testes

```bash
# Todos os testes
mvn test

# Apenas testes unitários
mvn test -Dtest=**/*Test

# Apenas testes de integração
mvn test -Dtest=**/*IT
```

---

## 📈 Observabilidade

### Prometheus

Acesse `http://localhost:9090` e consulte métricas:

```promql
http_server_requests_seconds_count{uri="/api/monitoring/health"}
```

### Grafana

1. Acesse `http://localhost:3000`
2. Login: `admin/admin`
3. Dashboards pré-configurados em `/grafana/dashboards`

---

## 🤝 Contribuindo

Este é um projeto educacional. Sinta-se livre para:

- Abrir issues com dúvidas
- Sugerir melhorias
- Fazer fork e experimentar

---

## 📝 Licença

MIT License - sinta-se livre para usar e aprender!

---

## 👤 Autor

**lucaslnasc** - [GitHub](https://github.com/lucaslnasc)

---

## 🎓 Aprendizados

Este projeto cobre:

- ✅ Clean Architecture / DDD
- ✅ Event-Driven Architecture
- ✅ Circuit Breaker Pattern
- ✅ Time-Series Databases
- ✅ Distributed Tracing
- ✅ Infrastructure as Code
