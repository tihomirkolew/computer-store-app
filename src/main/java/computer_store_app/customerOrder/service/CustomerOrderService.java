package computer_store_app.customerOrder.service;

import computer_store_app.OrderItem.model.OrderItem;
import computer_store_app.OrderItem.repository.OrderItemRepository;
import computer_store_app.cart.model.Cart;
import computer_store_app.customerOrder.model.CustomerOrder;
import computer_store_app.item.model.Item;
import computer_store_app.customerOrder.repository.CustomerOrderRepository;
import computer_store_app.user.service.UserService;
import computer_store_app.web.dto.OrderRequest;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class CustomerOrderService {

    private final BigDecimal STANDARD_SHIPPING_FEE = BigDecimal.valueOf(5);


    private final CustomerOrderRepository customerOrderRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserService userService;

    @Autowired
    public CustomerOrderService(CustomerOrderRepository customerOrderRepository, OrderItemRepository orderItemRepository, UserService userService) {
        this.customerOrderRepository = customerOrderRepository;
        this.orderItemRepository = orderItemRepository;
        this.userService = userService;
    }

    public CustomerOrder getOrderById(UUID orderId) {

        return customerOrderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order with id [%s] does not exist".formatted(orderId)));
    }

    @Transactional
    public CustomerOrder createOrderFromCart(OrderRequest orderRequest, UUID userId) {


        Cart cart = userService.getById(userId).getCart();

        if (cart.getCartItems().isEmpty()) {
            throw new RuntimeException("Cart is empty!");
        }

        List<OrderItem> itemsToOrder = new ArrayList<>();

        for (Item cartItem : cart.getCartItems()) {
            OrderItem cartItemToOrderItem = OrderItem.builder()
                    .owner(cartItem.getOwner())
                    .brand(cartItem.getBrand())
                    .model(cartItem.getModel())
                    .price(cartItem.getPrice())
                    .imageUrl(cartItem.getImageUrl())
                    .description(cartItem.getDescription())
                    .type(cartItem.getType())
                    .build();

            itemsToOrder.add(cartItemToOrderItem);
            cartItem.setSold(true);
            cartItem.setCart(null);
            orderItemRepository.save(cartItemToOrderItem);
        }

        cart.getCartItems().clear();

        // making the order
        CustomerOrder customerOrder = CustomerOrder.builder()
                .owner(cart.getOwner())
                .shippingAddress(orderRequest.getShippingAddress())
                .billingAddress(orderRequest.getBillingAddress())
                .customerPhoneNumber(orderRequest.getCustomerPhoneNumber())
                .createdOn(LocalDateTime.now())
                .orderedItems(itemsToOrder)
                .orderAmount(cart.getCartAmount().add(STANDARD_SHIPPING_FEE)) // standard shipping fee
                .build();

        for (OrderItem orderItem : itemsToOrder) {
            orderItem.setCustomerOrder(customerOrder);
            orderItemRepository.save(orderItem);
        }

        customerOrderRepository.save(customerOrder);

        return customerOrder;
    }
}
