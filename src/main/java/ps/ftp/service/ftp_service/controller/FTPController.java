package ps.ftp.service.ftp_service.controller;

import org.openapitools.api.FileApi;
import org.openapitools.model.FileAttributes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;

@RestController
public class FTPController implements FileApi {
    private static final Logger logger = LoggerFactory.getLogger(FTPController.class);
    @Override
    public ResponseEntity<FileAttributes> uploadFile(MultipartFile file) throws IOException {
        FileAttributes fileAttributes = new FileAttributes();
        fileAttributes.filePath(file.getContentType());
        fileAttributes.fileSize(file.getSize());
        return ResponseEntity.status(HttpStatus.CREATED).body(fileAttributes);
    }
}
