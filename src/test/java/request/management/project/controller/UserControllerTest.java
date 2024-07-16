package request.management.project.controller;

import br.com.caelum.stella.validation.InvalidStateException;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.data.domain.Sort;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.data.util.TypeInformation;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.TransactionSystemException;
import request.management.project.dto.RequestDto;
import request.management.project.dto.UserDto;
import request.management.project.mapper.GenericMapper;
import request.management.project.model.EnumMessage;
import request.management.project.model.Gender;
import request.management.project.model.User;
import request.management.project.model.UserType;
import request.management.project.security.jwt.JwtUtils;
import request.management.project.security.services.UserDetailsImpl;
import request.management.project.security.services.UserDetailsServiceImpl;
import request.management.project.service.UserService;

import java.time.LocalDate;
import java.util.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserControllerTest {
    @MockBean
    private UserService service;

    @MockBean
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private GenericMapper<UserDto, User> mapper;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @LocalServerPort
    private int port;

    private User getLoggedUser() {
        var user = new User();
        user.setId(1L);
        user.setUsername("username");
        user.setPassword(passwordEncoder.encode("password"));
        user.setEmail("some@email.com");
        user.setCpf("000.000.000-00");
        user.setUserType(UserType.ADMIN);

        return user;
    }

    private UserDetailsImpl getLoggedUserDetails() {
        return UserDetailsImpl.build(getLoggedUser());
    }

    private Map<String, Object> getHeaderMap() {
        var loggedUser = getLoggedUserDetails();
        Authentication authentication = new UsernamePasswordAuthenticationToken(loggedUser, loggedUser.getPassword(), loggedUser.getAuthorities());

        var token = jwtUtils.generateJwtToken(authentication);

        Map<String, Object> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + token);

        when(userDetailsService.loadUserByUsername(loggedUser.getUsername())).thenReturn(getLoggedUserDetails());
        when(service.findByUsername(loggedUser.getUsername())).thenReturn(mapper.toDto(getLoggedUser()));

        return headers;
    }

    private User getLoggedUserTeacher() {
        var user = new User();
        user.setId(1L);
        user.setUsername("username");
        user.setPassword(passwordEncoder.encode("password"));
        user.setEmail("some@email.com");
        user.setCpf("000.000.000-00");
        user.setUserType(UserType.TEACHER);

        return user;
    }

    private UserDetailsImpl getLoggedUserDetailsTeacher() {
        return UserDetailsImpl.build(getLoggedUserTeacher());
    }

    private Map<String, Object> getHeaderMapTeacher() {
        var loggedUser = getLoggedUserDetailsTeacher();
        Authentication authentication = new UsernamePasswordAuthenticationToken(loggedUser, loggedUser.getPassword(), loggedUser.getAuthorities());

        var token = jwtUtils.generateJwtToken(authentication);

        Map<String, Object> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + token);

        when(userDetailsService.loadUserByUsername(loggedUser.getUsername())).thenReturn(getLoggedUserDetailsTeacher());
        when(service.findByUsername(loggedUser.getUsername())).thenReturn(mapper.toDto(getLoggedUserTeacher()));

        return headers;
    }

    @Test
    void getByIdShouldReturnOkResponse() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.port = port;

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

        when(service.find(id)).thenReturn(userDto);

        given().log().all()
                .when()
                .contentType(ContentType.JSON)
                .headers(getHeaderMap())
                .get("users/1")
                .then().log().all()
                .statusCode(200)
                .body("data.id", equalTo(id.intValue()))
                .body("data.username", equalTo(username))
                .body("data.cpf", equalTo(cpf))
                .body("data.email", equalTo(email))
                .body("data.registrationNumber", equalTo(registrationNumber))
                .body("data.name", equalTo(name))
                .body("data.birthDate", equalTo(birthDate.toString()))
                .body("data.userType", equalTo(userType.name()))
                .body("data.requests", equalTo(List.of()))
                .body("message", equalTo(EnumMessage.GET_MESSAGE.message()));
    }

    @Test
    void getByIdShouldReturnNotFoundResponse() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.port = port;

        doThrow(new NoSuchElementException()).when(service).find(1L);

        given().log().all()
                .when()
                .contentType(ContentType.JSON)
                .headers(getHeaderMap())
                .get("users/1")
                .then().log().all()
                .statusCode(404)
                .body("message", equalTo(EnumMessage.ENTITY_NOT_FOUND_MESSAGE.message()));
    }

    @Test
    void listShouldReturnOkResponse() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.port = port;

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

        when(service.findAll(Sort.Direction.ASC, "id")).thenReturn(List.of(userDto));

        given().log().all()
                .when()
                .contentType(ContentType.JSON)
                .headers(getHeaderMap())
                .get("users")
                .then().log().all()
                .statusCode(200)
                .body("data[0].id", equalTo(id.intValue()))
                .body("data[0].username", equalTo(username))
                .body("data[0].cpf", equalTo(cpf))
                .body("data[0].email", equalTo(email))
                .body("data[0].registrationNumber", equalTo(registrationNumber))
                .body("data[0].name", equalTo(name))
                .body("data[0].birthDate", equalTo(birthDate.toString()))
                .body("data[0].userType", equalTo(userType.name()))
                .body("data[0].requests", equalTo(List.of()))
                .body("message", equalTo(EnumMessage.GET_MESSAGE.message()));
    }

    @Test
    void listShouldReturnPropertyNotFoundResponse() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.port = port;

        doThrow(new PropertyReferenceException("id", TypeInformation.LIST, List.of())).when(service).findAll(Sort.Direction.ASC, "id");

        given().log().all()
                .when()
                .contentType(ContentType.JSON)
                .headers(getHeaderMap())
                .get("users")
                .then().log().all()
                .statusCode(400)
                .body("message", equalTo(EnumMessage.PROPERTY_NOT_FOUND_MESSAGE.message()));
    }

    @Test
    void createShouldReturnCreatedResponse() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.port = port;

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

        when(service.create(userDto)).thenReturn(userDto);

        given().log().all()
                .when()
                .contentType(ContentType.JSON)
                .headers(getHeaderMap())
                .body(userDto)
                .post("users")
                .then().log().all()
                .statusCode(201)
                .body("message", equalTo(EnumMessage.POST_MESSAGE.message()));
    }

    @Test
    void createShouldReturnInvalidCpfResponse() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.port = port;

        doThrow(new InvalidStateException(List.of())).when(service).create(any());

        given().log().all()
                .when()
                .contentType(ContentType.JSON)
                .headers(getHeaderMap())
                .body(new UserDto())
                .post("users")
                .then().log().all()
                .statusCode(400)
                .body("message", equalTo(EnumMessage.INVALID_CPF.message()));
    }

    @Test
    void createShouldReturnConstraintViolationResponse() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.port = port;

        doThrow(new ConstraintViolationException("", Set.of())).when(service).create(any());

        given().log().all()
                .when()
                .contentType(ContentType.JSON)
                .headers(getHeaderMap())
                .body(new UserDto())
                .post("users")
                .then().log().all()
                .statusCode(400)
                .body("message", equalTo(EnumMessage.CONSTRAINT_VIOLATION_MESSAGE.message()));
    }

    @Test
    void updateShouldReturnOkResponse() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.port = port;

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

        when(service.update(id, userDto)).thenReturn(userDto);

        given().log().all()
                .when()
                .contentType(ContentType.JSON)
                .headers(getHeaderMap())
                .body(userDto)
                .put("users/1")
                .then().log().all()
                .statusCode(200)
                .body("message", equalTo(EnumMessage.PUT_MESSAGE.message()));
    }

    @Test
    void updateShouldReturnDontHavePermissionResponse() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.port = port;

        given().log().all()
                .when()
                .contentType(ContentType.JSON)
                .headers(getHeaderMapTeacher())
                .body(new UserDto())
                .put("/users/2")
                .then().log().all()
                .statusCode(400)
                .body("message", equalTo(EnumMessage.DONT_HAVE_PERMISSION_MESSAGE.message()));
    }

    @Test
    void updateShouldReturnEntityNotFoundResponse() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.port = port;

        doThrow(new NoSuchElementException()).when(service).update(any(), any());

        given().log().all()
                .when()
                .contentType(ContentType.JSON)
                .headers(getHeaderMap())
                .body(new UserDto())
                .put("/users/1")
                .then().log().all()
                .statusCode(404)
                .body("message", equalTo(EnumMessage.ENTITY_NOT_FOUND_MESSAGE.message()));
    }

    @Test
    void updateShouldReturnConstraintViolationResponse() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.port = port;

        doThrow(new TransactionSystemException("")).when(service).update(any(), any());

        given().log().all()
                .when()
                .contentType(ContentType.JSON)
                .headers(getHeaderMap())
                .body(new UserDto())
                .put("/users/1")
                .then().log().all()
                .statusCode(400)
                .body("message", equalTo(EnumMessage.CONSTRAINT_VIOLATION_MESSAGE.message()));
    }

    @Test
    void deleteShouldReturnNoContentResponse() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.port = port;

        doNothing().when(service).delete(1L);

        given().log().all()
                .when()
                .contentType(ContentType.JSON)
                .headers(getHeaderMap())
                .delete("/users/1")
                .then().log().all()
                .statusCode(204);
    }

    @Test
    void deleteShouldReturnDontHavePermissionResponse() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.port = port;

        given().log().all()
                .when()
                .contentType(ContentType.JSON)
                .headers(getHeaderMapTeacher())
                .delete("/users/2")
                .then().log().all()
                .statusCode(400)
                .body("message", equalTo(EnumMessage.DONT_HAVE_PERMISSION_MESSAGE.message()));
    }

    @Test
    void deleteShouldReturnEntityNotFoundResponse() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.port = port;

        doThrow(new NoSuchElementException()).when(service).delete(1L);

        given().log().all()
                .when()
                .contentType(ContentType.JSON)
                .headers(getHeaderMapTeacher())
                .delete("/users/1")
                .then().log().all()
                .statusCode(404)
                .body("message", equalTo(EnumMessage.ENTITY_NOT_FOUND_MESSAGE.message()));
    }
}
