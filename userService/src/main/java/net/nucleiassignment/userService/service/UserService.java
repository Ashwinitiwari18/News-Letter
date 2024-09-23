package net.nucleiassignment.userService.service;

import net.nucleiassignment.userService.entity.Role;
import net.nucleiassignment.userService.entity.User;
import net.nucleiassignment.userService.repository.RoleRepository;
import net.nucleiassignment.userService.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private RoleRepository roleRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  public User saveUser(User user) {
    user.setPassword(passwordEncoder.encode(user.getPassword()));
    System.out.println("service " + user);
    return userRepository.save(user);
  }

  public User assignRolesToUser(int userId, Set<Role> roles) {
    User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

    Set<Role> persistedRoles = roles.stream()
        .map(role -> roleRepository.findByRoleName(role.getRoleName())
            .orElseThrow(() -> new RuntimeException("Role not found: " + role.getRoleName())))
        .collect(Collectors.toSet());

    user.setRoles(persistedRoles);

    return userRepository.save(user);
  }
  public List<User> getAllUsers() {
    return userRepository.findAll();
  }
  public Optional<User> getUserById(int id) {
    return userRepository.findById(id);
  }

  public void deleteUser(int id) {
    userRepository.deleteById(id);
  }

  public User updateUser(int id, User user) {
    Optional<User> existingUserOpt = userRepository.findById(id);
    if (existingUserOpt.isPresent()) {
      User existingUser = existingUserOpt.get();
      if (user.getUserName() != null && !user.getUserName().isEmpty()) {
        existingUser.setUserName(user.getUserName());
      }
      if (user.getFullName() != null && !user.getFullName().isEmpty()) {
        existingUser.setFullName(user.getFullName());
      }
      if (user.getPassword() != null && !user.getPassword().isEmpty()) {
        existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
      }
      if (user.getEmail() != null && !user.getEmail().isEmpty()) {
        existingUser.setEmail(user.getEmail());
      }
      return userRepository.save(existingUser);
    } else {
      throw new RuntimeException("User not found with id: " + id);
    }
  }

  public List<Role> getAllRoles() {
    return roleRepository.findAll();
  }

  public Optional<User> findByUserName(String userName) {
    return Optional.ofNullable(userRepository.findByUserName(userName));
  }
}