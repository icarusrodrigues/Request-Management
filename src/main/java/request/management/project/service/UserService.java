package request.management.project.service;

import br.com.caelum.stella.validation.CPFValidator;
import br.com.caelum.stella.validation.InvalidStateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import request.management.project.dto.UserDto;
import request.management.project.mapper.GenericMapper;
import request.management.project.model.User;
import request.management.project.repository.IRepository;
import request.management.project.repository.UserRepository;

@Service
public class UserService extends CrudService<UserDto, User> {

    @Autowired
    private UserRepository repository;

    @Autowired
    private CPFValidator cpfValidator;

    @Autowired
    private GenericMapper<UserDto, User> genericMapper;

    @Autowired
    public UserService(GenericMapper<UserDto, User> mapper, IRepository<User, Long> repository) {
        super(mapper, repository);
    }

    @Override
    public UserDto create(UserDto dto) {
        cpfValidator.assertValid(dto.getCpf());
        return super.create(dto);
    }

    @Override
    public UserDto update(Long id, UserDto dto) {
        var foundUser = find(id);
        dto.setId(id);
        dto.setCpf(foundUser.getCpf());
        dto.setRegistrationNumber(foundUser.getRegistrationNumber());
        dto.setUserType(foundUser.getUserType());

        if (dto.getEmail() == null)
            dto.setEmail(foundUser.getEmail());

        if (dto.getPassword() == null)
            dto.setPassword(foundUser.getPassword());

        if (dto.getName() == null)
            dto.setName(foundUser.getName());

        if (dto.getPassword() == null)
            dto.setPassword(foundUser.getPassword());

        if (dto.getBirthDate() == null)
            dto.setBirthDate(foundUser.getBirthDate());

        if (dto.getGender() == null)
            dto.setGender(foundUser.getGender());

        if (dto.getRequests() == null)
            dto.setRequests(foundUser.getRequests());

        return mapper.toDto(repository.save(mapper.toEntity(dto)));
    }
}
