package haru.harudongseon.likeplace.presentation;

import static haru.harudongseon.common.fixtures.LikePlaceFixtures.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import haru.harudongseon.common.E2ETest;
import haru.harudongseon.common.builder.LikePlaceBuilder;
import haru.harudongseon.common.builder.MemberBuilder;
import haru.harudongseon.likeplace.application.dto.LikePlaceAddRequest;
import haru.harudongseon.likeplace.application.dto.LikePlaceResponse;
import haru.harudongseon.likeplace.application.dto.LikePlacesResponse;
import haru.harudongseon.likeplace.application.dto.RecentLikePlacesResponse;
import haru.harudongseon.likeplace.domain.LikePlace;
import haru.harudongseon.likeplace.domain.LikePlaceRepository;
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

class LikePlaceControllerTest extends E2ETest {

    @Autowired
    private MemberBuilder memberBuilder;

    @Autowired
    private LikePlaceBuilder likePlaceBuilder;

    @Autowired
    private LikePlaceRepository likePlaceRepository;

    @Nested
    @DisplayName("보관 장소 추가 시")
    class AddLikePlace {

        @Test
        @DisplayName("보관 장소 추가에 성공한다.")
        void success() {
            // given
            final Member member = memberBuilder.defaultMember().build();
            final String accessToken = jwtService.createAccessToken(member.getId());
            final LikePlaceAddRequest request = likePlaceBuilder.defaultLikePlace(null).buildAddRequest();

            // when
            final ExtractableResponse<Response> response = ADD_LIKE_PLACE_REQUEST(accessToken, request);

            // then
            assertSoftly(softly -> {
                softly.assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
                softly.assertThat(response.header("Location")).contains("/like-places/");
            });
        }

        @Test
        @DisplayName("보관 장소의 사진이 중복된다면, 추가에 실패한다.")
        void fail_duplicate_photo() {
            // given
            final Member member = memberBuilder.defaultMember().build();
            final String accessToken = jwtService.createAccessToken(member.getId());
            final LikePlaceAddRequest duplicatePhotoAddRequest = likePlaceBuilder.defaultLikePlace(member)
                    .photoReferences(List.of(기본_보관_장소_사진_참조1, 기본_보관_장소_사진_참조1, 기본_보관_장소_사진_참조2))
                    .buildAddRequest();


            // when
            final ExtractableResponse<Response> response = ADD_LIKE_PLACE_REQUEST(accessToken, duplicatePhotoAddRequest);

            // then
            assertSoftly(softly -> {
                assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                assertThat(response.jsonPath().getString("errorMessage")).isEqualTo("보관 장소에 중복되는 사진이 존재합니다.");
            });
        }

        @ParameterizedTest
        @ValueSource(strings = {"", " "})
        @DisplayName("보관 장소의 외부 공급자 장소 ID가 없다면, 추가에 실패한다.")
        void fail_not_exist_provider_place_id(final String notExistProviderPlaceId) {
            // given
            final Member member = memberBuilder.defaultMember().build();
            final String accessToken = jwtService.createAccessToken(member.getId());
            final LikePlaceAddRequest notExistProviderIdRequest = likePlaceBuilder.defaultLikePlace(null)
                    .providerPlaceId(notExistProviderPlaceId).buildAddRequest();

            // when
            final ExtractableResponse<Response> response = ADD_LIKE_PLACE_REQUEST(accessToken, notExistProviderIdRequest);

            // then
            assertSoftly(softly -> {
                assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                assertThat(response.jsonPath().getString("errorMessage")).isEqualTo("외부 공급자의 PlaceId는 공백일 수 없습니다.");
            });
        }

        @ParameterizedTest
        @ValueSource(strings = {"", " "})
        @DisplayName("보관 장소의 이름이 없다면, 추가에 실패한다.")
        void fail_not_exist_name(final String notExistName) {
            // given
            final Member member = memberBuilder.defaultMember().build();
            final String accessToken = jwtService.createAccessToken(member.getId());
            final LikePlaceAddRequest notExistNameRequest = likePlaceBuilder.defaultLikePlace(null)
                    .name(notExistName).buildAddRequest();

            // when
            final ExtractableResponse<Response> response = ADD_LIKE_PLACE_REQUEST(accessToken, notExistNameRequest);

            // then
            assertSoftly(softly -> {
                assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                assertThat(response.jsonPath().getString("errorMessage")).isEqualTo("이름은 공백일 수 없습니다.");
            });
        }

