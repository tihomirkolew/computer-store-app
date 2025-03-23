package computer_store_app.item.service;

import computer_store_app.item.model.Item;
import computer_store_app.item.model.ItemCondition;
import computer_store_app.item.model.ItemType;
import computer_store_app.item.repository.ItemRepository;
import computer_store_app.client.model.Client;
import computer_store_app.web.dto.NewItemRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemService itemService;

    @Test
    void testAddNewItem() {
        // Given
        Client mockClient = Client.builder().id(UUID.randomUUID()).build();
        NewItemRequest newItemRequest = NewItemRequest.builder()
                .brand("Brand")
                .model("Model")
                .price(BigDecimal.valueOf(100.0))
                .imageUrl("http://example.com/image.jpg")
                .description("Description")
                .type(ItemType.GPU)
                .itemCondition(ItemCondition.NEW)
                .build();

        Item mockItem = Item.builder()
                .id(UUID.randomUUID())
                .brand("Brand")
                .model("Model")
                .price(BigDecimal.valueOf(100.0))
                .description("Description")
                .build();
        when(itemRepository.save(any(Item.class))).thenReturn(mockItem);

        // When
        Item result = itemService.addNewItem(newItemRequest, mockClient);

        // Then
        assertNotNull(result);
        assertEquals("Brand", result.getBrand());
        verify(itemRepository, times(1)).save(any(Item.class));
    }

}
