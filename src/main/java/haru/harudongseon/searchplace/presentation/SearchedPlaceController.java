package haru.harudongseon.searchplace.presentation;

import haru.harudongseon.global.exception.ErrorResponse;
import haru.harudongseon.global.mvc.AuthMemberDto;
import haru.harudongseon.global.mvc.AuthPrincipal;
import haru.harudongseon.searchplace.application.SearchedPlaceService;
import haru.harudongseon.searchplace.application.dto.SearchedPlaceAddRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "SEARCHED PLACE API", description = "검색한 장소 관련 API")
@RestController
@RequestMapping("/searched-places")
@RequiredArgsConstructor
public class SearchedPlaceController {

    private final SearchedPlaceService searchedPlaceService;

    @Operation(summary = "검색한 장소 추가 API")
    @ApiResponse(responseCode = "200", description = "검색한 장소 추가 성공")
    @ApiResponse(responseCode = "400", description = "요청 Field Error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "멤버 Not Found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @PostMapping
    public ResponseEntity<Void> addSearchedPlace(
            @Parameter(hidden = true) @AuthPrincipal AuthMemberDto authMemberDto,
            @Valid @RequestBody SearchedPlaceAddRequest request
    ) {
        final Long memberId = authMemberDto.memberId();
        searchedPlaceService.addSearchedPlace(memberId, request);
        return ResponseEntity.ok().build();
    }
}
