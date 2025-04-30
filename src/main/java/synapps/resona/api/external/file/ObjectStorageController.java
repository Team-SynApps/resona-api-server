package synapps.resona.api.external.file;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import synapps.resona.api.external.file.dto.FileMetadataDto;
import synapps.resona.api.global.config.server.ServerInfoConfig;
import synapps.resona.api.global.dto.metadata.MetaDataDto;
import synapps.resona.api.global.dto.response.ResponseDto;
import synapps.resona.api.mysql.member.service.MemberService;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/storage")
@RequiredArgsConstructor
public class ObjectStorageController {
    private final ObjectStorageService storageService;
    private final MemberService memberService;
    private final ServerInfoConfig serverInfo;

    private MetaDataDto createSuccessMetaData(String queryString) {
        return MetaDataDto.createSuccessMetaData(queryString, serverInfo.getApiVersion(), serverInfo.getServerName());
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> uploadFile(HttpServletRequest request,
                                        @RequestParam("file") MultipartFile file) throws IOException {
        MetaDataDto metaData = createSuccessMetaData(request.getQueryString());
        String email = memberService.getMemberEmail();
        FileMetadataDto fileMetadata = storageService.uploadToBuffer(file, email);

        ResponseDto responseData = new ResponseDto(metaData, List.of(fileMetadata));
        return ResponseEntity.ok(responseData);
    }

    @PostMapping(
            path = "/multiple",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> uploadMultipleFiles(HttpServletRequest request,
                                                 @RequestParam("files") List<MultipartFile> files) {
        MetaDataDto metaData = createSuccessMetaData(request.getQueryString());
        List<FileMetadataDto> fileMetadatas = storageService.uploadMultipleFile(files);

        ResponseDto responseData = new ResponseDto(metaData, fileMetadatas);
        return ResponseEntity.ok(responseData);
    }

    /**
     * 프로필 등록시, 업로드 후 디스크 버킷으로 옮길때 사용
     *
     * @param request
     * @param metadata
     * @param finalFileName
     * @return
     * @throws IOException
     */
    @PostMapping("/finalize")
    public ResponseEntity<?> finalizeFile(HttpServletRequest request,
                                          @RequestBody FileMetadataDto metadata,
                                          @RequestParam String finalFileName) throws IOException {
        MetaDataDto metaData = createSuccessMetaData(request.getQueryString());
        String finalUrl = storageService.copyToDisk(metadata, finalFileName);

        ResponseDto responseData = new ResponseDto(metaData, List.of(finalUrl));
        return ResponseEntity.ok(responseData);
    }
}