package haru.harudongseon.likeplace.domain;

import static haru.harudongseon.common.fixtures.LikePlaceFixtures.기본_보관_장소_엔티티;
import static haru.harudongseon.common.fixtures.LikePlaceFixtures.기본_보관_장소_엔티티_이름_파라미터;
import static haru.harudongseon.common.fixtures.MemberFixtures.기본_회원_엔티티;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import haru.harudongseon.common.RepositoryTest;
import haru.harudongseon.member.domain.Member;
import haru.harudongseon.member.domain.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class LikePlaceRepositoryTest extends RepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private LikePlaceRepository likePlaceRepository;

    @Test
    @DisplayName("Member Id에 해당하는 최대 3개의 보관 장소를 생성일 기준 내림차순 정렬로 조회한다.")
    void find_recent_three_by_member_id() {
        // given
        final Member member = memberRepository.save(기본_회원_엔티티());
        final LikePlace likePlace1 = likePlaceRepository.save(기본_보관_장소_엔티티(member));
        final LikePlace likePlace2 = likePlaceRepository.save(기본_보관_장소_엔티티(member));
        final LikePlace likePlace3 = likePlaceRepository.save(기본_보관_장소_엔티티(member));
        final LikePlace likePlace4 = likePlaceRepository.save(기본_보관_장소_엔티티(member));

        final List<LikePlace> expected = List.of(likePlace4, likePlace3, likePlace2);

        // when
        final List<LikePlace> actual = likePlaceRepository.findRecentThreeByMemberId(member.getId());

        // then
        assertThat(actual).usingRecursiveFieldByFieldElementComparator().isEqualTo(expected);
    }

    @Test
    @DisplayName("Member Id에 해당하는 모든 보관 장소를 생성일 기준 내림차순 정렬로 조회한다.")
    void final_all_by_member_id() {
        // given
        final Member member = memberRepository.save(기본_회원_엔티티());
        final LikePlace likePlace1 = likePlaceRepository.save(기본_보관_장소_엔티티(member));
        final LikePlace likePlace2 = likePlaceRepository.save(기본_보관_장소_엔티티(member));
        final LikePlace likePlace3 = likePlaceRepository.save(기본_보관_장소_엔티티(member));
        final LikePlace likePlace4 = likePlaceRepository.save(기본_보관_장소_엔티티(member));

        final List<LikePlace> expected = List.of(likePlace4, likePlace3, likePlace2, likePlace1);

        // when
        final List<LikePlace> actual = likePlaceRepository.findAllByMemberId(member.getId());

        // then
        assertThat(actual).usingRecursiveFieldByFieldElementComparator().isEqualTo(expected);
    }

    @Test
    @DisplayName("Member Id와 키워드에 해당하는 보관 장소를 정확도 순(키워드 일치 -> 키워드로 시작 -> 키워드로 끝)으로 조회한다.")
    void search_by_keyword_and_member_id_with_correct_order() {
        // given
        final Member member = memberRepository.save(기본_회원_엔티티());
        final String keyword = "성수";

        final LikePlace thirdLikePlace = likePlaceRepository.save(기본_보관_장소_엔티티_이름_파라미터(member, "베이커리 " + keyword));
        final LikePlace secondLikePlace = likePlaceRepository.save(기본_보관_장소_엔티티_이름_파라미터(member, keyword + "지점"));
        final LikePlace firstLikePlace = likePlaceRepository.save(기본_보관_장소_엔티티_이름_파라미터(member, keyword));
        final LikePlace fourthLikePlace = likePlaceRepository.save(기본_보관_장소_엔티티_이름_파라미터(member, "스타벅스 " + keyword +"점"));

        final List<LikePlace> expected = List.of(firstLikePlace, secondLikePlace, thirdLikePlace, fourthLikePlace);

        // when
        final List<LikePlace> actual = likePlaceRepository.searchByKeywordAndMemberId(keyword, member.getId());

        // then
        assertThat(actual).usingRecursiveFieldByFieldElementComparator().isEqualTo(expected);
    }
}
