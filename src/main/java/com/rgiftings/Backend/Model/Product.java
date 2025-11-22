package com.rgiftings.Backend.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer productId;
    private String productName;
    private String productDescription;
    private BigDecimal productPrice;
    private Integer productStock;
    private String productImageUrl;
    private Date productCreatedAt;
    private Date productUpdatedAt;

    @Override
    public String toString() {
        return "Product{" +
                "product_id=" + productId +
                ", productName='" + productName + '\'' +
                ", productDescription='" + productDescription + '\'' +
                ", productPrice=" + productPrice +
                ", productStock=" + productStock +
                ", productImageUrl='" + productImageUrl + '\'' +
                ", productCreatedAt=" + productCreatedAt +
                ", productUpdatedAt=" + productUpdatedAt +
                '}';
    }
}
