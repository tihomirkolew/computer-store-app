package computer_store_app.recommendation.client.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class Recommendation {

    private UUID id;

    private String userEmail;

    private String content;

    private LocalDateTime createdOn;

    private boolean archived;
}
