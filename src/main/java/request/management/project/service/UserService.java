package request.management.project.service;

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
    public UserService(GenericMapper<UserDto, User> mapper, IRepository<User, Long> repository) {
        super(mapper, repository);
    }

}
