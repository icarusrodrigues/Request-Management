package request.management.project.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Sort;
import request.management.project.dto.RequestDto;
import request.management.project.dto.UserDto;
import request.management.project.exceptions.EmptyReasonException;
import request.management.project.exceptions.RequestAlreadyApprovedException;
import request.management.project.exceptions.RequestAlreadyUnapprovedException;
import request.management.project.mapper.GenericMapper;
import request.management.project.model.*;
import request.management.project.repository.RequestRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@SpringBootTest
public class RequestServiceTest {
    @Autowired
    private RequestService service;

    @Autowired
    private GenericMapper<RequestDto, Request> mapper;

    @Autowired
    private GenericMapper<UserDto, User> userMapper;

    @MockBean
    private RequestRepository repository;

    @Test
    void testFind() {
        var requestDto = new RequestDto();
        requestDto.setId(1L);

        when(repository.findById(1L)).thenReturn(Optional.ofNullable(mapper.toEntity(requestDto)));

        var foundRequest = service.find(1L);

        assertEquals(foundRequest, requestDto);
        assertEquals(foundRequest.getId(), requestDto.getId());
    }

    @Test
    void testFindAll() {
        var requestDto = new RequestDto();
        requestDto.setId(1L);

        var direction = Sort.Direction.ASC;
        String property = "id";

        when(repository.findAll(Sort.by(direction, property))).thenReturn(List.of(mapper.toEntity(requestDto)));

        var foundRequests = service.findAll(direction, property);

        assertEquals(foundRequests.get(0), requestDto);
        assertEquals(foundRequests.get(0).getId(), requestDto.getId());
    }

    @Test
    void testCreate() {
        Long id = 1L;

        var requestDto = new RequestDto();
        requestDto.setId(id);

        when(repository.save(mapper.toEntity(requestDto))).thenReturn(mapper.toEntity(requestDto));

        var savedUser = service.create(requestDto);

        assertEquals(savedUser, requestDto);
        assertEquals(savedUser.getId(), requestDto.getId());
    }

    @Test
    void testUpdate() {
        Long id = 1L;
        String area = "area";
        RequestType requestType = RequestType.MASTERS_DEGREE;
        Integer workload = 0;
        Float totalCost = (float) 0;
        RequestStatus requestStatus = RequestStatus.UNAPPROVED;
        LocalDateTime requestDate = LocalDateTime.now();
        String disapproveReason = "disapprove reason";

        var requestDto = new RequestDto();
        requestDto.setId(id);
        requestDto.setArea(area);
        requestDto.setRequestType(requestType);
        requestDto.setWorkload(workload);
        requestDto.setTotalCost(totalCost);
        requestDto.setRequestStatus(requestStatus);
        requestDto.setRequestDate(requestDate);
        requestDto.setDisapproveReason(disapproveReason);

        when(repository.findById(id)).thenReturn(Optional.of(mapper.toEntity(requestDto)));
        when(repository.save(mapper.toEntity(requestDto))).thenReturn(mapper.toEntity(requestDto));

        var updatedRequest = service.update(id, new RequestDto());

        assertEquals(updatedRequest, requestDto);
        assertEquals(updatedRequest.getId(), requestDto.getId());
        assertEquals(updatedRequest.getArea(), requestDto.getArea());
        assertEquals(updatedRequest.getRequestType(), requestDto.getRequestType());
        assertEquals(updatedRequest.getWorkload(), requestDto.getWorkload());
        assertEquals(updatedRequest.getTotalCost(), requestDto.getTotalCost());
        assertEquals(updatedRequest.getRequestStatus(), requestDto.getRequestStatus());
        assertEquals(updatedRequest.getRequestDate(), requestDto.getRequestDate());
        assertEquals(updatedRequest.getDisapproveReason(), requestDto.getDisapproveReason());
    }

    @Test
    void testDelete() {
        var requestDto = new RequestDto();
        requestDto.setId(1L);

        when(repository.findById(1L)).thenReturn(Optional.of(mapper.toEntity(requestDto)));
        doNothing().when(repository).deleteById(1L);

        service.delete(1L);
    }

    @Test
    void testApproveRequest() throws RequestAlreadyApprovedException, RequestAlreadyUnapprovedException {
        Long id = 1L;

        var requestDto = new RequestDto();
        requestDto.setId(id);
        requestDto.setRequestStatus(RequestStatus.CREATED);

        var updatedRequest = new RequestDto();
        updatedRequest.setId(id);
        updatedRequest.setRequestStatus(RequestStatus.APPROVED);

        when(repository.findById(id)).thenReturn(Optional.of(mapper.toEntity(requestDto)));
        when(repository.save(mapper.toEntity(updatedRequest))).thenReturn(mapper.toEntity(updatedRequest));

        var approvedRequest = service.approveRequest(id);

        assertEquals(approvedRequest, updatedRequest);
        assertEquals(approvedRequest.getId(), updatedRequest.getId());
        assertEquals(approvedRequest.getRequestStatus(), updatedRequest.getRequestStatus());
    }

