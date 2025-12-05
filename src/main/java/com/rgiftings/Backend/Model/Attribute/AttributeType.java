package com.rgiftings.Backend.Model.Attribute;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttributeType {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false, unique = true)
    private String name; // SIZE, COLOR, MATERIAL

    private String inputType; // TEXT, FILE, DROPDOWN, MULTIPLE, RADIO, CHECKBOX

    @OneToMany(mappedBy = "attributeType", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AttributeValue> attributeValues;

    @Override
    public String toString() {
        return "AttributeType{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", inputType='" + inputType + '\'' +
                ", attributeValues=" + attributeValues +
                '}';
    }
}
