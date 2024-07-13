package request.management.project.service;

import org.springframework.data.domain.Sort;
import request.management.project.dto.BaseDto;

import java.util.List;

public interface ICrudService<T extends BaseDto<Long>>{
    T find(Long id);
    List<T> findAll(Sort.Direction direction, String property);
    T create(T dto);
    T update(Long id, T dto);
    void delete(Long id);
}
