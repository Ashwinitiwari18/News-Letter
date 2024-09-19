package net.nucleiassignment.userService.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import net.nucleiassignment.userService.entity.Role;

import java.util.Set;

@Data
@NoArgsConstructor
public class UserDTO {
  private Integer id;
  private String userName;
  private String fullName;
  private String email;
  private Set<Role> roles;
}
