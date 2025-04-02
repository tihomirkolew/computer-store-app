package computer_store_app.web;

import computer_store_app.customerOrder.model.CustomerOrder;
import computer_store_app.customerOrder.service.CustomerOrderService;
import computer_store_app.security.AuthenticationMetadata;
import computer_store_app.user.model.User;
import computer_store_app.user.model.UserRole;
import computer_store_app.user.repository.UserRepository;
import computer_store_app.user.service.UserService;
import computer_store_app.web.dto.OrderRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.validation.BindingResult;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static computer_store_app.UserTestBuilder.aRandomUser;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(CustomerOrderController.class)
public class CustomerOrderControllerApiTest {

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private CustomerOrderService customerOrderService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void postRequestToCreateOrder_shouldRedirectOrReturnCartOnValidationError() throws Exception {
        when(userService.getById(any())).thenReturn(aRandomUser());

        UUID userId = UUID.randomUUID();
        AuthenticationMetadata principal = new AuthenticationMetadata(userId, "Ash Ketchup", "email@email.com", "123123123", UserRole.CLIENT);

        OrderRequest validOrderRequest = new OrderRequest();
        validOrderRequest.setFirstName("Ash");
        validOrderRequest.setLastName("Ketchup");
        validOrderRequest.setCustomerPhoneNumber("123123123");

        BindingResult mockBindingResult = mock(BindingResult.class);
        when(mockBindingResult.hasErrors()).thenReturn(true);

        MockHttpServletRequestBuilder request = post("/orders/create")
                .flashAttr("orderRequest", validOrderRequest)
                .with(user(principal))
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("cart"))
                .andExpect(model().attributeExists("cartItems", "cartSubtotal", "standardShippingFee", "cartTotalPrice", "orderRequest"));

        verify(userService, times(1)).getById(eq(userId));
        verify(userRepository, times(1)).save(any());
        verify(customerOrderService, times(0)).createOrderFromCart(any(), any());
    }



    @Test
    void postRequestToCreateOrder_shouldRedirectToOrdersPageOnSuccess() throws Exception {
        when(userService.getById(any())).thenReturn(aRandomUser());

        UUID userId = UUID.randomUUID();
        AuthenticationMetadata principal = new AuthenticationMetadata(userId, "Ash Ketchup", "email@email.com", "123123123", UserRole.CLIENT);

        OrderRequest validOrderRequest = new OrderRequest();
        validOrderRequest.setFirstName("Ash");
        validOrderRequest.setLastName("Ketchup");
        validOrderRequest.setCustomerPhoneNumber("+359123123123");
        validOrderRequest.setBillingAddress("123 Test St");
        validOrderRequest.setShippingAddress("456 Example Rd");

        CustomerOrder mockOrder = new CustomerOrder();
        mockOrder.setId(UUID.randomUUID());
        when(customerOrderService.createOrderFromCart(any(), eq(userId))).thenReturn(mockOrder);

        MockHttpServletRequestBuilder request = post("/orders/create")
                .flashAttr("orderRequest", validOrderRequest)
                .with(user(principal))
                .with(csrf());

        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/orders/" + mockOrder.getId()));

        verify(userService, times(1)).getById(eq(userId));
        verify(userRepository, times(1)).save(any());
        verify(customerOrderService, times(1)).createOrderFromCart(any(), eq(userId));
    }

    @Test
    void getRequestToOrderById_shouldReturnOrderPage() throws Exception {

        UUID orderId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        AuthenticationMetadata principal = new AuthenticationMetadata(userId, "Ash Ketchup", "email@email.com", "123123123", UserRole.CLIENT);

        User mockOwner = new User();
        mockOwner.setId(userId);
        mockOwner.setFirstName("Ash");
        mockOwner.setLastName("Ketchup");

        CustomerOrder mockOrder = new CustomerOrder();
        mockOrder.setOwner(mockOwner);
        mockOrder.setId(orderId);
        mockOrder.setOrderedItems(List.of());
        mockOrder.setOrderAmount(BigDecimal.valueOf(100));
        when(customerOrderService.getOrderById(orderId)).thenReturn(mockOrder);;

        MockHttpServletRequestBuilder request = get("/orders/{id}", orderId)
                .with(user(principal))
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("order"))
                .andExpect(model().attributeExists("customerOrder"))
                .andExpect(model().attribute("customerOrder", mockOrder));

        verify(customerOrderService, times(1)).getOrderById(eq(orderId));
    }

}
