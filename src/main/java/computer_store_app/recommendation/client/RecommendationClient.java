package computer_store_app.recommendation.client;

import computer_store_app.recommendation.client.dto.Recommendation;
import computer_store_app.recommendation.client.dto.RecommendationRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "email-svc", url = "http://localhost:8081/api/v1/recommendations")
public interface RecommendationClient {

    @PostMapping
    ResponseEntity<Void> sendRecommendation(@RequestBody RecommendationRequest recommendationRequest);

    @GetMapping
    ResponseEntity<List<Recommendation>> getAllRecommendations();

    @PostMapping("{id}/archive")
    ResponseEntity<Void> archiveRecommendation(@PathVariable UUID id);
}
