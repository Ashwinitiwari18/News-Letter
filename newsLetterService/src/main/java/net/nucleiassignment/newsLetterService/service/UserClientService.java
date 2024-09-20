package net.nucleiassignment.newsLetterService.service;

import net.nucleiassignment.newsLetterService.entity.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "userService",url ="${userService.url}")
public interface UserClientService {
  @GetMapping("/users/getUserByToken")
  UserDTO getUserByToken(@RequestHeader("Authorization") String authorizationHeader);
}
