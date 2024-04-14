package haru.harudongseon.global.fileupload;

import org.springframework.web.multipart.MultipartFile;

public interface FileUploader {

    String upload(final MultipartFile multipartFile, final String originalFileName);
}
