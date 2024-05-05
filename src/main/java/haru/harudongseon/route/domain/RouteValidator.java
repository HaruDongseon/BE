package haru.harudongseon.route.domain;

import java.util.List;

import haru.harudongseon.route.exception.RouteException;
import org.springframework.stereotype.Component;

@Component
public class RouteValidator {

    public void validateDuplicateTag(final List<String> tagNames) {
        if (isDuplicate(tagNames)) {
            throw new RouteException.DuplicateTagException();
        }
    }

    public void validateDuplicatePlacePhotoReference(final List<String> placePhotoReferences) {
        if (isDuplicate(placePhotoReferences)) {
            throw new RouteException.DuplicateRoutePlacePhotoException();
        }
    }

    private boolean isDuplicate(final List<String> targets) {
        final int originalTargetSize = targets.size();
        final int removeDuplicateTargetSize = targets.stream()
                .distinct()
                .toList()
                .size();

        return originalTargetSize != removeDuplicateTargetSize;
    }
}
