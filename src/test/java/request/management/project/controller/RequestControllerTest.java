package request.management.project.controller;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.data.domain.Sort;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.data.util.TypeInformation;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.TransactionSystemException;
import request.management.project.dto.RequestDto;
import request.management.project.dto.UserDto;
import request.management.project.exceptions.EmptyReasonException;
import request.management.project.exceptions.RequestAlreadyApprovedException;
import request.management.project.exceptions.RequestAlreadyUnapprovedException;
import request.management.project.mapper.GenericMapper;
import request.management.project.model.*;
import request.management.project.security.jwt.JwtUtils;
import request.management.project.security.services.UserDetailsImpl;
import request.management.project.security.services.UserDetailsServiceImpl;
import request.management.project.service.RequestService;
import request.management.project.service.UserService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RequestControllerTest {

    @MockBean
    private UserDetailsServiceImpl userDetailsService;

    @MockBean
    private RequestService service;

    @MockBean
    private UserService userService;

    @Autowired
    private GenericMapper<UserDto, User> userMapper;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @LocalServerPort
    private int port;

    private User getLoggedUser() {
        var user = new User();
        user.setId(1L);
        user.setUsername("username");
        user.setPassword(passwordEncoder.encode("password"));
        user.setEmail("some@email.com");
        user.setCpf("000.000.000-00");
        user.setUserType(UserType.ADMIN);

        return user;
    }

    private UserDetailsImpl getLoggedUserDetails() {
        return UserDetailsImpl.build(getLoggedUser());
    }

    private Map<String, Object> getHeaderMap() {
        var loggedUser = getLoggedUserDetails();
        Authentication authentication = new UsernamePasswordAuthenticationToken(loggedUser, loggedUser.getPassword(), loggedUser.getAuthorities());

        var token = jwtUtils.generateJwtToken(authentication);

        Map<String, Object> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + token);

        when(userDetailsService.loadUserByUsername(loggedUser.getUsername())).thenReturn(getLoggedUserDetails());
        when(userService.findByUsername(loggedUser.getUsername())).thenReturn(userMapper.toDto(getLoggedUser()));

        return headers;
    }

    private User getLoggedUserTeacher() {
        var user = new User();
        user.setId(1L);
        user.setUsername("username");
        user.setPassword(passwordEncoder.encode("password"));
        user.setEmail("some@email.com");
        user.setCpf("000.000.000-00");
        user.setUserType(UserType.TEACHER);

        return user;
    }

    private UserDetailsImpl getLoggedUserDetailsTeacher() {
        return UserDetailsImpl.build(getLoggedUserTeacher());
    }

    private Map<String, Object> getHeaderMapTeacher() {
        var loggedUser = getLoggedUserDetailsTeacher();
        Authentication authentication = new UsernamePasswordAuthenticationToken(loggedUser, loggedUser.getPassword(), loggedUser.getAuthorities());

        var token = jwtUtils.generateJwtToken(authentication);

        Map<String, Object> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + token);

        when(userDetailsService.loadUserByUsername(loggedUser.getUsername())).thenReturn(getLoggedUserDetailsTeacher());
        when(userService.findByUsername(loggedUser.getUsername())).thenReturn(userMapper.toDto(getLoggedUserTeacher()));

        return headers;
    }

    @Test
    void getByIdShouldReturnOkResponse() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.port = port;

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

        when(service.find(id)).thenReturn(requestDto);

        given().log().all()
                .when()
                .contentType(ContentType.JSON)
                .headers(getHeaderMap())
                .get("requests/1")
                .then().log().all()
                .statusCode(200)
                .body("data.id", equalTo(id.intValue()))
                .body("data.area", equalTo(area))
                .body("data.requestType", equalTo(requestType.name()))
                .body("data.workload", equalTo(workload))
                .body("data.totalCost", equalTo(totalCost))
                .body("data.requestStatus", equalTo(requestStatus.name()))
                .body("data.ownerId", equalTo(null))
                .body("data.disapproveReason", equalTo(disapproveReason))
                .body("message", equalTo(EnumMessage.GET_MESSAGE.message()));
    }

    @Test
    void getByIdShouldReturnEntityNotResponse() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.port = port;

        doThrow(new NoSuchElementException()).when(service).find(1L);

        given().log().all()
                .when()
                .contentType(ContentType.JSON)
                .headers(getHeaderMap())
                .get("requests/1")
                .then().log().all()
                .statusCode(404)
                .body("message", equalTo(EnumMessage.ENTITY_NOT_FOUND_MESSAGE.message()));
    }

    @Test
    void listShouldReturnOkResponse() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.port = port;

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

        when(service.findAll(Sort.Direction.ASC, "requestDate")).thenReturn(List.of(requestDto));

        given().log().all()
                .when()
                .contentType(ContentType.JSON)
                .headers(getHeaderMap())
                .get("requests")
                .then().log().all()
                .statusCode(200)
                .body("data[0].id", equalTo(id.intValue()))
                .body("data[0].area", equalTo(area))
                .body("data[0].requestType", equalTo(requestType.name()))
                .body("data[0].workload", equalTo(workload))
                .body("data[0].totalCost", equalTo(totalCost))
                .body("data[0].requestStatus", equalTo(requestStatus.name()))
                .body("data[0].ownerId", equalTo(null))
                .body("data[0].disapproveReason", equalTo(disapproveReason))
                .body("message", equalTo(EnumMessage.GET_MESSAGE.message()));
    }

    @Test
    void listShouldReturnPropertyNotFoundResponse() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.port = port;

        doThrow(new PropertyReferenceException("requestDate", TypeInformation.LIST, List.of())).when(service).findAll(Sort.Direction.ASC, "requestDate");

        given().log().all()
                .when()
                .contentType(ContentType.JSON)
                .headers(getHeaderMap())
                .get("requests")
                .then().log().all()
                .statusCode(400)
                .body("message", equalTo(EnumMessage.PROPERTY_NOT_FOUND_MESSAGE.message()));
    }

    @Test
    void createShouldReturnCreatedResponse() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.port = port;

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

        when(service.create(requestDto)).thenReturn(requestDto);

        given().log().all()
                .when()
                .contentType(ContentType.JSON)
                .headers(getHeaderMap())
                .body(requestDto)
                .post("requests")
                .then().log().all()
                .statusCode(201)
                .body("message", equalTo(EnumMessage.POST_MESSAGE.message()));
    }

    @Test
    void createShouldReturnConstraintViolationResponse() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.port = port;

        doThrow(new ConstraintViolationException("", Set.of())).when(service).create(any());

        given().log().all()
                .when()
                .contentType(ContentType.JSON)
                .headers(getHeaderMap())
                .body(new RequestDto())
                .post("requests")
                .then().log().all()
                .statusCode(400)
                .body("message", equalTo(EnumMessage.CONSTRAINT_VIOLATION_MESSAGE.message()));
    }

    @Test
    void updateShouldReturnOkResponseWhenUserIsATeacher() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.port = port;

        Long id = 1L;
        String area = "area";
        RequestType requestType = RequestType.MASTERS_DEGREE;
        Integer workload = 0;
        Float totalCost = (float) 0;
        RequestStatus requestStatus = RequestStatus.CREATED;
        LocalDateTime requestDate = LocalDateTime.now();

        var requestDto = new RequestDto();
        requestDto.setId(id);
        requestDto.setArea(area);
        requestDto.setRequestType(requestType);
        requestDto.setWorkload(workload);
        requestDto.setTotalCost(totalCost);
        requestDto.setRequestStatus(requestStatus);
        requestDto.setRequestDate(requestDate);
        requestDto.setOwnerId(getLoggedUserTeacher().getId());

        when(service.find(id)).thenReturn(requestDto);
        when(service.update(id, requestDto)).thenReturn(requestDto);

        given().log().all()
                .when()
                .contentType(ContentType.JSON)
                .headers(getHeaderMapTeacher())
                .body(requestDto)
                .put("requests/1")
                .then().log().all()
                .statusCode(200)
                .body("message", equalTo(EnumMessage.PUT_MESSAGE.message()));
    }

    @Test
    void updateShouldReturnDontHavePermissionResponseWhenUserIsATeacher() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.port = port;

        Long id = 1L;
        String area = "area";
        RequestType requestType = RequestType.MASTERS_DEGREE;
        Integer workload = 0;
        Float totalCost = (float) 0;
        RequestStatus requestStatus = RequestStatus.CREATED;
        LocalDateTime requestDate = LocalDateTime.now();

        var requestDto = new RequestDto();
        requestDto.setId(id);
        requestDto.setArea(area);
        requestDto.setRequestType(requestType);
        requestDto.setWorkload(workload);
        requestDto.setTotalCost(totalCost);
        requestDto.setRequestStatus(requestStatus);
        requestDto.setRequestDate(requestDate);
        requestDto.setOwnerId(2L);

        when(service.find(id)).thenReturn(requestDto);
        when(service.update(id, requestDto)).thenReturn(requestDto);

        given().log().all()
                .when()
                .contentType(ContentType.JSON)
                .headers(getHeaderMapTeacher())
                .body(requestDto)
                .put("requests/1")
                .then().log().all()
                .statusCode(400)
                .body("message", equalTo(EnumMessage.DONT_HAVE_PERMISSION_MESSAGE.message()));
    }

    @Test
    void updateShouldReturnEntityNotFoundResponseWhenUserIsATeacher() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.port = port;

        Long id = 1L;
        String area = "area";
        RequestType requestType = RequestType.MASTERS_DEGREE;
        Integer workload = 0;
        Float totalCost = (float) 0;
        RequestStatus requestStatus = RequestStatus.CREATED;
        LocalDateTime requestDate = LocalDateTime.now();

        var requestDto = new RequestDto();
        requestDto.setId(id);
        requestDto.setArea(area);
        requestDto.setRequestType(requestType);
        requestDto.setWorkload(workload);
        requestDto.setTotalCost(totalCost);
        requestDto.setRequestStatus(requestStatus);
        requestDto.setRequestDate(requestDate);
        requestDto.setOwnerId(getLoggedUserTeacher().getId());

        when(service.find(id)).thenReturn(requestDto);
        doThrow(new NoSuchElementException()).when(service).update(id, requestDto);

        given().log().all()
                .when()
                .contentType(ContentType.JSON)
                .headers(getHeaderMapTeacher())
                .body(requestDto)
                .put("requests/1")
                .then().log().all()
                .statusCode(404)
                .body("message", equalTo(EnumMessage.ENTITY_NOT_FOUND_MESSAGE.message()));
    }

    @Test
    void updateShouldReturnConstraintViolationResponseWhenUserIsATeacher() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.port = port;

        Long id = 1L;
        String area = "area";
        RequestType requestType = RequestType.MASTERS_DEGREE;
        Integer workload = 0;
        Float totalCost = (float) 0;
        RequestStatus requestStatus = RequestStatus.CREATED;
        LocalDateTime requestDate = LocalDateTime.now();

        var requestDto = new RequestDto();
        requestDto.setId(id);
        requestDto.setArea(area);
        requestDto.setRequestType(requestType);
        requestDto.setWorkload(workload);
        requestDto.setTotalCost(totalCost);
        requestDto.setRequestStatus(requestStatus);
        requestDto.setRequestDate(requestDate);
        requestDto.setOwnerId(getLoggedUserTeacher().getId());

        when(service.find(id)).thenReturn(requestDto);
        doThrow(new TransactionSystemException("")).when(service).update(id, requestDto);

        given().log().all()
                .when()
                .contentType(ContentType.JSON)
                .headers(getHeaderMapTeacher())
                .body(requestDto)
                .put("requests/1")
                .then().log().all()
                .statusCode(400)
                .body("message", equalTo(EnumMessage.CONSTRAINT_VIOLATION_MESSAGE.message()));
    }

    @Test
    void updateShouldReturnOkResponseWhenUserIsAnAdmin() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.port = port;

        Long id = 1L;
        String area = "area";
        RequestType requestType = RequestType.MASTERS_DEGREE;
        Integer workload = 0;
        Float totalCost = (float) 0;
        RequestStatus requestStatus = RequestStatus.CREATED;
        LocalDateTime requestDate = LocalDateTime.now();

        var requestDto = new RequestDto();
        requestDto.setId(id);
        requestDto.setArea(area);
        requestDto.setRequestType(requestType);
        requestDto.setWorkload(workload);
        requestDto.setTotalCost(totalCost);
        requestDto.setRequestStatus(requestStatus);
        requestDto.setRequestDate(requestDate);

        when(service.find(id)).thenReturn(requestDto);
        when(service.update(id, requestDto)).thenReturn(requestDto);

        given().log().all()
                .when()
                .contentType(ContentType.JSON)
                .headers(getHeaderMap())
                .body(requestDto)
                .put("requests/1")
                .then().log().all()
                .statusCode(200)
                .body("message", equalTo(EnumMessage.PUT_MESSAGE.message()));
    }

    @Test
    void updateShouldReturnEntityNotFoundResponseWhenUserIsAnAdmin() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.port = port;

        Long id = 1L;
        String area = "area";
        RequestType requestType = RequestType.MASTERS_DEGREE;
        Integer workload = 0;
        Float totalCost = (float) 0;
        RequestStatus requestStatus = RequestStatus.CREATED;
        LocalDateTime requestDate = LocalDateTime.now();

        var requestDto = new RequestDto();
        requestDto.setId(id);
        requestDto.setArea(area);
        requestDto.setRequestType(requestType);
        requestDto.setWorkload(workload);
        requestDto.setTotalCost(totalCost);
        requestDto.setRequestStatus(requestStatus);
        requestDto.setRequestDate(requestDate);

        when(service.find(id)).thenReturn(requestDto);
        doThrow(new NoSuchElementException()).when(service).update(id, requestDto);

        given().log().all()
                .when()
                .contentType(ContentType.JSON)
                .headers(getHeaderMap())
                .body(requestDto)
                .put("requests/1")
                .then().log().all()
                .statusCode(404)
                .body("message", equalTo(EnumMessage.ENTITY_NOT_FOUND_MESSAGE.message()));
    }

    @Test
    void updateShouldReturnConstraintViolationResponseWhenUserIsAnAdmin() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.port = port;

        Long id = 1L;
        String area = "area";
        RequestType requestType = RequestType.MASTERS_DEGREE;
        Integer workload = 0;
        Float totalCost = (float) 0;
        RequestStatus requestStatus = RequestStatus.CREATED;
        LocalDateTime requestDate = LocalDateTime.now();

        var requestDto = new RequestDto();
        requestDto.setId(id);
        requestDto.setArea(area);
        requestDto.setRequestType(requestType);
        requestDto.setWorkload(workload);
        requestDto.setTotalCost(totalCost);
        requestDto.setRequestStatus(requestStatus);
        requestDto.setRequestDate(requestDate);

        when(service.find(id)).thenReturn(requestDto);
        doThrow(new TransactionSystemException("")).when(service).update(id, requestDto);

        given().log().all()
                .when()
                .contentType(ContentType.JSON)
                .headers(getHeaderMap())
                .body(requestDto)
                .put("requests/1")
                .then().log().all()
                .statusCode(400)
                .body("message", equalTo(EnumMessage.CONSTRAINT_VIOLATION_MESSAGE.message()));
    }

    @Test
    void updateShouldReturnRequestAlreadyClosedResponse() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.port = port;

        Long id = 1L;
        String area = "area";
        RequestType requestType = RequestType.MASTERS_DEGREE;
        Integer workload = 0;
        Float totalCost = (float) 0;
        RequestStatus requestStatus = RequestStatus.APPROVED;
        LocalDateTime requestDate = LocalDateTime.now();

        var requestDto = new RequestDto();
        requestDto.setId(id);
        requestDto.setArea(area);
        requestDto.setRequestType(requestType);
        requestDto.setWorkload(workload);
        requestDto.setTotalCost(totalCost);
        requestDto.setRequestStatus(requestStatus);
        requestDto.setRequestDate(requestDate);

        when(service.find(id)).thenReturn(requestDto);

        given().log().all()
                .when()
                .contentType(ContentType.JSON)
                .headers(getHeaderMap())
                .body(requestDto)
                .put("requests/1")
                .then().log().all()
                .statusCode(400)
                .body("message", equalTo("Request already closed"));
    }

    @Test
    void updateShouldReturnEntityNotFoundResponse() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.port = port;

        Long id = 1L;
        String area = "area";
        RequestType requestType = RequestType.MASTERS_DEGREE;
        Integer workload = 0;
        Float totalCost = (float) 0;
        RequestStatus requestStatus = RequestStatus.APPROVED;
        LocalDateTime requestDate = LocalDateTime.now();

        var requestDto = new RequestDto();
        requestDto.setId(id);
        requestDto.setArea(area);
        requestDto.setRequestType(requestType);
        requestDto.setWorkload(workload);
        requestDto.setTotalCost(totalCost);
        requestDto.setRequestStatus(requestStatus);
        requestDto.setRequestDate(requestDate);

        doThrow(new NoSuchElementException()).when(service).find(id);

        given().log().all()
                .when()
                .contentType(ContentType.JSON)
                .headers(getHeaderMap())
                .body(requestDto)
                .put("requests/1")
                .then().log().all()
                .statusCode(404)
                .body("message", equalTo(EnumMessage.ENTITY_NOT_FOUND_MESSAGE.message()));
    }

    @Test
    void deleteShouldReturnNoContentResponseWhenUserIsATeacher() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.port = port;

        var requestDto = new RequestDto();
        requestDto.setId(1L);
        requestDto.setOwnerId(getLoggedUserTeacher().getId());

        when(service.find(1L)).thenReturn(requestDto);
        doNothing().when(service).delete(1L);

        given().log().all()
                .when()
                .contentType(ContentType.JSON)
                .headers(getHeaderMapTeacher())
                .body(requestDto)
                .delete("requests/1")
                .then().log().all()
                .statusCode(204);
    }

    @Test
    void deleteShouldReturnDontHavePermissionResponseWhenUserIsATeacher() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.port = port;

        var requestDto = new RequestDto();
        requestDto.setId(1L);
        requestDto.setOwnerId(2L);

        when(service.find(1L)).thenReturn(requestDto);
        doNothing().when(service).delete(1L);

        given().log().all()
                .when()
                .contentType(ContentType.JSON)
                .headers(getHeaderMapTeacher())
                .body(requestDto)
                .delete("requests/1")
                .then().log().all()
                .statusCode(400)
                .body("message", equalTo(EnumMessage.DONT_HAVE_PERMISSION_MESSAGE.message()));
    }

    @Test
    void deleteShouldReturnEntityNotFoundResponseWhenUserIsATeacher() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.port = port;

        var requestDto = new RequestDto();
        requestDto.setId(1L);
        requestDto.setOwnerId(2L);

        doThrow(new NoSuchElementException()).when(service).find(1L);

        given().log().all()
                .when()
                .contentType(ContentType.JSON)
                .headers(getHeaderMapTeacher())
                .body(requestDto)
                .delete("requests/1")
                .then().log().all()
                .statusCode(404)
                .body("message", equalTo(EnumMessage.ENTITY_NOT_FOUND_MESSAGE.message()));
    }

    @Test
    void deleteShouldReturnNoContentResponseWhenUserIsAnAdmin() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.port = port;

        var requestDto = new RequestDto();
        requestDto.setId(1L);

        doNothing().when(service).delete(1L);

        given().log().all()
                .when()
                .contentType(ContentType.JSON)
                .headers(getHeaderMap())
                .body(requestDto)
                .delete("requests/1")
                .then().log().all()
                .statusCode(204);
    }

    @Test
    void deleteShouldReturnEntityNotFoundResponseWhenUserIsAnAdmin() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.port = port;

        var requestDto = new RequestDto();
        requestDto.setId(1L);

        doThrow(new NoSuchElementException()).when(service).delete(1L);

        given().log().all()
                .when()
                .contentType(ContentType.JSON)
                .headers(getHeaderMap())
                .body(requestDto)
                .delete("requests/1")
                .then().log().all()
                .statusCode(404)
                .body("message", equalTo(EnumMessage.ENTITY_NOT_FOUND_MESSAGE.message()));
    }

    @Test
    void approveRequestShouldReturnOkResponse() throws RequestAlreadyApprovedException, RequestAlreadyUnapprovedException {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.port = port;

        Long id = 1L;
        String area = "area";
        RequestType requestType = RequestType.MASTERS_DEGREE;
        Integer workload = 0;
        Float totalCost = (float) 0;
        RequestStatus requestStatus = RequestStatus.APPROVED;
        LocalDateTime requestDate = LocalDateTime.now();

        var requestDto = new RequestDto();
        requestDto.setId(id);
        requestDto.setArea(area);
        requestDto.setRequestType(requestType);
        requestDto.setWorkload(workload);
        requestDto.setTotalCost(totalCost);
        requestDto.setRequestStatus(requestStatus);
        requestDto.setRequestDate(requestDate);
        requestDto.setOwnerId(getLoggedUserTeacher().getId());

        when(service.approveRequest(id)).thenReturn(requestDto);

        given().log().all()
                .when()
                .contentType(ContentType.JSON)
                .headers(getHeaderMap())
                .put("requests/approve/1")
                .then().log().all()
                .statusCode(200)
                .body("data.id", equalTo(id.intValue()))
                .body("data.area", equalTo(area))
                .body("data.requestType", equalTo(requestType.name()))
                .body("data.workload", equalTo(workload))
                .body("data.totalCost", equalTo(totalCost))
                .body("data.requestStatus", equalTo(requestStatus.name()))
                .body("data.ownerId", equalTo(1))
                .body("message", equalTo(EnumMessage.PUT_MESSAGE.message()));
    }

    @Test
    void approveRequestShouldReturnEntityNotFoundResponse() throws RequestAlreadyApprovedException, RequestAlreadyUnapprovedException {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.port = port;

        doThrow(new NoSuchElementException()).when(service).approveRequest(1L);

        given().log().all()
                .when()
                .contentType(ContentType.JSON)
                .headers(getHeaderMap())
                .put("requests/approve/1")
                .then().log().all()
                .statusCode(404)
                .body("message", equalTo(EnumMessage.ENTITY_NOT_FOUND_MESSAGE.message()));
    }

    @Test
    void approveRequestShouldReturnRequestAlreadyApprovedResponse() throws RequestAlreadyApprovedException, RequestAlreadyUnapprovedException {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.port = port;

        doThrow(new RequestAlreadyApprovedException()).when(service).approveRequest(1L);

        given().log().all()
                .when()
                .contentType(ContentType.JSON)
                .headers(getHeaderMap())
                .put("requests/approve/1")
                .then().log().all()
                .statusCode(400)
                .body("message", equalTo(new RequestAlreadyApprovedException().getMessage()));
    }

    @Test
    void approveRequestShouldReturnRequestAlreadyUnapprovedResponse() throws RequestAlreadyApprovedException, RequestAlreadyUnapprovedException {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.port = port;

        doThrow(new RequestAlreadyUnapprovedException()).when(service).approveRequest(1L);

        given().log().all()
                .when()
                .contentType(ContentType.JSON)
                .headers(getHeaderMap())
                .put("requests/approve/1")
                .then().log().all()
                .statusCode(400)
                .body("message", equalTo(new RequestAlreadyUnapprovedException().getMessage()));
    }

    @Test
    void disapproveRequestShouldReturnOkResponse() throws RequestAlreadyApprovedException, RequestAlreadyUnapprovedException, EmptyReasonException {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.port = port;

        var disapproveReason = new DisapproveReason();
        disapproveReason.setReason("reason");

        when(service.disapproveRequest(1L, disapproveReason)).thenReturn(new RequestDto());

        given().log().all()
                .when()
                .contentType(ContentType.JSON)
                .headers(getHeaderMap())
                .body(disapproveReason)
                .put("/requests/disapprove/1")
                .then().log().all()
                .statusCode(200);
    }

    @Test
    void disapproveRequestShouldReturnEntityNotFoundResponse() throws RequestAlreadyApprovedException, RequestAlreadyUnapprovedException, EmptyReasonException {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.port = port;

        var disapproveReason = new DisapproveReason("reason");

        doThrow(new NoSuchElementException()).when(service).disapproveRequest(1L, disapproveReason);

        given().log().all()
                .when()
                .contentType(ContentType.JSON)
                .headers(getHeaderMap())
                .body(disapproveReason)
                .put("/requests/disapprove/1")
                .then().log().all()
                .statusCode(404)
                .body("message", equalTo(EnumMessage.ENTITY_NOT_FOUND_MESSAGE.message()));
    }

    @Test
    void disapproveRequestShouldReturnRequestAlreadyApprovedResponse() throws RequestAlreadyApprovedException, RequestAlreadyUnapprovedException, EmptyReasonException {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.port = port;

        var disapproveReason = new DisapproveReason("reason");

        doThrow(new RequestAlreadyApprovedException()).when(service).disapproveRequest(1L, disapproveReason);

        given().log().all()
                .when()
                .contentType(ContentType.JSON)
                .headers(getHeaderMap())
                .body(disapproveReason)
                .put("/requests/disapprove/1")
                .then().log().all()
                .statusCode(400)
                .body("message", equalTo(new RequestAlreadyApprovedException().getMessage()));
    }

    @Test
    void disapproveRequestShouldReturnRequestAlreadyUnapprovedResponse() throws RequestAlreadyApprovedException, RequestAlreadyUnapprovedException, EmptyReasonException {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.port = port;

        var disapproveReason = new DisapproveReason("reason");

        doThrow(new RequestAlreadyUnapprovedException()).when(service).disapproveRequest(1L, disapproveReason);

        given().log().all()
                .when()
                .contentType(ContentType.JSON)
                .headers(getHeaderMap())
                .body(disapproveReason)
                .put("/requests/disapprove/1")
                .then().log().all()
                .statusCode(400)
                .body("message", equalTo(new RequestAlreadyUnapprovedException().getMessage()));
    }

    @Test
    void disapproveRequestShouldReturnEmptyResponse() throws RequestAlreadyApprovedException, RequestAlreadyUnapprovedException, EmptyReasonException {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.port = port;

        var disapproveReason = new DisapproveReason("");

        doThrow(new EmptyReasonException()).when(service).disapproveRequest(1L, disapproveReason);

        given().log().all()
                .when()
                .contentType(ContentType.JSON)
                .headers(getHeaderMap())
                .body(disapproveReason)
                .put("/requests/disapprove/1")
                .then().log().all()
                .statusCode(400)
                .body("message", equalTo(new EmptyReasonException().getMessage()));
    }

    @Test
    void listTasksByUserShouldReturnOkResponse() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.port = port;

        when(service.listAllByOwner(userMapper.toDto(getLoggedUserTeacher()), Sort.Direction.ASC, "requestDate")).thenReturn(List.of());

        given().log().all()
                .when()
                .contentType(ContentType.JSON)
                .headers(getHeaderMapTeacher())
                .get("requests/my-requests")
                .then().log().all()
                .statusCode(200)
                .body("message", equalTo(EnumMessage.GET_MESSAGE.message()));
    }

}
