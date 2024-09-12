package net.nucleiassignment.userService.controller;

import net.nucleiassignment.userService.entity.Role;
import net.nucleiassignment.userService.entity.User;
import net.nucleiassignment.userService.service.UserService;
import net.nucleiassignment.userService.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/users")
public class UserController {

  @Autowired
  private UserService userService;

  @Autowired
  private JwtUtil jwtUtil;

  @Autowired
  private final PasswordEncoder passwordEncoder;

  public UserController(PasswordEncoder passwordEncoder) {
    this.passwordEncoder = passwordEncoder;
  }

  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestParam String userName,
                                 @RequestParam String password) {
    try{
      return userService.findByUserName(userName)
          .filter(user -> passwordEncoder.matches(password, user.getPassword()))
          .map(user -> {
            Set<Role> roles = user.getRoles();
            String token = jwtUtil.generateToken(user.getId(), roles);
            return ResponseEntity.ok(Map.of("token", token));
          }).orElse(ResponseEntity.badRequest().body(
              Map.of("error",
                  "Invalid credentials or user not found")));
    }catch (Exception e){
      return ResponseEntity.badRequest().body(
          Map.of("error",
              "Invalid credentials or user not found"));
    }
  }

  @PostMapping("/signup")
  public ResponseEntity<?> createUser(@RequestBody User user) {
    try {

      User savedUser = userService.saveUser(user);
      return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    } catch (Exception e) {
      return new ResponseEntity<>("An error occurred while creating the user.", HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @PostMapping("/{id}/assignRoles")
  public ResponseEntity<?> assignRolesToUser(@PathVariable int id, @RequestBody Set<Role> roles) {
    try {
      User user = userService.assignRolesToUser(id, roles);
      return new ResponseEntity<>(user, HttpStatus.OK);
    } catch (Exception e) {
      System.out.println(e);
      return new ResponseEntity<>("An error occurred while assigning roles.", HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @GetMapping("/all")
  public ResponseEntity<?> getAllUsers() {
    try {
      List<User> users = userService.getAllUsers();
      if (users.isEmpty()) {
        return new ResponseEntity<>("No users found.", HttpStatus.NO_CONTENT);
      }
      return new ResponseEntity<>(users, HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>("An error occurred while fetching users.", HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @GetMapping("/{id}")
  public ResponseEntity<?> getUserById(@PathVariable int id) {
    try {
      Optional<User> user = userService.getUserById(id);
      if (user.isPresent()) {
        return new ResponseEntity<>(user.get(), HttpStatus.OK);
      } else {
        return new ResponseEntity<>("User not found with id: " + id, HttpStatus.NOT_FOUND);
      }
    } catch (Exception e) {
      return new ResponseEntity<>("An error occurred while fetching the user.", HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @DeleteMapping("/delete/{id}")
  public ResponseEntity<?> deleteUser(@PathVariable int id) {
    try {
      if (userService.getUserById(id).isPresent()) {
        userService.deleteUser(id);
        return new ResponseEntity<>("User deleted with id: " + id, HttpStatus.OK);
      } else {
        return new ResponseEntity<>("User not found with id: " + id, HttpStatus.NOT_FOUND);
      }
    } catch (Exception e) {
      return new ResponseEntity<>("An error occurred while deleting the user.", HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @PutMapping("/update/{id}")
  public ResponseEntity<?> updateUser(@PathVariable int id, @RequestBody User user) {
    try {
      System.out.println("1");
      User updatedUser = userService.updateUser(id, user);
      System.out.println("2");
      return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>("An error occurred while updating the user.", HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}