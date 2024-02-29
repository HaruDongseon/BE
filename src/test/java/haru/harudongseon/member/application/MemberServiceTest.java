package haru.harudongseon.member.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import haru.harudongseon.common.builder.MemberBuilder;
import haru.harudongseon.member.application.dto.MyProfileResponse;
import haru.harudongseon.member.domain.Member;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class MemberServiceTest {

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberBuilder memberBuilder;


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
}
