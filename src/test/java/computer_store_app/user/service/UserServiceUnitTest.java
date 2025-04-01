package computer_store_app.user.service;

import computer_store_app.cart.model.Cart;
import computer_store_app.cart.service.CartService;
import computer_store_app.security.AuthenticationMetadata;
import computer_store_app.user.model.User;
import computer_store_app.user.model.UserRole;
import computer_store_app.user.repository.UserRepository;
import computer_store_app.web.dto.EditUserRequest;
import computer_store_app.web.dto.RegisterRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceUnitTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private CartService cartService;

    @InjectMocks
    private UserService userService;

    @Test
    void givenRegisterRequestForExistingUsername_whenRegisterUser_thenReturnException() {

        // Given
        RegisterRequest request = RegisterRequest.builder()
                .username("existingUser")
                .email("newemail@test.com")
                .password("123123123")
                .build();
        when(userRepository.findByUsername("existingUser"))
                .thenReturn(Optional.of(User.builder().username("existingUser").build()));

        // When
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.registerUser(request);
        });

        // Then
        assertEquals("User with that username already exists.", exception.getMessage());
    }

    @Test
    void givenRegisterRequestForExistingEmail_whenRegisterUser_thenReturnException() {
        // Given
        RegisterRequest request = new RegisterRequest("newuser", "existingemail@test.com", "password");
        when(userRepository.findByEmail("existingemail@test.com"))
                .thenReturn(Optional.of(User.builder().email("existingemail@test.com").build()));

        // When
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.registerUser(request);
        });

        // Then
        assertEquals("User with that email already exists.", exception.getMessage());
    }

    @Test
    void givenValidRegisterRequest_whenRegisterUser_thenReturnRegisteredUser() {
        // Given
        RegisterRequest request = new RegisterRequest("newuser", "newemail@test.com", "password");
        User newUser = User.builder()
                .username("newuser")
                .email("newemail@test.com")
                .password("encodedPassword")
                .role(UserRole.CLIENT)
                .build();
        Cart emptyCart = new Cart();

        when(userRepository.findByUsername("newuser")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("newemail@test.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(newUser);
        when(cartService.createEmptyCart(newUser)).thenReturn(emptyCart);

        // When
        User registeredUser = userService.registerUser(request);

        // Then
        assertNotNull(registeredUser);
        assertEquals("newuser", registeredUser.getUsername());
        assertEquals("newemail@test.com", registeredUser.getEmail());
        assertEquals("encodedPassword", registeredUser.getPassword());
        assertEquals(UserRole.CLIENT, registeredUser.getRole());
        assertNotNull(registeredUser.getCart());
        verify(userRepository, times(1)).save(any(User.class));
        verify(cartService, times(1)).createEmptyCart(newUser);

    }

    @Test
    void givenEmailExists_whenLoadUserByUsername_thenReturnAuthenticationMetadata() {
        // Given
        String email = "test@example.com";
        UUID mockId = UUID.randomUUID(); // Generate a mock UUID
        User mockUser = User.builder()
                .id(mockId)
                .username("testuser")
                .email(email)
                .password("encodedPassword")
                .role(UserRole.CLIENT)
                .build();
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(mockUser));

        // When
        UserDetails userDetails = userService.loadUserByUsername(email);

        // Then
        assertNotNull(userDetails);
        assertEquals(mockUser.getUsername(), userDetails.getUsername());
        assertEquals(mockUser.getEmail(), ((AuthenticationMetadata) userDetails).getEmail());
        assertEquals(mockUser.getPassword(), userDetails.getPassword());
        assertEquals(1, ((AuthenticationMetadata) userDetails).getAuthorities().size());
        assertTrue(((AuthenticationMetadata) userDetails).getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_CLIENT")));
        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    void givenEmailDoesNotExist_whenLoadUserByUsername_thenThrowIllegalArgumentException() {
        // Given
        String email = "nonexistent@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.loadUserByUsername(email);
        });

        assertEquals("user with this email does not exist", exception.getMessage());
        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    void givenUserIdAndEditUserRequest_whenEditUserInfo_thenSaveNewInfo() {

        // Given
        // user
        // editRequest
        UUID userId = UUID.randomUUID();
        User user1 = User.builder()
                .id(userId)
                .username("user1")
                .email("user1@email.kon")
                .firstName("Tisho")
                .lastName("Kol")
                .build();

        EditUserRequest editUserRequest = EditUserRequest.builder()
                .username("user1edited")
                .email("user1@email.com")
                .firstName("Tihomir")
                .lastName("Kolev")
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user1));
        // When
        userService.editUserInfo(userId, editUserRequest);

        // Then
        assertEquals(user1.getUsername(), editUserRequest.getUsername());
        assertEquals(user1.getEmail(), editUserRequest.getEmail());
        assertEquals(user1.getFirstName(), editUserRequest.getFirstName());
        assertEquals(user1.getLastName(), editUserRequest.getLastName());
        verify(userRepository, times(1)).save(user1);
    }

    @Test
    void getAllUsersOrderedByRole_thenReturnUserOrderedByRole() {

        // Given
        User user1 = User.builder()
                .id(UUID.randomUUID())
                .username("user1")
                .email("user1@email.kon")
                .firstName("Tisho")
                .lastName("Kol")
                .role(UserRole.CLIENT)
                .build();
        User user2 = User.builder()
                .id(UUID.randomUUID())
                .username("user2")
                .email("user2@email.kon")
                .firstName("ime")
                .lastName("familiq")
                .role(UserRole.ADMIN)
                .build();
        when(userRepository.findAll()).thenReturn(List.of(user1, user2));

        // When
        List<User> allUsersOrderedByRole = userService.getAllUsersOrderedByRole();

        // Then
        assertEquals(2, allUsersOrderedByRole.size());
        assertEquals(user1, allUsersOrderedByRole.get(1));
        assertEquals(user2, allUsersOrderedByRole.get(0));
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void givenUserIdWithClientRole_whenSwitchRole_thenReturnUserWithAdminRole() {

        // Given
        UUID userId = UUID.randomUUID();
        User user1 = User.builder()
                .id(userId)
                .username("user1")
                .email("user1@email.kon")
                .firstName("Tisho")
                .lastName("Kol")
                .role(UserRole.CLIENT)
                .build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user1));

        // When
        userService.switchRole(userId);

        // Then
        assertEquals(UserRole.ADMIN, user1.getRole());
        verify(userRepository, times(1)).save(user1);
    }

    @Test
    void givenUserIdWithAdminRole_whenSwitchRole_thenReturnUserWithClientRole() {

        // Given
        UUID userId = UUID.randomUUID();
        User user1 = User.builder()
                .id(userId)
                .username("user1")
                .email("user1@email.kon")
                .firstName("Tisho")
                .lastName("Kol")
                .role(UserRole.ADMIN)
                .build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user1));

        // When
        userService.switchRole(userId);

        // Then
        assertEquals(UserRole.CLIENT, user1.getRole());
        verify(userRepository, times(1)).save(user1);
    }
}
