package request.management.project.security.services;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import request.management.project.model.User;
import request.management.project.model.UserType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class UserDetailsImplTest {

    @Test
    void testAllArgsConstructor() {
        Long id = 1L;
        String username = "username";
        String email = "email@email.com";
        String cpf = "cpf";
        String password = "password";

        var user = new User();
        user.setId(id);
        user.setUsername(username);
        user.setEmail(email);
        user.setCpf(cpf);
        user.setPassword(password);
        user.setUserType(UserType.ADMIN);

        var userDetails = UserDetailsImpl.build(user);

        assertEquals(userDetails.getAuthorities().stream().toList().get(0).toString(), UserType.ADMIN.name());
        assertEquals(userDetails.getPassword(), password);
        assertEquals(userDetails.getUsername(), username);
        assertTrue(userDetails.isAccountNonExpired());
        assertTrue(userDetails.isAccountNonLocked());
        assertTrue(userDetails.isCredentialsNonExpired());
        assertTrue(userDetails.isEnabled());
    }
}
