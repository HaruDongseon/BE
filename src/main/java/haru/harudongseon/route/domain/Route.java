package haru.harudongseon.route.domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import haru.harudongseon.route.domain.routeplace.RoutePlace;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Route {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;
    private String title;

    @OneToMany(mappedBy = "route")
    private List<SelectedTag> tags = new ArrayList<>();

    private List<MoveWay> moveWays = new ArrayList<>();

    @OneToMany
    @JoinColumn(name = "route_id")
    private List<RoutePlace> routePlaces = new ArrayList<>();

    public Route(final LocalDate date, final String title) {
        this.date = date;
        this.title = title;
    }
}
