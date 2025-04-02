package computer_store_app.web;

import computer_store_app.item.service.ItemService;
import computer_store_app.review.service.ReviewService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(IndexController.class)
public class IndexControllerApiTest {

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private ItemService itemService;

    @MockitoBean
    private ReviewService reviewService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getRequestToIndexEndpoint_shouldReturnIndexView () throws Exception {

        // build request
        MockHttpServletRequestBuilder request = get("/");

        // send request
        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("index"));
    }

    @Test
    void getRequestToContactsEndpoint_shouldReturnIndexView() throws Exception {

        // build request
        MockHttpServletRequestBuilder request = get("/contacts");

        // send request
        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("contacts"));
    }

    @Test
    void getRequestToRegisterEndpoint_shouldReturnRegisterView() throws Exception {

        // build request
        MockHttpServletRequestBuilder request = get("/register");

        // send request
        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeExists("registerRequest"));
    }

    @Test
    void getRequestToLoginEndpoint_shouldReturnLoginViewAndErrorMessageAttribute() throws Exception {

        // build
        MockHttpServletRequestBuilder request = get("/login").param("error", "");

        // send
        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(model().attributeExists("loginRequest", "errorMessage"));
    }


    @Test
    void postRequestToRegisterEndpoint_shouldRegisterUserAndRedirectToLogin() throws Exception {

        MockHttpServletRequestBuilder request = post("/register")
                .formField("username", "Ash Ketchup")
                .formField("email", "email@email.com")
                .formField("password", "123123123")
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
        verify(userService, times(1)).registerUser(any());
    }

    @Test
    void postRequestToRegisterEndpoint_shouldReturnRegisterView () throws Exception {

        MockHttpServletRequestBuilder request = post("/register")
                .formField("username", "")
                .formField("email", "")
                .formField("password", "")
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("register"));
        verify(userService, never()).registerUser(any());
    }

    @Test
    void getAuthenticatedRequestToHome_shouldReturnHomeView () throws Exception {

        when(userService.getById(any())).thenReturn(aRandomUser());

        UUID userId = UUID.randomUUID();
        AuthenticationMetadata principal =
                new AuthenticationMetadata(userId, "Ash Ketchup", "email@email.com", "123123123", UserRole.CLIENT);

        MockHttpServletRequestBuilder request = get("/home").with(user(principal));

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("home"))
                .andExpect(model().attributeExists("user"));
        verify(userService, times(1)).getById(any());
    }
}
