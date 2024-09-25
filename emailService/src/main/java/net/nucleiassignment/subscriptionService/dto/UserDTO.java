package net.nucleiassignment.subscriptionService.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Set;

@Data
@NoArgsConstructor
public class UserDTO implements Serializable {
  private static final long serialVersionUID = 1L;

  private Integer id;
  private String userName;
  private String fullName;
  private String email;
  private Set<RoleDTO> roles;
}
