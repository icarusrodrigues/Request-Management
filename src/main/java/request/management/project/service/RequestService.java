package request.management.project.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import request.management.project.dto.RequestDto;
import request.management.project.dto.UserDto;
import request.management.project.exceptions.EmptyReasonException;
import request.management.project.exceptions.RequestAlreadyApprovedException;
import request.management.project.exceptions.RequestAlreadyUnapprovedException;
import request.management.project.mapper.GenericMapper;
import request.management.project.model.Request;
import request.management.project.model.RequestStatus;
import request.management.project.model.DisapproveReason;
import request.management.project.model.User;
import request.management.project.repository.IRepository;
import request.management.project.repository.RequestRepository;

import java.util.List;

@Service
public class RequestService extends CrudService<RequestDto, Request> {

    @Autowired
    private RequestRepository repository;

    @Autowired
    private GenericMapper<RequestDto, Request> mapper;

    @Autowired
    private GenericMapper<UserDto, User> userMapper;

    @Autowired
    public RequestService(GenericMapper<RequestDto, Request> mapper, IRepository<Request, Long> repository) {
        super(mapper, repository);
    }

    @Override
    public RequestDto update(Long id, RequestDto dto) {
        var foundRequest = find(id);
        dto.setId(id);
        dto.setRequestDate(foundRequest.getRequestDate());
        dto.setRequestStatus(foundRequest.getRequestStatus());

        if (dto.getArea() ==null)
            dto.setArea(foundRequest.getArea());

        if (dto.getRequestType() == null)
            dto.setRequestType(foundRequest.getRequestType());

        if (dto.getWorkload() == null)
            dto.setWorkload(foundRequest.getWorkload());

        if (dto.getTotalCost() == null)
            dto.setTotalCost(foundRequest.getTotalCost());

        if (dto.getOwnerId() == null)
            dto.setOwnerId(foundRequest.getOwnerId());

        return mapper.toDto(repository.save(mapper.toEntity(dto)));
    }

    @Transactional
    public RequestDto approveRequest(Long id) throws RequestAlreadyUnapprovedException, RequestAlreadyApprovedException {
        var request = find(id);

        if (request.getRequestStatus().equals(RequestStatus.UNAPPROVED)) {
            throw new RequestAlreadyUnapprovedException();
        }

        if (request.getRequestStatus().equals(RequestStatus.APPROVED)) {
            throw new RequestAlreadyApprovedException();
        }

        request.setRequestStatus(RequestStatus.APPROVED);

        return update(id, request);
    }

    @Transactional
    public RequestDto disapproveRequest(Long id, DisapproveReason reason) throws RequestAlreadyUnapprovedException, RequestAlreadyApprovedException, EmptyReasonException {
        if (reason.getReason() == null || reason.getReason().isEmpty()) {
            throw new EmptyReasonException();
        }

        var request = find(id);

        if (request.getRequestStatus().equals(RequestStatus.UNAPPROVED)) {
            throw new RequestAlreadyUnapprovedException();
        }

        if (request.getRequestStatus().equals(RequestStatus.APPROVED)) {
            throw new RequestAlreadyApprovedException();
        }

        request.setRequestStatus(RequestStatus.UNAPPROVED);
        request.setDisapproveReason(reason.getReason());

        return mapper.toDto(repository.save(mapper.toEntity(request)));
    }

    public List<RequestDto> listAllByOwner(UserDto loggedUser, Sort.Direction direction, String property) {
        return repository.findAllByOwner(userMapper.toEntity(loggedUser), Sort.by(direction, property)).stream().map(mapper::toDto).toList();
    }
}
