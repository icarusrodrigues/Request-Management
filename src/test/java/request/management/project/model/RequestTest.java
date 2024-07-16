package request.management.project.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class RequestTest {
    @Test
    void testAllArgsConstructor() {
        var owner = new User();

        String area = "area";
        RequestType requestType = RequestType.MASTERS_DEGREE;
        Integer workload = 0;
        Float totalCost = (float) 0;
        RequestStatus requestStatus = RequestStatus.UNAPPROVED;
        LocalDateTime requestDate = LocalDateTime.now();
        String disapproveReason = "disapprove reason";

        var request = new Request(area, requestType, workload, totalCost, requestStatus, requestDate, owner, disapproveReason);

        Long id = 1L;
        String code = "code";

        request.setId(id);
        request.setCode(code);

        assertEquals(request.getId(), id);
        assertEquals(request.getCode(), code);
        assertEquals(request.getArea(), area);
        assertEquals(request.getRequestType(), requestType);
        assertEquals(request.getWorkload(), workload);
        assertEquals(request.getTotalCost(), totalCost);
        assertEquals(request.getRequestStatus(), requestStatus);
        assertEquals(request.getRequestDate(), requestDate);
        assertEquals(request.getOwner(), owner);
        assertEquals(request.getDisapproveReason(), disapproveReason);
    }

    @Test
    void testEquals() {
        var request1 = new Request();
        var request2 = new Request();

        assertEquals(request1, request2);
    }

    @Test
    void testToString() {
        String toString = "Request(area=null, requestType=null, workload=null, totalCost=null, requestStatus=null, requestDate=null, owner=null, disapproveReason=null)";
        var request = new Request();

        assertEquals(request.toString(), toString);
    }

    @Test
    void testPrePersist() {
        var request = new Request();
        request.prePersist();

        assertNotNull(request.getCode());
    }

    @Test
    void testPreUpdate() {
        var request = new User();
        request.preUpdate();

        assertNotNull(request.getCode());
    }
}
