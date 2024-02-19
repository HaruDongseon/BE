package haru.harudongseon.member.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OauthLoginResponse {
    private String accessToken;
}
