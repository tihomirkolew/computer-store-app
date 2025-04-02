package computer_store_app.web;


import computer_store_app.recommendation.service.RecommendationService;
import computer_store_app.security.AuthenticationMetadata;
import computer_store_app.user.model.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RecommendationController.class)
public class RecommendationControllerApiTest {

    @MockitoBean
    private RecommendationService recommendationService;

    @Autowired
    private MockMvc mockMvc;

//    @Test
//    void givenValidAuthentication_whenShowRecommendationForm_thenReturnCorrectViewAndEmail() throws Exception {
//        // Specific setup for this test
//        UUID userId = UUID.randomUUID();
//        AuthenticationMetadata principal = new AuthenticationMetadata(
//                userId, "User123", "email@email.com", "123123123", UserRole.CLIENT);
//
//        mockMvc.perform(get("/recommendation-form").with(user(principal))) // Simulate authenticated user
//                .andExpect(status().isOk()) // Expect 200 OK
//                .andExpect(view().name("recommendation-form")) // Verify view name
//                .andExpect(model().attribute("userEmail", "email@email.com")); // Verify userEmail attribute
//    }


}
