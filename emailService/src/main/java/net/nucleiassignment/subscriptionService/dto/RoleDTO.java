package net.nucleiassignment.subscriptionService.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class RoleDTO implements Serializable {
  private static final long serialVersionUID = 1L;

  private Integer id;
  private String roleName;
}
