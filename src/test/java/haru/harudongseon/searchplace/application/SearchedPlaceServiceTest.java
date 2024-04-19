package haru.harudongseon.searchplace.application;

import static haru.harudongseon.common.fixtures.SearchPlaceFixtures.기본_검색_장소_키워드;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import haru.harudongseon.common.ServiceTest;
import haru.harudongseon.common.builder.MemberBuilder;
import haru.harudongseon.searchplace.application.dto.SearchedPlaceAddRequest;
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

    @Nested
    @DisplayName("최근 검색 장소 추가 시")
    class AddRecentSearchedRoutePlace {

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
}
