package haru.harudongseon.likeplace.domain;

import haru.harudongseon.global.BaseEntity;
import haru.harudongseon.likeplace.domain.placedetails.PlaceDetails;
import haru.harudongseon.member.domain.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class LikePlace extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private Member member;

    private String providerPlaceId;
    private String name;

    @Embedded
    private PhotoReferences photoReferences;

    @Embedded
    private Coordinates coordinates;

    private String openingHours;
    private String addressName;
    private String phoneNumber;
    private String website;
    private String url;

    @Embedded
    private PlaceDetails placeDetails;

    public LikePlace(final Member member, final String providerPlaceId,
                     final String name, final PhotoReferences photoReferences,
                     final Coordinates coordinates, final String openingHours,
                     final String addressName, final String phoneNumber,
                     final String website, final String url,
                     final PlaceDetails placeDetails) {
        this.member = member;
        this.providerPlaceId = providerPlaceId;
        this.name = name;
        this.photoReferences = photoReferences;
        this.coordinates = coordinates;
        this.openingHours = openingHours;
        this.addressName = addressName;
        this.phoneNumber = phoneNumber;
        this.website = website;
        this.url = url;
        this.placeDetails = placeDetails;
    }
}
