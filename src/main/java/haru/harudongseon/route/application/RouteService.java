package haru.harudongseon.route.application;

import java.util.Optional;
import java.util.Set;

import haru.harudongseon.member.domain.Member;
import haru.harudongseon.member.domain.MemberRepository;
import haru.harudongseon.place.domain.Place;
import haru.harudongseon.place.domain.PlaceRepository;
import haru.harudongseon.route.application.dto.RouteAddRequest;
import haru.harudongseon.route.application.dto.RoutePlaceDto;
import haru.harudongseon.route.domain.*;
import haru.harudongseon.routetag.domain.RouteTag;
import haru.harudongseon.routetag.domain.RouteTagRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class RouteService {

    private final MemberRepository memberRepository;
    private final RouteRepository routeRepository;
    private final RouteTagRepository routeTagRepository;
    private final SelectedTagRepository selectedTagRepository;
    private final PlaceRepository placeRepository;
    private final RoutePlaceRepository routePlaceRepository;

    public Long addRoute(final Long memberId, final RouteAddRequest request) {
        final Member findMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("해당하는 멤버를 찾을 수 없습니다."));
        final Route route = new Route(findMember, request.date(), request.title(), request.moveWay());
        final Route savedRoute = routeRepository.save(route);

        addTag(request.tag(), savedRoute);
        addPlace(request.routePlaces(), savedRoute);

        return savedRoute.getId();
    }

    private void addTag(final Set<String> tagNames, final Route savedRoute) {
        for (String tagName : tagNames) {
            final Optional<RouteTag> optionalRouteTag = routeTagRepository.findByName(tagName);
            if (optionalRouteTag.isPresent()) {
                final RouteTag findRouteTag = optionalRouteTag.get();
                findRouteTag.select();
                final SelectedTag selectedTag = savedRoute.addTag(findRouteTag);
                selectedTagRepository.save(selectedTag);
            }

            if (optionalRouteTag.isEmpty()) {
                final RouteTag savedRouteTag = routeTagRepository.save(new RouteTag(tagName));
                final SelectedTag selectedTag = savedRoute.addTag(savedRouteTag);
                selectedTagRepository.save(selectedTag);
            }
        }
    }

    private void addPlace(final Set<RoutePlaceDto> routePlaces, final Route savedRoute) {
        for (RoutePlaceDto routePlaceDto : routePlaces) {
            final String providerPlaceId = routePlaceDto.providerPlaceId();
            final Optional<Place> optionalPlace = placeRepository.findByProviderPlaceId(providerPlaceId);
            if (optionalPlace.isPresent()) {
                final Place findPlace = optionalPlace.get();
                final RoutePlace routePlace = savedRoute.addPlace(findPlace);
                routePlaceRepository.save(routePlace);
            }

            if (optionalPlace.isEmpty()) {
                final Place savedPlace = placeRepository.save(routePlaceDto.toEntity());
                final RoutePlace routePlace = savedRoute.addPlace(savedPlace);
                routePlaceRepository.save(routePlace);
            }
        }
    }
}
