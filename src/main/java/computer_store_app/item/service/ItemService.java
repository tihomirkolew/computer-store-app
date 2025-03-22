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

    private final String DEFAULT_IMG_SRC = "https://odoo-community.org/web/image/product.product/19823/image_1024/Default%20Product%20Images?unique=d7e15ed";

    @Autowired
    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public Item addNewItem(@Valid NewItemRequest newItemRequest, User user) {

        String imageUrl;
        if (newItemRequest.getImageUrl().isEmpty() || newItemRequest.getImageUrl().isBlank()) {
            imageUrl = DEFAULT_IMG_SRC;
        } else {
            imageUrl = newItemRequest.getImageUrl();
        }

        Item newItem = Item.builder()
                .owner(user)
                .brand(newItemRequest.getBrand())
                .model(newItemRequest.getModel())
                .price(newItemRequest.getPrice())
                .imageUrl(imageUrl)
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
