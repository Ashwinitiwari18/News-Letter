package net.nucleiassignment.subscriptionService.service;

import net.nucleiassignment.subscriptionService.dto.NewsLetterDTO;
import net.nucleiassignment.subscriptionService.dto.UserDTO;
import net.nucleiassignment.subscriptionService.entity.UserSubscription;
import net.nucleiassignment.subscriptionService.repository.UserSubscriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserSubscriptionService {
  @Autowired
  private UserSubscriptionRepository userSubscriptionRepository;
  @Autowired
  private UserClientService userClientService;
  @Autowired
  private NewsLetterClientService newsLetterClientService;

  @Cacheable(value = "subscriptions", key = "#id")
  public Optional<UserSubscription> findSubscriptionById(Integer id){
    return userSubscriptionRepository.findById(id);
  }

  public List<UserSubscription> findAllSubscription(String authorizationHeader){
    UserDTO user = getUser(authorizationHeader);
    boolean isAdmin = user != null && user.getRoles().stream()
            .anyMatch(role -> "ROLE_ADMIN".equals(role.getRoleName()));
    if (isAdmin){
      return userSubscriptionRepository.findAll();
    }else {
      throw new RuntimeException("User is not admin.");
    }
  }

  public UserSubscription saveSubscription(String authorizationHeader,int newsLetterId){
    UserSubscription userSubscription = new UserSubscription();
    try{
      UserDTO userDTO = getUser(authorizationHeader);
      NewsLetterDTO newsLetterDTO = getNewsLetter(authorizationHeader,newsLetterId);
      userSubscription.setUserId(userDTO.getId());
      userSubscription.setNewsLetterId(newsLetterDTO.getId());
      userSubscription.setStartDate(LocalDateTime.now());
      userSubscription.setEndDate(userSubscription.getStartDate().plus(newsLetterDTO.getDuration()));
    }catch (Exception e){
      System.out.println(e);
      throw new RuntimeException("Error not found : " + e);
    }
    return userSubscriptionRepository.save(userSubscription);
  }

  public UserSubscription updateSubscription(String authorizationHeader,int subscriptionId,UserSubscription userSubscription) {
    Optional<UserSubscription> oldEntryOpt = userSubscriptionRepository.findById(subscriptionId);
    if (oldEntryOpt.isPresent()) {
      UserSubscription oldEntry = oldEntryOpt.get();
      UserDTO user = getUser(authorizationHeader);
      if (!Objects.equals(user.getId(), oldEntry.getUserId())){
        throw new RuntimeException("User id misMatch: " + user.getId() + ", " + oldEntry.getUserId());
      }
      if (userSubscription.getUserId() != null) {
        oldEntry.setUserId(userSubscription.getUserId());
      }
      if (userSubscription.getNewsLetterId() != null) {
        oldEntry.setNewsLetterId(userSubscription.getNewsLetterId());
      }
      if (userSubscription.getEndDate() != null) {
        oldEntry.setEndDate(userSubscription.getEndDate());
      }
      if (userSubscription.getSubsrciptionStatus() != null) {
        oldEntry.setSubsrciptionStatus(userSubscription.getSubsrciptionStatus());
      }
      return userSubscriptionRepository.save(oldEntry);
    } else {
      throw new RuntimeException("Subscription not found with id: " + subscriptionId);
    }
  }

  @CacheEvict(value = "subscriptions", key = "#id")
  public void deleteSubscription(int id){
    userSubscriptionRepository.deleteById(id);
  }

  public UserDTO getUserById(Integer userId) {
    try {
      UserDTO user = userClientService.getUserById(userId);
      if (user != null) {
        return user;
      } else {
        throw new Exception("User not found for the given authorization header.");
      }
    } catch (Exception e) {
      throw new RuntimeException("Failed to retrieve user from user service", e);
    }
  }

  public List<Integer> getUsersSubscribedToNewsletter(Integer newsletterId) {
    List<UserSubscription> subscriptions = userSubscriptionRepository.findByNewsLetterId(newsletterId);

    return subscriptions.stream()
            .map(UserSubscription::getUserId)
            .collect(Collectors.toList());
  }

  private UserDTO getUser(String token){
    UserDTO userDTO = userClientService.getUserByToken(token);
    if (userDTO==null){
      throw new RuntimeException("User token invalid");
    }
    return userDTO;
  }
  NewsLetterDTO getNewsLetter(String token, int id){
    NewsLetterDTO newsLetterDTO = newsLetterClientService.findById(token,id);
    if (newsLetterDTO==null){
      throw new RuntimeException("News Letter id not found");
    }
    return newsLetterDTO;
  }
}