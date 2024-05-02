package haru.harudongseon.route.presentation;

import static haru.harudongseon.common.fixtures.RouteFixtures.*;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import haru.harudongseon.common.E2ETest;
import haru.harudongseon.common.builder.MemberBuilder;
import haru.harudongseon.common.builder.PlaceBuilder;
import haru.harudongseon.common.builder.RouteBuilder;
import haru.harudongseon.common.builder.RouteTagBuilder;
import haru.harudongseon.member.domain.Member;
import haru.harudongseon.place.domain.Place;
import haru.harudongseon.route.application.dto.RouteAddRequest;
import haru.harudongseon.route.application.dto.RoutePlaceDto;
import haru.harudongseon.routetag.domain.RouteTag;
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

class RouteControllerTest extends E2ETest {

    @Autowired
    private MemberBuilder memberBuilder;

    @Autowired
    private RouteBuilder routeBuilder;

    @Autowired
    private RouteTagBuilder routeTagBuilder;

    @Autowired
    private PlaceBuilder placeBuilder;

    /**
     * 기본 상황 : Place 1, 2 DB 존재 / Tag 1, 2 DB 존재
     */
    @Nested
    @DisplayName("동선 추가 시")
    class AddRoute {

        @Test
        @DisplayName("추가할 태그가 서버에 존재하지 않는다면, 태그를 저장하고 동선 추가에 성공한다.")
        void success_when_not_exist_tag_save_tag() {
            // given
            final Member member = memberBuilder.defaultMember().build();
            final String accessToken = jwtService.createAccessToken(member.getId());

            final PlaceBuilder defalutPlaceBuilder = placeBuilder.defaultPlace();
            final RoutePlaceDto routePlaceDto1 = defalutPlaceBuilder.buildRoutePlaceDto();
            final Place place1 = defalutPlaceBuilder.build();

            final PlaceBuilder newPlaceBuilder = placeBuilder.defaultPlace().providerPlaceId("new ProviderPlaceId");
            final RoutePlaceDto routePlaceDto2 = newPlaceBuilder.buildRoutePlaceDto();
            final Place place2 = newPlaceBuilder.build();

            final Set<RoutePlaceDto> routePlaceDtos = Set.of(routePlaceDto1, routePlaceDto2);
            final RouteAddRequest routeAddRequest = new RouteAddRequest(기본_동선_날짜, 기본_동선_제목, Set.of(기본_동선_태그1, 기본_동선_태그2), 기본_동선_이동수단, routePlaceDtos);

            // when
            final ExtractableResponse<Response> response = ADD_ROUTE_REQUEST(accessToken, routeAddRequest);

            // then
            assertSoftly(softly -> {
                softly.assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
                softly.assertThat(response.header("Location")).contains("/routes/");
            });
        }

        @Test
        @DisplayName("추가할 장소가 존재하지 않는다면, 장소를 추가하고 동선 성공에 성공한다.")
        void success_when_not_exist_place_save_place() {
            // given
            final Member member = memberBuilder.defaultMember().build();
            final String accessToken = jwtService.createAccessToken(member.getId());

            final PlaceBuilder defalutPlaceBuilder = placeBuilder.defaultPlace();
            final RoutePlaceDto routePlaceDto1 = defalutPlaceBuilder.buildRoutePlaceDto();

            final PlaceBuilder newPlaceBuilder = placeBuilder.defaultPlace().providerPlaceId("new ProviderPlaceId");
            final RoutePlaceDto routePlaceDto2 = newPlaceBuilder.buildRoutePlaceDto();

            final Set<RoutePlaceDto> routePlaceDtos = Set.of(routePlaceDto1, routePlaceDto2);

            final RouteTagBuilder defaultRouteTagBuilder = routeTagBuilder.defaultRouteTag();
            final RouteTag routeTag1 = defaultRouteTagBuilder.defaultRouteTag().name("tag1").build();
            final RouteTag routeTag2 = defaultRouteTagBuilder.defaultRouteTag().name("tag2").build();

            final RouteAddRequest routeAddRequest = new RouteAddRequest(기본_동선_날짜, 기본_동선_제목, Set.of(routeTag1.getName(), routeTag2.getName()), 기본_동선_이동수단, routePlaceDtos);

            // when
            final ExtractableResponse<Response> response = ADD_ROUTE_REQUEST(accessToken, routeAddRequest);

            // then
            assertSoftly(softly -> {
                softly.assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
                softly.assertThat(response.header("Location")).contains("/routes/");
            });
        }

