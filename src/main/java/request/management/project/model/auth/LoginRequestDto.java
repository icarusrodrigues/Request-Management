package request.management.project.model.auth;

import lombok.Data;

@Data
public class LoginRequestDto {
    private String auth;
    private String password;
}
