package computer_store_app.web;

import computer_store_app.cart.model.Cart;
import computer_store_app.cart.service.CartService;
import computer_store_app.security.AuthenticationMetadata;
import computer_store_app.client.model.Client;
import computer_store_app.client.service.ClientService;
import computer_store_app.web.dto.OrderRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.math.BigDecimal;
import java.util.UUID;

@Controller
@RequestMapping("cart")
public class CartController {

    private final ClientService clientService;
    private final CartService cartService;

    private final BigDecimal STANDARD_SHIPPING_FEE = BigDecimal.valueOf(5);

    @Autowired
    public CartController(ClientService clientService, CartService cartService) {
        this.clientService = clientService;
        this.cartService = cartService;
    }

    // todo
    // get view to show
    @GetMapping
    public ModelAndView getCart(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {

        Client client = clientService.getById(authenticationMetadata.getUserId());
        Cart currentUserCart = client.getCart();

        BigDecimal cartSubtotal = currentUserCart.getCartAmount();
        BigDecimal cartTotalPrice = cartSubtotal.add(STANDARD_SHIPPING_FEE);

        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setFirstName(client.getFirstName());
        orderRequest.setLastName(client.getLastName());
        orderRequest.setCustomerPhoneNumber(client.getPhoneNumber());


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


        Client client = clientService.getById(authenticationMetadata.getUserId());
        Cart cart = client.getCart();

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


