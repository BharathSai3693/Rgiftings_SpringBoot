package com.rgiftings.Backend.Repository;

import com.rgiftings.Backend.Model.Product.ProductAttribute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductAttributeRepository extends JpaRepository<ProductAttribute, Long> {

    long countByAttributeType_Id(Long attributeTypeId);
}
