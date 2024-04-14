package haru.harudongseon.global.fileupload.exception;

public class FileUploadException extends RuntimeException {

    public FileUploadException(final Exception e) {
        super("File Upload 예외 발생", e);
    }
}
