package haru.harudongseon.likeplacestorage.domain;

import static haru.harudongseon.common.fixtures.LikePlaceFixtures.기본_보관_장소_도메인_이름_파라미터;
import static haru.harudongseon.common.fixtures.LikePlaceStorageFixtures.기본_장소_보관함_이름;
import static haru.harudongseon.common.fixtures.MemberFixtures.기본_회원_도메인;
import static haru.harudongseon.common.fixtures.MemberFixtures.기본_회원_엔티티;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.util.List;

import haru.harudongseon.likeplace.domain.LikePlace;
import haru.harudongseon.likeplacestorage.exception.LikePlaceStorageException;
import haru.harudongseon.member.domain.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class LikePlaceStorageTest {

    @Nested
    @DisplayName("장소 보관함에서 보관 장소 추가 시")
    class AddLikePlaces {

        @Test
        @DisplayName("추가할 보관 장소 중 하나라도 이미 존재하는 보관 장소면 예외가 발생한다.")
        void success() {
            // given
            final Long likePlace1Id = 1L;
            final Long likePlace2Id = 2L;
            final Member member = 기본_회원_엔티티();
            final LikePlace likePlace1 = 기본_보관_장소_도메인_이름_파라미터(likePlace1Id, member, "성수 베이커리");
            final LikePlace likePlace2 = 기본_보관_장소_도메인_이름_파라미터(likePlace2Id, member, "스타벅스 성수점");

            final LikePlaceStorage likePlaceStorage = new LikePlaceStorage(기본_장소_보관함_이름, member);
            likePlaceStorage.addLikePlaces(List.of(likePlace1));

            // when & then
            assertThatThrownBy(() -> likePlaceStorage.addLikePlaces(List.of(likePlace1, likePlace2)))
                    .isInstanceOf(LikePlaceStorageException.AlreadyExistLikePlaceException.class)
                    .hasMessage("장소 보관함에 이미 존재하는 보관 장소입니다.");

        }
    }

    @Nested
    @DisplayName("보관 장소함에서 보관 장소 삭제 시")
    class RemoveLikePlace {

        @Test
        @DisplayName("보관 장소 삭제에 성공한다.")
        void success() {
            // given
            final Long likePlace1Id = 1L;
            final Long likePlace2Id = 2L;
            final Member member = 기본_회원_엔티티();
            final LikePlace likePlace1 = 기본_보관_장소_도메인_이름_파라미터(likePlace1Id, member, "성수 베이커리");
            final LikePlace likePlace2 = 기본_보관_장소_도메인_이름_파라미터(likePlace2Id, member, "스타벅스 성수점");

            final LikePlaceStorage likePlaceStorage = new LikePlaceStorage(기본_장소_보관함_이름, member);
            likePlaceStorage.addLikePlaces(List.of(likePlace1, likePlace2));

            // when
            likePlaceStorage.removeLikePlaces(List.of(likePlace1Id));
            final List<StoredLikePlace> likePlaces = likePlaceStorage.getLikePlaces();

            // then
            assertSoftly(softly -> {
                softly.assertThat(likePlaces.size()).isEqualTo(1);
                softly.assertThat(likePlaces.get(0).getLikePlace().getId()).isEqualTo(likePlace2Id);
            });
        }

        @Test
        @DisplayName("보관 장소가 없으면 예외가 발생한다.")
        void throws_not_exist_like_place() {
            // given
            final Long likePlace1Id = 1L;
            final Long likePlace2Id = 2L;
            final Long memberId = 1L;
            final Member member = 기본_회원_도메인(memberId);
            final LikePlace likePlace1 = 기본_보관_장소_도메인_이름_파라미터(likePlace1Id, member, "성수 베이커리");
            final LikePlace likePlace2 = 기본_보관_장소_도메인_이름_파라미터(likePlace2Id, member, "스타벅스 성수점");
            final LikePlaceStorage likePlaceStorage = new LikePlaceStorage(기본_장소_보관함_이름, member);
            likePlaceStorage.addLikePlaces(List.of(likePlace1, likePlace2));

            final Long notExistLikePlaceId = -1L;

            // when & then
            assertThatThrownBy(() -> likePlaceStorage.removeLikePlaces(List.of(notExistLikePlaceId)))
                    .isInstanceOf(LikePlaceStorageException.NotExistLikePlaceException.class)
                    .hasMessage("장소 보관함에 해당하는 장소가 존재하지 않습니다.");
        }
    }
}