        @ParameterizedTest
        @ValueSource(strings = {"", " "})
        @DisplayName("보관 장소의 카테고리가 없다면, 추가에 실패한다.")
        void fail_not_exist_category(final String notExistCategory) {
            // given
            final Member member = memberBuilder.defaultMember().build();
            final String accessToken = jwtService.createAccessToken(member.getId());
            final LikePlaceAddRequest notExistNameRequest = likePlaceBuilder.defaultLikePlace(null)
                    .category(notExistCategory).buildAddRequest();

            // when
            final ExtractableResponse<Response> response = ADD_LIKE_PLACE_REQUEST(accessToken, notExistNameRequest);

            // then
            assertSoftly(softly -> {
                assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                assertThat(response.jsonPath().getString("errorMessage")).isEqualTo("카테고리는 공백일 수 없습니다.");
            });
        }


        @Test
        @DisplayName("보관 장소의 사진 참조는 비어있더라도 추가에 성공한다.")
        void success_empty_photo_references() {
            // given
            final Member member = memberBuilder.defaultMember().build();
            final String accessToken = jwtService.createAccessToken(member.getId());
            final LikePlaceAddRequest notExistNameRequest = likePlaceBuilder.defaultLikePlace(null)
                    .photoReferences(Collections.emptyList()).buildAddRequest();

            // when
            final ExtractableResponse<Response> response = ADD_LIKE_PLACE_REQUEST(accessToken, notExistNameRequest);

            // then
            assertSoftly(softly -> {
                softly.assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
                softly.assertThat(response.header("Location")).contains("/like-places/");
            });
        }

        @ParameterizedTest
        @ValueSource(strings = {"", " "})
        @DisplayName("보관 장소의 위도가 잘못된 형식의 위도라면, 추가에 실패한다.")
        void fail_wrong_type_latitude(final String wrongTypeLatitude) {
            // given
            final Member member = memberBuilder.defaultMember().build();
            final String accessToken = jwtService.createAccessToken(member.getId());

            // when
            final ExtractableResponse<Response> response = WRONG_TYPE_LATITUDE_ADD_LIKE_PLACE_REQUEST(accessToken, wrongTypeLatitude);

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        }

        @ParameterizedTest
        @ValueSource(strings = {"1234567890.1", "0.1234567", "1234567890.1234567"})
        @DisplayName("보관 장소의 위도가 10진수 9자, 소수점 6자 이내가 아니라면, 추가에 실패한다.")
        void fail_invalid_latitude(final String invalidLatitude) {
            // given
            final Member member = memberBuilder.defaultMember().build();
            final String accessToken = jwtService.createAccessToken(member.getId());
            final LikePlaceAddRequest invalidLatitudeRequest = likePlaceBuilder.defaultLikePlace(null).latitude(new BigDecimal(invalidLatitude)).buildAddRequest();

            // when
            final ExtractableResponse<Response> response = ADD_LIKE_PLACE_REQUEST(accessToken, invalidLatitudeRequest);

            // then
            assertSoftly(softly -> {
                softly.assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                softly.assertThat(response.jsonPath().getString("errorMessage")).isEqualTo("위도는 10진수 9자, 소수점 6자 이내 여야합니다.");
            });
        }

        @ParameterizedTest
        @ValueSource(strings = {"", " "})
        @DisplayName("보관 장소의 경도가 없다면, 추가에 실패한다.")
        void fail_wrong_type_longitude(final String wrongTypeLongitude) {
            // given
            final Member member = memberBuilder.defaultMember().build();
            final String accessToken = jwtService.createAccessToken(member.getId());

            // when
            final ExtractableResponse<Response> response = WRONG_TYPE_LONGITUDE_ADD_LIKE_PLACE_REQUEST(accessToken, wrongTypeLongitude);

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        }


        @ParameterizedTest
        @ValueSource(strings = {"1234567890.1", "0.1234567", "1234567890.1234567"})
        @DisplayName("보관 장소의 경도가 10진수 9자, 소수점 6자 이내가 아니라면, 추가에 실패한다.")
        void fail_invalid_longitude(final String invalidLongitude) {
            // given
            final Member member = memberBuilder.defaultMember().build();
            final String accessToken = jwtService.createAccessToken(member.getId());
            final LikePlaceAddRequest invalidLongitudeRequest = likePlaceBuilder.defaultLikePlace(null).longitude(new BigDecimal(invalidLongitude)).buildAddRequest();

            // when
            final ExtractableResponse<Response> response = ADD_LIKE_PLACE_REQUEST(accessToken, invalidLongitudeRequest);

            // then
            assertSoftly(softly -> {
                softly.assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                softly.assertThat(response.jsonPath().getString("errorMessage")).isEqualTo("경도는 10진수 9자, 소수점 6자 이내 여야합니다.");
            });
        }

