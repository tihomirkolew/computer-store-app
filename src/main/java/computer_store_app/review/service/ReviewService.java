package computer_store_app.review.service;

import computer_store_app.review.model.Review;
import computer_store_app.review.repository.ReviewRepository;
import computer_store_app.client.model.Client;
import computer_store_app.web.dto.NewReviewRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ReviewService {


    private final ReviewRepository reviewRepository;

    @Autowired
    public ReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    public void addNewReview(@Valid NewReviewRequest newReviewRequest, Client client) {

        Review newReview = Review.builder()
                .addedBy(client)
                .rating(newReviewRequest.getRating())
                .comment(newReviewRequest.getComment())
                .createdOn(LocalDateTime.now())
                .build();

        reviewRepository.save(newReview);
    }

    public List<Review> getAllReviews() {

        return reviewRepository.findAll();
    }

    public void deleteReviewById(UUID reviewId) {

        reviewRepository.deleteById(reviewId);
    }
}
