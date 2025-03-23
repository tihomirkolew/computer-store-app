package computer_store_app.user.service;

import computer_store_app.cart.model.Cart;
import computer_store_app.cart.service.CartService;
import computer_store_app.security.AuthenticationMetadata;
import computer_store_app.user.model.User;
import computer_store_app.user.model.UserRole;
import computer_store_app.user.repository.UserRepository;
import computer_store_app.web.dto.EditUserRequest;
import computer_store_app.web.dto.RegisterRequest;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CartService cartService;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, CartService cartService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.cartService = cartService;
    }

    @Transactional
    public User registerUser(RegisterRequest registerRequest) {

        Optional<User> userRepositoryByUsername = userRepository.findByUsername(registerRequest.getUsername());

        Optional<User> userRepositoryByEmail = userRepository.findByEmail(registerRequest.getEmail());

        if (userRepositoryByUsername.isPresent()) {
            throw new IllegalArgumentException("User with that username already exists.");
        }
        if (userRepositoryByEmail.isPresent()) {
            throw new IllegalArgumentException("User with that email already exists.");
        }

        User initializeUser = User.builder()
                .username(registerRequest.getUsername())
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .role(UserRole.CLIENT)
                .build();

        User user = userRepository.save(initializeUser);

        // make empty cart
        Cart defaultCart = cartService.createEmptyCart(user);
        user.setCart(defaultCart);

        return user;
    }

    //for login
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        User user = userRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("user with this email does not exist"));

        return new AuthenticationMetadata(user.getId(), user.getUsername(), user.getEmail(), user.getPassword(), user.getRole());
    }

    public User getById(UUID userId) {

        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User with id [%S] does not exist.".formatted(userId)));
    }

    public void editUserInfo(UUID userId, EditUserRequest editUserRequest) {

        User user = getById(userId);

        user.setUsername(editUserRequest.getUsername());
        user.setFirstName(editUserRequest.getFirstName());
        user.setLastName(editUserRequest.getLastName());
        user.setEmail(editUserRequest.getEmail());

        userRepository.save(user);
    }

    public List<User> getAllUsers() {

        return userRepository.findAll();
    }

    public void switchRole(UUID userId) {

        User user = getById(userId);

        if (user.getRole() == UserRole.CLIENT) {
            user.setRole(UserRole.ADMIN);
        } else {
            user.setRole(UserRole.CLIENT);
        }

        userRepository.save(user);
    }
}
