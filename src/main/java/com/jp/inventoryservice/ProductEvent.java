package com.jp.inventoryservice;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductEvent {

    private Long productId;
    private String productName;
    private String updateType;
    private String description;

    @Override
    public String toString() {
        return "ProductData{" +
                "productId=" + productId +
                ", productName='" + productName + '\'' +
                ", updateType='" + updateType + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
