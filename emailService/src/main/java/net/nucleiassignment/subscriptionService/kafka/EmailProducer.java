package net.nucleiassignment.subscriptionService.kafka;

import net.nucleiassignment.subscriptionService.dto.EmailMessage;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class EmailProducer {

    private final KafkaTemplate<String, EmailMessage> kafkaTemplate;

    public EmailProducer(KafkaTemplate<String, EmailMessage> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendEmailMessage(EmailMessage emailMessage) {
        kafkaTemplate.send("email-topic", emailMessage);
    }
}