        @ParameterizedTest
        @ValueSource(strings = {"", " "})
        @DisplayName("보관 장소의 영업 시간이 없다면, 추가에 실패한다.")
        void fail_not_exist_opening_hours(final String notExistOpeningHours) {
            // given
            final Member member = memberBuilder.defaultMember().build();
            final String accessToken = jwtService.createAccessToken(member.getId());
            final LikePlaceAddRequest notExistNameRequest = likePlaceBuilder.defaultLikePlace(null)
                    .openingHours(notExistOpeningHours).buildAddRequest();

            // when
            final ExtractableResponse<Response> response = ADD_LIKE_PLACE_REQUEST(accessToken, notExistNameRequest);

            // then
            assertSoftly(softly -> {
                assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                assertThat(response.jsonPath().getString("errorMessage")).isEqualTo("영업 시간은 공백일 수 없습니다. 정보가 없다면 NONE을 입력하세요.");
            });
        }

        @ParameterizedTest
        @ValueSource(strings = {"", " "})
        @DisplayName("보관 장소의 주소 이름이 없다면, 추가에 실패한다.")
        void fail_not_exist_address_name(final String notExistAddressName) {
            // given
            final Member member = memberBuilder.defaultMember().build();
            final String accessToken = jwtService.createAccessToken(member.getId());
            final LikePlaceAddRequest notExistNameRequest = likePlaceBuilder.defaultLikePlace(null)
                    .addressName(notExistAddressName).buildAddRequest();

            // when
            final ExtractableResponse<Response> response = ADD_LIKE_PLACE_REQUEST(accessToken, notExistNameRequest);

            // then
            assertSoftly(softly -> {
                assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                assertThat(response.jsonPath().getString("errorMessage")).isEqualTo("주소 이름은 공백일 수 없습니다.");
            });
        }

        @ParameterizedTest
        @ValueSource(strings = {"", " "})
        @DisplayName("보관 장소의 전화번호가 없다면, 추가에 실패한다.")
        void fail_not_exist_phone_number(final String notExistPhoneNumber) {
            // given
            final Member member = memberBuilder.defaultMember().build();
            final String accessToken = jwtService.createAccessToken(member.getId());
            final LikePlaceAddRequest notExistNameRequest = likePlaceBuilder.defaultLikePlace(null)
                    .phoneNumber(notExistPhoneNumber).buildAddRequest();

            // when
            final ExtractableResponse<Response> response = ADD_LIKE_PLACE_REQUEST(accessToken, notExistNameRequest);

            // then
            assertSoftly(softly -> {
                assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                assertThat(response.jsonPath().getString("errorMessage")).isEqualTo("전화번호는 공백일 수 없습니다. 정보가 없다면 NONE을 입력하세요.");
            });
        }

        @ParameterizedTest
        @ValueSource(strings = {"", " "})
        @DisplayName("보관 장소의 웹사이트가 없다면, 추가에 실패한다.")
        void fail_not_exist_website(final String notExistWebsite) {
            // given
            final Member member = memberBuilder.defaultMember().build();
            final String accessToken = jwtService.createAccessToken(member.getId());
            final LikePlaceAddRequest notExistNameRequest = likePlaceBuilder.defaultLikePlace(null)
                    .website(notExistWebsite).buildAddRequest();

            // when
            final ExtractableResponse<Response> response = ADD_LIKE_PLACE_REQUEST(accessToken, notExistNameRequest);

            // then
            assertSoftly(softly -> {
                assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                assertThat(response.jsonPath().getString("errorMessage")).isEqualTo("웹사이트는 공백일 수 없습니다. 정보가 없다면 NONE을 입력하세요.");
            });
        }

        @ParameterizedTest
        @ValueSource(strings = {"", " "})
        @DisplayName("보관 장소의 구글 검색 URL이 없다면, 추가에 실패한다.")
        void fail_not_exist_url(final String notExistUrl) {
            // given
            final Member member = memberBuilder.defaultMember().build();
            final String accessToken = jwtService.createAccessToken(member.getId());
            final LikePlaceAddRequest notExistNameRequest = likePlaceBuilder.defaultLikePlace(null)
                    .googleMapsUri(notExistUrl).buildAddRequest();

            // when
            final ExtractableResponse<Response> response = ADD_LIKE_PLACE_REQUEST(accessToken, notExistNameRequest);

            // then
            assertSoftly(softly -> {
                assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                assertThat(response.jsonPath().getString("errorMessage")).isEqualTo("장소 구글 검색 URL은 공백일 수 없습니다.");
            });
        }

