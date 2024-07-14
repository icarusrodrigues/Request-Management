package request.management.project.repository;

import request.management.project.model.User;

import java.util.Optional;

public interface UserRepository extends IRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByCpf(String cpf);
    Optional<User> findByEmail(String email);
}
