package net.nucleiassignment.newsLetterService.repository;

import net.nucleiassignment.newsLetterService.entity.NewsLetter;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NewsLetterRepository extends JpaRepository<NewsLetter,Integer>{
}
