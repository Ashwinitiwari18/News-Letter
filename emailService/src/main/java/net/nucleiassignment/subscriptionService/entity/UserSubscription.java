package net.nucleiassignment.subscriptionService.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.nucleiassignment.subscriptionService.enums.SubsrciptionStatus;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@Table(name = "subscriptions")
public class UserSubscription {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @NonNull
  private Integer userId;

  @NonNull
  private Integer newsLetterId;

  @NonNull
  private LocalDateTime startDate;

  @NonNull
  private LocalDateTime endDate;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", columnDefinition = "ENUM('ACTIVE','INACTIVE') default 'ACTIVE'")
  @NonNull
  private SubsrciptionStatus subsrciptionStatus = SubsrciptionStatus.ACTIVE;

  @Column(name = "created_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
  private LocalDateTime createdAt = LocalDateTime.now();
}
