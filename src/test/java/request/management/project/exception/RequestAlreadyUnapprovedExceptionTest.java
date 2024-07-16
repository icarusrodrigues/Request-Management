package request.management.project.exception;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import request.management.project.exceptions.RequestAlreadyUnapprovedException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class RequestAlreadyUnapprovedExceptionTest {

    @Test
    void testConstructRequestAlreadyUnapprovedException() {
        var exception = new RequestAlreadyUnapprovedException();

        assertEquals(exception.getMessage(), "Request already unapproved");
    }
}
