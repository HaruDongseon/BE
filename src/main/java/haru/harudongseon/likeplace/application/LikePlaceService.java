package haru.harudongseon.likeplace.application;

import haru.harudongseon.likeplace.application.dto.LikePlaceAddRequest;
import haru.harudongseon.likeplace.domain.LikePlace;
import haru.harudongseon.likeplace.domain.LikePlaceRepository;
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

    public Long addLikePlace(final Long memberId, final LikePlaceAddRequest request) {
        final Member findMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("해당하는 멤버를 찾을 수 없습니다."));
        final LikePlace likePlace = request.toEntity(findMember);
        final LikePlace savedLikePlace = likePlaceRepository.save(likePlace);
        return savedLikePlace.getId();
    }
}