        @ParameterizedTest
        @ValueSource(strings = {"", " "})
        @DisplayName("보관 장소의 예약 가능 여부가 없다면, 추가에 실패한다.")
        void fail_not_exist_reservable(final String notExistReservable) {
            // given
            final Member member = memberBuilder.defaultMember().build();
            final String accessToken = jwtService.createAccessToken(member.getId());
            final LikePlaceAddRequest notExistReservableRequest = new LikePlaceAddRequest(
                    기본_외부_공급자_ID, 기본_보관_장소_이름, 기본_보관_장소_카테고리, 기본_보관_장소_사진_참조,
                    기본_보관_장소_위도, 기본_보관_장소_경도, 기본_보관_장소_영업_시간,
                    기본_보관_장소_주소_이름, 기본_보관_장소_전화번호, 기본_보관_장소_웹사이트, 기본_보관_장소_구글_맵_URL,
                    notExistReservable, 기본_보관_장소_포장_가능_여부.name(), 기본_보관_장소_주차_가능_여부.name()
            );

            // when
            final ExtractableResponse<Response> response = ADD_LIKE_PLACE_REQUEST(accessToken, notExistReservableRequest);

            // then
            assertSoftly(softly -> {
                assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                assertThat(response.jsonPath().getString("errorMessage")).isEqualTo("예약 가능 여부는 공백일 수 없습니다. 정보가 없다면 NONE을 입력하세요.");
            });
        }

        @ParameterizedTest
        @ValueSource(strings = {"", " "})
        @DisplayName("보관 장소의 포장 가능 여부가 없다면, 추가에 실패한다.")
        void fail_not_exist_takeout_available(final String notExistTakeoutAvailable) {
            // given
            final Member member = memberBuilder.defaultMember().build();
            final String accessToken = jwtService.createAccessToken(member.getId());
            final LikePlaceAddRequest notExistTakeoutAvailableRequest = new LikePlaceAddRequest(
                    기본_외부_공급자_ID, 기본_보관_장소_이름, 기본_보관_장소_카테고리, 기본_보관_장소_사진_참조,
                    기본_보관_장소_위도, 기본_보관_장소_경도, 기본_보관_장소_영업_시간,
                    기본_보관_장소_주소_이름, 기본_보관_장소_전화번호, 기본_보관_장소_웹사이트, 기본_보관_장소_구글_맵_URL,
                    기본_보관_장소_예약_가능_여부.name(), notExistTakeoutAvailable, 기본_보관_장소_주차_가능_여부.name()
            );

            // when
            final ExtractableResponse<Response> response = ADD_LIKE_PLACE_REQUEST(accessToken, notExistTakeoutAvailableRequest);

            // then
            assertSoftly(softly -> {
                assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                assertThat(response.jsonPath().getString("errorMessage")).isEqualTo("포장 가능 여부는 공백일 수 없습니다. 정보가 없다면 NONE을 입력하세요.");
            });
        }

        @ParameterizedTest
        @ValueSource(strings = {"", " "})
        @DisplayName("보관 장소의 주차 가능 여부가 없다면, 추가에 실패한다.")
        void fail_not_exist_parking_available(final String notExistParkingAvailable) {
            // given
            final Member member = memberBuilder.defaultMember().build();
            final String accessToken = jwtService.createAccessToken(member.getId());
            final LikePlaceAddRequest notExistParkingAvailableRequest = new LikePlaceAddRequest(
                    기본_외부_공급자_ID, 기본_보관_장소_이름, 기본_보관_장소_카테고리, 기본_보관_장소_사진_참조,
                    기본_보관_장소_위도, 기본_보관_장소_경도, 기본_보관_장소_영업_시간,
                    기본_보관_장소_주소_이름, 기본_보관_장소_전화번호, 기본_보관_장소_웹사이트, 기본_보관_장소_구글_맵_URL,
                    기본_보관_장소_예약_가능_여부.name(), 기본_보관_장소_포장_가능_여부.name(), notExistParkingAvailable
            );

            // when
            final ExtractableResponse<Response> response = ADD_LIKE_PLACE_REQUEST(accessToken, notExistParkingAvailableRequest);

            // then
            assertSoftly(softly -> {
                assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                assertThat(response.jsonPath().getString("errorMessage")).isEqualTo("주차 가능 여부는 공백일 수 없습니다. 정보가 없다면 NONE을 입력하세요.");
            });
        }

        @Test
        @DisplayName("존재하지 않는 멤버의 요청이면 추가에 실패한다.")
        void fail_not_exist_member_request() {
            // given
            final Long notExistMemberId = -1L;
            final String accessToken = jwtService.createAccessToken(notExistMemberId);
            final LikePlaceAddRequest request = likePlaceBuilder.defaultLikePlace(null).buildAddRequest();

            // when
            final ExtractableResponse<Response> response = ADD_LIKE_PLACE_REQUEST(accessToken, request);

            // then
            assertSoftly(softly -> {
                softly.assertThat(response.statusCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
                softly.assertThat(response.jsonPath().getString("errorMessage")).isEqualTo("인증에 실패했습니다. 정확한 에러는 서버 로그를 확인해주세요.");
            });
        }
    }

    @Nested
    @DisplayName("보관 장소 조회 시")
    class FindLikePlace {

