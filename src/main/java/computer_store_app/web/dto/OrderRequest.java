package computer_store_app.web.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequest {

    @NotNull
    private String firstName;

    @NotNull
    private String lastName;

    @Pattern(regexp = "^\\+?[0-9]*$", message = "Phone number must be numeric and can include a '+' prefix")
    @Size(min = 10, max = 15, message = "Phone number must be between 10 and 15 characters")
    private String phoneNumber;

    @NotNull
    private String billingAddress;

    @NotNull
    private String shippingAddress;
}