        @Test
        @DisplayName("추가할 태그와 장소가 모두 존재하지 않는다면, 태그와 장소를 저장하고 동선 추가에 성공한다.")
        void success_when_not_exist_tag_and_place_save_tag_and_place() {
            // given
            final Member member = memberBuilder.defaultMember().build();
            final String accessToken = jwtService.createAccessToken(member.getId());

            final PlaceBuilder defalutPlaceBuilder = placeBuilder.defaultPlace();
            final RoutePlaceDto routePlaceDto1 = defalutPlaceBuilder.buildRoutePlaceDto();

            final PlaceBuilder newPlaceBuilder = placeBuilder.defaultPlace().providerPlaceId("new ProviderPlaceId");
            final RoutePlaceDto routePlaceDto2 = newPlaceBuilder.buildRoutePlaceDto();

            final Set<RoutePlaceDto> routePlaceDtos = Set.of(routePlaceDto1, routePlaceDto2);

            final RouteAddRequest routeAddRequest = new RouteAddRequest(기본_동선_날짜, 기본_동선_제목, Set.of(기본_동선_태그1, 기본_동선_태그2), 기본_동선_이동수단, routePlaceDtos);

            // when
            final ExtractableResponse<Response> response = ADD_ROUTE_REQUEST(accessToken, routeAddRequest);

            // then
            assertSoftly(softly -> {
                softly.assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
                softly.assertThat(response.header("Location")).contains("/routes/");
            });
        }

        @Test
        @DisplayName("추가할 태그와 장소가 모두 존재한다면, 저장하지 않고 조회하여 동선 추가에 성공한다.")
        void success_when_exist_all_find_tag_and_place() {
            // given
            final Member member = memberBuilder.defaultMember().build();
            final String accessToken = jwtService.createAccessToken(member.getId());

            final RouteTagBuilder defaultRouteTagBuilder = routeTagBuilder.defaultRouteTag();
            final RouteTag routeTag1 = defaultRouteTagBuilder.defaultRouteTag().name("tag1").build();
            final RouteTag routeTag2 = defaultRouteTagBuilder.defaultRouteTag().name("tag2").build();

            final PlaceBuilder defalutPlaceBuilder = placeBuilder.defaultPlace();
            final RoutePlaceDto routePlaceDto1 = defalutPlaceBuilder.buildRoutePlaceDto();
            final Place place1 = defalutPlaceBuilder.build();

            final PlaceBuilder newPlaceBuilder = placeBuilder.defaultPlace().providerPlaceId("new ProviderPlaceId");
            final RoutePlaceDto routePlaceDto2 = newPlaceBuilder.buildRoutePlaceDto();
            final Place place2 = newPlaceBuilder.build();

            final Set<RoutePlaceDto> routePlaceDtos = Set.of(routePlaceDto1, routePlaceDto2);
            final RouteAddRequest routeAddRequest = new RouteAddRequest(기본_동선_날짜, 기본_동선_제목, Set.of(routeTag1.getName(), routeTag2.getName()), 기본_동선_이동수단, routePlaceDtos);

            // when
            final ExtractableResponse<Response> response = ADD_ROUTE_REQUEST(accessToken, routeAddRequest);

            // then
            assertSoftly(softly -> {
                softly.assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
                softly.assertThat(response.header("Location")).contains("/routes/");
            });
        }

