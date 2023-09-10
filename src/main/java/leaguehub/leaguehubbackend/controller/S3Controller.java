package leaguehub.leaguehubbackend.controller;

import leaguehub.leaguehubbackend.service.s3.S3FileUploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import static org.springframework.http.HttpStatus.OK;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class S3Controller {

    final S3FileUploadService s3FileUploadService;

    @PostMapping("/image")
    public ResponseEntity saveProfile(MultipartFile multipartFile) {
        String imageUrl = s3FileUploadService.uploadFile(multipartFile);

        return new ResponseEntity<>(imageUrl, OK);
    }
}
