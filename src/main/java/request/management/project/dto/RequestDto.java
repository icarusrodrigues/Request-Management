package request.management.project.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import request.management.project.model.RequestType;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class RequestDto extends BaseDto<Long> {
    String area;

    RequestType requestType;

    Integer workload;

    Long totalCost;

    Long ownerId;
}
