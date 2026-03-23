package com.example.demo.repo;

import com.example.demo.entity.ShipmentTimeLineEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
    public interface ShipmentTimelineEventRepository
            extends JpaRepository<ShipmentTimeLineEvent, Long> {

        List<ShipmentTimeLineEvent> findByShipmentTrackingIdOrderByEventTimeAsc(String trackingId);

    }

