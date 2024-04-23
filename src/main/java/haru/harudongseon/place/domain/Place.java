package haru.harudongseon.place.domain;

import haru.harudongseon.global.BaseEntity;
import haru.harudongseon.place.domain.placedetails.PlaceDetails;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Place extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String providerPlaceId;
    private String name;

    @Embedded
    private PhotoReferences photoReferences;

    @Embedded
    private Coordinates coordinates;

    private String addressName;
    private String phoneNumber;
    private String website;
    private String url;

    @Embedded
    private PlaceDetails placeDetails;

    public Place(final String providerPlaceId, final String name,
                 final PhotoReferences photoReferences, final Coordinates coordinates,
                 final String addressName, final String phoneNumber,
                 final String website, final String url,
                 final PlaceDetails placeDetails) {
        this.providerPlaceId = providerPlaceId;
        this.name = name;
        this.photoReferences = photoReferences;
        this.coordinates = coordinates;
        this.addressName = addressName;
        this.phoneNumber = phoneNumber;
        this.website = website;
        this.url = url;
        this.placeDetails = placeDetails;
    }
}
