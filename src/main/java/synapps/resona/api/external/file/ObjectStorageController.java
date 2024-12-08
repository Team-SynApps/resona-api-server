package synapps.resona.api.external.file;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/storage")
@RequiredArgsConstructor
public class ObjectStorageController {
    private final ObjectStorageService storageService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "objectName", required = false) String objectName) throws IOException {
        if (objectName == null) {
            objectName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        }

        String fileUrl = storageService.uploadFile(objectName, file);
        return ResponseEntity.ok(fileUrl);
    }

    @GetMapping("/download/{objectName}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable String objectName) throws IOException {
        byte[] data = storageService.downloadFile(objectName);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + objectName + "\"")
                .body(data);
    }
}