package computer_store_app.user.model;

import computer_store_app.cart.model.Cart;
import computer_store_app.customerOrder.model.CustomerOrder;
import computer_store_app.item.model.Item;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "\"users\"")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String username;

    private String firstName;

    private String lastName;

    private String phoneNumber;

    @Column(nullable = false ,unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "owner")
    @OrderBy("createdOn DESC")
    private List<CustomerOrder> customerOrders;

    // cart
    @OneToOne
    private Cart cart;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "owner")
    private List<Item> addedItems;

}
