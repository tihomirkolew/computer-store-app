package computer_store_app.cart.service;

import computer_store_app.cart.model.Cart;
import computer_store_app.cart.repository.CartRepository;
import computer_store_app.user.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@Slf4j
public class CartService {

    private final CartRepository cartRepository;

    public CartService(CartRepository cartRepository) {
        this.cartRepository = cartRepository;
    }

    public Cart createEmptyCart(User user) {

        Cart initializeCart = Cart.builder()
                .owner(user)
                .totalAmount(BigDecimal.ZERO)
                .build();

        Cart cart = cartRepository.save(initializeCart);

        log.info("Successfully created cart with id [%s] for user with id [%s]."
                .formatted(cart.getId(), user.getId()));

        return cart;
    }
}
