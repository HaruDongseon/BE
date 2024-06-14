package haru.harudongseon;

import haru.harudongseon.global.oauth.LoginType;
import haru.harudongseon.member.domain.Member;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Profile({"local"})
@Component
@RequiredArgsConstructor
public class InitData {

    private final InitDataService initDataService;

    @PostConstruct
    public void init() {
        initDataService.init();
    }

    @Component
    static class InitDataService {
        @PersistenceContext
        private EntityManager em;

        @Transactional
        public void init() {
            int totalMemberCount = 300;

            for (long i = 1; i <= totalMemberCount; i++) {
                final Member member = new Member("sh" + i + "@gmail.com", "seongha" + i, "url" + i, "oauthId" + i, "deviceId" + i, LoginType.KAKAO);
                em.persist(member);
            }
        }
    }

}

