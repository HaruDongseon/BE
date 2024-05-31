package haru.harudongseon.place.domain;

import java.util.ArrayList;
import java.util.List;

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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String providerPlaceId;
    private String name;
    private String category;

    @ElementCollection
    @Column(unique = true)
    private List<String> photoReferences = new ArrayList<>();

    @Embedded
    private Coordinates coordinates;

    private String openingHours;
    private String addressName;
    private String phoneNumber;
    private String website;
    private String googleMapsUri;

    @Embedded
    private PlaceDetails placeDetails;

    public Place(final Long id, final String providerPlaceId,
                 final String name, final String category,
                 final List<String> photoReferences, final Coordinates coordinates,
                 final String openingHours, final String addressName,
                 final String phoneNumber, final String website,
                 final String googleMapsUri, final PlaceDetails placeDetails) {
        this.id = id;
        this.providerPlaceId = providerPlaceId;
        this.name = name;
        this.category = category;
        this.photoReferences = photoReferences;
        this.coordinates = coordinates;
        this.openingHours = openingHours;
        this.addressName = addressName;
        this.phoneNumber = phoneNumber;
        this.website = website;
        this.googleMapsUri = googleMapsUri;
        this.placeDetails = placeDetails;
    }

    public Place(final String providerPlaceId, final String name,
                 final String category, final List<String> photoReferences,
                 final Coordinates coordinates, final String openingHours,
                 final String addressName, final String phoneNumber,
                 final String website, final String googleMapsUri,
                 final PlaceDetails placeDetails) {
        this.providerPlaceId = providerPlaceId;
        this.name = name;
        this.category = category;
        this.photoReferences = photoReferences;
        this.coordinates = coordinates;
        this.openingHours = openingHours;
        this.addressName = addressName;
        this.phoneNumber = phoneNumber;
        this.website = website;
        this.googleMapsUri = googleMapsUri;
        this.placeDetails = placeDetails;
    }
}
