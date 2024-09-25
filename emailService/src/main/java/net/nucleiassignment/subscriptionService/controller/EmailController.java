package net.nucleiassignment.subscriptionService.controller;

import net.nucleiassignment.subscriptionService.service.EmailService;
import net.nucleiassignment.subscriptionService.service.UserSubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/email")
public class EmailController {

    @Autowired
    private UserSubscriptionService userSubscriptionService;
    @Autowired
    private EmailService emailService;

    @PostMapping("/send/{newsletterId}")
    public String sendNewsletterToSubscribers(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable Integer newsletterId) {
        emailService.sendNewsletterToSubscribedUsers(newsletterId, authorizationHeader);
        return "Newsletter sent to all subscribed users!";
    }
}
