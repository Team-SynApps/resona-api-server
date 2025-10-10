package com.synapps.resona.file;

import com.oracle.bmc.model.BmcException;
import com.oracle.bmc.objectstorage.ObjectStorage;
import com.oracle.bmc.objectstorage.model.CopyObjectDetails;
import com.oracle.bmc.objectstorage.requests.CopyObjectRequest;
import com.oracle.bmc.objectstorage.requests.GetObjectRequest;
import com.oracle.bmc.objectstorage.requests.PutObjectRequest;
import com.oracle.bmc.objectstorage.responses.GetObjectResponse;
import com.synapps.resona.file.code.FileErrorCode;
import com.synapps.resona.file.dto.FileMetadataDto;
import com.synapps.resona.file.exception.FileEmptyException;
import com.synapps.resona.config.database.StorageProperties;
import com.synapps.resona.service.MemberService;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.imageio.ImageIO;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ObjectStorageService {

  private final ObjectStorage objectStorageClient;
  private final StorageProperties storageProperties;
  private final MemberService memberService;
  private final Logger logger = LoggerFactory.getLogger(ObjectStorageService.class);

  // 버퍼 버킷에 임시 저장
  public FileMetadataDto uploadToBuffer(MultipartFile file, String userEmail) throws IOException {
    if (file == null || file.isEmpty()) {
      throw FileEmptyException.of(FileErrorCode.FILE_EMPTY_EXCEPTION.toString(),
          FileErrorCode.FILE_EMPTY_EXCEPTION.getStatus(), FileErrorCode.FILE_EMPTY_EXCEPTION.getCustomCode());
    }

    String temporaryFileName = generateTemporaryFileName(userEmail);

    try {
      uploadFileToBufferStorage(file, temporaryFileName);
      return createFileMetadata(file, temporaryFileName);
    } catch (Exception e) {
      logger.error("Failed to upload file: " + file.getOriginalFilename(), e);
      throw new FileUploadException("Failed to upload file", e);
    }
  }

  private void uploadFileToBufferStorage(MultipartFile file, String temporaryFileName)
      throws IOException {
    PutObjectRequest request = PutObjectRequest.builder()
        .bucketName(storageProperties.getBufferBucketName())
        .namespaceName(storageProperties.getNamespace())
        .objectName(temporaryFileName)
        .contentType(file.getContentType())
        .putObjectBody(file.getInputStream())
        .build();

    objectStorageClient.putObject(request);
    logger.info("File uploaded to buffer: {}", temporaryFileName);
  }

  private FileMetadataDto createFileMetadata(MultipartFile file, String temporaryFileName)
      throws IOException {
    BufferedImage bufferedImage = ImageIO.read(file.getInputStream());
    return FileMetadataDto.builder()
        .originalFileName(file.getOriginalFilename())
        .temporaryFileName(temporaryFileName)
        .uploadTime(LocalDateTime.now().toString())
        .contentType(file.getContentType())
        .width(bufferedImage.getWidth())
        .height(bufferedImage.getHeight())
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

  public String copyToDiskWithStructuredName(FileMetadataDto metadata, Long memberId) {
    String finalFileName = String.format("%d/%s?width=%d&height=%d&index=%d",
        memberId,
        "profile_image",
        metadata.getWidth(),
        metadata.getHeight(),
        metadata.getIndex()
    );

    try {
      logger.info("Copying file from buffer '{}' to disk as '{}'", metadata.getTemporaryFileName(), finalFileName);
      return copyToDisk(metadata, finalFileName);
    } catch (BmcException e) {
      logger.error("Failed to copy file with structured name: {}", metadata.getOriginalFileName(), e);
      throw new RuntimeException("Failed to copy file: " + metadata.getOriginalFileName(), e);
    }
  }

  private String generateTemporaryFileName(String userEmail) {
    return String.format("%s_%s_%s",
        UUID.randomUUID(),
        LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")),
        userEmail
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

  public List<FileMetadataDto> uploadMultipleFile(List<MultipartFile> files) {
    String email = memberService.getMemberEmail();
    return files.parallelStream()
        .map(file -> {
          try {
            return uploadToBuffer(file, email);
          } catch (IOException e) {
            logger.error("Failed to upload file: {}", file.getOriginalFilename(), e);
            throw new RuntimeException(e);
          }
        })
        .collect(Collectors.toList());
  }

}