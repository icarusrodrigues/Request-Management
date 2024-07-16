package request.management.project.model;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest

public class UserTest {
    @Test
    void testAllArgsConstructor() {
        String username = "username";
        String cpf = "000.000.000-00";
        String email = "email@email.com";
        String registrationNumber = "1234";
        String name = "name";
        String password = "password";
        LocalDate birthDate = LocalDate.now();
        Gender gender = Gender.NON_SPECIFICATION;
        UserType userType = UserType.ADMIN;
        List<Request> requests = new ArrayList<>();

        var user = new User(username, cpf, email, registrationNumber, name, password, birthDate, gender, userType, requests);

        Long id = 1L;
        String code = "code";

        user.setId(id);
        user.setCode(code);

        assertEquals(user.getId(), id);
        assertEquals(user.getCode(), code);
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
        var user1 = new User();
        var user2 = new User();

        assertEquals(user1, user2);
    }

    @Test
    void testToString() {
        var user = new User();
        System.out.println(user);
    }

    @Test
    void testPrePersist() {
        var user = new User();
        user.prePersist();

        assertNotNull(user.getCode());
    }

    @Test
    void testPreUpdate() {
        var user = new User();
        user.preUpdate();

        assertNotNull(user.getCode());
    }
}
