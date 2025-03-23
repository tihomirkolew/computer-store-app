package computer_store_app.recommendation.client.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class Recommendation {

    private String userEmail;

    private String content;

    private LocalDateTime createdOn;
}
