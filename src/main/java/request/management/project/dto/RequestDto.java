package request.management.project.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import request.management.project.model.RequestStatus;
import request.management.project.model.RequestType;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class RequestDto extends BaseDto<Long> {
    String area;

    RequestType requestType;

    Integer workload;

    Float totalCost;

    RequestStatus requestStatus;

    LocalDateTime requestDate = LocalDateTime.now();

    Long ownerId;

    String disapproveReason;
}
