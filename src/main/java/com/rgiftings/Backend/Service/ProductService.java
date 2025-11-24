package com.rgiftings.Backend.Service;

import com.rgiftings.Backend.DTO.Product.ProductAttributeRequest;
import com.rgiftings.Backend.DTO.Product.ProductAttributeResponse;
import com.rgiftings.Backend.DTO.Product.ProductAttributeValueRequest;
import com.rgiftings.Backend.DTO.Product.ProductAttributeValueResponse;
import com.rgiftings.Backend.DTO.Product.ProductRequest;
import com.rgiftings.Backend.DTO.Product.ProductResponse;
import com.rgiftings.Backend.Model.AttributeType;
import com.rgiftings.Backend.Model.AttributeValue;
import com.rgiftings.Backend.Model.Product;
import com.rgiftings.Backend.Model.ProductAttribute;
import com.rgiftings.Backend.Model.ProductAttributeValue;
import com.rgiftings.Backend.Repository.DEV.AttributeRepository;
import com.rgiftings.Backend.Repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private AttributeRepository attributeRepository;

    public ProductResponse createProduct(ProductRequest request) {
        Product product = mapRequestToEntity(request);
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());
        Product createdProduct = productRepository.save(product);
        return mapToResponse(createdProduct);
    }

    public List<ProductResponse> getProducts() {
        return productRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public ProductResponse updateProduct(Long id, ProductRequest request) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + id));

        applyRequestToEntity(existingProduct, request);
        existingProduct.setUpdatedAt(LocalDateTime.now());
        Product updatedProduct = productRepository.save(existingProduct);
        return mapToResponse(updatedProduct);
    }

    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new IllegalArgumentException("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
    }

    private Product mapRequestToEntity(ProductRequest request) {
        Product product = new Product();
        applyRequestToEntity(product, request);
        return product;
    }

    private void applyRequestToEntity(Product product, ProductRequest request) {
        product.setName(request.name());
        product.setDescription(request.description());
        product.setBasePrice(request.basePrice());
        product.setStock(request.stock());
        product.setCategory(request.category());
        product.setImageUrl(request.imageUrl());
        if (product.getAttributes() == null) {
            product.setAttributes(new HashSet<>());
        }
        syncProductAttributes(product, request.attributes());
    }

    private ProductResponse mapToResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getBasePrice(),
                product.getStock(),
                product.getCategory(),
                product.getImageUrl(),
                mapAttributesToResponse(product.getAttributes()),
                product.getCreatedAt(),
                product.getUpdatedAt()
        );
    }

    private void syncProductAttributes(Product product, List<ProductAttributeRequest> attributes) {
        Set<ProductAttribute> productAttributes = product.getAttributes();
        if (productAttributes == null) {
            productAttributes = new HashSet<>();
            product.setAttributes(productAttributes);
        } else {
            productAttributes.clear();
        }

        if (attributes == null) {
            return;
        }

        for (ProductAttributeRequest attribute : attributes) {
            AttributeType attributeType = attributeRepository.findById(attribute.attributeTypeId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Attribute type not found with id: " + attribute.attributeTypeId()
                    ));

            ProductAttribute productAttribute = new ProductAttribute();
            productAttribute.setProduct(product);
            productAttribute.setAttributeType(attributeType);
            productAttribute.setLabel(attribute.label());

            syncProductAttributeValues(productAttribute, attribute.values());

            productAttributes.add(productAttribute);
        }
    }

    private void syncProductAttributeValues(ProductAttribute productAttribute, List<ProductAttributeValueRequest> values) {
        Set<ProductAttributeValue> valueEntities = productAttribute.getValues();
        if (valueEntities == null) {
            valueEntities = new HashSet<>();
            productAttribute.setValues(valueEntities);
        } else {
            valueEntities.clear();
        }

        if (values == null) {
            return;
        }

        for (ProductAttributeValueRequest valueRequest : values) {
            AttributeValue baseValue;
            if (valueRequest.attributeValueId() != null) {
                baseValue = findAttributeValue(productAttribute.getAttributeType(), valueRequest.attributeValueId());
            } else {
                if (valueRequest.value() == null || valueRequest.value().isBlank()) {
                    throw new IllegalArgumentException("Attribute value text is required when attributeValueId is not provided");
                }
                baseValue = new AttributeValue();
                baseValue.setAttributeType(productAttribute.getAttributeType());
                baseValue.setValue(valueRequest.value());
                baseValue.setDisplayCode(valueRequest.displayCode());
                attachAndPersistNewValue(productAttribute.getAttributeType(), baseValue);
            }

            ProductAttributeValue productAttributeValue = new ProductAttributeValue();
            productAttributeValue.setProductAttribute(productAttribute);
            productAttributeValue.setAttributeValue(baseValue);
            productAttributeValue.setExtraPrice(valueRequest.extraPrice() != null ? valueRequest.extraPrice() : 0.0);

            valueEntities.add(productAttributeValue);
        }
    }

    private AttributeValue findAttributeValue(AttributeType attributeType, Long attributeValueId) {
        if (attributeType.getValues() == null) {
            throw new IllegalArgumentException("Attribute value not found with id: " + attributeValueId);
        }

        return attributeType.getValues()
                .stream()
                .filter(value -> attributeValueId.equals(value.getId()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Attribute value not found with id: " + attributeValueId));
    }

    private void attachAndPersistNewValue(AttributeType attributeType, AttributeValue baseValue) {
        List<AttributeValue> values = attributeType.getValues();
        if (values == null) {
            values = new ArrayList<>();
            attributeType.setValues(values);
        }
        values.add(baseValue);
        attributeRepository.save(attributeType);
    }

    private List<ProductAttributeResponse> mapAttributesToResponse(Set<ProductAttribute> attributes) {
        if (attributes == null) {
            return List.of();
        }

        return attributes.stream()
                .map(attribute -> new ProductAttributeResponse(
                        attribute.getId(),
                        attribute.getLabel(),
                        attribute.getAttributeType().getId(),
                        attribute.getAttributeType().getType(),
                        mapAttributeValuesToResponse(attribute.getValues())
                ))
                .toList();
    }

    private List<ProductAttributeValueResponse> mapAttributeValuesToResponse(Set<ProductAttributeValue> values) {
        if (values == null) {
            return List.of();
        }

        return values.stream()
                .map(value -> new ProductAttributeValueResponse(
                        value.getId(),
                        value.getAttributeValue().getId(),
                        value.getAttributeValue().getValue(),
                        value.getAttributeValue().getDisplayCode(),
                        value.getExtraPrice()
                ))
                .toList();
    }
}
