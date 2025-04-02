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
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class CartServiceUnitTest {

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

    @Test
    void givenCartAndItemId_whenAddItemToCart_thenThrowException () {

        // Given

        Cart cart = Cart.builder()
                .cartAmount(BigDecimal.ZERO)
                .cartItems(new ArrayList<>())
                .build();

        UUID itemId = UUID.randomUUID();
        Item item1 = Item.builder()
                .id(itemId)
                .price(BigDecimal.valueOf(5))
                .sold(true)
                .build();

        when(itemService.getItemById(itemId)).thenReturn(item1);

        // When
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            cartService.addItemToCart(cart, itemId);
        });

        // Then
        assertEquals("Item does not exist or is already sold.", exception.getMessage());
        assertTrue(cart.getCartItems().isEmpty()); // Ensure no items were added to the cart
        verify(cartRepository, never()).save(any(Cart.class)); // Verify cart was not saved
        verify(itemRepository, never()).save(any(Item.class)); // Verify item was not saved
    }

    @Test
    void givenCartWithItem_whenRemoveItemFromUserCart_thenUpdateCartAndItem() {
        // Given
        UUID userId = UUID.randomUUID();
        UUID itemId = UUID.randomUUID();

        Item itemToRemove = Item.builder()
                .id(itemId)
                .price(BigDecimal.valueOf(50))
                .build();

        Cart cart = Cart.builder()
                .id(UUID.randomUUID())
                .cartItems(new ArrayList<>(List.of(itemToRemove)))
                .cartAmount(BigDecimal.valueOf(50))
                .build();

        when(cartRepository.getCartByOwnerId(userId)).thenReturn(Optional.of(cart));
        when(itemService.getItemById(itemId)).thenReturn(itemToRemove);

        // When
        cartService.removeItemFromUserCart(itemId, userId);

        // Then
        assertFalse(cart.getCartItems().contains(itemToRemove));
        assertEquals(BigDecimal.ZERO, cart.getCartAmount());
        assertNull(itemToRemove.getCart());
        verify(cartRepository, times(1)).save(cart);
        verify(itemRepository, times(1)).save(itemToRemove);
    }

    @Test
    void givenNoCartForUser_whenRemoveItemFromUserCart_thenThrowRuntimeException() {
        // Given
        UUID userId = UUID.randomUUID();
        UUID itemId = UUID.randomUUID();

        when(cartRepository.getCartByOwnerId(userId)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            cartService.removeItemFromUserCart(itemId, userId);
        });
        assertEquals("There is no cart for user with id [%s]".formatted(userId), exception.getMessage());
        verify(cartRepository, never()).save(any(Cart.class));
        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    void givenItemNotInCart_whenRemoveItemFromUserCart_thenThrowIllegalArgumentException() {
        // Given
        UUID userId = UUID.randomUUID();
        UUID itemId = UUID.randomUUID();

        Item unrelatedItem = Item.builder()
                .id(UUID.randomUUID())
                .price(BigDecimal.valueOf(50))
                .build();

        Cart cart = Cart.builder()
                .id(UUID.randomUUID())
                .cartItems(new ArrayList<>(List.of(unrelatedItem)))
                .cartAmount(BigDecimal.valueOf(50))
                .build();

        when(cartRepository.getCartByOwnerId(userId)).thenReturn(Optional.of(cart));
        when(itemService.getItemById(itemId)).thenReturn(unrelatedItem);

        // When
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            cartService.removeItemFromUserCart(itemId, userId);
        });

        // Then
        assertEquals("Item is not present in the user's cart.", exception.getMessage());
        verify(cartRepository, never()).save(any(Cart.class));
        verify(itemRepository, never()).save(any(Item.class));
    }


    // Given
    // When
    // Then
}
