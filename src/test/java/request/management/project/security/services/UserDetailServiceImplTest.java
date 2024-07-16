package request.management.project.security.services;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import request.management.project.model.User;
import request.management.project.model.UserType;
import request.management.project.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@SpringBootTest
public class UserDetailServiceImplTest {
    @MockBean
    private UserRepository repository;

    @Autowired
    private UserDetailsServiceImpl service;

    @Test
    void testLoadUserByUsername() {
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

        when(repository.findByUsername(username)).thenReturn(Optional.of(user));

        var loadedUser = service.loadUserByUsername(username);

        assertEquals(loadedUser.getUsername(), username);
        assertEquals(loadedUser.getPassword(), password);
        assertEquals(loadedUser.getAuthorities().stream().toList().get(0).toString(), UserType.ADMIN.name());

    }
}
