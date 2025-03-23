package computer_store_app.client.service;

import computer_store_app.client.model.Client;
import computer_store_app.client.model.ClientRole;
import computer_store_app.client.repository.ClientRepository;
import computer_store_app.web.dto.RegisterRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class ClientInit implements CommandLineRunner {


    private final ClientService clientService;
    private final ClientRepository clientRepository;

    @Autowired
    public ClientInit(ClientService clientService, ClientRepository clientRepository) {
        super();
        this.clientService = clientService;
        this.clientRepository = clientRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (!clientService.getAllUsers().isEmpty()) {
            return;
        }

        RegisterRequest registerRequest = RegisterRequest.builder()
                .username("pensaka")
                .password("123123123")
                .email("sineadfly@gmail.com")
                .build();

        Client initialClient = clientService.registerUser(registerRequest);

        initialClient.setRole(ClientRole.ADMIN);

        clientRepository.save(initialClient);
    }


}
