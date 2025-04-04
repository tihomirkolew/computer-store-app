package computer_store_app.review.repository;

import computer_store_app.review.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ReviewRepository extends JpaRepository<Review, UUID> {


    List<Review> getAllByOrderByCreatedOnDesc();
}
