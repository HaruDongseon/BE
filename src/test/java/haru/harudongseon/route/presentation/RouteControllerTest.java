package haru.harudongseon.route.presentation;

import static haru.harudongseon.common.fixtures.PlaceFixtures.*;
import static haru.harudongseon.common.fixtures.RouteFixtures.*;
import static haru.harudongseon.common.fixtures.RouteTagFixtures.기본_동선_태그1_엔티티;
import static haru.harudongseon.common.fixtures.RouteTagFixtures.기본_동선_태그2_엔티티;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import haru.harudongseon.common.E2ETest;
import haru.harudongseon.common.builder.MemberBuilder;
import haru.harudongseon.common.builder.PlaceBuilder;
import haru.harudongseon.common.builder.RouteTagBuilder;
import haru.harudongseon.member.domain.Member;
import haru.harudongseon.place.domain.Place;
import haru.harudongseon.route.application.dto.RouteAddRequest;
import haru.harudongseon.route.application.dto.RoutePlaceDto;
import haru.harudongseon.route.application.dto.RouteResponse;
import haru.harudongseon.route.application.dto.RoutesResponse;
import haru.harudongseon.route.domain.Route;
import haru.harudongseon.routetag.domain.RouteTag;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

class RouteControllerTest extends E2ETest {

    @Autowired
    private MemberBuilder memberBuilder;

    @Autowired
    private RouteTagBuilder routeTagBuilder;

    @Autowired
    private PlaceBuilder placeBuilder;

    @Autowired
    private RedisTemplate redisTemplate;

    @BeforeEach
    void setUp() {
        redisTemplate.getConnectionFactory().getConnection().flushAll();
    }

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

            final PlaceBuilder defaultPlace1Builder = placeBuilder.defaultPlace1();
            final RoutePlaceDto routePlaceDto1 = defaultPlace1Builder.buildRoutePlaceDto();
            final Place place1 = defaultPlace1Builder.build();

            final PlaceBuilder defaultPlace2Builder = placeBuilder.defaultPlace2();
            final RoutePlaceDto routePlaceDto2 = defaultPlace2Builder.buildRoutePlaceDto();
            final Place place2 = defaultPlace2Builder.build();

            final List<String> tag = List.of(기본_동선_태그1, 기본_동선_태그2);
            final List<RoutePlaceDto> routePlaceDtos = List.of(routePlaceDto1, routePlaceDto2);

            final RouteAddRequest routeAddRequest = new RouteAddRequest(기본_동선_날짜, 기본_동선_제목, tag, 기본_동선_이동수단, routePlaceDtos);

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

            final PlaceBuilder defaultPlace1Builder = placeBuilder.defaultPlace1();
            final RoutePlaceDto routePlaceDto1 = defaultPlace1Builder.buildRoutePlaceDto();

            final PlaceBuilder defaultPlace2Builder = placeBuilder.defaultPlace2();
            final RoutePlaceDto routePlaceDto2 = defaultPlace2Builder.buildRoutePlaceDto();

            final RouteTagBuilder defaultRouteTagBuilder = routeTagBuilder.defaultRouteTag();
            final RouteTag routeTag1 = defaultRouteTagBuilder.defaultRouteTag().name("tag1").build();
            final RouteTag routeTag2 = defaultRouteTagBuilder.defaultRouteTag().name("tag2").build();

            final List<String> tag = List.of(routeTag1.getName(), routeTag2.getName());
            final List<RoutePlaceDto> routePlaceDtos = List.of(routePlaceDto1, routePlaceDto2);

            final RouteAddRequest routeAddRequest = new RouteAddRequest(기본_동선_날짜, 기본_동선_제목, tag, 기본_동선_이동수단, routePlaceDtos);

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

            final PlaceBuilder defaultPlace1Builder = placeBuilder.defaultPlace1();
            final RoutePlaceDto routePlaceDto1 = defaultPlace1Builder.buildRoutePlaceDto();

            final PlaceBuilder defaultPlace2Builder = placeBuilder.defaultPlace2();
            final RoutePlaceDto routePlaceDto2 = defaultPlace2Builder.buildRoutePlaceDto();

            final List<String> tag = List.of(기본_동선_태그1, 기본_동선_태그2);
            final List<RoutePlaceDto> routePlaceDtos = List.of(routePlaceDto1, routePlaceDto2);

