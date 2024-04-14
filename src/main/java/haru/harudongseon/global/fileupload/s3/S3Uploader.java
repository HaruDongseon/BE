package haru.harudongseon.global.fileupload.s3;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import haru.harudongseon.global.fileupload.FileUploader;
import haru.harudongseon.global.fileupload.exception.FileUploadException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@RequiredArgsConstructor
@Slf4j
public class S3Uploader implements FileUploader {

    @Value("${aws.s3.bucket}")
    private String bucket;

    @Value("${aws.s3.image-directory}")
    private String imageDirectory;

    @Value("${aws.s3.domain}")
    private String uploadDomain;

    private final S3Client s3Client;

    @Override
    public String upload(final MultipartFile multipartFile, final String uploadFileName) {
        try {
            final String extension = extractExtension(multipartFile.getOriginalFilename());
            final String finalUploadFileName = uploadFileName + extension;
            final RequestBody requestBody = RequestBody.fromInputStream(multipartFile.getInputStream(), multipartFile.getSize());
            final MediaType mediaType = MediaType.parseMediaType(Files.probeContentType(Paths.get(multipartFile.getOriginalFilename())));
            return uploadFile(finalUploadFileName, requestBody, mediaType);
        } catch (IOException e) {
            log.error("S3 업로드 예외 발생", e);
            throw new FileUploadException(e);
        }
    }

    private String uploadFile(final String originalFileName, final RequestBody requestBody, final MediaType mediaType) {
        final String uploadPathWithFileName = imageDirectory + "/" + originalFileName;
        final PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .key(uploadPathWithFileName)
                .contentType(mediaType.toString())
                .bucket(bucket)
                .build();

        s3Client.putObject(putObjectRequest, requestBody);

        return uploadDomain + uploadPathWithFileName;
    }

    private String extractExtension(final String fileName) {
        final int startIdx = fileName.lastIndexOf(".");
        final int endIdx = fileName.length();
        return fileName.substring(startIdx, endIdx);
    }
}
