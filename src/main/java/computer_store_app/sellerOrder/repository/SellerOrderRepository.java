package computer_store_app.sellerOrder.repository;

import computer_store_app.sellerOrder.model.SellerOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SellerOrderRepository extends JpaRepository<SellerOrder, UUID> {
    List<SellerOrder> findBySellerId(UUID sellerId);
}
