package request.management.project.dto.auth;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class LoginRequestDtoTest {
    @Test
    void testRequiredArgsConstructor() {
        String auth = "auth";
        String password = "password";

        var login = new LoginRequestDto();
        login.setAuth(auth);
        login.setPassword(password);

        assertEquals(login.getAuth(), auth);
        assertEquals(login.getPassword(), password);
    }

    @Test
    void testEquals() {
        var login1 = new LoginRequestDto();
        var login2 = new LoginRequestDto();

        assertEquals(login1, login2);
    }
}