        @Test
        @DisplayName("보관 장소 ID에 해당하는 조회에 성공한다.")
        void success() {
            // given
            final Member member = memberBuilder.defaultMember().build();
            final String accessToken = jwtService.createAccessToken(member.getId());
            final LikePlaceBuilder builder = likePlaceBuilder.defaultLikePlace(member);
            final LikePlaceAddRequest addRequest = builder.buildAddRequest();

            final ExtractableResponse<Response> addResponse = ADD_LIKE_PLACE_REQUEST(accessToken, addRequest);
            final String location = addResponse.header("Location");
            final long targetLikePlaceId = Long.parseLong(location.substring(location.lastIndexOf("/") + 1));

            final LikePlaceResponse expected = LikePlaceResponse.from(addRequest.toEntity(member));

            // when
            final ExtractableResponse<Response> response = FIND_LIKE_PLACE_REQUEST(accessToken, targetLikePlaceId);
            final LikePlaceResponse actual = response.as(LikePlaceResponse.class);

            // then
            assertSoftly(softly -> {
                softly.assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
                softly.assertThat(response.jsonPath().getLong("id")).isEqualTo(targetLikePlaceId);
                softly.assertThat(actual).usingRecursiveComparison().ignoringFields("id").isEqualTo(expected);
            });
        }

        @Test
        @DisplayName("보관 장소 ID에 해당하는 보관 장소가 존재하지 않으면 조회에 실패한다.")
        void fail_not_exits_like_place() {
            // given
            final Member member = memberBuilder.defaultMember().build();
            final String accessToken = jwtService.createAccessToken(member.getId());
            final LikePlaceAddRequest addRequest = likePlaceBuilder.defaultLikePlace(member).buildAddRequest();
            ADD_LIKE_PLACE_REQUEST(accessToken, addRequest);

            final Long notExistLikePlaceId = -1L;

            // when
            final ExtractableResponse<Response> response = FIND_LIKE_PLACE_REQUEST(accessToken, notExistLikePlaceId);

            // then
            assertSoftly(softly -> {
                softly.assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
                softly.assertThat(response.jsonPath().getString("errorMessage")).isEqualTo("해당하는 보관 장소를 찾을 수 없습니다.");
            });
        }
    }

    @Nested
    @DisplayName("최근 보관 장소 조회 시 ")
    class FindRecentLikePlace {

        @Test
        @DisplayName("최근에 저장한 순서로 최대 3개의 보관 장소가 조회된다.")
        void success() {
            // given
            final Member member = memberBuilder.defaultMember().build();
            final String accessToken = jwtService.createAccessToken(member.getId());

            final LikePlaceAddRequest addRequest1 = likePlaceBuilder.defaultLikePlace(member)
                    .photoReferences(List.of("photoReference1")).buildAddRequest();
            final LikePlaceAddRequest addRequest2 = likePlaceBuilder.defaultLikePlace(member)
                    .photoReferences(List.of("photoReference2")).buildAddRequest();
            final LikePlaceAddRequest addRequest3 = likePlaceBuilder.defaultLikePlace(member)
                    .photoReferences(List.of("photoReference3")).buildAddRequest();
            final LikePlaceAddRequest addRequest4 = likePlaceBuilder.defaultLikePlace(member)
                    .photoReferences(List.of("photoReference4")).buildAddRequest();

            final ExtractableResponse<Response> addResponse1 = ADD_LIKE_PLACE_REQUEST(accessToken, addRequest1);
            final ExtractableResponse<Response> addResponse2 = ADD_LIKE_PLACE_REQUEST(accessToken, addRequest2);
            final ExtractableResponse<Response> addResponse3 = ADD_LIKE_PLACE_REQUEST(accessToken, addRequest3);
            final ExtractableResponse<Response> addResponse4 = ADD_LIKE_PLACE_REQUEST(accessToken, addRequest4);

            final Long likePlace2Id = getLikePlaceId(addResponse2);
            final Long likePlace3Id = getLikePlaceId(addResponse3);
            final Long likePlace4Id = getLikePlaceId(addResponse4);

            final LikePlace likePlace2 = addRequest2.toEntity(member);
            final LikePlace likePlace3 = addRequest3.toEntity(member);
            final LikePlace likePlace4 = addRequest4.toEntity(member);

            final List<LikePlaceResponse> expected = List.of(LikePlaceResponse.from(likePlace4), LikePlaceResponse.from(likePlace3), LikePlaceResponse.from(likePlace2));

            // when
            final ExtractableResponse<Response> response = FIND_RECENT_LIKE_PLACE_REQUEST(accessToken);
            final List<LikePlaceResponse> actual = response.as(RecentLikePlacesResponse.class).getLikePlaces();

            // then
            assertSoftly(softly -> {
                softly.assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
                softly.assertThat(actual.get(0).getId()).isEqualTo(likePlace4Id);
                softly.assertThat(actual.get(1).getId()).isEqualTo(likePlace3Id);
                softly.assertThat(actual.get(2).getId()).isEqualTo(likePlace2Id);
                softly.assertThat(actual).usingRecursiveFieldByFieldElementComparatorIgnoringFields("id").isEqualTo(expected);
            });
        }

