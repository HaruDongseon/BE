package haru.harudongseon.route.domain;

import haru.harudongseon.global.BaseEntity;
import haru.harudongseon.place.domain.Place;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
public class RoutePlace extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private Route route;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private Place place;

    public void associate(final Route route, final Place place) {
        this.route = route;
        this.place = place;
        route.getRoutePlaces().add(this);
    }
}
