package computer_store_app.web.dto;

import computer_store_app.review.model.ReviewRating;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NewReviewRequest {

    @NotNull(message = "Please select a category.")
    private ReviewRating rating;

    @Size(max = 2001, message = "Reached maximum character limit (2000).")
    private String comment;
}
