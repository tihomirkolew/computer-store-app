package computer_store_app.recommendation.service;

import computer_store_app.recommendation.client.RecommendationClient;
import computer_store_app.recommendation.client.dto.Recommendation;
import computer_store_app.recommendation.client.dto.RecommendationRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@Slf4j
public class RecommendationService {

    private final RecommendationClient recommendationClient;

    public RecommendationService(RecommendationClient recommendationClient) {
        this.recommendationClient = recommendationClient;
    }

    public void sendRecommendation(String userEmail, String content) {
        RecommendationRequest recommendationRequest = RecommendationRequest.builder()
                .userEmail(userEmail)
                .content(content)
                .build();

        ResponseEntity<Void> httpResponse;
        try {
            httpResponse = recommendationClient.sendRecommendation(recommendationRequest);
            if (!httpResponse.getStatusCode().is2xxSuccessful()) {
                log.error("[Feign call to recommendation-svc failed] Can't send recommendation to our store");
            }
        } catch (Exception e) {
            log.warn("Can't send recommendation right now due to 500 Internal Server Error");
        }

    }

    public List<Recommendation> getAllRecommendations() {

        ResponseEntity<List<Recommendation>> response = recommendationClient.getAllRecommendations();

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return response.getBody(); // Return the list of recommendations
        } else {
            throw new IllegalStateException("Failed to fetch recommendations from the recommendation service");
        }
    }


}
