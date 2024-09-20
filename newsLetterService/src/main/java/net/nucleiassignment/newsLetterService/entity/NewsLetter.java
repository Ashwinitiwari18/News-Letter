package net.nucleiassignment.newsLetterService.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.sql.Time;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@Table(name = "newsletter")
public class NewsLetter {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @NonNull
  private String title;

  @NonNull
  private String content;

  @NonNull
  private Double price;

  @NonNull
  private LocalDateTime createdAt;

  @NonNull
  private Time duration;
}
