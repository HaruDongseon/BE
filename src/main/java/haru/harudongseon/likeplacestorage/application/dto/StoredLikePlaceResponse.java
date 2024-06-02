package haru.harudongseon.likeplacestorage.application.dto;

import java.util.List;

import haru.harudongseon.likeplace.domain.LikePlace;
import haru.harudongseon.likeplacestorage.domain.StoredLikePlace;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StoredLikePlaceResponse {

    @Schema(description = "보관 장소 ID", example = "1")
    private Long id;

    @Schema(description = "보관 장소 이름", example = "스타벅스 부평점")
    private String name;

    @Schema(description = "보관 장소 주소 이름", example = "인천광역시 부평구 경원대로 1397")
    private String addressName;

    @Schema(description = "구글 장소 이미지 요청 Reference 리스트", example = "[\"reference1\", \"reference2\", \"reference3\"]")
    private List<String> photoReferences;

    private StoredLikePlaceResponse(final Long id, final String name,
                                    final String addressName, final List<String> photoReferences) {
        this.id = id;
        this.name = name;
        this.addressName = addressName;
        this.photoReferences = photoReferences;
    }

    public static StoredLikePlaceResponse from(final StoredLikePlace storedLikePlace) {
        final LikePlace likePlace = storedLikePlace.getLikePlace();
        final Long id = likePlace.getId();
        final String name = likePlace.getName();
        final String addressName = likePlace.getAddressName();
        final List<String> photoReferences = likePlace.getPhotoReferences();

        return new StoredLikePlaceResponse(id, name, addressName, photoReferences);
    }
}
