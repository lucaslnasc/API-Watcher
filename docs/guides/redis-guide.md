# 🔴 Redis no API Watcher

## O que é Redis? 

**Redis** = **RE**mote **DI**ctionary **S**erver

É um banco de dados **em memória** (super rápido) que funciona como um **cache**. 

---

## Por que usar Redis no projeto?

### 1️⃣ **Cache de Resultados de Health Check**
Evitar consultas repetidas ao banco PostgreSQL.

**Exemplo**:
```java
// Sem Redis:  consulta o banco toda vez
MonitoredApi api = repository.findById(apiId);

// Com Redis: consulta 1x, depois pega do cache
MonitoredApi api = redisCache.get(apiId);
if (api == null) {
    api = repository.findById(apiId);
    redisCache.set(apiId, api, 5_MINUTES);
}
```

### 2️⃣ **Armazenar Última Execução**
Guardar o timestamp do último health check de cada API.

```java
redisTemplate.opsForValue().set("api:123:last-check", LocalDateTime.now());
```

### 3️⃣ **Rate Limiting** (Fase avançada)
Limitar quantas vezes uma API pode ser chamada por minuto.

---

## Como funciona no Spring Boot?

### Configuração (`RedisConfig. java`)

```java
@Configuration
public class RedisConfig {
    
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        return template;
    }
}
```

### Uso Prático

```java
@Service
public class CacheService {
    
    @Autowired
    private RedisTemplate<String, Object> redis;
    
    public void cacheApiResult(String apiId, CheckResult result) {
        redis.opsForValue().set("check:" + apiId, result, 5, TimeUnit.MINUTES);
    }
    
    public CheckResult getCachedResult(String apiId) {
        return (CheckResult) redis.opsForValue().get("check:" + apiId);
    }
}
```

---

## Comandos Úteis (Redis CLI)

```bash
# Entrar no container
docker exec -it apiwatcher-redis redis-cli

# Ver todas as chaves
KEYS *

# Ver valor de uma chave
GET check:api-123

# Deletar uma chave
DEL check:api-123

# Ver tempo de expiração (em segundos)
TTL check:api-123
```

---

## Quando NÃO usar Redis?

- ❌ Dados que precisam ser persistidos permanentemente
- ❌ Consultas complexas (JOINs, agregações)
- ✅ Usar apenas para dados **temporários** e **alta performance**

---

## Resumo

| Conceito | Explicação |
|----------|-----------|
| **O que é?** | Banco de dados em memória (cache) |
| **Por que usar?** | Performance (1000x mais rápido que SQL) |
| **Uso no projeto** | Cache de health checks, última execução |
| **Dados** | Key-Value (chave → valor) |
| **Persistência** | Não (dados temporários com TTL) |

---

## Próximos Passos

1. Implementar cache básico de APIs
2. Adicionar TTL (Time To Live) configurável
3. Monitorar hit/miss do cache no Grafana