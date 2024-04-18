package haru.harudongseon.routetag.presentation;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.util.List;

import haru.harudongseon.common.E2ETest;
import haru.harudongseon.common.builder.MemberBuilder;
import haru.harudongseon.common.builder.RouteTagBuilder;
import haru.harudongseon.member.domain.Member;
import haru.harudongseon.routetag.application.dto.RouteTagResponse;
import haru.harudongseon.routetag.application.dto.RouteTagSearchResponse;
import haru.harudongseon.routetag.domain.RouteTag;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

class RouteTagControllerTest extends E2ETest {

    @Autowired
    private MemberBuilder memberBuilder;

    @Autowired
    private RouteTagBuilder routeTagBuilder;

    @Nested
    @DisplayName("동선 태그 검색 시")
    class SearchByKeyword {

        @Test
        @DisplayName("검색에 성공하면 대소문자를 무시한 검색 키워드가 포함된 동선 태그들이 조회된다.")
        void success_keyword_containing_ignore_case() {
            // given
            final Member savedMember = memberBuilder.defaultMember().build();
            final String accessToken = jwtService.createAccessToken(savedMember.getId());

            final RouteTag defaultRouteTag = routeTagBuilder.defaultRouteTag().build();
            final String keyword = defaultRouteTag.getName();

            final RouteTag firstKeywordRouteTag = routeTagBuilder.defaultRouteTag().name("first " + keyword).build();
            final RouteTag upperCaseKeywordRouteTag = routeTagBuilder.defaultRouteTag().name("first " + keyword.toUpperCase()).build();
            final RouteTag lastKeywordRouteTag = routeTagBuilder.defaultRouteTag().name(keyword + " last").build();
            final List<RouteTag> routeTags = List.of(defaultRouteTag, firstKeywordRouteTag, upperCaseKeywordRouteTag, lastKeywordRouteTag);
            final List<RouteTagResponse> expected = RouteTagSearchResponse.from(routeTags).getRouteTags();

            // when
            final ExtractableResponse<Response> response = SEARCH_BY_KEYWORD_REQUEST(accessToken, keyword);
            final List<RouteTagResponse> actual = response.as(RouteTagSearchResponse.class).getRouteTags();

            // then
            assertSoftly(softly -> {
                softly.assertThat(actual).usingRecursiveFieldByFieldElementComparator()
                        .containsExactlyInAnyOrderElementsOf(expected);
                softly.assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
            });
        }

        @Test
        @DisplayName("검색에 성공하면 선택 횟수로 내림차순 정렬된 동선 태그들이 조회된다.")
        void success_order_by_select_count_desc() {
            // given
            final Member savedMember = memberBuilder.defaultMember().build();
            final String accessToken = jwtService.createAccessToken(savedMember.getId());

            final RouteTag defaultAndFirstRouteTag = routeTagBuilder.defaultRouteTag().build();
            final String keyword = defaultAndFirstRouteTag.getName();
            final Long selectCount = defaultAndFirstRouteTag.getSelectCount();

            final RouteTag thirdRouteTag = routeTagBuilder.name(keyword + " diff1").selectCount(selectCount - 2L).build();
            final RouteTag secondRouteTag = routeTagBuilder.name(keyword + " diff2").selectCount(selectCount - 1L).build();
            final List<RouteTag> routeTags = List.of(defaultAndFirstRouteTag, secondRouteTag, thirdRouteTag);
            final List<RouteTagResponse> expected = RouteTagSearchResponse.from(routeTags).getRouteTags();

            // when
            final ExtractableResponse<Response> response = SEARCH_BY_KEYWORD_REQUEST(accessToken, keyword);
            final List<RouteTagResponse> actual = response.as(RouteTagSearchResponse.class).getRouteTags();

            // then
            assertSoftly(softly -> {
                softly.assertThat(actual).usingRecursiveFieldByFieldElementComparator()
                        .isEqualTo(expected);
                softly.assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
            });
        }

        @Test
        @DisplayName("검색 성공 시 선택 횟수가 같은 동선 태그면, '키워드 일치' -> '키워드 뒤에 다른 문자 포함' -> '키워드 앞에 다른 문자 포함' 순으로 정렬된 동선 태그들이 조회된다.")
        void success_same_select_count_order_by_wild_card() {
            // given
            final Member savedMember = memberBuilder.defaultMember().build();
            final String accessToken = jwtService.createAccessToken(savedMember.getId());

            final RouteTag defaultAndFirstRouteTag = routeTagBuilder.defaultRouteTag().build();
            final String keyword = defaultAndFirstRouteTag.getName();

            final RouteTag thirdKeywordRouteTag = routeTagBuilder.defaultRouteTag().name("forward " + keyword).build();
            final RouteTag secondKeywordRouteTag = routeTagBuilder.defaultRouteTag().name(keyword + " behind").build();
            final List<RouteTag> routeTags = List.of(defaultAndFirstRouteTag, secondKeywordRouteTag, thirdKeywordRouteTag);
            final List<RouteTagResponse> expected = RouteTagSearchResponse.from(routeTags).getRouteTags();

            // when
            final ExtractableResponse<Response> response = SEARCH_BY_KEYWORD_REQUEST(accessToken, keyword);
            final List<RouteTagResponse> actual = response.as(RouteTagSearchResponse.class).getRouteTags();

            // then
            assertSoftly(softly -> {
                softly.assertThat(actual).usingRecursiveFieldByFieldElementComparator()
                        .isEqualTo(expected);
                softly.assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
            });
        }

        @Test
        @DisplayName("검색 성공 시 검색 결과가 없으면 빈 리스트가 조회된다.")
        void success_not_exist_result_empty_list() {
            // given
            final Member savedMember = memberBuilder.defaultMember().build();
            final String accessToken = jwtService.createAccessToken(savedMember.getId());

            final String notExistKeyword = "not Exist Keyword";

            // when
            final ExtractableResponse<Response> response = SEARCH_BY_KEYWORD_REQUEST(accessToken, notExistKeyword);
            final List<RouteTagResponse> actual = response.as(RouteTagSearchResponse.class).getRouteTags();

            // then
            assertSoftly(softly -> {
                softly.assertThat(actual.isEmpty()).isTrue();
                softly.assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
            });
        }
    }

    private static ExtractableResponse<Response> SEARCH_BY_KEYWORD_REQUEST(final String accessToken, final String keyword) {
        return RestAssured.given().log().all()
                .header(HttpHeaders.AUTHORIZATION, JWT_PREFIX + accessToken)
                .when().log().all()
                .get("/route-tags/search?keyword=" + keyword)
                .then().log().all()
                .extract();
    }
}
