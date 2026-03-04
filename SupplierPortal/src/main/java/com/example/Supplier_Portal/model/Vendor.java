package com.example.Supplier_Portal.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "vendors")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vendor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String companyName;
    private String taxId;
    private String address;

    private String email;
    private String phone;
    private String contactPerson;

    private String paymentTerms;
    private String startDate;
    private String endDate;

    private String sku;
    private String unitPrice;

    @Column(nullable = false)
    private String status;

    // --- NEW PERFORMANCE METRICS FIELDS ---
    // These will be initialized to 0 for new vendors
    private Double performanceScore;      // e.g., 0.0 to 100.0
    private Double avgLeadTime;           // e.g., 0.0 days
    private Integer delayProbability;     // e.g., 0 to 100
    private Double reliabilityRating;     // e.g., 0.0 to 5.0
    private Integer totalOrders;          // Used to identify "New" status (0 = New)

    // --- EXISTING FIELDS ---
    private String blacklistReason;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}