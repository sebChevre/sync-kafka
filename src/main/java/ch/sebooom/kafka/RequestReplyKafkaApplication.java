package ch.sebooom.kafka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(basePackages = {
        "ch.sebooom"
    })
@SpringBootApplication
public class RequestReplyKafkaApplication {

	public static void main(String[] args) {
		SpringApplication.run(RequestReplyKafkaApplication.class, args);
	}
}
