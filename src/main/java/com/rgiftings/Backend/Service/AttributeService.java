package com.rgiftings.Backend.Service;

import com.rgiftings.Backend.DTO.Attribute.AttributeTypeRequest;
import com.rgiftings.Backend.DTO.Attribute.AttributeTypeResponse;
import com.rgiftings.Backend.DTO.Attribute.AttributeValueRequest;
import com.rgiftings.Backend.DTO.Attribute.AttributeValueResponse;
import com.rgiftings.Backend.Model.AttributeType;
import com.rgiftings.Backend.Model.AttributeValue;
import com.rgiftings.Backend.Repository.DEV.AttributeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AttributeService {

    @Autowired
    private AttributeRepository attributeRepository;

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

    public String updateAttibute(Long id, AttributeType attributeType) {
        Optional<AttributeType> optional = attributeRepository.findById(id);

        if(optional.isEmpty()){
            return "NOT FOUND";
        }

        AttributeType existingAttributeType = optional.get();
        existingAttributeType.setType(attributeType.getType());
        existingAttributeType.setDescription(attributeType.getDescription());

        if (attributeType.getValues() != null) {
            attributeType.getValues().forEach(value -> value.setAttributeType(existingAttributeType));
        }

        existingAttributeType.setValues(attributeType.getValues());
        attributeRepository.save(existingAttributeType);
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
}



