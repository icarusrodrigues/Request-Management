package request.management.project.model;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class DisapproveReasonTest {
    @Test
    void testAllArgsConstructor() {
        String reason = "reason";
        var disapproveReason = new DisapproveReason(reason);

        assertEquals(disapproveReason.getReason(), reason);

        String newReason = "new reason";
        disapproveReason.setReason(newReason);

        assertEquals(disapproveReason.getReason(), newReason);

        String toString = "DisapproveReason(reason=new reason)";

        assertEquals(disapproveReason.toString(), toString);
    }
}
