package haru.harudongseon.routestorage.presentation;

import static haru.harudongseon.common.fixtures.RouteStorageFixtures.기본_동선_보관함_이름;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import haru.harudongseon.common.E2ETest;
import haru.harudongseon.common.builder.MemberBuilder;
import haru.harudongseon.common.builder.RouteBuilder;
import haru.harudongseon.common.builder.RouteStorageBuilder;
import haru.harudongseon.member.domain.Member;
import haru.harudongseon.route.domain.Route;
import haru.harudongseon.routestorage.application.dto.RouteDeleteRequest;
import haru.harudongseon.routestorage.application.dto.RouteStorageAddRequest;
import haru.harudongseon.routestorage.application.dto.RouteStoragesResponse;
import haru.harudongseon.routestorage.domain.RouteStorage;
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

    @Autowired
    private RouteBuilder routeBuilder;

    @Autowired
    private RouteStorageBuilder routeStorageBuilder;

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
                softly.assertThat(response.statusCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
                softly.assertThat(response.jsonPath().getString("errorMessage")).isEqualTo("인증에 실패했습니다. 정확한 에러는 서버 로그를 확인해주세요.");
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

    @Nested
    @DisplayName("동선 보관함의 동선 삭제 시")
    class DeleteRoute {

        @Test
        @DisplayName("삭제에 성공한다.")
        void success() {
            // given
            final Member member = memberBuilder.defaultMember().build();
            final String accessToken = jwtService.createAccessToken(member.getId());
            final Route route1 = routeBuilder.defaultRoute(member).build();
            final Route route2 = routeBuilder.defaultRoute(member).build();
            final Route route3 = routeBuilder.defaultRoute(member).build();
            final RouteStorage routeStorage = routeStorageBuilder.defaultRouteStorage(member).build(new ArrayList<>(List.of(route1, route2, route3)));

            final RouteDeleteRequest request = new RouteDeleteRequest(routeStorage.getId(), List.of(route1.getId(), route3.getId()));

            // when
            final ExtractableResponse<Response> response = DELETE_ROUTE_REQUEST(accessToken, request);

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
        }

        @Test
        @DisplayName("요청한 동선 보관함 ID가 없으면 실패한다.")
        void fail_null_route_storage_id() {
            // given
            final Member member = memberBuilder.defaultMember().build();
            final String accessToken = jwtService.createAccessToken(member.getId());
            final Route route1 = routeBuilder.defaultRoute(member).build();
            final Route route2 = routeBuilder.defaultRoute(member).build();
            final Route route3 = routeBuilder.defaultRoute(member).build();
            final RouteStorage routeStorage = routeStorageBuilder.defaultRouteStorage(member).build(new ArrayList<>(List.of(route1, route2, route3)));

            final Long nullRouteStorageId = null;
            final RouteDeleteRequest nullRouteStorageRequest = new RouteDeleteRequest(nullRouteStorageId, List.of(route1.getId(), route3.getId()));

            // when
            final ExtractableResponse<Response> nullRouteStorageResponse = DELETE_ROUTE_REQUEST(accessToken, nullRouteStorageRequest);

            // then
            assertSoftly(softly -> {
                softly.assertThat(nullRouteStorageResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                softly.assertThat(nullRouteStorageResponse.jsonPath().getString("errorMessage")).contains("동선 보관함 ID는 공백일 수 없습니다.");
            });
        }

        @Test
        @DisplayName("요청한 동선 ID 리스트가 비어있으면 실패한다.")
        void fail_empty_route_ids() {
            // given
            final Member member = memberBuilder.defaultMember().build();
            final String accessToken = jwtService.createAccessToken(member.getId());
            final Route route1 = routeBuilder.defaultRoute(member).build();
            final Route route2 = routeBuilder.defaultRoute(member).build();
            final Route route3 = routeBuilder.defaultRoute(member).build();
            final RouteStorage routeStorage = routeStorageBuilder.defaultRouteStorage(member).build(new ArrayList<>(List.of(route1, route2, route3)));

            final RouteDeleteRequest emptyRouteIdsRequest = new RouteDeleteRequest(routeStorage.getId(), Collections.EMPTY_LIST);

            // when
            final ExtractableResponse<Response> emptyRouteIdsResponse = DELETE_ROUTE_REQUEST(accessToken, emptyRouteIdsRequest);

            // then
            assertSoftly(softly -> {
                softly.assertThat(emptyRouteIdsResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                softly.assertThat(emptyRouteIdsResponse.jsonPath().getString("errorMessage")).contains("동선 ID 리스트는 공백일 수 없습니다.");
            });
        }

        @Test
        @DisplayName("동선 보관함 ID에 해당하는 동선 보관함이 존재하지 않으면 실패한다.")
        void fail_not_exist_route_storage() {
            // given
            final Member member = memberBuilder.defaultMember().build();
            final String accessToken = jwtService.createAccessToken(member.getId());
            final Route route1 = routeBuilder.defaultRoute(member).build();
            final Route route2 = routeBuilder.defaultRoute(member).build();
            final Route route3 = routeBuilder.defaultRoute(member).build();
            final RouteStorage routeStorage = routeStorageBuilder.defaultRouteStorage(member).build(new ArrayList<>(List.of(route1, route2, route3)));

            final Long notExistRouteStorageId = -1L;
            final RouteDeleteRequest notExistRouteStorageRequest = new RouteDeleteRequest(notExistRouteStorageId, List.of(route1.getId(), route3.getId()));

            // when
            final ExtractableResponse<Response> notExistRouteStorageResponse = DELETE_ROUTE_REQUEST(accessToken, notExistRouteStorageRequest);

            // then
            assertSoftly(softly -> {
                softly.assertThat(notExistRouteStorageResponse.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
                softly.assertThat(notExistRouteStorageResponse.jsonPath().getString("errorMessage")).contains("해당하는 동선 보관함이 존재하지 않습니다.");
            });
        }

        @Test
        @DisplayName("로그인한 멤버가 동선 보관함을 가진 멤버가 아니면 실패한다.")
        void fail_not_owner() {
            // given
            final Member member = memberBuilder.defaultMember().build();
            final Member notOwnerMember = memberBuilder.defaultMember().build();
            final String notOwnerMemberAccessToken = jwtService.createAccessToken(notOwnerMember.getId());
            final Route route1 = routeBuilder.defaultRoute(member).build();
            final Route route2 = routeBuilder.defaultRoute(member).build();
            final Route route3 = routeBuilder.defaultRoute(member).build();
            final RouteStorage routeStorage = routeStorageBuilder.defaultRouteStorage(member).build(new ArrayList<>(List.of(route1, route2, route3)));

            final RouteDeleteRequest notExistRouteStorageRequest = new RouteDeleteRequest(routeStorage.getId(), List.of(route1.getId(), route3.getId()));

            // when
            final ExtractableResponse<Response> notOwnerMemberResponse = DELETE_ROUTE_REQUEST(notOwnerMemberAccessToken, notExistRouteStorageRequest);

            // then
            assertSoftly(softly -> {
                softly.assertThat(notOwnerMemberResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                softly.assertThat(notOwnerMemberResponse.jsonPath().getString("errorMessage")).contains("로그인한 회원이 해당 동선 보관함을 가진 회원이 아닙니다.");
            });
        }

        @Test
        @DisplayName("삭제할 동선들 중 하나라도 존재하지 않으면 실패한다.")
        void fail_not_exist_route() {
            // given
            final Member member = memberBuilder.defaultMember().build();
            final String accessToken = jwtService.createAccessToken(member.getId());
            final Route route1 = routeBuilder.defaultRoute(member).build();
            final Route route2 = routeBuilder.defaultRoute(member).build();
            final Route notContainRoute = routeBuilder.defaultRoute(member).build();
            final RouteStorage routeStorage = routeStorageBuilder.defaultRouteStorage(member).build(new ArrayList<>(List.of(route1, route2)));

            final RouteDeleteRequest notExistRouteRequest = new RouteDeleteRequest(routeStorage.getId(), List.of(route1.getId(), notContainRoute.getId()));

            // when
            final ExtractableResponse<Response> notExistRouteResponse = DELETE_ROUTE_REQUEST(accessToken, notExistRouteRequest);

            // then
            assertSoftly(softly -> {
                softly.assertThat(notExistRouteResponse.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
                softly.assertThat(notExistRouteResponse.jsonPath().getString("errorMessage")).contains("동선 보관함에 해당하는 동선이 존재하지 않습니다.");
            });
        }
    }

    @Nested
    @DisplayName("동선 보관함 이름 조회 시")
    class FindAllRouteStorageNames {

        @Test
        @DisplayName("조회에 성공한다.")
        void success() {
            // given
            final Member member = memberBuilder.defaultMember().build();
            final String accessToken = jwtService.createAccessToken(member.getId());
            final Route route1 = routeBuilder.defaultRoute(member).build();
            final Route route2 = routeBuilder.defaultRoute(member).build();
            final RouteStorage routeStorage1 = routeStorageBuilder.defaultRouteStorage(member).name("데이트").build(new ArrayList<>(List.of(route1)));
            final RouteStorage routeStorage2 = routeStorageBuilder.defaultRouteStorage(member).name("보드게임 동아리").build(new ArrayList<>(List.of(route2)));

            final RouteStoragesResponse expected = RouteStoragesResponse.from(List.of(routeStorage1, routeStorage2));

            // when
            final ExtractableResponse<Response> response = FIND_ALL_ROUTE_STORAGE_NAME_REQUEST(accessToken);
            final RouteStoragesResponse actual = response.as(RouteStoragesResponse.class);

            // then
            assertSoftly(softly -> {
                softly.assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
                softly.assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
            });
        }

        @Test
        @DisplayName("멤버 ID에 해당하는 멤버가 존재하지 않으면 실패한다.")
        void fail_not_exist_member() {
            // given
            final String notExistMemberAccessToken = jwtService.createAccessToken(-1L);

            // when
            final ExtractableResponse<Response> response = FIND_ALL_ROUTE_STORAGE_NAME_REQUEST(notExistMemberAccessToken);

            // then
            assertSoftly(softly -> {
                softly.assertThat(response.statusCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
                softly.assertThat(response.jsonPath().getString("errorMessage")).contains("인증에 실패했습니다. 정확한 에러는 서버 로그를 확인해주세요.");
            });
        }
    }

    private static ExtractableResponse<Response> ADD_ROUTE_STORAGE_REQUEST(final String accessToken, final RouteStorageAddRequest request) {
        return RestAssured.given().log().all()
                .header(HttpHeaders.AUTHORIZATION, JWT_PREFIX + accessToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .when().log().all()
                .post("/route-storages")
                .then().log().all()
                .extract();
    }

    private static ExtractableResponse<Response> DELETE_ROUTE_REQUEST(final String accessToken, final RouteDeleteRequest request) {
        return RestAssured.given().log().all()
                .header(HttpHeaders.AUTHORIZATION, JWT_PREFIX + accessToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .when().log().all()
                .delete("/route-storages/routes")
                .then().log().all()
                .extract();
    }

    private static ExtractableResponse<Response> FIND_ALL_ROUTE_STORAGE_NAME_REQUEST(final String accessToken) {
        return RestAssured.given().log().all()
                .header(HttpHeaders.AUTHORIZATION, JWT_PREFIX + accessToken)
                .when().log().all()
                .get("/route-storages/names")
                .then().log().all()
                .extract();
    }
}
