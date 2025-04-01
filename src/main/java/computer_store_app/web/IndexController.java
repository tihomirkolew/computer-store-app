package computer_store_app.web;

import computer_store_app.user.model.User;
import computer_store_app.item.model.Item;
import computer_store_app.item.service.ItemService;
import computer_store_app.review.model.Review;
import computer_store_app.review.service.ReviewService;
import computer_store_app.security.AuthenticationMetadata;
import computer_store_app.user.service.UserService;
import computer_store_app.web.dto.LoginRequest;
import computer_store_app.web.dto.RegisterRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping
public class IndexController {

    private final UserService userService;
    private final ItemService itemService;
    private final ReviewService reviewService;

    @Autowired
    public IndexController(UserService userService, ItemService itemService, ReviewService reviewService) {
        this.userService = userService;
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

        userService.registerUser(registerRequest);

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
        User user = userService.getById(userId);

        List<Item> authorizedAndNotSoldNotArchivedItems = itemService.getLastThreeAuthorizedNotSoldAndNotArchivedItemsOrderedByAddedOn();

        List<Review> newestReviews = reviewService.getLastThreeReviewsOrderedByCreatedOn();

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("home");
        modelAndView.addObject("user", user);
        modelAndView.addObject("authorizedAndNotSoldItems", authorizedAndNotSoldNotArchivedItems);
        modelAndView.addObject("newestReviews", newestReviews);

        return modelAndView;
    }
}
