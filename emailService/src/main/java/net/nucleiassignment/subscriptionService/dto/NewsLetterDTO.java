package net.nucleiassignment.subscriptionService.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class NewsLetterDTO implements Serializable {
  private static final long serialVersionUID = 1L;

  private Integer id;
  private String title;
  private String content;
  private Double price;
  private LocalDateTime createdAt;
  private Duration duration;
}
