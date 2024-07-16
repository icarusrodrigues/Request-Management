package request.management.project.dto;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import request.management.project.model.RequestStatus;
import request.management.project.model.RequestType;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class RequestDtoTest {
    @Test
    void noArgsConstructor() {
        Long id = 1L;
        String area = "area";
        RequestType requestType = RequestType.MASTERS_DEGREE;
        Integer workload = 0;
        Float totalCost = (float) 0;
        RequestStatus requestStatus = RequestStatus.UNAPPROVED;
        LocalDateTime requestDate = LocalDateTime.now();
        Long ownerId = 1L;
        String disapproveReason = "disapprove reason";

        var request = new RequestDto();
        request.setId(id);
        request.setArea(area);
        request.setRequestType(requestType);
        request.setWorkload(workload);
        request.setTotalCost(totalCost);
        request.setRequestStatus(requestStatus);
        request.setRequestDate(requestDate);
        request.setOwnerId(ownerId);
        request.setDisapproveReason(disapproveReason);

        assertEquals(request.getId(), id);
        assertEquals(request.getArea(), area);
        assertEquals(request.getRequestType(), requestType);
        assertEquals(request.getWorkload(), workload);
        assertEquals(request.getTotalCost(), totalCost);
        assertEquals(request.getRequestStatus(), requestStatus);
        assertEquals(request.getRequestDate(), requestDate);
        assertEquals(request.getOwnerId(), ownerId);
        assertEquals(request.getDisapproveReason(), disapproveReason);
    }

    @Test
    void testEquals() {
        var request1 = new RequestDto();
        var request2 = new RequestDto();

        assertEquals(request1, request2);
    }
}
