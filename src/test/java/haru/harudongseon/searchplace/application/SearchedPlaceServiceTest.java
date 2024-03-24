package haru.harudongseon.searchplace.application;

import static haru.harudongseon.common.fixtures.SearchPlaceFixtures.기본_검색_장소_키워드;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.util.Optional;

import haru.harudongseon.common.ServiceTest;
import haru.harudongseon.common.builder.MemberBuilder;
import haru.harudongseon.common.builder.SearchPlaceBuilder;
import haru.harudongseon.member.domain.Member;
import haru.harudongseon.searchplace.application.dto.SearchedPlaceAddRequest;
import haru.harudongseon.searchplace.domain.SearchedPlace;
import haru.harudongseon.searchplace.domain.SearchedPlaceRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class SearchedPlaceServiceTest extends ServiceTest {

    @Autowired
    private MemberBuilder memberBuilder;

    @Autowired
    private SearchPlaceBuilder searchPlaceBuilder;

    @Autowired
    private SearchedPlaceService searchedPlaceService;

    @Autowired
    private SearchedPlaceRepository searchedPlaceRepository;

    @Nested
    @DisplayName("최근 검색 장소 추가 시")
    class AddRecentSearchedPlace {

        @Test
        @DisplayName("이전에 존재하지 않은 검색 장소면 추가에 성공한다.")
        void success_not_exist_search_place() {
            // given
            final String keyword = 기본_검색_장소_키워드;
            final Long memberId = memberBuilder.defaultMember().build().getId();
            final Optional<SearchedPlace> beforeResult = searchedPlaceRepository.findByMemberIdAndKeyword(memberId, keyword);
            final SearchedPlaceAddRequest request = new SearchedPlaceAddRequest(keyword);

            // when
            searchedPlaceService.addSearchedPlace(memberId, request);
            final Optional<SearchedPlace> afterResult = searchedPlaceRepository.findByMemberIdAndKeyword(memberId, keyword);

            // then
            assertSoftly(softly -> {
                softly.assertThat(beforeResult.isEmpty()).isTrue();
                softly.assertThat(afterResult.isPresent()).isTrue();
                softly.assertThat(afterResult.get().getMember().getId()).isEqualTo(memberId);
                softly.assertThat(afterResult.get().getKeyword()).isEqualTo(keyword);
            });
        }

        @Test
        @DisplayName("이미 존재하지 않은 검색 장소면 기존 장소를 삭제하고 추가에 성공한다.")
        void success_exist_search_place_delete_exits_place_and_add() {
            // given
            final String keyword = 기본_검색_장소_키워드;
            final Member savedMember = memberBuilder.defaultMember().build();
            searchPlaceBuilder.defaultSearchPlace(savedMember).build();

            final Long memberId = savedMember.getId();
            final Optional<SearchedPlace> beforeResult = searchedPlaceRepository.findByMemberIdAndKeyword(memberId, keyword);
            final SearchedPlaceAddRequest request = new SearchedPlaceAddRequest(keyword);

            // when
            searchedPlaceService.addSearchedPlace(memberId, request);
            final Optional<SearchedPlace> afterResult = searchedPlaceRepository.findByMemberIdAndKeyword(memberId, keyword);

            // then
            assertSoftly(softly -> {
                softly.assertThat(beforeResult.isPresent()).isTrue();
                softly.assertThat(afterResult.isPresent()).isTrue();
                softly.assertThat(beforeResult.get().getId()).isNotEqualTo(afterResult.get().getId());
                softly.assertThat(afterResult.get().getMember().getId()).isEqualTo(memberId);
                softly.assertThat(afterResult.get().getKeyword()).isEqualTo(keyword);
            });
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
