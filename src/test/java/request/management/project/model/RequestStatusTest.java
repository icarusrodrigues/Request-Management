package request.management.project.model;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class RequestStatusTest {
    @Test
    void testValues() {
        List<RequestStatus> requestStatuses = List.of(
                RequestStatus.CREATED,
                RequestStatus.APPROVED,
                RequestStatus.UNAPPROVED
        );

        assertTrue(Arrays.stream(RequestStatus.values()).toList().containsAll(requestStatuses));
    }
}
