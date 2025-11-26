package com.rgiftings.Backend.Repository;

import com.rgiftings.Backend.Model.Attribute.AttributeValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AttributeValueRepository extends JpaRepository<AttributeValue, Long> {
}