        @Test
        @DisplayName("동선의 태그가 하나도 없더라도 추가에 성공한다.")
        void success_when_not_exist_tag() {
            // given
            final Member member = memberBuilder.defaultMember().build();
            final String accessToken = jwtService.createAccessToken(member.getId());

            final RouteTagBuilder defaultRouteTagBuilder = routeTagBuilder.defaultRouteTag();
            final RouteTag routeTag1 = defaultRouteTagBuilder.defaultRouteTag().name("tag1").build();
            final RouteTag routeTag2 = defaultRouteTagBuilder.defaultRouteTag().name("tag2").build();

            final PlaceBuilder defalutPlaceBuilder = placeBuilder.defaultPlace();
            final RoutePlaceDto routePlaceDto1 = defalutPlaceBuilder.buildRoutePlaceDto();
            final Place place1 = defalutPlaceBuilder.build();

            final PlaceBuilder newPlaceBuilder = placeBuilder.defaultPlace().providerPlaceId("new ProviderPlaceId");
            final RoutePlaceDto routePlaceDto2 = newPlaceBuilder.buildRoutePlaceDto();
            final Place place2 = newPlaceBuilder.build();

            final Set<RoutePlaceDto> routePlaceDtos = Set.of(routePlaceDto1, routePlaceDto2);
            final RouteAddRequest routeAddRequest = new RouteAddRequest(기본_동선_날짜, 기본_동선_제목, Collections.EMPTY_SET, 기본_동선_이동수단, routePlaceDtos);

            // when
            final ExtractableResponse<Response> response = ADD_ROUTE_REQUEST(accessToken, routeAddRequest);

            // then
            assertSoftly(softly -> {
                softly.assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
                softly.assertThat(response.header("Location")).contains("/routes/");
            });
        }

        @Test
        @DisplayName("동선의 이동수단을 선택하지 않을 때 NONE으로 입력하면 추가에 성공한다.")
        void success_when_move_way_not_select_NONE() {
            // given
            final Member member = memberBuilder.defaultMember().build();
            final String accessToken = jwtService.createAccessToken(member.getId());

            final RouteTagBuilder defaultRouteTagBuilder = routeTagBuilder.defaultRouteTag();
            final RouteTag routeTag1 = defaultRouteTagBuilder.defaultRouteTag().name("tag1").build();
            final RouteTag routeTag2 = defaultRouteTagBuilder.defaultRouteTag().name("tag2").build();

            final PlaceBuilder defalutPlaceBuilder = placeBuilder.defaultPlace();
            final RoutePlaceDto routePlaceDto1 = defalutPlaceBuilder.buildRoutePlaceDto();
            final Place place1 = defalutPlaceBuilder.build();

            final PlaceBuilder newPlaceBuilder = placeBuilder.defaultPlace().providerPlaceId("new ProviderPlaceId");
            final RoutePlaceDto routePlaceDto2 = newPlaceBuilder.buildRoutePlaceDto();
            final Place place2 = newPlaceBuilder.build();

            final Set<RoutePlaceDto> routePlaceDtos = Set.of(routePlaceDto1, routePlaceDto2);
            final RouteAddRequest routeAddRequest = new RouteAddRequest(기본_동선_날짜, 기본_동선_제목, Set.of(routeTag1.getName(), routeTag2.getName()), "NONE", routePlaceDtos);

            // when
            final ExtractableResponse<Response> response = ADD_ROUTE_REQUEST(accessToken, routeAddRequest);

            // then
            assertSoftly(softly -> {
                softly.assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
                softly.assertThat(response.header("Location")).contains("/routes/");
            });
        }

