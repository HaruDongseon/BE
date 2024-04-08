package haru.harudongseon.routeplace.presentation;

import haru.harudongseon.global.exception.ErrorResponse;
import haru.harudongseon.routeplace.application.RoutePlaceService;
import haru.harudongseon.routeplace.application.dto.RoutePlaceAddRequest;
import io.swagger.v3.oas.annotations.Operation;
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

@Tag(name = "PLACE API", description = "장소 관련 API")
@RestController
@RequestMapping("/places")
@RequiredArgsConstructor
public class RoutePlaceController {

    private final RoutePlaceService routePlaceService;

    @Operation(summary = "장소 추가 API")
    @ApiResponse(responseCode = "200", description = "장소 추가 성공")
    @ApiResponse(responseCode = "400", description = "요청 Field Error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @PostMapping
    public ResponseEntity<Void> addPlace(@RequestBody @Valid final RoutePlaceAddRequest request) {
        routePlaceService.addPlace(request);
        return ResponseEntity.ok().build();
    }
}
