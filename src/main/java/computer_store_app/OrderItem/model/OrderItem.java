package computer_store_app.OrderItem.model;

import computer_store_app.sellerOrder.model.SellerOrder;
import computer_store_app.user.model.User;
import computer_store_app.item.model.ItemType;
import computer_store_app.customerOrder.model.CustomerOrder;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {

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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ItemType type;

    @ManyToOne
    private CustomerOrder customerOrder;

    @ManyToOne
    private SellerOrder sellerOrder;
}
