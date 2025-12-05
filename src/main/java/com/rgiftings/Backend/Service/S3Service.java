package com.rgiftings.Backend.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Presigner preSigner;
    private final S3Client s3Client;

    private final String BUCKET = "rgiftingz";

    public String getPresignedUrl(String filename) {
        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(BUCKET)
                .key("Products/"+filename)
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(10))
                .putObjectRequest(objectRequest)
                .build();

        String url = preSigner.presignPutObject(presignRequest).url().toString();
        return url;
    }

    public void deleteFile(String key){
        DeleteObjectRequest deleteRequest =  DeleteObjectRequest.builder()
                .bucket(BUCKET)
                .key(key)
                .build();

        s3Client.deleteObject(deleteRequest);
    }
}
