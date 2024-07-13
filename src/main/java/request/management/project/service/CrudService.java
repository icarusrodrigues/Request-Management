package request.management.project.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import request.management.project.dto.BaseDto;
import request.management.project.mapper.GenericMapper;
import request.management.project.model.BaseEntity;
import request.management.project.repository.IRepository;

import java.util.List;

@AllArgsConstructor
public class CrudService<T extends BaseDto<Long>, E extends BaseEntity<Long>> implements ICrudService<T> {

    protected GenericMapper<T, E> mapper;
    protected IRepository<E, Long> repository;

    @Override
    public T find(Long id) {
        return repository.findById(id).map(mapper::toDto).orElseThrow();
    }

    @Override
    public List<T> findAll(Sort.Direction direction, String property) {
        return repository.findAll(Sort.by(direction, property)).stream().map(mapper::toDto).toList();
    }

    @Override
    public T create(T dto) {
        return mapper.toDto(repository.save(mapper.toEntity(dto)));
    }

    @Override
    public T update(Long id, T dto) {
        find(id);
        dto.setId(id);
        return mapper.toDto(repository.save(mapper.toEntity(dto)));
    }

    @Override
    public void delete(Long id) {
        find(id);
        repository.deleteById(id);
    }
}
