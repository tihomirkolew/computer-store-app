package computer_store_app.cart.service;

import computer_store_app.cart.model.Cart;
import computer_store_app.cart.repository.CartRepository;
import computer_store_app.item.model.Item;
import computer_store_app.item.repository.ItemRepository;
import computer_store_app.item.service.ItemService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class CartUnitTest {

    @Mock
    private CartRepository cartRepository;
    @Mock
    private ItemService itemService;
    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private CartService cartService;

    @Test
    void givenCartAndItemId_whenAddItemToCart_thenSaveItemAndCart () {

        // Given

        Cart cart = Cart.builder()
                .cartAmount(BigDecimal.ZERO)
                .cartItems(new ArrayList<>())
                .build();

        UUID itemId = UUID.randomUUID();
        Item item1 = Item.builder()
                .id(itemId)
                .price(BigDecimal.valueOf(5))
                .sold(false)
                .build();

        when(itemService.getItemById(itemId)).thenReturn(item1);

        // When
        cartService.addItemToCart(cart, itemId);

        // Then
        assertEquals(1, cart.getCartItems().size());
        assertEquals(item1, cart.getCartItems().getFirst());
        assertEquals(item1.getPrice(), cart.getCartItems().getFirst().getPrice());
        verify(cartRepository, times(1)).save(cart);
        verify(itemRepository, times(1)).save(item1);
    }

    // Given
    // When
    // Then
}
