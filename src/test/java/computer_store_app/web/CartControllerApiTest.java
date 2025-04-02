package computer_store_app.web;

import computer_store_app.cart.service.CartService;
import computer_store_app.security.AuthenticationMetadata;
import computer_store_app.user.model.UserRole;
import computer_store_app.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;


import java.util.UUID;

import static computer_store_app.UserTestBuilder.aRandomUser;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CartController.class)
public class CartControllerApiTest {

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private CartService cartService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getAuthenticatedRequestToCartEndpoint_shouldReturnCartPage() throws Exception {

        // build
        when(userService.getById(any())).thenReturn(aRandomUser());

        UUID userId = UUID.randomUUID();
        AuthenticationMetadata principal = new AuthenticationMetadata(userId, "Ash Ketchup", "email@email.com", "123123123", UserRole.CLIENT);

        MockHttpServletRequestBuilder request = get("/cart").with(user(principal));

        // send
        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("cart"))
                .andExpect(model().attributeExists("cartItems"))
                .andExpect(model().attributeExists("cartSubtotal"))
                .andExpect(model().attributeExists("standardShippingFee"))
                .andExpect(model().attributeExists("cartTotalPrice"))
                .andExpect(model().attributeExists("orderRequest"));

        verify(userService, times(1)).getById(any());
    }

    @Test
    void postAuthenticatedRequestToCart_shouldRedirectToCart() throws Exception {

        // build
        when(userService.getById(any())).thenReturn(aRandomUser());

        UUID userId = UUID.randomUUID();
        UUID itemId = UUID.randomUUID();
        AuthenticationMetadata principal = new AuthenticationMetadata(userId, "Ash Ketchup", "email@email.com", "123123123", UserRole.CLIENT);

        MockHttpServletRequestBuilder request = post("/cart/add/{itemId}", itemId)
                .with(user(principal))
                .with(csrf());

        // send
        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/cart"));

        verify(cartService, times(1)).addItemToCart(any(), eq(itemId));
    }

    @Test
    void deleteRequestToCartRemoveItem_shouldRedirectToCart() throws Exception {

        // build
        when(userService.getById(any())).thenReturn(aRandomUser());

        UUID userId = UUID.randomUUID();
        UUID itemId = UUID.randomUUID();
        AuthenticationMetadata principal = new AuthenticationMetadata(userId, "Ash Ketchup", "email@email.com", "123123123", UserRole.CLIENT);

        MockHttpServletRequestBuilder request = delete("/cart/remove/{itemId}", itemId)
                .with(user(principal))
                .with(csrf());

        // send
        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/cart"));

        verify(cartService, times(1)).removeItemFromUserCart(eq(itemId), any());
    }

}