        @Test
        @DisplayName("동선의 장소가 하나도 없더라도 추가에 성공한다.")
        void success_when_not_exist_place() {
            // given
            final Member member = memberBuilder.defaultMember().build();
            final String accessToken = jwtService.createAccessToken(member.getId());

            final RouteTagBuilder defaultRouteTagBuilder = routeTagBuilder.defaultRouteTag();
            final RouteTag routeTag1 = defaultRouteTagBuilder.defaultRouteTag().name("tag1").build();
            final RouteTag routeTag2 = defaultRouteTagBuilder.defaultRouteTag().name("tag2").build();

            final PlaceBuilder defalutPlaceBuilder = placeBuilder.defaultPlace();
            final RoutePlaceDto routePlaceDto1 = defalutPlaceBuilder.buildRoutePlaceDto();
            final Place place1 = defalutPlaceBuilder.build();

            final PlaceBuilder newPlaceBuilder = placeBuilder.defaultPlace().providerPlaceId("new ProviderPlaceId");
            final RoutePlaceDto routePlaceDto2 = newPlaceBuilder.buildRoutePlaceDto();
            final Place place2 = newPlaceBuilder.build();

            final RouteAddRequest routeAddRequest = new RouteAddRequest(기본_동선_날짜, 기본_동선_제목, Collections.EMPTY_SET, 기본_동선_이동수단, Collections.EMPTY_SET);

            // when
            final ExtractableResponse<Response> response = ADD_ROUTE_REQUEST(accessToken, routeAddRequest);

            // then
            assertSoftly(softly -> {
                softly.assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
                softly.assertThat(response.header("Location")).contains("/routes/");
            });
        }

        @Test
        @DisplayName("멤버 ID에 해당하는 멤버가 존재하지 않으면 실패한다.")
        void fail_not_exist_member() {
            // given
            final Member member = memberBuilder.defaultMember().build();
            final Long notExistMemberId = -1L;
            final String notExistMemberAccessToken = jwtService.createAccessToken(notExistMemberId);

            final PlaceBuilder defalutPlaceBuilder = placeBuilder.defaultPlace();
            final RoutePlaceDto routePlaceDto1 = defalutPlaceBuilder.buildRoutePlaceDto();

            final PlaceBuilder newPlaceBuilder = placeBuilder.defaultPlace().providerPlaceId("new ProviderPlaceId");
            final RoutePlaceDto routePlaceDto2 = newPlaceBuilder.buildRoutePlaceDto();

            final Set<RoutePlaceDto> routePlaceDtos = Set.of(routePlaceDto1, routePlaceDto2);

            final RouteAddRequest routeAddRequest = new RouteAddRequest(기본_동선_날짜, 기본_동선_제목, Set.of(기본_동선_태그1, 기본_동선_태그2), 기본_동선_이동수단, routePlaceDtos);

            // when
            final ExtractableResponse<Response> response = ADD_ROUTE_REQUEST(notExistMemberAccessToken, routeAddRequest);

            // then
            assertSoftly(softly -> {
                softly.assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
                softly.assertThat(response.jsonPath().getString("errorMessage")).isEqualTo("해당하는 멤버를 찾을 수 없습니다.");
            });
        }

        @Test
        @DisplayName("동선의 날짜가 없으면 실패한다.")
        void fail_date_null() {
            // given
            final Member member = memberBuilder.defaultMember().build();
            final String accessToken = jwtService.createAccessToken(member.getId());

            final RouteTagBuilder defaultRouteTagBuilder = routeTagBuilder.defaultRouteTag();
            final RouteTag routeTag1 = defaultRouteTagBuilder.defaultRouteTag().name("tag1").build();
            final RouteTag routeTag2 = defaultRouteTagBuilder.defaultRouteTag().name("tag2").build();

            final PlaceBuilder defalutPlaceBuilder = placeBuilder.defaultPlace();
            final RoutePlaceDto routePlaceDto1 = defalutPlaceBuilder.buildRoutePlaceDto();
            final Place place1 = defalutPlaceBuilder.build();

            final PlaceBuilder newPlaceBuilder = placeBuilder.defaultPlace().providerPlaceId("new ProviderPlaceId");
            final RoutePlaceDto routePlaceDto2 = newPlaceBuilder.buildRoutePlaceDto();
            final Place place2 = newPlaceBuilder.build();

            final Set<RoutePlaceDto> routePlaceDtos = Set.of(routePlaceDto1, routePlaceDto2);
            final RouteAddRequest nullDateRequest = new RouteAddRequest(null, 기본_동선_제목, Set.of(routeTag1.getName(), routeTag2.getName()), 기본_동선_이동수단, routePlaceDtos);

            // when
            final ExtractableResponse<Response> response = ADD_ROUTE_REQUEST(accessToken, nullDateRequest);

            // then
            assertSoftly(softly -> {
                softly.assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                softly.assertThat(response.jsonPath().getString("errorMessage")).isEqualTo("동선 날짜는 공백일 수 없습니다.");
            });
        }

