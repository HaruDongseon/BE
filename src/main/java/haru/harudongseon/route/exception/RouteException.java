package haru.harudongseon.route.exception;

public class RouteException extends RuntimeException {

    private RouteException(final String message) {
        super(message);
    }

    public static class DuplicateTagException extends RouteException {

        public DuplicateTagException() {
            super("동선에 중복된 태그가 존재합니다.");
        }
    }

    public static class DuplicateRoutePlacePhotoException extends RouteException {

        public DuplicateRoutePlacePhotoException() {
            super("동선 장소에 중복된 사진이 존재합니다.");
        }
    }
}
