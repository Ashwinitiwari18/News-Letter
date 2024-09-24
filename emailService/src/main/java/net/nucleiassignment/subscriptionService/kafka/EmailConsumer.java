package net.nucleiassignment.subscriptionService.kafka;

import net.nucleiassignment.subscriptionService.dto.EmailMessage;
import net.nucleiassignment.subscriptionService.service.EmailService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class EmailConsumer {

    private final EmailService emailService;

    public EmailConsumer(EmailService emailService) {
        this.emailService = emailService;
    }

    @KafkaListener(topics = "email-topic", groupId = "email-group")
    public void listen(EmailMessage emailMessage) {
        emailService.sendNewsletterEmail(emailMessage.getTo(), emailMessage.getSubject(), emailMessage.getBody());
    }
}
