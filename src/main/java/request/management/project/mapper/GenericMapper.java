package request.management.project.mapper;

import request.management.project.dto.BaseDto;
import request.management.project.model.BaseEntity;

public abstract class GenericMapper<T extends BaseDto<Long>, E extends BaseEntity<Long>> implements IMapper<T, E> {
}
