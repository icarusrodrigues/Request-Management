package request.management.project.dto.auth;

import lombok.Data;

@Data
public class LoginRequestDto {
    private String auth;
    private String password;
}
