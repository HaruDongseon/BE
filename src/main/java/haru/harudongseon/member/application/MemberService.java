package haru.harudongseon.member.application;

import haru.harudongseon.member.application.dto.MyProfileResponse;
import haru.harudongseon.member.domain.Member;
import haru.harudongseon.member.domain.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    @Transactional(readOnly = true)
    public MyProfileResponse findMyProfile(Long memberId) {
        final Member findMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("해당하는 멤버를 찾을 수 없습니다."));

        final String email = findMember.getEmail();
        final String nickname = findMember.getNickname();
        final String profileImageUrl = findMember.getProfileImageUrl();
        return new MyProfileResponse(email, nickname, profileImageUrl);
    }
}
