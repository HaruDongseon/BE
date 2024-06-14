package haru.harudongseon.place.domain;

import java.util.Optional;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

public interface PlaceRepository extends JpaRepository<Place, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Place> findByProviderPlaceId(final String providerPlaceId);
}
