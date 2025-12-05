package com.rgiftings.Backend.Controller;


import com.rgiftings.Backend.Service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/s3")
@RequiredArgsConstructor
@CrossOrigin("*")
public class S3Controller {

    @Autowired
    private S3Service s3Service;

    @GetMapping("/presign")
    public Map<String, String> getPresignedUrl(@RequestParam String filename){

        String url = s3Service.getPresignedUrl(filename);
        Map<String, String> response = new HashMap<>();
        response.put("url", url);
        return response;

    }

    @GetMapping("/deleteImage/{key}")
    public String deleteFile(@PathVariable String key){
        s3Service.deleteFile(key);
        return "Deleted Image";
    }

}
