package haru.harudongseon.place.domain;

import java.util.List;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class PhotoReferences {

    private List<String> photoReferences;

    public PhotoReferences(final List<String> photoReferences) {
        this.photoReferences = photoReferences;
    }
}
