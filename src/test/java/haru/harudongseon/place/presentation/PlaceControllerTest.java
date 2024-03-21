package haru.harudongseon.place.presentation;

import static haru.harudongseon.common.fixtures.PlaceFixtures.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.math.BigDecimal;
import java.util.stream.Stream;

import haru.harudongseon.common.E2ETest;
import haru.harudongseon.common.builder.MemberBuilder;
import haru.harudongseon.common.builder.PlaceBuilder;
import haru.harudongseon.member.domain.Member;
import haru.harudongseon.place.application.dto.PlaceAddRequest;
import haru.harudongseon.place.domain.Place;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

class PlaceControllerTest extends E2ETest {

    @Autowired
    private MemberBuilder memberBuilder;

    @Autowired
    private PlaceBuilder placeBuilder;

    @Nested
    @DisplayName("장소 추가 시 ")
    class AddPlace {

        @Test
        @DisplayName("존재하지 않는 장소면, 장소를 추가한다.")
        void success_not_exist_place_add_place() {
            // given
            final Member member = memberBuilder.defaultMember().build();
            final String accessToken = jwtService.createAccessToken(member.getId());
            final PlaceAddRequest notExistPlaceRequest = 기본_장소_추가_REQUEST;

            // when
            final ExtractableResponse<Response> response = ADD_PLACE_REQUEST(accessToken, notExistPlaceRequest);

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        }

        @Test
        @DisplayName("이미 장소가 존재한다면, 장소의 선택 횟수를 1 증가시킨다.")
        void success_exist_place_select_count_1_increase() {
            // given
            final Member member = memberBuilder.defaultMember().build();
            final String accessToken = jwtService.createAccessToken(member.getId());
            final Place place = placeBuilder.defaultPlace().build();
            final PlaceAddRequest existPlaceRequest = placeBuilder.buildPlaceAddRequest(place);

            // when
            final ExtractableResponse<Response> response = ADD_PLACE_REQUEST(accessToken, existPlaceRequest);

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        }

        @ParameterizedTest
        @MethodSource(value = "failNullRequests")
        @DisplayName("장소 정보 중에 하나라도 값이 없으면 장소 추가에 실패한다.")
        void fail_place_request_field_null(final PlaceAddRequest request) {
            // given
            final Member member = memberBuilder.defaultMember().build();
            final String accessToken = jwtService.createAccessToken(member.getId());

            // when
            final ExtractableResponse<Response> response = ADD_PLACE_REQUEST(accessToken, request);

            // then
            assertSoftly(softly -> {
                softly.assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                softly.assertThat(response.jsonPath().getString("errorMessage")).contains("공백일 수 없습니다.");
            });
        }

        private static Stream<Arguments> failNullRequests() {
            final Long providerPlaceId = 기본_외부_공급자_ID;
            final String name = 기본_장소_이름;
            final String category = 기본_장소_카테고리;
            final BigDecimal latitude = 기본_장소_위도;
            final BigDecimal longitude = 기본_장소_경도;
            final String addressName = 기본_장소_주소_이름;

            return Stream.of(
                    Arguments.arguments(new PlaceAddRequest(null, name, category, latitude, longitude, addressName)),
                    Arguments.arguments(new PlaceAddRequest(providerPlaceId, null, category, latitude, longitude, addressName)),
                    Arguments.arguments(new PlaceAddRequest(providerPlaceId, name, null, latitude, longitude, addressName)),
                    Arguments.arguments(new PlaceAddRequest(providerPlaceId, name, category, null, longitude, addressName)),
                    Arguments.arguments(new PlaceAddRequest(providerPlaceId, name, category, latitude, null, addressName)),
                    Arguments.arguments(new PlaceAddRequest(providerPlaceId, name, category, latitude, longitude, null))
            );
        }

        @ParameterizedTest
        @MethodSource(value = "failCoordinateWrongValidationRequests")
        @DisplayName("장소의 위도, 경도가 올바른 형식이 아니면 장소 추가에 실패한다.")
        void fail_place_coordinates_wrong_validation(final PlaceAddRequest request) {
            // given
            final Member member = memberBuilder.defaultMember().build();
            final String accessToken = jwtService.createAccessToken(member.getId());

            // when
            final ExtractableResponse<Response> response = ADD_PLACE_REQUEST(accessToken, request);

            // then
            assertSoftly(softly -> {
                softly.assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                softly.assertThat(response.jsonPath().getString("errorMessage")).contains("10진수 9자, 소수점 6자 이내 여야합니다.");
            });
        }

        private static Stream<Arguments> failCoordinateWrongValidationRequests() {
            final Long providerPlaceId = 기본_외부_공급자_ID;
            final String name = 기본_장소_이름;
            final String category = 기본_장소_카테고리;
            final BigDecimal latitude = 기본_장소_위도;
            final BigDecimal longitude = 기본_장소_경도;
            final String addressName = 기본_장소_주소_이름;

            return Stream.of(
                    Arguments.arguments(new PlaceAddRequest(providerPlaceId, name, category, new BigDecimal(1234567890.0), longitude, addressName)),
                    Arguments.arguments(new PlaceAddRequest(providerPlaceId, name, category, latitude, new BigDecimal(1234567890.0), addressName)),
                    Arguments.arguments(new PlaceAddRequest(providerPlaceId, name, category, new BigDecimal(1.1234567), longitude, addressName)),
                    Arguments.arguments(new PlaceAddRequest(providerPlaceId, name, category, latitude, new BigDecimal(1.1234567), addressName))
            );
        }
    }

    private static ExtractableResponse<Response> ADD_PLACE_REQUEST(final String accessToken, final PlaceAddRequest request) {
        return RestAssured.given().log().all()
                .header(HttpHeaders.AUTHORIZATION, JWT_PREFIX + accessToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .when().log().all()
                .post("/places")
                .then().log().all()
                .extract();
    }
}
