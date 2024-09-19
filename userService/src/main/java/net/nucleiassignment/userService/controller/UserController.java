package net.nucleiassignment.userService.controller;

import jakarta.servlet.http.HttpServletRequest;
import net.nucleiassignment.userService.dto.UserDTO;
import net.nucleiassignment.userService.dto.UserMapper;
import net.nucleiassignment.userService.entity.Role;
import net.nucleiassignment.userService.entity.User;
import net.nucleiassignment.userService.service.UserService;
import net.nucleiassignment.userService.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
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
  private UserMapper userMapper;

  @Autowired
  private final PasswordEncoder passwordEncoder;

  public UserController(PasswordEncoder passwordEncoder) {
    this.passwordEncoder = passwordEncoder;
  }

  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestParam String userName, @RequestParam String password) {
    try {
      return userService.findByUserName(userName)
          .filter(user -> passwordEncoder.matches(password, user.getPassword()))
          .map(user -> {
            Set<Role> roles = user.getRoles();
            String token = jwtUtil.generateToken(user.getId(), roles);
            return ResponseEntity.ok(Map.of("token", token));
          }).orElse(ResponseEntity.badRequest().body(
              Map.of("error", "Invalid credentials or user not found")));
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(Map.of("error", "Invalid credentials or user not found"));
    }
  }

  @GetMapping("/{id}")
  public ResponseEntity<?> getUserById(@PathVariable int id) {
    if (isCurrentUser(id) || isAdmin()){
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
    }else {
      return new ResponseEntity<>("Unauthorized access", HttpStatus.FORBIDDEN);
    }
  }

  @GetMapping("/getByToken")
  public ResponseEntity<?> getUserByToken(HttpServletRequest request) {
    try {
      final String authorizationHeader = request.getHeader("Authorization");

      if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
        return ResponseEntity.badRequest().body("Authorization header missing or invalid");
      }

      String jwt = authorizationHeader.substring(7);
      Integer userId = jwtUtil.extractUserId(jwt);
      Optional<User> user = userService.getUserById(userId);
      if (user.isPresent()){
        UserDTO userDTO = userMapper.toUserDTO(user.get());
        return ResponseEntity.ok(userDTO);
      }else {
        return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
      }
    } catch (Exception e) {
      return new ResponseEntity<>("An error occurred while fetching the user.", HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @PostMapping("/{id}/assignRoles")
  public ResponseEntity<?> assignRolesToUser(@PathVariable int id, @RequestBody Set<Role> roles) {
    try {
      User user = userService.assignRolesToUser(id, roles);
      return new ResponseEntity<>(user, HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>("An error occurred while assigning roles.", HttpStatus.INTERNAL_SERVER_ERROR);
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

  @GetMapping("/detail/all")
  public ResponseEntity<?> getAllUsersDetail() {
      try {
        List<User> users = userService.getAllUsers();
        List<UserDTO> userDTOs = userMapper.toUserDTOs(users);
        return ResponseEntity.ok(userDTOs);
      } catch (Exception e) {
        return new ResponseEntity<>("An error occurred while fetching users.", HttpStatus.INTERNAL_SERVER_ERROR);
      }
  }

  @DeleteMapping("/delete/{id}")
  public ResponseEntity<?> deleteUser(@PathVariable int id) {
    if (isCurrentUser(id) || isAdmin()){
      try {
        if (userService.getUserById(id).isPresent()) {
          userService.deleteUser(id);
          return new ResponseEntity<>("User deleted", HttpStatus.OK);
        } else {
          return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
      } catch (Exception e) {
        return new ResponseEntity<>("An error occurred while deleting the user.", HttpStatus.INTERNAL_SERVER_ERROR);
      }
    }
    return new ResponseEntity<>("Unauthorized access", HttpStatus.FORBIDDEN);
  }

  @PutMapping("/update/{id}")
  public ResponseEntity<?> updateUser(@PathVariable int id, @RequestBody User user) {
    if (isCurrentUser(id) || isAdmin()) {
      try {
        User updatedUser = userService.updateUser(id, user);
        return ResponseEntity.ok(updatedUser);
      } catch (Exception e) {
        return new ResponseEntity<>("Error updating user", HttpStatus.INTERNAL_SERVER_ERROR);
      }
    } else {
      return new ResponseEntity<>("Unauthorized access", HttpStatus.FORBIDDEN);
    }
  }

  private boolean isAdmin() {
    System.out.println(SecurityContextHolder.getContext().getAuthentication().getAuthorities());
    return SecurityContextHolder.getContext().getAuthentication()
        .getAuthorities().stream()
        .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
  }

  private boolean isCurrentUser(int userId) {
    Integer currentUserId = jwtUtil.extractUserIdFromContext();
    return currentUserId != null && currentUserId == userId;
  }
}