package net.nucleiassignment.subscriptionService.dto;

import lombok.Data;

@Data
public class EmailMessage {
    private String to;
    private String subject;
    private String body;
}
