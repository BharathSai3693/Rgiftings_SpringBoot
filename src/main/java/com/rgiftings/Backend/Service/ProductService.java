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

    public String createProduct(ProductRequest productRequest) {
        Product newProduct = new Product();
        newProduct.setName(productRequest.name());
        newProduct.setDescription(productRequest.description());
        newProduct.setBasePrice(productRequest.basePrice());
        newProduct.setStock(productRequest.stock());
        newProduct.setCategory(productRequest.category());
        newProduct.setImageUrl(productRequest.imageUrl());
        newProduct.setTaxRate(productRequest.taxRate());
        List<ProductAttribute> productAttributes = mapProductAttributes(newProduct, productRequest);
        newProduct.setAttributes(productAttributes);
        newProduct.setCreatedAt(LocalDateTime.now());
        newProduct.setUpdatedAt(LocalDateTime.now());
        Product createdProduct = productRepository.save(newProduct);
        return "CREATED";
    }

    public List<ProductAttribute> mapProductAttributes(Product newProduct, ProductRequest productRequest){
        List<ProductAttribute> productAttributes = new ArrayList<>();

        for(ProductAttributeRequest productAttributeRequest : productRequest.attributes()){
            //Fetch Attribute Type
            AttributeType attributeType = attributeRepository.findById(productAttributeRequest.attributeTypeId())
                    .orElseThrow(() -> new IllegalArgumentException("Attribute type not found with id: " + productAttributeRequest.attributeTypeId()));

            ProductAttribute productAttribute = new ProductAttribute();
            productAttribute.setProduct(newProduct);
            productAttribute.setAttributeType(attributeType);
            productAttribute.setLabel(productAttributeRequest.label());
            List<ProductAttributeValue> productAttributeValues = getProductValuesSet(productAttribute,productAttributeRequest);
            productAttribute.setValues(productAttributeValues);

            productAttributes.add(productAttribute);
        }

        return productAttributes;
    }

    public List<ProductAttributeValue> getProductValuesSet(ProductAttribute productAttribute, ProductAttributeRequest productAttributeRequest){
        List<ProductAttributeValue> productAttributeValues = new ArrayList<>();

        for(ProductAttributeValueRequest productAttributeValueRequest : productAttributeRequest.values()){

            //Fetch Attribute Values
            AttributeValue attributeValue = attributeValueRepository.findById(productAttributeValueRequest.attributeValueId())
                    .orElseThrow(() -> new IllegalArgumentException("Attribute value not found with id: " + productAttributeValueRequest.attributeValueId()));

            ProductAttributeValue productAttributeValue = new ProductAttributeValue();
            productAttributeValue.setProductAttribute(productAttribute);
            productAttributeValue.setAttributeValue(attributeValue);
            productAttributeValue.setExtraPrice(productAttributeValueRequest.extraPrice());
            productAttributeValues.add(productAttributeValue);
        }

        return productAttributeValues;
    }


    public List<ProductResponse> getProducts() {
        List<Product> allProducts = productRepository.findAll();
        List<ProductResponse> productResponses = new ArrayList<>();

        for(Product product : allProducts){
            Set<ProductAttributeResponse> productAttributeResponses = new HashSet<>();

            for(ProductAttribute productAttribute : product.getAttributes()){

                List<ProductAttributeValueResponse> productAttributeValueResponses = new ArrayList<>();
                for(ProductAttributeValue productAttributeValue : productAttribute.getValues()){
                    ProductAttributeValueResponse productAttributeValueResponse = new ProductAttributeValueResponse(
                            productAttributeValue.getId(),
                            productAttributeValue.getAttributeValue().getId(),
                            productAttributeValue.getAttributeValue().getValue(),
                            productAttributeValue.getExtraPrice()
                    );
                    productAttributeValueResponses.add(productAttributeValueResponse);
                }


                ProductAttributeResponse productAttributeResponse = new ProductAttributeResponse(
                        productAttribute.getId(),
                        productAttribute.getAttributeType().getId(),
                        productAttribute.getLabel(),
                        productAttributeValueResponses
                );
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
                    .attributes(productAttributeResponses)
                    .createdAt(product.getCreatedAt())
                    .updatedAt(product.getUpdatedAt())
                    .build();


            productResponses.add(productResponse);
        }
        return productResponses;
    }

    public String updateProduct(Long id, UpdateProductRequest updateProductRequest) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product Not Found"));

        existingProduct.setName(updateProductRequest.name());
        existingProduct.setDescription(updateProductRequest.description());
        existingProduct.setBasePrice(updateProductRequest.basePrice());
        existingProduct.setTaxRate(updateProductRequest.taxRate());
        existingProduct.setStock(updateProductRequest.stock());
        existingProduct.setUpdatedAt(updateProductRequest.updatedAt());
        existingProduct.setCategory(updateProductRequest.category());
        existingProduct.setImageUrl(updateProductRequest.imageUrl());

        // Existing values (Hibernate-managed)
        List<ProductAttribute> currentAttributes = existingProduct.getAttributes();
        List<Long> incomingIds = updateProductRequest.productAttributeRequestList().stream()
                .map(UpdateProductAttributeRequest::id)
                .toList();

        currentAttributes.removeIf(val -> !incomingIds.contains(val.getId()));


        // Step 2 : Update Existing Ones + Add new ones
        for(UpdateProductAttributeRequest updateProductAttributeRequest  : updateProductRequest.productAttributeRequestList()){
            if(updateProductAttributeRequest.id() != null){
                ProductAttribute existingProductAttribute = currentAttributes.stream()
                        .filter(attr -> updateProductAttributeRequest.id().equals(attr.getId()))
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException("Product Attribute not Found "+ updateProductAttributeRequest.id()));

                existingProductAttribute.setLabel(updateProductAttributeRequest.label());

                List<ProductAttributeValue> existingProdAttrValues = existingProductAttribute.getValues();
                List<Long> incomingProdAttrValueIds = updateProductAttributeRequest.productAttributeValueRequestList().stream()
                        .map(UpdateProductAttributeValueRequest::id)
                        .toList();
                existingProdAttrValues.removeIf(attrVal -> !incomingProdAttrValueIds.contains(attrVal.getId()));

                //Updating or Creating Product Attribute values
                for(UpdateProductAttributeValueRequest updateProductAttributeValueRequest  : updateProductAttributeRequest.productAttributeValueRequestList()){

                    if(updateProductAttributeValueRequest.id() != null) {
                        ProductAttributeValue existingProdAttrValue = existingProdAttrValues.stream()
                                .filter(val -> updateProductAttributeValueRequest.id().equals(val.getId()))
                                .findFirst()
                                .orElseThrow(() -> new RuntimeException("Prod Atttr value Not found " + updateProductAttributeValueRequest.id()));

                        existingProdAttrValue.setExtraPrice(updateProductAttributeValueRequest.extraPrice());
                    }
                    else{
                        //create new prod attr value
                        ProductAttributeValue newProdAttrvalue = new ProductAttributeValue();
                        newProdAttrvalue.setProductAttribute(existingProductAttribute);
                        AttributeValue attributeValue = attributeValueRepository.findById(updateProductAttributeValueRequest.id()).orElseThrow(() -> new RuntimeException("Attribute Value is not valid 1"));
                        newProdAttrvalue.setAttributeValue(attributeValue);
                        newProdAttrvalue.setExtraPrice(updateProductAttributeValueRequest.extraPrice());

                        existingProdAttrValues.add(newProdAttrvalue);
                    }
                }
            }
            else{
                ProductAttribute newProductAttribute = new ProductAttribute();
                newProductAttribute.setProduct(existingProduct);

                //fetch Attribute Type
                AttributeType attributeType = attributeRepository.findById(updateProductAttributeRequest.attributeTypeId())
                        .orElseThrow(() -> new RuntimeException("Attribute Type not found"));

                newProductAttribute.setAttributeType(attributeType);
                newProductAttribute.setLabel(updateProductAttributeRequest.label());

                List<ProductAttributeValue> productAttributeValueList = new ArrayList<>();
                for(UpdateProductAttributeValueRequest updateProdAttrValueRequest : updateProductAttributeRequest.productAttributeValueRequestList()){

                    ProductAttributeValue newProdAttrValue = new ProductAttributeValue();
                    AttributeValue attributeValue = attributeValueRepository.findById(updateProdAttrValueRequest.AttributeValueId())
                            .orElseThrow(() -> new RuntimeException("Attribute Value not found" + updateProdAttrValueRequest.AttributeValueId()));


                    newProdAttrValue.setProductAttribute(newProductAttribute);
                    newProdAttrValue.setAttributeValue(attributeValue);
                    newProdAttrValue.setExtraPrice(updateProdAttrValueRequest.extraPrice());
                    productAttributeValueList.add(newProdAttrValue);
                }
                newProductAttribute.setValues(productAttributeValueList);
                currentAttributes.add(newProductAttribute);

            }

        }


        productRepository.save(existingProduct);

        return "Updated";
    }



    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new IllegalArgumentException("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
    }



}
