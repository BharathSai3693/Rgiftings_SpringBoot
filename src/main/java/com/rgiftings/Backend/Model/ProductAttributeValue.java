package com.rgiftings.Backend.Model;

import jakarta.persistence.*;
import lombok.*;

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
    private ProductAttribute productAttribute;

    @ManyToOne
    @JoinColumn(nullable = false)
    private AttributeValue AttributeValue;

    private Double extraPrice = 0.0;
}
