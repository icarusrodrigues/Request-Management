package request.management.project.model;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class UserTypeTest {
    @Test
    void testValues() {
        List<UserType> userTypes = List.of(
                UserType.ADMIN,
                UserType.TEACHER,
                UserType.TECHNICIAN
        );

        assertTrue(Arrays.stream(UserType.values()).toList().containsAll(userTypes));
    }
}
