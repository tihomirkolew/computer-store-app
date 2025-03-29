package computer_store_app.item.service;

import computer_store_app.item.model.Item;
import computer_store_app.item.repository.ItemRepository;
import computer_store_app.client.model.Client;
import computer_store_app.client.model.ClientRole;
import computer_store_app.web.dto.NewItemRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class ItemService {


    private final ItemRepository itemRepository;

    private final String DEFAULT_IMG_SRC =
            "https://odoo-community.org/web/image/product.product/19823/image_1024/Default%20Product%20Images?unique=d7e15ed";

    @Autowired
    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public Item addNewItem(@Valid NewItemRequest newItemRequest, Client client) {

        String imageUrl;
        if (newItemRequest.getImageUrl().isEmpty() || newItemRequest.getImageUrl().isBlank()) {
            imageUrl = DEFAULT_IMG_SRC;
        } else {
            imageUrl = newItemRequest.getImageUrl();
        }

        Item newItem = Item.builder()
                .owner(client)
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

        if (client.getRole().equals(ClientRole.ADMIN)) {
            newItem.setAuthorized(true);
        }

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

    public void approveItem(UUID id, Client client) {

        if (!client.getRole().equals(ClientRole.ADMIN)) {
            throw new IllegalArgumentException("User with id [%s] not authorized.".formatted(client.getId()));
        }

        Item itemById = getItemById(id);

        itemById.setAuthorized(!itemById.isAuthorized());

        itemRepository.save(itemById);
        log.info("Item with id [%s] approved for sale.".formatted(id));
    }

    public List<Item> getItemsByUserId(UUID id) {

        return itemRepository.getItemsByOwnerId(id);
    }

    public void deleteItemById(UUID itemId){

        itemRepository.deleteById(itemId);
    }

    public void markItemAsSold(UUID id) {

        Item itemById = getItemById(id);

        if (!itemById.isSold()) {
            itemById.setSold(true);
        }

        itemRepository.save(itemById);
    }
}
