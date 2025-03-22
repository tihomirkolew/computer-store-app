package computer_store_app.web;

import computer_store_app.cart.model.Cart;
import computer_store_app.cart.repository.CartRepository;
import computer_store_app.cart.service.CartService;
import computer_store_app.item.model.Item;
import computer_store_app.item.repository.ItemRepository;
import computer_store_app.item.service.ItemService;
import computer_store_app.security.AuthenticationMetadata;
import computer_store_app.user.model.User;
import computer_store_app.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("cart")
public class CartController {

    private final UserService userService;
    private final CartService cartService;
    private final ItemService itemService;
    private final CartRepository cartRepository;
    private final ItemRepository itemRepository;

    @Autowired
    public CartController(UserService userService, CartService cartService, ItemService itemService, CartRepository cartRepository, ItemRepository itemRepository) {
        this.userService = userService;
        this.cartService = cartService;
        this.itemService = itemService;
        this.cartRepository = cartRepository;
        this.itemRepository = itemRepository;
    }

    // todo
    // get view to show
    @GetMapping()
    public ModelAndView getCart(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {

        User user = userService.getById(authenticationMetadata.getUserId());
        UUID cartId = user.getCart().getId();
        Cart currentUserCart = cartService.getById(cartId);
        List<Item> cartItems = currentUserCart.getItems();

        BigDecimal cartSubtotal = currentUserCart.getCartAmount();
        BigDecimal standardShippingFee = BigDecimal.valueOf(5);
        BigDecimal cartTotalPrice = cartSubtotal.add(standardShippingFee);

        ModelAndView modelAndView = new ModelAndView("cart");
        modelAndView.addObject("cartItems", cartItems);
        modelAndView.addObject("cartSubtotal", cartSubtotal);
        modelAndView.addObject("standardShippingFee", standardShippingFee);
        modelAndView.addObject("cartTotalPrice", cartTotalPrice);

        return modelAndView;
    }

    @PostMapping("/add/{itemId}")
    public String addItemToCart(
            @PathVariable UUID itemId,
            @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {


        User user = userService.getById(authenticationMetadata.getUserId());
        Cart cart = user.getCart();

        cartService.addItemToCart(cart, itemId);

        return "redirect:/cart";
    }

    // ability to remove items
    @DeleteMapping("/remove/{id}")
    public String removeCartItem(@PathVariable UUID id, @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {

        cartService.removeItemFromUserCart(id, authenticationMetadata.getUserId());

        return "redirect:/cart";
    }

    // something to make order think about it
}
