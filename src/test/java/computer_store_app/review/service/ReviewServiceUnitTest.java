package computer_store_app.review.service;

import computer_store_app.review.model.Review;
import computer_store_app.review.model.ReviewRating;
import computer_store_app.review.repository.ReviewRepository;
import computer_store_app.user.model.User;
import computer_store_app.user.model.UserRole;
import computer_store_app.web.dto.NewReviewRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReviewServiceUnitTest {

    @InjectMocks
    private ReviewService reviewService;

    @Mock
    private  ReviewRepository reviewRepository;

    @Test
    void givenUserAndNewReviewRequest_whenAddNewReview_thenSaveNewReview () {

        // Given
        User user = User.builder().id(UUID.randomUUID()).role(UserRole.ADMIN).build();
        NewReviewRequest reviewRequest = NewReviewRequest.builder()
                .rating(ReviewRating.EXCELLENT)
                .comment("Great service!")
                .build();

        Review expectedReview = Review.builder()
                .addedBy(user)
                .rating(ReviewRating.EXCELLENT)
                .comment("Great service!")
                .createdOn(LocalDateTime.now())
                .build();

        when(reviewRepository.save(any(Review.class))).thenReturn(expectedReview);

        // When
        reviewService.addNewReview(reviewRequest, user);

        // Then
        Review savedReview = reviewRepository.save(expectedReview);
        assertNotNull(savedReview);
        assertEquals(user, savedReview.getAddedBy());
        assertEquals(ReviewRating.EXCELLENT, savedReview.getRating());
        assertEquals("Great service!", savedReview.getComment());
        assertNotNull(savedReview.getCreatedOn());
    }

    @Test
    void getAllReviews_thenReturnReviewsList() {

        // Given
        Review review1 = Review.builder()
                .rating(ReviewRating.EXCELLENT)
                .comment("Great service!")
                .createdOn(LocalDateTime.now())
                .build();

        Review review2 = Review.builder()
                .rating(ReviewRating.EXCELLENT)
                .comment("Great service!")
                .createdOn(LocalDateTime.now().minusMonths(1))
                .build();
        when(reviewRepository.findAll()).thenReturn(List.of(review1, review2));

        // When
        List<Review> result = reviewService.getAllReviews();

        // Then
        assertEquals(2, result.size());
        assertEquals(review1, result.get(0));
        assertEquals(review2, result.get(1));
        verify(reviewRepository, times(1)).findAll();

    }

    @Test
    void getAllReviewsOrderedByCreatedOn_thenReturnReviewListOrdered () {

        // Given
        Review review1 = Review.builder()
                .rating(ReviewRating.EXCELLENT)
                .comment("Great service!")
                .createdOn(LocalDateTime.now())
                .build();

        Review review2 = Review.builder()
                .rating(ReviewRating.EXCELLENT)
                .comment("Great service!")
                .createdOn(LocalDateTime.now().minusMonths(1))
                .build();

        when(reviewRepository.getAllByOrderByCreatedOnDesc())
                .thenReturn(List.of(review1, review2));

        // When
        List<Review> result = reviewService.getAllReviewsOrderedByCreatedOn();

        // Then
        assertNotNull(result);
        assertEquals(review1, result.get(0));
        assertEquals(review2, result.get(1));
        verify(reviewRepository, times(1)).getAllByOrderByCreatedOnDesc();
    }

    @Test
    void getLastThreeReviewsOrderedByCreatedOn_thenReturnReviewListOrderedLimitedToThree () {

        // Given
        Review review1 = Review.builder()
                .rating(ReviewRating.EXCELLENT)
                .comment("Great service!")
                .createdOn(LocalDateTime.now())
                .build();

        Review review2 = Review.builder()
                .rating(ReviewRating.EXCELLENT)
                .comment("Great service!")
                .createdOn(LocalDateTime.now().minusDays(1))
                .build();

        Review review3 = Review.builder()
                .rating(ReviewRating.EXCELLENT)
                .comment("Great service!")
                .createdOn(LocalDateTime.now().minusDays(2))
                .build();

        Review review4 = Review.builder()
                .rating(ReviewRating.EXCELLENT)
                .comment("Great service!")
                .createdOn(LocalDateTime.now().minusDays(2))
                .build();

        when(reviewRepository.getAllByOrderByCreatedOnDesc())
                .thenReturn(List.of(review1, review2, review3, review4));

        // When
        List<Review> result = reviewService.getLastThreeReviewsOrderedByCreatedOn();

        // Then
        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals(review1, result.get(0));
        assertEquals(review2, result.get(1));
        assertEquals(review3, result.get(2));
        verify(reviewRepository, times(1)).getAllByOrderByCreatedOnDesc();
    }

    @Test
    void givenReviewId_whenDeleteReviewById_thenDeleteFromRepository () {

        // Given
        UUID reviewId = UUID.randomUUID();
        Review review1 = Review.builder()
                .id(reviewId)
                .rating(ReviewRating.EXCELLENT)
                .comment("Great service!")
                .createdOn(LocalDateTime.now())
                .build();
        doNothing().when(reviewRepository).deleteById(reviewId);

        // When
        reviewService.deleteReviewById(reviewId);

        // Then
        verify(reviewRepository, times(1)).deleteById(reviewId);
    }

}