            final RouteAddRequest routeAddRequest = new RouteAddRequest(기본_동선_날짜, 기본_동선_제목, tag, 기본_동선_이동수단, routePlaceDtos);

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

            final PlaceBuilder defaultPlace1Builder = placeBuilder.defaultPlace1();
            final RoutePlaceDto routePlaceDto1 = defaultPlace1Builder.buildRoutePlaceDto();
            final Place place1 = defaultPlace1Builder.build();

            final PlaceBuilder defaultPlace2Builder = placeBuilder.defaultPlace2();
            final RoutePlaceDto routePlaceDto2 = defaultPlace2Builder.buildRoutePlaceDto();
            final Place place2 = defaultPlace2Builder.build();

            final List<String> tag = List.of(routeTag1.getName(), routeTag2.getName());
            final List<RoutePlaceDto> routePlaceDtos = List.of(routePlaceDto1, routePlaceDto2);

            final RouteAddRequest routeAddRequest = new RouteAddRequest(기본_동선_날짜, 기본_동선_제목, tag, 기본_동선_이동수단, routePlaceDtos);

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

            final PlaceBuilder defaultPlace1Builder = placeBuilder.defaultPlace1();
            final RoutePlaceDto routePlaceDto1 = defaultPlace1Builder.buildRoutePlaceDto();
            final Place place1 = defaultPlace1Builder.build();

            final PlaceBuilder defaultPlace2Builder = placeBuilder.defaultPlace2();
            final RoutePlaceDto routePlaceDto2 = defaultPlace2Builder.buildRoutePlaceDto();
            final Place place2 = defaultPlace2Builder.build();

            final List<RoutePlaceDto> routePlaceDtos = List.of(routePlaceDto1, routePlaceDto2);
            final RouteAddRequest routeAddRequest = new RouteAddRequest(기본_동선_날짜, 기본_동선_제목, Collections.emptyList(), 기본_동선_이동수단, routePlaceDtos);

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

            final PlaceBuilder defaultPlace1Builder = placeBuilder.defaultPlace1();
            final RoutePlaceDto routePlaceDto1 = defaultPlace1Builder.buildRoutePlaceDto();
            final Place place1 = defaultPlace1Builder.build();

            final PlaceBuilder defaultPlace2Builder = placeBuilder.defaultPlace2();
            final RoutePlaceDto routePlaceDto2 = defaultPlace2Builder.buildRoutePlaceDto();
            final Place place2 = defaultPlace2Builder.build();

            final List<String> tag = List.of(routeTag1.getName(), routeTag2.getName());
            final List<RoutePlaceDto> routePlaceDtos = List.of(routePlaceDto1, routePlaceDto2);

            final RouteAddRequest routeAddRequest = new RouteAddRequest(기본_동선_날짜, 기본_동선_제목, tag, "NONE", routePlaceDtos);

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

            final PlaceBuilder defaultPlace1Builder = placeBuilder.defaultPlace1();
            final RoutePlaceDto routePlaceDto1 = defaultPlace1Builder.buildRoutePlaceDto();
            final Place place1 = defaultPlace1Builder.build();

            final PlaceBuilder defaultPlace2Builder = placeBuilder.defaultPlace2();
            final RoutePlaceDto routePlaceDto2 = defaultPlace2Builder.buildRoutePlaceDto();
            final Place place2 = defaultPlace2Builder.build();

            final List<String> tag = List.of(routeTag1.getName(), routeTag2.getName());

            final RouteAddRequest routeAddRequest = new RouteAddRequest(기본_동선_날짜, 기본_동선_제목, tag, 기본_동선_이동수단, Collections.emptyList());

            // when
            final ExtractableResponse<Response> response = ADD_ROUTE_REQUEST(accessToken, routeAddRequest);

