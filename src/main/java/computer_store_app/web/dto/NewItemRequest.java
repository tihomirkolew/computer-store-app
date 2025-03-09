package computer_store_app.web.dto;

import computer_store_app.item.model.ItemCondition;
import computer_store_app.item.model.ItemType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class NewItemRequest {

    @NotBlank(message = "Brand cannot be blank")
    private String brand;

    @NotBlank(message = "Model cannot be blank")
    private String model;

    @NotNull(message = "Price cannot be null")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    private BigDecimal price;

    private String imageUrl;

    private String description;

    @NotNull(message = "Item type cannot be null")
    private ItemType type;

    @NotNull(message = "Item condition cannot be null")
    private ItemCondition itemCondition;
}
