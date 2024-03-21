package haru.harudongseon.place.application;

import java.util.Optional;

import haru.harudongseon.place.application.dto.PlaceAddRequest;
import haru.harudongseon.place.domain.Place;
import haru.harudongseon.place.domain.PlaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class PlaceService {

    private final PlaceRepository placeRepository;

    public void addPlace(final PlaceAddRequest request) {
        final Optional<Place> optionalPlace = placeRepository.findByProviderPlaceId(request.providerPlaceId());
        if (optionalPlace.isPresent()) {
            final Place place = optionalPlace.get();
            place.select();
        }

        if (optionalPlace.isEmpty()) {
            final Place place = new Place(request.providerPlaceId(), request.name(), request.category(),
                    request.latitude(), request.longitude(), request.addressName());
            placeRepository.save(place);
        }
    }
}
