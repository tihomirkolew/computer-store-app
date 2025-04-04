package computer_store_app.web;

import computer_store_app.user.model.User;
import computer_store_app.item.model.Item;
import computer_store_app.item.service.ItemService;
import computer_store_app.review.model.Review;
import computer_store_app.review.service.ReviewService;
import computer_store_app.user.service.UserService;
import computer_store_app.web.dto.EditUserRequest;
import computer_store_app.web.mapper.UserToEditRequestMapper;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("users")
public class UserController {

    private final UserService userService;
    private final ItemService itemService;
    private final ReviewService reviewService;

    @Autowired
    public UserController(UserService userService, ItemService itemService, ReviewService reviewService) {
        this.userService = userService;
        this.itemService = itemService;
        this.reviewService = reviewService;
    }

    @GetMapping("/{id}/profile")
    public ModelAndView getProfilePage(@PathVariable UUID id) {

        User user = userService.getById(id);

        ModelAndView modelAndView = new ModelAndView("profile");
        modelAndView.addObject(user);

        return modelAndView;
    }

    @GetMapping("/{id}/edit")
    public ModelAndView getEditProfilePage(@PathVariable UUID id) {

        User user = userService.getById(id);

        ModelAndView modelAndView = new ModelAndView("edit-profile");
        modelAndView.addObject("user", user);
        modelAndView.addObject("editUserRequest", UserToEditRequestMapper.mapUserInfoToEditRequest(user));

        return modelAndView;
    }

    @PutMapping("/{id}/edit")
    public ModelAndView editUserDetails(@PathVariable UUID id, @Valid EditUserRequest editUserRequest, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            User user = userService.getById(id);

            ModelAndView modelAndView = new ModelAndView();
            modelAndView.setViewName("edit-profile");
            modelAndView.addObject("user", user);
            modelAndView.addObject("editUserRequest", editUserRequest);
            return modelAndView;
        }

        userService.editUserInfo(id, editUserRequest);

        return new ModelAndView("redirect:/users/" + id + "/profile");
    }

    @GetMapping("/admin-dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    public ModelAndView getAdminDashboard() {

        List<User> usersSortedByAdminFirst = userService.getAllUsersOrderedByRole();

        List<Item> itemsSortedFirstWaiting = itemService.getAllNotArchivedItemsSortedByIsAuthorized();

        List<Review> reviewsOrderedByCreatedOn = reviewService.getAllReviewsOrderedByCreatedOn();

        ModelAndView modelAndView = new ModelAndView("admin-dashboard");
        modelAndView.addObject("usersSortedByAdminFirst", usersSortedByAdminFirst);
        modelAndView.addObject("itemsSortedFirstWaiting", itemsSortedFirstWaiting);
        modelAndView.addObject("newestReviews", reviewsOrderedByCreatedOn);

        return modelAndView;
    }

    @PutMapping("/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public String switchUserRole(@PathVariable UUID id) {

        userService.switchRole(id);

        return "redirect:/users/admin-dashboard";
    }
}
