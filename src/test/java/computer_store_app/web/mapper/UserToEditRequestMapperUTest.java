package computer_store_app.web.mapper;

import computer_store_app.user.model.User;
import computer_store_app.web.dto.EditUserRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class UserToEditRequestMapperUTest {
    @Test
    void givenHappyPath_whenMappingUserToUserEditRequest(){

        // Given
        User user = User.builder()
                .username("user1")
                .firstName("Ash")
                .lastName("Ketchup")
                .email("email@email.com")
                .build();

        // When
        EditUserRequest editUserRequest = UserToEditRequestMapper.mapUserInfoToEditRequest(user);

        // Then
        assertEquals(user.getFirstName(), editUserRequest.getFirstName());
        assertEquals(user.getLastName(), editUserRequest.getLastName());
        assertEquals(user.getEmail(), editUserRequest.getEmail());
        assertEquals(user.getUsername(), editUserRequest.getUsername());
    }
}
