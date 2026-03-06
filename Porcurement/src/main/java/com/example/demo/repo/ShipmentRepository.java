package com.example.demo.repo;

import com.example.demo.entity.Shipment;
import com.example.demo.enums.ShipmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ShipmentRepository extends JpaRepository<Shipment, Long> {

    // Active shipments for your table
    List<Shipment> findByStatusIn(List<ShipmentStatus> statuses);

    List<Shipment> findByArrivalWeek(LocalDateTime start, LocalDateTime end);
}
