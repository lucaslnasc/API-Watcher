# 🍃 MongoDB no API Watcher

## O que é MongoDB? 

**MongoDB** é um banco de dados **NoSQL** (não relacional) orientado a **documentos JSON**.

---

## Por que usar MongoDB no projeto?

### PostgreSQL vs MongoDB

| PostgreSQL (Relacional) | MongoDB (NoSQL) |
|------------------------|-----------------|
| Tabelas com linhas e colunas | Documentos JSON |
| JOINs complexos | Dados aninhados |
| Esquema rígido | Esquema flexível |
| **Uso**:  dados transacionais | **Uso**: métricas, logs |

---

## Use Case: Time-Series Data

**Problema**: Precisamos armazenar milhões de métricas de APIs (latency, status code, timestamp).

**Solução**: MongoDB com **Time-Series Collections** (otimizado para séries temporais).

### Estrutura do Documento

```json
{
  "_id": "507f1f77bcf86cd799439011",
  "apiId": "api-123",
  "timestamp": "2026-01-05T10:30:00Z",
  "statusCode": 200,
  "latency": 145,
  "success": true
}
```

---

## Como funciona no Spring Boot?

### 1️⃣ **Model** (`ApiMetric.java`)

```java
@Document(collection = "api_metrics")
@Data
public class ApiMetric {
    
    @Id
    private String id;
    
    @Indexed
    private String apiId;
    
    private int statusCode;
    private long latency;
    private boolean success;
    
    @Indexed
    private LocalDateTime timestamp;
}
```

### 2️⃣ **Repository** (`MetricMongoRepository.java`)

```java
public interface MetricMongoRepository extends MongoRepository<ApiMetric, String> {
    
    List<ApiMetric> findByApiIdAndTimestampBetween(
        String apiId, 
        LocalDateTime start, 
        LocalDateTime end
    );
    
    @Query("{ 'apiId': ?0, 'success': false }")
    List<ApiMetric> findFailuresByApiId(String apiId);
}
```

### 3️⃣ **Service** (Salvar Métrica)

```java
@Service
public class MetricService {
    
    @Autowired
    private MetricMongoRepository repository;
    
    public void saveMetric(ApiCheckResult result) {
        ApiMetric metric = new ApiMetric();
        metric.setApiId(result.getApiId());
        metric.setStatusCode(result. getStatusCode());
        metric.setLatency(result.getLatency());
        metric.setSuccess(result.isSuccess());
        metric.setTimestamp(LocalDateTime. now());
        
        repository. save(metric);
    }
    
    public List<ApiMetric> getLast24Hours(String apiId) {
        LocalDateTime end = LocalDateTime.now();
        LocalDateTime start = end. minusHours(24);
        return repository.findByApiIdAndTimestampBetween(apiId, start, end);
    }
}
```

---

## Queries Úteis

### Via Spring Data

```java
// Buscar métricas da última hora
repository.findByApiIdAndTimestampAfter(apiId, LocalDateTime. now().minusHours(1));

// Contar falhas
repository.countByApiIdAndSuccessFalse(apiId);

// Média de latência (usando @Aggregation)
@Aggregation(pipeline = {
    "{ '$match': { 'apiId':  ?0 } }",
    "{ '$group':  { '_id': null, 'avgLatency': { '$avg': '$latency' } } }"
})
Double getAverageLatency(String apiId);
```

### Via Mongo Shell

```javascript
// Entrar no container
docker exec -it apiwatcher-mongo mongosh -u admin -p admin

// Selecionar database
use apiwatcher

// Ver todas as métricas de uma API
db.api_metrics.find({ apiId: "api-123" })

// Contar documentos
db.api_metrics. countDocuments()

// Agregação:  média de latência por API
db.api_metrics. aggregate([
  { $group: { _id: "$apiId", avgLatency: { $avg: "$latency" } } }
])
```

---

## Time-Series Collections (Avançado)

MongoDB 5.0+ tem suporte nativo a séries temporais.

### Criar Collection Otimizada

```java
@Configuration
public class MongoConfig {
    
    @Bean
    public MongoTemplate mongoTemplate(MongoDatabaseFactory factory) {
        MongoTemplate template = new MongoTemplate(factory);
        
        // Criar time-series collection
        TimeSeriesOptions options = new TimeSeriesOptions("timestamp")
            .metaField("apiId")
            .granularity(TimeSeriesGranularity.SECONDS);
        
        CreateCollectionOptions collectionOptions = new CreateCollectionOptions()
            .timeSeriesOptions(options);
        
        template.createCollection("api_metrics", collectionOptions);
        
        return template;
    }
}
```

---

## Quando usar MongoDB vs PostgreSQL?

### ✅ Use MongoDB para: 
- Métricas (muitas escritas, poucas leituras complexas)
- Logs de aplicação
- Dados não estruturados
- Prototipação rápida

### ✅ Use PostgreSQL para: 
- Dados transacionais (CRUD de APIs)
- Relacionamentos complexos
- ACID garantido
- Queries com JOINs

---

## Resumo

| Conceito | Explicação |
|----------|-----------|
| **O que é?** | Banco NoSQL orientado a documentos |
| **Formato** | JSON (BSON internamente) |
| **Uso no projeto** | Armazenar métricas históricas |
| **Vantagem** | Rápido para grandes volumes de escrita |
| **Collection** | Equivalente a "tabela" no SQL |
| **Document** | Equivalente a "linha" no SQL |

---

## Próximos Passos

1. Criar model `ApiMetric`
2. Implementar repository
3. Salvar métricas após cada health check
4. Criar endpoint para consultar histórico