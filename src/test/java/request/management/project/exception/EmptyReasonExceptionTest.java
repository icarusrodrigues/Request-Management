package request.management.project.exception;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import request.management.project.exceptions.EmptyReasonException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class EmptyReasonExceptionTest {

    @Test
    void testConstructEmptyReasonException() {
        var exception = new EmptyReasonException();

        assertEquals(exception.getMessage(), "Please, inform your reason to disapproving the request.");
    }
}
