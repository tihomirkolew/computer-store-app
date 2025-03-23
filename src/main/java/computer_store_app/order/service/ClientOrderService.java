package computer_store_app.order.service;

import computer_store_app.OrderItem.model.OrderItem;
import computer_store_app.OrderItem.repository.OrderItemRepository;
import computer_store_app.cart.model.Cart;
import computer_store_app.item.model.Item;
import computer_store_app.order.model.ClientOrder;
import computer_store_app.order.repository.ClientOrderRepository;
import computer_store_app.user.service.UserService;
import computer_store_app.web.dto.OrderRequest;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ClientOrderService {


    private final ClientOrderRepository clientOrderRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserService userService;

    @Autowired
    public ClientOrderService(ClientOrderRepository clientOrderRepository, OrderItemRepository orderItemRepository, UserService userService) {
        this.clientOrderRepository = clientOrderRepository;
        this.orderItemRepository = orderItemRepository;
        this.userService = userService;
    }

    public ClientOrder getOrderById(UUID orderId) {

        return clientOrderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order with id [%s] does not exist".formatted(orderId)));
    }

    @Transactional
    public ClientOrder createOrderFromCart(OrderRequest orderRequest, UUID userId) {


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
        ClientOrder clientOrder = ClientOrder.builder()
                .owner(cart.getOwner())
                .shippingAddress(orderRequest.getShippingAddress())
                .billingAddress(orderRequest.getBillingAddress())
                .customerPhoneNumber(orderRequest.getCustomerPhoneNumber())
                .createdOn(LocalDateTime.now())
                .orderedItems(itemsToOrder)
                .orderAmount(cart.getCartAmount())
                .build();

        for (OrderItem orderItem : itemsToOrder) {
            orderItem.setClientOrder(clientOrder);
            orderItemRepository.save(orderItem);
        }

        clientOrderRepository.save(clientOrder);

        return clientOrder;
    }
}
