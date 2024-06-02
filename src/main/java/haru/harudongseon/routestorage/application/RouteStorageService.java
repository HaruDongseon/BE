package haru.harudongseon.routestorage.application;

import java.util.List;

import haru.harudongseon.member.domain.Member;
import haru.harudongseon.member.domain.MemberRepository;
import haru.harudongseon.routestorage.application.dto.RouteDeleteRequest;
import haru.harudongseon.routestorage.application.dto.RouteStorageAddRequest;
import haru.harudongseon.routestorage.application.dto.RouteStoragesResponse;
import haru.harudongseon.routestorage.domain.RouteStorage;
import haru.harudongseon.routestorage.domain.RouteStorageRepository;
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

    public void deleteRouteStorage(final Long memberId, final RouteDeleteRequest request) {
        final Long routeStorageId = request.routeStorageId();
        final List<Long> routeIds = request.routeIds();

        final RouteStorage routeStorage = routeStorageRepository.findById(routeStorageId)
                .orElseThrow(() -> new EntityNotFoundException("해당하는 동선 보관함이 존재하지 않습니다."));

        routeStorage.removeRoutes(memberId, routeIds);
    }

    @Transactional(readOnly = true)
    public RouteStoragesResponse findRouteStorageNames(final Long memberId) {
        final Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("해당하는 멤버가 존재하지 않습니다."));

        final List<RouteStorage> routeStorages = routeStorageRepository.findAllByMemberId(memberId);
        return RouteStoragesResponse.from(routeStorages);
    }
}
