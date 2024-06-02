package haru.harudongseon.likeplacestorage.application.dto;

import haru.harudongseon.likeplacestorage.domain.LikePlaceStorage;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class LikePlaceStorageResponse {

    @Schema(description = "장소 보관함 ID", example = "1")
    private Long id;

    @Schema(description = "장소 보관함 이름", example = "카페")
    private String name;

    private LikePlaceStorageResponse(final Long id, final String name) {
        this.id = id;
        this.name = name;
    }

    public static LikePlaceStorageResponse from(final LikePlaceStorage likePlaceStorage) {
        final Long id = likePlaceStorage.getId();
        final String name = likePlaceStorage.getName();

        return new LikePlaceStorageResponse(id, name);
    }
}
