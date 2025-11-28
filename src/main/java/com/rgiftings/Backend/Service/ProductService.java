package com.rgiftings.Backend.Service;

import com.rgiftings.Backend.DTO.Attribute.Update.UpdateAttributeValueRequest;
import com.rgiftings.Backend.DTO.Product.Create.ProductAttributeRequest;
import com.rgiftings.Backend.DTO.Product.Retrieve.ProductAttributeResponse;
import com.rgiftings.Backend.DTO.Product.Create.ProductAttributeValueRequest;
import com.rgiftings.Backend.DTO.Product.Retrieve.ProductAttributeValueResponse;
import com.rgiftings.Backend.DTO.Product.Create.ProductRequest;
import com.rgiftings.Backend.DTO.Product.Retrieve.ProductResponse;
import com.rgiftings.Backend.DTO.Product.Update.UpdateProductAttributeRequest;
import com.rgiftings.Backend.DTO.Product.Update.UpdateProductAttributeValueRequest;
import com.rgiftings.Backend.DTO.Product.Update.UpdateProductRequest;
import com.rgiftings.Backend.Model.Attribute.AttributeType;
import com.rgiftings.Backend.Model.Attribute.AttributeValue;
import com.rgiftings.Backend.Model.Product.Product;
import com.rgiftings.Backend.Model.Product.ProductAttribute;
import com.rgiftings.Backend.Model.Product.ProductAttributeValue;
import com.rgiftings.Backend.Repository.AttributeRepository;
import com.rgiftings.Backend.Repository.AttributeValueRepository;
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

    @Autowired
    private AttributeValueRepository attributeValueRepository;

    //CREATE PRODUCT
    public String createProduct(ProductRequest productRequest) {
        Product newProduct = Product.builder()
                .name(productRequest.name())
                .description(productRequest.description())
                .basePrice(productRequest.basePrice())
                .stock(productRequest.stock())
                .category(productRequest.category())
                .imageUrl(productRequest.imageUrl())
                .taxRate(productRequest.taxRate())
                .build();
        List<ProductAttribute> productAttributes = mapProductAttributes(newProduct, productRequest);
        newProduct.setProductAttributes(productAttributes);
        newProduct.setCreatedAt(LocalDateTime.now());
        newProduct.setUpdatedAt(LocalDateTime.now());
        Product createdProduct = productRepository.save(newProduct);
        return "CREATED";
    }

    public List<ProductAttribute> mapProductAttributes(Product newProduct, ProductRequest productRequest){
        List<ProductAttribute> productAttributes = new ArrayList<>();

        for(ProductAttributeRequest productAttributeRequest : productRequest.productAttributes()){
            //Fetch Attribute Type
            AttributeType attributeType = attributeRepository.findById(productAttributeRequest.attributeTypeId())
                    .orElseThrow(() -> new RuntimeException("Attribute type not found with id: " + productAttributeRequest.attributeTypeId()));

            ProductAttribute productAttribute = ProductAttribute.builder()
                    .product(newProduct)
                    .attributeType(attributeType)
                    .productAttributeLabel(productAttributeRequest.productAttributeLabel())
                    .build();


            List<ProductAttributeValue> productAttributeValues = mapProductAttributeValues(productAttribute,productAttributeRequest);
            productAttribute.setProductAttributeValues(productAttributeValues);
            productAttributes.add(productAttribute);
        }

        return productAttributes;
    }

    public List<ProductAttributeValue> mapProductAttributeValues(ProductAttribute productAttribute, ProductAttributeRequest productAttributeRequest){
        List<ProductAttributeValue> productAttributeValues = new ArrayList<>();

        for(ProductAttributeValueRequest productAttributeValueRequest : productAttributeRequest.productAttributeValues()){

            //Fetch Attribute Values
            AttributeValue attributeValue = attributeValueRepository.findById(productAttributeValueRequest.attributeValueId())
                    .orElseThrow(() -> new IllegalArgumentException("Attribute value not found with id: " + productAttributeValueRequest.attributeValueId()));

            ProductAttributeValue productAttributeValue = ProductAttributeValue.builder()
                    .productAttribute(productAttribute)
                    .attributeValue(attributeValue)
                    .extraPrice(productAttributeValueRequest.extraPrice())
                    .build();

            productAttributeValues.add(productAttributeValue);
        }

        return productAttributeValues;
    }

    //FETCH ALL PRODUCTS
    public List<ProductResponse> getProducts() {
        List<Product> allProducts = productRepository.findAll();
        List<ProductResponse> productResponses = new ArrayList<>();

        for(Product product : allProducts){
            List<ProductAttributeResponse> productAttributeResponses = new ArrayList<>();

            for(ProductAttribute productAttribute : product.getProductAttributes()){

                List<ProductAttributeValueResponse> productAttributeValueResponses = new ArrayList<>();
                for(ProductAttributeValue productAttributeValue : productAttribute.getProductAttributeValues()){

                    AttributeValue attributeValue = attributeValueRepository.findById(productAttributeValue.getAttributeValue().getId())
                            .orElseThrow(() -> new RuntimeException("Attribute Value not found"));
                    ProductAttributeValueResponse productAttributeValueResponse = ProductAttributeValueResponse.builder()
                            .id(productAttributeValue.getId())
                            .attributeValueName(attributeValue.getValue())
                            .extraPrice(productAttributeValue.getExtraPrice())
                            .build();

                    productAttributeValueResponses.add(productAttributeValueResponse);
                }

                AttributeType attributeType = attributeRepository.findById(productAttribute.getAttributeType().getId())
                        .orElseThrow(() -> new RuntimeException("Attribute Type Not Found"));
                ProductAttributeResponse productAttributeResponse = ProductAttributeResponse.builder()
                        .id(productAttribute.getId())
                        .attributeName(attributeType.getName())
                        .attributeInputType(attributeType.getInputType())
                        .productAttributeLabel(productAttribute.getProductAttributeLabel())
                        .productAttributeValues(productAttributeValueResponses)
                        .build();

                productAttributeResponses.add(productAttributeResponse);
            }

            ProductResponse productResponse = ProductResponse.builder()
                    .id(product.getId())
                    .name(product.getName())
                    .description(product.getDescription())
                    .basePrice(product.getBasePrice())
                    .stock(product.getStock())
                    .taxRate(product.getTaxRate())
                    .category(product.getCategory())
                    .imageUrl(product.getImageUrl())
                    .productAttributes(productAttributeResponses)
                    .createdAt(product.getCreatedAt())
                    .updatedAt(product.getUpdatedAt())
                    .build();

            productResponses.add(productResponse);
        }
        return productResponses;
    }


    //UPDATING PRODUCT
    public String updateProduct(Long id, UpdateProductRequest updateRequest) {

        // STEP 1: Fetch product
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product Not Found"));

        // STEP 2: Update product fields
        existingProduct.setName(updateRequest.name());
        existingProduct.setDescription(updateRequest.description());
        existingProduct.setBasePrice(updateRequest.basePrice());
        existingProduct.setStock(updateRequest.stock());
        existingProduct.setCategory(updateRequest.category());
        existingProduct.setImageUrl(updateRequest.imageUrl());
        existingProduct.setTaxRate(updateRequest.taxRate());
        existingProduct.setUpdatedAt(LocalDateTime.now());

        // STEP 3: Existing attributes
        List<ProductAttribute> existingAttributes = existingProduct.getProductAttributes();

        // Collect incoming attribute IDs
        List<Long> incomingAttributeIds = updateRequest.productAttributes().stream()
                .map(UpdateProductAttributeRequest::id)
                .filter(idVal -> idVal != null)
                .toList();

        // REMOVE attributes not present in incoming list
        existingAttributes.removeIf(attr -> !incomingAttributeIds.contains(attr.getId()));

        // STEP 4: Loop through all incoming attributes
        for (UpdateProductAttributeRequest incomingAttr : updateRequest.productAttributes()) {

            // ------------------------
            // CASE A: EXISTING ATTRIBUTE
            // ------------------------
            if (incomingAttr.id() != null) {

                ProductAttribute existingAttr = existingAttributes.stream()
                        .filter(a -> a.getId().equals(incomingAttr.id()))
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException("Product Attribute not found id=" + incomingAttr.id()));

                // Update label
                existingAttr.setProductAttributeLabel(incomingAttr.productAttributeLabel());

                // Update AttributeType (ONLY IF front-end sends new one)
                if (incomingAttr.attributeTypeId() != null &&
                        !existingAttr.getAttributeType().getId().equals(incomingAttr.attributeTypeId())) {

                    AttributeType newType = attributeRepository.findById(incomingAttr.attributeTypeId())
                            .orElseThrow(() -> new RuntimeException("AttributeType not found id=" + incomingAttr.attributeTypeId()));

                    existingAttr.setAttributeType(newType);
                }

                // Handle ProductAttributeValues
                List<ProductAttributeValue> existingValues = existingAttr.getProductAttributeValues();

                List<Long> incomingValueIds = incomingAttr.productAttributeValues().stream()
                        .map(UpdateProductAttributeValueRequest::id)
                        .filter(idVal -> idVal != null)
                        .toList();

                // REMOVE values missing in incoming request
                existingValues.removeIf(v -> !incomingValueIds.contains(v.getId()));

                // LOOP new + existing values
                for (UpdateProductAttributeValueRequest incVal : incomingAttr.productAttributeValues()) {

                    // CASE A1: UPDATE EXISTING VALUE
                    if (incVal.id() != null) {

                        ProductAttributeValue existingVal = existingValues.stream()
                                .filter(v -> v.getId().equals(incVal.id()))
                                .findFirst()
                                .orElseThrow(() -> new RuntimeException("Product Attribute Value not found id=" + incVal.id()));

                        existingVal.setExtraPrice(incVal.extraPrice());
                    }

                    // CASE A2: ADD NEW VALUE
                    else {
                        ProductAttributeValue newVal = new ProductAttributeValue();
                        newVal.setProductAttribute(existingAttr);

                        AttributeValue av = attributeValueRepository.findById(incVal.attributeValueId())
                                .orElseThrow(() -> new RuntimeException("AttributeValue not found id=" + incVal.attributeValueId()));

                        newVal.setAttributeValue(av);
                        newVal.setExtraPrice(incVal.extraPrice());

                        existingValues.add(newVal);
                    }
                }
            }

            // ------------------------
            // CASE B: NEW ATTRIBUTE
            // ------------------------
            else {
                ProductAttribute newAttr = new ProductAttribute();
                newAttr.setProduct(existingProduct);

                AttributeType type = attributeRepository.findById(incomingAttr.attributeTypeId())
                        .orElseThrow(() -> new RuntimeException("AttributeType not found id=" + incomingAttr.attributeTypeId()));

                newAttr.setAttributeType(type);
                newAttr.setProductAttributeLabel(incomingAttr.productAttributeLabel());

                // Add values
                List<ProductAttributeValue> newValues = new ArrayList<>();

                for (UpdateProductAttributeValueRequest incVal : incomingAttr.productAttributeValues()) {

                    ProductAttributeValue val = new ProductAttributeValue();
                    AttributeValue av = attributeValueRepository.findById(incVal.attributeValueId())
                            .orElseThrow(() -> new RuntimeException("AttributeValue not found id=" + incVal.attributeValueId()));

                    val.setProductAttribute(newAttr);
                    val.setAttributeValue(av);
                    val.setExtraPrice(incVal.extraPrice());

                    newValues.add(val);
                }

                newAttr.setProductAttributeValues(newValues);
                existingAttributes.add(newAttr);
            }
        }

        productRepository.save(existingProduct);
        return "UPDATED";
    }

    public String deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new IllegalArgumentException("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
        return "DELETED";
    }


}
