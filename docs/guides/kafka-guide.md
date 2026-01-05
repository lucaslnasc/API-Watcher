# 🟠 Kafka no API Watcher

## O que é Kafka? 

**Apache Kafka** é um sistema de **mensageria distribuída**. 

Pense nele como um **correio** onde:
- **Producer** = quem envia cartas
- **Consumer** = quem recebe cartas
- **Topic** = caixa de correio específica

---

## Por que usar Kafka no projeto?

### Event-Driven Architecture

Quando um health check falha, o sistema precisa:
1. Salvar a métrica no MongoDB
2. Avaliar se deve disparar um alerta
3. Enviar notificação

**Sem Kafka** (acoplado):
```java
checkApi();
saveMetric();
evaluateAlert();
sendNotification();
```

**Com Kafka** (desacoplado):
```java
// Monitoring Context
checkApi();
kafka.publish("api-check-failed", event);

// Metrics Context (escuta o evento)
@KafkaListener(topics = "api-check-failed")
void onApiCheckFailed(ApiCheckFailed event) {
    saveMetric(event);
}

// Alerts Context (escuta o evento)
@KafkaListener(topics = "api-check-failed")
void onApiCheckFailed(ApiCheckFailed event) {
    evaluateAlert(event);
}
```

---

## Conceitos Fundamentais

### 1️⃣ **Topic**
Nome da "fila" onde as mensagens ficam. 

Exemplos no projeto:
- `api-check-succeeded`
- `api-check-failed`
- `latency-threshold-exceeded`

### 2️⃣ **Producer**
Quem **publica** eventos.

```java
@Service
public class EventPublisher {
    
    @Autowired
    private KafkaTemplate<String, DomainEvent> kafka;
    
    public void publish(DomainEvent event) {
        kafka.send("api-events", event);
    }
}
```

### 3️⃣ **Consumer**
Quem **escuta** eventos.

```java
@Component
public class MetricEventListener {
    
    @KafkaListener(topics = "api-events", groupId = "metrics-group")
    public void handle(ApiCheckFailed event) {
        // Salvar métrica no MongoDB
    }
}
```

### 4️⃣ **Consumer Group**
Múltiplos consumidores processando mensagens em paralelo.

---

## Como funciona no Spring Boot?

### Configuração (`KafkaConfig.java`)

```java
@Configuration
public class KafkaConfig {
    
    @Bean
    public NewTopic apiEventsTopic() {
        return TopicBuilder
            .name("api-events")
            .partitions(3)
            .replicas(1)
            .build();
    }
}
```

### Publicar Evento

```java
@Service
public class HealthCheckService {
    
    @Autowired
    private KafkaTemplate<String, ApiCheckFailed> kafka;
    
    public void executeCheck(MonitoredApi api) {
        try {
            // executa check
        } catch (Exception e) {
            kafka.send("api-events", new ApiCheckFailed(api.getId()));
        }
    }
}
```

### Consumir Evento

```java
@Component
public class AlertListener {
    
    @KafkaListener(topics = "api-events", groupId = "alerts-group")
    public void onApiCheckFailed(ApiCheckFailed event) {
        // Lógica de alertas
    }
}
```

---

## Comandos Úteis

```bash
# Entrar no container do Kafka
docker exec -it apiwatcher-kafka bash

# Listar tópicos
kafka-topics. sh --list --bootstrap-server localhost:9092

# Criar tópico
kafka-topics.sh --create --topic api-events --bootstrap-server localhost:9092

# Consumir mensagens (ver o que está sendo publicado)
kafka-console-consumer.sh --topic api-events --from-beginning --bootstrap-server localhost: 9092
```

---

## Fluxo Completo no Projeto

```
┌─────────────────┐
│ Health Check    │
│ (Monitoring)    │
└────────┬────────┘
         │ publica evento
         ▼
┌─────────────────┐
│   Kafka Topic   │
│  "api-events"   │
└────┬────────┬───┘
     │        │
     │        └──────────┐
     ▼                   ▼
┌─────────────┐   ┌─────────────┐
│  Metrics    │   │   Alerts    │
│  Consumer   │   │  Consumer   │
└─────────────┘   └─────────────┘
     │                   │
     ▼                   ▼
┌─────────────┐   ┌─────────────┐
│  MongoDB    │   │ Notification│
└─────────────┘   └─────────────┘
```

---

## Vantagens

✅ **Desacoplamento**: serviços não se conhecem  
✅ **Escalabilidade**: adicionar novos consumidores facilmente  
✅ **Resiliência**: se um consumidor cair, as mensagens ficam na fila  
✅ **Auditoria**: histórico de todos os eventos  

---

## Resumo

| Conceito | Explicação |
|----------|-----------|
| **O que é? ** | Sistema de mensageria distribuída |
| **Por que usar?** | Desacoplar bounded contexts |
| **Producer** | Quem publica eventos |
| **Consumer** | Quem escuta eventos |
| **Topic** | Nome da "fila" de mensagens |
| **Uso no projeto** | Comunicação entre Monitoring, Metrics e Alerts |

---

## Próximos Passos

1. Criar eventos de domínio
2. Implementar publisher genérico
3. Criar listeners para métricas e alertas