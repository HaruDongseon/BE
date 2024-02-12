package haru.harudongseon.member.application.dto;

public record OauthLoginRequest(String loginType, String token, String deviceId) {
}
