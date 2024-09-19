package net.nucleiassignment.subscriptionService.repository;

import net.nucleiassignment.subscriptionService.entity.NewsLetter;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NewsLetterRepository extends JpaRepository<NewsLetter,Integer>{
}
