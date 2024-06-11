package haru.harudongseon.likeplacestorage.application;

import java.util.List;

import haru.harudongseon.likeplace.domain.LikePlaceRepository;
import haru.harudongseon.likeplacestorage.application.dto.*;
import haru.harudongseon.likeplacestorage.domain.LikePlaceStorage;
import haru.harudongseon.likeplacestorage.domain.LikePlaceStorageRepository;
import haru.harudongseon.likeplacestorage.exception.LikePlaceStorageException;
import haru.harudongseon.member.domain.Member;
import haru.harudongseon.member.domain.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class LikePlaceStorageService {

    private final MemberRepository memberRepository;
    private final LikePlaceStorageRepository likePlaceStorageRepository;
    private final LikePlaceRepository likePlaceRepository;

    public Long addLikePlaceStorage(final Long memberId, final LikePlaceStorageAddRequest request) {
        final Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("해당하는 멤버가 존재하지 않습니다."));

        final String name = request.name();
        checkDuplicateLikePlaceStorage(memberId, name);

        final LikePlaceStorage savedLikePlaceStorage = likePlaceStorageRepository.save(new LikePlaceStorage(name, member));
        return savedLikePlaceStorage.getId();
    }

    private void checkDuplicateLikePlaceStorage(final Long memberId, final String name) {
        if (likePlaceStorageRepository.existsByMemberIdAndName(memberId, name)) {
            throw new LikePlaceStorageException.DuplicateException();
        }
    }

    public void deleteLikePlace(final LikePlaceDeleteRequest request) {
        final Long likePlaceStorageId = request.likePlaceStorageId();
        final List<Long> likePlaceIds = request.likePlaceIds();

        final LikePlaceStorage likePlaceStorage = likePlaceStorageRepository.findById(likePlaceStorageId)
                .orElseThrow(() -> new EntityNotFoundException("해당하는 장소 보관함을 찾을 수 없습니다."));

        likePlaceStorage.removeLikePlaces(likePlaceIds);
    }

    @Transactional(readOnly = true)
    public LikePlaceStoragesResponse findLikePlaceStorageNames(final Long memberId) {
        final Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("해당하는 멤버가 존재하지 않습니다."));

        final List<LikePlaceStorage> likePlaceStorages = likePlaceStorageRepository.findAllByMemberId(member.getId());
        return LikePlaceStoragesResponse.from(likePlaceStorages);
    }

    @Transactional(readOnly = true)
    public StoredLikePlacesResponse findLikePlaces(final Long likePlaceStorageId, final Long memberId) {
        final LikePlaceStorage likePlaceStorage = likePlaceStorageRepository.findByIdAndMemberId(likePlaceStorageId, memberId)
                .orElseThrow(() -> new EntityNotFoundException("장소 보관함 ID와 멤버 ID에 해당하는 장소 보관함이 존재하지 않습니다."));

        return StoredLikePlacesResponse.from(likePlaceStorage.getLikePlaces());
    }

    public void addLikePlaces(final Long likePlaceStorageId, final LikePlaceAddRequest request) {
        final LikePlaceStorage likePlaceStorage = likePlaceStorageRepository.findById(likePlaceStorageId)
                .orElseThrow(() -> new EntityNotFoundException("해당하는 장소 보관함을 찾을 수 없습니다."));

        final List<Long> likePlaceIds = request.likePlaceIds();
        validateAlreadyExist(likePlaceIds, likePlaceStorage);

        likePlaceIds.stream()
                .map(likePlaceId -> likePlaceRepository.findById(likePlaceId)
                        .orElseThrow(() -> new EntityNotFoundException("해당하는 보관 장소가 존재하지 않습니다."))
                ).forEach(likePlaceStorage::addLikePlace);
    }

    private void validateAlreadyExist(final List<Long> likePlaceIds, final LikePlaceStorage likePlaceStorage) {
        for (Long likePlaceId : likePlaceIds) {
            if (likePlaceStorage.isAlreadyExistLikePlace(likePlaceId)) {
                throw new LikePlaceStorageException.AlreadyExistLikePlaceException();
            }
        }
    }
}
