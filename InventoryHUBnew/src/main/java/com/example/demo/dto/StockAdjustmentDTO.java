package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockAdjustmentDTO implements Serializable {
    private Long productId;
    private int newSafetyLimit;
    private String adjustmentType;
}