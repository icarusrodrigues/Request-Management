package request.management.project.exception;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import request.management.project.exceptions.RequestAlreadyApprovedException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class RequestAlreadyApprovedExceptionTest {

    @Test
    void testConstructRequestAlreadyApprovedException() {
        var exception = new RequestAlreadyApprovedException();

        assertEquals(exception.getMessage(), "Request already approved");
    }
}
