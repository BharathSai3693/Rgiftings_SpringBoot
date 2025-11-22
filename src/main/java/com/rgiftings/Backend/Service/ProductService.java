package com.rgiftings.Backend.Service;

import com.rgiftings.Backend.DTO.Product.ProductRequest;
import com.rgiftings.Backend.DTO.Product.ProductResponse;
import com.rgiftings.Backend.Model.Product;
import com.rgiftings.Backend.Repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

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
    }

    private ProductResponse mapToResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getBasePrice(),
                product.getStock(),
                product.getCategory(),
                product.getCreatedAt(),
                product.getUpdatedAt()
        );
    }
}
