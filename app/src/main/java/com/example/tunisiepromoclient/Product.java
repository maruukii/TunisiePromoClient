package com.example.tunisiepromoclient;
public class Product {
    private String productId;
    private String name;
    private double priceOld;
    private double priceNew;
    private String imageUrl;

    // Constructor, getters, and setters


    public Product(String productId, String name, double priceOld, double priceNew, String imageUrl) {
        this.productId = productId;
        this.name = name;
        this.priceOld = priceOld;
        this.priceNew = priceNew;
        this.imageUrl = imageUrl;
    }

    public Product() {
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPriceOld() {
        return priceOld;
    }

    public void setPriceOld(double priceOld) {
        this.priceOld = priceOld;
    }

    public double getPriceNew() {
        return priceNew;
    }

    public void setPriceNew(double priceNew) {
        this.priceNew = priceNew;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}

