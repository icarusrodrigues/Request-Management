package request.management.project.mapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import request.management.project.dto.UserDto;
import request.management.project.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class UserMapperTest {

    @Autowired
    private UserMapper mapper;

    @Test
    void testToDto() {
        Long id = 1L;
        String username = "username";

        var user = new User();
        user.setId(id);
        user.setUsername(username);

        var userDto = mapper.toDto(user);

        assertEquals(userDto.getId(), user.getId());
        assertEquals(userDto.getUsername(), user.getUsername());
    }

    @Test
    void testToEntity() {
        Long id = 1L;
        String username = "username";

        var userDto = new UserDto();
        userDto.setId(id);
        userDto.setUsername(username);

        var user = mapper.toEntity(userDto);

        assertEquals(user.getId(), userDto.getId());
        assertEquals(user.getUsername(), userDto.getUsername());
    }
}
