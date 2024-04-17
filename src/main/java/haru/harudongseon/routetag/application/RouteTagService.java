package haru.harudongseon.routetag.application;

import java.util.List;

import haru.harudongseon.routetag.application.dto.RouteTagSearchResponse;
import haru.harudongseon.routetag.domain.RouteTag;
import haru.harudongseon.routetag.domain.RouteTagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class RouteTagService {

    private final RouteTagRepository routeTagRepository;

    public RouteTagSearchResponse searchByKeyword(final String keyword) {
        final List<RouteTag> searchedRouteTags = routeTagRepository.findByKeywordContainingIgnoreCaseAndOrderBySelectCountDesc(keyword);
        return RouteTagSearchResponse.from(searchedRouteTags);
    }
}
