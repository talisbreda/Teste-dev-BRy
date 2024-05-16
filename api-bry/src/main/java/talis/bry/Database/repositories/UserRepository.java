package talis.bry.Database.repositories;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import talis.bry.Database.Classes.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>{
    @Transactional
    Optional<User> findByCpf(String cpf);
}
