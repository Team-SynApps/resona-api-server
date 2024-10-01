package com.synapps.atch.external.file;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;

@Component
@RequiredArgsConstructor
public class AmazonS3ResourceStorage {

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private final AmazonS3Client amazonS3Client;


    public void store(String fullPath, MultipartFile multipartFile) {
        try {
            byte[] fileBytes = StreamUtils.copyToByteArray(multipartFile.getInputStream());
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(fileBytes.length);

            amazonS3Client.putObject(new PutObjectRequest(bucket, fullPath, new ByteArrayInputStream(fileBytes), metadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
