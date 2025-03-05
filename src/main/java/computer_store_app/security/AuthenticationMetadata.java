package computer_store_app.security;


import computer_store_app.user.model.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

// този клас пази данните на логнатия потребител
@Data
@AllArgsConstructor
public class AuthenticationMetadata implements UserDetails {

    private UUID userId;
    private String username;
    private String email;
    private String password;
    private UserRole role; // CLIENT, ADMIN

    // този метод се използва от Spring Security за да се разбере какви authorities има потребитела
    // Authority - permission или роля
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        // hasRole("ADMIN") -> "ROLE_ADMIN"
        // hasAuthority("ADMIN") -> "ADMIN"

        // Permission: CAN_EDIT_USER_PROFILES

        SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority("ROLE_" + role.name());

        return List.of(simpleGrantedAuthority);
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }
}

