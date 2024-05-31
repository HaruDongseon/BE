package haru.harudongseon.likeplacestorage.presentation;

import java.net.URI;

import haru.harudongseon.global.exception.ErrorResponse;
import haru.harudongseon.global.mvc.AuthMemberDto;
import haru.harudongseon.global.mvc.AuthPrincipal;
import haru.harudongseon.likeplacestorage.application.LikePlaceStorageService;
import haru.harudongseon.likeplacestorage.application.dto.LikePlaceDeleteRequest;
import haru.harudongseon.likeplacestorage.application.dto.LikePlaceStorageAddRequest;
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

@Tag(name = "LIKE PLACE STORAGE API", description = "장소 보관함 관련 API")
@RestController
@RequestMapping("/like-place-storage")
@RequiredArgsConstructor
public class LikePlaceStorageController {

    private final LikePlaceStorageService likePlaceStorageService;

    @Operation(summary = "장소 보관함 추가 API")
    @ApiResponse(responseCode = "201", description = "장소 보관함 추가 성공", headers = @Header(name = "Location", description = "생성된 장소 보관함 페이지(ID로 이동)"))
    @ApiResponse(responseCode = "400", description = "요청 Field Error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "멤버 Not Found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @PostMapping
    public ResponseEntity<Void> addLikePlaceStorage(
            @Parameter(hidden = true) @AuthPrincipal AuthMemberDto authMemberDto,
            @Valid @RequestBody LikePlaceStorageAddRequest request
    ) {
        final Long memberId = authMemberDto.memberId();
        final Long likePlaceStorageId = likePlaceStorageService.addLikePlaceStorage(memberId, request);
        return ResponseEntity.created(URI.create("/like-place-storage/" + likePlaceStorageId)).build();
    }

    @Operation(summary = "장소 보관함 보관 장소 삭제 API")
    @ApiResponse(responseCode = "204", description = "장소 보관함 보관 장소 삭제 성공")
    @ApiResponse(responseCode = "400", description = "요청 Field Error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "멤버/보관 장소 Not Found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @DeleteMapping("/like-places")
    public ResponseEntity<Void> deleteLikePlace(
            @Valid @RequestBody LikePlaceDeleteRequest request
    ) {
        likePlaceStorageService.deleteLikePlace(request);
        return ResponseEntity.noContent().build();
    }
}
