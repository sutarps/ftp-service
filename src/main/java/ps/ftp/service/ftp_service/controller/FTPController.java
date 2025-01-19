package ps.ftp.service.ftp_service.controller;

import org.openapitools.api.FileApi;
import org.openapitools.model.FileAttributes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@RestController
public class FTPController implements FileApi {
    private static final Logger logger = LoggerFactory.getLogger(FTPController.class);
    @Value("${storage.path:\"c://temp/\"}")
    private String STORAGE_PATH;



    @Override
    public ResponseEntity<FileAttributes> uploadFile(MultipartFile file) {
        logger.info("File received name {} ", file.getOriginalFilename());
        FileAttributes fileAttributes = new FileAttributes();
        fileAttributes.setFilePath(STORAGE_PATH + file.getOriginalFilename());
        fileAttributes.setFileSize(file.getSize());

        // Save the file to the persistent volume store
        File targetFile = new File(STORAGE_PATH + file.getOriginalFilename());
        try {
            file.transferTo(targetFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(fileAttributes);
    }

    @GetMapping("/files")
    public ResponseEntity<List<String>> browseFiles() {
        logger.info("Request received to get list of files");
        File folder = new File(STORAGE_PATH);
        File[] listOfFiles = folder.listFiles();
        List<String> fileNames = new ArrayList<>();

        if (listOfFiles != null) {
            for (File file : listOfFiles) {
                if (file.isFile()) {
                    fileNames.add(file.getName());
                }
            }
        }

        return ResponseEntity.ok(fileNames);
    }

    @GetMapping("/files/{fileName}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName) throws IOException {
        File file = new File(STORAGE_PATH + fileName);
        if (!file.exists()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Path path = Paths.get(STORAGE_PATH + fileName);
        Resource resource = new UrlResource(path.toUri());

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"")
                .body(resource);
    }
}

