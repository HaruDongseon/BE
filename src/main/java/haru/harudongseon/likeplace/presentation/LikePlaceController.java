package haru.harudongseon.likeplace.presentation;

import java.net.URI;

import haru.harudongseon.global.exception.ErrorResponse;
import haru.harudongseon.global.mvc.AuthMemberDto;
import haru.harudongseon.global.mvc.AuthPrincipal;
import haru.harudongseon.likeplace.application.LikePlaceService;
import haru.harudongseon.likeplace.application.dto.LikePlaceAddRequest;
import haru.harudongseon.likeplace.application.dto.LikePlaceResponse;
import haru.harudongseon.likeplace.application.dto.RecentLikePlacesResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "LIKE PLACE API", description = "보관 장소 관련 API")
@RestController
@RequestMapping("/like-places")
@RequiredArgsConstructor
public class LikePlaceController {

    private final LikePlaceService likePlaceService;

    @Operation(summary = "보관 장소 추가 API")
    @ApiResponse(responseCode = "201", description = "보관 장소 추가 성공", headers = @Header(name = "Location", description = "생성된 보관 장소 상세 페이지(ID로 이동)"))
    @ApiResponse(responseCode = "400", description = "요청 Field Error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "멤버 Not Found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @PostMapping
    public ResponseEntity<Void> addSearchedPlace(
            @Parameter(hidden = true) @AuthPrincipal AuthMemberDto authMemberDto,
            @Valid @RequestBody LikePlaceAddRequest request
    ) {
        final Long memberId = authMemberDto.memberId();
        final Long likePlaceId = likePlaceService.addLikePlace(memberId, request);
        return ResponseEntity.created(URI.create("/like-places/" + likePlaceId)).build();
    }

    @Operation(summary = "보관 장소 조회 API")
    @ApiResponse(responseCode = "200", description = "보관 장소 조회 성공")
    @ApiResponse(responseCode = "404", description = "보관 장소 Not Found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @GetMapping("/{like-place-id}")
    public ResponseEntity<LikePlaceResponse> findLikePlace(
            @PathVariable("like-place-id") Long likePlaceId
    ) {
        final LikePlaceResponse response = likePlaceService.findLikePlace(likePlaceId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "최근 보관 장소 조회 API")
    @ApiResponse(responseCode = "200", description = "최근 보관 장소 조회 성공")
    @GetMapping("/recent")
    public ResponseEntity<RecentLikePlacesResponse> findRecentLikePlace(
            @Parameter(hidden = true) @AuthPrincipal AuthMemberDto authMemberDto
    ) {
        final RecentLikePlacesResponse response = likePlaceService.findRecentLikePlace(authMemberDto.memberId());
        return ResponseEntity.ok(response);
    }
}
