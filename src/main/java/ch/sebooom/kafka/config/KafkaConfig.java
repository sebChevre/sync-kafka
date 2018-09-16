package ch.sebooom.kafka.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.config.ContainerProperties;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import ch.sebooom.kafka.controller.SommeComandDto;

@Configuration
public class KafkaConfig {
	
	  @Value("${kafka.bootstrap-servers}")
	  private String bootstrapServers;
	  
	  @Value("${kafka.topic.sommeCalculeeTopic}")
	  private String sommeCalculeeTopic;
	  
	  @Value("${kafka.consumerGroup}")
	  private String consumerGroup;
	
	  @Bean
	  public Map<String, Object> producerConfigs() {
	    Map<String, Object> props = new HashMap<>();
	    // list of host:port pairs used for establishing the initial connections to the Kakfa cluster
	    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
	        bootstrapServers);
	    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
	        StringSerializer.class);
	    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
	    return props;
	  }
	  
	  @Bean
	  public Map<String, Object> consumerConfigs() {
	    Map<String, Object> props = new HashMap<>();
	    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,bootstrapServers);
	    props.put(ConsumerConfig.GROUP_ID_CONFIG, "calculGroup");
	    return props;
	  }

	  @Bean
	  public ProducerFactory<String,SommeComandDto> producerFactory() {
	    return new DefaultKafkaProducerFactory<>(producerConfigs());
	  }
	  
	  @Bean
	  public KafkaTemplate<String, SommeComandDto> kafkaTemplate() {
	    return new KafkaTemplate<>(producerFactory());
	  }
	  
	  @Bean
	  public ReplyingKafkaTemplate<String, SommeComandDto, SommeComandDto> replyKafkaTemplate(ProducerFactory<String, SommeComandDto> pf, KafkaMessageListenerContainer<String, SommeComandDto> container){
		  return new ReplyingKafkaTemplate<>(pf, container);
		  
	  }
	  
	  @Bean
	  public KafkaMessageListenerContainer<String, SommeComandDto> replyContainer(ConsumerFactory<String, SommeComandDto> cf) {
	        ContainerProperties containerProperties = new ContainerProperties(sommeCalculeeTopic);
	        return new KafkaMessageListenerContainer<>(cf, containerProperties);
	    }
	  
	  @Bean
	  public ConsumerFactory<String, SommeComandDto> consumerFactory() {
	    return new DefaultKafkaConsumerFactory<>(consumerConfigs(),new StringDeserializer(),new JsonDeserializer<>(SommeComandDto.class));
	  }
	  
	  @Bean
	  public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, SommeComandDto>> kafkaListenerContainerFactory() {
	    ConcurrentKafkaListenerContainerFactory<String, SommeComandDto> factory = new ConcurrentKafkaListenerContainerFactory<>();
	    factory.setConsumerFactory(consumerFactory());
	    factory.setReplyTemplate(kafkaTemplate());
	    return factory;
	  }
	  
}

