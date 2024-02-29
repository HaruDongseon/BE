package haru.harudongseon.member.presentation;

import haru.harudongseon.global.mvc.AuthMemberDto;
import haru.harudongseon.global.mvc.AuthPrincipal;
import haru.harudongseon.member.application.MemberService;
import haru.harudongseon.member.application.dto.MyProfileEditRequest;
import haru.harudongseon.member.application.dto.MyProfileResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PatchMapping("/me")
    public ResponseEntity<MyProfileResponse> editMyProfile(@AuthPrincipal AuthMemberDto authMemberDto,
                                                          @Valid @RequestBody MyProfileEditRequest request) {
        memberService.editMyProfile(authMemberDto.memberId(), request);
        return ResponseEntity.ok().build();
    }
}
