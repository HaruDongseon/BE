package haru.harudongseon.route.application;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import haru.harudongseon.member.domain.Member;
import haru.harudongseon.member.domain.MemberRepository;
import haru.harudongseon.place.domain.Place;
import haru.harudongseon.place.domain.PlaceRepository;
import haru.harudongseon.route.application.dto.*;
import haru.harudongseon.route.application.event.RouteDeleteEvent;
import haru.harudongseon.route.domain.*;
import haru.harudongseon.route.exception.RouteException;
import haru.harudongseon.routetag.domain.RouteTag;
import haru.harudongseon.routetag.domain.RouteTagRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class RouteService {

    private final MemberRepository memberRepository;
    private final RouteRepository routeRepository;
    private final RouteTagRepository routeTagRepository;
    private final SelectedTagRepository selectedTagRepository;
    private final PlaceRepository placeRepository;
    private final RoutePlaceRepository routePlaceRepository;

    private final ApplicationEventPublisher applicationEventPublisher;

    private final RedisTemplate redisTemplate;

    private final RouteValidator routeValidator;

    public Long addRoute(final Long memberId, final RouteAddRequest request) {
        final Member findMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("해당하는 멤버를 찾을 수 없습니다."));

        final LocalDate date = request.date();
        final List<String> tagNames = request.tag();
        final List<RoutePlaceDto> routePlaces = request.routePlaces();
        validateAlreadyExist(date, memberId);
        validateDuplicate(tagNames, routePlaces);

        final Route route = new Route(findMember, request.date(), request.title(), request.moveWay());
        final Route savedRoute = routeRepository.save(route);

        addTag(tagNames, savedRoute);
        addPlace(routePlaces, savedRoute);

        final String action = "addRoute:";
        final String idempotentKey = action + date + ":" + memberId;

        final Boolean isFirstRequest = redisTemplate.opsForValue().setIfAbsent(idempotentKey, "success", 10, TimeUnit.SECONDS);
        if (!isFirstRequest) {
            log.info("동선 생성 시 중복 요청 발생 - 멱등키 : {}", idempotentKey);
            throw new RouteException.DuplicateSameDateException();
        }

        return savedRoute.getId();
    }

    private void validateAlreadyExist(final LocalDate date, final Long memberId) {
        routeValidator.validateAlreadyExistSameDate(date, memberId);
    }

    private void validateDuplicate(final List<String> tagNames, final List<RoutePlaceDto> routePlaces) {
        routeValidator.validateDuplicateTag(tagNames);
        routePlaces.forEach(routePlaceDto ->
                routeValidator.validateDuplicatePlacePhotoReference(routePlaceDto.photoReferences())
        );
    }

    private void addTag(final List<String> tagNames, final Route savedRoute) {
        for (String tagName : tagNames) {
            final boolean isAlreadyExistTag = routeTagRepository.existsByName(tagName);
            if (isAlreadyExistTag) {
                final RouteTag findRouteTag = routeTagRepository.findByName(tagName).get();
                findRouteTag.selected();
                final SelectedTag selectedTag = savedRoute.addTag(findRouteTag);
                selectedTagRepository.save(selectedTag);
            }

            if (!isAlreadyExistTag) {
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

    @Transactional(readOnly = true)
    public RouteResponse findRoute(final Long routeId, final Long memberId) {
        final Route findRoute = routeRepository.findByIdAndMemberId(routeId, memberId)
                .orElseThrow(() -> new EntityNotFoundException("멤버 ID와 동선 ID에 해당하는 동선이 존재하지 않습니다."));

        return RouteResponse.from(findRoute);
    }

    @Transactional(readOnly = true)
    public RoutesResponse findRouteByPeriod(final Long memberId, final LocalDate startDate, final LocalDate endDate) {
        final List<Route> findRoutes = routeRepository.findByMemberIdAndPeriod(memberId, startDate, endDate);

        return RoutesResponse.from(findRoutes);
    }

    public void editRoute(final Long memberId, final Long routeId, final RouteEditRequest request) {
        final Route findRoute = routeRepository.findByIdAndMemberId(routeId, memberId)
                .orElseThrow(() -> new EntityNotFoundException("멤버 ID와 동선 ID에 해당하는 동선이 존재하지 않습니다."));

        findRoute.deleteTagAll();
        addTag(request.tag(), findRoute);

        findRoute.deleteRoutePlaceAll();
        addPlace(request.routePlaces(), findRoute);

        findRoute.changeInfo(request.toEditEntity());
    }

    public void deleteRoute(final Long memberId, final Long routeId) {
        if (!routeRepository.existsByIdAndMemberId(routeId, memberId)) {
            throw new EntityNotFoundException("멤버 ID와 동선 ID에 해당하는 동선이 존재하지 않습니다.");
        }

        applicationEventPublisher.publishEvent(new RouteDeleteEvent(routeId));
        routeRepository.deleteById(routeId);
    }
}
