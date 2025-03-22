package computer_store_app.order.service;

import computer_store_app.order.model.CustomerOrder;
import computer_store_app.order.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class OrderService {


    private final OrderRepository orderRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public CustomerOrder getOrderById(UUID orderId) {

        return orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order with id [%s] does not exist".formatted(orderId)));
    }
}
