package request.management.project.controller;

import br.com.caelum.stella.validation.InvalidStateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import request.management.project.dto.UserDto;
import request.management.project.model.EnumMessage;
import request.management.project.model.UserType;
import request.management.project.response.ResponseHandler;
import request.management.project.security.services.UserDetailsImpl;
import request.management.project.service.ICrudService;
import request.management.project.service.UserService;

@RestController
@RequestMapping("users")
public class UserController extends CrudController<UserDto> {

    @Autowired
    private UserService userService;

    public UserController(ICrudService<UserDto> service, UserService userService) {
        super(service);
        this.userService = userService;
    }

    @Override
    @PreAuthorize("hasAnyAuthority('ADMIN', 'TEACHER', 'TECHNICIAN')")
    public ResponseEntity<?> getById(@PathVariable("id") Long id) {
        return super.getById(id);
    }

    @Override
    @PreAuthorize("hasAnyAuthority('ADMIN', 'TEACHER', 'TECHNICIAN')")
    public ResponseEntity<?> list(@RequestParam(name = "direction", defaultValue = "ASC") Sort.Direction direction,
                                  @RequestParam(name = "property", defaultValue = "id") String property) {
        return super.list(direction, property);
    }

    @Override
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> create(@RequestBody UserDto dto){
        try {
            return super.create(dto);
        } catch (InvalidStateException exception) {
            return ResponseHandler.generateResponse(ResponseEntity.badRequest().build(), EnumMessage.INVALID_CPF.message());
        }
    }

    @Override
    @PreAuthorize("hasAnyAuthority('ADMIN', 'TEACHER', 'TECHNICIAN')")
    public ResponseEntity<?> update(@PathVariable("id") Long id, @RequestBody UserDto dto) {
        var user = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var loggedUser = userService.findByUsername(user.getUsername());

        if (loggedUser.getId().equals(id) || loggedUser.getUserType().equals(UserType.ADMIN)) {
            return super.update(id, dto);
        } else {
            return ResponseHandler.generateResponse(ResponseEntity.badRequest().build(), EnumMessage.DONT_HAVE_PERMISSION_MESSAGE.message());
        }
    }

    @Override
    @PreAuthorize("hasAnyAuthority('ADMIN', 'TEACHER', 'TECHNICIAN')")
    public ResponseEntity<?> delete(@PathVariable("id") Long id) {
        var user = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var loggedUser = userService.findByUsername(user.getUsername());

        if (loggedUser.getId().equals(id) || loggedUser.getUserType().equals(UserType.ADMIN)) {
            return super.delete(id);
        } else {
            return ResponseHandler.generateResponse(ResponseEntity.badRequest().build(), EnumMessage.DONT_HAVE_PERMISSION_MESSAGE.message());
        }
    }
}
