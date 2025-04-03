package computer_store_app.item.service;

import computer_store_app.item.model.Item;
import computer_store_app.item.repository.ItemRepository;
import computer_store_app.user.model.User;
import computer_store_app.user.model.UserRole;
import computer_store_app.web.dto.NewItemRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
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
                .archived(false)
                .type(newItemRequest.getType())
                .addedOn(LocalDateTime.now())
                .itemCondition(newItemRequest.getItemCondition())
                .build();

        if (user.getRole().equals(UserRole.ADMIN)) {
            newItem.setAuthorized(true);
        }

        itemRepository.save(newItem);

        if (newItem.isAuthorized()) {
            log.info("Item with id [%s] added and automatically approved for sale.".formatted(newItem.getId()));
        } else {
            log.info("Item with id [%S] added and waiting for approval.".formatted(newItem.getId()));
        }

        return newItem;
    }

    public List<Item> getAllItems() {

        return itemRepository.findAll();
    }

    public List<Item> getAuthorizedNotSoldAndNotArchivedItems() {

        return itemRepository.findAllBySoldFalseAndAuthorizedTrueAndArchivedFalseOrderByAddedOnDesc();
    }

    public List<Item> getLastThreeAuthorizedNotSoldAndNotArchivedItemsOrderedByAddedOn() {

        return itemRepository.findAllBySoldFalseAndAuthorizedTrueAndArchivedFalseOrderByAddedOnDesc()
                .stream().limit(3).toList();
    }

    public Item getItemById(UUID itemId) {

        return itemRepository.findById(itemId).orElseThrow(() -> new IllegalArgumentException("Item not available."));
    }

    public List<Item> getAllNotArchivedItemsSortedByIsAuthorized() {

        return itemRepository.findAllByArchivedFalseOrderByAuthorized();
    }

    public void approveItem(UUID id, User user) {

        if (!user.getRole().equals(UserRole.ADMIN)) {
            throw new IllegalArgumentException("User with id [%s] not authorized.".formatted(user.getId()));
        }

        Item itemById = getItemById(id);

        if (!itemById.isAuthorized()) {
            itemById.setAuthorized(true);
            log.info("Item with id [%s] has been approved for sale.".formatted(id));
        } else {
            itemById.setAuthorized(false);
            log.info("Item with id [%s] has been disapproved.".formatted(id));
        }

        itemRepository.save(itemById);
    }

    public List<Item> getItemsByUserIdNotArchived(UUID id) {

        return itemRepository.getItemsByOwnerId(id).stream()
                .filter(item -> !item.isArchived())
                .sorted(Comparator.comparing(Item::isSold)).toList();
    }

    public void archiveItemById(UUID itemId) {

        Item itemById = getItemById(itemId);

        itemById.setArchived(!itemById.isArchived());
        itemRepository.save(itemById);
        log.info("Item with id [%s] archived.".formatted(itemId));
    }

    public void markItemAsSold(UUID id) {

        Item itemById = getItemById(id);

        if (!itemById.isSold()) {
            itemById.setSold(true);
            itemRepository.save(itemById);
        }
        log.info("Item with id [%s] marked as sold.".formatted(id));
    }
}
