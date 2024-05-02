package haru.harudongseon.route.presentation;

import java.net.URI;

import haru.harudongseon.global.exception.ErrorResponse;
import haru.harudongseon.global.mvc.AuthMemberDto;
import haru.harudongseon.global.mvc.AuthPrincipal;
import haru.harudongseon.route.application.RouteService;
import haru.harudongseon.route.application.dto.RouteAddRequest;
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

@Tag(name = "ROUTE API", description = "동선 관련 API")
@RestController
@RequestMapping("/routes")
@RequiredArgsConstructor
public class RouteController {

    private final RouteService routeService;

    @Operation(summary = "동선 추가 API")
    @ApiResponse(responseCode = "201", description = "동선 추가 성공", headers = @Header(name = "Location", description = "생성된 동선 페이지(ID로 이동)"))
    @ApiResponse(responseCode = "400", description = "요청 Field Error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "멤버 Not Found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @PostMapping
    public ResponseEntity<Void> addRoute(
            @Parameter(hidden = true) @AuthPrincipal AuthMemberDto authMemberDto,
            @Valid @RequestBody RouteAddRequest request
    ) {
        final Long memberId = authMemberDto.memberId();
        final Long savedRouteId = routeService.addRoute(memberId, request);
        return ResponseEntity.created(URI.create("/routes/" + savedRouteId)).build();
    }
}
