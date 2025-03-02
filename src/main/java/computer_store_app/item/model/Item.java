package computer_store_app.item.model;

import computer_store_app.cart.model.Cart;
import computer_store_app.order.model.CustomerOrder;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String brand;

    @Column(nullable = false)
    private String model;

    @Column(nullable = false)
    private String specifications;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private boolean inStock;

    @Column(nullable = false)
    private int quantity;

    private String imageUrl;

    private String description;

    @Enumerated(EnumType.STRING)
    private ItemType type;

    @Enumerated(EnumType.STRING)
    private ItemCondition itemCondition;

    @ManyToOne
    private CustomerOrder customerOrder;

    @ManyToOne
    private Cart cart;
}

