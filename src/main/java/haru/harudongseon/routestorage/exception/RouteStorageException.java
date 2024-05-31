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

    public static class NotOwnerException extends RouteStorageException {

        public NotOwnerException() {
            super("로그인한 회원이 해당 동선 보관함을 가진 회원이 아닙니다.");
        }
    }

    public static class NotExistRouteException extends RouteStorageException {

        public NotExistRouteException() {
            super("동선 보관함에 해당하는 동선이 존재하지 않습니다.");
        }
    }
}