            // then
            assertSoftly(softly -> {
                softly.assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
                softly.assertThat(response.header("Location")).contains("/routes/");
            });
        }

        @Test
        @DisplayName("동선에 중복된 태그가 존재하면 실패한다.")
        void fail_duplicate_tag() {
            // given
            final Member member = memberBuilder.defaultMember().build();
            final String accessToken = jwtService.createAccessToken(member.getId());

            final PlaceBuilder defaultPlace1Builder = placeBuilder.defaultPlace1();
            final RoutePlaceDto routePlaceDto1 = defaultPlace1Builder.buildRoutePlaceDto();

            final PlaceBuilder defaultPlace2Builder = placeBuilder.defaultPlace2();
            final RoutePlaceDto routePlaceDto2 = defaultPlace2Builder.buildRoutePlaceDto();

            final List<String> duplicateTag = List.of(기본_동선_태그1, 기본_동선_태그1);
            final List<RoutePlaceDto> routePlaceDtos = List.of(routePlaceDto1, routePlaceDto2);

            final RouteAddRequest routeAddRequest = new RouteAddRequest(기본_동선_날짜, 기본_동선_제목, duplicateTag, 기본_동선_이동수단, routePlaceDtos);

            // when
            final ExtractableResponse<Response> response = ADD_ROUTE_REQUEST(accessToken, routeAddRequest);

            // then
            assertSoftly(softly -> {
                softly.assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                softly.assertThat(response.jsonPath().getString("errorMessage")).isEqualTo("동선에 중복된 태그가 존재합니다.");
            });
        }

        @Test
        @DisplayName("동선에 포함된 장소 각각에 중복된 사진이 존재하면 실패한다.")
        void fail_duplicate_route_place_photo_references() {
            // given
            final Member member = memberBuilder.defaultMember().build();
            final String accessToken = jwtService.createAccessToken(member.getId());

            final List<String> duplicateRoutePlace1PhotoReferences = List.of(기본_장소1_사진_참조1, 기본_장소1_사진_참조1, 기본_장소1_사진_참조2);
            final List<String> duplicateRoutePlace2PhotoReferences = List.of(기본_장소2_사진_참조1, 기본_장소2_사진_참조1, 기본_장소2_사진_참조2);

            final PlaceBuilder defaultPlace1Builder = placeBuilder.defaultPlace1();
            final RoutePlaceDto routePlaceDto1 = defaultPlace1Builder
                    .photoReferences(duplicateRoutePlace1PhotoReferences)
                    .buildRoutePlaceDto();

            final PlaceBuilder defaultPlace2Builder = placeBuilder.defaultPlace2();
            final RoutePlaceDto routePlaceDto2 = defaultPlace2Builder
                    .photoReferences(duplicateRoutePlace2PhotoReferences)
                    .buildRoutePlaceDto();

            final List<String> tag = List.of(기본_동선_태그1, 기본_동선_태그2);
            final List<RoutePlaceDto> duplicateRoutePlaceDtos = List.of(routePlaceDto1, routePlaceDto2);

            final RouteAddRequest routeAddRequest = new RouteAddRequest(기본_동선_날짜, 기본_동선_제목, tag, 기본_동선_이동수단, duplicateRoutePlaceDtos);

            // when
            final ExtractableResponse<Response> response = ADD_ROUTE_REQUEST(accessToken, routeAddRequest);

            // then
            assertSoftly(softly -> {
                softly.assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                softly.assertThat(response.jsonPath().getString("errorMessage")).isEqualTo("동선 장소에 중복된 사진이 존재합니다.");
            });
        }

        @Test
        @DisplayName("멤버 ID에 해당하는 멤버가 존재하지 않으면 실패한다.")
        void fail_not_exist_member() {
            // given
            final Member member = memberBuilder.defaultMember().build();
            final Long notExistMemberId = -1L;
            final String notExistMemberAccessToken = jwtService.createAccessToken(notExistMemberId);

            final PlaceBuilder defaultPlace1Builder = placeBuilder.defaultPlace1();
            final RoutePlaceDto routePlaceDto1 = defaultPlace1Builder.buildRoutePlaceDto();
            final Place place1 = defaultPlace1Builder.build();

            final PlaceBuilder defaultPlace2Builder = placeBuilder.defaultPlace2();
            final RoutePlaceDto routePlaceDto2 = defaultPlace2Builder.buildRoutePlaceDto();
            final Place place2 = defaultPlace2Builder.build();

            final List<String> tag = List.of(기본_동선_태그1, 기본_동선_태그2);
            final List<RoutePlaceDto> routePlaceDtos = List.of(routePlaceDto1, routePlaceDto2);

            final RouteAddRequest routeAddRequest = new RouteAddRequest(기본_동선_날짜, 기본_동선_제목, tag, 기본_동선_이동수단, routePlaceDtos);

            // when
            final ExtractableResponse<Response> response = ADD_ROUTE_REQUEST(notExistMemberAccessToken, routeAddRequest);

            // then
            assertSoftly(softly -> {
                softly.assertThat(response.statusCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
                softly.assertThat(response.jsonPath().getString("errorMessage")).isEqualTo("인증에 실패했습니다. 정확한 에러는 서버 로그를 확인해주세요.");
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

            final PlaceBuilder defaultPlace1Builder = placeBuilder.defaultPlace1();
            final RoutePlaceDto routePlaceDto1 = defaultPlace1Builder.buildRoutePlaceDto();
            final Place place1 = defaultPlace1Builder.build();

            final PlaceBuilder defaultPlace2Builder = placeBuilder.defaultPlace2();
            final RoutePlaceDto routePlaceDto2 = defaultPlace2Builder.buildRoutePlaceDto();
            final Place place2 = defaultPlace2Builder.build();

            final List<String> tag = List.of(기본_동선_태그1, 기본_동선_태그2);
            final List<RoutePlaceDto> routePlaceDtos = List.of(routePlaceDto1, routePlaceDto2);

            final RouteAddRequest nullDateRequest = new RouteAddRequest(null, 기본_동선_제목, tag, 기본_동선_이동수단, routePlaceDtos);

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

            final PlaceBuilder defaultPlace1Builder = placeBuilder.defaultPlace1();
            final RoutePlaceDto routePlaceDto1 = defaultPlace1Builder.buildRoutePlaceDto();
            final Place place1 = defaultPlace1Builder.build();

            final PlaceBuilder defaultPlace2Builder = placeBuilder.defaultPlace2();
            final RoutePlaceDto routePlaceDto2 = defaultPlace2Builder.buildRoutePlaceDto();
            final Place place2 = defaultPlace2Builder.build();

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

            final PlaceBuilder defaultPlace1Builder = placeBuilder.defaultPlace1();
            final RoutePlaceDto routePlaceDto1 = defaultPlace1Builder.buildRoutePlaceDto();
            final Place place1 = defaultPlace1Builder.build();

            final PlaceBuilder defaultPlace2Builder = placeBuilder.defaultPlace2();
            final RoutePlaceDto routePlaceDto2 = defaultPlace2Builder.buildRoutePlaceDto();
            final Place place2 = defaultPlace2Builder.build();

            final List<String> tag = List.of(routeTag1.getName(), routeTag2.getName());
            final List<RoutePlaceDto> routePlaceDtos = List.of(routePlaceDto1, routePlaceDto2);

            final RouteAddRequest notExistTitleRequest = new RouteAddRequest(기본_동선_날짜, notExistTitle, tag, 기본_동선_이동수단, routePlaceDtos);

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

            final PlaceBuilder defaultPlace1Builder = placeBuilder.defaultPlace1();
            final RoutePlaceDto routePlaceDto1 = defaultPlace1Builder.buildRoutePlaceDto();
            final Place place1 = defaultPlace1Builder.build();

            final PlaceBuilder defaultPlace2Builder = placeBuilder.defaultPlace2();
            final RoutePlaceDto routePlaceDto2 = defaultPlace2Builder.buildRoutePlaceDto();
            final Place place2 = defaultPlace2Builder.build();

            final List<String> tag = List.of(routeTag1.getName(), routeTag2.getName());
            final List<RoutePlaceDto> routePlaceDtos = List.of(routePlaceDto1, routePlaceDto2);

            final RouteAddRequest notExistTitleRequest = new RouteAddRequest(기본_동선_날짜, overLengthTitle, tag, 기본_동선_이동수단, routePlaceDtos);

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

            final PlaceBuilder defaultPlace1Builder = placeBuilder.defaultPlace1();
            final RoutePlaceDto routePlaceDto1 = defaultPlace1Builder.buildRoutePlaceDto();
            final Place place1 = defaultPlace1Builder.build();

            final PlaceBuilder defaultPlace2Builder = placeBuilder.defaultPlace2();
            final RoutePlaceDto routePlaceDto2 = defaultPlace2Builder.buildRoutePlaceDto();
            final Place place2 = defaultPlace2Builder.build();

            final List<String> tag = List.of(routeTag1.getName(), routeTag2.getName());
            final List<RoutePlaceDto> routePlaceDtos = List.of(routePlaceDto1, routePlaceDto2);

            final RouteAddRequest notExistMoveWaysRequest = new RouteAddRequest(기본_동선_날짜, 기본_동선_제목, tag, notExistMoveWays, routePlaceDtos);

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

            final PlaceBuilder defaultPlace1Builder = placeBuilder.defaultPlace1();
            final RoutePlaceDto routePlaceDto1 = defaultPlace1Builder.buildRoutePlaceDto();
            final Place place1 = defaultPlace1Builder.build();

            final PlaceBuilder defaultPlace2Builder = placeBuilder.defaultPlace2();
            final RoutePlaceDto routePlaceDto2 = defaultPlace2Builder.buildRoutePlaceDto();
            final Place place2 = defaultPlace2Builder.build();

            final List<String> tag = List.of(routeTag1.getName(), routeTag2.getName());
            final List<RoutePlaceDto> overCountRoutePlaceDtos = new ArrayList<>();
            final int maxCount = 30;
            for (int i = 0; i < maxCount + 1; i++) {
                overCountRoutePlaceDtos.add(defaultPlace1Builder.name("place" + i).buildRoutePlaceDto());
            }

            final RouteAddRequest nullDateRequest = new RouteAddRequest(기본_동선_날짜, 기본_동선_제목, tag, 기본_동선_이동수단, overCountRoutePlaceDtos);

            // when
            final ExtractableResponse<Response> response = ADD_ROUTE_REQUEST(accessToken, nullDateRequest);

            // then
            assertSoftly(softly -> {
                softly.assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                softly.assertThat(response.jsonPath().getString("errorMessage")).isEqualTo("동선 장소는 30개 이하여야합니다.");
            });
        }
    }

    @Nested
    @DisplayName("동선 조회 시")
    class FindRoute {

        @Test
        @DisplayName("동선 조회에 성공한다.")
        void success() {
            // given
            final Member member = memberBuilder.defaultMember().build();
            final String accessToken = jwtService.createAccessToken(member.getId());

            final PlaceBuilder defaultPlace1Builder = placeBuilder.defaultPlace1();
            final RoutePlaceDto routePlaceDto1 = defaultPlace1Builder.buildRoutePlaceDto();

            final PlaceBuilder defaultPlace2Builder = placeBuilder.defaultPlace2();
            final RoutePlaceDto routePlaceDto2 = defaultPlace2Builder.buildRoutePlaceDto();

            final List<String> tag = List.of(기본_동선_태그1, 기본_동선_태그2);
            final List<RoutePlaceDto> routePlaceDtos = List.of(routePlaceDto1, routePlaceDto2);

            final RouteAddRequest routeAddRequest = new RouteAddRequest(기본_동선_날짜, 기본_동선_제목, tag, 기본_동선_이동수단, routePlaceDtos);

            final ExtractableResponse<Response> addResponse = ADD_ROUTE_REQUEST(accessToken, routeAddRequest);
            final long targetRouteId = getLocationId(addResponse);

            final Route route = 기본_동선_엔티티(member, List.of(기본_동선_태그1_엔티티(), 기본_동선_태그2_엔티티()), List.of(기본_장소1_엔티티(), 기본_장소2_엔티티()));
            final RouteResponse expected = RouteResponse.from(route);

            // when
            final ExtractableResponse<Response> response = FIND_ROUTE_REQUEST(accessToken, targetRouteId);
            final RouteResponse actual = response.as(RouteResponse.class);

            // then
            assertSoftly(softly -> {
                softly.assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
                softly.assertThat(response.jsonPath().getLong("id")).isEqualTo(targetRouteId);
                softly.assertThat(actual).usingRecursiveComparison().ignoringFields("id", "routePlaces.id").isEqualTo(expected);
            });
        }

        @Test
        @DisplayName("멤버와 동선 ID에 해당하는 동선이 존재하지 않으면 실패한다.")
        void fail_not_exist_member_and_route_id() {
            // given
            final Member member = memberBuilder.defaultMember().build();
            final String accessToken = jwtService.createAccessToken(member.getId());

            final PlaceBuilder defaultPlace1Builder = placeBuilder.defaultPlace1();
            final RoutePlaceDto routePlaceDto1 = defaultPlace1Builder.buildRoutePlaceDto();

            final PlaceBuilder defaultPlace2Builder = placeBuilder.defaultPlace2();
            final RoutePlaceDto routePlaceDto2 = defaultPlace2Builder.buildRoutePlaceDto();

            final List<String> tag = List.of(기본_동선_태그1, 기본_동선_태그2);
            final List<RoutePlaceDto> routePlaceDtos = List.of(routePlaceDto1, routePlaceDto2);

            final RouteAddRequest routeAddRequest = new RouteAddRequest(기본_동선_날짜, 기본_동선_제목, tag, 기본_동선_이동수단, routePlaceDtos);

            final ExtractableResponse<Response> addResponse = ADD_ROUTE_REQUEST(accessToken, routeAddRequest);
            final long targetRouteId = getLocationId(addResponse);

            final Member notExistRouteMember = memberBuilder.defaultMember().build();
            final String notExistRouteMemberAccessToken = jwtService.createAccessToken(notExistRouteMember.getId());
            final Long notExistRouteId = -1L;

            // when
            final ExtractableResponse<Response> response1 = FIND_ROUTE_REQUEST(notExistRouteMemberAccessToken, targetRouteId);
            final ExtractableResponse<Response> response2 = FIND_ROUTE_REQUEST(accessToken, notExistRouteId);
            final ExtractableResponse<Response> response3 = FIND_ROUTE_REQUEST(notExistRouteMemberAccessToken, notExistRouteId);

            // then
            assertSoftly(softly -> {
                softly.assertThat(response1.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
                softly.assertThat(response1.jsonPath().getString("errorMessage")).isEqualTo("멤버 ID와 동선 ID에 해당하는 동선이 존재하지 않습니다.");
                softly.assertThat(response2.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
                softly.assertThat(response2.jsonPath().getString("errorMessage")).isEqualTo("멤버 ID와 동선 ID에 해당하는 동선이 존재하지 않습니다.");
                softly.assertThat(response3.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
                softly.assertThat(response3.jsonPath().getString("errorMessage")).isEqualTo("멤버 ID와 동선 ID에 해당하는 동선이 존재하지 않습니다.");
            });
        }
    }

    @Nested
    @DisplayName("동선 기간 조회 시")
    class FindRouteByPeriod {

        @Test
        @DisplayName("조회할 월의 첫 날짜와 마지막 날짜를 입력하여 월별 기간 조회에 성공한다.")
        void success_monthly() {
            // given
            final Member member = memberBuilder.defaultMember().build();
            final String accessToken = jwtService.createAccessToken(member.getId());

            final RouteAddRequest firstOfMonthAddRequest = new RouteAddRequest(동선_5월_첫날_날짜, 기본_동선_제목, Collections.emptyList(), 기본_동선_이동수단, Collections.emptyList());
            final RouteAddRequest lastOfMonthAddRequest = new RouteAddRequest(동선_5월_마지막날_날짜, 기본_동선_제목, Collections.emptyList(), 기본_동선_이동수단, Collections.emptyList());
            final RouteAddRequest middleOfMonthAddRequest = new RouteAddRequest(동선_5월_둘째주_마지막날_날짜, 기본_동선_제목, Collections.emptyList(), 기본_동선_이동수단, Collections.emptyList());

            ADD_ROUTE_REQUEST(accessToken, firstOfMonthAddRequest);
            ADD_ROUTE_REQUEST(accessToken, lastOfMonthAddRequest);
            ADD_ROUTE_REQUEST(accessToken, middleOfMonthAddRequest);

            final String startDate = 동선_5월_첫날_날짜.toString();
            final String endDate = 동선_5월_마지막날_날짜.toString();

            final Route firstOfMonthDateRoute = 동선_날짜_입력_엔티티(member, 동선_5월_첫날_날짜);
            final Route middleOfMonthDateRoute = 동선_날짜_입력_엔티티(member, 동선_5월_둘째주_마지막날_날짜);
            final Route lastOfMonthDateRoute = 동선_날짜_입력_엔티티(member, 동선_5월_마지막날_날짜);

            final RoutesResponse expected = RoutesResponse.from(
                    List.of(firstOfMonthDateRoute, middleOfMonthDateRoute, lastOfMonthDateRoute)
            );

            // when
            final ExtractableResponse<Response> response = FIND_ROUTE_BY_PERIOD_REQUEST(accessToken, startDate, endDate);
            final RoutesResponse actual = response.as(RoutesResponse.class);

            // then
            assertSoftly(softly -> {
                softly.assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
                softly.assertThat(actual).usingRecursiveComparison().ignoringFields("id", "routes.id").isEqualTo(expected);
            });
        }

        @Test
        @DisplayName("조회할 주의 첫 날짜와 마지막 날짜를 입력하여 주간 기간 조회에 성공한다.")
        void success_weekly() {
            // given
            final Member member = memberBuilder.defaultMember().build();
            final String accessToken = jwtService.createAccessToken(member.getId());

            final RouteAddRequest firstOfWeekAddRequest = new RouteAddRequest(동선_5월_둘째주_첫날_날짜, 기본_동선_제목, Collections.emptyList(), 기본_동선_이동수단, Collections.emptyList());
            final RouteAddRequest lastOfWeekAddRequest = new RouteAddRequest(동선_5월_둘째주_마지막날_날짜, 기본_동선_제목, Collections.emptyList(), 기본_동선_이동수단, Collections.emptyList());
            final RouteAddRequest middleOfWeekAddRequest = new RouteAddRequest(동선_5월_둘째주_중간_날짜, 기본_동선_제목, Collections.emptyList(), 기본_동선_이동수단, Collections.emptyList());

            ADD_ROUTE_REQUEST(accessToken, firstOfWeekAddRequest);
            ADD_ROUTE_REQUEST(accessToken, lastOfWeekAddRequest);
            ADD_ROUTE_REQUEST(accessToken, middleOfWeekAddRequest);

            final String startDate = 동선_5월_둘째주_첫날_날짜.toString();
            final String endDate = 동선_5월_둘째주_마지막날_날짜.toString();

            final Route firstOfWeekDateRoute = 동선_날짜_입력_엔티티(member, 동선_5월_둘째주_첫날_날짜);
            final Route middleOfWeekDateRoute = 동선_날짜_입력_엔티티(member, 동선_5월_둘째주_중간_날짜);
            final Route lastOfWeekDateRoute = 동선_날짜_입력_엔티티(member, 동선_5월_둘째주_마지막날_날짜);

            final RoutesResponse expected = RoutesResponse.from(
                    List.of(firstOfWeekDateRoute, middleOfWeekDateRoute, lastOfWeekDateRoute)
            );

            // when
            final ExtractableResponse<Response> response = FIND_ROUTE_BY_PERIOD_REQUEST(accessToken, startDate, endDate);
            final RoutesResponse actual = response.as(RoutesResponse.class);

            // then
            assertSoftly(softly -> {
                softly.assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
                softly.assertThat(actual).usingRecursiveComparison().ignoringFields("id", "routes.id").isEqualTo(expected);
            });
        }
    }

    @Nested
    @DisplayName("동선 삭제 시")
    class DeleteRoute {

        @Test
        @DisplayName("동선 삭제에 성공한다.")
        void success() {
            // given
            final Member member = memberBuilder.defaultMember().build();
            final String accessToken = jwtService.createAccessToken(member.getId());

            final PlaceBuilder defaultPlace1Builder = placeBuilder.defaultPlace1();
            final RoutePlaceDto routePlaceDto1 = defaultPlace1Builder.buildRoutePlaceDto();
            final Place place1 = defaultPlace1Builder.build();

            final PlaceBuilder defaultPlace2Builder = placeBuilder.defaultPlace2();
            final RoutePlaceDto routePlaceDto2 = defaultPlace2Builder.buildRoutePlaceDto();
            final Place place2 = defaultPlace2Builder.build();

            final List<String> tag = List.of(기본_동선_태그1, 기본_동선_태그2);
            final List<RoutePlaceDto> routePlaceDtos = List.of(routePlaceDto1, routePlaceDto2);

            final RouteAddRequest routeAddRequest = new RouteAddRequest(기본_동선_날짜, 기본_동선_제목, tag, 기본_동선_이동수단, routePlaceDtos);
            final ExtractableResponse<Response> addResponse = ADD_ROUTE_REQUEST(accessToken, routeAddRequest);
            final long routeId = getLocationId(addResponse);

            // when
            final ExtractableResponse<Response> response = DELETE_ROUTE_REQUEST(accessToken, routeId);
            final ExtractableResponse<Response> findResponse = FIND_ROUTE_REQUEST(accessToken, routeId);

            // then
            assertSoftly(softly -> {
                softly.assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
                softly.assertThat(findResponse.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
            });
        }

        @Test
        @DisplayName("멤버 ID와 동선 ID에 해당하는 동선이 존재하지 않으면 실패한다.")
        void fail_not_exist_route() {
            // given
            final Member member = memberBuilder.defaultMember().build();
            final String accessToken = jwtService.createAccessToken(member.getId());

            final PlaceBuilder defaultPlace1Builder = placeBuilder.defaultPlace1();
            final RoutePlaceDto routePlaceDto1 = defaultPlace1Builder.buildRoutePlaceDto();
            final Place place1 = defaultPlace1Builder.build();

            final PlaceBuilder defaultPlace2Builder = placeBuilder.defaultPlace2();
            final RoutePlaceDto routePlaceDto2 = defaultPlace2Builder.buildRoutePlaceDto();
            final Place place2 = defaultPlace2Builder.build();

            final List<String> tag = List.of(기본_동선_태그1, 기본_동선_태그2);
            final List<RoutePlaceDto> routePlaceDtos = List.of(routePlaceDto1, routePlaceDto2);

            final RouteAddRequest routeAddRequest = new RouteAddRequest(기본_동선_날짜, 기본_동선_제목, tag, 기본_동선_이동수단, routePlaceDtos);
            final ExtractableResponse<Response> addResponse = ADD_ROUTE_REQUEST(accessToken, routeAddRequest);
            final long routeId = getLocationId(addResponse);

            final Member notExistRouteMember = memberBuilder.defaultMember().build();
            final String notExistRouteMemberAccessToken = jwtService.createAccessToken(notExistRouteMember.getId());
            final Long notExistRouteId = -1L;

            // when
            final ExtractableResponse<Response> response1 = DELETE_ROUTE_REQUEST(notExistRouteMemberAccessToken, notExistRouteId);
            final ExtractableResponse<Response> response2 = DELETE_ROUTE_REQUEST(accessToken, notExistRouteId);
            final ExtractableResponse<Response> response3 = DELETE_ROUTE_REQUEST(notExistRouteMemberAccessToken, routeId);

            // then
            assertSoftly(softly -> {
                softly.assertThat(response1.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
                softly.assertThat(response1.jsonPath().getString("errorMessage")).isEqualTo("멤버 ID와 동선 ID에 해당하는 동선이 존재하지 않습니다.");
                softly.assertThat(response2.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
                softly.assertThat(response2.jsonPath().getString("errorMessage")).isEqualTo("멤버 ID와 동선 ID에 해당하는 동선이 존재하지 않습니다.");
                softly.assertThat(response3.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
                softly.assertThat(response3.jsonPath().getString("errorMessage")).isEqualTo("멤버 ID와 동선 ID에 해당하는 동선이 존재하지 않습니다.");
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

    private static ExtractableResponse<Response> FIND_ROUTE_REQUEST(final String accessToken, final Long routeId) {
        return RestAssured.given().log().all()
                .header(HttpHeaders.AUTHORIZATION, JWT_PREFIX + accessToken)
                .when().log().all()
                .get("/routes/{route-id}", routeId)
                .then().log().all()
                .extract();
    }

    private long getLocationId(final ExtractableResponse<Response> addResponse) {
        final String location = addResponse.header("Location");
        final long targetRouteId = Long.parseLong(location.substring(location.lastIndexOf("/") + 1));
        return targetRouteId;
    }

    private static ExtractableResponse<Response> FIND_ROUTE_BY_PERIOD_REQUEST(final String accessToken, final String startDate, final String endDate) {
        return RestAssured.given().log().all()
                .header(HttpHeaders.AUTHORIZATION, JWT_PREFIX + accessToken)
                .when().log().all()
                .get("/routes?start_date=" + startDate + "&end_date=" + endDate)
                .then().log().all()
                .extract();
    }

    private static ExtractableResponse<Response> DELETE_ROUTE_REQUEST(final String accessToken, final Long routeId) {
        return RestAssured.given().log().all()
                .header(HttpHeaders.AUTHORIZATION, JWT_PREFIX + accessToken)
                .when().log().all()
                .delete("/routes/{route-id}", routeId)
                .then().log().all()
                .extract();
    }
}
