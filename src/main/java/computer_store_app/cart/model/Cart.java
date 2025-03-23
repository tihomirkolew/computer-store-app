package computer_store_app.cart.model;

import computer_store_app.item.model.Item;
import computer_store_app.client.model.Client;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne
    private Client owner;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "cart")
    private List<Item> cartItems;

    @Column(nullable = false)
    private BigDecimal cartAmount;
}
