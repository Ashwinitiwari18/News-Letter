package net.nucleiassignment.userService.repository;

import net.nucleiassignment.userService.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
  public User findByUserName(String userName);
}
