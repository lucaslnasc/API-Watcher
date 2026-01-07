# 🔴 Guia Redis Cache - API Watcher

## Objetivo

Redis é usado como **camada de cache** para reduzir carga no PostgreSQL e melhorar performance das consultas frequentes.

---

## Estratégia de Cache

### 1. **Caches Implementados**

| Cache Name            | TTL        | Descrição                         | Chave      |
| --------------------- | ---------- | --------------------------------- | ---------- |
| `monitored-apis`      | 5 minutos  | APIs ativas (consulta frequente)  | `'active'` |
| `monitored-api-by-id` | 15 minutos | API individual por ID             | ID da API  |
| `all-apis`            | 10 minutos | Todas as APIs (ativas + inativas) | `'all'`    |

### 2. **Quando o Cache é Usado**

✅ **Cache HIT** (dados vêm do Redis):

- Listagem de APIs ativas (scheduler consulta a cada 60s)
- Consulta de API por ID
- Listagem completa de APIs

❌ **Cache MISS** (busca no PostgreSQL):

- Primeira consulta após inicialização
- Após TTL expirar
- Após invalidação por save/delete

### 3. **Invalidação de Cache**

O cache é **invalidado automaticamente** quando:

- Uma API é **criada** (`save()`)
- Uma API é **atualizada** (`save()`)
- Uma API é **deletada** (`deleteById()`)

**Estratégia:** Invalidação total dos 3 caches para garantir consistência.

---

## Arquitetura

```
┌─────────────────────────────────────────────────────────┐
│                    REST Controller                      │
└───────────────────────┬─────────────────────────────────┘
                        │
                        ▼
┌─────────────────────────────────────────────────────────┐
│                    Use Cases                            │
└───────────────────────┬─────────────────────────────────┘
                        │
                        ▼
┌─────────────────────────────────────────────────────────┐
│         MonitoredApiRepository (com @Cacheable)         │
└─────────────┬───────────────────────────┬───────────────┘
              │                           │
       Cache HIT?                   Cache MISS?
              │                           │
              ▼                           ▼
    ┌─────────────────┐        ┌──────────────────┐
    │      Redis      │        │    PostgreSQL    │
    │   (5-15 min)    │        │  (fonte verdade) │
    └─────────────────┘        └──────────────────┘
```

---

## Benefícios

### 1. **Performance**

- **Reduz latência** em consultas frequentes (APIs ativas consultadas a cada 60s)
- **Diminui carga no PostgreSQL** (menos queries)

### 2. **Escalabilidade**

- Redis suporta **milhares de requisições por segundo**
- Facilita escalonamento horizontal

### 3. **Resiliência**

- Se Redis cair, aplicação continua funcionando (fallback para PostgreSQL)
- Cache é reconstruído automaticamente

---

## Configuração

### application.yml

```yaml
spring:
  data:
    redis:
      host: localhost
      port: 6379
      timeout: 3000ms
      lettuce:
        pool:
          max-active: 8
          max-idle: 8
          min-idle: 2

  cache:
    type: redis
    redis:
      time-to-live: 600000 # 10 minutos
      cache-null-values: false
      use-key-prefix: true
      key-prefix: "api-watcher:"
```

### RedisConfig.java

TTLs específicos por cache:

```java
var cacheConfigurations = Map.of(
    "monitored-apis", RedisCacheConfiguration.defaultCacheConfig()
        .entryTtl(Duration.ofMinutes(5)),
    "monitored-api-by-id", RedisCacheConfiguration.defaultCacheConfig()
        .entryTtl(Duration.ofMinutes(15)),
    "all-apis", RedisCacheConfiguration.defaultCacheConfig()
        .entryTtl(Duration.ofMinutes(10))
);
```

---

## Testes

### 1. **Verificar Cache Hit/Miss**

Ative logs de debug:

```yaml
logging:
  level:
    com.apiwatcher.monitoring.infrastructure.persistence: DEBUG
    org.springframework.cache: DEBUG
```

**Logs esperados:**

