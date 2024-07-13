package request.management.project.mapper;

import request.management.project.dto.BaseDto;
import request.management.project.model.BaseEntity;

public interface IMapper <T extends BaseDto<Long>, E extends BaseEntity<Long>>{
    T toDto(E entity);
    E toEntity(T dto);
}
