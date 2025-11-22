package com.rgiftings.Backend.Controller.DEV;

import com.rgiftings.Backend.Model.AttributeType;
import com.rgiftings.Backend.Repository.DEV.AttributeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class AttributeController {

    @Autowired
    private AttributeRepository attributeRepository;


    @PostMapping("/attribute")
    public String createAttribute(@RequestBody AttributeType attributeType){
        // Set parent reference for all child values
        if (attributeType.getValues() != null) {
            attributeType.getValues().forEach(v -> v.setAttributeType(attributeType));
        }

        attributeRepository.save(attributeType);
        return "Created Attribute";
    }

}
