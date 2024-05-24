package haru.harudongseon.route.domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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

    @OneToMany(mappedBy = "route", orphanRemoval = true)
    private List<SelectedTag> tags = new ArrayList<>();

    private String moveWays;

    @OneToMany(mappedBy = "route", orphanRemoval = true)
    private List<RoutePlace> routePlaces = new ArrayList<>();

    public Route(final Member member, final LocalDate date,
                 final String title, final String moveWays) {
        this.member = member;
        this.date = date;
        this.title = title;
        this.moveWays = moveWays;
    }

    public Route(final LocalDate date, final String title,
                 final String moveWays) {
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

    public void deleteTagAll() {
        System.out.println("tags.size() = " + tags.size());
        tags.forEach(SelectedTag::unselected);
        tags.clear();
    }

    public void deleteRoutePlaceAll() {
        routePlaces.clear();
    }

    public void changeInfo(final Route routeToChange) {
        this.date = routeToChange.date;
        this.title = routeToChange.title;
        this.moveWays = routeToChange.moveWays;
    }
}
