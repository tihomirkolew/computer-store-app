package computer_store_app.web.dto;

import computer_store_app.review.model.ReviewRating;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class NewReviewRequest {

    @NotNull(message = "Item type cannot be null.")
    private ReviewRating rating;

    @Size(max = 2000, message = "Reached maximum character limit (2000).")
    private String comment;
}
