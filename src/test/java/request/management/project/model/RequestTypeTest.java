package request.management.project.model;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class RequestTypeTest {
    @Test
    void testValues() {
        List<RequestType> requestTypes = List.of(
                RequestType.POSTGRADUATE,
                RequestType.MASTERS_DEGREE,
                RequestType.DOCTORATE_DEGREE
        );

        assertTrue(Arrays.stream(RequestType.values()).toList().containsAll(requestTypes));
    }
}
