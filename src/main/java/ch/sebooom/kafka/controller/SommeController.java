package ch.sebooom.kafka.controller;

import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SommeController {
	
	@Autowired
	ReplyingKafkaTemplate<String, SommeComandDto,SommeComandDto> kafkaTemplate;
	
	@Value("${kafka.topic.sommeDemandeeTopic}")
	String sommeDemandeeTopic;
	
	@Value("${kafka.topic.sommeCalculeeTopic}")
	String sommeCalculeeTopic;
	
	@ResponseBody
	@PostMapping(value="/somme",produces=MediaType.APPLICATION_JSON_VALUE,consumes=MediaType.APPLICATION_JSON_VALUE)
	public SommeComandDto sum(@RequestBody SommeComandDto request) throws InterruptedException, ExecutionException {
		// create producer record
		ProducerRecord<String, SommeComandDto> record = new ProducerRecord<String, SommeComandDto>(sommeDemandeeTopic, request);
		// set reply topic in header
		record.headers().add(new RecordHeader(KafkaHeaders.REPLY_TOPIC, sommeCalculeeTopic.getBytes()));
		// post in kafka topic
		RequestReplyFuture<String, SommeComandDto, SommeComandDto> sendAndReceive = kafkaTemplate.sendAndReceive(record);

		// confirm if producer produced successfully
		SendResult<String, SommeComandDto> sendResult = sendAndReceive.getSendFuture().get();
		
		//print all headers
		sendResult.getProducerRecord().headers().forEach(header -> System.out.println(header.key() + ":" + header.value().toString()));
		
		// get consumer record
		ConsumerRecord<String, SommeComandDto> consumerRecord = sendAndReceive.get();
		// return consumer value
		return consumerRecord.value();		
	}

}
