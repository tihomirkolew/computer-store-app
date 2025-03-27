package computer_store_app.web;

import computer_store_app.client.model.Client;
import computer_store_app.item.model.Item;
import computer_store_app.item.service.ItemService;
import computer_store_app.security.AuthenticationMetadata;
import computer_store_app.web.dto.NewItemRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import computer_store_app.client.service.ClientService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("items")
public class ItemController {

    private final ItemService itemService;
    private final ClientService clientService;

    @Autowired
    public ItemController(ItemService itemService, ClientService clientService) {
        this.itemService = itemService;
        this.clientService = clientService;
        System.out.println("ClientService initialized: " + (clientService != null));
    }

    @GetMapping("/items-list")
    public ModelAndView getAllItemsPage() {

        List<Item> authorizedAndNotSoldItems = itemService.getAuthorizedAndNotSoldItemsOrderedByUpdatedOn();

        ModelAndView modelAndView = new ModelAndView("items-list");
        modelAndView.addObject("authorizedAndNotSoldItems", authorizedAndNotSoldItems);

        return modelAndView;
    }

    @GetMapping("/{id}/added-items")
    public ModelAndView getCurrentUserItemsPage(@PathVariable UUID id) {

        List<Item> itemsByUserId = itemService.getItemsByUserId(id);

        ModelAndView modelAndView = new ModelAndView("user-items");
        modelAndView.addObject("itemsByUserId", itemsByUserId);

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

        Client client = clientService.getById(userId);

        itemService.addNewItem(newItemRequest, client);

        return "redirect:/home";
    }

    @GetMapping("/{id}")
    public ModelAndView getItemDetails(@PathVariable UUID id) {

        Item item = itemService.getItemById(id);

        ModelAndView modelAndView = new ModelAndView("item-details");
        modelAndView.addObject("item", item);

        return modelAndView;
    }

    @PutMapping("/{id}/approve")
    public String approveItem(@PathVariable UUID id, @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata){

        UUID clientId = authenticationMetadata.getUserId();
        Client client = clientService.getById(clientId);

        itemService.approveItem(id, client);

        return "redirect:/users/admin-dashboard";
    }
}