```
# Cache MISS (primeira consulta)
DEBUG MonitoredApiRepositoryImpl - Buscando APIs ativas (cache miss)
DEBUG RedisCacheManager - Cache miss for key 'active'

# Cache HIT (consulta subsequente)
DEBUG RedisCacheManager - Cache hit for key 'active'
```

### 2. **Inspecionar Redis CLI**

```bash
# Conectar ao Redis
docker exec -it apiwatcher-redis redis-cli

# Ver todas as chaves
KEYS api-watcher:*

# Ver conteúdo de um cache
GET "api-watcher:monitored-apis::active"

# Ver TTL restante
TTL "api-watcher:monitored-apis::active"

# Limpar todos os caches (útil para testes)
FLUSHDB
```

### 3. **Teste de Performance**

**Sem cache (primeira chamada):**

```bash
time curl http://localhost:8080/api/monitoring/apis?active=true
# ~50-100ms (consulta PostgreSQL)
```

**Com cache (chamadas subsequentes):**

```bash
time curl http://localhost:8080/api/monitoring/apis?active=true
# ~5-10ms (consulta Redis) ⚡
```

---

## Monitoramento

### 1. **Métricas do Redis**

Acessar via Redis CLI:

```bash
docker exec -it apiwatcher-redis redis-cli INFO stats

# Métricas importantes:
# - keyspace_hits: Quantas vezes o cache foi usado
# - keyspace_misses: Quantas vezes houve cache miss
# - instantaneous_ops_per_sec: Operações por segundo
```

### 2. **Cache Hit Rate**

**Fórmula:**

```
Hit Rate = keyspace_hits / (keyspace_hits + keyspace_misses) * 100
```

**Meta:** > 80% de hit rate após warm-up

### 3. **Memory Usage**

```bash
docker exec -it apiwatcher-redis redis-cli INFO memory

# Verificar:
# - used_memory_human: Memória usada
# - maxmemory_policy: Política de evicção
```

---

## Troubleshooting

### Problema: Cache não está sendo usado

**Verificar:**

1. Redis está rodando?

```bash
docker ps | grep redis
```

2. Aplicação está conectada?

```bash
docker logs apiwatcher-redis
```

3. Logs mostram cache hit/miss?

```yaml
logging:
  level:
    org.springframework.cache: DEBUG
```

### Problema: Dados desatualizados no cache

**Causa:** Invalidação não ocorreu após save/delete

**Solução:**

```java
@CacheEvict(value = { "monitored-apis", "monitored-api-by-id", "all-apis" },
            allEntries = true)
public MonitoredApi save(MonitoredApi api) { ... }
```

### Problema: Redis fora do ar

**Comportamento:** Aplicação continua funcionando, mas mais lenta (fallback para PostgreSQL)

**Verificar:**

```bash
docker-compose ps redis
docker-compose restart redis
```

---

## Boas Práticas

### ✅ Fazer

- **TTL curto para dados frequentes**: 5 minutos para APIs ativas
- **TTL longo para dados estáticos**: 15 minutos para API individual
- **Invalidar cache após mutações**: Sempre usar `@CacheEvict`
- **Monitorar hit rate**: Verificar efetividade do cache
- **Logs de debug em DEV**: Para entender comportamento

### ❌ Evitar

- **Cache de dados sensíveis**: Senhas, tokens, etc.
- **TTL muito longo**: Pode gerar inconsistências
- **Cache sem invalidação**: Dados desatualizados
- **Chaves dinâmicas sem controle**: Explosão de memória

---

## Evolução Futura

### Fase 3 (Atual)

- ✅ Cache implementado
- 🔄 Circuit Breaker + Retry
- 🔄 Observabilidade (Prometheus + Grafana)

### Fase 4

- 🔄 Cache de resultados de health checks (última verificação)
- 🔄 Cache distribuído (Redis Cluster)
- 🔄 Rate limiting com Redis

---

## Referências

- [Spring Cache Abstraction](https://docs.spring.io/spring-framework/reference/integration/cache.html)
- [Redis Cache Configuration](https://docs.spring.io/spring-data/redis/reference/redis/redis-cache.html)
- [Redis Best Practices](https://redis.io/docs/management/optimization/)

---

**✅ Redis Cache implementado e funcionando!**
