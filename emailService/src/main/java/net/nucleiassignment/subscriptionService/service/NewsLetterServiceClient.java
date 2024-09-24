package net.nucleiassignment.subscriptionService.service;

import net.nucleiassignment.subscriptionService.dto.NewsLetterDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "newsLetterService",url ="${newsLetterService.url}")
public interface NewsLetterServiceClient {
  @GetMapping("/newsLetter/{id}")
  NewsLetterDTO findById(@RequestHeader("Authorization") String authorizationHeader, @PathVariable int id);
}
