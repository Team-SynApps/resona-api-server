package synapps.resona.api.external.file;

import com.oracle.bmc.objectstorage.ObjectStorage;
import com.oracle.bmc.objectstorage.model.CopyObjectDetails;
import com.oracle.bmc.objectstorage.requests.CopyObjectRequest;
import com.oracle.bmc.objectstorage.requests.GetObjectRequest;
import com.oracle.bmc.objectstorage.requests.PutObjectRequest;
import com.oracle.bmc.objectstorage.responses.GetObjectResponse;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import synapps.resona.api.external.file.dto.FileMetadataDto;
import synapps.resona.api.global.config.StorageProperties;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ObjectStorageService {
    private final ObjectStorage objectStorageClient;
    private final StorageProperties storageProperties;
    private final Logger logger = LogManager.getLogger(ObjectStorageService.class);

    // 버퍼 버킷에 임시 저장
    public FileMetadataDto uploadToBuffer(MultipartFile file) throws IOException {
        String temporaryFileName = generateTemporaryFileName(file.getOriginalFilename());

        PutObjectRequest request = PutObjectRequest.builder()
                .bucketName(storageProperties.getBufferBucketName())
                .namespaceName(storageProperties.getNamespace())
                .objectName(temporaryFileName)
                .contentType(file.getContentType())
                .putObjectBody(file.getInputStream())
                .build();

        objectStorageClient.putObject(request);

        return FileMetadataDto.builder()
                .originalFileName(file.getOriginalFilename())
                .temporaryFileName(temporaryFileName)
                .uploadTime(LocalDateTime.now().toString())
                .contentType(file.getContentType())
                .fileSize(file.getSize())
                .build();
    }

    // 디스크 버킷으로 복사
    public String copyToDisk(FileMetadataDto metadata, String finalFileName) {
        CopyObjectDetails copyObjectDetails = CopyObjectDetails.builder()
                .sourceObjectName(metadata.getTemporaryFileName())
                .destinationBucket(storageProperties.getDiskBucketName())
                .destinationNamespace(storageProperties.getNamespace())
                .destinationObjectName(finalFileName)
                .destinationRegion(storageProperties.getRegion())
                .build();

        CopyObjectRequest copyRequest = CopyObjectRequest.builder()
                .namespaceName(storageProperties.getNamespace())
                .bucketName(storageProperties.getBufferBucketName())
                .copyObjectDetails(copyObjectDetails)
                .build();

        objectStorageClient.copyObject(copyRequest);
        return generateFileUrl(storageProperties.getDiskBucketName(), finalFileName);
    }

    private String generateTemporaryFileName(String originalFileName) {
        return String.format("%s_%s_%s",
                UUID.randomUUID().toString(),
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")),
                originalFileName
        );
    }

    private String generateFileUrl(String bucketName, String objectName) {
        return String.format("https://objectstorage.%s.oraclecloud.com/n/%s/b/%s/o/%s",
                storageProperties.getRegion(),
                storageProperties.getNamespace(),
                bucketName,
                objectName);
    }

    // 기존 다운로드 메소드는 버킷 이름을 파라미터로 받도록 수정
    public byte[] downloadFile(String bucketName, String objectName) throws IOException {
        GetObjectRequest request = GetObjectRequest.builder()
                .bucketName(bucketName)
                .namespaceName(storageProperties.getNamespace())
                .objectName(objectName)
                .build();

        GetObjectResponse response = objectStorageClient.getObject(request);
        return response.getInputStream().readAllBytes();
    }

//    public List<String> copyMultipleToDisk(List<FileMetadataDto> metadataList) {
//        return metadataList.parallelStream()
//                .map(metadata -> {
//                    try {
//                        String finalFileName = generateFinalFileName(metadata); // 파일명 생성 메서드 필요
//                        CopyObjectDetails copyObjectDetails = CopyObjectDetails.builder()
//                                .sourceObjectName(metadata.getOriginalFileName())
//                                .destinationBucket(storageProperties.getDiskBucketName())
//                                .destinationNamespace(storageProperties.getNamespace())
//                                .destinationObjectName(finalFileName)
//                                .destinationRegion(storageProperties.getRegion())
//                                .build();
//
//                        CopyObjectRequest copyRequest = CopyObjectRequest.builder()
//                                .namespaceName(storageProperties.getNamespace())
//                                .bucketName(storageProperties.getBufferBucketName())
//                                .copyObjectDetails(copyObjectDetails)
//                                .build();
//
//                        objectStorageClient.copyObject(copyRequest);
//                        return generateFileUrl(storageProperties.getDiskBucketName(), finalFileName);
//                    } catch (BmcException e) {
//                        logger.error("Failed to copy file: {}", metadata.getOriginalFileName(), e);
//                        throw new RuntimeException("Failed to copy file: " + metadata.getOriginalFileName(), e);
//                    }
//                })
//                .collect(Collectors.toList());
//    }
}