package net.nucleiassignment.subscriptionService.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class NewsLetterDTO {
  private Integer id;
  private String title;
  private String content;
  private Double price;
  private LocalDateTime createdAt;
  private Duration duration;
}