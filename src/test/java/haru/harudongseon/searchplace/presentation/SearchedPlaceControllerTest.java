package haru.harudongseon.searchplace.presentation;

import static haru.harudongseon.common.fixtures.SearchPlaceFixtures.기본_검색_장소_키워드;
import static org.assertj.core.api.Assertions.assertThat;

import haru.harudongseon.common.E2ETest;
import haru.harudongseon.common.builder.MemberBuilder;
import haru.harudongseon.common.builder.SearchPlaceBuilder;
import haru.harudongseon.member.domain.Member;
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
    class AddSearchedPlace {

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
            assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
            assertThat(response.jsonPath().getString("errorMessage")).isEqualTo("해당하는 멤버를 찾을 수 없습니다.");
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
}
