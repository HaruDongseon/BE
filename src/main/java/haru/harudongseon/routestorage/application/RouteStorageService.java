package haru.harudongseon.routestorage.application;

import java.util.List;

import haru.harudongseon.member.domain.Member;
import haru.harudongseon.member.domain.MemberRepository;
import haru.harudongseon.route.domain.Route;
import haru.harudongseon.route.domain.RouteRepository;
import haru.harudongseon.routestorage.application.dto.*;
import haru.harudongseon.routestorage.domain.RouteStorage;
import haru.harudongseon.routestorage.domain.RouteStorageRepository;
import haru.harudongseon.routestorage.domain.StoredRoute;
import haru.harudongseon.routestorage.exception.RouteStorageException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class RouteStorageService {

    private final MemberRepository memberRepository;
    private final RouteStorageRepository routeStorageRepository;
    private final RouteRepository routeRepository;

    public Long addRouteStorage(final Long memberId, final RouteStorageAddRequest request) {
        final Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("해당하는 멤버가 존재하지 않습니다."));

        final String name = request.name();
        checkDuplicateRouteStorage(memberId, name);

        final RouteStorage routeStorage = new RouteStorage(member, name);
        final RouteStorage savedRouteStorage = routeStorageRepository.save(routeStorage);
        return savedRouteStorage.getId();
    }

    private void checkDuplicateRouteStorage(final Long memberId, final String name) {
        if (routeStorageRepository.existsByMemberIdAndName(memberId, name)) {
            throw new RouteStorageException.DuplicateException();
        }
    }

    public void deleteRouteStorage(final StoredRouteDeleteRequest request) {
        final Long routeStorageId = request.routeStorageId();
        final List<Long> routeIds = request.routeIds();

        final RouteStorage routeStorage = routeStorageRepository.findById(routeStorageId)
                .orElseThrow(() -> new EntityNotFoundException("해당하는 동선 보관함이 존재하지 않습니다."));

        routeStorage.removeRoutes(routeIds);
    }

    @Transactional(readOnly = true)
    public RouteStoragesResponse findRouteStorageNames(final Long memberId) {
        final Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("해당하는 멤버가 존재하지 않습니다."));

        final List<RouteStorage> routeStorages = routeStorageRepository.findAllByMemberId(memberId);
        return RouteStoragesResponse.from(routeStorages);
    }

    @Transactional(readOnly = true)
    public StoredRoutesResponse findRoutes(final Long routeStorageId, final Long memberId) {
        final RouteStorage routeStorage = routeStorageRepository.findByIdAndMemberId(routeStorageId, memberId)
                .orElseThrow(() -> new EntityNotFoundException("동선 보관함 ID와 멤버 ID에 해당하는 동선 보관함이 존재하지 않습니다."));

        final List<StoredRoute> storedRoutes = routeStorage.getRoutes();
        return StoredRoutesResponse.from(storedRoutes);
    }

    public void addRoutes(final Long routeStorageId, final StoredRouteAddRequest request) {
        final RouteStorage routeStorage = routeStorageRepository.findById(routeStorageId)
                .orElseThrow(() -> new EntityNotFoundException("해당하는 동선 보관함이 존재하지 않습니다."));

        final List<Route> routes = request.routeIds().stream()
                .map(routeId -> routeRepository.findById(routeId)
                        .orElseThrow(() -> new EntityNotFoundException("해당하는 동선이 존재하지 않습니다."))
                ).toList();

        routeStorage.addRoutes(routes);
    }

    public void moveRoutes(final Long routeStorageId, final StoredRouteMoveRequest request) {
        final RouteStorage routeStorage = routeStorageRepository.findById(routeStorageId)
                .orElseThrow(() -> new EntityNotFoundException("해당하는 동선 보관함이 존재하지 않습니다."));

        final List<RouteStorage> routeStoragesToMove = request.routeStorageIds().stream()
                .map(routeStorageIdToMove -> routeStorageRepository.findById(routeStorageIdToMove)
                        .orElseThrow(() -> new EntityNotFoundException("해당하는 이동할 동선 보관함을 찾을 수 없습니다."))
                ).toList();

        final List<Route> routesToMove = request.routeIds().stream()
                .map(routeId -> routeRepository.findById(routeId)
                        .orElseThrow(() -> new EntityNotFoundException("해당하는 이동할 동선이 존재하지 않습니다."))
                ).toList();

        routeStorage.moveRoutes(routeStoragesToMove, routesToMove);
    }
}
