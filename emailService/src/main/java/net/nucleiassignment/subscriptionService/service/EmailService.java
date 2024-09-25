package net.nucleiassignment.subscriptionService.service;

import net.nucleiassignment.subscriptionService.dto.EmailMessage;
import net.nucleiassignment.subscriptionService.dto.NewsLetterDTO;
import net.nucleiassignment.subscriptionService.dto.UserDTO;
import net.nucleiassignment.subscriptionService.kafka.EmailProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmailService {

  @Autowired
  private JavaMailSender mailSender;

  @Autowired
  private EmailProducer emailProducer;
  @Autowired
  private UserSubscriptionService userSubscriptionService;

  @Autowired
  private UserClientService userClientService;

  public void sendNewsletterEmail(String to, String subject, String text) {
    SimpleMailMessage message = new SimpleMailMessage();
    message.setTo(to);
    message.setSubject(subject);
    message.setText(text);
    mailSender.send(message);
  }

  public void sendNewsletterToSubscribedUsers(Integer newsletterId, String authorizationHeader) {
    if (isAdmin(authorizationHeader)) {
      throw new RuntimeException("Only admin can send newsletters to the users");
    }

    List<Integer> subscribedUsers = userSubscriptionService.getUsersSubscribedToNewsletter(newsletterId);
    NewsLetterDTO newsletter = userSubscriptionService.getNewsLetter(authorizationHeader, newsletterId);

    String subject = "Newsletter: " + newsletter.getTitle();
    String emailContent = "Dear Subscriber,\n\n" +
            "Here is the latest edition of the newsletter:\n" + newsletter.getContent() + "\n\n" +
            "Best regards,\nNewsletter Team";

    for (Integer userId : subscribedUsers) {
      UserDTO user = userSubscriptionService.getUserById(userId);

      // Create EmailMessage DTO
      EmailMessage emailMessage = new EmailMessage();
      emailMessage.setTo(user.getEmail());
      emailMessage.setSubject(subject);
      emailMessage.setBody(emailContent);

      // Send email event to Kafka
      emailProducer.sendEmailMessage(emailMessage);
    }
  }

  private boolean isAdmin(String authorizationHeader){
    try {
      UserDTO userDTO = userClientService.getUserByToken(authorizationHeader);
      return userDTO != null && userDTO.getRoles().stream()
              .anyMatch(role -> "ROLE_ADMIN".equals(role.getRoleName()));
    } catch (Exception e) {
      return false;
    }
  }
}