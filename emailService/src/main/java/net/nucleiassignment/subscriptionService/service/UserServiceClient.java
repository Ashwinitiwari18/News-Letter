package net.nucleiassignment.subscriptionService.service;

import net.nucleiassignment.subscriptionService.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "userService",url ="${userService.url}")
public interface UserServiceClient {
  @GetMapping("/users/getUserByToken")
  UserDTO getUserByToken(@RequestHeader("Authorization") String authorizationHeader);

  @GetMapping("/users/{id}")
  UserDTO getUserById(@PathVariable Integer id);
}
