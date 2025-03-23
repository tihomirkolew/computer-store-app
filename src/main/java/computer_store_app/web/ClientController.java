package computer_store_app.web;

import computer_store_app.client.model.Client;
import computer_store_app.item.model.Item;
import computer_store_app.item.service.ItemService;
import computer_store_app.review.model.Review;
import computer_store_app.review.service.ReviewService;
import computer_store_app.client.service.ClientService;
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

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("users")
public class ClientController {

    private final ClientService clientService;
    private final ItemService itemService;
    private final ReviewService reviewService;

    @Autowired
    public ClientController(ClientService clientService, ItemService itemService, ReviewService reviewService) {
        this.clientService = clientService;
        this.itemService = itemService;
        this.reviewService = reviewService;
    }

    @GetMapping("/{id}/profile")
    public ModelAndView getProfilePage(@PathVariable UUID id) {

        Client client = clientService.getById(id);

        ModelAndView modelAndView = new ModelAndView("profile");
        modelAndView.addObject(client);

        return modelAndView;
    }

    @GetMapping("/{id}/edit")
    public ModelAndView getEditProfilePage(@PathVariable UUID id) {

        Client client = clientService.getById(id);

        ModelAndView modelAndView = new ModelAndView("edit-profile");
        modelAndView.addObject("client", client);
        modelAndView.addObject("editUserRequest", UserToEditRequestMapper.mapUserInfoToEditRequest(client));

        return modelAndView;
    }

    @PutMapping("/{id}/edit")
    public ModelAndView editUserDetails(@PathVariable UUID id, @Valid EditUserRequest editUserRequest, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            Client client = clientService.getById(id);

            ModelAndView modelAndView = new ModelAndView();
            modelAndView.setViewName("edit-profile");
            modelAndView.addObject("client", client);
            modelAndView.addObject("editUserRequest", editUserRequest);
            return modelAndView;
        }

        clientService.editUserInfo(id, editUserRequest);

        return new ModelAndView("redirect:/users/" + id + "/profile");
    }

    @GetMapping("/admin-dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    public ModelAndView getAdminDashboard() {

        List<Client> usersList = clientService.getAllUsers();
        // sorted user showing admins first
        List<Client> usersSortedByAdminFirst = usersList.stream().sorted(Comparator.comparing(Client::getRole).reversed()).toList();

        List<Item> allItems = itemService.getAllItems();
        // sorting items making it show the waiting for approval first
        List<Item> itemsSortedFirstWaiting = allItems.stream().sorted(Comparator.comparing(Item::isAuthorized)).toList();

        List<Review> newestReviews = reviewService.getAllReviews()
                .stream()
                .sorted(Comparator.comparing(Review::getCreatedOn))
                .toList();


        ModelAndView modelAndView = new ModelAndView("admin-dashboard");
        modelAndView.addObject("usersSortedByAdminFirst", usersSortedByAdminFirst);
        modelAndView.addObject("itemsSortedFirstWaiting", itemsSortedFirstWaiting);
        modelAndView.addObject("newestReviews", newestReviews);

        return modelAndView;
    }

    @PutMapping("/{id}/role")
    public String switchUserRole(@PathVariable UUID id) {

        clientService.switchRole(id);

        return "redirect:/users/admin-dashboard";
    }
}
