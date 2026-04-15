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

    //PERFORMANCE METRICS FIELDS ---
    private Double performanceScore;
    private Double avgLeadTime;
    private Integer delayProbability;
    private Double reliabilityRating;
    private Integer totalOrders;

    //EXISTING FIELDS
    private String blacklistReason;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}