        @Test
        @DisplayName("추가한 보관 장소가 없다면 빈 리스트가 조회된다.")
        void success_not_exist_like_place_empty_list() {
            // given
            final Member member = memberBuilder.defaultMember().build();
            final String accessToken = jwtService.createAccessToken(member.getId());

            // when
            final ExtractableResponse<Response> response = FIND_RECENT_LIKE_PLACE_REQUEST(accessToken);
            final List<LikePlaceResponse> actual = response.as(RecentLikePlacesResponse.class).getLikePlaces();

            // then
            assertSoftly(softly -> {
                softly.assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
                softly.assertThat(actual).isEmpty();
            });
        }
    }

    @Nested
    @DisplayName("보관 장소 전체 조회 시")
    class FindAllLikePlace {

        @Test
        @DisplayName("전체 조회에 성공한다.")
        void success() {
            // given
            final Member member = memberBuilder.defaultMember().build();
            final String accessToken = jwtService.createAccessToken(member.getId());

            final LikePlaceAddRequest addRequest1 = likePlaceBuilder.defaultLikePlace(member)
                    .photoReferences(List.of("photoReference1")).buildAddRequest();
            final LikePlaceAddRequest addRequest2 = likePlaceBuilder.defaultLikePlace(member)
                    .photoReferences(List.of("photoReference2")).buildAddRequest();
            final LikePlaceAddRequest addRequest3 = likePlaceBuilder.defaultLikePlace(member)
                    .photoReferences(List.of("photoReference3")).buildAddRequest();
            final LikePlaceAddRequest addRequest4 = likePlaceBuilder.defaultLikePlace(member)
                    .photoReferences(List.of("photoReference4")).buildAddRequest();

            final ExtractableResponse<Response> addResponse1 = ADD_LIKE_PLACE_REQUEST(accessToken, addRequest1);
            final ExtractableResponse<Response> addResponse2 = ADD_LIKE_PLACE_REQUEST(accessToken, addRequest2);
            final ExtractableResponse<Response> addResponse3 = ADD_LIKE_PLACE_REQUEST(accessToken, addRequest3);
            final ExtractableResponse<Response> addResponse4 = ADD_LIKE_PLACE_REQUEST(accessToken, addRequest4);

            final LikePlace likePlace1 = addRequest1.toEntity(member);
            final LikePlace likePlace2 = addRequest2.toEntity(member);
            final LikePlace likePlace3 = addRequest3.toEntity(member);
            final LikePlace likePlace4 = addRequest4.toEntity(member);

            final LikePlacesResponse expected = LikePlacesResponse.from(List.of(likePlace4, likePlace3, likePlace2, likePlace1));

            // when
            final ExtractableResponse<Response> response = FIND_ALL_LIKE_PLACE_REQUEST(accessToken);
            final LikePlacesResponse actual = response.as(LikePlacesResponse.class);

            // then
            assertSoftly(softly -> {
                softly.assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
                softly.assertThat(actual).usingRecursiveComparison().ignoringFields("likePlaces.id").isEqualTo(expected);
            });
        }

        @Test
        @DisplayName("추가한 보관 장소가 없다면 빈 리스트가 조회된다.")
        void success_not_exist_like_place_empty_list() {
            // given
            final Member member = memberBuilder.defaultMember().build();
            final String accessToken = jwtService.createAccessToken(member.getId());

            // when
            final ExtractableResponse<Response> response = FIND_ALL_LIKE_PLACE_REQUEST(accessToken);
            final List<LikePlaceResponse> actual = response.as(LikePlacesResponse.class).getLikePlaces();

            // then
            assertSoftly(softly -> {
                softly.assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
                softly.assertThat(actual).isEmpty();
            });
        }
    }

    @Nested
    @DisplayName("보관 장소 검색 시")
    class SearchByKeyword {

