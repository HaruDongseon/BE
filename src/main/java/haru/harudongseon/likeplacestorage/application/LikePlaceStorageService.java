package haru.harudongseon.likeplacestorage.application;

import java.util.List;

import haru.harudongseon.likeplacestorage.application.dto.LikePlaceDeleteRequest;
import haru.harudongseon.likeplacestorage.application.dto.LikePlaceStorageAddRequest;
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

        likePlaceIds.forEach(likePlaceStorage::removeLikePlace);
    }
}
