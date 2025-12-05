package com.rgiftings.Backend.Service;

import com.rgiftings.Backend.DTO.Product.Create.ProductAttributeRequest;
import com.rgiftings.Backend.DTO.Product.Create.ProductImageRequest;
import com.rgiftings.Backend.DTO.Product.Retrieve.ProductAttributeResponse;
import com.rgiftings.Backend.DTO.Product.Create.ProductAttributeValueRequest;
import com.rgiftings.Backend.DTO.Product.Retrieve.ProductAttributeValueResponse;
import com.rgiftings.Backend.DTO.Product.Create.ProductRequest;
import com.rgiftings.Backend.DTO.Product.Retrieve.ProductImageResponse;
import com.rgiftings.Backend.DTO.Product.Retrieve.ProductResponse;
import com.rgiftings.Backend.DTO.Product.Update.UpdateProductAttributeRequest;
import com.rgiftings.Backend.DTO.Product.Update.UpdateProductAttributeValueRequest;
import com.rgiftings.Backend.DTO.Product.Update.UpdateProductImageRequest;
import com.rgiftings.Backend.DTO.Product.Update.UpdateProductRequest;
import com.rgiftings.Backend.Model.Attribute.AttributeType;
import com.rgiftings.Backend.Model.Attribute.AttributeValue;
import com.rgiftings.Backend.Model.Product.Product;
import com.rgiftings.Backend.Model.Product.ProductAttribute;
import com.rgiftings.Backend.Model.Product.ProductAttributeValue;
import com.rgiftings.Backend.Model.Product.ProductImage;
import com.rgiftings.Backend.Repository.AttributeRepository;
import com.rgiftings.Backend.Repository.AttributeValueRepository;
import com.rgiftings.Backend.Repository.OrderRepository;
import com.rgiftings.Backend.Repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Map;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private AttributeRepository attributeRepository;

    @Autowired
    private AttributeValueRepository attributeValueRepository;

    @Autowired
    private OrderRepository orderRepository;

    //CREATE PRODUCT
    public String createProduct(ProductRequest productRequest) {

        Product newProduct = Product.builder()
                .name(productRequest.name())
                .description(productRequest.description())
                .basePrice(productRequest.basePrice())
                .stock(productRequest.stock())
                .category(productRequest.category())
                .taxRate(productRequest.taxRate())
                .build();


        List<ProductImage> productImages = new ArrayList<>();
        if(productRequest.imageUrls() != null){
            for(ProductImageRequest productImageRequest : productRequest.imageUrls()){
                ProductImage productImage = ProductImage.builder()
                        .product(newProduct)
                        .imageUrl(productImageRequest.imageUrl())
                        .isPrimary(productImageRequest.isPrimary())
                        .createdAt(productImageRequest.createdAt())
                        .build();
                productImages.add(productImage);
            }
        }

        newProduct.setImages(productImages);

        List<ProductAttribute> productAttributes = mapProductAttributes(newProduct, productRequest);
        newProduct.setProductAttributes(productAttributes);
        newProduct.setCreatedAt(LocalDateTime.now());
        newProduct.setUpdatedAt(LocalDateTime.now());
        Product createdProduct = productRepository.save(newProduct);
        return "CREATED";
    }

    private List<ProductImage> mapUpdateProductImages(Product product, List<ProductImage> existingImages, List<UpdateProductImageRequest> imageRequests) {
        List<ProductImage> updatedImages = new ArrayList<>();
        if (imageRequests == null) {
            return updatedImages;
        }

        Map<Long, ProductImage> imagesById = existingImages == null
                ? Map.of()
                : existingImages.stream()
                .filter(img -> img.getId() != null)
                .collect(Collectors.toMap(ProductImage::getId, img -> img));

        boolean primaryAssigned = false;
        for (UpdateProductImageRequest imageRequest : imageRequests) {
            if (imageRequest == null || imageRequest.imageUrl() == null || imageRequest.imageUrl().isBlank()) {
                continue;
            }

            ProductImage image;
            if (imageRequest.id() != null) {
                image = imagesById.get(imageRequest.id());
                if (image == null) {
                    throw new RuntimeException("Product image not found with id: " + imageRequest.id());
                }
            } else {
                image = new ProductImage();
                image.setProduct(product);
            }

            image.setImageUrl(imageRequest.imageUrl());

            boolean isPrimary = Boolean.TRUE.equals(imageRequest.isPrimary()) && !primaryAssigned;
            image.setIsPrimary(isPrimary);
            if (isPrimary) {
                primaryAssigned = true;
            }

            if (imageRequest.createdAt() != null) {
                image.setCreatedAt(imageRequest.createdAt());
            } else if (image.getCreatedAt() == null) {
                image.setCreatedAt(LocalDateTime.now());
            }

            updatedImages.add(image);
        }

        // Ensure exactly one primary image
        if (!primaryAssigned && !updatedImages.isEmpty()) {
            updatedImages.get(0).setIsPrimary(true);
            for (int i = 1; i < updatedImages.size(); i++) {
                updatedImages.get(i).setIsPrimary(false);
            }
        } else if (primaryAssigned) {
            boolean seenPrimary = false;
            for (ProductImage image : updatedImages) {
                if (Boolean.TRUE.equals(image.getIsPrimary()) && !seenPrimary) {
                    seenPrimary = true;
                } else {
                    image.setIsPrimary(false);
                }
            }
        }

        return updatedImages;
    }

    public List<ProductAttribute> mapProductAttributes(Product newProduct, ProductRequest productRequest){
        List<ProductAttribute> productAttributes = new ArrayList<>();
        if (productRequest.productAttributes() == null) {
            return productAttributes;
        }

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
            List<ProductImageResponse> imageUrls = new ArrayList<>();


            if (product.getProductAttributes() == null) {
                product.setProductAttributes(new ArrayList<>());
            }


            if (product.getImages() != null) {
                imageUrls = product.getImages().stream()
                        .map(image -> ProductImageResponse.builder()
                                .id(image.getId())
                                .imageUrl(image.getImageUrl())
                                .isPrimary(image.getIsPrimary())
                                .createdAt(image.getCreatedAt())
                                .build()
                        )
                        .collect(Collectors.toList());
            }


            for(ProductAttribute productAttribute : product.getProductAttributes()){

                List<ProductAttributeValueResponse> productAttributeValueResponses = new ArrayList<>();
                for(ProductAttributeValue productAttributeValue : productAttribute.getProductAttributeValues()){

                    AttributeValue attributeValue = productAttributeValue.getAttributeValue();
                    ProductAttributeValueResponse productAttributeValueResponse = ProductAttributeValueResponse.builder()
                            .id(productAttributeValue.getId())
                            .attributeValueId(attributeValue.getId())
                            .attributeValueName(attributeValue.getValue())
                            .extraPrice(productAttributeValue.getExtraPrice())
                            .build();

                    productAttributeValueResponses.add(productAttributeValueResponse);
                }

                AttributeType attributeType = productAttribute.getAttributeType();
                ProductAttributeResponse productAttributeResponse = ProductAttributeResponse.builder()
                        .id(productAttribute.getId())
                        .attributeTypeId(attributeType.getId())
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
                    .imageUrls(imageUrls)
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
        existingProduct.setTaxRate(updateRequest.taxRate());
        existingProduct.setUpdatedAt(LocalDateTime.now());

        // Replace images (respecting DTO structure and primary flag)
        if (existingProduct.getImages() == null) {
            existingProduct.setImages(new ArrayList<>());
        }
        List<ProductImage> updatedImages = mapUpdateProductImages(existingProduct, existingProduct.getImages(), updateRequest.imageUrls());
        existingProduct.getImages().clear();
        existingProduct.getImages().addAll(updatedImages);

        // STEP 3: Existing attributes
        List<ProductAttribute> existingAttributes = existingProduct.getProductAttributes();
        if (existingAttributes == null) {
            existingAttributes = new ArrayList<>();
            existingProduct.setProductAttributes(existingAttributes);
        }

        List<UpdateProductAttributeRequest> incomingAttributes = updateRequest.productAttributes() != null
                ? updateRequest.productAttributes()
                : new ArrayList<>();

        // Collect incoming attribute IDs
        List<Long> incomingAttributeIds = incomingAttributes.stream()
                .map(UpdateProductAttributeRequest::id)
                .filter(idVal -> idVal != null)
                .toList();

        // REMOVE attributes not present in incoming list
        existingAttributes.removeIf(attr -> !incomingAttributeIds.contains(attr.getId()));

        // STEP 4: Loop through all incoming attributes
        for (UpdateProductAttributeRequest incomingAttr : incomingAttributes) {

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

                // Prevent AttributeType change (would cause data inconsistency)
                if (incomingAttr.attributeTypeId() != null) {
                    // Add null check for existingAttr.getAttributeType()
                    if (existingAttr.getAttributeType() == null) {
                        throw new RuntimeException("Product Attribute has no AttributeType set");
                    }

                    if (!existingAttr.getAttributeType().getId().equals(incomingAttr.attributeTypeId())) {
                        throw new RuntimeException("Cannot change AttributeType of existing ProductAttribute. Delete and recreate instead.");
                    }
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

    //DELETING PRODUCT
    public String deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new IllegalArgumentException("Product not found with id: " + id);
        }

        long referencedOrders = orderRepository.countOrderItemsByProductId(id);
        if (referencedOrders > 0) {
            throw new IllegalStateException("Cannot delete product: it is referenced by existing orders");
        }

        try {
            productRepository.deleteById(id);
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalStateException("Cannot delete product due to existing references", ex);
        }
        return "DELETED";
    }


}
