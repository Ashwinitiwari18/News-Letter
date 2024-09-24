package net.nucleiassignment.subscriptionService.repository;

import net.nucleiassignment.subscriptionService.entity.UserSubscription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserSubscriptionRepository extends JpaRepository<UserSubscription,Integer> {
  List<UserSubscription> findByNewsLetterId(Integer newsletterId);
}
