package computer_store_app.web.dto;

import computer_store_app.item.model.ItemCondition;
import computer_store_app.item.model.ItemType;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NewItemRequest {

    @NotBlank(message = "Brand cannot be blank.")
    private String brand;

    @NotBlank(message = "Model cannot be blank.")
    private String model;

    @NotNull
    @Positive
    private BigDecimal price;

    private String imageUrl;

    @Size(min = 0, max = 300, message = "Reached maximum character limit (300).")
    @Lob
    private String description;

    @NotNull(message = "Item type cannot be null.")
    private ItemType type;

    @NotNull(message = "Item condition cannot be null.")
    private ItemCondition itemCondition;
}
