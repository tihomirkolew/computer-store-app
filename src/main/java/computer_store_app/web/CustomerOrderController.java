package computer_store_app.web;

import computer_store_app.cart.model.Cart;
import computer_store_app.client.model.Client;
import computer_store_app.customerOrder.model.CustomerOrder;
import computer_store_app.item.model.Item;
import computer_store_app.customerOrder.service.CustomerOrderService;
import computer_store_app.security.AuthenticationMetadata;
import computer_store_app.client.repository.ClientRepository;
import computer_store_app.client.service.ClientService;
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
public class CustomerOrderController {

    private final ClientService clientService;
    private final ClientRepository clientRepository;
    private final CustomerOrderService customerOrderService;

    private final BigDecimal STANDARD_SHIPPING_FEE = BigDecimal.valueOf(5);


    public CustomerOrderController(ClientService clientService, ClientRepository clientRepository, CustomerOrderService customerOrderService) {
        this.clientService = clientService;
        this.clientRepository = clientRepository;
        this.customerOrderService = customerOrderService;
    }

    @PostMapping("/create")
    public ModelAndView createOrder(@Valid OrderRequest orderRequest, BindingResult bindingResult,
                                    @AuthenticationPrincipal AuthenticationMetadata metadata) {

        UUID userId = metadata.getUserId();
        Client client = clientService.getById(userId);
        Cart cart = client.getCart();
        List<Item> cartItems = cart.getCartItems();

        if (client.getFirstName() == null && orderRequest.getFirstName() != null) {
            client.setFirstName(orderRequest.getFirstName());
        }
        if (client.getLastName() == null && orderRequest.getLastName() != null) {
            client.setLastName(orderRequest.getLastName());
        }
        if (client.getPhoneNumber() == null && orderRequest.getCustomerPhoneNumber() != null) {
            client.setPhoneNumber(orderRequest.getCustomerPhoneNumber());
        }

        clientRepository.save(client);

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

        CustomerOrder customerOrder = customerOrderService.createOrderFromCart(orderRequest, userId);

        return new ModelAndView("redirect:/orders/" + customerOrder.getId());
    }

    @GetMapping("/{id}")
    public ModelAndView getOrderById(@PathVariable UUID id) {

        CustomerOrder customerOrder = customerOrderService.getOrderById(id);

        ModelAndView modelAndView = new ModelAndView("order");
        modelAndView.addObject("customerOrder", customerOrder);

        return modelAndView;
    }
}
