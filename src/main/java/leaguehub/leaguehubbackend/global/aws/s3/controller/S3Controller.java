package leaguehub.leaguehubbackend.global.aws.s3.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import leaguehub.leaguehubbackend.global.aws.s3.ImageUploadRequest;
import leaguehub.leaguehubbackend.global.aws.s3.S3FileUploadService;
import leaguehub.leaguehubbackend.global.aws.s3.dto.S3ResponseDto;
import leaguehub.leaguehubbackend.global.exception.global.ExceptionResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.OK;

@Slf4j
@Tag(name = "S3-Controller", description = "사진 업로드 관련 API")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class S3Controller {

    final S3FileUploadService s3FileUploadService;

    @Operation(summary = "사진 업로드 API")
    @Parameter(name = "multipartFile", description = "사진 파일", example = "image.jpg")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "사진 저장 주소 반환"),
            @ApiResponse(responseCode = "400", description = "유효하지 않은 이미지입니다.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponse.class)))
    })
    @PostMapping("/image")
    public ResponseEntity saveProfile(ImageUploadRequest imageUploadRequest) {
        S3ResponseDto s3ResponseDto = s3FileUploadService.uploadFile(imageUploadRequest.uploadImage());

        return new ResponseEntity<>(s3ResponseDto, OK);
    }
}
