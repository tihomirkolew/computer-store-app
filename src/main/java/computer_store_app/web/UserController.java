package computer_store_app.web;

import computer_store_app.security.AuthenticationMetadata;
import computer_store_app.user.model.User;
import computer_store_app.user.service.UserService;
import computer_store_app.web.dto.EditUserRequest;
import computer_store_app.web.mapper.UserToEditRequestMapper;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.UUID;

@Controller
@RequestMapping("users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
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
}
