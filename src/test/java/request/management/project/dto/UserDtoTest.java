package request.management.project.dto;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import request.management.project.model.Gender;
import request.management.project.model.UserType;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class UserDtoTest {
    @Test
    void testNoArgsConstructor() {
        Long id = 1L;
        String username = "username";
        String cpf = "000.000.000-00";
        String email = "email@email.com";
        String registrationNumber = "1234";
        String name = "name";
        String password = "password";
        LocalDate birthDate = LocalDate.now();
        Gender gender = Gender.NON_SPECIFICATION;
        UserType userType = UserType.ADMIN;
        List<RequestDto> requests = new ArrayList<>();

        var user = new UserDto();
        user.setId(id);
        user.setUsername(username);
        user.setCpf(cpf);
        user.setEmail(email);
        user.setRegistrationNumber(registrationNumber);
        user.setName(name);
        user.setPassword(password);
        user.setBirthDate(birthDate);
        user.setGender(gender);
        user.setUserType(userType);
        user.setRequests(requests);

        assertEquals(user.getId(), id);
        assertEquals(user.getUsername(), username);
        assertEquals(user.getCpf(), cpf);
        assertEquals(user.getEmail(), email);
        assertEquals(user.getRegistrationNumber(), registrationNumber);
        assertEquals(user.getName(), name);
        assertEquals(user.getPassword(), password);
        assertEquals(user.getBirthDate(), birthDate);
        assertEquals(user.getGender(), gender);
        assertEquals(user.getUserType(), userType);
        assertEquals(user.getRequests(), requests);
    }

    @Test
    void testEquals() {
        var user1 = new UserDto();
        var user2 = new UserDto();

        assertEquals(user1, user2);
    }
}
