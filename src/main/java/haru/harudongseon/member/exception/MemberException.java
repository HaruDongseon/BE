package haru.harudongseon.member.exception;

public class MemberException extends RuntimeException {

    public MemberException(final String message) {
        super(message);
    }

    public static class DuplicateNicknameException extends MemberException {

        public DuplicateNicknameException() {
            super("중복되는 닉네임이 존재합니다.");
        }
    }
}
