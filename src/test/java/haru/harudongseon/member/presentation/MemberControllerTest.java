package haru.harudongseon.member.presentation;

import static haru.harudongseon.common.fixtures.MemberFixtures.성하_프로필_이미지_URL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.util.stream.Stream;

import haru.harudongseon.common.E2ETest;
import haru.harudongseon.common.H2TruncateUtils;
import haru.harudongseon.common.builder.MemberBuilder;
import haru.harudongseon.common.fixtures.MemberFixtures;
import haru.harudongseon.global.jwt.JwtService;
import haru.harudongseon.member.application.dto.MyProfileEditRequest;
import haru.harudongseon.member.domain.Member;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

class MemberControllerTest extends E2ETest {

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

    @Nested
    @DisplayName("내 정보 수정 시")
    class EditMyProfile {

        @ParameterizedTest
        @MethodSource("editRequests")
        @DisplayName("수정에 성공한다.")
        void success(final MyProfileEditRequest request) {
            // given
            final Member savedMember = memberBuilder.defaultMember().build();
            final Long memberId = savedMember.getId();
            final String accessToken = jwtService.createAccessToken(memberId);

            // when
            final ExtractableResponse<Response> response = EDIT_MY_PROFILE_REQUEST(accessToken, request);

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        }

        private static Stream<Arguments> editRequests() {
            final String nickname = MemberFixtures.성하_닉네임;
            final String profileImageUrl = 성하_프로필_이미지_URL;

            return Stream.of(
                    Arguments.arguments(new MyProfileEditRequest(nickname, profileImageUrl)),
                    Arguments.arguments(new MyProfileEditRequest(nickname + "1", profileImageUrl)),
                    Arguments.arguments(new MyProfileEditRequest(nickname, profileImageUrl + "a")),
                    Arguments.arguments(new MyProfileEditRequest(nickname + "1", profileImageUrl + "a"))
            );
        }

        @Test
        @DisplayName("존재하지 않는 멤버의 내 정보 수정이면 실패한다.")
        void fail_edit_not_exist_member_profile() {
            // given
            final Long notExistMemberId = -1L;
            final String accessToken = jwtService.createAccessToken(notExistMemberId);
            final MyProfileEditRequest request = new MyProfileEditRequest("seongha1", 성하_프로필_이미지_URL);

            // when
            final ExtractableResponse<Response> response = EDIT_MY_PROFILE_REQUEST(accessToken, request);

            // then
            assertSoftly(softly -> {
                softly.assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
                softly.assertThat(response.jsonPath().getString("errorMessage")).isEqualTo("해당하는 멤버를 찾을 수 없습니다.");
            });
        }

        @ParameterizedTest
        @CsvSource(value = {",https://lh3.googleusercontent.com/a/xxx", "seongha1,", ","}, delimiter = ',')
        @DisplayName("정보가 하나라도 빈 값이면 내 정보 수정이 실패한다.")
        void fail_edit_profile_request_blank(final String nickname, final String profileImageUrl) {
            // given
            final Member savedMember = memberBuilder.defaultMember().build();
            final Long memberId = savedMember.getId();
            final String accessToken = jwtService.createAccessToken(memberId);
            final MyProfileEditRequest request = new MyProfileEditRequest(nickname, profileImageUrl);

            // when
            final ExtractableResponse<Response> response = EDIT_MY_PROFILE_REQUEST(accessToken, request);

            // then
            assertSoftly(softly -> {
                softly.assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                softly.assertThat(response.jsonPath().getString("errorMessage")).contains("공백일 수 없습니다.");
            });
        }

        @ParameterizedTest
        @ValueSource(strings = {"aa", "a", "0123456789a"})
        @DisplayName("수정할 닉네임이 Validation에 위배되면 내 정보 수정에 실패한다.")
        void fail_not_validated_nickname(final String notValidatedNickname) {
            // given
            final Member savedMember = memberBuilder.defaultMember().build();
            final Long memberId = savedMember.getId();
            final String profileImageUrl = savedMember.getProfileImageUrl();
            final String accessToken = jwtService.createAccessToken(memberId);
            final MyProfileEditRequest request = new MyProfileEditRequest(notValidatedNickname, profileImageUrl);

            // when
            final ExtractableResponse<Response> response = EDIT_MY_PROFILE_REQUEST(accessToken, request);

            // then
            assertSoftly(softly -> {
                softly.assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                softly.assertThat(response.jsonPath().getString("errorMessage")).contains("수정할 닉네임은 한글, 영문, 숫자, 띄어쓰기 포함 3~10자 여야합니다.");
            });
        }

        @ParameterizedTest
        @ValueSource(strings = {"aa", "www.naver.com", "aa.jpg"})
        @DisplayName("수정할 프로필 이미지 URL이 Validation에 위배되면 내 정보 수정에 실패한다.")
        void fail_not_validated_profile_image_url(final String notValidatedProfileImageUrl) {
            // given
            final Member savedMember = memberBuilder.defaultMember().build();
            final Long memberId = savedMember.getId();
            final String nickname = savedMember.getNickname();
            final String accessToken = jwtService.createAccessToken(memberId);
            final MyProfileEditRequest request = new MyProfileEditRequest(nickname, notValidatedProfileImageUrl);

            // when
            final ExtractableResponse<Response> response = EDIT_MY_PROFILE_REQUEST(accessToken, request);

            // then
            assertSoftly(softly -> {
                softly.assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                softly.assertThat(response.jsonPath().getString("errorMessage")).contains("수정할 프로필 이미지 URL은 URL 형식이어야합니다.");
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

    private static ExtractableResponse<Response> EDIT_MY_PROFILE_REQUEST(final String accessToken, final MyProfileEditRequest request) {
        return RestAssured.given().log().all()
                .header(HttpHeaders.AUTHORIZATION, JWT_PREFIX + accessToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .when().log().all()
                .patch("/members/me")
                .then().log().all()
                .extract();
    }
}
