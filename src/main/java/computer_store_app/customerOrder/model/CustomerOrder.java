package computer_store_app.customerOrder.model;

import computer_store_app.OrderItem.model.OrderItem;
import computer_store_app.user.model.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    private User owner;

    @Column(nullable = false)
    private String shippingAddress;

    @Column(nullable = false)
    private String billingAddress;

    @Column(nullable = false)
    private String customerPhoneNumber;

    @Column(nullable = false)
    private LocalDateTime createdOn;

    @OneToMany(mappedBy = "customerOrder", fetch = FetchType.EAGER)
    private List<OrderItem> orderedItems;

    @Column(nullable = false)
    private BigDecimal orderAmount;
}
