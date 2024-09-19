package net.nucleiassignment.subscriptionService.controller;

import net.nucleiassignment.subscriptionService.entity.NewsLetter;
import net.nucleiassignment.subscriptionService.service.NewsLetterService;
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
  public ResponseEntity<?> addNewsLetter(@RequestBody NewsLetter newsLetter){
    try{
      return new ResponseEntity<>(newsLetterService.saveNewsLetter(newsLetter),HttpStatus.CREATED);
    }catch (Exception e){
      return new ResponseEntity<>("An error occurred while adding News Letters.",HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @GetMapping("{id}")
  public ResponseEntity<?> findById(@PathVariable int id){
    try{
      Optional<NewsLetter> newsLetter = newsLetterService.getNewsLetterById(id);
      if (newsLetter.isPresent()){
        return new ResponseEntity<>(newsLetter.get(),HttpStatus.FOUND);
      }else {
        return new ResponseEntity<>("NewsLetter Not Found",HttpStatus.NOT_FOUND);
      }
    }catch (Exception e){
      return new ResponseEntity<>("An error occurred while fetching News Letters.",HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @DeleteMapping("/{id}/delete")
  public ResponseEntity<?> deleteById(@PathVariable int id){
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
  }

  @PutMapping("/update/{id}")
  public ResponseEntity<?> updateUser(@PathVariable int id,@RequestBody NewsLetter newsLetter){
    try{
      NewsLetter updatedNewsLetter = newsLetterService.updateNewsLetter(id, newsLetter);
      return ResponseEntity.ok(updatedNewsLetter);
    }catch (Exception e){
      return new ResponseEntity<>("An error occurred while updating the News Letter.", HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
