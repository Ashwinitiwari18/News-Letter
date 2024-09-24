package net.nucleiassignment.newsLetterService.controller;

import net.nucleiassignment.newsLetterService.entity.NewsLetter;
import net.nucleiassignment.newsLetterService.entity.UserDTO;
import net.nucleiassignment.newsLetterService.service.NewsLetterService;
import net.nucleiassignment.newsLetterService.service.UserClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/newsLetter")
public class NewsLetterController {
  @Autowired
  private NewsLetterService newsLetterService;

  @Autowired
  private UserClientService userClientService;

  @GetMapping("/checkUser")
  public ResponseEntity<?> checkUser(@RequestHeader("Authorization") String authorizationHeader){
    try{
      return ResponseEntity.ok(userClientService.getUserByToken(authorizationHeader));
    }catch (Exception e){
      return new ResponseEntity<>("An error occurred while checking User.",HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @GetMapping("/all")
  public ResponseEntity<?> getAll(){
    try{
      List<NewsLetter> newsLetters = newsLetterService.getAllNewsLetter();
      return ResponseEntity.ok(newsLetters);
    }catch (Exception e){
      return new ResponseEntity<>("An error occurred while getting News Letters.",HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @PostMapping("/add")
  public ResponseEntity<?> addNewsLetter(@RequestHeader("Authorization") String authorizationHeader,@RequestBody NewsLetter newsLetter){
    if (isAdmin(authorizationHeader)){
      try{
        return new ResponseEntity<>(newsLetterService.saveNewsLetter(newsLetter),HttpStatus.CREATED);
      }catch (Exception e){
        return new ResponseEntity<>("An error occurred while adding News Letters.",HttpStatus.INTERNAL_SERVER_ERROR);
      }
    }else {
      return new ResponseEntity<>("Unauthorized access", HttpStatus.FORBIDDEN);
    }

  }

  @GetMapping("{id}")
  public ResponseEntity<?> findById(@PathVariable int id){
    try{
      Optional<NewsLetter> newsLetter = newsLetterService.getNewsLetterById(id);
      if (newsLetter.isPresent()){
        return new ResponseEntity<>(newsLetter.get(), HttpStatus.OK);
      } else {
        return new ResponseEntity<>("NewsLetter Not Found", HttpStatus.NOT_FOUND);
      }
    }catch (Exception e){
      return new ResponseEntity<>("An error occurred while fetching News Letters.",HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @DeleteMapping("/{id}/delete")
  public ResponseEntity<?> deleteById(@RequestHeader("Authorization") String authorizationHeader,@PathVariable int id){
    if (isAdmin(authorizationHeader)){
      try{
        if (newsLetterService.getNewsLetterById(id).isPresent()){
          newsLetterService.deleteNewsLetterById(id);
          return new ResponseEntity<>("NewsLetter deleted", HttpStatus.OK);
        }else{
          return new ResponseEntity<>("NewsLetter Not Found",HttpStatus.NOT_FOUND);
        }
      }catch (Exception e){
        return new ResponseEntity<>("An error occurred while deleting the News Letter.", HttpStatus.INTERNAL_SERVER_ERROR);
      }
    }else {
      return new ResponseEntity<>("Unauthorized access", HttpStatus.FORBIDDEN);
    }

  }

  @PutMapping("/{id}/update")
  public ResponseEntity<?> updateUser(@RequestHeader("Authorization") String authorizationHeader,@PathVariable int id,@RequestBody NewsLetter newsLetter){
    if (isAdmin(authorizationHeader)){
      try{
        NewsLetter updatedNewsLetter = newsLetterService.updateNewsLetter(id, newsLetter);
        return ResponseEntity.ok(updatedNewsLetter);
      }catch (Exception e){
        return new ResponseEntity<>("An error occurred while updating the News Letter.", HttpStatus.INTERNAL_SERVER_ERROR);
      }
    }else {
      return new ResponseEntity<>("Unauthorized access", HttpStatus.FORBIDDEN);
    }
  }

  private boolean isAdmin(String authorizationHeader){
//    try{
//      ResponseEntity<?> userResponse = userClientService.getUserByToken(authorizationHeader);
//      if (userResponse.getStatusCode().is2xxSuccessful()){
//        Object responseBody = userResponse.getBody();
//        if (responseBody instanceof LinkedHashMap){
//          ObjectMapper objectMapper = new ObjectMapper();
//          UserDTO userDTO = objectMapper.convertValue(responseBody,UserDTO.class);
//          return userDTO!=null && userDTO.getRoles().stream()
//              .anyMatch(role->"ROLE_ADMIN".equals(role.getRoleName()));
//        }
//      }
//    }catch (Exception e){
//      System.out.println("Error " + e);
//    }
//    return false;

    try {
      UserDTO userDTO = userClientService.getUserByToken(authorizationHeader);
      return userDTO != null && userDTO.getRoles().stream()
          .anyMatch(role -> "ROLE_ADMIN".equals(role.getRoleName()));
    } catch (Exception e) {
      System.out.println("Error in isAdmin: " + e);
      return false;
    }
  }
}
