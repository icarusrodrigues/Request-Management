package request.management.project.controller;

import br.com.caelum.stella.validation.InvalidStateException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import request.management.project.dto.UserDto;
import request.management.project.model.EnumMessage;
import request.management.project.response.ResponseHandler;
import request.management.project.service.ICrudService;

@RestController
@RequestMapping("users")
public class UserController extends CrudController<UserDto> {

    public UserController(ICrudService<UserDto> service) {
        super(service);
    }

    @Override
    public ResponseEntity<?> create(@RequestBody UserDto dto){
        try {
            return super.create(dto);
        } catch (InvalidStateException exception) {
            return ResponseHandler.generateResponse(ResponseEntity.badRequest().build(), EnumMessage.INVALID_CPF.message());
        }
    }

}
