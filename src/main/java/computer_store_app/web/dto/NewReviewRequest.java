package computer_store_app.web.dto;

import computer_store_app.review.model.ReviewRating;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class NewReviewRequest {

    @NotNull(message = "Please select a category.")
    private ReviewRating rating;

    @Size(max = 2001, message = "Reached maximum character limit (2000).")
    private String comment;
}
