package computer_store_app.user.service;

import computer_store_app.user.model.User;
import computer_store_app.user.model.UserRole;
import computer_store_app.user.repository.UserRepository;
import computer_store_app.web.dto.RegisterRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class UserInit implements CommandLineRunner {


    private final UserService userService;

    @Autowired
    public UserInit(UserService userService) {
        super();
        this.userService = userService;
    }

    @Override
    public void run(String... args) throws Exception {
        if (!userService.getAllUsers().isEmpty()) {
            return;
        }

        RegisterRequest registerRequest = RegisterRequest.builder()
                .username("pensaka")
                .password("123123123")
                .email("sineadfly@gmail.com")
                .build();

        User initialUser = userService.registerUser(registerRequest);

        initialUser.setRole(UserRole.ADMIN);
    }


}
