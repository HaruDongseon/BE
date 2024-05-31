package haru.harudongseon.searchplace.presentation;

import static haru.harudongseon.common.fixtures.SearchPlaceFixtures.기본_검색_장소_키워드;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.util.List;

import haru.harudongseon.common.E2ETest;
import haru.harudongseon.common.builder.MemberBuilder;
import haru.harudongseon.common.builder.SearchPlaceBuilder;
import haru.harudongseon.member.domain.Member;
import haru.harudongseon.searchplace.application.dto.RecentSearchedPlaceResponse;
import haru.harudongseon.searchplace.application.dto.RecentSearchedPlacesResponse;
import haru.harudongseon.searchplace.application.dto.SearchedPlaceAddRequest;
import haru.harudongseon.searchplace.domain.SearchedPlace;
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

class SearchedPlaceControllerTest extends E2ETest {

    @Autowired
    private MemberBuilder memberBuilder;

    @Autowired
    private SearchPlaceBuilder searchPlaceBuilder;

    @Nested
    @DisplayName("검색한 장소 추가 시")
    class AddSearchedRoutePlace {

        @Test
        @DisplayName("해당 장소를 검색하지 않았었다면, 검색한 장소에 추가한다.")
        void success_not_exist_searched_place_then_add() {
            // given
            final Member member = memberBuilder.defaultMember().build();
            final String accessToken = jwtService.createAccessToken(member.getId());
            final String keyword = 기본_검색_장소_키워드;
            final SearchedPlaceAddRequest request = new SearchedPlaceAddRequest(keyword);

            // when
            final ExtractableResponse<Response> response = ADD_SEARCHED_PLACE_REQUEST(accessToken, request);

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        }

        @Test
        @DisplayName("이전에 같은 장소를 검색했다면, 이전 검색한 장소를 삭제하고 새로 추가한다.")
        void success_exist_searched_place_delete_place_and_add() {
            // given
            final Member member = memberBuilder.defaultMember().build();
            final SearchedPlace searchedPlace = searchPlaceBuilder.defaultSearchPlace(member).build();
            final String accessToken = jwtService.createAccessToken(member.getId());
            final String keyword = searchedPlace.getKeyword();
            final SearchedPlaceAddRequest request = new SearchedPlaceAddRequest(keyword);

            // when
            final ExtractableResponse<Response> response = ADD_SEARCHED_PLACE_REQUEST(accessToken, request);

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        }

        @Test
        @DisplayName("최근 검색 장소가 최대 개수라면, 가장 오래된 검색 장소를 삭제하고 새로 추가한다.")
        void success_max_searched_place_delete_oldest_and_add_new() {
            // given
            final Member member = memberBuilder.defaultMember().build();
            final SearchedPlace searchedPlace1 = searchPlaceBuilder.member(member).keyword(기본_검색_장소_키워드).build();
            final SearchedPlace searchedPlace2 = searchPlaceBuilder.member(member).keyword(기본_검색_장소_키워드 + "2").build();
            final SearchedPlace searchedPlace3 = searchPlaceBuilder.member(member).keyword(기본_검색_장소_키워드 + "3").build();
            final SearchedPlace searchedPlace4 = searchPlaceBuilder.member(member).keyword(기본_검색_장소_키워드 + "4").build();
            final SearchedPlace oldestSearchedPlace = searchPlaceBuilder.member(member).keyword(기본_검색_장소_키워드 + "5").build();

            final String accessToken = jwtService.createAccessToken(member.getId());

            final String keyword = "new " + 기본_검색_장소_키워드;
            final SearchedPlaceAddRequest request = new SearchedPlaceAddRequest(keyword);


            // when
            final ExtractableResponse<Response> response = ADD_SEARCHED_PLACE_REQUEST(accessToken, request);

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        }

        @ParameterizedTest
        @ValueSource(strings = {"", " "})
        @DisplayName("검색 키워드가 공백이라면 추가에 실패한다.")
        void fail_keyword_blank(final String blankKeyword) {
            // given
            final Member member = memberBuilder.defaultMember().build();
            final String accessToken = jwtService.createAccessToken(member.getId());
            final SearchedPlaceAddRequest request = new SearchedPlaceAddRequest(blankKeyword);

            // when
            final ExtractableResponse<Response> response = ADD_SEARCHED_PLACE_REQUEST(accessToken, request);

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
            assertThat(response.jsonPath().getString("errorMessage")).isEqualTo("검색어는 공백일 수 없습니다.");
        }

