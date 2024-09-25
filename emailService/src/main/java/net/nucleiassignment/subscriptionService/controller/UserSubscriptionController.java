package net.nucleiassignment.subscriptionService.controller;

import net.nucleiassignment.subscriptionService.entity.UserSubscription;
import net.nucleiassignment.subscriptionService.service.UserSubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/userSubscriptions")
public class UserSubscriptionController {

  @Autowired
  private UserSubscriptionService userSubscriptionService;

  @GetMapping("/{id}")
  public ResponseEntity<?> getSubscriptionById(@PathVariable Integer id) {
    try {
      Optional<UserSubscription> subscription = userSubscriptionService.findSubscriptionById(id);
      if (subscription.isPresent()) {
        return ResponseEntity.ok(subscription.get());
      } else {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body("Error: Subscription with ID " + id + " not found.");
      }
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("Error fetching subscription: " + e.getMessage());
    }
  }

  @GetMapping("/all")
  public ResponseEntity<?> getAllSubscriptions(@RequestHeader("Authorization") String authorizationHeader) {
    try {
      List<UserSubscription> subscriptions = userSubscriptionService.findAllSubscription(authorizationHeader);
      return ResponseEntity.ok(subscriptions);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("Error fetching all subscriptions: " + e.getMessage());
    }
  }

  @PostMapping("/add/{newsLetterId}")
  public ResponseEntity<?> createSubscription(@RequestHeader("Authorization") String authorizationHeader,@PathVariable int newsLetterId) {
    try {
      UserSubscription savedSubscription = userSubscriptionService.saveSubscription(authorizationHeader,newsLetterId);
      return ResponseEntity.status(HttpStatus.CREATED).body(savedSubscription);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body("Error creating subscription: " + e.getMessage());
    }
  }

  @PutMapping("/{id}/update")
  public ResponseEntity<?> updateSubscription(@RequestHeader("Authorization") String authorizationHeader,@PathVariable int id, @RequestBody UserSubscription userSubscription) {
    try {
      UserSubscription updatedSubscription = userSubscriptionService.updateSubscription(authorizationHeader,id, userSubscription);
      return ResponseEntity.ok(updatedSubscription);
    } catch (RuntimeException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body("Error updating subscription: " + e.getMessage());
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("Internal Server Error while updating subscription: " + e.getMessage());
    }
  }

  @DeleteMapping("/{id}/delete")
  public ResponseEntity<?> deleteSubscription(@PathVariable int id) {
    try {
      userSubscriptionService.deleteSubscription(id);
      return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    } catch (RuntimeException e) {
      System.out.println(e);
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body("Error: Subscription with ID " + id + " not found.");
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("Error deleting subscription: " + e.getMessage());
    }
  }
}
