package com.rgiftings.Backend.Model.Product;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rgiftings.Backend.Model.Attribute.AttributeType;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductAttribute {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Product product;

    @ManyToOne
    @JoinColumn(nullable = false)
    private AttributeType attributeType; //SIZE, COLOR,

    @Column(nullable = false)
    private String productAttributeLabel; //PRODUCT SIZE, GIFT COLOR

    @OneToMany(mappedBy = "productAttribute", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductAttributeValue> productAttributeValues;


}