        @Test
        @DisplayName("존재하지 않는 멤버의 요청이면 추가에 실패한다.")
        void fail_not_exist_member_request() {
            // given
            final Long notExistMemberId = -1L;
            final String accessToken = jwtService.createAccessToken(notExistMemberId);
            final String keyword = 기본_검색_장소_키워드;
            final SearchedPlaceAddRequest request = new SearchedPlaceAddRequest(keyword);

            // when
            final ExtractableResponse<Response> response = ADD_SEARCHED_PLACE_REQUEST(accessToken, request);

            // then
            assertSoftly(softly -> {
                softly.assertThat(response.statusCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
                softly.assertThat(response.jsonPath().getString("errorMessage")).isEqualTo("인증에 실패했습니다. 정확한 에러는 서버 로그를 확인해주세요.");
            });
        }
    }

    @Nested
    @DisplayName("최근 검색한 장소 조회 시")
    class FindRecentSearchedPlace {

        @Test
        @DisplayName("최근 검색한 장소 순서대로 조회에 성공한다.")
        void success_with_recent_order() {
            // given
            final Member member = memberBuilder.defaultMember().build();
            final String accessToken = jwtService.createAccessToken(member.getId());
            final SearchedPlace searchedPlace1 = searchPlaceBuilder.member(member).keyword(기본_검색_장소_키워드 + "1").build();
            final SearchedPlace searchedPlace2 = searchPlaceBuilder.member(member).keyword(기본_검색_장소_키워드 + "2").build();
            final SearchedPlace searchedPlace3 = searchPlaceBuilder.member(member).keyword(기본_검색_장소_키워드 + "3").build();
            final SearchedPlace searchedPlace4 = searchPlaceBuilder.member(member).keyword(기본_검색_장소_키워드 + "4").build();
            final SearchedPlace searchedPlace5 = searchPlaceBuilder.member(member).keyword(기본_검색_장소_키워드 + "5").build();
            final List<SearchedPlace> searchedPlaces = List.of(searchedPlace5, searchedPlace4, searchedPlace3, searchedPlace2, searchedPlace1);
            final List<RecentSearchedPlaceResponse> expected = RecentSearchedPlacesResponse.from(searchedPlaces).getSearchedPlaces();

            // when
            final ExtractableResponse<Response> response = FIND_RECENT_SEARCHED_PLACE_REQUEST(accessToken);
            final RecentSearchedPlacesResponse recentSearchedPlacesResponse = response.as(RecentSearchedPlacesResponse.class);
            final List<RecentSearchedPlaceResponse> actual = recentSearchedPlacesResponse.getSearchedPlaces();

            // then
            assertSoftly(softly -> {
                softly.assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
                softly.assertThat(expected).usingRecursiveFieldByFieldElementComparator()
                        .isEqualTo(actual);
            });
        }

        @Test
        @DisplayName("최근 검색한 장소가 없으면 빈 리스트가 반환된다.")
        void success_not_exist_recent_search_place_empty_list() {
            // given
            final Member member = memberBuilder.defaultMember().build();
            final String accessToken = jwtService.createAccessToken(member.getId());

            // when
            final ExtractableResponse<Response> response = FIND_RECENT_SEARCHED_PLACE_REQUEST(accessToken);
            final RecentSearchedPlacesResponse recentSearchedPlacesResponse = response.as(RecentSearchedPlacesResponse.class);
            final List<RecentSearchedPlaceResponse> actual = recentSearchedPlacesResponse.getSearchedPlaces();

            // then
            assertSoftly(softly -> {
                softly.assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
                softly.assertThat(actual).isEmpty();
            });
        }

        @Test
        @DisplayName("존재하지 않는 멤버면 조회에 실패한다.")
        void fail_not_exist_member() {
            // given
            final Long notExistMemberId = -1L;
            final String notExistAccessToken = jwtService.createAccessToken(notExistMemberId);

            // when
            final ExtractableResponse<Response> response = FIND_RECENT_SEARCHED_PLACE_REQUEST(notExistAccessToken);

            // then
            assertSoftly(softly -> {
                softly.assertThat(response.statusCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
                softly.assertThat(response.jsonPath().getString("errorMessage")).isEqualTo("인증에 실패했습니다. 정확한 에러는 서버 로그를 확인해주세요.");
            });
        }
    }

    @Nested
    @DisplayName("검색한 장소 삭제 시")
    class Delete {

        @Test
        @DisplayName("삭제에 성공한다.")
        void success() {
            // given
            final Member member = memberBuilder.defaultMember().build();
            final String accessToken = jwtService.createAccessToken(member.getId());
            final SearchedPlace searchedPlace = searchPlaceBuilder.defaultSearchPlace(member).build();
            final Long searchedPlaceId = searchedPlace.getId();

            // when
            final ExtractableResponse<Response> response = DELETE_SEARCHED_PLACE_REQUEST(accessToken, searchedPlaceId);

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
        }

