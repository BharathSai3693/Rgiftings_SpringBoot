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
public class ProductAttribute {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(nullable = false)
    @JsonIgnore
    private Product product;

    @ManyToOne
    @JoinColumn(nullable = false)
    private AttributeType attributeType;

    @Column(nullable = false)
    private String label;

    @OneToMany(mappedBy = "productAttribute", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductAttributeValue> values = new ArrayList<>();

//    private Boolean required = false;

}
