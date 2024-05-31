package haru.harudongseon.routestorage.presentation;

import static haru.harudongseon.common.fixtures.RouteStorageFixtures.기본_동선_보관함_이름;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import haru.harudongseon.common.E2ETest;
import haru.harudongseon.common.builder.MemberBuilder;
import haru.harudongseon.member.domain.Member;
import haru.harudongseon.routestorage.application.dto.RouteStorageAddRequest;
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

class RouteStorageControllerTest extends E2ETest {

    @Autowired
    private MemberBuilder memberBuilder;

    @Nested
    @DisplayName("동선 보관함 추가 시")
    class AddRouteStorage {

        @Test
        @DisplayName("추가에 성공한다.")
        void success() {
            // given
            final Member member = memberBuilder.defaultMember().build();
            final String accessToken = jwtService.createAccessToken(member.getId());
            final RouteStorageAddRequest request = new RouteStorageAddRequest(기본_동선_보관함_이름);

            // when
            final ExtractableResponse<Response> response = ADD_ROUTE_STORAGE_REQUEST(accessToken, request);

            // then
            assertSoftly(softly -> {
                softly.assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
                softly.assertThat(response.header("Location")).contains("/route-storages/");
            });
        }

        @Test
        @DisplayName("로그인한 멤버가 존재하지 않으면 실패한다.")
        void fail_not_exist_member() {
            // given
            final Long notExistMemberId = -1L;
            final String notExistMemberAccessToken = jwtService.createAccessToken(notExistMemberId);
            final RouteStorageAddRequest request = new RouteStorageAddRequest(기본_동선_보관함_이름);

            // when
            final ExtractableResponse<Response> response = ADD_ROUTE_STORAGE_REQUEST(notExistMemberAccessToken, request);

            // then
            assertSoftly(softly -> {
                softly.assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
                softly.assertThat(response.jsonPath().getString("errorMessage")).contains("해당하는 멤버가 존재하지 않습니다.");
            });
        }

        @Test
        @DisplayName("중복되는 이름의 동선 보관함이 있으면 실패한다.")
        void fail_duplicate_route_storage_name() {
            // given
            final Member member = memberBuilder.defaultMember().build();
            final String accessToken = jwtService.createAccessToken(member.getId());
            final RouteStorageAddRequest request = new RouteStorageAddRequest(기본_동선_보관함_이름);
            ADD_ROUTE_STORAGE_REQUEST(accessToken, request);

            // when
            final ExtractableResponse<Response> response = ADD_ROUTE_STORAGE_REQUEST(accessToken, request);

            // then
            assertSoftly(softly -> {
                softly.assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                softly.assertThat(response.jsonPath().getString("errorMessage")).contains("중복된 이름을 가진 회원의 동선 보관함이 이미 존재합니다.");
            });
        }

        @ParameterizedTest
        @ValueSource(strings = {"", " "})
        @DisplayName("동선 보관함 이름이 공백이면 실패한다.")
        void fail_blank_route_storage_name(final String routeStorageName) {
            // given
            final Member member = memberBuilder.defaultMember().build();
            final String accessToken = jwtService.createAccessToken(member.getId());
            final RouteStorageAddRequest blankNameRequest = new RouteStorageAddRequest(routeStorageName);

            // when
            final ExtractableResponse<Response> response = ADD_ROUTE_STORAGE_REQUEST(accessToken, blankNameRequest);

            // then
            assertSoftly(softly -> {
                softly.assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                softly.assertThat(response.jsonPath().getString("errorMessage")).contains("동선 보관함 이름은 공백일 수 없습니다.");
            });
        }

        @Test
        @DisplayName("동선 보관함 이름이 15자 초과면 실패한다.")
        void fail_over_length_route_storage_name() {
            // given
            final Member member = memberBuilder.defaultMember().build();
            final String accessToken = jwtService.createAccessToken(member.getId());
            final String overLengthName = "a".repeat(16);
            final RouteStorageAddRequest overLengthNameRequest = new RouteStorageAddRequest(overLengthName);

            // when
            final ExtractableResponse<Response> response = ADD_ROUTE_STORAGE_REQUEST(accessToken, overLengthNameRequest);

            // then
            assertSoftly(softly -> {
                softly.assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                softly.assertThat(response.jsonPath().getString("errorMessage")).contains("동선 보관함 이름은 15자 이하여야 합니다.");
            });
        }
    }

    private static ExtractableResponse<Response> ADD_ROUTE_STORAGE_REQUEST(final String accessToken, final RouteStorageAddRequest request) {
        return RestAssured.given().log().all()
                .header(HttpHeaders.AUTHORIZATION, JWT_PREFIX + accessToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .when().log().all()
                .post("/route-storage")
                .then().log().all()
                .extract();
    }
}
