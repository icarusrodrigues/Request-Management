package request.management.project.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import request.management.project.dto.UserDto;
import request.management.project.service.ICrudService;

@RestController
@RequestMapping("users")
public class UserController extends CrudController<UserDto> {

    public UserController(ICrudService<UserDto> service) {
        super(service);
    }

}
