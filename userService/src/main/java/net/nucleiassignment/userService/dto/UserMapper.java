package net.nucleiassignment.userService.dto;

import net.nucleiassignment.userService.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
  @Mapping(target = "id", source = "user.id")
  UserDTO toUserDTO(User user);

  List<UserDTO> toUserDTOs(List<User> users);
}