package computer_store_app.review.service;

import computer_store_app.review.model.Review;
import computer_store_app.review.repository.ReviewRepository;
import computer_store_app.user.model.User;
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

    public void addNewReview(NewReviewRequest newReviewRequest, User user) {

        Review newReview = Review.builder()
                .addedBy(user)
                .rating(newReviewRequest.getRating())
                .comment(newReviewRequest.getComment())
                .createdOn(LocalDateTime.now())
                .build();

        reviewRepository.save(newReview);
    }

    public List<Review> getAllReviews() {

        return reviewRepository.findAll();
    }

    public List<Review> getAllReviewsOrderedByCreatedOn () {

       return reviewRepository.getAllByOrderByCreatedOnDesc();
    }

    public List<Review> getLastThreeReviewsOrderedByCreatedOn () {

        return reviewRepository.getAllByOrderByCreatedOnDesc()
                .stream().limit(3).toList();
    }

    public void deleteReviewById(UUID reviewId) {

        reviewRepository.deleteById(reviewId);
    }
}
