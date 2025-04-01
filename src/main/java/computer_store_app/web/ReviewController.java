package computer_store_app.web;

import computer_store_app.user.model.User;
import computer_store_app.review.model.Review;
import computer_store_app.review.service.ReviewService;
import computer_store_app.security.AuthenticationMetadata;
import computer_store_app.user.service.UserService;
import computer_store_app.web.dto.NewReviewRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/reviews")
public class ReviewController {

    private final UserService userService;
    private final ReviewService reviewService;

    @Autowired
    public ReviewController(UserService userService, ReviewService reviewService) {
        this.userService = userService;
        this.reviewService = reviewService;
    }

    @GetMapping
    public ModelAndView getReviewsPage(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {

        User user = userService.getById(authenticationMetadata.getUserId());
        List<Review> allCustomerReviews = reviewService.getAllReviewsOrderedByCreatedOn();

        ModelAndView modelAndView = new ModelAndView("reviews");
        modelAndView.addObject("user", user);
        modelAndView.addObject("allCustomerReviews", allCustomerReviews);

        return modelAndView;
    }

    // add-review page
    @GetMapping("/new-review")
    public ModelAndView getAddReviewPage() {

        ModelAndView modelAndView = new ModelAndView("new-review");
        modelAndView.addObject("newReviewRequest", new NewReviewRequest());

        return modelAndView;
    }

    @PostMapping("/new-review")
    public String addReview(@Valid NewReviewRequest newReviewRequest, BindingResult bindingResult, @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {

        if (bindingResult.hasErrors()) {
            return "new-review";
        }

        User user = userService.getById(authenticationMetadata.getUserId());

        reviewService.addNewReview(newReviewRequest, user);

        return "redirect:/home";
    }

    @DeleteMapping("/{id}/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteReview(@PathVariable UUID id) {

        reviewService.deleteReviewById(id);

        return "redirect:/users/admin-dashboard";
    }


}
