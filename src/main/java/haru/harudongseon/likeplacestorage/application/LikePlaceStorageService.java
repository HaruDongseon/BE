package haru.harudongseon.likeplacestorage.application;

import haru.harudongseon.likeplacestorage.application.dto.LikePlaceStorageAddRequest;
import haru.harudongseon.likeplacestorage.domain.LikePlaceStorage;
import haru.harudongseon.likeplacestorage.domain.LikePlaceStorageRepository;
import haru.harudongseon.likeplacestorage.exception.LikePlaceStorageException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class LikePlaceStorageService {

    private final LikePlaceStorageRepository likePlaceStorageRepository;

    public Long addLikePlaceStorage(final Long memberId, final LikePlaceStorageAddRequest request) {
        final String name = request.name();
        checkDuplicateLikePlaceStorage(memberId, name);

        final LikePlaceStorage savedLikePlaceStorage = likePlaceStorageRepository.save(new LikePlaceStorage(name, memberId));
        return savedLikePlaceStorage.getId();
    }

    private void checkDuplicateLikePlaceStorage(final Long memberId, final String name) {
        if (likePlaceStorageRepository.existsByMemberIdAndName(memberId, name)) {
            throw new LikePlaceStorageException.DuplicateException();
        }
    }
}
