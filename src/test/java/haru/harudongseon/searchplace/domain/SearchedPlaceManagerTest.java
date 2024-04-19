package haru.harudongseon.searchplace.domain;

import static haru.harudongseon.common.fixtures.SearchPlaceFixtures.기본_검색_장소_키워드;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.util.List;
import java.util.Optional;

import haru.harudongseon.common.builder.MemberBuilder;
import haru.harudongseon.common.builder.SearchPlaceBuilder;
import haru.harudongseon.member.domain.Member;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SearchedPlaceManagerTest {

    @Autowired
    private SearchedPlaceManager searchedPlaceManager;

    @Autowired
    private SearchedPlaceRepository searchedPlaceRepository;

    @Autowired
    private MemberBuilder memberBuilder;

    @Autowired
    private SearchPlaceBuilder searchPlaceBuilder;

    @Nested
    @DisplayName("검색 장소 추가 시 ")
    class Save {

        @Test
        @DisplayName("이전에 존재하지 않은 검색 장소면 추가에 성공한다.")
        void success_not_exist_search_place() {
            // given
            final String keyword = 기본_검색_장소_키워드;
            final Member member = memberBuilder.defaultMember().build();
            final Long memberId = member.getId();
            final Optional<SearchedPlace> beforeResult = searchedPlaceRepository.findByMemberIdAndKeyword(memberId, keyword);

            // when
            searchedPlaceManager.save(member, keyword);
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
        @DisplayName("이미 존재하는 검색 장소면 기존 장소를 삭제하고 추가에 성공한다.")
        void success_exist_search_place_delete_exits_place_and_add() {
            // given
            final String keyword = 기본_검색_장소_키워드;
            final Member member = memberBuilder.defaultMember().build();
            searchPlaceBuilder.defaultSearchPlace(member).build();

            final Long memberId = member.getId();
            final Optional<SearchedPlace> beforeResult = searchedPlaceRepository.findByMemberIdAndKeyword(memberId, keyword);

            // when
            searchedPlaceManager.save(member, keyword);
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
        @DisplayName("검색 장소 개수가 최대라면 가장 오래된 검색 장소를 삭제하고 추가에 성공한다.")
        void success_max_searched_place_delete_oldest_and_add_new() {
            // given
            final int maxSearchedPlaceCount = 5;
            final String keyword = "new " + 기본_검색_장소_키워드;
            final Member member = memberBuilder.defaultMember().build();

            final SearchedPlace searchedPlace1 = searchPlaceBuilder.member(member).keyword(기본_검색_장소_키워드).build();
            final SearchedPlace searchedPlace2 = searchPlaceBuilder.member(member).keyword(기본_검색_장소_키워드 + "2").build();
            final SearchedPlace searchedPlace3 = searchPlaceBuilder.member(member).keyword(기본_검색_장소_키워드 + "3").build();
            final SearchedPlace searchedPlace4 = searchPlaceBuilder.member(member).keyword(기본_검색_장소_키워드 + "4").build();
            final SearchedPlace oldestSearchedPlace = searchPlaceBuilder.member(member).keyword(기본_검색_장소_키워드 + "5").build();

            // when
            searchedPlaceManager.save(member, keyword);
            final List<SearchedPlace> actual = searchedPlaceRepository.findAllByMemberIdOrderByCreatedAtDesc(member.getId());

            // then
            assertSoftly(softly -> {
                softly.assertThat(actual.size()).isEqualTo(maxSearchedPlaceCount);
                softly.assertThat(actual.get(maxSearchedPlaceCount - 1)).isNotEqualTo(oldestSearchedPlace);
                softly.assertThat(actual.get(0)).isNotEqualTo(searchedPlace1);
            });
        }
    }

    @Nested
    @DisplayName("검색 장소 삭제 시")
    class Delete {

        @Test
        @DisplayName("멤버가 가진 검색 장소가 아니라면 예외가 발생한다.")
        void throws_not_have_member_searched_place() {
            // given
            final Member member1 = memberBuilder.defaultMember().build();
            final Member member2 = memberBuilder.defaultMember().build();
            final SearchedPlace member1SearchedPlace = searchPlaceBuilder.defaultSearchPlace(member1).build();

            final Long member2Id = member2.getId();
            final Long notExistSearchedPlaceId = -1L;
            final Long member1SearchedPlaceId = member1SearchedPlace.getId();

            // when & then
            assertSoftly(softly -> {
                softly.assertThatThrownBy(() -> searchedPlaceManager.deleteById(notExistSearchedPlaceId, member2Id))
                        .isInstanceOf(EntityNotFoundException.class)
                        .hasMessage("해당하는 검색 장소가 존재하지 않습니다.");
                softly.assertThatThrownBy(() -> searchedPlaceManager.deleteById(member1SearchedPlaceId, member2Id))
                        .isInstanceOf(EntityNotFoundException.class)
                        .hasMessage("해당하는 검색 장소가 존재하지 않습니다.");
            });
        }

        @Test
        @DisplayName("둘 중 하나라도 존재하지 않는 멤버, 검색 장소 ID라면 예외가 발생한다.")
        void throws_not_exist_member_and_not_exist_search_place() {
            // given
            final Member member = memberBuilder.defaultMember().build();
            final SearchedPlace searchedPlace = searchPlaceBuilder.defaultSearchPlace(member).build();

            final Long notExistMemberId = -1L;
            final Long notExistSearchedPlaceId = -1L;

            // when & then
            assertSoftly(softly -> {
                softly.assertThatThrownBy(() -> searchedPlaceManager.deleteById(notExistSearchedPlaceId, notExistMemberId))
                        .isInstanceOf(EntityNotFoundException.class)
                        .hasMessage("해당하는 검색 장소가 존재하지 않습니다.");
                softly.assertThatThrownBy(() -> searchedPlaceManager.deleteById(notExistSearchedPlaceId, member.getId()))
                        .isInstanceOf(EntityNotFoundException.class)
                        .hasMessage("해당하는 검색 장소가 존재하지 않습니다.");
                softly.assertThatThrownBy(() -> searchedPlaceManager.deleteById(searchedPlace.getId(), notExistMemberId))
                        .isInstanceOf(EntityNotFoundException.class)
                        .hasMessage("해당하는 검색 장소가 존재하지 않습니다.");
            });
        }

        @Test
        @DisplayName("존재하지 않는 검색 장소 라면 예외가 발생한다.")
        void throws_not_exist_member() {
            // given
            final Member member = memberBuilder.defaultMember().build();
            final SearchedPlace searchedPlace = searchPlaceBuilder.defaultSearchPlace(member).build();
            final Long existSearchedPlaceId = searchedPlace.getId();
            final Long notExistMemberId = -1L;

            // when & then
            assertThatThrownBy(() -> searchedPlaceManager.deleteById(existSearchedPlaceId, notExistMemberId))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessage("해당하는 검색 장소가 존재하지 않습니다.");
        }
    }
}
