package haru.harudongseon.routestorage.presentation;

import java.net.URI;

import haru.harudongseon.global.exception.ErrorResponse;
import haru.harudongseon.global.mvc.AuthMemberDto;
import haru.harudongseon.global.mvc.AuthPrincipal;
import haru.harudongseon.routestorage.application.RouteStorageService;
import haru.harudongseon.routestorage.application.dto.*;
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

@Tag(name = "ROUTE STORAGE API", description = "동선 보관함 관련 API")
@RestController
@RequestMapping("/route-storages")
@RequiredArgsConstructor
public class RouteStorageController {

    private final RouteStorageService routeStorageService;

    @Operation(summary = "동선 보관함 추가 API")
    @ApiResponse(responseCode = "201", description = "동선 보관함 추가 성공", headers = @Header(name = "Location", description = "생성된 동선 보관함 페이지(ID로 이동)"))
    @ApiResponse(responseCode = "400", description = "요청 Field Error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "401", description = "인증 실패(토큰 오류)", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "멤버 Not Found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @PostMapping
    public ResponseEntity<Void> addLikePlaceStorage(
            @Parameter(hidden = true) @AuthPrincipal AuthMemberDto authMemberDto,
            @Valid @RequestBody RouteStorageAddRequest request
    ) {
        final Long memberId = authMemberDto.memberId();
        final Long routeStorageId = routeStorageService.addRouteStorage(memberId, request);
        return ResponseEntity.created(URI.create("/route-storages/" + routeStorageId)).build();
    }

    @Operation(summary = "동선 보관함 동선 삭제 API")
    @ApiResponse(responseCode = "204", description = "장소 보관함 보관 장소 삭제 성공")
    @ApiResponse(responseCode = "400", description = "요청 Field Error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "401", description = "인증 실패(토큰 오류)", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "멤버/동선 Not Found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @DeleteMapping("/routes")
    public ResponseEntity<Void> deleteRoutes(
            @Valid @RequestBody StoredRouteDeleteRequest request
    ) {
        routeStorageService.deleteRouteStorage(request);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "동선 보관함 이름 조회 API")
    @ApiResponse(responseCode = "200", description = "동선 보관함 이름 조회 성공")
    @ApiResponse(responseCode = "401", description = "인증 실패(토큰 오류)", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "멤버 Not Found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @GetMapping("/names")
    public ResponseEntity<RouteStoragesResponse> findAllRouteStorageName(
            @Parameter(hidden = true) @AuthPrincipal AuthMemberDto authMemberDto
    ) {
        final Long memberId = authMemberDto.memberId();
        final RouteStoragesResponse response = routeStorageService.findRouteStorageNames(memberId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "동선 보관함 동선 조회 API")
    @ApiResponse(responseCode = "200", description = "동선 보관함 동선 조회 성공")
    @ApiResponse(responseCode = "401", description = "인증 실패(토큰 오류)", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "동선 보관함 Not Found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @GetMapping("/{route-storage-id}/routes")
    public ResponseEntity<StoredRoutesResponse> findAllRoutes(
            @Parameter(hidden = true) @AuthPrincipal AuthMemberDto authMemberDto,
            @PathVariable("route-storage-id") Long routeStorageId

    ) {
        final Long memberId = authMemberDto.memberId();
        final StoredRoutesResponse response = routeStorageService.findRoutes(routeStorageId, memberId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "동선 보관함 동선 추가 API")
    @ApiResponse(responseCode = "200", description = "동선 보관함 동선 추가 성공")
    @ApiResponse(responseCode = "400", description = "요청 Field Error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "401", description = "인증 실패(토큰 오류)", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "동선 보관함/동선 Not Found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @PostMapping("/{route-storage-id}/routes")
    public ResponseEntity<Void> addRoutes(
            @PathVariable("route-storage-id") Long routeStorageId,
            @Valid @RequestBody StoredRouteAddRequest request
    ) {
        routeStorageService.addRoutes(routeStorageId, request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "동선 보관함 동선 이동 API")
    @ApiResponse(responseCode = "200", description = "동선 보관함 동선 이동 성공")
    @ApiResponse(responseCode = "400", description = "요청 Field Error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "401", description = "인증 실패(토큰 오류)", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "동선 보관함/동선 Not Found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @PostMapping("/{route-storage-id}/move-routes")
    public ResponseEntity<Void> moveRoutes(
            @PathVariable("route-storage-id") Long routeStorageId,
            @Valid @RequestBody StoredRouteMoveRequest request
    ) {
        routeStorageService.moveRoutes(routeStorageId, request);
        return ResponseEntity.ok().build();
    }
}
