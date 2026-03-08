package com.example.demo.repo;

import com.example.demo.entity.Shipment;
import com.example.demo.enums.ShipmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ShipmentRepository extends JpaRepository<Shipment, Long> {

    List<Shipment> findByStatusIn(List<ShipmentStatus> statuses);

    List<Shipment> findByEstimatedArrivalBetween(LocalDate start, LocalDate end);

    Optional<Shipment> findByTrackingId(String trackingId);

    Optional<Shipment> findByPurchaseOrderId(Long purchaseOrderId);
}