    @Test
    void approveRequestShouldThrowRequestAlreadyApprovedException() {
        Long id = 1L;

        var requestDto = new RequestDto();
        requestDto.setId(id);
        requestDto.setRequestStatus(RequestStatus.APPROVED);

        when(repository.findById(id)).thenReturn(Optional.of(mapper.toEntity(requestDto)));

        RequestAlreadyApprovedException exception = assertThrows(RequestAlreadyApprovedException.class, () -> service.approveRequest(id));

        assertEquals(exception.getMessage(), "Request already approved");
    }

    @Test
    void approveRequestShouldThrowRequestAlreadyUnapprovedException() {
        Long id = 1L;

        var requestDto = new RequestDto();
        requestDto.setId(id);
        requestDto.setRequestStatus(RequestStatus.UNAPPROVED);

        when(repository.findById(id)).thenReturn(Optional.of(mapper.toEntity(requestDto)));

        RequestAlreadyUnapprovedException exception = assertThrows(RequestAlreadyUnapprovedException.class, () -> service.approveRequest(id));

        assertEquals(exception.getMessage(), "Request already unapproved");
    }

    @Test
    void testDisapproveRequest() throws RequestAlreadyApprovedException, RequestAlreadyUnapprovedException, EmptyReasonException {
        Long id = 1L;
        var reason = new DisapproveReason("reason");

        var requestDto = new RequestDto();
        requestDto.setId(id);
        requestDto.setRequestStatus(RequestStatus.CREATED);

        var updatedRequest = new RequestDto();
        updatedRequest.setId(id);
        updatedRequest.setRequestStatus(RequestStatus.UNAPPROVED);
        updatedRequest.setDisapproveReason(reason.getReason());

        when(repository.findById(id)).thenReturn(Optional.of(mapper.toEntity(requestDto)));
        when(repository.save(mapper.toEntity(updatedRequest))).thenReturn(mapper.toEntity(updatedRequest));

        var approvedRequest = service.disapproveRequest(id, reason);

        assertEquals(approvedRequest, updatedRequest);
        assertEquals(approvedRequest.getId(), updatedRequest.getId());
        assertEquals(approvedRequest.getRequestStatus(), updatedRequest.getRequestStatus());
        assertEquals(approvedRequest.getDisapproveReason(), updatedRequest.getDisapproveReason());
    }

    @Test
    void disapproveRequestShouldThrowEmptyReasonException() {
        Long id = 1L;

        var requestDto = new RequestDto();
        requestDto.setId(id);
        requestDto.setRequestStatus(RequestStatus.CREATED);

        when(repository.findById(id)).thenReturn(Optional.of(mapper.toEntity(requestDto)));

        EmptyReasonException exception = assertThrows(EmptyReasonException.class, () -> service.disapproveRequest(id, new DisapproveReason("")));

        assertEquals(exception.getMessage(), "Please, inform your reason to disapproving the request.");
    }

    @Test
    void disapproveRequestShouldThrowRequestAlreadyApprovedException() {
        Long id = 1L;

        var requestDto = new RequestDto();
        requestDto.setId(id);
        requestDto.setRequestStatus(RequestStatus.APPROVED);

        when(repository.findById(id)).thenReturn(Optional.of(mapper.toEntity(requestDto)));

        RequestAlreadyApprovedException exception = assertThrows(RequestAlreadyApprovedException.class, () -> service.disapproveRequest(id, new DisapproveReason("reason")));

        assertEquals(exception.getMessage(), "Request already approved");
    }

    @Test
    void disapproveRequestShouldThrowRequestAlreadyUnapprovedException() {
        Long id = 1L;

        var requestDto = new RequestDto();
        requestDto.setId(id);
        requestDto.setRequestStatus(RequestStatus.UNAPPROVED);

        when(repository.findById(id)).thenReturn(Optional.of(mapper.toEntity(requestDto)));

        RequestAlreadyUnapprovedException exception = assertThrows(RequestAlreadyUnapprovedException.class, () -> service.disapproveRequest(id, new DisapproveReason("reason")));

        assertEquals(exception.getMessage(), "Request already unapproved");
    }

    @Test
    void testListAllByOwner() {
        Long id = 1L;

        var owner = new UserDto();
        owner.setId(id);

        var requestDto = new RequestDto();
        requestDto.setId(id);
        requestDto.setOwnerId(id);

        var direction = Sort.Direction.ASC;
        String property = "id";

        when(repository.findAllByOwner(userMapper.toEntity(owner), Sort.by(direction, property))).thenReturn(List.of(mapper.toEntity(requestDto)));

        var requests = service.listAllByOwner(owner, direction, property);

        assertEquals(requests.get(0), requestDto);
        assertEquals(requests.get(0).getId(), requestDto.getId());
        assertEquals(requests.get(0).getOwnerId(), requestDto.getOwnerId());
    }
}
