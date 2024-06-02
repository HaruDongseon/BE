package haru.harudongseon.likeplacestorage.application.dto;

import java.util.List;

import haru.harudongseon.likeplacestorage.domain.LikePlaceStorage;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class LikePlaceStoragesResponse {

    private List<LikePlaceStorageResponse> likePlaceStorages;

    private LikePlaceStoragesResponse(final List<LikePlaceStorageResponse> likePlaceStorages) {
        this.likePlaceStorages = likePlaceStorages;
    }

    public static LikePlaceStoragesResponse from(final List<LikePlaceStorage> likePlaceStorages) {
        final List<LikePlaceStorageResponse> likePlaceStorageResponses = likePlaceStorages.stream()
                .map(LikePlaceStorageResponse::from)
                .toList();

        return new LikePlaceStoragesResponse(likePlaceStorageResponses);
    }
}