        @Test
        @DisplayName("잘못된 날짜 형식이면 실패한다.")
        void fail_invalid_date() {
            // given
            final Member member = memberBuilder.defaultMember().build();
            final String accessToken = jwtService.createAccessToken(member.getId());

            final RouteTagBuilder defaultRouteTagBuilder = routeTagBuilder.defaultRouteTag();
            final RouteTag routeTag1 = defaultRouteTagBuilder.defaultRouteTag().name("tag1").build();
            final RouteTag routeTag2 = defaultRouteTagBuilder.defaultRouteTag().name("tag2").build();

            final PlaceBuilder defalutPlaceBuilder = placeBuilder.defaultPlace();
            final RoutePlaceDto routePlaceDto1 = defalutPlaceBuilder.buildRoutePlaceDto();
            final Place place1 = defalutPlaceBuilder.build();

            final PlaceBuilder newPlaceBuilder = placeBuilder.defaultPlace().providerPlaceId("new ProviderPlaceId");
            final RoutePlaceDto routePlaceDto2 = newPlaceBuilder.buildRoutePlaceDto();
            final Place place2 = newPlaceBuilder.build();

            String invalidDate = "\"date\": \"2023:05:24\",";

            // when
            final ExtractableResponse<Response> response = INVALID_DATE_ADD_ROUTE_REQUEST(accessToken, invalidDate);

            // then
            assertSoftly(softly -> {
                softly.assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
            });
        }

        @ParameterizedTest
        @ValueSource(strings = {"", " "})
        @DisplayName("동선 제목이 없으면 실패한다.")
        void fail_not_exist_title(final String notExistTitle) {
            // given
            final Member member = memberBuilder.defaultMember().build();
            final String accessToken = jwtService.createAccessToken(member.getId());

            final RouteTagBuilder defaultRouteTagBuilder = routeTagBuilder.defaultRouteTag();
            final RouteTag routeTag1 = defaultRouteTagBuilder.defaultRouteTag().name("tag1").build();
            final RouteTag routeTag2 = defaultRouteTagBuilder.defaultRouteTag().name("tag2").build();

            final PlaceBuilder defalutPlaceBuilder = placeBuilder.defaultPlace();
            final RoutePlaceDto routePlaceDto1 = defalutPlaceBuilder.buildRoutePlaceDto();
            final Place place1 = defalutPlaceBuilder.build();

            final PlaceBuilder newPlaceBuilder = placeBuilder.defaultPlace().providerPlaceId("new ProviderPlaceId");
            final RoutePlaceDto routePlaceDto2 = newPlaceBuilder.buildRoutePlaceDto();
            final Place place2 = newPlaceBuilder.build();

            final Set<RoutePlaceDto> routePlaceDtos = Set.of(routePlaceDto1, routePlaceDto2);
            final RouteAddRequest notExistTitleRequest = new RouteAddRequest(기본_동선_날짜, notExistTitle, Set.of(routeTag1.getName(), routeTag2.getName()), 기본_동선_이동수단, routePlaceDtos);

            // when
            final ExtractableResponse<Response> response = ADD_ROUTE_REQUEST(accessToken, notExistTitleRequest);

            // then
            assertSoftly(softly -> {
                softly.assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                softly.assertThat(response.jsonPath().getString("errorMessage")).isEqualTo("동선 제목은 공백일 수 없습니다.");
            });
        }

