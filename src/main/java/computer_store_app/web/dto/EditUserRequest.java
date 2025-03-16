package computer_store_app.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EditUserRequest {

    @Size(min = 6, message = "Username must be at least 6 characters.")
    private String username;

    private String firstName;

    private String lastName;

    @Email
    @NotNull
    private String email;
}
