package computer_store_app.sellerOrder.model;


import computer_store_app.OrderItem.model.OrderItem;
import computer_store_app.user.model.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SellerOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    private User seller;

    @Column(nullable = false)
    private UUID buyerId;

    @Column(nullable = false)
    private String buyerName;

    @Column(nullable = false)
    private String shippingAddress;

    @Column(nullable = false)
    private String billingAddress;

    @Column(nullable = false)
    private String customerPhoneNumber;

    @Column(nullable = false)
    private BigDecimal orderAmount;

    @Column(nullable = false)
    private LocalDateTime createdOn;

    @OneToMany(mappedBy = "sellerOrder", fetch = FetchType.EAGER)
    private List<OrderItem> orderedItemsByBuyer;

}
