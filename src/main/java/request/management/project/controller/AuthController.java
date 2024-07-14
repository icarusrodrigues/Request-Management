package request.management.project.controller;

import br.com.caelum.stella.validation.InvalidStateException;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import request.management.project.dto.UserDto;
import request.management.project.model.EnumMessage;
import request.management.project.model.UserType;
import request.management.project.model.auth.LoginRequestDto;
import request.management.project.model.auth.RegisterRequestDto;
import request.management.project.response.ResponseHandler;
import request.management.project.security.jwt.JwtUtils;
import request.management.project.security.services.UserDetailsImpl;
import request.management.project.service.UserService;

import java.util.HashMap;
import java.util.NoSuchElementException;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    private static final String CPF_REGEX = "^(\\d{11})$";
    private static final String CPF_WITH_SYMBOLS_REGEX = "^(\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2})$";
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDto loginDto) {
        try {
            UserDto foundUser;

            if (loginDto.getAuth().matches(CPF_REGEX)) {
                foundUser = userService.findByCpf(formatCpf(loginDto.getAuth()));
            } else if (loginDto.getAuth().matches(CPF_WITH_SYMBOLS_REGEX)) {
                foundUser = userService.findByCpf(loginDto.getAuth());
            } else if (loginDto.getAuth().matches(EMAIL_REGEX)) {
                foundUser = userService.findByEmail(loginDto.getAuth());
            } else {
                foundUser = userService.findByUsername(loginDto.getAuth());
            }

            if (passwordEncoder.matches(loginDto.getPassword(), foundUser.getPassword())){
                var userDetails = UserDetailsImpl.build(userService.getUserEntityFindByUsername(foundUser.getUsername()));

                Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                SecurityContextHolder
                        .getContext()
                        .setAuthentication(authentication);

                var token = jwtUtils.generateJwtToken(authentication);

                var responseMap = new HashMap<String, String>();
                responseMap.put("username", foundUser.getUsername());
                responseMap.put("CPF", foundUser.getCpf());
                responseMap.put("email", foundUser.getEmail());
                responseMap.put("name", foundUser.getName());
                responseMap.put("userType", foundUser.getUserType().toString());
                responseMap.put("token", token);

                return ResponseEntity.ok(responseMap);
            } else {
                return ResponseHandler.generateResponse(ResponseEntity.badRequest().build(), "User or password doesn't match!");
            }

        } catch (NoSuchElementException exception) {
            if (loginDto.getAuth().matches(CPF_REGEX) || loginDto.getAuth().matches(CPF_WITH_SYMBOLS_REGEX)) {
                return ResponseHandler.generateResponse(ResponseEntity.badRequest().build(), "User not found with the passed CPF");
            } else if (loginDto.getAuth().matches(EMAIL_REGEX)) {
                return ResponseHandler.generateResponse(ResponseEntity.badRequest().build(), "User not found with the passed email");
            } else {
                return ResponseHandler.generateResponse(ResponseEntity.badRequest().build(), "User not found with the passed username");
            }
        } catch (InvalidStateException exception) {
            return ResponseHandler.generateResponse(ResponseEntity.badRequest().build(), EnumMessage.INVALID_CPF.message());
        }
    }

    @PostMapping("/sign-up")
    public ResponseEntity<?> register(@RequestBody RegisterRequestDto registerDto) {
        try {
            UserDto userDto = new UserDto();

            userDto.setUsername(registerDto.getUsername());
            userDto.setName(registerDto.getName());
            userDto.setRegistrationNumber(registerDto.getRegistrationNumber());
            userDto.setPassword(passwordEncoder.encode(registerDto.getPassword()));
            userDto.setBirthDate(registerDto.getBirthDate());
            userDto.setGender(registerDto.getGender());

            if (registerDto.getCpf().matches(CPF_REGEX)){
                userDto.setCpf(formatCpf(registerDto.getCpf()));
            } else if (registerDto.getCpf().matches(CPF_WITH_SYMBOLS_REGEX)){
                userDto.setCpf(registerDto.getCpf());
            } else {
                return ResponseHandler.generateResponse(ResponseEntity.badRequest().build(), EnumMessage.INVALID_CPF.message());
            }

            if (registerDto.getEmail().matches(EMAIL_REGEX)) {
                userDto.setEmail(registerDto.getEmail());
            } else {
                return ResponseHandler.generateResponse(ResponseEntity.badRequest().build(), "Invalid email!");
            }

            if (registerDto.getUserType().equals(UserType.ADMIN)){
                return ResponseHandler.generateResponse(ResponseEntity.badRequest().build(), "You can't create users of type ADMIN!");
            } else {
                userDto.setUserType(registerDto.getUserType());
            }

            return ResponseHandler.generateResponse(ResponseEntity.ok(userService.create(userDto)), EnumMessage.POST_MESSAGE.message());

        } catch (DataIntegrityViolationException exception) {
            if (exception.getMessage().contains("unique_cpf")){
                return ResponseHandler.generateResponse(ResponseEntity.badRequest().build(), "CPF already in use!");
            } else if (exception.getMessage().contains("unique_email")) {
                return ResponseHandler.generateResponse(ResponseEntity.badRequest().build(), "Email already in use!");
            } else {
                return ResponseHandler.generateResponse(ResponseEntity.badRequest().build(), "Username already in use!");
            }
        } catch (InvalidStateException exception) {
            return ResponseHandler.generateResponse(ResponseEntity.badRequest().build(), EnumMessage.INVALID_CPF.message());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        return ResponseEntity.noContent().build();
    }

    private String formatCpf(String cpf) {
        return cpf.substring(0, 3) + "." +
                cpf.substring(3, 6) + "." +
                cpf.substring(6, 9) + "-" +
                cpf.substring(9, 11);
    }
}
