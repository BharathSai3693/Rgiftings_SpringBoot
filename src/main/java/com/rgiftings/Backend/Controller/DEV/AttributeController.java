package com.rgiftings.Backend.Controller.DEV;

import com.rgiftings.Backend.Model.AttributeType;
import com.rgiftings.Backend.Repository.DEV.AttributeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/attribute")
    public List<AttributeType> getAttributes() {
        System.out.println("vachindhiii");
        attributeRepository.findAll().stream().forEach(attributeType -> {
            System.out.println(attributeType);
        });
        System.out.println("retunrded");
        return attributeRepository.findAll();
    }

    @GetMapping("/attribute/{id}")
    public ResponseEntity<AttributeType> getAttribute(@PathVariable Long id) {
        return attributeRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/attribute/{id}")
    public ResponseEntity<String> updateAttribute(@PathVariable Long id, @RequestBody AttributeType attributeType) {
        return attributeRepository.findById(id)
                .map(existing -> {
                    existing.setName(attributeType.getName());
                    existing.setDescription(attributeType.getDescription());

                    if (attributeType.getValues() != null) {
                        attributeType.getValues().forEach(value -> value.setAttributeType(existing));
                    }

                    existing.setValues(attributeType.getValues());
                    attributeRepository.save(existing);
                    return ResponseEntity.ok("Updated Attribute");
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/attribute/{id}")
    public ResponseEntity<Void> deleteAttribute(@PathVariable Long id) {
        if (!attributeRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        attributeRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }



}