        @Test
        @DisplayName("멤버가 가진 검색 장소가 아니라면 삭제에 실패한다.")
        void fail_not_have_member_searched_place() {
            // given
            final Member member1 = memberBuilder.defaultMember().build();
            final Member member2 = memberBuilder.defaultMember().build();
            final String member2AccessToken = jwtService.createAccessToken(member2.getId());
            final SearchedPlace member1SearchedPlace = searchPlaceBuilder.defaultSearchPlace(member1).build();

            final Long notExistSearchedPlaceId = -1L;
            final Long member1SearchedPlaceId = member1SearchedPlace.getId();


            // when
            final ExtractableResponse<Response> response1 = DELETE_SEARCHED_PLACE_REQUEST(member2AccessToken, notExistSearchedPlaceId);
            final ExtractableResponse<Response> response2 = DELETE_SEARCHED_PLACE_REQUEST(member2AccessToken, member1SearchedPlaceId);

            // then
            assertSoftly(softly -> {
                softly.assertThat(response1.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
                softly.assertThat(response2.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
            });
        }

        @Test
        @DisplayName("둘 중 하나라도 존재하지 않는 멤버, 검색 장소 ID라면 삭제에 실패한다.")
        void fail_not_exist_member_and_not_exist_search_place() {
            // given
            final Member member = memberBuilder.defaultMember().build();
            final String existAccessToken = jwtService.createAccessToken(member.getId());
            final SearchedPlace searchedPlace = searchPlaceBuilder.defaultSearchPlace(member).build();

            final Long notExistMemberId = -1L;
            final String notExistMemberAccessToken = jwtService.createAccessToken(notExistMemberId);
            final Long notExistSearchedPlaceId = -1L;

            // when
            final ExtractableResponse<Response> response1 = DELETE_SEARCHED_PLACE_REQUEST(notExistMemberAccessToken, notExistSearchedPlaceId);
            final ExtractableResponse<Response> response2 = DELETE_SEARCHED_PLACE_REQUEST(notExistMemberAccessToken, searchedPlace.getId());
            final ExtractableResponse<Response> response3 = DELETE_SEARCHED_PLACE_REQUEST(existAccessToken, notExistSearchedPlaceId);

            // then
            assertSoftly(softly -> {
                softly.assertThat(response1.statusCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
                softly.assertThat(response2.statusCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
                softly.assertThat(response3.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
            });
        }
    }

    @Nested
    @DisplayName("검색한 장소 전체 삭제 시")
    class DeleteAll {

        @Test
        @DisplayName("전체 삭제에 성공한다.")
        void success() {
            // given
            final Member member = memberBuilder.defaultMember().build();
            final String accessToken = jwtService.createAccessToken(member.getId());
            searchPlaceBuilder.member(member).keyword(기본_검색_장소_키워드 + "1").build();
            searchPlaceBuilder.member(member).keyword(기본_검색_장소_키워드 + "2").build();
            searchPlaceBuilder.member(member).keyword(기본_검색_장소_키워드 + "3").build();
            searchPlaceBuilder.member(member).keyword(기본_검색_장소_키워드 + "4").build();
            searchPlaceBuilder.member(member).keyword(기본_검색_장소_키워드 + "5").build();

            // when
            final ExtractableResponse<Response> response = DELETE_ALL_SEARCHED_PLACE_REQUEST(accessToken);

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
        }
    }

    private static ExtractableResponse<Response> ADD_SEARCHED_PLACE_REQUEST(final String accessToken, final SearchedPlaceAddRequest request) {
        return RestAssured.given().log().all()
                .header(HttpHeaders.AUTHORIZATION, JWT_PREFIX + accessToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .when().log().all()
                .post("/searched-places")
                .then().log().all()
                .extract();
    }

    private static ExtractableResponse<Response> FIND_RECENT_SEARCHED_PLACE_REQUEST(final String accessToken) {
        return RestAssured.given().log().all()
                .header(HttpHeaders.AUTHORIZATION, JWT_PREFIX + accessToken)
                .when().log().all()
                .get("/searched-places/recent")
                .then().log().all()
                .extract();
    }

    private static ExtractableResponse<Response> DELETE_SEARCHED_PLACE_REQUEST(final String accessToken, final Long searchedPlaceId) {
        return RestAssured.given().log().all()
                .header(HttpHeaders.AUTHORIZATION, JWT_PREFIX + accessToken)
                .when().log().all()
                .delete("/searched-places/{searched-place-id}", searchedPlaceId)
                .then().log().all()
                .extract();
    }

    private static ExtractableResponse<Response> DELETE_ALL_SEARCHED_PLACE_REQUEST(final String accessToken) {
        return RestAssured.given().log().all()
                .header(HttpHeaders.AUTHORIZATION, JWT_PREFIX + accessToken)
                .when().log().all()
                .delete("/searched-places")
                .then().log().all()
                .extract();
    }
}
