package haru.harudongseon.likeplacestorage.domain;

import static haru.harudongseon.common.fixtures.LikePlaceFixtures.기본_보관_장소_도메인_이름_파라미터;
import static haru.harudongseon.common.fixtures.LikePlaceStorageFixtures.기본_장소_보관함_이름;
import static haru.harudongseon.common.fixtures.MemberFixtures.기본_회원_엔티티;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.util.ArrayList;
import java.util.List;

import haru.harudongseon.likeplace.domain.LikePlace;
import haru.harudongseon.likeplacestorage.exception.LikePlaceStorageException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class LikePlaceStorageTest {

    @Nested
    @DisplayName("보관 장소함에서 보관 장소 삭제 시")
    class RemoveLikePlace {

        @Test
        @DisplayName("보관 장소 삭제에 성공한다.")
        void success() {
            // given
            final Long likePlace1Id = 1L;
            final Long likePlace2Id = 2L;
            final LikePlace likePlace1 = 기본_보관_장소_도메인_이름_파라미터(likePlace1Id, 기본_회원_엔티티(), "성수 베이커리");
            final LikePlace likePlace2 = 기본_보관_장소_도메인_이름_파라미터(likePlace2Id, 기본_회원_엔티티(), "스타벅스 성수점");
            final LikePlaceStorage likePlaceStorage = new LikePlaceStorage(기본_장소_보관함_이름, 1L, new ArrayList<>(List.of(likePlace1, likePlace2)));

            // when
            likePlaceStorage.removeLikePlace(likePlace1Id);
            final List<LikePlace> likePlaces = likePlaceStorage.getLikePlaces();

            // then
            assertSoftly(softly -> {
                softly.assertThat(likePlaces.size()).isEqualTo(1);
                softly.assertThat(likePlaces.get(0).getId()).isEqualTo(likePlace2Id);
            });
        }

        @Test
        @DisplayName("보관 장소가 없으면 예외가 발생한다.")
        void throws_not_exist_like_place() {
            // given
            final Long likePlace1Id = 1L;
            final Long likePlace2Id = 2L;
            final LikePlace likePlace1 = 기본_보관_장소_도메인_이름_파라미터(likePlace1Id, 기본_회원_엔티티(), "성수 베이커리");
            final LikePlace likePlace2 = 기본_보관_장소_도메인_이름_파라미터(likePlace2Id, 기본_회원_엔티티(), "스타벅스 성수점");
            final LikePlaceStorage likePlaceStorage = new LikePlaceStorage(기본_장소_보관함_이름, 1L, new ArrayList<>(List.of(likePlace1, likePlace2)));

            final Long notExistLikePlaceId = -1L;

            // when & then
            assertThatThrownBy(() -> likePlaceStorage.removeLikePlace(notExistLikePlaceId))
                    .isInstanceOf(LikePlaceStorageException.NotExistLikePlaceException.class)
                    .hasMessage("장소 보관함에 해당하는 장소가 존재하지 않습니다.");
        }
    }
}
