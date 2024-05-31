package haru.harudongseon.global.exception;

import java.util.Random;

import haru.harudongseon.likeplace.exception.LikePlaceException;
import haru.harudongseon.likeplacestorage.exception.LikePlaceStorageException;
import haru.harudongseon.member.exception.MemberException;
import haru.harudongseon.route.exception.RouteException;
import haru.harudongseon.routestorage.exception.RouteStorageException;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private static final int ERROR_KEY_LENGTH = 5;
    private static final Random random = new Random();
    private static final String GENERATE_CHARS = "abcdefghijklmnopqrstuvwxyz";
    private static final String DEFAULT_ERROR_MESSAGE = "관리자에게 문의하세요. ";

    @ExceptionHandler(value = {
            RuntimeException.class
    })
    public ResponseEntity<ErrorResponse> handleRuntimeException(final RuntimeException exception) {
        final String message = exception.getMessage();

        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ERROR_KEY_LENGTH; i++) {
            sb.append(GENERATE_CHARS.charAt(random.nextInt(GENERATE_CHARS.length())));
        }
        final String logErrorKeyInfo = String.format("%n error key : %s", sb);
        final String exceptionTypeInfo = String.format("%n class type : %s", exception.getClass());

        log.error(message + logErrorKeyInfo + exceptionTypeInfo);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(DEFAULT_ERROR_MESSAGE + String.format("error key : %s", sb)));
    }


    @ExceptionHandler(value = {
            MethodArgumentNotValidException.class
    })
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(final MethodArgumentNotValidException exception) {
        final ObjectError errorTarget = exception.getAllErrors().get(0);
        final String defaultErrorMessage = errorTarget.getDefaultMessage();
        final String objectName = errorTarget.getObjectName();
        log.warn("Request Field Error - Message: {}, Target : {}", defaultErrorMessage, objectName);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(defaultErrorMessage));
    }

    @ExceptionHandler(value = {
            IllegalArgumentException.class,
            MemberException.DuplicateNicknameException.class,
            HttpMessageNotReadableException.class,
            LikePlaceException.PhotoDuplicateException.class,
            RouteException.DuplicateTagException.class,
            RouteException.DuplicateRoutePlacePhotoException.class,
            LikePlaceStorageException.DuplicateException.class,
            RouteStorageException.DuplicateException.class,
            RouteStorageException.NotOwnerException.class
    })
    public ResponseEntity<ErrorResponse> handleBadRequestException(final RuntimeException exception) {
        final String errorMessage = exception.getMessage();
        log.warn(errorMessage);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(errorMessage));
    }

    @ExceptionHandler(value = {
            EntityNotFoundException.class,
            LikePlaceStorageException.NotExistLikePlaceException.class,
            RouteStorageException.NotExistRouteException.class
    })
    public ResponseEntity<ErrorResponse> handleNotFoundException(final RuntimeException exception) {
        final String errorMessage = exception.getMessage();
        log.warn(errorMessage);

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(errorMessage));
    }
}
