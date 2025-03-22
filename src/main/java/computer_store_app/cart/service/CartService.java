package computer_store_app.cart.service;

import computer_store_app.cart.model.Cart;
import computer_store_app.cart.repository.CartRepository;
import computer_store_app.item.model.Item;
import computer_store_app.item.repository.ItemRepository;
import computer_store_app.item.service.ItemService;
import computer_store_app.user.model.User;
import computer_store_app.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class CartService {

    private final CartRepository cartRepository;
    private final ItemService itemService;
    private final ItemRepository itemRepository;

    @Autowired
    public CartService(CartRepository cartRepository, ItemService itemService, ItemRepository itemRepository) {
        this.cartRepository = cartRepository;
        this.itemService = itemService;
        this.itemRepository = itemRepository;
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

        if (item.isSold()) {
            throw new IllegalArgumentException("Item does not exist or is already sold.");
        }

        item.setCart(cart);
        cart.getItems().add(item);

        cart.setCartAmount(cart.getItems().stream()
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

        if (!cart.getItems().contains(itemToRemove)) {
            throw new IllegalArgumentException("Item is not present in the user's cart.");
        }

        itemToRemove.setCart(null);

        cartRepository.save(cart);
        itemRepository.save(itemToRemove);

        log.info("Item with ID [%s] successfully removed from cart with ID [%s].".formatted(itemId, cart.getId()));
    }
}
