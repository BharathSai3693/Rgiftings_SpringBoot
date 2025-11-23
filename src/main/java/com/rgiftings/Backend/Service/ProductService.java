package com.rgiftings.Backend.Service;

import com.rgiftings.Backend.DTO.Product.ProductAttributeRequest;
import com.rgiftings.Backend.DTO.Product.ProductAttributeResponse;
import com.rgiftings.Backend.DTO.Product.ProductRequest;
import com.rgiftings.Backend.DTO.Product.ProductResponse;
import com.rgiftings.Backend.Model.AttributeType;
import com.rgiftings.Backend.DTO.Product.ProductRequest;
import com.rgiftings.Backend.DTO.Product.ProductResponse;
import com.rgiftings.Backend.Model.Product;
import com.rgiftings.Backend.Model.ProductAttribute;
import com.rgiftings.Backend.Repository.DEV.AttributeRepository;
import com.rgiftings.Backend.Repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
                mapAttributesToResponse(product.getAttributes()),
                product.getCreatedAt(),
                product.getUpdatedAt()
        );
    }

    private void syncProductAttributes(Product product, List<ProductAttributeRequest> attributes) {
        if (attributes == null) {
            return;
        }

        Set<ProductAttribute> productAttributes = product.getAttributes();
        if (productAttributes == null) {
            productAttributes = new HashSet<>();
            product.setAttributes(productAttributes);
        } else {
            productAttributes.clear();
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

            productAttributes.add(productAttribute);
        }
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
                        attribute.getAttributeType().getName()
                ))
                .toList();
    }
}
