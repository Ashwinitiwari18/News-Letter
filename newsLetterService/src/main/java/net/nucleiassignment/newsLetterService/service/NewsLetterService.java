package net.nucleiassignment.newsLetterService.service;

import net.nucleiassignment.newsLetterService.entity.NewsLetter;
import net.nucleiassignment.newsLetterService.repository.NewsLetterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class NewsLetterService {
  @Autowired
  private NewsLetterRepository newsLetterRepository;

  public NewsLetter saveNewsLetter(NewsLetter newsLetter){
    newsLetter.setCreatedAt(LocalDateTime.now());
    return newsLetterRepository.save(newsLetter);
  }

  @Cacheable(value = "newsLetter", key = "#id")
  public Optional<NewsLetter> getNewsLetterById(int id){
    return newsLetterRepository.findById(id);
  }


  public List<NewsLetter> getAllNewsLetter(){
    return newsLetterRepository.findAll();
  }

  @CacheEvict(value = "newsLetter", key = "#id")
  public void deleteNewsLetterById(int id){
    newsLetterRepository.deleteById(id);
  }

  @CachePut(value = "newsLetter", key = "#id")
  public NewsLetter updateNewsLetter(int id,NewsLetter newsLetter){
    Optional<NewsLetter> existingNewsLetterOpt = newsLetterRepository.findById(id);
    if (existingNewsLetterOpt.isPresent()){
      NewsLetter existingNewsLetter = existingNewsLetterOpt.get();
      if (newsLetter.getTitle()!=null && !newsLetter.getTitle().isEmpty()){
        existingNewsLetter.setTitle(newsLetter.getTitle());
      }
      if (newsLetter.getContent()!=null && !newsLetter.getContent().isEmpty()){
        existingNewsLetter.setContent(newsLetter.getContent());
      }
      if (newsLetter.getPrice()!=null && newsLetter.getPrice()!=0){
        existingNewsLetter.setPrice(newsLetter.getPrice());
      }
      if (newsLetter.getDuration()!=null){
        existingNewsLetter.setDuration(newsLetter.getDuration());
      }
      return newsLetterRepository.save(existingNewsLetter);
    }else {
      throw new RuntimeException("NewsLetter not found with id: " + id);
    }
  }
}