        @ParameterizedTest
        @ValueSource(strings = {"띄어쓰기 포함 15자 이하여야", "띄어쓰기포함15자이하여야합니다."})
        @DisplayName("동선 제목이 길이 제한을 초과하면 실패한다.")
        void fail_over_length_title(final String overLengthTitle) {
            // given
            final Member member = memberBuilder.defaultMember().build();
            final String accessToken = jwtService.createAccessToken(member.getId());

            final RouteTagBuilder defaultRouteTagBuilder = routeTagBuilder.defaultRouteTag();
            final RouteTag routeTag1 = defaultRouteTagBuilder.defaultRouteTag().name("tag1").build();
            final RouteTag routeTag2 = defaultRouteTagBuilder.defaultRouteTag().name("tag2").build();

            final PlaceBuilder defalutPlaceBuilder = placeBuilder.defaultPlace();
            final RoutePlaceDto routePlaceDto1 = defalutPlaceBuilder.buildRoutePlaceDto();
            final Place place1 = defalutPlaceBuilder.build();

            final PlaceBuilder newPlaceBuilder = placeBuilder.defaultPlace().providerPlaceId("new ProviderPlaceId");
            final RoutePlaceDto routePlaceDto2 = newPlaceBuilder.buildRoutePlaceDto();
            final Place place2 = newPlaceBuilder.build();

            final Set<RoutePlaceDto> routePlaceDtos = Set.of(routePlaceDto1, routePlaceDto2);
            final RouteAddRequest notExistTitleRequest = new RouteAddRequest(기본_동선_날짜, overLengthTitle, Set.of(routeTag1.getName(), routeTag2.getName()), 기본_동선_이동수단, routePlaceDtos);

            // when
            final ExtractableResponse<Response> response = ADD_ROUTE_REQUEST(accessToken, notExistTitleRequest);

            // then
            assertSoftly(softly -> {
                softly.assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                softly.assertThat(response.jsonPath().getString("errorMessage")).isEqualTo("동선 제목은 15자 이하여야합니다.");
            });
        }

        @ParameterizedTest
        @ValueSource(strings = {"", " "})
        @DisplayName("동선 이동수단 미선택 시 NONE을 입력하지 않고, 아예 입력하지 않으면 실패한다.")
        void fail_not_exist_move_ways(final String notExistMoveWays) {
            // given
            final Member member = memberBuilder.defaultMember().build();
            final String accessToken = jwtService.createAccessToken(member.getId());

            final RouteTagBuilder defaultRouteTagBuilder = routeTagBuilder.defaultRouteTag();
            final RouteTag routeTag1 = defaultRouteTagBuilder.defaultRouteTag().name("tag1").build();
            final RouteTag routeTag2 = defaultRouteTagBuilder.defaultRouteTag().name("tag2").build();

            final PlaceBuilder defalutPlaceBuilder = placeBuilder.defaultPlace();
            final RoutePlaceDto routePlaceDto1 = defalutPlaceBuilder.buildRoutePlaceDto();
            final Place place1 = defalutPlaceBuilder.build();

            final PlaceBuilder newPlaceBuilder = placeBuilder.defaultPlace().providerPlaceId("new ProviderPlaceId");
            final RoutePlaceDto routePlaceDto2 = newPlaceBuilder.buildRoutePlaceDto();
            final Place place2 = newPlaceBuilder.build();

            final Set<RoutePlaceDto> routePlaceDtos = Set.of(routePlaceDto1, routePlaceDto2);
            final RouteAddRequest notExistMoveWaysRequest = new RouteAddRequest(기본_동선_날짜, 기본_동선_제목, Set.of(routeTag1.getName(), routeTag2.getName()), notExistMoveWays, routePlaceDtos);

            // when
            final ExtractableResponse<Response> response = ADD_ROUTE_REQUEST(accessToken, notExistMoveWaysRequest);

            // then
            assertSoftly(softly -> {
                softly.assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                softly.assertThat(response.jsonPath().getString("errorMessage")).isEqualTo("동선 이동수단은 공백일 수 없습니다. 미선택인 경우 NONE을 입력하세요.");
            });
        }

