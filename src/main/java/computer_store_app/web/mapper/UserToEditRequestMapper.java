package computer_store_app.web.mapper;

import computer_store_app.user.model.User;
import computer_store_app.web.dto.EditUserRequest;
import lombok.experimental.UtilityClass;
import org.hibernate.dialect.unique.CreateTableUniqueDelegate;

@UtilityClass
public class UserToEditRequestMapper {

    public static EditUserRequest mapUserInfoToEditRequest (User user) {

        return EditUserRequest.builder()
                .username(user.getUsername())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .build();
    }
}
