package computer_store_app.web;

import computer_store_app.client.model.Client;
import computer_store_app.item.model.Item;
import computer_store_app.item.service.ItemService;
import computer_store_app.review.model.Review;
import computer_store_app.review.service.ReviewService;
import computer_store_app.security.AuthenticationMetadata;
import computer_store_app.client.service.ClientService;
import computer_store_app.web.dto.LoginRequest;
import computer_store_app.web.dto.RegisterRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
@RequestMapping
public class IndexController {

    private final ClientService clientService;
    private final ItemService itemService;
    private final ReviewService reviewService;

    @Autowired
    public IndexController(ClientService clientService, ItemService itemService, ReviewService reviewService) {
        this.clientService = clientService;
        this.itemService = itemService;
        this.reviewService = reviewService;
    }

    @GetMapping("/")
    public ModelAndView getIndexPage() {
        return new ModelAndView("index");
    }

    @GetMapping("/contact")
    public ModelAndView getContactsPage() {
        return new ModelAndView("contact");
    }

    @GetMapping("/register")
    public ModelAndView getRegisterPage() {

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("register");
        modelAndView.addObject("registerRequest", new RegisterRequest());

        return modelAndView;
    }

    @PostMapping("/register")
    public ModelAndView registerNewUser(@Valid RegisterRequest registerRequest, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {

            return new ModelAndView("register");
        }

        clientService.registerUser(registerRequest);

        return new ModelAndView("redirect:/login");
    }

    @GetMapping("/login")
    public ModelAndView getLoginPage(@RequestParam(value = "error", required = false) String errorParam) {

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("login");
        modelAndView.addObject("loginRequest", new LoginRequest());

        if (errorParam != null) {

            modelAndView.addObject("errorMessage", "Incorrect username or password");
        }

        return modelAndView;
    }

    @GetMapping("/home")
    public ModelAndView getHomePage(@AuthenticationPrincipal AuthenticationMetadata metadata) {

        UUID userId = metadata.getUserId();
        Client client = clientService.getById(userId);

        List<Item> authorizedAndNotSoldItems = itemService.getAuthorizedAndNotSoldItemsOrderedByUpdatedOn()
                .stream()
                .limit(3)
                .collect(Collectors.toList());

        List<Review> newestReviews = reviewService.getAllReviews()
                .stream()
                .sorted(Comparator.comparing(Review::getCreatedOn))
                .limit(3)
                .toList();

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("home");
        modelAndView.addObject("client", client);
        modelAndView.addObject("authorizedAndNotSoldItems", authorizedAndNotSoldItems);
        modelAndView.addObject("newestReviews", newestReviews);

        return modelAndView;
    }
}