        @Test
        @DisplayName("정확도 순(키워드 일치 -> 키워드로 시작 -> 키워드로 끝)으로 보관 장소가 조회된다.")
        void success_with_correct_order() {
            // given
            final Member member = memberBuilder.defaultMember().build();
            final String accessToken = jwtService.createAccessToken(member.getId());
            final String keyword = "성수";

            final LikePlaceAddRequest addRequest1 = likePlaceBuilder.defaultLikePlace(member).photoReferences(List.of("photoReference1")).name("베이커리 " + keyword).buildAddRequest();
            final LikePlaceAddRequest addRequest2 = likePlaceBuilder.defaultLikePlace(member).photoReferences(List.of("photoReference2")).name(keyword + "지점").buildAddRequest();
            final LikePlaceAddRequest addRequest3 = likePlaceBuilder.defaultLikePlace(member).photoReferences(List.of("photoReference3")).name(keyword).buildAddRequest();
            final LikePlaceAddRequest addRequest4 = likePlaceBuilder.defaultLikePlace(member).photoReferences(List.of("photoReference4")).name("스타벅스 " + keyword + "점").buildAddRequest();

            ADD_LIKE_PLACE_REQUEST(accessToken, addRequest1);
            ADD_LIKE_PLACE_REQUEST(accessToken, addRequest2);
            ADD_LIKE_PLACE_REQUEST(accessToken, addRequest3);
            ADD_LIKE_PLACE_REQUEST(accessToken, addRequest4);

            final LikePlace thirdLikePlace = addRequest1.toEntity(member);
            final LikePlace secondLikePlace = addRequest2.toEntity(member);
            final LikePlace firstLikePlace = addRequest3.toEntity(member);
            final LikePlace fourthLikePlace = addRequest4.toEntity(member);

            final LikePlacesResponse expected = LikePlacesResponse.from(List.of(firstLikePlace, secondLikePlace, thirdLikePlace, fourthLikePlace));

            // when
            final ExtractableResponse<Response> response = SEARCH_LIKE_PLACE_REQUEST(accessToken, keyword);
            final LikePlacesResponse actual = response.as(LikePlacesResponse.class);

            // then
            assertSoftly(softly -> {
                softly.assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
                softly.assertThat(actual).usingRecursiveComparison().ignoringFields("likePlaces.id").isEqualTo(expected);
            });
        }
    }

    @Nested
    @DisplayName("보관 장소 삭제 시")
    class DeleteLikePlace {

        @Test
        @DisplayName("보관 장소 삭제에 성공한다.")
        void success() {
            // given
            final Member member = memberBuilder.defaultMember().build();
            final String accessToken = jwtService.createAccessToken(member.getId());
            final LikePlace likePlace = likePlaceBuilder.defaultLikePlace(member).build();

            // when
            final ExtractableResponse<Response> response = DELETE_LIKE_PLACE_REQUEST(accessToken, likePlace.getId());

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
        }

        @Test
        @DisplayName("보관 장소 ID에 해당하는 보관 장소가 존재하지 않으면 실패한다.")
        void fail_not_exist_like_place() {
            // given
            final Member member = memberBuilder.defaultMember().build();
            final String accessToken = jwtService.createAccessToken(member.getId());
            final Long notExistLikePlaceId = -1L;

            // when
            final ExtractableResponse<Response> response = DELETE_LIKE_PLACE_REQUEST(accessToken, notExistLikePlaceId);

            // then
            assertSoftly(softly -> {
                softly.assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
                softly.assertThat(response.jsonPath().getString("errorMessage")).isEqualTo("해당하는 보관 장소를 찾을 수 없습니다.");
            });
        }
    }

