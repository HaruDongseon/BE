package haru.harudongseon.member.presentation;

import haru.harudongseon.global.exception.ErrorResponse;
import haru.harudongseon.global.mvc.AuthMemberDto;
import haru.harudongseon.global.mvc.AuthPrincipal;
import haru.harudongseon.member.application.MemberService;
import haru.harudongseon.member.application.dto.MyProfileEditRequest;
import haru.harudongseon.member.application.dto.MyProfileResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "MEMBER API", description = "회원 관련 API")
@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @Operation(summary = "내 정보 조회 API")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @ApiResponse(responseCode = "400", description = "요청 Field Error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "멤버 Not Found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @GetMapping("/me")
    public ResponseEntity<MyProfileResponse> getMyProfile(
            @Parameter(hidden = true) @AuthPrincipal AuthMemberDto authMemberDto) {
        final MyProfileResponse response = memberService.findMyProfile(authMemberDto.memberId());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "내 정보 수정 API")
    @ApiResponse(responseCode = "200", description = "수정 성공")
    @ApiResponse(responseCode = "404", description = "멤버 Not Found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @PatchMapping("/me")
    public ResponseEntity<Void> editMyProfile(@Parameter(hidden = true) @AuthPrincipal AuthMemberDto authMemberDto,
                                                          @Valid @RequestBody MyProfileEditRequest request) {
        memberService.editMyProfile(authMemberDto.memberId(), request);
        return ResponseEntity.ok().build();
    }
}
