package computer_store_app.web.mapper;

import computer_store_app.client.model.Client;
import computer_store_app.web.dto.EditUserRequest;
import lombok.experimental.UtilityClass;

@UtilityClass
public class UserToEditRequestMapper {

    public static EditUserRequest mapUserInfoToEditRequest (Client client) {

        return EditUserRequest.builder()
                .username(client.getUsername())
                .firstName(client.getFirstName())
                .lastName(client.getLastName())
                .email(client.getEmail())
                .build();
    }
}
