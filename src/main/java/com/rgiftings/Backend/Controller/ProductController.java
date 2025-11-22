package com.rgiftings.Backend.Controller;

import com.rgiftings.Backend.Model.Product;
import com.rgiftings.Backend.Service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping("/products")
    public ResponseEntity<List<Product>> GetProducts(){
        List<Product> products = new ArrayList<>();
        products = productService.getProducts();
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @PostMapping("/product")
    public ResponseEntity<Product> AddProduct(@RequestBody Product product){
        Product createdProduct = productService.addOrUpdateProduct(product);
        System.out.println("controller "+ createdProduct);
        if(createdProduct!=null){
            return new ResponseEntity<>(createdProduct, HttpStatus.OK);
        }
        else {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @PutMapping("/product/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id,@RequestBody Product product){
        Product updatedProduct = productService.addOrUpdateProduct(product);
        System.out.println("controller "+ updatedProduct);
        if(updatedProduct!=null){
            return new ResponseEntity<>(updatedProduct, HttpStatus.OK);
        }
        else {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/product/{id}")
    public  ResponseEntity<String> deleteProduct(@PathVariable Long id){
        productService.deleteProduct(id);
        return new ResponseEntity<>("Successfully deleted", HttpStatus.OK);
    }

}
