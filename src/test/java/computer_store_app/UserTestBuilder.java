package computer_store_app;

import computer_store_app.cart.model.Cart;
import computer_store_app.customerOrder.model.CustomerOrder;
import computer_store_app.item.model.Item;
import computer_store_app.item.model.ItemCondition;
import computer_store_app.item.model.ItemType;
import computer_store_app.user.model.User;
import computer_store_app.user.model.UserRole;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@UtilityClass
public class UserTestBuilder {

    public static User aRandomUser() {

        User user = User.builder()
                .id(UUID.randomUUID())
                .username("TestUser123")
                .firstName("John")
                .lastName("Doe")
                .email("testuser123@example.com")
                .password("password123")
                .role(UserRole.CLIENT)
                .build();


        Cart cart = Cart.builder()
                .id(UUID.randomUUID())
                .owner(user)
                .cartItems(List.of())
                .cartAmount(BigDecimal.ZERO)
                .build();

        user.setCart(cart);

        Item item = Item.builder()
                .id(UUID.randomUUID())
                .owner(user)
                .cart(cart)
                .brand("TestBrand")
                .model("TestModel123")
                .price(BigDecimal.valueOf(499.99))
                .imageUrl("https://example.com/image.jpg")
                .description("Test item description")
                .sold(false)
                .authorized(true)
                .archived(false)
                .type(ItemType.HEADSET)
                .addedOn(LocalDateTime.now())
                .itemCondition(ItemCondition.NEW)
                .build();

        cart.setCartItems(List.of(item));

        CustomerOrder order = CustomerOrder.builder()
                .id(UUID.randomUUID())
                .owner(user)
                .shippingAddress("123 Test Street")
                .billingAddress("456 Test Avenue")
                .customerPhoneNumber("123-456-7890")
                .createdOn(LocalDateTime.now())
                .orderedItems(List.of())
                .orderAmount(BigDecimal.ZERO)
                .build();

        user.setCustomerOrders(List.of(order));

        return user;
    }
}
