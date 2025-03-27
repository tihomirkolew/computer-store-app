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

    @NotBlank(message = "Brand cannot be empty.")
    private String brand;

    @NotBlank(message = "Model cannot be empty.")
    private String model;

    @NotNull(message = "You must have a price")
    @Positive
    private BigDecimal price;

    private String imageUrl;

    @Size(max = 300, message = "Reached maximum character limit (300).")
    @Lob
    private String description;

    @NotNull(message = "Please chose what type is your item")
    private ItemType type;

    @NotNull(message = "Please chose your items condition")
    private ItemCondition itemCondition;
}
