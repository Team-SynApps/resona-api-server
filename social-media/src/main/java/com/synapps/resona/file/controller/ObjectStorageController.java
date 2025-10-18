package com.synapps.resona.file.controller;

import com.synapps.resona.entity.AuthenticatedUser;
import com.synapps.resona.file.ObjectStorageService;
import com.synapps.resona.file.code.FileSuccessCode;
import com.synapps.resona.file.dto.FileMetadataDto;
import com.synapps.resona.config.server.ServerInfoConfig;
import com.synapps.resona.dto.RequestInfo;
import com.synapps.resona.dto.response.ErrorResponse;
import com.synapps.resona.dto.response.SuccessResponse;
import com.synapps.resona.command.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
@Tag(name = "File Storage", description = "파일 업로드 및 관리 API")
@RestController
@RequestMapping("/storage")
@RequiredArgsConstructor
public class ObjectStorageController {

  private final ObjectStorageService storageService;
  private final MemberService memberService;
  private final ServerInfoConfig serverInfo;

  private RequestInfo createRequestInfo(String path) {
    return new RequestInfo(serverInfo.getApiVersion(), serverInfo.getServerName(), path);
  }

  @Operation(summary = "단일 파일 업로드", description = "하나의 파일을 임시 버킷에 업로드합니다. (인증 필요)")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "파일 업로드 성공"),
      @ApiResponse(responseCode = "401", description = "인증 실패",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<SuccessResponse<FileMetadataDto>> uploadFile(HttpServletRequest request,
      @Parameter(
          description = "업로드할 단일 파일",
          required = true,
          content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
              schema = @Schema(type = "string", format = "binary"))
      )
      @RequestPart("file") MultipartFile file) throws IOException {
    String email = memberService.getMemberEmail();
    FileMetadataDto fileMetadata = storageService.uploadToBuffer(file, email);

    return ResponseEntity
        .status(FileSuccessCode.UPLOAD_FILE_SUCCESS.getStatus())
        .body(SuccessResponse.of(FileSuccessCode.UPLOAD_FILE_SUCCESS, createRequestInfo(request.getQueryString()), fileMetadata));
  }

  @Operation(summary = "다중 파일 업로드", description = "여러 개의 파일을 임시 버킷에 업로드합니다. (인증 필요)")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "다중 파일 업로드 성공"),
      @ApiResponse(responseCode = "401", description = "인증 실패",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  @PostMapping(
      path = "/multiple",
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  public ResponseEntity<SuccessResponse<List<FileMetadataDto>>> uploadMultipleFiles(HttpServletRequest request,
      @Parameter(
          description = "업로드할 다중 파일",
          required = true,
          content = @Content(
              mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
              array = @ArraySchema(schema = @Schema(type = "string", format = "binary"))
          )
      )
      @RequestPart("files") List<MultipartFile> files) {
    List<FileMetadataDto> fileMetadatas = storageService.uploadMultipleFile(files);

    return ResponseEntity
        .status(FileSuccessCode.UPLOAD_MULTIPLE_FILES_SUCCESS.getStatus())
        .body(SuccessResponse.of(FileSuccessCode.UPLOAD_MULTIPLE_FILES_SUCCESS, createRequestInfo(request.getQueryString()), fileMetadatas));
  }

  @Operation(summary = "파일 최종 처리", description = "임시 버킷에 업로드된 파일을 영구 버킷으로 복사(이동)합니다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "파일 이동 성공"),
      @ApiResponse(responseCode = "404", description = "임시 버킷에 파일이 존재하지 않음",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  @PostMapping("/finalize")
  public ResponseEntity<SuccessResponse<Map<String, String>>> finalizeFile(HttpServletRequest request,
      @Parameter(hidden = true) @AuthenticationPrincipal AuthenticatedUser user,
      @RequestBody FileMetadataDto metadata) throws IOException {
    String finalUrl = storageService.copyToDiskWithStructuredName(metadata,user.getMemberId());

    return ResponseEntity
        .status(FileSuccessCode.FINALIZE_FILE_SUCCESS.getStatus())
        .body(SuccessResponse.of(FileSuccessCode.FINALIZE_FILE_SUCCESS, createRequestInfo(request.getQueryString()), Map.of("finalUrl", finalUrl)));
  }
}