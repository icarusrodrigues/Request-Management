package request.management.project.mapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import request.management.project.dto.RequestDto;
import request.management.project.dto.UserDto;
import request.management.project.model.Request;
import request.management.project.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class RequestMapperTest {

    @Autowired
    private RequestMapper mapper;

    @Test
    void testToDto() {
        Long id = 1L;
        String area = "area";

        var request = new Request();
        request.setId(id);
        request.setArea(area);

        var requestDto = mapper.toDto(request);

        assertEquals(requestDto.getId(), request.getId());
        assertEquals(requestDto.getArea(), request.getArea());
    }

    @Test
    void testToEntity() {
        Long id = 1L;
        String username = "username";

        var requestDto = new RequestDto();
        requestDto.setId(id);
        requestDto.setArea(username);

        var request = mapper.toEntity(requestDto);

        assertEquals(request.getId(), requestDto.getId());
        assertEquals(request.getArea(), requestDto.getArea());
    }
}
