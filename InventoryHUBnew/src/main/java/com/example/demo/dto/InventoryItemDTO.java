package com.example.demo.dto;

public class InventoryItemDTO {

    private Long id;
    private String name;
    private String sku;
    private int stock;
    private int safety;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }

    public int getSafety() { return safety; }
    public void setSafety(int safety) { this.safety = safety; }
}