    private static ExtractableResponse<Response> ADD_LIKE_PLACE_REQUEST(final String accessToken, final LikePlaceAddRequest request) {
        return RestAssured.given().log().all()
                .header(HttpHeaders.AUTHORIZATION, JWT_PREFIX + accessToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .when().log().all()
                .post("/like-places")
                .then().log().all()
                .extract();
    }

    private static ExtractableResponse<Response> WRONG_TYPE_LATITUDE_ADD_LIKE_PLACE_REQUEST(final String accessToken, final String latitude) {
        return RestAssured.given().log().all()
                .header(HttpHeaders.AUTHORIZATION, JWT_PREFIX + accessToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body("{\n" +
                        "\t\"providerPlaceId\" : \"ChIJ07n0DkZ8ezUR7wp5kpXtCYQ\",\n" +
                        "\t\"name\" : \"스타벅스 부평점\",\n" +
                        "\t\"photoReferences\" : [\"AxxA\", \"BxxA\", \"BxxC\"],\n" +
                        "\t\"latitude\" : " + latitude + ",\n" +
                        "\t\"longitude\" : 1.1123,\n" +
                        "\t\"openingHours\" : \"월요일: 오전 7:00 ~ 오후 11:00, 화요일: 오전 7:00 ~ 오후 11:00, 수요일: 오전 7:00 ~ 오후 11:00, 목요일: 오전 7:00 ~ 오후 11:00, 금요일: 오전 7:00 ~ 오후 11:00, 토요일: 오전 7:00 ~ 오후 11:00, 일요일: 오전 9:00 ~ 오후 11:00\",\n" +
                        "\t\"addressName\" : \"인천광역시 부평구 경원대로 1397\",\n" +
                        "\t\"phoneNumber\" : \"1522-3232\",\n" +
                        "\t\"website\" : \"http://www.starbucks.co.kr/\",\n" +
                        "\t\"googleMapsUri\" : \"https://maps.google.com/?cid=9514396914460199663\",\n" +
                        "\t\"reservable\" : \"FALSE\",\n" +
                        "\t\"takeoutAvailable\" : \"TRUE\",\n" +
                        "\t\"parkingAvailable\" : \"FALSE\"\n" +
                        "}")
                .when().log().all()
                .post("/like-places")
                .then().log().all()
                .extract();
    }

    private static ExtractableResponse<Response> WRONG_TYPE_LONGITUDE_ADD_LIKE_PLACE_REQUEST(final String accessToken, final String longitude) {
        return RestAssured.given().log().all()
                .header(HttpHeaders.AUTHORIZATION, JWT_PREFIX + accessToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body("{\n" +
                        "\t\"providerPlaceId\" : \"ChIJ07n0DkZ8ezUR7wp5kpXtCYQ\",\n" +
                        "\t\"name\" : \"스타벅스 부평점\",\n" +
                        "\t\"photoReferences\" : [\"AxxA\", \"BxxA\", \"BxxC\"],\n" +
                        "\t\"latitude\" : 2.2212,\n" +
                        "\t\"longitude\" : " + longitude + ",\n" +
                        "\t\"openingHours\" : \"월요일: 오전 7:00 ~ 오후 11:00, 화요일: 오전 7:00 ~ 오후 11:00, 수요일: 오전 7:00 ~ 오후 11:00, 목요일: 오전 7:00 ~ 오후 11:00, 금요일: 오전 7:00 ~ 오후 11:00, 토요일: 오전 7:00 ~ 오후 11:00, 일요일: 오전 9:00 ~ 오후 11:00\",\n" +
                        "\t\"addressName\" : \"인천광역시 부평구 경원대로 1397\",\n" +
                        "\t\"phoneNumber\" : \"1522-3232\",\n" +
                        "\t\"website\" : \"http://www.starbucks.co.kr/\",\n" +
                        "\t\"googleMapsUri\" : \"https://maps.google.com/?cid=9514396914460199663\",\n" +
                        "\t\"reservable\" : \"FALSE\",\n" +
                        "\t\"takeoutAvailable\" : \"TRUE\",\n" +
                        "\t\"parkingAvailable\" : \"FALSE\"\n" +
                        "}")
                .when().log().all()
                .post("/like-places")
                .then().log().all()
                .extract();
    }

    private static ExtractableResponse<Response> FIND_LIKE_PLACE_REQUEST(final String accessToken, final Long likePlaceId) {
        return RestAssured.given().log().all()
                .header(HttpHeaders.AUTHORIZATION, JWT_PREFIX + accessToken)
                .when().log().all()
                .get("/like-places/{like-place-id}", likePlaceId)
                .then().log().all()
                .extract();
    }

    private static ExtractableResponse<Response> FIND_RECENT_LIKE_PLACE_REQUEST(final String accessToken) {
        return RestAssured.given().log().all()
                .header(HttpHeaders.AUTHORIZATION, JWT_PREFIX + accessToken)
                .when().log().all()
                .get("/like-places/recent")
                .then().log().all()
                .extract();
    }

    private static Long getLikePlaceId(final ExtractableResponse<Response> addResponse1) {
        final String location = addResponse1.header("Location");
        return Long.parseLong(location.substring(location.lastIndexOf("/") + 1));
    }

    private static ExtractableResponse<Response> FIND_ALL_LIKE_PLACE_REQUEST(final String accessToken) {
        return RestAssured.given().log().all()
                .header(HttpHeaders.AUTHORIZATION, JWT_PREFIX + accessToken)
                .when().log().all()
                .get("/like-places")
                .then().log().all()
                .extract();
    }

    private static ExtractableResponse<Response> SEARCH_LIKE_PLACE_REQUEST(final String accessToken, final String keyword) {
        return RestAssured.given().log().all()
                .header(HttpHeaders.AUTHORIZATION, JWT_PREFIX + accessToken)
                .when().log().all()
                .get("/like-places/search?keyword=" + keyword)
                .then().log().all()
                .extract();
    }

    private static ExtractableResponse<Response> DELETE_LIKE_PLACE_REQUEST(final String accessToken, final Long likePlaceId) {
        return RestAssured.given().log().all()
                .header(HttpHeaders.AUTHORIZATION, JWT_PREFIX + accessToken)
                .when().log().all()
                .delete("/like-places/{like-place-id}", likePlaceId)
                .then().log().all()
                .extract();
    }
}
