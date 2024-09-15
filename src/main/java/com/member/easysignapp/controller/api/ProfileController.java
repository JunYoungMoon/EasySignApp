package com.member.easysignapp.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URLConnection;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/profile")
public class ProfileController {
    @Value("${upload.profile.directory}")
    private String uploadPath;

    private final MessageSourceAccessor messageSourceAccessor;

    @Autowired
    public ProfileController(MessageSourceAccessor messageSourceAccessor) {
        this.messageSourceAccessor = messageSourceAccessor;
    }

    @Operation(summary = "프로필 이미지 업로드", description = "프로필 이미지를 업로드 합니다.")
    @GetMapping("/{fileName:.+}")
    public ResponseEntity<Resource> serveFile(@PathVariable String fileName) {
        // 외부 디렉터리 경로 설정
        Path filePath = Paths.get(uploadPath).resolve(fileName).normalize();

        // 파일 읽기 및 Resource 객체 생성
        Resource resource;
        try {
            resource = new UrlResource(filePath.toUri());
        } catch (MalformedURLException e) {
            String errorMessage = messageSourceAccessor.getMessage("profile.serveFile.fileFind.error.message");

            throw new RuntimeException(errorMessage);
        }

        // 파일의 MIME 타입 동적으로 결정
        String contentType;
        try {
            contentType = determineMimeType(resource);
        } catch (IOException e) {
            String errorMessage = messageSourceAccessor.getMessage("profile.serveFile.fileMime.error.message");

            throw new RuntimeException(errorMessage);
        }

        // 파일 다운로드를 위한 HttpHeaders 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(contentType));
        return ResponseEntity.ok()
                .headers(headers)
                .body(resource);
    }

    private String determineMimeType(Resource resource) throws IOException {
        try (InputStream inputStream = resource.getInputStream()) {
            return URLConnection.guessContentTypeFromStream(inputStream);
        }
    }
}
