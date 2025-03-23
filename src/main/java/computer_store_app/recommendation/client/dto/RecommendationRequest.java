package computer_store_app.recommendation.client.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RecommendationRequest {

    private String userEmail;

    private String content;
}
