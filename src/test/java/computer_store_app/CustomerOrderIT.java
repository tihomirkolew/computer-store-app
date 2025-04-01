package computer_store_app;

import computer_store_app.OrderItem.model.OrderItem;
import computer_store_app.cart.model.Cart;
import computer_store_app.user.model.User;
import computer_store_app.user.service.UserService;
import computer_store_app.customerOrder.model.CustomerOrder;
import computer_store_app.customerOrder.service.CustomerOrderService;
import computer_store_app.item.model.Item;
import computer_store_app.item.model.ItemCondition;
import computer_store_app.item.model.ItemType;
import computer_store_app.web.dto.OrderRequest;
import computer_store_app.web.dto.RegisterRequest;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest
@Slf4j
public class CustomerOrderIT {

    @Autowired
    private UserService userService;
    @Autowired
    private CustomerOrderService customerOrderService;

    // what do we need to make an order
    // registered user
    // cart to not be empty
    // built order dto

    @Transactional
    @Test
    void createCustomerOrder_happyPath() {

        // Given
        RegisterRequest registerRequest = RegisterRequest.builder()
                .email("baigosho@gosho.bai")
                .username("bYgosho")
                .password("123123123")
                .build();

        User registerUser = userService.registerUser(registerRequest);
        UUID registerClientId = registerUser.getId();
        log.info("Successfully registered client: {}", registerUser.getUsername());

        Cart cart = registerUser.getCart();
        log.info("Retrieved cart for the client. Cart ID: {}", cart.getId());

        Item item1 = Item.builder()
                .id(UUID.randomUUID())
                .brand("Brand 1")
                .model("Model 1")
                .price(BigDecimal.valueOf(10))
                .imageUrl("http://example.com/image1.jpg")
                .description("High-quality laptop")
                .sold(false)
                .authorized(true)
                .type(ItemType.HEADSET)
                .addedOn(LocalDateTime.now())
                .itemCondition(ItemCondition.USED)
                .owner(registerUser)
                .cart(cart)
                .build();

        Item item2 = Item.builder()
                .id(UUID.randomUUID())
                .brand("Brand 2")
                .model("Model 2")
                .price(BigDecimal.valueOf(20))
                .imageUrl("http://example.com/image2.jpg")
                .description("Powerful desktop")
                .sold(false)
                .authorized(true)
                .type(ItemType.CPU)
                .addedOn(LocalDateTime.now())
                .itemCondition(ItemCondition.NEW)
                .owner(registerUser)
                .cart(cart)
                .build();


        List<Item> itemsForClientCart = new ArrayList<>();
        itemsForClientCart.add(item1);
        itemsForClientCart.add(item2);

        cart.setCartItems(itemsForClientCart);
        cart.setCartAmount(item1.getPrice().add(item2.getPrice()));
        BigDecimal cartAmount = cart.getCartAmount();
        log.info("Items successfully added to the cart. Total items: {}", itemsForClientCart.size());

        OrderRequest orderRequest = OrderRequest.builder()
                .firstName("Gosho")
                .lastName("Goshov")
                .customerPhoneNumber("+359123123123")
                .billingAddress("456 Test Avenue")
                .shippingAddress("123 Test Street")
                .build();
        log.info("Created an OrderRequest: {}", orderRequest);

        // When
        CustomerOrder customerOrder =
                customerOrderService.createOrderFromCart(orderRequest, registerClientId);

        // Then
        assertNotNull(customerOrder);
        assertEquals(registerUser, customerOrder.getOwner());
        assertEquals("+359123123123", customerOrder.getCustomerPhoneNumber()); // phone number
        assertEquals("456 Test Avenue", customerOrder.getBillingAddress()); // billing address
        assertEquals("123 Test Street", customerOrder.getShippingAddress()); // shipping address
        log.info("Cart amount: {}", cart.getCartAmount());
        log.info("Order amount: {}", customerOrder.getOrderAmount());
        assertEquals(cartAmount, item1.getPrice().add(item2.getPrice()));
        log.info("Cart amount - {}, items amount {}",cartAmount, item1.getPrice().add(item2.getPrice()));
        assertEquals(cartAmount.add(BigDecimal.valueOf(5)), customerOrder.getOrderAmount()); // amount, these 5 are standard shipping

        log.info("Order properties validated successfully.");

        List<OrderItem> orderedItems = customerOrder.getOrderedItems();
        assertEquals(2, orderedItems.size());
        assertTrue(orderedItems.stream().anyMatch(item -> item.getModel().equals("Model 1")));
        assertTrue(orderedItems.stream().anyMatch(item -> item.getModel().equals("Model 2")));
        log.info("Verified ordered items in the customer order.");

        assertTrue(cart.getCartItems().isEmpty());
        assertTrue(item1.isSold());
        assertTrue(item2.isSold());
        log.info("Verified that cart is empty and items are marked as sold.");

        log.info("Test createCustomerOrder_happyPath completed successfully.");
    }

    @Test
    void givenEmptyCart_whenCreateOrderFromCart_thenThrowRuntimeException() {

        // Given
        RegisterRequest registerRequest = RegisterRequest.builder()
                .email("emptycart@gosho.bai")
                .username("EmptyCartUser")
                .password("password123")
                .build();

        User registerUser = userService.registerUser(registerRequest);
        UUID registerClientId = registerUser.getId();

        Cart emptyCart = registerUser.getCart();
        emptyCart.setCartItems(new ArrayList<>());
        emptyCart.setCartAmount(BigDecimal.ZERO);

        log.info("Prepared client with an empty cart. Cart ID: {}", emptyCart.getId());

        OrderRequest orderRequest = OrderRequest.builder()
                .firstName("Empty")
                .lastName("Cart")
                .customerPhoneNumber("+359000000000")
                .billingAddress("No Address")
                .shippingAddress("No Address")
                .build();
        log.info("Created an OrderRequest for a client with an empty cart: {}", orderRequest);

        // When
        Exception exception = assertThrows(
                RuntimeException.class,
                () -> customerOrderService.createOrderFromCart(orderRequest, registerClientId)
        );

        // Then
        assertEquals("Cart is empty!", exception.getMessage());
        log.info("Verified RuntimeException is thrown with the correct message for an empty cart.");
    }
}
