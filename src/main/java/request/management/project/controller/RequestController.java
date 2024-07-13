package request.management.project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import request.management.project.dto.RequestDto;
import request.management.project.exceptions.EmptyReasonException;
import request.management.project.exceptions.RequestAlreadyApprovedException;
import request.management.project.exceptions.RequestAlreadyUnapprovedException;
import request.management.project.model.EnumMessage;
import request.management.project.model.RequestStatus;
import request.management.project.model.DisapproveReason;
import request.management.project.response.ResponseHandler;
import request.management.project.service.ICrudService;
import request.management.project.service.RequestService;

import java.util.NoSuchElementException;

@RestController
@RequestMapping("requests")
public class RequestController extends CrudController<RequestDto> {

    @Autowired
    private RequestService requestService;

    public RequestController(ICrudService<RequestDto> service) {
        super(service);
    }

    @Override
    public ResponseEntity<?> list(@RequestParam(name = "direction", defaultValue = "ASC") Sort.Direction direction,
                                  @RequestParam(name = "property", defaultValue = "requestDate") String property){
        return super.list(direction, property);
    }

    @Override
    public ResponseEntity<?> create(RequestDto dto) {
        dto.setRequestStatus(RequestStatus.CREATED);
        dto.setDisapproveReason(null);
        return super.create(dto);
    }

    @PutMapping("/approve/{id}")
    public ResponseEntity<?> approveRequest(@PathVariable(value = "id") Long id) {
        try {
            return ResponseHandler.generateResponse(ResponseEntity.ok(requestService.approveRequest(id)), EnumMessage.PUT_MESSAGE.message());
        } catch (NoSuchElementException ignored) {
            return ResponseHandler.generateResponse(ResponseEntity.notFound().build(), EnumMessage.ENTITY_NOT_FOUND_MESSAGE.message());
        } catch (RequestAlreadyUnapprovedException | RequestAlreadyApprovedException exception) {
            return ResponseHandler.generateResponse(ResponseEntity.badRequest().build(), exception.getMessage());
        }
    }

    @PutMapping("/disapprove/{id}")
    public ResponseEntity<?> disapproveRequest(@PathVariable(value = "id") Long id, @RequestBody DisapproveReason reason) {
        try {
            return ResponseHandler.generateResponse(ResponseEntity.ok(requestService.disapproveRequest(id, reason)), EnumMessage.PUT_MESSAGE.message());
        } catch (NoSuchElementException ignored) {
            return ResponseHandler.generateResponse(ResponseEntity.notFound().build(), EnumMessage.ENTITY_NOT_FOUND_MESSAGE.message());
        } catch (RequestAlreadyUnapprovedException | RequestAlreadyApprovedException | EmptyReasonException exception) {
            return ResponseHandler.generateResponse(ResponseEntity.badRequest().build(), exception.getMessage());
        }
    }
}
