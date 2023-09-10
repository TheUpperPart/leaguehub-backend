package leaguehub.leaguehubbackend.service.s3;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.Upload;
import leaguehub.leaguehubbackend.dto.s3.S3ResponseDto;
import leaguehub.leaguehubbackend.exception.s3.exception.S3InvalidImageException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3FileUploadService {


    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.s3.bucket.url}")
    private String defaultUrl;

    private final AmazonS3Client amazonS3Client;

    public S3ResponseDto uploadFile(MultipartFile uploadFile) {

        String origName = uploadFile.getOriginalFilename();

        String ext = origName.substring(origName.lastIndexOf('.'));

        String saveFileName = UUID.randomUUID().toString().replaceAll("-","") + ext;

        File file = new File(System.getProperty("user.dir") + saveFileName);

        try {
            uploadFile.transferTo(file);
        } catch (IOException e) {
            throw new S3InvalidImageException();
        }

        TransferManager transferManager = new TransferManager(this.amazonS3Client);

        PutObjectRequest request = new PutObjectRequest(bucket, saveFileName, file);

        Upload upload = transferManager.upload(request);

        try {
            upload.waitForCompletion();
        } catch (InterruptedException e) {
            throw new S3InvalidImageException();
        }

        String imageUrl = defaultUrl + saveFileName;

        file.delete();

        S3ResponseDto s3ResponseDto = new S3ResponseDto();
        s3ResponseDto.setImgUrl(imageUrl);
        return s3ResponseDto;

    }
}
