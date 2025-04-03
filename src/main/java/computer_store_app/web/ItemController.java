package computer_store_app.web;

import computer_store_app.sellerOrder.model.SellerOrder;
import computer_store_app.sellerOrder.repository.SellerOrderRepository;
import computer_store_app.user.model.User;
import computer_store_app.item.model.Item;
import computer_store_app.item.service.ItemService;
import computer_store_app.security.AuthenticationMetadata;
import computer_store_app.web.dto.NewItemRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import computer_store_app.user.service.UserService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("items")
public class ItemController {

    private final ItemService itemService;
    private final UserService userService;
    private final SellerOrderRepository sellerOrderRepository;

    @Autowired
    public ItemController(ItemService itemService, UserService userService, SellerOrderRepository sellerOrderRepository) {
        this.itemService = itemService;
        this.userService = userService;
        this.sellerOrderRepository = sellerOrderRepository;
    }

    @GetMapping("/items-list")
    public ModelAndView getAllItemsPage() {

        List<Item> authorizedAndNotSoldItems = itemService.getAuthorizedNotSoldAndNotArchivedItems();

        ModelAndView modelAndView = new ModelAndView("items-list");
        modelAndView.addObject("authorizedAndNotSoldItems", authorizedAndNotSoldItems);

        return modelAndView;
    }

    @GetMapping("/{id}/added-items")
    public ModelAndView getCurrentUserItemsPage(@PathVariable UUID id, @AuthenticationPrincipal AuthenticationMetadata metadata) {

        List<Item> itemsByUserId = itemService.getItemsByUserIdNotArchived(id);
        List<SellerOrder> ordersMadeByOtherPeople = sellerOrderRepository.findBySellerId(metadata.getUserId());

        ModelAndView modelAndView = new ModelAndView("user-dashboard");
        modelAndView.addObject("itemsByUserId", itemsByUserId);
        modelAndView.addObject("ordersMadeByOtherPeople", ordersMadeByOtherPeople);

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
            return "new-item";
        }

        UUID userId = metadata.getUserId();

        User user = userService.getById(userId);

        itemService.addNewItem(newItemRequest, user);

        return "redirect:/home";
    }

    @GetMapping("/{id}")
    public ModelAndView getItemDetails(@PathVariable UUID id, @AuthenticationPrincipal AuthenticationMetadata metadata) {

        User user = userService.getById(metadata.getUserId());
        Item item = itemService.getItemById(id);

        ModelAndView modelAndView = new ModelAndView("item-details");
        modelAndView.addObject("item", item);
        modelAndView.addObject("user", user);

        return modelAndView;
    }

    @PutMapping("/{id}/approve")
    public String approveItem(@PathVariable UUID id, @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {

        UUID userId = authenticationMetadata.getUserId();
        User user = userService.getById(userId);

        itemService.approveItem(id, user);

        return "redirect:/users/admin-dashboard";
    }

    @PostMapping("/{id}/archive")
    public String deleteItem(@PathVariable UUID id) {

        itemService.archiveItemById(id);

        return "redirect:/users/admin-dashboard";
    }

    @PostMapping("/{id}/sold")
    public String markItemAsSold(@PathVariable UUID id, @AuthenticationPrincipal AuthenticationMetadata metadata) {

        itemService.markItemAsSold(id);

        return "redirect:/items/" + metadata.getUserId() + "/added-items";
    }
}
