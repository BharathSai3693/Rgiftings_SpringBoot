package com.rgiftings.Backend.Model.Attribute;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;


@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class AttributeValue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JsonIgnore
    private AttributeType attributeType;

    @Column(nullable = false)
    private String value;

    private String displayCode;

    @Override
    public String toString() {
        return "AttributeValue{" +
                "id=" + id +
                ", value='" + value + '\'' +
                ", displayCode='" + displayCode + '\'' +
                '}';
    }
}
