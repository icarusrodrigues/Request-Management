package request.management.project.dto.auth;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import request.management.project.model.Gender;
import request.management.project.model.UserType;

import java.time.LocalDate;

@Data
public class RegisterRequestDto {
    String username;

    String cpf;

    String email;

    String registrationNumber;

    String name;

    String password;

    LocalDate birthDate;

    Gender gender;

    UserType userType;
}
