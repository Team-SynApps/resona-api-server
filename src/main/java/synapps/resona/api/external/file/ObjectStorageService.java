package synapps.resona.api.external.file;

import com.oracle.bmc.objectstorage.ObjectStorage;
import com.oracle.bmc.objectstorage.requests.GetObjectRequest;
import com.oracle.bmc.objectstorage.requests.PutObjectRequest;
import com.oracle.bmc.objectstorage.responses.GetObjectResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import synapps.resona.api.global.config.StorageProperties;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class ObjectStorageService {
    private final ObjectStorage objectStorageClient;
    private final StorageProperties storageProperties;

    public String uploadFile(String objectName, MultipartFile file) throws IOException {
        PutObjectRequest request = PutObjectRequest.builder()
                .bucketName(storageProperties.getBucketName())
                .namespaceName(storageProperties.getNamespace())
                .objectName(objectName)
                .contentType(file.getContentType())
                .putObjectBody(file.getInputStream())
                .build();

        objectStorageClient.putObject(request);

        return String.format("https://objectstorage.%s.oraclecloud.com/n/%s/b/%s/o/%s",
                storageProperties.getRegion(),  // 여기를 수정
                storageProperties.getNamespace(),
                storageProperties.getBucketName(),
                objectName);
    }

    public byte[] downloadFile(String objectName) throws IOException {
        GetObjectRequest request = GetObjectRequest.builder()
                .bucketName(storageProperties.getBucketName())
                .namespaceName(storageProperties.getNamespace())
                .objectName(objectName)
                .build();

        GetObjectResponse response = objectStorageClient.getObject(request);
        return response.getInputStream().readAllBytes();
    }
}