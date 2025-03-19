package computer_store_app.item.service;

import computer_store_app.item.model.Item;
import computer_store_app.item.repository.ItemRepository;
import computer_store_app.user.model.User;
import computer_store_app.user.service.UserService;
import computer_store_app.web.dto.NewItemRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ItemService {


    private final ItemRepository itemRepository;
    private final UserService userService;

    @Autowired
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
                .sold(false)
                .authorized(false)
                .type(newItemRequest.getType())
                .addedOn(LocalDateTime.now())
                .updatedOn(LocalDateTime.now())
                .itemCondition(newItemRequest.getItemCondition())
                .build();

        itemRepository.save(newItem);

        return newItem;
    }

    public List<Item> getAuthorizedAndNotSoldItemsOrderedByUpdatedOn() {

        return itemRepository.findAllBySoldFalseAndAuthorizedTrueOrderByUpdatedOnDesc();
    }

    public Item getItemById(UUID itemId) {

        return itemRepository.findById(itemId).orElseThrow(() -> new IllegalArgumentException("Item not available."));
    }


    public List<Item> getAllItems() {

        return itemRepository.findAll();
    }

    public void approveItem(UUID id) {

        Item itemById = getItemById(id);

        itemById.setAuthorized(!itemById.isAuthorized());

        itemRepository.save(itemById);
    }

    public List<Item> getItemsByUserId(UUID id) {

        return itemRepository.getItemsByOwnerId(id);
    }
}
