//package computer_store_app.web;
//
//import computer_store_app.cart.service.CartService;
//import computer_store_app.order.model.CustomerOrder;
//import computer_store_app.order.service.OrderService;
//import computer_store_app.security.AuthenticationMetadata;
//import computer_store_app.user.model.User;
//import computer_store_app.user.service.UserService;
//import computer_store_app.web.dto.OrderRequest;
//import jakarta.validation.Valid;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.servlet.ModelAndView;
//
//import java.util.UUID;
//
//@Controller
//@RequestMapping("/orders")
//public class OrderController {
//
//    private final CartService cartService;
//    private final OrderService orderService;
//    private final UserService userService;
//
//    @Autowired
//    public OrderController(CartService cartService, OrderService orderService, UserService userService) {
//        this.cartService = cartService;
//        this.orderService = orderService;
//        this.userService = userService;
//    }
//
//    // get view when pushing Proceed to checkout then Order
//    // get order from checkout
//    // from cart go to proceed to checkout(CartController) which gives you a form in which you put billing and shipping info
//    // proceed to checkout contains a button "Order" that creates an order
//
//    @GetMapping("/{orderId}")
//    public ModelAndView getOrder(@PathVariable UUID orderId) {
//        // Retrieve the order details using the orderId
//        CustomerOrder order = orderService.getOrderById(orderId);
//
//        // Prepare the ModelAndView
//        ModelAndView modelAndView = new ModelAndView("order");
//        modelAndView.addObject("order", order); // Pass the order to the view
//
//        return modelAndView;
//    }
//
//
//    @PostMapping("/create")
//    public String createOrder(@Valid OrderRequest orderRequest, @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {
//
//        User user = userService.getById(authenticationMetadata.getUserId());
//
//        CustomerOrder customerOrder = cartService.finalizeOrder(orderRequest, user);
//
//        return "redirect:/orders/" + customerOrder.getId();
//    }
//
//}
////@PostMapping("/order/create")
////public String createOrder(@ModelAttribute CheckoutRequest checkoutRequest, Principal principal) {
////    // Retrieve the current user's cart items
////    List<Item> cartItems = cartService.getCartItems(principal.getName());
////    BigDecimal totalAmount = cartService.calculateTotal(principal.getName());
////
////    // Create and save the CustomerOrder
////    CustomerOrder order = CustomerOrder.builder()
////            .owner(userService.findByUsername(principal.getName()))
////            .shippingAddress(checkoutRequest.getShippingAddress())
////            .billingAddress(checkoutRequest.getBillingAddress())
////            .createdOn(LocalDateTime.now())
////            .items(cartItems)
////            .totalAmount(totalAmount)
////            .status(OrderStatus.PENDING)
////            .build();
////
////    orderService.saveOrder(order);
////
////    // Clear the user's cart
////    cartService.clearCart(principal.getName());
////
////    return "redirect:/order/confirmation";
////}
//
////@GetMapping("/order/confirmation")
////public String getOrderConfirmation(Model model, Principal principal) {
////    CustomerOrder latestOrder = orderService.findLatestOrderForUser(principal.getName());
////    model.addAttribute("order", latestOrder);
////    return "confirmation";
////}
//
////
