package haru.harudongseon.likeplacestorage.exception;

public class LikePlaceStorageException extends RuntimeException {

    public LikePlaceStorageException(final String message) {
        super(message);
    }

    public static class DuplicateException extends LikePlaceStorageException {

        public DuplicateException() {
            super("중복된 이름을 가진 회원의 장소 보관함이 이미 존재합니다.");
        }
    }
}
