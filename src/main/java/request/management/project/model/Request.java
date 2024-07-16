package request.management.project.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "requests")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Request extends BaseEntity<Long> {

    @NotBlank
    private String area;

    @Enumerated(EnumType.STRING)
    private RequestType requestType;

    private Integer workload;

    private Float totalCost;

    @Enumerated(EnumType.STRING)
    private RequestStatus requestStatus;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd@HH:mm:ss")
    private LocalDateTime requestDate;

    @ManyToOne
    private User owner;

    private String disapproveReason;
}
