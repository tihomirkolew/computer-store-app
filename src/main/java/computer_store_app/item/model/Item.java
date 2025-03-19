package computer_store_app.item.model;

import computer_store_app.cart.model.Cart;
import computer_store_app.order.model.CustomerOrder;
import computer_store_app.user.model.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    private User owner;

    @Column(nullable = false)
    private String brand;

    @Column(nullable = false)
    private String model;

    @Column(nullable = false)
    private BigDecimal price;

    private String imageUrl;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private boolean sold;

    @Column(nullable = false)
    private boolean authorized;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ItemType type;

    @Column(nullable = false)
    private LocalDateTime addedOn;

    @Column(nullable = false)
    private LocalDateTime updatedOn;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ItemCondition itemCondition;

    @ManyToOne
    private CustomerOrder customerOrder;

    @ManyToOne
    private Cart cart;
}

