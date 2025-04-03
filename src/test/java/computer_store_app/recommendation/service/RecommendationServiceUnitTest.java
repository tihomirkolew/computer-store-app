package computer_store_app.recommendation.service;

import computer_store_app.recommendation.client.RecommendationClient;
import computer_store_app.recommendation.client.dto.Recommendation;
import computer_store_app.recommendation.client.dto.RecommendationRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RecommendationServiceUnitTest {

    @Mock
    private RecommendationClient recommendationClient;

    @InjectMocks
    private RecommendationService recommendationService;

    @Test
    void givenValidRecommendationRequest_whenSendRecommendation_thenFeignClientCalledSuccessfully() {
        // Given
        String userEmail = "test@example.com";
        String content = "Recommendation content";
        RecommendationRequest recommendationRequest = RecommendationRequest.builder()
                .userEmail(userEmail)
                .content(content)
                .build();

        when(recommendationClient.sendRecommendation(recommendationRequest))
                .thenReturn(ResponseEntity.ok().build());

        // When
        recommendationService.sendRecommendation(userEmail, content);

        // Then
        verify(recommendationClient, times(1)).sendRecommendation(recommendationRequest);
    }

    @Test
    void givenRecommendationClientThrowsException_whenSendRecommendation_thenLogWarn() {
        // Given
        String userEmail = "test@example.com";
        String content = "Recommendation content";
        RecommendationRequest recommendationRequest = RecommendationRequest.builder()
                .userEmail(userEmail)
                .content(content)
                .build();

        when(recommendationClient.sendRecommendation(recommendationRequest))
                .thenThrow(new RuntimeException("FeignClient error"));

        // When
        recommendationService.sendRecommendation(userEmail, content);

        // Then
        verify(recommendationClient, times(1)).sendRecommendation(recommendationRequest);
    }

    @Test
    void givenNon2xxResponse_whenSendRecommendation_thenLogError() {
        // Given
        String userEmail = "user@example.com";
        String content = "Great store!";
        RecommendationRequest recommendationRequest = RecommendationRequest.builder()
                .userEmail(userEmail)
                .content(content)
                .build();

        when(recommendationClient.sendRecommendation(recommendationRequest))
                .thenReturn(ResponseEntity.status(400).build());

        // When
        recommendationService.sendRecommendation(userEmail, content);

        // Then
        verify(recommendationClient, times(1)).sendRecommendation(recommendationRequest);

    }

    @Test
    void givenSuccessfulResponse_whenGetAllRecommendations_thenReturnRecommendationsList() {
        // Given
        Recommendation recommendation1 = Recommendation.builder()
                .userEmail("user1@example.com")
                .content("Great service!")
                .createdOn(LocalDateTime.now())
                .build();

        Recommendation recommendation2 = Recommendation.builder()
                .userEmail("user2@example.com")
                .content("Amazing quality!")
                .createdOn(LocalDateTime.now())
                .build();

        List<Recommendation> recommendations = List.of(recommendation1, recommendation2);

        when(recommendationClient.getAllRecommendations())
                .thenReturn(ResponseEntity.ok(recommendations));

        // When
        List<Recommendation> result = recommendationService.getAllRecommendations();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(recommendation1, result.get(0));
        assertEquals(recommendation2, result.get(1));
        verify(recommendationClient, times(1)).getAllRecommendations();
    }

    @Test
    void givenNonSuccessfulResponse_whenGetAllRecommendations_thenThrowIllegalStateException() {
        // Given
        when(recommendationClient.getAllRecommendations())
                .thenReturn(ResponseEntity.status(500).build());

        // When & Then
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            recommendationService.getAllRecommendations();
        });

        assertEquals("Failed to fetch recommendations from the recommendation service", exception.getMessage());
        verify(recommendationClient, times(1)).getAllRecommendations();
    }

    @Test
    void givenSuccessfulResponseWithNullBody_whenGetAllRecommendations_thenThrowIllegalStateException() {
        // Given
        when(recommendationClient.getAllRecommendations())
                .thenReturn(ResponseEntity.ok(null));

        // When & Then
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            recommendationService.getAllRecommendations();
        });

        assertEquals("Failed to fetch recommendations from the recommendation service", exception.getMessage());
        verify(recommendationClient, times(1)).getAllRecommendations();
    }

    @Test
    void givenValidResponse_whenArchiveRecommendation_thenFeignClientCalledSuccessfully() {

        // given
        UUID recommendationId = UUID.randomUUID();

        when(recommendationClient.archiveRecommendation(recommendationId))
                .thenReturn(ResponseEntity.ok().build());

        // when
        recommendationService.archiveRecommendation(recommendationId);

        // then
        verify(recommendationClient, times(1)).archiveRecommendation(recommendationId);
    }

    @Test
    void givenNon2xxResponse_whenArchiveRecommendation_thenLogError() {

        // given
        UUID recommendationId = UUID.randomUUID();

        when(recommendationClient.archiveRecommendation(recommendationId))
                .thenReturn(ResponseEntity.status(400).build());

        // when
        recommendationService.archiveRecommendation(recommendationId);

        // then
        verify(recommendationClient, times(1)).archiveRecommendation(recommendationId);
    }

    @Test
    void givenException_whenArchiveRecommendation_thenLogWarn() {

        // given
        UUID recommendationId = UUID.randomUUID();

        when(recommendationClient.archiveRecommendation(recommendationId))
                .thenThrow(new RuntimeException("FeignClient error"));

        // when
        recommendationService.archiveRecommendation(recommendationId);

        // then
        verify(recommendationClient, times(1)).archiveRecommendation(recommendationId);
    }


}
