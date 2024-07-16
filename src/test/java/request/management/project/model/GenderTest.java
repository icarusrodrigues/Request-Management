package request.management.project.model;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class GenderTest {
    @Test
    void testAllValues() {
        List<Gender> genders = List.of(
                Gender.MALE,
                Gender.FEMALE,
                Gender.NON_SPECIFICATION
        );

        assertTrue(Arrays.stream(Gender.values()).toList().containsAll(genders));
    }
}
