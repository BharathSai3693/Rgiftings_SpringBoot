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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AttributeService {

    @Autowired
    private AttributeRepository attributeRepository;

    @Autowired
    private AttributeValueRepository attributeValueRepository;

    public String createAttribute(AttributeTypeRequest attributeTypeRequest) {
        AttributeType attributeType = new AttributeType();
        attributeType.setType(attributeTypeRequest.type());
        attributeType.setDescription(attributeTypeRequest.description());

        List<AttributeValue> attributeValues = new ArrayList<>();
        for(AttributeValueRequest attributeValueRequest : attributeTypeRequest.attributeValueRequests()){
            AttributeValue attributeValue = new AttributeValue();
            attributeValue.setAttributeType(attributeType);
            attributeValue.setValue(attributeValueRequest.value());
            attributeValue.setDisplayCode(attributeValueRequest.displayCode());

            attributeValues.add(attributeValue);
        }
        attributeType.setValues(attributeValues);

        attributeRepository.save(attributeType);
        return "Attribute Created";
    }


    public List<AttributeTypeResponse> getAllAttributes() {
        List<AttributeType> attributes = attributeRepository.findAll();
        List<AttributeTypeResponse> attributeTypeResponses = new ArrayList<>();
        for(AttributeType attributeType : attributes){

            List<AttributeValueResponse> attributeValueResponses = new ArrayList<>();
            for(AttributeValue attributeValue  : attributeType.getValues()){
                AttributeValueResponse attributeValueResponse = new AttributeValueResponse(
                        attributeValue.getId(),
                        attributeValue.getDisplayCode(),
                        attributeValue.getValue()
                );
                attributeValueResponses.add(attributeValueResponse);
            }

            AttributeTypeResponse attributeTypeResponse = new AttributeTypeResponse(
                    attributeType.getId(),
                    attributeType.getType(),
                    attributeType.getDescription(),
                    attributeValueResponses
            );
            attributeTypeResponses.add(attributeTypeResponse);
        }
        return attributeTypeResponses;

    }

    public String updateAttribute(Long id, UpdateAttributeRequest updateAttributeRequest) {

        AttributeType existing = attributeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Attribute not found"));

        // update simple fields
        existing.setType(updateAttributeRequest.type());
        existing.setDescription(updateAttributeRequest.description());

        // Existing values (Hibernate-managed)
        List<AttributeValue> currentValues = existing.getValues();
        List<Long> incomingIds = updateAttributeRequest.attributeValues().stream()
                .map(UpdateAttributeValueRequest::id)
                .toList();

        currentValues.removeIf(val -> !incomingIds.contains(val.getId()));

        // Step 2: Update existing ones + add new ones
        for (UpdateAttributeValueRequest reqVal : updateAttributeRequest.attributeValues()) {

            if (reqVal.id() != null) {
                // Update existing value
                AttributeValue existingValue = currentValues.stream()
                        .filter(v -> reqVal.id().equals(v.getId()))
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException("Value not found: " + reqVal.id()));

                existingValue.setDisplayCode(reqVal.displayCode());
                existingValue.setValue(reqVal.value());

            } else {
                // Create NEW value
                AttributeValue newVal = new AttributeValue();
                newVal.setValue(reqVal.value());
                newVal.setDisplayCode(reqVal.displayCode());
                newVal.setAttributeType(existing);

                currentValues.add(newVal);
            }
        }

//        List<AttributeValue> attributeValues = new ArrayList<>();
//        for(UpdateAttributeValueRequest updateAttributeValueRequest : updateAttributeRequest.attributeValues()){
//            AttributeValue attributeValue = new AttributeValue();
//            attributeValue.setId(updateAttributeValueRequest.id());
//            attributeValue.setAttributeType(existing);
//            attributeValue.setDisplayCode(updateAttributeValueRequest.displayCode());
//            attributeValue.setValue(updateAttributeValueRequest.value());
//            attributeValues.add(attributeValue);
//        }
//        existing.setValues(attributeValues);


        attributeRepository.save(existing);

        return "UPDATED";

    }


    public ResponseEntity<String> deleteAttribute(Long id) {
        attributeRepository.existsById(id);

        if (!attributeRepository.existsById(id)) {
            return new ResponseEntity<>("NOT FOUND", HttpStatus.NOT_FOUND);
        }

        attributeRepository.deleteById(id);
        return new ResponseEntity<>("DELETED", HttpStatus.OK);
    }

    public AttributeTypeResponse getAttributeById(Long id) {
        AttributeType attributeType = attributeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No Attribute found with id : " + id));

        List<AttributeValueResponse> attributeValueResponses = new ArrayList<>();

        for(AttributeValue attributeValue : attributeType.getValues()){
            AttributeValueResponse attributeValueResponse = new AttributeValueResponse(
                    attributeValue.getId(),
                    attributeValue.getDisplayCode(),
                    attributeValue.getValue()
            );
            attributeValueResponses.add(attributeValueResponse);
        }

        AttributeTypeResponse attributeTypeResponse = new AttributeTypeResponse(
                attributeType.getId(),
                attributeType.getType(),
                attributeType.getDescription(),
                attributeValueResponses
        );

        return attributeTypeResponse;

    }
}



