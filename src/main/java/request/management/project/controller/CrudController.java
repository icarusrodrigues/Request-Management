package request.management.project.controller;

import jakarta.validation.ConstraintViolationException;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.annotation.*;
import request.management.project.dto.BaseDto;
import request.management.project.model.EnumMessage;
import request.management.project.response.ResponseHandler;
import request.management.project.service.ICrudService;

import java.util.NoSuchElementException;

@AllArgsConstructor
public abstract class CrudController <T extends BaseDto<Long>> {

    protected ICrudService<T> service;

    @GetMapping(value = "{id}")
    public ResponseEntity<?> getById(@PathVariable("id") Long id){
        try {
            return ResponseHandler.generateResponse(ResponseEntity.ok(service.find(id)), EnumMessage.GET_MESSAGE.message());
        } catch (NoSuchElementException ignored) {
            return ResponseHandler.generateResponse(ResponseEntity.notFound().build(), EnumMessage.ENTITY_NOT_FOUND_MESSAGE.message());
        }
    }

    @GetMapping
    public ResponseEntity<?> list(@RequestParam(name = "direction", defaultValue = "ASC") Sort.Direction direction,
                                  @RequestParam(name = "property", defaultValue = "id") String property) {
        try {
            return ResponseHandler.generateResponse(ResponseEntity.ok(service.findAll(direction, property)), EnumMessage.GET_MESSAGE.message());
        } catch (NoSuchElementException ignored) {
            return ResponseHandler.generateResponse(ResponseEntity.notFound().build(), EnumMessage.PROPERTY_NOT_FOUND_MESSAGE.message());
        }
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody T dto) {
        try {
            return ResponseHandler.generateResponse(ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(service.create(dto)), EnumMessage.POST_MESSAGE.message());
        } catch (ConstraintViolationException ignored) {
            return ResponseHandler.generateResponse(ResponseEntity.badRequest().build(), EnumMessage.CONSTRAINT_VIOLATION_MESSAGE.message());
        }
    }

    @PutMapping(value = "{id}")
    public ResponseEntity<?> update(@PathVariable("id") Long id, @RequestBody T dto) {
        try {
            return ResponseHandler.generateResponse(ResponseEntity.ok(service.update(id, dto)), EnumMessage.PUT_MESSAGE.message());
        } catch (NoSuchElementException ignored) {
            return ResponseHandler.generateResponse(ResponseEntity.notFound().build(), EnumMessage.ENTITY_NOT_FOUND_MESSAGE.message());
        } catch (TransactionSystemException ignored) {
            return ResponseHandler.generateResponse(ResponseEntity.badRequest().build(), EnumMessage.CONSTRAINT_VIOLATION_MESSAGE.message());
        }
    }

    @DeleteMapping(value = "{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Long id) {
        try {
            service.delete(id);
            return ResponseHandler.generateResponse(ResponseEntity.noContent().build(), EnumMessage.DELETE_MESSAGE.message());
        } catch (NoSuchElementException ignored) {
            return ResponseHandler.generateResponse(ResponseEntity.notFound().build(), EnumMessage.ENTITY_NOT_FOUND_MESSAGE.message());

        } catch (DataIntegrityViolationException ignored){
            return ResponseHandler.generateResponse(ResponseEntity.badRequest().build(), "The user is a team leader, change the leader(s) of that team(s) first.");
        }
    }
}
