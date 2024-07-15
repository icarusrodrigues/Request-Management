package request.management.project.repository;

import org.springframework.data.domain.Sort;
import request.management.project.model.Request;
import request.management.project.model.User;

import java.util.List;

public interface RequestRepository extends IRepository<Request, Long> {
    List<Request> findAllByOwner(User owner, Sort sort);
}
