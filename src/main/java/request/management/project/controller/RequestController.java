package request.management.project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import request.management.project.dto.RequestDto;
import request.management.project.exceptions.EmptyReasonException;
import request.management.project.exceptions.RequestAlreadyApprovedException;
import request.management.project.exceptions.RequestAlreadyUnapprovedException;
import request.management.project.model.EnumMessage;
import request.management.project.model.RequestStatus;
import request.management.project.model.DisapproveReason;
import request.management.project.model.UserType;
import request.management.project.response.ResponseHandler;
import request.management.project.security.services.UserDetailsImpl;
import request.management.project.service.ICrudService;
import request.management.project.service.RequestService;
import request.management.project.service.UserService;

import java.util.NoSuchElementException;

@RestController
@RequestMapping("requests")
public class RequestController extends CrudController<RequestDto> {

    @Autowired
    private RequestService requestService;

    @Autowired
    private UserService userService;

    public RequestController(ICrudService<RequestDto> service) {
        super(service);
    }

    @Override
    @PreAuthorize("hasAnyAuthority('ADMIN', 'TECHNICIAN')")
    public ResponseEntity<?> getById(@PathVariable("id") Long id) {
        return super.getById(id);
    }

    @Override
    @PreAuthorize("hasAnyAuthority('ADMIN', 'TECHNICIAN')")
    public ResponseEntity<?> list(@RequestParam(name = "direction", defaultValue = "ASC") Sort.Direction direction,
                                  @RequestParam(name = "property", defaultValue = "requestDate") String property){
        return super.list(direction, property);
    }

    @Override
    @PreAuthorize("hasAnyAuthority('ADMIN', 'TEACHER')")
    public ResponseEntity<?> create(RequestDto dto) {
        var user = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var loggedUser = userService.findByUsername(user.getUsername());

        dto.setRequestStatus(RequestStatus.CREATED);
        dto.setDisapproveReason(null);
        dto.setOwnerId(loggedUser.getId());

        return super.create(dto);
    }

    @Override
    @PreAuthorize("hasAnyAuthority('ADMIN', 'TEACHER')")
    public ResponseEntity<?> update(@PathVariable("id") Long id, @RequestBody RequestDto dto) {
        var user = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var loggedUser = userService.findByUsername(user.getUsername());

        try {
            var foundRequest = requestService.find(id);
            dto.setRequestDate(foundRequest.getRequestDate());
            dto.setDisapproveReason(null);
            dto.setOwnerId(foundRequest.getOwnerId());

            if (foundRequest.getRequestStatus().equals(RequestStatus.CREATED)) {
                if (loggedUser.getUserType().equals(UserType.TEACHER)) {
                    if (foundRequest.getOwnerId().equals(loggedUser.getId())) {
                        dto.setRequestStatus(foundRequest.getRequestStatus());

                        return super.update(id, dto);
                    } else {
                        return ResponseHandler.generateResponse(ResponseEntity.badRequest().build(), EnumMessage.DONT_HAVE_PERMISSION_MESSAGE.message());
                    }
                } else {
                    return super.update(id, dto);
                }
            } else {
                return ResponseHandler.generateResponse(ResponseEntity.badRequest().build(), "Request already closed");
            }
        } catch (NoSuchElementException ignored) {
            return ResponseHandler.generateResponse(ResponseEntity.notFound().build(), EnumMessage.ENTITY_NOT_FOUND_MESSAGE.message());
        }
    }

    @Override
    @PreAuthorize("hasAnyAuthority('ADMIN', 'TEACHER')")
    public ResponseEntity<?> delete(@PathVariable("id") Long id) {
        var user = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var loggedUser = userService.findByUsername(user.getUsername());

        if (loggedUser.getUserType().equals(UserType.TEACHER)) {
            try {
                var foundRequest = requestService.find(id);

                if (foundRequest.getOwnerId().equals(loggedUser.getId())) {
                    return super.delete(id);
                } else {
                    return ResponseHandler.generateResponse(ResponseEntity.badRequest().build(), EnumMessage.DONT_HAVE_PERMISSION_MESSAGE.message());
                }
            } catch (NoSuchElementException ignored) {
                return ResponseHandler.generateResponse(ResponseEntity.notFound().build(), EnumMessage.ENTITY_NOT_FOUND_MESSAGE.message());
            }
        } else {
            return super.delete(id);
        }
    }

    @PutMapping("/approve/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'TECHNICIAN')")
    public ResponseEntity<?> approveRequest(@PathVariable("id") Long id) {
        try {
            return ResponseHandler.generateResponse(ResponseEntity.ok(requestService.approveRequest(id)), EnumMessage.PUT_MESSAGE.message());
        } catch (NoSuchElementException ignored) {
            return ResponseHandler.generateResponse(ResponseEntity.notFound().build(), EnumMessage.ENTITY_NOT_FOUND_MESSAGE.message());
        } catch (RequestAlreadyUnapprovedException | RequestAlreadyApprovedException exception) {
            return ResponseHandler.generateResponse(ResponseEntity.badRequest().build(), exception.getMessage());
        }
    }

    @PutMapping("/disapprove/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'TECHNICIAN')")
    public ResponseEntity<?> disapproveRequest(@PathVariable("id") Long id,
                                               @RequestBody DisapproveReason reason) {
        try {
            return ResponseHandler.generateResponse(ResponseEntity.ok(requestService.disapproveRequest(id, reason)), EnumMessage.PUT_MESSAGE.message());
        } catch (NoSuchElementException ignored) {
            return ResponseHandler.generateResponse(ResponseEntity.notFound().build(), EnumMessage.ENTITY_NOT_FOUND_MESSAGE.message());
        } catch (RequestAlreadyUnapprovedException | RequestAlreadyApprovedException | EmptyReasonException exception) {
            return ResponseHandler.generateResponse(ResponseEntity.badRequest().build(), exception.getMessage());
        }
    }

    @GetMapping("/my-requests")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'TEACHER')")
    public ResponseEntity<?> listTasksByUser(@RequestParam(name = "direction", defaultValue = "ASC") Sort.Direction direction,
                                             @RequestParam(name = "property", defaultValue = "requestDate") String property) {
        var user = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var foundUser = userService.findByUsername(user.getUsername());

        try {
            return ResponseHandler.generateResponse(ResponseEntity.ok(requestService.listAllByOwner(foundUser, direction, property)), EnumMessage.GET_MESSAGE.message());
        } catch (PropertyReferenceException ignored) {
            return ResponseHandler.generateResponse(ResponseEntity.badRequest().build(), EnumMessage.PROPERTY_NOT_FOUND_MESSAGE.message());
        }
    }}
