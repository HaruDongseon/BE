package haru.harudongseon.route.domain;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import haru.harudongseon.global.BaseEntity;
import haru.harudongseon.member.domain.Member;
import haru.harudongseon.place.domain.Place;
import haru.harudongseon.routetag.domain.RouteTag;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Route extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private Member member;

    private LocalDate date;
    private String title;

    @OneToMany(mappedBy = "route")
    private Set<SelectedTag> tags = new HashSet<>();

    private String moveWays;

    @OneToMany(mappedBy = "route")
    private Set<RoutePlace> routePlaces = new HashSet<>();

    public Route(final Member member, final LocalDate date,
                 final String title, final String moveWays) {
        this.member = member;
        this.date = date;
        this.title = title;
        this.moveWays = moveWays;
    }

    public SelectedTag addTag(final RouteTag routeTag) {
        final SelectedTag selectedTag = new SelectedTag();
        selectedTag.associate(this, routeTag);
        return selectedTag;
    }

    public RoutePlace addPlace(final Place place) {
        final RoutePlace routePlace = new RoutePlace();
        routePlace.associate(this, place);
        return routePlace;
    }
}
