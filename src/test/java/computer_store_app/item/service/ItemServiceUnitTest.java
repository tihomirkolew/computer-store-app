package computer_store_app.item.service;

import computer_store_app.user.model.User;
import computer_store_app.user.model.UserRole;
import computer_store_app.item.model.Item;
import computer_store_app.item.model.ItemCondition;
import computer_store_app.item.model.ItemType;
import computer_store_app.item.repository.ItemRepository;
import computer_store_app.web.dto.NewItemRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceUnitTest {

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemService itemService;

    @Test
    void givenNewItemRequestAndAdminClient_whenAddNewItem_thenItemIsAuthorized() {

        // Given
        User user = User.builder().id(UUID.randomUUID()).role(UserRole.ADMIN).build();
        NewItemRequest newItemRequest = NewItemRequest.builder()
                .brand("Brand")
                .model("Model")
                .price(BigDecimal.valueOf(100.0))
                .imageUrl("https://example.com/image.jpg")
                .description("Description")
                .type(ItemType.GPU)
                .itemCondition(ItemCondition.NEW)
                .build();

        Item item1 = Item.builder()
                .id(UUID.randomUUID())
                .brand("Brand")
                .model("Model")
                .price(BigDecimal.valueOf(100.0))
                .description("Description")
                .build();
        when(itemRepository.save(any(Item.class))).thenReturn(item1);

        // When
        Item result = itemService.addNewItem(newItemRequest, user);

        // Then
        assertNotNull(result);
        assertEquals("Brand", result.getBrand());
        assertEquals("Model", result.getModel());
        assertEquals(BigDecimal.valueOf(100.0), result.getPrice());
        assertEquals("https://example.com/image.jpg", result.getImageUrl());
        assertEquals("Description", result.getDescription());
        assertTrue(result.isAuthorized());
        assertFalse(result.isSold());
        verify(itemRepository, times(1)).save(any(Item.class));
    }

    @Test
    void givenEmptyImageUrlInNewItemRequest_whenAddNewItem_thenSetDefaultImageUrl() {
        // Given
        User user = User.builder()
                .id(UUID.randomUUID())
                .role(UserRole.CLIENT)
                .build();

        NewItemRequest newItemRequest = NewItemRequest.builder()
                .brand("Brand")
                .model("Model")
                .price(BigDecimal.valueOf(100))
                .imageUrl("")
                .description("Description")
                .type(ItemType.GPU)
                .itemCondition(ItemCondition.NEW)
                .build();

        String expectedDefaultImageUrl = "https://odoo-community.org/web/image/product.product/19823/image_1024/Default%20Product%20Images?unique=d7e15ed";

        when(itemRepository.save(any(Item.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Item newItem = itemService.addNewItem(newItemRequest, user);

        // Then
        assertNotNull(newItem);
        assertEquals(expectedDefaultImageUrl, newItem.getImageUrl());
        verify(itemRepository, times(1)).save(newItem);
    }


    @Test
    void givenItemsInRepository_whenGetAuthorizedNotSoldAndNotArchivedItems_thenReturnMatchingItemsOrdered() {
        // Given
        Item item1 = Item.builder()
                .sold(false)
                .authorized(true)
                .archived(false)
                .addedOn(LocalDateTime.now().minusDays(1))
                .build();

        Item item2 = Item.builder()
                .sold(false)
                .authorized(true)
                .archived(false)
                .addedOn(LocalDateTime.now())
                .build();

        Item item3 = Item.builder()
                .sold(true)
                .authorized(true)
                .archived(false)
                .addedOn(LocalDateTime.now().minusDays(2))
                .build();

        Item item4 = Item.builder()
                .sold(false)
                .authorized(false)
                .archived(false)
                .addedOn(LocalDateTime.now().minusHours(12))
                .build();

        when(itemRepository.findAllBySoldFalseAndAuthorizedTrueAndArchivedFalseOrderByAddedOnDesc())
                .thenReturn(List.of(item2, item1));

        // When
        List<Item> result = itemService.getAuthorizedNotSoldAndNotArchivedItems();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(item2, result.get(0));
        assertEquals(item1, result.get(1));
        verify(itemRepository, times(1))
                .findAllBySoldFalseAndAuthorizedTrueAndArchivedFalseOrderByAddedOnDesc();
    }

    @Test
    void givenItemsInRepository_whenGetAllNotArchivedItemsSortedByIsAuthorized_thenReturnMatchingItemsOrdered() {
        // Given
        Item item1 = Item.builder()
                .archived(false)
                .authorized(false)
                .addedOn(LocalDateTime.now())
                .build();

        Item item2 = Item.builder()
                .archived(false)
                .authorized(true)
                .addedOn(LocalDateTime.now().minusDays(1))
                .build();

        Item item3 = Item.builder()
                .archived(true)
                .authorized(true)
                .addedOn(LocalDateTime.now().minusDays(2))
                .build();

        when(itemRepository.findAllByArchivedFalseOrderByAuthorized())
                .thenReturn(List.of(item1, item2));

        // When
        List<Item> result = itemService.getAllNotArchivedItemsSortedByIsAuthorized();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(item1, result.get(0));
        assertEquals(item2, result.get(1));
        verify(itemRepository, times(1)).findAllByArchivedFalseOrderByAuthorized();
    }

    @Test
    void givenNewItemRequestAndNonAdminClient_whenAddNewItem_thenItemIsNotAuthorized() {

        // Given
        User user = User.builder().id(UUID.randomUUID()).role(UserRole.CLIENT).build();
        NewItemRequest newItemRequest = NewItemRequest.builder()
                .brand("Brand")
                .model("Model")
                .price(BigDecimal.valueOf(100.0))
                .imageUrl("https://example.com/image.jpg")
                .description("Description")
                .type(ItemType.GPU)
                .itemCondition(ItemCondition.NEW)
                .build();

        Item item1 = Item.builder()
                .id(UUID.randomUUID())
                .brand("Brand")
                .model("Model")
                .price(BigDecimal.valueOf(100.0))
                .description("Description")
                .build();
        when(itemRepository.save(any(Item.class))).thenReturn(item1);

        // When
        Item result = itemService.addNewItem(newItemRequest, user);

        // Then
        assertNotNull(result);
        assertFalse(result.isAuthorized());
        verify(itemRepository, times(1)).save(any(Item.class));
    }

    @Test
    void givenNewItemAndClientAdmin_whenApproveItem_thenItemIsApproved() {

        // Given
        User user = User.builder().id(UUID.randomUUID()).role(UserRole.ADMIN).build();
        Item item1 = Item.builder()
                .id(UUID.randomUUID())
                .brand("Brand")
                .model("Model")
                .price(BigDecimal.valueOf(100.0))
                .description("Description")
                .authorized(false)
                .build();
        when(itemRepository.findById(item1.getId())).thenReturn(Optional.of(item1));

        // When
        itemService.approveItem(item1.getId(), user);

        // Then
        assertTrue(item1.isAuthorized());
        verify(itemRepository, times(1)).findById(item1.getId());
        verify(itemRepository, times(1)).save(item1);
    }

    @Test
    void givenNewItemAndClientNotAdmin_whenApproveItem_thenItemIsNotApproved() {

        // Given
        User user = User.builder().id(UUID.randomUUID()).role(UserRole.CLIENT).build();
        Item item1 = Item.builder()
                .id(UUID.randomUUID())
                .brand("Brand")
                .model("Model")
                .price(BigDecimal.valueOf(100.0))
                .description("Description")
                .authorized(false)
                .build();
        lenient().when(itemRepository.findById(item1.getId())).thenReturn(Optional.of(item1));

        // When
        assertThrows(IllegalArgumentException.class, () -> {
            itemService.approveItem(item1.getId(), user);
        });

        // Then
        assertFalse(item1.isAuthorized());
        verify(itemRepository, never()).findById(any());
        verify(itemRepository, never()).save(any());
    }

    // getAuthorizedAndNotSoldAndNotArchivedItemsOrderedByCreatedOn
    @Test
    void givenAuthorizedNotSoldAndNotArchivedItems_whenRetrieved_thenItemsAreOrderedByAddedOn () {

        // Given
        Item item1 = Item.builder()
                .id(UUID.randomUUID())
                .brand("Brand 1")
                .model("Model 1")
                .price(BigDecimal.valueOf(100.0))
                .description("Description")
                .sold(false)
                .authorized(true)
                .archived(false)
                .addedOn(LocalDateTime.now().minusDays(2))
                .build();
        Item item2 = Item.builder()
                .id(UUID.randomUUID())
                .brand("Brand 2")
                .model("Model 2")
                .price(BigDecimal.valueOf(100.0))
                .description("Description")
                .sold(false)
                .authorized(true)
                .archived(false)
                .addedOn(LocalDateTime.now())
                .build();
        when(itemRepository.findAllBySoldFalseAndAuthorizedTrueAndArchivedFalseOrderByAddedOnDesc())
                .thenReturn(List.of(item1, item2).stream().sorted(Comparator.comparing(Item::getAddedOn)).toList());

        // When
        List<Item> wantedItems = itemService.getLastThreeAuthorizedNotSoldAndNotArchivedItemsOrderedByAddedOn();

        // Then
        assertEquals(2, wantedItems.size());
        assertEquals(item1, wantedItems.get(0));
        assertEquals(item2, wantedItems.get(1));
        verify(itemRepository, times(1))
                .findAllBySoldFalseAndAuthorizedTrueAndArchivedFalseOrderByAddedOnDesc();

    }

    // getItemsByUserIdNotArchived
    @Test
    void givenUserId_whenGetItemsByUserId_thenReturnNotArchivedItemList() {

        // Given
        UUID userId = UUID.randomUUID();
        User user = User.builder().id(userId).role(UserRole.ADMIN).build();
        Item item1 = Item.builder()
                .id(UUID.randomUUID())
                .owner(user)
                .brand("Brand")
                .model("Model")
                .price(BigDecimal.valueOf(1))
                .description("Description")
                .sold(false)
                .build();
        Item item2 = Item.builder()
                .id(UUID.randomUUID())
                .owner(user)
                .brand("Brand 2")
                .model("Model 2")
                .price(BigDecimal.valueOf(2))
                .description("Description")
                .sold(false)
                .build();
        when(itemRepository.getItemsByOwnerId(userId)).thenReturn(List.of(item1, item2));

        // When
        List<Item> itemsByUserId = itemService.getItemsByUserIdNotArchived(userId);

        // Then
        assertEquals(2, itemsByUserId.size());
        assertEquals(item1, itemsByUserId.getFirst());
        assertEquals(item2, itemsByUserId.get(1));
        verify(itemRepository, times(1)).getItemsByOwnerId(userId);

    }

    // archiveItemById
    @Test
    void givenItem_whenArchiveItemById_thenArchiveItem() {

        // Given
        Item item1 = Item.builder()
                .id(UUID.randomUUID())
                .brand("Brand")
                .model("Model")
                .price(BigDecimal.valueOf(1))
                .description("Description")
                .archived(false)
                .build();
        when(itemRepository.findById(item1.getId())).thenReturn(Optional.of(item1));

        // When
        itemService.archiveItemById(item1.getId());

        // Then
        assertTrue(item1.isArchived());
        verify(itemRepository, times(1)).save(item1);
    }

    // markItemAsSold
    @Test
    void givenNotSoldItem_whenMarkItemAsSold_thenItemIsSoldTrue() {

        // Given
        Item item1 = Item.builder()
                .id(UUID.randomUUID())
                .brand("Brand")
                .model("Model")
                .price(BigDecimal.valueOf(100.0))
                .description("Description")
                .sold(false)
                .build();
        lenient().when(itemRepository.findById(item1.getId())).thenReturn(Optional.of(item1));

        // When
        itemService.markItemAsSold(item1.getId());

        // Then
        assertTrue(item1.isSold());
        verify(itemRepository, times(1)).findById(item1.getId());
        verify(itemRepository, times(1)).save(item1);
    }

    @Test
    void givenSoldItem_whenMarkItemAsSold_thenItemRemainsSold() {

        // Given
        Item item1 = Item.builder()
                .id(UUID.randomUUID())
                .brand("Brand")
                .model("Model")
                .price(BigDecimal.valueOf(100.0))
                .description("Description")
                .sold(true)
                .build();
        lenient().when(itemRepository.findById(item1.getId())).thenReturn(Optional.of(item1));

        // When
        itemService.markItemAsSold(item1.getId());

        // Then
        assertTrue(item1.isSold());
        verify(itemRepository, times(1)).findById(item1.getId());
        verify(itemRepository, never()).save(item1);
    }
}
