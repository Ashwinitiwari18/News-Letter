package net.nucleiassignment.newsLetterService.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
public class UserDTO {
  private Integer id;
  private String userName;
  private String fullName;
  private String email;
  private Set<RoleDTO> roles;
}