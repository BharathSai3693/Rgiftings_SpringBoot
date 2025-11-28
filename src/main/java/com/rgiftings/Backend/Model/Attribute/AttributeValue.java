package com.rgiftings.Backend.Model.Attribute;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;


@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttributeValue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(nullable = false)
    private AttributeType attributeType;

    @Column(nullable = false)
    private String value; // RED,YELLOW, SMALL, LARGER

    @Override
    public String toString() {
        return "AttributeValue{" +
                "id=" + id +
                ", value='" + value + '\'' +
                '}';
    }
}
