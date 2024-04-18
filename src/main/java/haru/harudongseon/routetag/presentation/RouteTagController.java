package haru.harudongseon.routetag.presentation;

import haru.harudongseon.routetag.application.RouteTagService;
import haru.harudongseon.routetag.application.dto.RouteTagSearchResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "ROUTE TAG API", description = "동선 태그 관련 API")
@RestController
@RequestMapping("/route-tags")
@RequiredArgsConstructor
public class RouteTagController {

    private final RouteTagService routeTagService;

    @Operation(summary = "동선 태그 검색 API")
    @ApiResponse(responseCode = "200", description = "검색 성공")
    @GetMapping("/search")
    public ResponseEntity<RouteTagSearchResponse> searchByKeyword(@Parameter(description = "검색할 키워드", example = "데이트") @RequestParam("keyword") final String keyword) {
        final RouteTagSearchResponse response = routeTagService.searchByKeyword(keyword);
        return ResponseEntity.ok(response);
    }
}
