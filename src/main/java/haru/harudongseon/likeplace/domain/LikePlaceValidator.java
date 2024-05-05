package haru.harudongseon.likeplace.domain;

import java.util.List;

import haru.harudongseon.likeplace.exception.LikePlaceException;
import org.springframework.stereotype.Component;

@Component
public class LikePlaceValidator {

    public void validatePhotoDuplicate(final LikePlace likePlace) {
        final List<String> photoReferences = likePlace.getPhotoReferences();
        final int originalSize = photoReferences.size();
        final int removeDuplicateSize = photoReferences.stream()
                .distinct()
                .toList()
                .size();

        if (originalSize != removeDuplicateSize) {
            throw new LikePlaceException.PhotoDuplicateException();
        }
    }
}
