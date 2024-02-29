package haru.harudongseon.member.presentation;

import haru.harudongseon.global.mvc.AuthMemberDto;
import haru.harudongseon.global.mvc.AuthPrincipal;
import haru.harudongseon.member.application.MemberService;
import haru.harudongseon.member.application.dto.MyProfileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/me")
    public ResponseEntity<MyProfileResponse> getMyProfile(@AuthPrincipal AuthMemberDto authMemberDto) {
        final MyProfileResponse response = memberService.findMyProfile(authMemberDto.memberId());
        return ResponseEntity.ok(response);
    }
}
