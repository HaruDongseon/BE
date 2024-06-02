package haru.harudongseon.likeplacestorage.application;

import static haru.harudongseon.common.fixtures.LikePlaceStorageFixtures.기본_장소_보관함_이름;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.util.Collections;
import java.util.List;

import haru.harudongseon.common.ServiceTest;
import haru.harudongseon.common.builder.LikePlaceBuilder;
import haru.harudongseon.common.builder.LikePlaceStorageBuilder;
import haru.harudongseon.common.builder.MemberBuilder;
import haru.harudongseon.likeplace.domain.LikePlace;
import haru.harudongseon.likeplacestorage.application.dto.LikePlaceDeleteRequest;
import haru.harudongseon.likeplacestorage.application.dto.LikePlaceStorageAddRequest;
import haru.harudongseon.likeplacestorage.application.dto.LikePlaceStoragesResponse;
import haru.harudongseon.likeplacestorage.domain.LikePlaceStorage;
import haru.harudongseon.likeplacestorage.domain.LikePlaceStorageRepository;
import haru.harudongseon.likeplacestorage.exception.LikePlaceStorageException;
import haru.harudongseon.member.domain.Member;
import jakarta.persistence.EntityNotFoundException;
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
            assertThat(savedLikePlaceStorageId).isNotNull();
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
            final LikePlaceStorage existLikePlaceStorage = likePlaceStorageBuilder.defaultLikePlaceStorage(member).build(Collections.emptyList());

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
            final Member member = memberBuilder.defaultMember().build();
            final LikePlace likePlace1 = likePlaceBuilder.defaultLikePlace(member).photoReferences(List.of("reference1")).name("베이커리 성수").build();
            final LikePlace likePlace2 = likePlaceBuilder.defaultLikePlace(member).photoReferences(List.of("reference2")).name("스타벅스 성수점").build();
            final LikePlace likePlace3 = likePlaceBuilder.defaultLikePlace(member).photoReferences(List.of("reference3")).name("성수건설").build();
            final LikePlaceStorage likePlaceStorage = likePlaceStorageBuilder.defaultLikePlaceStorage(member).build(List.of(likePlace1, likePlace2, likePlace3));
            final LikePlaceDeleteRequest request = new LikePlaceDeleteRequest(likePlaceStorage.getId(), List.of(likePlace1.getId(), likePlace2.getId()));

            // when
            likePlaceStorageService.deleteLikePlace(request);
            final LikePlaceStorage likePlaceStorageAfterDelete = likePlaceStorageRepository.findById(likePlaceStorage.getId()).get();

            // then
            assertSoftly(softly -> {
                softly.assertThat(likePlaceStorageAfterDelete.getLikePlaces().size()).isEqualTo(1);
                softly.assertThat(likePlaceStorageAfterDelete.getLikePlaces().get(0).getId()).isEqualTo(likePlace3.getId());
            });
        }

        @Test
        @DisplayName("장소 보관함 ID에 해당하는 장소 보관함이 존재하지 않으면 예외가 발생한다.")
        void throws_not_exist_like_place_storage() {
            // given
            final Member member = memberBuilder.defaultMember().build();
            final LikePlace likePlace = likePlaceBuilder.defaultLikePlace(member).build();

            final Long notExistLikePlaceStorageId = -1L;
            final LikePlaceDeleteRequest request = new LikePlaceDeleteRequest(notExistLikePlaceStorageId, List.of(likePlace.getId()));

            // when & then
            assertThatThrownBy(() -> likePlaceStorageService.deleteLikePlace(request))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessage("해당하는 장소 보관함을 찾을 수 없습니다.");
        }
    }

    @Nested
    @DisplayName("장소 보관함 조회 시")
    class FindLikePlaceStorages {

        @Test
        @DisplayName("조회에 성공한다.")
        void success() {
            // given
            final Member member = memberBuilder.defaultMember().build();
            final LikePlace likePlace1 = likePlaceBuilder.defaultLikePlace(member).photoReferences(List.of("reference1")).name("베이커리 성수").build();
            final LikePlace likePlace2 = likePlaceBuilder.defaultLikePlace(member).photoReferences(List.of("reference2")).name("스타벅스 성수점").build();
            final LikePlaceStorage likePlaceStorage1 = likePlaceStorageBuilder.defaultLikePlaceStorage(member).name("베이커리").build(List.of(likePlace1));
            final LikePlaceStorage likePlaceStorage2 = likePlaceStorageBuilder.defaultLikePlaceStorage(member).name("카페").build(List.of(likePlace2));

            final LikePlaceStoragesResponse expected = LikePlaceStoragesResponse.from(List.of(likePlaceStorage1, likePlaceStorage2));

            // when
            final LikePlaceStoragesResponse actual = likePlaceStorageService.findLikePlaceStorages(member.getId());

            // then
            assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
        }

        @Test
        @DisplayName("장소 보관함이 없을 경우에는 빈 리스트로 조회된다.")
        void success_empty_list() {
            // given
            final Member member = memberBuilder.defaultMember().build();

            // when
            final LikePlaceStoragesResponse actual = likePlaceStorageService.findLikePlaceStorages(member.getId());

            // then
            assertThat(actual.getLikePlaceStorages()).isEmpty();
        }

        @Test
        @DisplayName("멤버 ID에 해당하는 멤버가 존재하지 않으면 예외가 발생한다.")
        void throws_not_exist_member() {
            // given
            final Long notExistMemberId = -1L;

            // when & then
            assertThatThrownBy(() -> likePlaceStorageService.findLikePlaceStorages(notExistMemberId))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessage("해당하는 멤버가 존재하지 않습니다.");
        }
    }
}
