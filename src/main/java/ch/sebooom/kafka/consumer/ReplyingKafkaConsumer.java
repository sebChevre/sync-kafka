package ch.sebooom.kafka.consumer;

import ch.sebooom.kafka.controller.SommeComandDto;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;


@Component
public class ReplyingKafkaConsumer {
	 
	 @KafkaListener(topics = "${kafka.topic.sommeDemandeeTopic}")
	 @SendTo
	  public SommeComandDto listen(SommeComandDto request)  {
		 
		 int sum = request.getFirstNumber() + request.getSecondNumber();
		 request.setAdditionalProperty("sum", sum);
		 return request;
	  }

}
