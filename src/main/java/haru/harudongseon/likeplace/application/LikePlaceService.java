package haru.harudongseon.likeplace.application;

import java.util.List;

import haru.harudongseon.likeplace.application.dto.LikePlaceAddRequest;
import haru.harudongseon.likeplace.application.dto.LikePlaceResponse;
import haru.harudongseon.likeplace.application.dto.RecentLikePlacesResponse;
import haru.harudongseon.likeplace.domain.LikePlace;
import haru.harudongseon.likeplace.domain.LikePlaceRepository;
import haru.harudongseon.likeplace.domain.LikePlaceValidator;
import haru.harudongseon.member.domain.Member;
import haru.harudongseon.member.domain.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class LikePlaceService {

    private final MemberRepository memberRepository;
    private final LikePlaceRepository likePlaceRepository;
    private final LikePlaceValidator likePlaceValidator;

    public Long addLikePlace(final Long memberId, final LikePlaceAddRequest request) {
        final Member findMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("해당하는 멤버를 찾을 수 없습니다."));
        final LikePlace likePlace = request.toEntity(findMember);
        likePlaceValidator.validatePhotoDuplicate(likePlace);
        final LikePlace savedLikePlace = likePlaceRepository.save(likePlace);
        return savedLikePlace.getId();
    }

    public LikePlaceResponse findLikePlace(final Long likePlaceId) {
        final LikePlace findLikePlace = likePlaceRepository.findById(likePlaceId)
                .orElseThrow(() -> new EntityNotFoundException("해당하는 보관 장소를 찾을 수 없습니다."));

        return LikePlaceResponse.from(findLikePlace);
    }

    public RecentLikePlacesResponse findRecentLikePlace(final Long memberId) {
        final List<LikePlace> likePlaces = likePlaceRepository.findRecentThreeByMemberId(memberId);
        return RecentLikePlacesResponse.from(likePlaces);
    }
}
