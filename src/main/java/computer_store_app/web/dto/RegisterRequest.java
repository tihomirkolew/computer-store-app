package computer_store_app.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {

    // username
    @Size(min = 6, message = "Username must be at least 6 characters.")
    private String username;
    // email
    @NotNull
    @Email
    private String email;
    // password
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;
}
