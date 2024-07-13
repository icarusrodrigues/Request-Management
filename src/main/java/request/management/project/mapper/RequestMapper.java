package request.management.project.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import request.management.project.dto.RequestDto;
import request.management.project.model.Request;

@Component
public class RequestMapper extends GenericMapper<RequestDto, Request> {
    @Autowired
    private ModelMapper modelMapper;

    @Override
    public RequestDto toDto(Request entity) {
        return modelMapper.map(entity, RequestDto.class);
    }

    @Override
    public Request toEntity(RequestDto dto) {
        return modelMapper.map(dto, Request.class);
    }
}
