package request.management.project.service;

import br.com.caelum.stella.validation.CPFValidator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Sort;
import request.management.project.dto.RequestDto;
import request.management.project.dto.UserDto;
import request.management.project.mapper.GenericMapper;
import request.management.project.mapper.UserMapper;
import request.management.project.model.Gender;
import request.management.project.model.User;
import request.management.project.model.UserType;
import request.management.project.repository.UserRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@SpringBootTest
public class UserServiceTest {
    @Autowired
    private UserService service;

    @Autowired
    private GenericMapper<UserDto, User> mapper;

    @MockBean
    private UserRepository repository;

    @MockBean
    private CPFValidator cpfValidator;

    @Test
    void testFind() {
        var userDto = new UserDto();
        userDto.setId(1L);

        when(repository.findById(1L)).thenReturn(Optional.ofNullable(mapper.toEntity(userDto)));

        var foundUser = service.find(1L);

        assertEquals(foundUser, userDto);
        assertEquals(foundUser.getId(), userDto.getId());
    }

    @Test
    void testFindAll() {
        var userDto = new UserDto();
        userDto.setId(1L);

        var direction = Sort.Direction.ASC;
        String property = "id";

        when(repository.findAll(Sort.by(direction, property))).thenReturn(List.of(mapper.toEntity(userDto)));

        var foundUsers = service.findAll(direction, property);

        assertEquals(foundUsers.get(0), userDto);
        assertEquals(foundUsers.get(0).getId(), userDto.getId());
    }

    @Test
    void testCreate() {
        Long id = 1L;
        String cpf = "000.000.000-00";

        var userDto = new UserDto();
        userDto.setId(id);
        userDto.setCpf(cpf);

        doNothing().when(cpfValidator).assertValid(cpf);
        when(repository.save(mapper.toEntity(userDto))).thenReturn(mapper.toEntity(userDto));

        var savedUser = service.create(userDto);

        assertEquals(savedUser, userDto);
        assertEquals(savedUser.getId(), userDto.getId());
        assertEquals(savedUser.getCpf(), userDto.getCpf());
    }

    @Test
    void testUpdate() {
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

        var userDto = new UserDto();
        userDto.setId(id);
        userDto.setUsername(username);
        userDto.setCpf(cpf);
        userDto.setEmail(email);
        userDto.setRegistrationNumber(registrationNumber);
        userDto.setName(name);
        userDto.setPassword(password);
        userDto.setBirthDate(birthDate);
        userDto.setGender(gender);
        userDto.setUserType(userType);
        userDto.setRequests(requests);

        when(repository.findById(id)).thenReturn(Optional.of(mapper.toEntity(userDto)));
        when(repository.save(mapper.toEntity(userDto))).thenReturn(mapper.toEntity(userDto));

        var userToUpdate = new UserDto();
        userToUpdate.setRequests(null);

        var updatedUser = service.update(id, userToUpdate);

        assertEquals(updatedUser, userDto);
        assertEquals(updatedUser.getId(), userDto.getId());
        assertEquals(updatedUser.getCpf(), userDto.getCpf());
        assertEquals(updatedUser.getEmail(), userDto.getEmail());
        assertEquals(updatedUser.getRegistrationNumber(), userDto.getRegistrationNumber());
        assertEquals(updatedUser.getName(), userDto.getName());
        assertEquals(updatedUser.getPassword(), userDto.getPassword());
        assertEquals(updatedUser.getBirthDate(), userDto.getBirthDate());
        assertEquals(updatedUser.getGender(), userDto.getGender());
        assertEquals(updatedUser.getUserType(), userDto.getUserType());
        assertEquals(updatedUser.getRequests(), userDto.getRequests());
    }

    @Test
    void testDelete() {
        var userDto = new UserDto();
        userDto.setId(1L);

        when(repository.findById(1L)).thenReturn(Optional.of(mapper.toEntity(userDto)));
        doNothing().when(repository).deleteById(1L);

        service.delete(1L);
    }

    @Test
    void testFindByUsername() {
        String username = "username";

        var userDto = new UserDto();
        userDto.setUsername(username);

        when(repository.findByUsername(username)).thenReturn(Optional.of(mapper.toEntity(userDto)));

        var foundUser = service.findByUsername(username);

        assertEquals(foundUser, userDto);
        assertEquals(foundUser.getUsername(), userDto.getUsername());
    }

    @Test
    void testFindByCpf() {
        String cpf = "000.000.000-00";

        var userDto = new UserDto();
        userDto.setCpf(cpf);

        when(repository.findByCpf(cpf)).thenReturn(Optional.of(mapper.toEntity(userDto)));

        var foundUser = service.findByCpf(cpf);

        assertEquals(foundUser, userDto);
        assertEquals(foundUser.getCpf(), userDto.getCpf());
    }

    @Test
    void testFindByEmail() {
        String email = "email@email.com";

        var userDto = new UserDto();
        userDto.setEmail(email);

        when(repository.findByEmail(email)).thenReturn(Optional.of(mapper.toEntity(userDto)));

        var foundUser = service.findByEmail(email);

        assertEquals(foundUser, userDto);
        assertEquals(foundUser.getEmail(), userDto.getEmail());
    }

    @Test
    void testGetUserEntityFindByUsername() {
        String username = "username";

        var user = new User();
        user.setUsername(username);

        when(repository.findByUsername(username)).thenReturn(Optional.of(user));

        var foundUser = service.getUserEntityFindByUsername(username);

        assertEquals(foundUser, user);
        assertEquals(foundUser.getUsername(), user.getUsername());
    }
}
