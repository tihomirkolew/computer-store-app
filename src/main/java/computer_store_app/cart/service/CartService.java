package computer_store_app.cart.service;

import computer_store_app.cart.model.Cart;
import computer_store_app.cart.repository.CartRepository;
import computer_store_app.item.model.Item;
import computer_store_app.item.repository.ItemRepository;
import computer_store_app.item.service.ItemService;
import computer_store_app.order.model.CustomerOrder;
import computer_store_app.order.repository.OrderRepository;
import computer_store_app.user.model.User;
import computer_store_app.web.dto.OrderRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class CartService {

    private final CartRepository cartRepository;
    private final ItemService itemService;
    private final ItemRepository itemRepository;
    private final OrderRepository orderRepository;

    @Autowired
    public CartService(CartRepository cartRepository, ItemService itemService, ItemRepository itemRepository, OrderRepository orderRepository) {
        this.cartRepository = cartRepository;
        this.itemService = itemService;
        this.itemRepository = itemRepository;
        this.orderRepository = orderRepository;
    }

    public Cart createEmptyCart(User user) {

        Cart initializeCart = Cart.builder()
                .owner(user)
                .cartAmount(BigDecimal.ZERO)
                .build();

        Cart cart = cartRepository.save(initializeCart);

        log.info("Successfully created cart with id [%s] for user with id [%s]."
                .formatted(cart.getId(), user.getId()));

        return cart;
    }

    public void addItemToCart(Cart cart, UUID itemId) {
        Item item = itemService.getItemById(itemId);

        if (item.isSold() || item.getCustomerOrder() != null) {
            throw new IllegalArgumentException("Item does not exist or is already sold.");
        }

        item.setCart(cart);
        cart.getCartItems().add(item);

        cart.setCartAmount(cart.getCartItems().stream()
                .map(Item::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add));

        cartRepository.save(cart);
        itemRepository.save(item);
    }

    public Cart getById(UUID cartId) {

        return cartRepository.findById(cartId).orElseThrow(() -> new IllegalArgumentException("There is problem with the cart"));
    }

    public void removeItemFromUserCart(UUID itemId, UUID userId) {
        Optional<Cart> cartByOwnerId = cartRepository.getCartByOwnerId(userId);

        if (cartByOwnerId.isEmpty()) {
            throw new RuntimeException("There is no cart for user with id [%s]".formatted(userId));
        }

        Cart cart = cartByOwnerId.get();
        Item itemToRemove = itemService.getItemById(itemId);

        if (!cart.getCartItems().contains(itemToRemove)) {
            throw new IllegalArgumentException("Item is not present in the user's cart.");
        }

        // Remove the item and update the total
        cart.getCartItems().remove(itemToRemove);
        cart.setCartAmount(cart.getCartItems().stream()
                .map(Item::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add));

        itemToRemove.setCart(null);

        cartRepository.save(cart);
        itemRepository.save(itemToRemove);

        log.info("Item with ID [%s] successfully removed from cart with ID [%s].".formatted(itemId, cart.getId()));
    }


    public Cart getByUserId(UUID userId) {

        return cartRepository.getCartByOwnerId(userId).orElseThrow(() -> new IllegalArgumentException("Cart does not exist"));
    }

    public CustomerOrder finalizeOrder(OrderRequest orderRequest, User user) {
        Cart cartByUserId = getByUserId(user.getId());

        CustomerOrder order = CustomerOrder.builder()
                .owner(user)
                .shippingAddress(orderRequest.getShippingAddress())
                .billingAddress(orderRequest.getBillingAddress())
                .createdOn(LocalDateTime.now())
                .orderedItems(cartByUserId.getCartItems())
                .orderAmount(cartByUserId.getCartAmount())
                .build();

        log.info("Creating order: {}", order);

        CustomerOrder savedOrder = orderRepository.save(order);
        log.info("Order saved with ID: {}", savedOrder.getId());

        cartByUserId.getCartItems().clear();
        cartByUserId.setCartAmount(BigDecimal.ZERO);
        cartRepository.save(cartByUserId);

        return savedOrder;
    }
}
