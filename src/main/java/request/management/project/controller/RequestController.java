package request.management.project.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import request.management.project.dto.RequestDto;
import request.management.project.service.ICrudService;

@RestController
@RequestMapping("requests")
public class RequestController extends CrudController<RequestDto> {

    public RequestController(ICrudService<RequestDto> service) {
        super(service);
    }

}
