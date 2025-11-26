package com.rgiftings.Backend.Repository;

import com.rgiftings.Backend.Model.Attribute.AttributeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AttributeRepository extends JpaRepository<AttributeType, Long> {

}
