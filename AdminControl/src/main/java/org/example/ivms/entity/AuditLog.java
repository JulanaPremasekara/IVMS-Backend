package org.example.ivms.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime timestamp = LocalDateTime.now();

    private String performedByUserId   = "SYSTEM";
    private String performedByUsername = "System";

    @Column(nullable = false)
    private String module;

    @Column(nullable = false)
    private String action;

    private String entityType;
    private String entityId;

    private String status = "Normal";
}