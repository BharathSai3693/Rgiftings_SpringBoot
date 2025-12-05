package com.rgiftings.Backend.Controller;

import com.rgiftings.Backend.DTO.Product.Create.ProductRequest;
import com.rgiftings.Backend.DTO.Product.Retrieve.ProductResponse;
import com.rgiftings.Backend.DTO.Product.Update.UpdateProductRequest;
import com.rgiftings.Backend.Service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin("*")
public class ProductController {

    @Autowired
    private ProductService productService;

    // GET ALL PRODUCTS
    @GetMapping("/products")
    public ResponseEntity<List<ProductResponse>> getProducts(){
        List<ProductResponse> products = new ArrayList<>();
        products = productService.getProducts();
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    // CREATE PRODUCT
    @PostMapping("/product")
    public String createProduct(@RequestBody ProductRequest productRequest){
        return productService.createProduct(productRequest);
    }

    // UPDATE PRODUCT
    @PutMapping("/product/{id}")
    public String updateproduct(@PathVariable Long id, @RequestBody UpdateProductRequest updateProductRequest){
        String result = productService.updateProduct(id, updateProductRequest);
        return result;
    }

    //DELETE PRODUCT
    @DeleteMapping("/product/{id}")
    public  ResponseEntity<String> deleteProduct(@PathVariable Long id){
        try {
            String response = productService.deleteProduct(id);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (IllegalStateException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }
    }

}
