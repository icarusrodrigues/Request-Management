package request.management.project.controller;

import br.com.caelum.stella.validation.CPFValidator;
import br.com.caelum.stella.validation.InvalidStateException;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import request.management.project.dto.UserDto;
import request.management.project.dto.auth.LoginRequestDto;
import request.management.project.dto.auth.RegisterRequestDto;
import request.management.project.mapper.GenericMapper;
import request.management.project.model.EnumMessage;
import request.management.project.model.Gender;
import request.management.project.model.User;
import request.management.project.model.UserType;
import request.management.project.security.jwt.JwtUtils;
import request.management.project.service.UserService;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuthControllerTest {

    @MockBean
    private UserService userService;

    @Autowired
    private GenericMapper<UserDto, User> userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    @LocalServerPort
    private int port;

    @Test
    void loginShouldReturnOkResponseWhenAuthIsCpfWithoutSymbols() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.port = port;

        String cpf = "00000000000";
        String cpfWithSymbols = "000.000.000-00";
        String username = "username";
        String name = "name";
        String password = "Some password";
        String email = "some@email.com";

        var login = new LoginRequestDto();
        login.setAuth(cpf);
        login.setPassword(password);

        var savedUser = new UserDto();
        savedUser.setCpf(cpfWithSymbols);
        savedUser.setName(name);
        savedUser.setUserType(UserType.ADMIN);
        savedUser.setEmail(email);
        savedUser.setUsername(username);
        savedUser.setPassword(passwordEncoder.encode(password));

        when(userService.findByCpf(cpfWithSymbols)).thenReturn(savedUser);
        when(userService.getUserEntityFindByUsername(savedUser.getUsername())).thenReturn(userMapper.toEntity(savedUser));

        given().log().all()
                .when()
                .contentType(ContentType.JSON)
                .body(login)
                .post("auth/login")
                .then().log().all()
                .statusCode(200)
                .body("CPF", equalTo(savedUser.getCpf()))
                .body("name", equalTo(savedUser.getName()))
                .body("userType", equalTo(savedUser.getUserType().name()))
                .body("email", equalTo(savedUser.getEmail()))
                .body("username", equalTo(savedUser.getUsername()));
    }

    @Test
    void loginShouldReturnBadRequestResponseWhenAuthIsCpfWithoutSymbols() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.port = port;

        String cpf = "00000000000";
        String cpfWithSymbols = "000.000.000-00";
        String username = "username";
        String name = "name";
        String password = "Some password";
        String email = "some@email.com";

        var login = new LoginRequestDto();
        login.setAuth(cpf);
        login.setPassword(password);

        var savedUser = new UserDto();
        savedUser.setCpf(cpfWithSymbols);
        savedUser.setName(name);
        savedUser.setUserType(UserType.ADMIN);
        savedUser.setEmail(email);
        savedUser.setUsername(username);
        savedUser.setPassword(passwordEncoder.encode(password));

        doThrow(new NoSuchElementException()).when(userService).findByCpf(cpfWithSymbols);

        given().log().all()
                .when()
                .contentType(ContentType.JSON)
                .body(login)
                .post("auth/login")
                .then().log().all()
                .statusCode(400)
                .body("message", equalTo("User not found with the passed CPF"));
    }

    @Test
    void loginShouldReturnOkResponseWhenAuthIsCpfWithSymbols() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.port = port;

        String cpfWithSymbols = "000.000.000-00";
        String username = "username";
        String name = "name";
        String password = "Some password";
        String email = "some@email.com";

        var login = new LoginRequestDto();
        login.setAuth(cpfWithSymbols);
        login.setPassword(password);

        var savedUser = new UserDto();
        savedUser.setCpf(cpfWithSymbols);
        savedUser.setName(name);
        savedUser.setUserType(UserType.ADMIN);
        savedUser.setEmail(email);
        savedUser.setUsername(username);
        savedUser.setPassword(passwordEncoder.encode(password));

        when(userService.findByCpf(cpfWithSymbols)).thenReturn(savedUser);
        when(userService.getUserEntityFindByUsername(savedUser.getUsername())).thenReturn(userMapper.toEntity(savedUser));

        given().log().all()
                .when()
                .contentType(ContentType.JSON)
                .body(login)
                .post("auth/login")
                .then().log().all()
                .statusCode(200)
                .body("CPF", equalTo(savedUser.getCpf()))
                .body("name", equalTo(savedUser.getName()))
                .body("userType", equalTo(savedUser.getUserType().name()))
                .body("email", equalTo(savedUser.getEmail()))
                .body("username", equalTo(savedUser.getUsername()));
    }

    @Test
    void loginShouldReturnBadRequestResponseWhenAuthIsInvalidCpf() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.port = port;

        String cpfWithSymbols = "000.000.000-00";
        String username = "username";
        String name = "name";
        String password = "Some password";
        String email = "some@email.com";

        var login = new LoginRequestDto();
        login.setAuth(cpfWithSymbols);
        login.setPassword(password);

        var savedUser = new UserDto();
        savedUser.setCpf(cpfWithSymbols);
        savedUser.setName(name);
        savedUser.setUserType(UserType.ADMIN);
        savedUser.setEmail(email);
        savedUser.setUsername(username);
        savedUser.setPassword(passwordEncoder.encode(password));

        doThrow(new InvalidStateException(List.of())).when(userService).findByCpf(cpfWithSymbols);

        given().log().all()
                .when()
                .contentType(ContentType.JSON)
                .body(login)
                .post("auth/login")
                .then().log().all()
                .statusCode(400)
                .body("message", equalTo(EnumMessage.INVALID_CPF.message()));
    }

    @Test
    void loginShouldReturnOkResponseWhenAuthIsEmail() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.port = port;

        String cpfWithSymbols = "000.000.000-00";
        String username = "username";
        String name = "name";
        String password = "Some password";
        String email = "some@email.com";

        var login = new LoginRequestDto();
        login.setAuth(email);
        login.setPassword(password);

        var savedUser = new UserDto();
        savedUser.setCpf(cpfWithSymbols);
        savedUser.setName(name);
        savedUser.setUserType(UserType.ADMIN);
        savedUser.setEmail(email);
        savedUser.setUsername(username);
        savedUser.setPassword(passwordEncoder.encode(password));

        when(userService.findByEmail(email)).thenReturn(savedUser);
        when(userService.getUserEntityFindByUsername(savedUser.getUsername())).thenReturn(userMapper.toEntity(savedUser));

        given().log().all()
                .when()
                .contentType(ContentType.JSON)
                .body(login)
                .post("auth/login")
                .then().log().all()
                .statusCode(200)
                .body("CPF", equalTo(savedUser.getCpf()))
                .body("name", equalTo(savedUser.getName()))
                .body("userType", equalTo(savedUser.getUserType().name()))
                .body("email", equalTo(savedUser.getEmail()))
                .body("username", equalTo(savedUser.getUsername()));
    }


    @Test
    void loginShouldReturnBadRequestResponseWhenAuthIsEmail() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.port = port;

        String cpfWithSymbols = "000.000.000-00";
        String username = "username";
        String name = "name";
        String password = "Some password";
        String email = "some@email.com";

        var login = new LoginRequestDto();
        login.setAuth(email);
        login.setPassword(password);

        var savedUser = new UserDto();
        savedUser.setCpf(cpfWithSymbols);
        savedUser.setName(name);
        savedUser.setUserType(UserType.ADMIN);
        savedUser.setEmail(email);
        savedUser.setUsername(username);
        savedUser.setPassword(passwordEncoder.encode(password));

        doThrow(new NoSuchElementException()).when(userService).findByEmail(email);

        given().log().all()
                .when()
                .contentType(ContentType.JSON)
                .body(login)
                .post("auth/login")
                .then().log().all()
                .statusCode(400)
                .body("message", equalTo("User not found with the passed email"));
    }

    @Test
    void loginShouldReturnOkResponseWhenAuthIsUsername() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.port = port;

        String cpfWithSymbols = "000.000.000-00";
        String username = "username";
        String name = "name";
        String password = "Some password";
        String email = "some@email.com";

        var login = new LoginRequestDto();
        login.setAuth(username);
        login.setPassword(password);

        var savedUser = new UserDto();
        savedUser.setCpf(cpfWithSymbols);
        savedUser.setName(name);
        savedUser.setUserType(UserType.ADMIN);
        savedUser.setEmail(email);
        savedUser.setUsername(username);
        savedUser.setPassword(passwordEncoder.encode(password));

        when(userService.findByUsername(username)).thenReturn(savedUser);
        when(userService.getUserEntityFindByUsername(savedUser.getUsername())).thenReturn(userMapper.toEntity(savedUser));

        given().log().all()
                .when()
                .contentType(ContentType.JSON)
                .body(login)
                .post("auth/login")
                .then().log().all()
                .statusCode(200)
                .body("CPF", equalTo(savedUser.getCpf()))
                .body("name", equalTo(savedUser.getName()))
                .body("userType", equalTo(savedUser.getUserType().name()))
                .body("email", equalTo(savedUser.getEmail()))
                .body("username", equalTo(savedUser.getUsername()));
    }

    @Test
    void loginShouldReturnBadRequestResponseWhenAuthIsUsername() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.port = port;

        String cpfWithSymbols = "000.000.000-00";
        String username = "username";
        String name = "name";
        String password = "Some password";
        String email = "some@email.com";

        var login = new LoginRequestDto();
        login.setAuth(username);
        login.setPassword(password);

        var savedUser = new UserDto();
        savedUser.setCpf(cpfWithSymbols);
        savedUser.setName(name);
        savedUser.setUserType(UserType.ADMIN);
        savedUser.setEmail(email);
        savedUser.setUsername(username);
        savedUser.setPassword(passwordEncoder.encode(password));

        doThrow(new NoSuchElementException()).when(userService).findByUsername(username);

        given().log().all()
                .when()
                .contentType(ContentType.JSON)
                .body(login)
                .post("auth/login")
                .then().log().all()
                .statusCode(400)
                .body("message", equalTo("User not found with the passed username"));
    }

    @Test
    void loginShouldReturnBadRequestWhenPasswordDoesntMatch() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.port = port;

        String cpfWithSymbols = "000.000.000-00";
        String username = "username";
        String name = "name";
        String password = "Some password";
        String email = "some@email.com";

        var login = new LoginRequestDto();
        login.setAuth(username);
        login.setPassword(password);

        var savedUser = new UserDto();
        savedUser.setCpf(cpfWithSymbols);
        savedUser.setName(name);
        savedUser.setUserType(UserType.ADMIN);
        savedUser.setEmail(email);
        savedUser.setUsername(username);
        savedUser.setPassword(password);

        when(userService.findByUsername(username)).thenReturn(savedUser);

        given().log().all()
                .when()
                .contentType(ContentType.JSON)
                .body(login)
                .post("auth/login")
                .then().log().all()
                .statusCode(400)
                .body("message", equalTo("User or password doesn't match!"));
    }

    @Test
    void signUpShouldReturnOkWhenCpfDontHaveWithSymbols() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.port = port;

        String username = "Some username";
        String name = "Some name";
        String registrationNumber = "1234";
        String password = "Some password";
        LocalDate birthDate = LocalDate.now();
        Gender gender = Gender.NON_SPECIFICATION;
        String email = "some@email.com";
        String cpf = "00000000000";

        var register = new RegisterRequestDto();
        register.setUsername(username);
        register.setName(name);
        register.setRegistrationNumber(registrationNumber);
        register.setPassword(password);
        register.setBirthDate(birthDate);
        register.setGender(gender);
        register.setEmail(email);
        register.setCpf(cpf);
        register.setUserType(UserType.TEACHER);

        var createdUser = new UserDto();
        createdUser.setUsername(username);
        createdUser.setName(name);
        createdUser.setRegistrationNumber(registrationNumber);
        createdUser.setPassword(password);
        createdUser.setBirthDate(birthDate);
        createdUser.setGender(gender);
        createdUser.setEmail(email);
        createdUser.setCpf(cpf);
        createdUser.setUserType(UserType.TEACHER);

        when(userService.create(createdUser)).thenReturn(createdUser);

        given().log().all()
                .when()
                .contentType(ContentType.JSON)
                .body(register)
                .post("auth/sign-up")
                .then().log().all()
                .statusCode(200);
    }

    @Test
    void signUpShouldReturnOkWhenCpfHaveWithSymbols() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.port = port;

        String username = "Some username";
        String name = "Some name";
        String registrationNumber = "1234";
        String password = "Some password";
        LocalDate birthDate = LocalDate.now();
        Gender gender = Gender.NON_SPECIFICATION;
        String email = "some@email.com";
        String cpf = "000.000.000-00";

        var register = new RegisterRequestDto();
        register.setUsername(username);
        register.setName(name);
        register.setRegistrationNumber(registrationNumber);
        register.setPassword(password);
        register.setBirthDate(birthDate);
        register.setGender(gender);
        register.setEmail(email);
        register.setCpf(cpf);
        register.setUserType(UserType.TEACHER);

        var createdUser = new UserDto();
        createdUser.setUsername(username);
        createdUser.setName(name);
        createdUser.setRegistrationNumber(registrationNumber);
        createdUser.setPassword(password);
        createdUser.setBirthDate(birthDate);
        createdUser.setGender(gender);
        createdUser.setEmail(email);
        createdUser.setCpf(cpf);
        createdUser.setUserType(UserType.TEACHER);

        when(userService.create(createdUser)).thenReturn(createdUser);

        given().log().all()
                .when()
                .contentType(ContentType.JSON)
                .body(register)
                .post("auth/sign-up")
                .then().log().all()
                .statusCode(200);
    }

    @Test
    void signUpShouldReturnBadRequestWhenCpfDoesntMatchRegex() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.port = port;

        String username = "Some username";
        String name = "Some name";
        String registrationNumber = "1234";
        String password = "Some password";
        LocalDate birthDate = LocalDate.now();
        Gender gender = Gender.NON_SPECIFICATION;
        String email = "some@email.com";
        String cpf = "0";

        var register = new RegisterRequestDto();
        register.setUsername(username);
        register.setName(name);
        register.setRegistrationNumber(registrationNumber);
        register.setPassword(password);
        register.setBirthDate(birthDate);
        register.setGender(gender);
        register.setEmail(email);
        register.setCpf(cpf);
        register.setUserType(UserType.TEACHER);

        var createdUser = new UserDto();
        createdUser.setUsername(username);
        createdUser.setName(name);
        createdUser.setRegistrationNumber(registrationNumber);
        createdUser.setPassword(password);
        createdUser.setBirthDate(birthDate);
        createdUser.setGender(gender);
        createdUser.setEmail(email);
        createdUser.setCpf(cpf);
        createdUser.setUserType(UserType.TEACHER);

        when(userService.create(createdUser)).thenReturn(createdUser);

        given().log().all()
                .when()
                .contentType(ContentType.JSON)
                .body(register)
                .post("auth/sign-up")
                .then().log().all()
                .statusCode(400)
                .body("message", equalTo(EnumMessage.INVALID_CPF.message()));
    }

    @Test
    void signUpShouldReturnBadRequestWhenEmailIsInvalid() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.port = port;

        String username = "Some username";
        String name = "Some name";
        String registrationNumber = "1234";
        String password = "Some password";
        LocalDate birthDate = LocalDate.now();
        Gender gender = Gender.NON_SPECIFICATION;
        String email = "some@email";
        String cpf = "000.000.000-00";

        var register = new RegisterRequestDto();
        register.setUsername(username);
        register.setName(name);
        register.setRegistrationNumber(registrationNumber);
        register.setPassword(password);
        register.setBirthDate(birthDate);
        register.setGender(gender);
        register.setEmail(email);
        register.setCpf(cpf);
        register.setUserType(UserType.TEACHER);

        var createdUser = new UserDto();
        createdUser.setUsername(username);
        createdUser.setName(name);
        createdUser.setRegistrationNumber(registrationNumber);
        createdUser.setPassword(password);
        createdUser.setBirthDate(birthDate);
        createdUser.setGender(gender);
        createdUser.setEmail(email);
        createdUser.setCpf(cpf);
        createdUser.setUserType(UserType.TEACHER);

        when(userService.create(createdUser)).thenReturn(createdUser);

        given().log().all()
                .when()
                .contentType(ContentType.JSON)
                .body(register)
                .post("auth/sign-up")
                .then().log().all()
                .statusCode(400)
                .body("message", equalTo("Invalid email!"));
    }

    @Test
    void signUpShouldReturnBadRequestWhenCreatingAdminUser() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.port = port;

        String username = "Some username";
        String name = "Some name";
        String registrationNumber = "1234";
        String password = "Some password";
        LocalDate birthDate = LocalDate.now();
        Gender gender = Gender.NON_SPECIFICATION;
        String email = "some@email.com";
        String cpf = "000.000.000-00";

        var register = new RegisterRequestDto();
        register.setUsername(username);
        register.setName(name);
        register.setRegistrationNumber(registrationNumber);
        register.setPassword(password);
        register.setBirthDate(birthDate);
        register.setGender(gender);
        register.setEmail(email);
        register.setCpf(cpf);
        register.setUserType(UserType.ADMIN);

        var createdUser = new UserDto();
        createdUser.setUsername(username);
        createdUser.setName(name);
        createdUser.setRegistrationNumber(registrationNumber);
        createdUser.setPassword(password);
        createdUser.setBirthDate(birthDate);
        createdUser.setGender(gender);
        createdUser.setEmail(email);
        createdUser.setCpf(cpf);
        createdUser.setUserType(UserType.ADMIN);

        when(userService.create(createdUser)).thenReturn(createdUser);

        given().log().all()
                .when()
                .contentType(ContentType.JSON)
                .body(register)
                .post("auth/sign-up")
                .then().log().all()
                .statusCode(400)
                .body("message", equalTo("You can't create users of type ADMIN!"));
    }

    @Test
    void signUpShouldReturnBadRequestWhenCpfIsInvalid() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.port = port;

        String username = "Some username";
        String name = "Some name";
        String registrationNumber = "1234";
        String password = "Some password";
        LocalDate birthDate = LocalDate.now();
        Gender gender = Gender.NON_SPECIFICATION;
        String email = "some@email.com";
        String cpf = "000.000.000-00";

        var register = new RegisterRequestDto();
        register.setUsername(username);
        register.setName(name);
        register.setRegistrationNumber(registrationNumber);
        register.setPassword(password);
        register.setBirthDate(birthDate);
        register.setGender(gender);
        register.setEmail(email);
        register.setCpf(cpf);
        register.setUserType(UserType.TEACHER);

        doThrow(new InvalidStateException(List.of())).when(userService).create(any());

        given().log().all()
                .when()
                .contentType(ContentType.JSON)
                .body(register)
                .post("auth/sign-up")
                .then().log().all()
                .statusCode(400)
                .body("message", equalTo(EnumMessage.INVALID_CPF.message()));
    }

    @Test
    void signUpShouldReturnBadRequestWhenCpfIsAlreadyInUse() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.port = port;

        String username = "Some username";
        String name = "Some name";
        String registrationNumber = "1234";
        String password = "Some password";
        LocalDate birthDate = LocalDate.now();
        Gender gender = Gender.NON_SPECIFICATION;
        String email = "some@email.com";
        String cpf = "000.000.000-00";

        var register = new RegisterRequestDto();
        register.setUsername(username);
        register.setName(name);
        register.setRegistrationNumber(registrationNumber);
        register.setPassword(password);
        register.setBirthDate(birthDate);
        register.setGender(gender);
        register.setEmail(email);
        register.setCpf(cpf);
        register.setUserType(UserType.TEACHER);

        doThrow(new DataIntegrityViolationException("unique_cpf")).when(userService).create(any());

        given().log().all()
                .when()
                .contentType(ContentType.JSON)
                .body(register)
                .post("auth/sign-up")
                .then().log().all()
                .statusCode(400)
                .body("message", equalTo("CPF already in use!"));
    }


    @Test
    void signUpShouldReturnBadRequestWhenEmailIsAlreadyInUse() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.port = port;

        String username = "Some username";
        String name = "Some name";
        String registrationNumber = "1234";
        String password = "Some password";
        LocalDate birthDate = LocalDate.now();
        Gender gender = Gender.NON_SPECIFICATION;
        String email = "some@email.com";
        String cpf = "000.000.000-00";

        var register = new RegisterRequestDto();
        register.setUsername(username);
        register.setName(name);
        register.setRegistrationNumber(registrationNumber);
        register.setPassword(password);
        register.setBirthDate(birthDate);
        register.setGender(gender);
        register.setEmail(email);
        register.setCpf(cpf);
        register.setUserType(UserType.TEACHER);

        doThrow(new DataIntegrityViolationException("unique_email")).when(userService).create(any());

        given().log().all()
                .when()
                .contentType(ContentType.JSON)
                .body(register)
                .post("auth/sign-up")
                .then().log().all()
                .statusCode(400)
                .body("message", equalTo("Email already in use!"));
    }


    @Test
    void signUpShouldReturnBadRequestWhenUsernameIsAlreadyInUse() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.port = port;

        String username = "Some username";
        String name = "Some name";
        String registrationNumber = "1234";
        String password = "Some password";
        LocalDate birthDate = LocalDate.now();
        Gender gender = Gender.NON_SPECIFICATION;
        String email = "some@email.com";
        String cpf = "000.000.000-00";

        var register = new RegisterRequestDto();
        register.setUsername(username);
        register.setName(name);
        register.setRegistrationNumber(registrationNumber);
        register.setPassword(password);
        register.setBirthDate(birthDate);
        register.setGender(gender);
        register.setEmail(email);
        register.setCpf(cpf);
        register.setUserType(UserType.TEACHER);

        doThrow(new DataIntegrityViolationException("")).when(userService).create(any());

        given().log().all()
                .when()
                .contentType(ContentType.JSON)
                .body(register)
                .post("auth/sign-up")
                .then().log().all()
                .statusCode(400)
                .body("message", equalTo("Username already in use!"));
    }

    @Test
    void logoutShouldReturnNoContent() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.port = port;

        given().log().all()
                .when()
                .contentType(ContentType.JSON)
                .post("auth/logout")
                .then().log().all()
                .statusCode(204);
    }
}
