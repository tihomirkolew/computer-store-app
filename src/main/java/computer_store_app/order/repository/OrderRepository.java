package computer_store_app.order.repository;

import computer_store_app.order.model.CustomerOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<CustomerOrder, UUID> {

}
