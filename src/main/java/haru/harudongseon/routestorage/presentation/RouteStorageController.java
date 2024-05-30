package haru.harudongseon.routestorage.presentation;

import java.net.URI;

import haru.harudongseon.global.exception.ErrorResponse;
import haru.harudongseon.global.mvc.AuthMemberDto;
import haru.harudongseon.global.mvc.AuthPrincipal;
import haru.harudongseon.routestorage.application.RouteStorageService;
import haru.harudongseon.routestorage.application.dto.RouteStorageAddRequest;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "ROUTE STORAGE API", description = "동선 보관함 관련 API")
@RestController
@RequestMapping("/route-storage")
@RequiredArgsConstructor
public class RouteStorageController {

    private final RouteStorageService routeStorageService;

    @Operation(summary = "동선 보관함 추가 API")
    @ApiResponse(responseCode = "201", description = "동선 보관함 추가 성공", headers = @Header(name = "Location", description = "생성된 동선 보관함 페이지(ID로 이동)"))
    @ApiResponse(responseCode = "400", description = "요청 Field Error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "멤버 Not Found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @PostMapping
    public ResponseEntity<Void> addLikePlaceStorage(
            @Parameter(hidden = true) @AuthPrincipal AuthMemberDto authMemberDto,
            @Valid @RequestBody RouteStorageAddRequest request
    ) {
        final Long memberId = authMemberDto.memberId();
        final Long routeStorageId = routeStorageService.addRouteStorage(memberId, request);
        return ResponseEntity.created(URI.create("/route-storage/" + routeStorageId)).build();
    }
}
