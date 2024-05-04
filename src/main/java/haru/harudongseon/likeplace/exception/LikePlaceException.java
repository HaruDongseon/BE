package haru.harudongseon.likeplace.exception;

public class LikePlaceException extends RuntimeException {

    private LikePlaceException(final String message) {
        super(message);
    }

    public static class PhotoDuplicateException extends LikePlaceException {

        public PhotoDuplicateException() {
            super("보관 장소에 중복되는 사진이 존재합니다.");
        }
    }
}
