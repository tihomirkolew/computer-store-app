package computer_store_app.client.service;

import computer_store_app.cart.model.Cart;
import computer_store_app.cart.service.CartService;
import computer_store_app.client.model.Client;
import computer_store_app.security.AuthenticationMetadata;
import computer_store_app.client.model.ClientRole;
import computer_store_app.client.repository.ClientRepository;
import computer_store_app.web.dto.EditUserRequest;
import computer_store_app.web.dto.RegisterRequest;
import jakarta.transaction.Transactional;
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
public class ClientService implements UserDetailsService {

    private final ClientRepository clientRepository;
    private final PasswordEncoder passwordEncoder;
    private final CartService cartService;

    @Autowired
    public ClientService(ClientRepository clientRepository, PasswordEncoder passwordEncoder, CartService cartService) {
        this.clientRepository = clientRepository;
        this.passwordEncoder = passwordEncoder;
        this.cartService = cartService;
    }

    @Transactional
    public Client registerUser(RegisterRequest registerRequest) {

        Optional<Client> userRepositoryByUsername = clientRepository.findByUsername(registerRequest.getUsername());

        Optional<Client> userRepositoryByEmail = clientRepository.findByEmail(registerRequest.getEmail());

        if (userRepositoryByUsername.isPresent()) {
            throw new IllegalArgumentException("User with that username already exists.");
        }
        if (userRepositoryByEmail.isPresent()) {
            throw new IllegalArgumentException("User with that email already exists.");
        }

        Client initializeClient = Client.builder()
                .username(registerRequest.getUsername())
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .role(ClientRole.CLIENT)
                .build();

        Client client = clientRepository.save(initializeClient);

        // make empty cart
        Cart defaultCart = cartService.createEmptyCart(client);
        client.setCart(defaultCart);

        return client;
    }

    //for login
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        Client client = clientRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("user with this email does not exist"));

        return new AuthenticationMetadata(client.getId(), client.getUsername(), client.getEmail(), client.getPassword(), client.getRole());
    }

    public Client getById(UUID userId) {

        return clientRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User with id [%S] does not exist.".formatted(userId)));
    }

    public void editUserInfo(UUID userId, EditUserRequest editUserRequest) {

        Client client = getById(userId);

        client.setUsername(editUserRequest.getUsername());
        client.setFirstName(editUserRequest.getFirstName());
        client.setLastName(editUserRequest.getLastName());
        client.setEmail(editUserRequest.getEmail());

        clientRepository.save(client);
    }

    public List<Client> getAllUsers() {

        return clientRepository.findAll();
    }

    public void switchRole(UUID userId) {

        Client client = getById(userId);

        if (client.getRole() == ClientRole.CLIENT) {
            client.setRole(ClientRole.ADMIN);
        } else {
            client.setRole(ClientRole.CLIENT);
        }

        clientRepository.save(client);
    }
}
