package computer_store_app.item.service;

import computer_store_app.item.model.Item;
import computer_store_app.item.repository.ItemRepository;
import computer_store_app.user.model.User;
import computer_store_app.user.service.UserService;
import computer_store_app.web.dto.NewItemRequest;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;

@Service
public class ItemService {


    private final ItemRepository itemRepository;
    private final UserService userService;

    public ItemService(ItemRepository itemRepository, UserService userService) {
        this.itemRepository = itemRepository;
        this.userService = userService;
    }

    public Item addNewItem(@Valid NewItemRequest newItemRequest, User user) {

        Item newItem = Item.builder()
                .owner(user)
                .brand(newItemRequest.getBrand())
                .model(newItemRequest.getModel())
                .price(newItemRequest.getPrice())
                .imageUrl(newItemRequest.getImageUrl())
                .description(newItemRequest.getDescription())
                .type(newItemRequest.getType())
                .itemCondition(newItemRequest.getItemCondition())
                .build();

        itemRepository.save(newItem);

        return newItem;
    }
}
