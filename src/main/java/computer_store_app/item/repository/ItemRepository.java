package computer_store_app.item.repository;

import computer_store_app.item.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ItemRepository extends JpaRepository<Item, UUID> {
    List<Item> findAllBySoldFalseAndAuthorizedTrueAndArchivedFalseOrderByAddedOnDesc();

    List<Item> getItemsByOwnerId(UUID userId);

    List<Item> findAllByArchivedFalseOrderByAuthorized();
}
