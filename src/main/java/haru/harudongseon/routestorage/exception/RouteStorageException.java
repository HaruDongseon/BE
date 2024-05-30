package haru.harudongseon.routestorage.exception;

public class RouteStorageException extends RuntimeException {

    public RouteStorageException(final String message) {
        super(message);
    }

    public static class DuplicateException extends RouteStorageException {

        public DuplicateException() {
            super("중복된 이름을 가진 회원의 동선 보관함이 이미 존재합니다.");
        }
    }
}
