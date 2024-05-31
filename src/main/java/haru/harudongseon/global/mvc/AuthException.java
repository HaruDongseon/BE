package haru.harudongseon.global.mvc;

public class AuthException extends RuntimeException {

    public AuthException(final String message) {
        super(message);
    }

    public static class UnauthorizedException extends AuthException {

        public UnauthorizedException(final String message) {
            super(message);
        }
    }

    public static class ForbiddenException extends AuthException {

        public ForbiddenException(final String message) {
            super(message);
        }
    }
}
