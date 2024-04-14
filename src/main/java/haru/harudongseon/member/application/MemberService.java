package haru.harudongseon.member.application;

import haru.harudongseon.global.fileupload.FileUploader;
import haru.harudongseon.member.application.dto.MemberImageSaveResponse;
import haru.harudongseon.member.application.dto.MyProfileEditRequest;
import haru.harudongseon.member.application.dto.MyProfileResponse;
import haru.harudongseon.member.domain.Member;
import haru.harudongseon.member.domain.MemberRepository;
import haru.harudongseon.member.exception.MemberException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final FileUploader fileUploader;

    @Transactional(readOnly = true)
    public MyProfileResponse findMyProfile(final Long memberId) {
        final Member findMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("해당하는 멤버를 찾을 수 없습니다."));

        final String email = findMember.getEmail();
        final String nickname = findMember.getNickname();
        final String profileImageUrl = findMember.getProfileImageUrl();

        return new MyProfileResponse(email, nickname, profileImageUrl);
    }

    public void editMyProfile(final Long memberId, final MyProfileEditRequest request) {
        final Member findMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("해당하는 멤버를 찾을 수 없습니다."));

        final String nickname = request.nickname();
        if (checkNicknameDuplicate(memberId, nickname)) {
            throw new MemberException.DuplicateNicknameException();
        }

        findMember.editProfile(request.nickname(), request.profileImageUrl());
    }

    private boolean checkNicknameDuplicate(final Long memberId, final String nickname) {
        return memberRepository.existsByIdNotAndNickname(memberId, nickname);
    }

    public MemberImageSaveResponse saveMemberImage(final Long memberId, final MultipartFile file) {
        final String memberImageName = "member" + memberId + "_image";
        final String uploadImageUrl = fileUploader.upload(file, memberImageName);
        return new MemberImageSaveResponse(uploadImageUrl);
    }
}
