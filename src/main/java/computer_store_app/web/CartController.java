package computer_store_app.web;

import computer_store_app.cart.model.Cart;
import computer_store_app.cart.service.CartService;
import computer_store_app.item.model.Item;
import computer_store_app.security.AuthenticationMetadata;
import computer_store_app.user.model.User;
import computer_store_app.user.repository.UserRepository;
import computer_store_app.user.service.UserService;
import computer_store_app.web.dto.OrderRequest;
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

    private final BigDecimal STANDARD_SHIPPING_FEE = BigDecimal.valueOf(5);

    @Autowired
    public CartController(UserService userService, CartService cartService) {
        this.userService = userService;
        this.cartService = cartService;
    }

    // todo
    // get view to show
    @GetMapping
    public ModelAndView getCart(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {

        User user = userService.getById(authenticationMetadata.getUserId());
        Cart currentUserCart = user.getCart();

        BigDecimal cartSubtotal = currentUserCart.getCartAmount();
        BigDecimal cartTotalPrice = cartSubtotal.add(STANDARD_SHIPPING_FEE);

        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setFirstName(user.getFirstName());
        orderRequest.setLastName(user.getLastName());
        orderRequest.setCustomerPhoneNumber(user.getPhoneNumber());


        ModelAndView modelAndView = new ModelAndView("cart");
        modelAndView.addObject("cartItems", currentUserCart.getCartItems());
        modelAndView.addObject("cartSubtotal", cartSubtotal);
        modelAndView.addObject("standardShippingFee", STANDARD_SHIPPING_FEE);
        modelAndView.addObject("cartTotalPrice", cartTotalPrice);
        modelAndView.addObject("orderRequest", orderRequest);

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
}


