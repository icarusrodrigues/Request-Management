package request.management.project.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import request.management.project.dto.RequestDto;
import request.management.project.mapper.GenericMapper;
import request.management.project.model.Request;
import request.management.project.repository.IRepository;

@Service
public class RequestService extends CrudService<RequestDto, Request> {

    @Autowired
    public RequestService(GenericMapper<RequestDto, Request> mapper, IRepository<Request, Long> repository) {
        super(mapper, repository);
    }

}
