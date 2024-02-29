package haru.harudongseon.member.presentation;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

import haru.harudongseon.common.H2TruncateUtils;
import haru.harudongseon.common.builder.MemberBuilder;
import haru.harudongseon.global.jwt.JwtService;
import haru.harudongseon.member.domain.Member;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MemberControllerTest {
    
    private static final String JWT_PREFIX = "Bearer ";

    @LocalServerPort
    private int port;

    @Autowired
    private H2TruncateUtils h2TruncateUtils;

    @Autowired
    private MemberBuilder memberBuilder;
    
    @Autowired
    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        RestAssured.port = this.port;
        h2TruncateUtils.truncateAll();
    }

    @Nested
    @DisplayName("내 정보 조회 시")
    class GetMyProfile {

        @Test
        @DisplayName("내 정보 조회에 성공한다.")
        void success() {
            // given
            final Member savedMember = memberBuilder.defaultMember().build();
            final Long memberId = savedMember.getId();
            final String email = savedMember.getEmail();
            final String nickname = savedMember.getNickname();
            final String profileImageUrl = savedMember.getProfileImageUrl();
            final String accessToken = jwtService.createAccessToken(memberId);

            // when
            final ExtractableResponse<Response> response = GET_MY_PROFILE_REQUEST(accessToken);
            final JsonPath jsonPath = response.jsonPath();

            // then
            assertSoftly(softly -> {
                softly.assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
                softly.assertThat(jsonPath.getString("email")).isEqualTo(email);
                softly.assertThat(jsonPath.getString("nickname")).isEqualTo(nickname);
                softly.assertThat(jsonPath.getString("profileImageUrl")).isEqualTo(profileImageUrl);
            });
        }

        @Test
        @DisplayName("존재하지 않는 멤버의 내 정보 조회면 실패한다.")
        void fail_get_not_exist_member_info() {
            // given
            final Long notExistMemberId = -1L;
            final String accessToken = jwtService.createAccessToken(notExistMemberId);

            // when
            final ExtractableResponse<Response> response = GET_MY_PROFILE_REQUEST(accessToken);

            // then
            assertSoftly(softly -> {
                softly.assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
                softly.assertThat(response.jsonPath().getString("errorMessage")).isEqualTo("해당하는 멤버를 찾을 수 없습니다.");
            });
        }
    }

    private static ExtractableResponse<Response> GET_MY_PROFILE_REQUEST(final String accessToken) {
        return RestAssured.given().log().all()
                .header(HttpHeaders.AUTHORIZATION, JWT_PREFIX + accessToken)
                .when().log().all()
                .get("/members/me")
                .then().log().all()
                .extract();
    }
}
