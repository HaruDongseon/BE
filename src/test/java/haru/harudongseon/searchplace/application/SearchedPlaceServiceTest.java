package haru.harudongseon.searchplace.application;

import static haru.harudongseon.common.fixtures.SearchPlaceFixtures.기본_검색_장소_키워드;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.util.List;

import haru.harudongseon.common.ServiceTest;
import haru.harudongseon.common.builder.MemberBuilder;
import haru.harudongseon.common.builder.SearchPlaceBuilder;
import haru.harudongseon.member.domain.Member;
import haru.harudongseon.searchplace.application.dto.RecentSearchedPlaceResponse;
import haru.harudongseon.searchplace.application.dto.RecentSearchedPlacesResponse;
import haru.harudongseon.searchplace.application.dto.SearchedPlaceAddRequest;
import haru.harudongseon.searchplace.domain.SearchedPlace;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class SearchedPlaceServiceTest extends ServiceTest {

    @Autowired
    private MemberBuilder memberBuilder;

    @Autowired
    private SearchedPlaceService searchedPlaceService;

    @Autowired
    private SearchPlaceBuilder searchPlaceBuilder;

    @Nested
    @DisplayName("검색 장소 추가 시")
    class AddSearchedPlace {

        @Test
        @DisplayName("정상적으로 추가에 성공한다.")
        void success_not_exist_search_place() {
            // given
            final String keyword = 기본_검색_장소_키워드;
            final Long memberId = memberBuilder.defaultMember().build().getId();
            final SearchedPlaceAddRequest request = new SearchedPlaceAddRequest(keyword);

            // when & then
            assertDoesNotThrow(() -> searchedPlaceService.addSearchedPlace(memberId, request));
        }

        @Test
        @DisplayName("멤버 ID에 해당하는 멤버가 존재하지 않으면 예외가 발생한다.")
        void throws_not_exits_member_id() {
            // given
            final Long notExistMemberId = -1L;
            final String keyword = 기본_검색_장소_키워드;
            final SearchedPlaceAddRequest request = new SearchedPlaceAddRequest(keyword);

            // when & then
            assertThatThrownBy(() -> searchedPlaceService.addSearchedPlace(notExistMemberId, request))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessage("해당하는 멤버를 찾을 수 없습니다.");
        }
    }

    @Nested
    @DisplayName("최근 검색 장소 조회 시")
    class FindRecentSearchedPlace {

        @Test
        @DisplayName("정상적으로 조회에 성공한다.")
        void success() {
            // given
            final Member member = memberBuilder.defaultMember().build();
            final Long memberId = member.getId();
            final SearchedPlace searchedPlace1 = searchPlaceBuilder.defaultSearchPlace(member).keyword(기본_검색_장소_키워드 + "1").build();
            final SearchedPlace searchedPlace2 = searchPlaceBuilder.defaultSearchPlace(member).keyword(기본_검색_장소_키워드 + "2").build();
            final SearchedPlace searchedPlace3 = searchPlaceBuilder.defaultSearchPlace(member).keyword(기본_검색_장소_키워드 + "3").build();
            final SearchedPlace searchedPlace4 = searchPlaceBuilder.defaultSearchPlace(member).keyword(기본_검색_장소_키워드 + "4").build();
            final SearchedPlace searchedPlace5 = searchPlaceBuilder.defaultSearchPlace(member).keyword(기본_검색_장소_키워드 + "5").build();
            final List<SearchedPlace> searchedPlaces = List.of(searchedPlace5, searchedPlace4, searchedPlace3, searchedPlace2, searchedPlace1);
            final RecentSearchedPlacesResponse expected = RecentSearchedPlacesResponse.from(searchedPlaces);

            // when
            final RecentSearchedPlacesResponse actual = searchedPlaceService.findRecentSearchedPlace(memberId);

            // then
            assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
        }

        @Test
        @DisplayName("최근에 검색한 검색 장소가 없으면 빈 Response가 반환된다.")
        void success_not_exist_recent_search_place_empty_response() {
            // given
            final Member member = memberBuilder.defaultMember().build();
            final Long memberId = member.getId();

            // when
            final List<RecentSearchedPlaceResponse> actual = searchedPlaceService.findRecentSearchedPlace(memberId).getSearchedPlaces();

            // then
            assertThat(actual).isEmpty();
        }

        @Test
        @DisplayName("멤버 ID에 해당하는 멤버가 존재하지 않으면 예외가 발생한다.")
        void throws_not_exist_member() {
            // given
            final Long notExistMemberId = -1L;

            // when & then
            assertThatThrownBy(() -> searchedPlaceService.findRecentSearchedPlace(notExistMemberId))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessage("해당하는 멤버를 찾을 수 없습니다.");
        }
    }
}
