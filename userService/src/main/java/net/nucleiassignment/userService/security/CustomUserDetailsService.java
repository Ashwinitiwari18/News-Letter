package net.nucleiassignment.userService.security;

import net.nucleiassignment.userService.entity.User;
import net.nucleiassignment.userService.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

  @Autowired
  private UserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    // Fetch the user from the database
    User user = userRepository.findByUserName(username);

    if (user == null) {
      throw new UsernameNotFoundException("User not found with username: " + username);
    }

    // Return a Spring Security User object
    return new org.springframework.security.core.userdetails.User(user.getUserName(), user.getPassword(), user.getRoles());
  }
}