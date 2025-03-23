package computer_store_app.client.repository;

import computer_store_app.client.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ClientRepository extends JpaRepository<Client, UUID> {


    Optional<Client> findByUsername(String username);

    Optional<Client> findByEmail(String email);
}
