package net.nucleiassignment.userService.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import net.nucleiassignment.userService.entity.Role;

import java.io.Serializable;
import java.util.Set;

@Data
@NoArgsConstructor
public class UserDTO implements Serializable {
  private Integer id;
  private String userName;
  private String fullName;
  private String email;
  private Set<Role> roles;
}
