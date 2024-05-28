package haru.harudongseon.likeplacestorage.presentation;

import static haru.harudongseon.common.fixtures.LikePlaceStorageFixtures.기본_장소_보관함_이름;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import haru.harudongseon.common.E2ETest;
import haru.harudongseon.common.builder.LikePlaceStorageBuilder;
import haru.harudongseon.common.builder.MemberBuilder;
import haru.harudongseon.likeplacestorage.application.dto.LikePlaceStorageAddRequest;
import haru.harudongseon.likeplacestorage.domain.LikePlaceStorage;
import haru.harudongseon.member.domain.Member;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

class LikePlaceStorageControllerTest extends E2ETest {

    @Autowired
    private MemberBuilder memberBuilder;

    @Autowired
    private LikePlaceStorageBuilder likePlaceStorageBuilder;

    @Nested
    @DisplayName("장소 보관함 추가 시")
    class AddLikePlaceStorage {

        @Test
        @DisplayName("추가에 성공한다.")
        void success() {
            // given
            final Member member = memberBuilder.defaultMember().build();
            final String accessToken = jwtService.createAccessToken(member.getId());
            final LikePlaceStorageAddRequest request = new LikePlaceStorageAddRequest(기본_장소_보관함_이름);

            // when
            final ExtractableResponse<Response> response = ADD_LIKE_PLACE_STORAGE_REQUEST(accessToken, request);

            // then
            assertSoftly(softly -> {
                softly.assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
                softly.assertThat(response.header("Location")).contains("/like-place-storage/");
            });
        }

        @Test
        @DisplayName("중복된 이름을 가진 이름으로 장소 보관함을 추가하면 실패한다.")
        void fail_duplicate_name() {
            // given
            final Member member = memberBuilder.defaultMember().build();
            final String accessToken = jwtService.createAccessToken(member.getId());
            final LikePlaceStorage existLikePlaceStorage = likePlaceStorageBuilder.defaultLikePlaceStorage(member.getId()).build();
            final LikePlaceStorageAddRequest duplicateRequest = new LikePlaceStorageAddRequest(existLikePlaceStorage.getName());

            // when
            final ExtractableResponse<Response> response = ADD_LIKE_PLACE_STORAGE_REQUEST(accessToken, duplicateRequest);

            // then
            assertSoftly(softly -> {
                softly.assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                softly.assertThat(response.jsonPath().getString("errorMessage")).contains("중복된 이름을 가진 회원의 장소 보관함이 이미 존재합니다.");
            });
        }

        @ParameterizedTest
        @ValueSource(strings = {"", " "})
        @DisplayName("장소 보관함 이름이 공백이면 실패한다.")
        void fail_blank_name(final String blankName) {
            // given
            final Member member = memberBuilder.defaultMember().build();
            final String accessToken = jwtService.createAccessToken(member.getId());
            final LikePlaceStorageAddRequest blankRequest = new LikePlaceStorageAddRequest(blankName);

            // when
            final ExtractableResponse<Response> response = ADD_LIKE_PLACE_STORAGE_REQUEST(accessToken, blankRequest);

            // then
            assertSoftly(softly -> {
                softly.assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                softly.assertThat(response.jsonPath().getString("errorMessage")).contains("장소 보관함 이름은 공백일 수 없습니다.");
            });
        }

        @Test
        @DisplayName("장소 보관함 이름이 16자 이상이면 실패한다.")
        void fail_over_16_name() {
            // given
            final Member member = memberBuilder.defaultMember().build();
            final String accessToken = jwtService.createAccessToken(member.getId());
            final String overLengthName = "a".repeat(16);
            final LikePlaceStorageAddRequest overLengthRequest = new LikePlaceStorageAddRequest(overLengthName);

            // when
            final ExtractableResponse<Response> response = ADD_LIKE_PLACE_STORAGE_REQUEST(accessToken, overLengthRequest);

            // then
            assertSoftly(softly -> {
                softly.assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                softly.assertThat(response.jsonPath().getString("errorMessage")).contains("장소 보관함 이름은 15자 이하여야 합니다.");
            });
        }
    }

    private static ExtractableResponse<Response> ADD_LIKE_PLACE_STORAGE_REQUEST(final String accessToken, final LikePlaceStorageAddRequest request) {
        return RestAssured.given().log().all()
                .header(HttpHeaders.AUTHORIZATION, JWT_PREFIX + accessToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .when().log().all()
                .post("/like-place-storage")
                .then().log().all()
                .extract();
    }
}
