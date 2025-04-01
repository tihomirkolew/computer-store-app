package computer_store_app.customerOrder;

import computer_store_app.OrderItem.repository.OrderItemRepository;
import computer_store_app.user.service.UserService;
import computer_store_app.customerOrder.model.CustomerOrder;
import computer_store_app.customerOrder.repository.CustomerOrderRepository;
import computer_store_app.customerOrder.service.CustomerOrderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CustomerOrderUnitTest {


    @Mock
    private CustomerOrderRepository customerOrderRepository;
    @Mock
    private OrderItemRepository orderItemRepository;
    @Mock
    private UserService userService;

    @InjectMocks
    private CustomerOrderService customerOrderService;

    // test getById()
    @Test
    void givenExistingCustomerOrder_whenGetById_thenReturnCustomerOrder() {

        // Given
        UUID customerOrderId = UUID.randomUUID();

        CustomerOrder expectedCustomerOrder = CustomerOrder.builder()
                .id(customerOrderId)
                .shippingAddress("123 Test Street")
                .billingAddress("456 Test Avenue")
                .customerPhoneNumber("123-456-7890")
                .createdOn(LocalDateTime.now())
                .orderAmount(BigDecimal.valueOf(150.75))
                .build();
//        customerOrderRepository.save(expectedCustomerOrder);

        // When
        when(customerOrderRepository.findById(customerOrderId))
                .thenReturn(Optional.of(expectedCustomerOrder));

        // Then
        CustomerOrder actualOrder = customerOrderService.getOrderById(customerOrderId);
        assertNotNull(actualOrder);
        assertEquals(expectedCustomerOrder.getId(), actualOrder.getId());
        assertEquals(expectedCustomerOrder.getShippingAddress(), actualOrder.getShippingAddress());
        assertEquals(expectedCustomerOrder.getBillingAddress(), actualOrder.getBillingAddress());
        assertEquals(expectedCustomerOrder.getCustomerPhoneNumber(), actualOrder.getCustomerPhoneNumber());
        assertEquals(expectedCustomerOrder.getOrderAmount(), actualOrder.getOrderAmount());

        verify(customerOrderRepository, times(1)).findById(customerOrderId);

    }

    @Test
    void givenExistingCustomerOrder_whenGetById_thenReturnException() {

        // Given
        UUID customerOrderId = UUID.randomUUID();

        // When
        when(customerOrderRepository.findById(customerOrderId))
                .thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> customerOrderService.getOrderById(customerOrderId));

        // then
        assertEquals("Order with id [%s] does not exist".formatted(customerOrderId), exception.getMessage());
    }
}
