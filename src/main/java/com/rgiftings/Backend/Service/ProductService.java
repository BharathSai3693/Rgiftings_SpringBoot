package com.rgiftings.Backend.Service;

import com.rgiftings.Backend.Model.Product;
import com.rgiftings.Backend.Repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public Product addOrUpdateProduct(Product product) {
        Product createdProduct =  productRepository.save(product);
        System.out.println("Repo "+ createdProduct);
        return createdProduct;
    }

    public List<Product> getProducts() {
        return productRepository.findAll();
    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }
}
