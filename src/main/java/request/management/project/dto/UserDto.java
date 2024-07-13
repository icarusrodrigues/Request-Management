package request.management.project.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import request.management.project.model.Gender;
import request.management.project.model.UserType;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class UserDto extends BaseDto<Long>{
    String cpf;

    String email;

    String registrationNumber;

    String name;

//    @JsonIgnore
    String password;

    LocalDate birthDate;

    Gender gender;

    UserType userType;

    @EqualsAndHashCode.Exclude
    List<RequestDto> requests = new ArrayList<>();
}
