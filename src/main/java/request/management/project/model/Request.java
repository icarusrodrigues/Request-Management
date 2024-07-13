package request.management.project.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

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

    @ManyToOne
    private User owner;
}