        @Test
        @DisplayName("동선의 장소 개수가 30개 초과면 실패한다.")
        void fail_place_count_over_validation() {
            // given
            final Member member = memberBuilder.defaultMember().build();
            final String accessToken = jwtService.createAccessToken(member.getId());

            final RouteTagBuilder defaultRouteTagBuilder = routeTagBuilder.defaultRouteTag();
            final RouteTag routeTag1 = defaultRouteTagBuilder.defaultRouteTag().name("tag1").build();
            final RouteTag routeTag2 = defaultRouteTagBuilder.defaultRouteTag().name("tag2").build();

            final PlaceBuilder defalutPlaceBuilder = placeBuilder.defaultPlace();
            final RoutePlaceDto routePlaceDto1 = defalutPlaceBuilder.buildRoutePlaceDto();
            final Place place1 = defalutPlaceBuilder.build();

            final PlaceBuilder newPlaceBuilder = placeBuilder.defaultPlace().providerPlaceId("new ProviderPlaceId");
            final RoutePlaceDto routePlaceDto2 = newPlaceBuilder.buildRoutePlaceDto();
            final Place place2 = newPlaceBuilder.build();

            final Set<RoutePlaceDto> overCountRoutePlaceDtos = new HashSet<>();
            final int maxCount = 30;
            for (int i = 0; i < maxCount + 1; i++) {
                overCountRoutePlaceDtos.add(defalutPlaceBuilder.name("place" + i).buildRoutePlaceDto());
            }

            final RouteAddRequest nullDateRequest = new RouteAddRequest(기본_동선_날짜, 기본_동선_제목, Set.of(routeTag1.getName(), routeTag2.getName()), 기본_동선_이동수단, overCountRoutePlaceDtos);

            // when
            final ExtractableResponse<Response> response = ADD_ROUTE_REQUEST(accessToken, nullDateRequest);

            // then
            assertSoftly(softly -> {
                softly.assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                softly.assertThat(response.jsonPath().getString("errorMessage")).isEqualTo("동선 장소는 30개 이하여야합니다.");
            });
        }
    }

    private static ExtractableResponse<Response> ADD_ROUTE_REQUEST(final String accessToken, final RouteAddRequest request) {
        return RestAssured.given().log().all()
                .header(HttpHeaders.AUTHORIZATION, JWT_PREFIX + accessToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .when().log().all()
                .post("/routes")
                .then().log().all()
                .extract();
    }

    private static ExtractableResponse<Response> INVALID_DATE_ADD_ROUTE_REQUEST(final String accessToken, String invalidDate) {
        return RestAssured.given().log().all()
                .header(HttpHeaders.AUTHORIZATION, JWT_PREFIX + accessToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body("{" +
                        invalidDate +
                        "\"title\": \"하루동선1\"," +
                        "\"tag\": [\"스터디\", \"데이트\"]," +
                        "\"moveWay\": \"대중교통/자동차\"," +
                        "\"routePlaces\": [" +
                        "{" +
                        "\"providerPlaceId\": \"ChIJ07n0DkZ8ezUR7wp5kpXtCYQ\"," +
                        "\"name\": \"스타벅스 부평점\"," +
                        "\"category\": \"커피숍/커피 전문점\"," +
                        "\"photoReferences\": [\"AxxA\", \"BxxA\", \"BxxC\"]," +
                        "\"latitude\": 2.2212," +
                        "\"longitude\": 1.1123," +
                        "\"openingHours\": \"월요일: 오전 7:00 ~ 오후 11:00, 화요일: 오전 7:00 ~ 오후 11:00, 수요일: 오전 7:00 ~ 오후 11:00, 목요일: 오전 7:00 ~ 오후 11:00, 금요일: 오전 7:00 ~ 오후 11:00, 토요일: 오전 7:00 ~ 오후 11:00, 일요일: 오전 9:00 ~ 오후 11:00\"," +
                        "\"addressName\": \"인천광역시 부평구 경원대로 1397\"," +
                        "\"phoneNumber\": \"1522-3232\"," +
                        "\"website\": \"http://www.starbucks.co.kr/\"," +
                        "\"googleMapsUri\": \"https://maps.google.com/?cid=9514396914460199663\"," +
                        "\"reservable\": \"FALSE\"," +
                        "\"takeoutAvailable\": \"TRUE\"," +
                        "\"parkingAvailable\": \"FALSE\"" +
                        "}" +
                        "]" +
                        "}")
                .when().log().all()
                .post("/routes")
                .then().log().all()
                .extract();
    }
}
