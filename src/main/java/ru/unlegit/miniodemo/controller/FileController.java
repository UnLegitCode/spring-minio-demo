package ru.unlegit.miniodemo.controller;

import io.minio.errors.MinioException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.unlegit.miniodemo.dto.FileUploadResponse;
import ru.unlegit.miniodemo.dto.PresignedUrlResponse;
import ru.unlegit.miniodemo.service.MinIoService;

import java.io.IOException;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("api/files")
public class FileController {

    private final MinIoService minIoService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public FileUploadResponse upload(
            @RequestParam("file") MultipartFile file
    ) throws MinioException, IOException {
        var objectName = minIoService.uploadFile(file);

        return new FileUploadResponse(objectName);
    }

    @GetMapping(value = "/download/{objectName}")
    public Resource download(
            @PathVariable String objectName, HttpServletResponse response
    ) throws MinioException {
        var fileData = minIoService.downloadFile(objectName);

        response.addHeader("Content-Disposition", "inline; filename=\"" + objectName + "\"");
        response.addHeader("Content-Type", fileData.contentType());
        response.addHeader("Content-Length", fileData.fileSize());

        return new InputStreamResource(fileData.stream());
    }

    @GetMapping("/list")
    public List<String> list() throws MinioException {
        return minIoService.listFiles();
    }

    @DeleteMapping("/{objectName}")
    public void delete(@PathVariable String objectName) throws MinioException {
        minIoService.deleteFile(objectName);
    }

    @GetMapping("/presigned/{objectName}")
    public PresignedUrlResponse presignedUrl(@PathVariable String objectName) throws MinioException {
        var presignedUrl = minIoService.getPresignedUrl(objectName);

        return new PresignedUrlResponse(presignedUrl, "10 minutes");
    }
}