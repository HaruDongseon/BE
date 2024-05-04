package haru.harudongseon.route.application;

import java.util.List;
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

    private final RouteValidator routeValidator;

    public Long addRoute(final Long memberId, final RouteAddRequest request) {
        final Member findMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("해당하는 멤버를 찾을 수 없습니다."));

        final List<String> tagNames = request.tag();
        final List<RoutePlaceDto> routePlaces = request.routePlaces();
        validateDuplicate(tagNames, routePlaces);

        final Route route = new Route(findMember, request.date(), request.title(), request.moveWay());
        final Route savedRoute = routeRepository.save(route);

        addTag(tagNames, savedRoute);
        addPlace(routePlaces, savedRoute);

        return savedRoute.getId();
    }

    private void validateDuplicate(final List<String> tagNames, final List<RoutePlaceDto> routePlaces) {
        routeValidator.validateDuplicateTag(tagNames);
        routePlaces.forEach(routePlaceDto ->
                routeValidator.validateDuplicatePlacePhotoReference(routePlaceDto.photoReferences())
        );
    }

    private void addTag(final List<String> tagNames, final Route savedRoute) {
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

    private void addPlace(final List<RoutePlaceDto> routePlaces, final Route savedRoute) {
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
