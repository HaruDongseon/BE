package haru.harudongseon.member.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import haru.harudongseon.common.builder.MemberBuilder;
import haru.harudongseon.member.application.dto.MyProfileEditRequest;
import haru.harudongseon.member.application.dto.MyProfileResponse;
import haru.harudongseon.member.domain.Member;
import haru.harudongseon.member.domain.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class MemberServiceTest {

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberBuilder memberBuilder;

    @Autowired
    private MemberRepository memberRepository;

    @Nested
    @DisplayName("내 정보 조회 시")
    class FindMyProfile {

        @Test
        @DisplayName("내 정보 조회에 성공한다.")
        void success() {
            // given
            final Member member = memberBuilder.defaultMember().build();
            final Long memberId = member.getId();
            final MyProfileResponse expected = new MyProfileResponse(member.getEmail(), member.getNickname(), member.getProfileImageUrl());

            // when
            final MyProfileResponse actual = memberService.findMyProfile(memberId);

            // then
            assertThat(actual).isEqualTo(expected);
        }

        @Test
        @DisplayName("멤버 ID에 해당하는 멤버가 없으면 예외가 발생한다.")
        void throws_not_found_member() {
            // given
            final Long notExistMemberId = -1L;

            // when & then
            assertThatThrownBy(() -> memberService.findMyProfile(notExistMemberId))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessage("해당하는 멤버를 찾을 수 없습니다.");
        }
    }

    @Nested
    @DisplayName("내 정보 수정 시")
    class EditMyProfile {

        @Test
        @DisplayName("수정할 정보가 같은 정보라도 내 정보 수정에 성공한다.")
        void success_same_info_profile() {
            // given
            final Member member = memberBuilder.defaultMember().build();
            final Long memberId = member.getId();
            final String nickname = member.getNickname();
            final String profileImageUrl = member.getProfileImageUrl();
            final MyProfileEditRequest request = new MyProfileEditRequest(nickname, profileImageUrl);

            // when
            memberService.editMyProfile(memberId, request);
            final Member findMember = memberRepository.findById(memberId).get();

            // then
            assertThat(findMember.getNickname()).isEqualTo(nickname);
            assertThat(findMember.getProfileImageUrl()).isEqualTo(profileImageUrl);
        }

        @Test
        @DisplayName("닉네임만 수정 시 내 정보 수정에 성공한다.")
        void success_edit_nickname() {
            // given
            final Member member = memberBuilder.defaultMember().build();
            final Long memberId = member.getId();
            System.out.println("memberId = " + memberId);
            final String nicknameToEdit = "edited " + member.getNickname();
            final String profileImageUrl = member.getProfileImageUrl();
            final MyProfileEditRequest request = new MyProfileEditRequest(nicknameToEdit, profileImageUrl);

            // when
            memberService.editMyProfile(memberId, request);
            final Member findMember = memberRepository.findById(memberId).get();

            // then
            assertThat(findMember.getNickname()).isEqualTo(nicknameToEdit);
            assertThat(findMember.getProfileImageUrl()).isEqualTo(profileImageUrl);
        }

        @Test
        @DisplayName("프로필 이미지 URL만 수정 시 내 정보 수정에 성공한다.")
        void success_edit_profile_image_url() {
            // given
            final Member member = memberBuilder.defaultMember().build();
            final Long memberId = member.getId();
            final String nickname = member.getNickname();
            final String profileImageUrlToEdit = "edited " + member.getProfileImageUrl();
            final MyProfileEditRequest request = new MyProfileEditRequest(nickname, profileImageUrlToEdit);

            // when
            memberService.editMyProfile(memberId, request);
            final Member findMember = memberRepository.findById(memberId).get();

            // then
            assertThat(findMember.getNickname()).isEqualTo(nickname);
            assertThat(findMember.getProfileImageUrl()).isEqualTo(profileImageUrlToEdit);
        }

        @Test
        @DisplayName("모든 정보 수정 시 내 정보 수정에 성공한다.")
        void success_edit_profile_all() {
            // given
            final Member member = memberBuilder.defaultMember().build();
            final Long memberId = member.getId();
            final String nicknameToEdit = "edited " + member.getNickname();
            final String profileImageUrlToEdit = "edited " + member.getProfileImageUrl();
            final MyProfileEditRequest request = new MyProfileEditRequest(nicknameToEdit, profileImageUrlToEdit);

            // when
            memberService.editMyProfile(memberId, request);
            final Member findMember = memberRepository.findById(memberId).get();

            // then
            assertThat(findMember.getNickname()).isEqualTo(nicknameToEdit);
            assertThat(findMember.getProfileImageUrl()).isEqualTo(profileImageUrlToEdit);
        }

        @Test
        @DisplayName("멤버 ID에 해당하는 멤버가 없으면 예외가 발생한다.")
        void throws_not_found_member() {
            // given
            final Long notExistMemberId = -1L;
            final String nicknameToEdit = "edited Nickname";
            final String profileImageUrlToEdit = "edited ProfileImageUrl";
            final MyProfileEditRequest request = new MyProfileEditRequest(nicknameToEdit, profileImageUrlToEdit);

            // when & then
            assertThatThrownBy(() -> memberService.editMyProfile(notExistMemberId, request))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessage("해당하는 멤버를 찾을 수 없습니다.");
        }
    }
}
