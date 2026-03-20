package com.example.demo.dto;

import java.time.LocalDateTime;
import java.util.List;


public record TimeLineEventsDto(
        String status,
        LocalDateTime time,
        String Location,
        String note,
        List<String> items
) {}
