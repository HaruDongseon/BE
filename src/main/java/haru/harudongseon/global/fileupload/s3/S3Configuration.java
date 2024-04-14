package haru.harudongseon.global.fileupload.s3;

import haru.harudongseon.global.fileupload.FileUploader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class S3Configuration {

    @Value("${aws.s3.region}")
    private String s3Region;

    @Bean
    public S3Client s3Client() {
        return S3Client.builder()
                .region(Region.of(s3Region))
                .build();
    }

    @Bean
    public FileUploader fileUploader() {
        return new S3Uploader(s3Client());
    }
}
