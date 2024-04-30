package haru.harudongseon.likeplace.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import haru.harudongseon.common.ServiceTest;
import haru.harudongseon.common.builder.LikePlaceBuilder;
import haru.harudongseon.common.builder.MemberBuilder;
import haru.harudongseon.likeplace.application.dto.LikePlaceAddRequest;
import haru.harudongseon.likeplace.application.dto.LikePlaceResponse;
import haru.harudongseon.likeplace.domain.LikePlace;
import haru.harudongseon.member.domain.Member;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class LikePlaceServiceTest extends ServiceTest {

    @Autowired
    private MemberBuilder memberBuilder;

    @Autowired
    private LikePlaceBuilder likePlaceBuilder;

    @Autowired
    private LikePlaceService likePlaceService;

    @Nested
    @DisplayName("보관 장소 추가 시")
    class AddLikePlace {

        @Test
        @DisplayName("추가에 성공한다.")
        void success() {
            // given
            final Member member = memberBuilder.defaultMember().build();
            final LikePlaceAddRequest request = likePlaceBuilder.defaultLikePlace(member).buildAddRequest();

            // when
            final Long savedLikePlaceId = likePlaceService.addLikePlace(member.getId(), request);

            // then
            assertThat(savedLikePlaceId).isNotNull();
        }

        @Test
        @DisplayName("멤버 ID에 해당하는 멤버가 없으면 예외가 발생한다.")
        void throws_not_exist_member() {
            // given
            final Long notExistMemberId = -1L;
            final LikePlaceAddRequest request = likePlaceBuilder.defaultLikePlace(null).buildAddRequest();

            // when & then
            assertThatThrownBy(() -> likePlaceService.addLikePlace(notExistMemberId, request))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessage("해당하는 멤버를 찾을 수 없습니다.");
        }
    }

    @Nested
    @DisplayName("보관 장소 조회 시")
    class FindLikePlace {

        @Test
        @DisplayName("보관 장소 ID에 해당하는 보관 장소 조회에 성공한다.")
        void success() {
            // given
            final Member savedMember = memberBuilder.defaultMember().build();
            final LikePlace savedLikePlace = likePlaceBuilder.defaultLikePlace(savedMember).build();
            final Long targetLikePlaceId = savedLikePlace.getId();
            final LikePlaceResponse expected = LikePlaceResponse.from(savedLikePlace);

            // when
            final LikePlaceResponse actual = likePlaceService.findLikePlace(targetLikePlaceId);

            // then
            assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
        }

        @Test
        @DisplayName("보관 장소 ID에 해당하는 보관 장소가 존재하지 않으면 예외가 발생한다.")
        void throws_not_exist_like_place() {
            // given
            final Member savedMember = memberBuilder.defaultMember().build();
            likePlaceBuilder.defaultLikePlace(savedMember).build();
            final Long notExistLikePlaceId = -1L;

            // when & then
            assertThatThrownBy(() -> likePlaceService.findLikePlace(notExistLikePlaceId))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessage("해당하는 보관 장소를 찾을 수 없습니다.");
        }
    }
}
