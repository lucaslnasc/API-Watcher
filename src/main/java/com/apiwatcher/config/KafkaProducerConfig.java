package com.apiwatcher.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

/**
 * Configuração do Kafka Producer.
 * 
 * Responsável por criar o KafkaTemplate que será usado
 * para enviar eventos (mensagens) para os tópicos do Kafka.
 * 
 * Como funciona:
 * 1. ProducerFactory cria as configurações de conexão
 * 2. KafkaTemplate usa essa factory para enviar mensagens
 * 3. JsonSerializer converte objetos Java em JSON automaticamente
 */
@Configuration
public class KafkaProducerConfig {

  @Value("${spring.kafka.bootstrap-servers}")
  private String bootstrapServers;

  /**
   * Configurações básicas do producer.
   * 
   * - BOOTSTRAP_SERVERS: Endereço do Kafka (localhost:9092)
   * - KEY_SERIALIZER: Como serializar a chave (String)
   * - VALUE_SERIALIZER: Como serializar o valor (JSON)
   */

  @Bean
  public ProducerFactory<String, Object> producerFactory() {
    Map<String, Object> configProps = new HashMap<>();

    // Endereço do Kafka
    configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);

    // Serializer da chave (usaremos o ID da API como chave)
    configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

    // Serializer do valor (nosso evento será convertido em JSON)
    configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

    // Adiciona informações de tipo no JSON (necessário para desserialização)
    configProps.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, false);

    return new DefaultKafkaProducerFactory<>(configProps);
  }

  /**
   * KafkaTemplate: ferramenta principal para enviar mensagens.
   * 
   * Uso:
   * kafkaTemplate.send("nome-do-topico", chave, objeto);
   */
  @Bean
  public KafkaTemplate<String, Object> kafkaTemplate() {
    return new KafkaTemplate<>(producerFactory());
  }
}
