package com.rgiftings.Backend.Service;

import com.rgiftings.Backend.DTO.Attribute.Create.AttributeTypeRequest;
import com.rgiftings.Backend.DTO.Attribute.Retrieve.AttributeTypeResponse;
import com.rgiftings.Backend.DTO.Attribute.Create.AttributeValueRequest;
import com.rgiftings.Backend.DTO.Attribute.Retrieve.AttributeValueResponse;
import com.rgiftings.Backend.DTO.Attribute.Update.UpdateAttributeRequest;
import com.rgiftings.Backend.DTO.Attribute.Update.UpdateAttributeValueRequest;
import com.rgiftings.Backend.Model.Attribute.AttributeType;
import com.rgiftings.Backend.Model.Attribute.AttributeValue;
import com.rgiftings.Backend.Repository.AttributeRepository;
import com.rgiftings.Backend.Repository.AttributeValueRepository;
import com.rgiftings.Backend.Repository.ProductAttributeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class AttributeService {

    @Autowired
    private AttributeRepository attributeRepository;

    @Autowired
    private AttributeValueRepository attributeValueRepository;

    @Autowired
    private ProductAttributeRepository productAttributeRepository;

    public String createAttribute(AttributeTypeRequest attributeTypeRequest) {

        AttributeType attributeType = AttributeType.builder()
                .name(attributeTypeRequest.name())
                .inputType(attributeTypeRequest.inputType())
                .build();

        List<AttributeValue> attributeValueList = new ArrayList<>();
        for(AttributeValueRequest attributeValueRequest : attributeTypeRequest.attributeValues()){
            AttributeValue attributeValue = AttributeValue.builder()
                    .attributeType(attributeType)
                    .value(attributeValueRequest.value())
                    .build();

            attributeValueList.add(attributeValue);
        }

        attributeType.setAttributeValues(attributeValueList);
        attributeRepository.save(attributeType);
        return "Attribute Created";
    }

    public List<AttributeTypeResponse> getAllAttributes() {
        List<AttributeType> attributes = attributeRepository.findAll();

        List<AttributeTypeResponse> attributeTypeResponses = new ArrayList<>();
        for(AttributeType attributeType : attributes){

            List<AttributeValueResponse> attributeValueResponseList = new ArrayList<>();
            for(AttributeValue attributeValue : attributeType.getAttributeValues()){
                AttributeValueResponse attributeValueResponse = AttributeValueResponse.builder()
                        .id(attributeValue.getId())
                        .value(attributeValue.getValue())
                        .build();

                attributeValueResponseList.add(attributeValueResponse);
            }

            AttributeTypeResponse attributeTypeResponse = AttributeTypeResponse.builder()
                    .id(attributeType.getId())
                    .name(attributeType.getName())
                    .inputType(attributeType.getInputType())
                    .attributeValues(attributeValueResponseList)
                    .build();

            attributeTypeResponses.add(attributeTypeResponse);
        }

        return  attributeTypeResponses;

    }

    public String updateAttribute(Long id, UpdateAttributeRequest updateAttributeRequest) {

        AttributeType existing = attributeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Attribute Type not found id : " + id));

        // update simple fields
        existing.setName(updateAttributeRequest.name());
        existing.setInputType(updateAttributeRequest.inputType());

        // Existing values (Hibernate-managed)
        List<AttributeValue> currentValues = existing.getAttributeValues();
        List<Long> incomingIds = updateAttributeRequest.attributeValues().stream()
                .map(UpdateAttributeValueRequest::id)
                .filter(attrValId -> attrValId != null)
                .toList();

        currentValues.removeIf(val -> !incomingIds.contains(val.getId()));

        // Step 2: Update existing ones + add new ones
        for (UpdateAttributeValueRequest updateAttributeValueRequest : updateAttributeRequest.attributeValues()) {

            if (updateAttributeValueRequest.id() != null) {
                // Update existing value
                AttributeValue existingValue = currentValues.stream()
                        .filter(v -> updateAttributeValueRequest.id().equals(v.getId()))
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException("Value not found: " + updateAttributeValueRequest.id()));
                existingValue.setValue(updateAttributeValueRequest.value());
            } else {
                // Create NEW value
                AttributeValue newAttributevalue = new AttributeValue();
                newAttributevalue.setValue(updateAttributeValueRequest.value());
                newAttributevalue.setAttributeType(existing);
                currentValues.add(newAttributevalue);
            }
        }
        attributeRepository.save(existing);
        return "UPDATED";
    }

    public ResponseEntity<String> deleteAttribute(Long id) {

        if (!attributeRepository.existsById(id)) {
            return new ResponseEntity<>("NOT FOUND", HttpStatus.NOT_FOUND);
        }

        long referencedCount = productAttributeRepository.countByAttributeType_Id(id);
        if (referencedCount > 0) {
            return new ResponseEntity<>("ATTRIBUTE IN USE", HttpStatus.CONFLICT);
        }

        try {
            attributeRepository.deleteById(id);
        } catch (DataIntegrityViolationException ex) {
            return new ResponseEntity<>("ATTRIBUTE IN USE", HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>("DELETED", HttpStatus.OK);
    }

}


