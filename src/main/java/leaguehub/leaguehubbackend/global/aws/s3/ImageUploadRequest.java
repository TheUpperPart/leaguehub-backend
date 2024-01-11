package leaguehub.leaguehubbackend.global.aws.s3;

import org.springframework.web.multipart.MultipartFile;

public record ImageUploadRequest(

        MultipartFile uploadImage
) {
}
