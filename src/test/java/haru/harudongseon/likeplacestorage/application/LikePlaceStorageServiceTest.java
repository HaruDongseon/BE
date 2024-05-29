package haru.harudongseon.likeplacestorage.application;

import static haru.harudongseon.common.fixtures.LikePlaceStorageFixtures.기본_장소_보관함_이름;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.util.ArrayList;
import java.util.List;

import haru.harudongseon.common.ServiceTest;
import haru.harudongseon.common.builder.LikePlaceBuilder;
import haru.harudongseon.common.builder.LikePlaceStorageBuilder;
import haru.harudongseon.common.builder.MemberBuilder;
import haru.harudongseon.likeplacestorage.application.dto.LikePlaceStorageAddRequest;
import haru.harudongseon.likeplacestorage.domain.LikePlaceStorage;
import haru.harudongseon.likeplacestorage.domain.LikePlaceStorageRepository;
import haru.harudongseon.likeplacestorage.exception.LikePlaceStorageException;
import haru.harudongseon.member.domain.Member;
import jakarta.persistence.EntityNotFoundException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class LikePlaceStorageServiceTest extends ServiceTest {

    @Autowired
    private MemberBuilder memberBuilder;

    @Autowired
    private LikePlaceStorageBuilder likePlaceStorageBuilder;

    @Autowired
    private LikePlaceStorageRepository likePlaceStorageRepository;

    @Autowired
    private LikePlaceBuilder likePlaceBuilder;

    @Autowired
    private LikePlaceStorageService likePlaceStorageService;

    @Nested
    @DisplayName("장소 보관함 추가 시")
    class AddLikePlaceStorage {

        @Test
        @DisplayName("추가에 성공한다.")
        void success() {
            // given
            final Member member = memberBuilder.defaultMember().build();
            final LikePlaceStorageAddRequest request = new LikePlaceStorageAddRequest(기본_장소_보관함_이름);

            // when
            final Long savedLikePlaceStorageId = likePlaceStorageService.addLikePlaceStorage(member.getId(), request);

            // then
            Assertions.assertThat(savedLikePlaceStorageId).isNotNull();
        }

        @Test
        @DisplayName("멤버 ID에 해당하는 멤버가 존재하지 않으면 예외가 발생한다.")
        void throws_not_exist_member() {
            // given
            final Long notExistMemberId = -1L;
            final LikePlaceStorageAddRequest request = new LikePlaceStorageAddRequest(기본_장소_보관함_이름);

            // when & then
            assertThatThrownBy(() -> likePlaceStorageService.addLikePlaceStorage(notExistMemberId, request))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessage("해당하는 멤버가 존재하지 않습니다.");
        }

        @Test
        @DisplayName("회원의 중복된 이름인 장소 보관함이 있으면 예외가 발생한다.")
        void throws_duplicate_like_place_storage() {
            // given
            final Member member = memberBuilder.defaultMember().build();
            final LikePlaceStorage existLikePlaceStorage = likePlaceStorageBuilder.defaultLikePlaceStorage(member).build();

            final LikePlaceStorageAddRequest duplicateNameRequest = new LikePlaceStorageAddRequest(existLikePlaceStorage.getName());

            // when & then
            assertThatThrownBy(() -> likePlaceStorageService.addLikePlaceStorage(member.getId(), duplicateNameRequest))
                    .isInstanceOf(LikePlaceStorageException.DuplicateException.class)
                    .hasMessage("중복된 이름을 가진 회원의 장소 보관함이 이미 존재합니다.");
        }
    }

    @Nested
    @DisplayName("장소 보관함의 보관 장소 삭제 시")
    class RemoveLikePlace {

        @Test
        @DisplayName("보관 장소 삭제에 성공한다.")
        void success() {
            // given
            final LikePlace likePlace1 = likePlaceBuilder.defaultLikePlace(기본_회원_엔티티()).name("베이커리 성수").build();
            final LikePlace likePlace2 = likePlaceBuilder.defaultLikePlace(기본_회원_엔티티()).name("스타벅스 성수점").build();
            final Long memberId = 1L;
            final List<LikePlace> likePlaces = new ArrayList<>(List.of(likePlace1, likePlace2));
            final LikePlaceStorage likePlaceStorage = likePlaceStorageBuilder.defaultLikePlaceStorage(memberId).likePlaces(likePlaces).build();

            final LikePlaceDeleteRequest request = new LikePlaceDeleteRequest(likePlaceStorage.getId(), likePlace1.getId());

            // when
            likePlaceStorageService.deleteLikePlace(request);
            final LikePlaceStorage likePlaceStorageAfterDelete = likePlaceStorageRepository.findById(likePlaceStorage.getId()).get();

            // then
            assertSoftly(softly -> {
                softly.assertThat(likePlaceStorageAfterDelete.getLikePlaces().size()).isEqualTo(1);
                softly.assertThat(likePlaceStorageAfterDelete.getLikePlaces().get(0).getId()).isEqualTo(likePlace2.getId());
            });
        }

        @Test
        @DisplayName("장소 보관함 ID에 해당하는 장소 보관함이 존재하지 않으면 예외가 발생한다.")
        void throws_not_exist_like_place_storage() {
            // given
            final LikePlace likePlace = likePlaceBuilder.defaultLikePlace(기본_회원_엔티티()).build();

            final Long notExistLikePlaceStorageId = -1L;
            final LikePlaceDeleteRequest request = new LikePlaceDeleteRequest(notExistLikePlaceStorageId, likePlace.getId());

            // when & then
            assertThatThrownBy(() -> likePlaceStorageService.deleteLikePlace(request))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessage("해당하는 장소 보관함을 찾을 수 없습니다.");
        }
    }
}
