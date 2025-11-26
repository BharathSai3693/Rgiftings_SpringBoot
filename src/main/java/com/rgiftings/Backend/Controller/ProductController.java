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

    @GetMapping("/products")
    public ResponseEntity<List<ProductResponse>> getProducts(){
        List<ProductResponse> products = new ArrayList<>();
        products = productService.getProducts();
        return new ResponseEntity<>(products, HttpStatus.OK);
    }


    @PostMapping("/product")
    public String createProduct(@RequestBody ProductRequest productRequest){
        return productService.createProduct(productRequest);
    }

//    @PutMapping("/product/{id}")
//    public ResponseEntity<ProductResponse> updateProduct(@PathVariable Long id,@RequestBody ProductRequest product){
//        ProductResponse updatedProduct = productService.updateProduct(id, product);
//        if(updatedProduct!=null){
//            return new ResponseEntity<>(updatedProduct, HttpStatus.OK);
//        }
//        else {
//            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }

    @PutMapping("/product/{id}")
    public String updateproduct(@PathVariable Long id, @RequestBody UpdateProductRequest updateProductRequest){
        System.out.println("UPDATE PRODUCT CALLED");
        System.out.println(updateProductRequest);
        String result = productService.updateProduct(id, updateProductRequest);
        return result;
    }

    @DeleteMapping("/product/{id}")
    public  ResponseEntity<String> deleteProduct(@PathVariable Long id){
        productService.deleteProduct(id);
        return new ResponseEntity<>("Successfully deleted", HttpStatus.OK);
    }

}
