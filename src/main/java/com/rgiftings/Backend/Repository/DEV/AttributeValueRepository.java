package com.rgiftings.Backend.Repository.DEV;

import com.rgiftings.Backend.Model.AtrributeValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AttributeValueRepository extends JpaRepository<AtrributeValue, Long> {
}
