package com.rgiftings.Backend.Model.Attribute;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AttributeType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String type;

    private String description;

    @OneToMany(mappedBy = "attributeType", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AttributeValue> values;

    @Override
    public String toString() {
        return "AttributeType{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", description='" + description + '\'' +
                ", values=" + values +
                '}';
    }
}
