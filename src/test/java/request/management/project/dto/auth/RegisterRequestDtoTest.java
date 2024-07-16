package request.management.project.dto.auth;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import request.management.project.dto.RequestDto;
import request.management.project.model.Gender;
import request.management.project.model.UserType;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class RegisterRequestDtoTest {
    @Test
    void testRequiredArgsConstructor() {
        String username = "username";
        String cpf = "000.000.000-00";
        String email = "email@email.com";
        String registrationNumber = "1234";
        String name = "name";
        String password = "password";
        LocalDate birthDate = LocalDate.now();
        Gender gender = Gender.NON_SPECIFICATION;
        UserType userType = UserType.ADMIN;

        var register = new RegisterRequestDto();
        register.setUsername(username);
        register.setCpf(cpf);
        register.setEmail(email);
        register.setRegistrationNumber(registrationNumber);
        register.setName(name);
        register.setPassword(password);
        register.setBirthDate(birthDate);
        register.setGender(gender);
        register.setUserType(userType);

        assertEquals(register.getUsername(), username);
        assertEquals(register.getCpf(), cpf);
        assertEquals(register.getEmail(), email);
        assertEquals(register.getRegistrationNumber(), registrationNumber);
        assertEquals(register.getName(), name);
        assertEquals(register.getPassword(), password);
        assertEquals(register.getBirthDate(), birthDate);
        assertEquals(register.getGender(), gender);
        assertEquals(register.getUserType(), userType);
    }

    @Test
    void testEquals() {
        var register1 = new RegisterRequestDto();
        var register2 = new RegisterRequestDto();

        assertEquals(register1, register2);
    }
}
