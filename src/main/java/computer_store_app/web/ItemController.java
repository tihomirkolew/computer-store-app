package computer_store_app.web;

import computer_store_app.item.service.ItemService;
import computer_store_app.security.AuthenticationMetadata;
import computer_store_app.user.model.User;
import computer_store_app.user.service.UserService;
import computer_store_app.web.dto.NewItemRequest;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("items")
public class ItemController {

    private final UserService userService;
    private final ItemService itemService;

    public ItemController(UserService userService, ItemService itemService) {
        this.userService = userService;
        this.itemService = itemService;
    }

    @GetMapping
    private ModelAndView getItemsPage() {

        ModelAndView modelAndView = new ModelAndView("items");

        return modelAndView;
    }

    @GetMapping("/new-item")
    private ModelAndView getNewItemPage() {

        ModelAndView modelAndView = new ModelAndView("new-item");
        modelAndView.addObject("newItemRequest", new NewItemRequest());

        return modelAndView;
    }

    @PostMapping("/new-item")
    private String addNewItem(@Valid NewItemRequest newItemRequest, BindingResult bindingResult, @AuthenticationPrincipal AuthenticationMetadata metadata) {

        if (bindingResult.hasErrors()) {
            return "redirect:/new-item";
        }

        User user = userService.getById(metadata.getUserId());

        itemService.addNewItem(newItemRequest, user);

        return "redirect:/home";
    }
}
