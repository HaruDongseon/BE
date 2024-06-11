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

    public static class NotExistLikePlaceException extends LikePlaceStorageException {

        public NotExistLikePlaceException() {
            super("장소 보관함에 해당하는 장소가 존재하지 않습니다.");
        }
    }

    public static class AlreadyExistLikePlaceException extends LikePlaceStorageException {

        public AlreadyExistLikePlaceException() {
            super("장소 보관함에 이미 존재하는 보관 장소입니다.");
        }
    }
}
