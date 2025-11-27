package com.rgiftings.Backend.Model.Product;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rgiftings.Backend.Model.Attribute.AttributeValue;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductAttributeValue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(nullable = false)
    @JsonIgnore
    private ProductAttribute productAttribute;

    @ManyToOne
    @JoinColumn(nullable = false)
    private AttributeValue AttributeValue;

    private BigDecimal extraPrice;
}
