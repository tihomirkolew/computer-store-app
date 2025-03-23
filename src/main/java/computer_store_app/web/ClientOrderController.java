package computer_store_app.web;

import computer_store_app.cart.model.Cart;
import computer_store_app.item.model.Item;
import computer_store_app.order.model.ClientOrder;
import computer_store_app.order.service.ClientOrderService;
import computer_store_app.security.AuthenticationMetadata;
import computer_store_app.user.model.User;
import computer_store_app.user.repository.UserRepository;
import computer_store_app.user.service.UserService;
import computer_store_app.web.dto.OrderRequest;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/orders")
public class ClientOrderController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final ClientOrderService clientOrderService;

    private final BigDecimal STANDARD_SHIPPING_FEE = BigDecimal.valueOf(5);


    public ClientOrderController(UserService userService, UserRepository userRepository, ClientOrderService clientOrderService) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.clientOrderService = clientOrderService;
    }

    @PostMapping("/create")
    public ModelAndView createOrder(@Valid OrderRequest orderRequest, BindingResult bindingResult,
                                    @AuthenticationPrincipal AuthenticationMetadata metadata) {

        UUID userId = metadata.getUserId();
        User user = userService.getById(userId);
        Cart cart = user.getCart();
        List<Item> cartItems = cart.getCartItems();

        if (user.getFirstName() == null && orderRequest.getFirstName() != null) {
            user.setFirstName(orderRequest.getFirstName());
        }
        if (user.getLastName() == null && orderRequest.getLastName() != null) {
            user.setLastName(orderRequest.getLastName());
        }
        if (user.getPhoneNumber() == null && orderRequest.getCustomerPhoneNumber() != null) {
            user.setPhoneNumber(orderRequest.getCustomerPhoneNumber());
        }

        userRepository.save(user);

        BigDecimal cartSubtotal = cart.getCartAmount();
        BigDecimal cartTotalPrice = cartSubtotal.add(STANDARD_SHIPPING_FEE);

        if (bindingResult.hasErrors()) {
            ModelAndView modelAndView = new ModelAndView("cart");
            modelAndView.addObject("cartItems", cartItems);
            modelAndView.addObject("cartSubtotal", cartSubtotal);
            modelAndView.addObject("standardShippingFee", STANDARD_SHIPPING_FEE);
            modelAndView.addObject("cartTotalPrice", cartTotalPrice);
            modelAndView.addObject("orderRequest", orderRequest);
            return modelAndView;
        }

        ClientOrder clientOrder = clientOrderService.createOrderFromCart(orderRequest, userId);

        return new ModelAndView("redirect:/orders/" + clientOrder.getId());
    }

    @GetMapping("/{id}")
    public ModelAndView getOrderById(@PathVariable UUID id) {

        ClientOrder clientOrder = clientOrderService.getOrderById(id);

        ModelAndView modelAndView = new ModelAndView("order");
        modelAndView.addObject("clientOrder", clientOrder);

        return modelAndView;
    }
}
