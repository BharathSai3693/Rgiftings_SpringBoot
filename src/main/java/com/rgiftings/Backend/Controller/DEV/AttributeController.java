package com.rgiftings.Backend.Controller.DEV;

import com.rgiftings.Backend.DTO.Attribute.AttributeTypeRequest;
import com.rgiftings.Backend.DTO.Attribute.AttributeTypeResponse;
import com.rgiftings.Backend.Model.AttributeType;
import com.rgiftings.Backend.Repository.DEV.AttributeRepository;
import com.rgiftings.Backend.Service.AttributeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class AttributeController {

    @Autowired
    private AttributeRepository attributeRepository;

    @Autowired
    private AttributeService attributeService;


    @GetMapping("/attribute")
    public ResponseEntity<List<AttributeTypeResponse>> getAllAttributes() {
        List<AttributeTypeResponse> attributes = attributeService.getAllAttributes();
        return new ResponseEntity<>(attributes, HttpStatus.OK);
    }


    @PostMapping("/attribute")
    public String createAttribute(@RequestBody AttributeTypeRequest attributeTypeRequest){
        attributeService.createAttribute(attributeTypeRequest);
        return "created";
    }



    @PutMapping("/attribute/{id}")
    public ResponseEntity<String> updateAttribute(@PathVariable Long id, @RequestBody AttributeType attributeType) {
        String result = attributeService.updateAttibute(id, attributeType);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @DeleteMapping("/attribute/{id}")
    public ResponseEntity<String> deleteAttribute(@PathVariable Long id) {
        ResponseEntity<String> result = attributeService.deleteAttribute(id);
        return result;
    }



}
