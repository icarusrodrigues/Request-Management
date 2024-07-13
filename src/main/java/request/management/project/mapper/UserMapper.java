package request.management.project.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import request.management.project.dto.UserDto;
import request.management.project.model.User;

@Component
public class UserMapper extends GenericMapper<UserDto, User> {
    @Autowired
    private ModelMapper modelMapper;

    @Override
    public UserDto toDto(User entity) {
        return modelMapper.map(entity, UserDto.class);
    }

    @Override
    public User toEntity(UserDto dto) {
        return modelMapper.map(dto, User.class);
    }
}
