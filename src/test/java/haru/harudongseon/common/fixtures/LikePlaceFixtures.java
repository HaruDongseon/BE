package haru.harudongseon.common.fixtures;

import java.math.BigDecimal;
import java.util.List;

import haru.harudongseon.likeplace.domain.Coordinates;
import haru.harudongseon.likeplace.domain.LikePlace;
import haru.harudongseon.likeplace.domain.placedetails.ParkingAvailable;
import haru.harudongseon.likeplace.domain.placedetails.PlaceDetails;
import haru.harudongseon.likeplace.domain.placedetails.Reservable;
import haru.harudongseon.likeplace.domain.placedetails.TakeoutAvailable;
import haru.harudongseon.member.domain.Member;

public class LikePlaceFixtures {

    public static final String 기본_외부_공급자_ID = "ChIJ07n0DkZ8ezUR7wp5kpXtCYQ";
    public static final String 기본_보관_장소_이름 = "스타벅스 부평점";
    public static final String 기본_보관_장소_카테고리 = "커피숍/커피 전문점";
    public static final String 기본_보관_장소_사진_참조1 = "ATJ83zhSSAtk1";
    public static final String 기본_보관_장소_사진_참조2 = "ATJ83zhSSAtk2";
    public static final String 기본_보관_장소_사진_참조3 = "ATJ83zhSSAtk3";
    public static final List<String> 기본_보관_장소_사진_참조 = List.of(기본_보관_장소_사진_참조1, 기본_보관_장소_사진_참조2, 기본_보관_장소_사진_참조3);
    public static final BigDecimal 기본_보관_장소_위도 = new BigDecimal("127.058970");
    public static final BigDecimal 기본_보관_장소_경도 = new BigDecimal("37.506051");
    public static final Coordinates 기본_보관_장소_좌표 = new Coordinates(기본_보관_장소_위도, 기본_보관_장소_경도);
    public static final String 기본_보관_장소_영업_시간 = "월요일: 오전 7:00 ~ 오후 11:00 " +
            "화요일: 오전 7:00 ~ 오후 11:00 " +
            "수요일: 오전 7:00 ~ 오후 11:00 " +
            "목요일: 오전 7:00 ~ 오후 11:00 " +
            "금요일: 오전 7:00 ~ 오후 11:00 " +
            "토요일: 오전 7:00 ~ 오후 11:00 " +
            "일요일: 오전 9:00 ~ 오후 11:00 ";
    public static final String 기본_보관_장소_주소_이름 = "인천광역시 부평구 경원대로 1397";
    public static final String 기본_보관_장소_전화번호 = "1522-3232";
    public static final String 기본_보관_장소_웹사이트 = "http://www.starbucks.co.kr/";
    public static final String 기본_보관_장소_구글_맵_URL = "https://maps.google.com/?cid=9514396914460199663";
    public static final Reservable 기본_보관_장소_예약_가능_여부 = Reservable.FALSE;
    public static final TakeoutAvailable 기본_보관_장소_포장_가능_여부 = TakeoutAvailable.TRUE;
    public static final ParkingAvailable 기본_보관_장소_주차_가능_여부 = ParkingAvailable.NONE;
    public static final PlaceDetails 기본_보관_장소_세부_정보 = new PlaceDetails(기본_보관_장소_예약_가능_여부, 기본_보관_장소_포장_가능_여부, 기본_보관_장소_주차_가능_여부);

    public static LikePlace 기본_보관_장소_엔티티(final Member member) {
        return new LikePlace(member, 기본_외부_공급자_ID, 기본_보관_장소_이름, 기본_보관_장소_카테고리, 기본_보관_장소_사진_참조, 기본_보관_장소_좌표, 기본_보관_장소_영업_시간, 기본_보관_장소_주소_이름, 기본_보관_장소_전화번호, 기본_보관_장소_웹사이트, 기본_보관_장소_구글_맵_URL, 기본_보관_장소_세부_정보);
    }

    public static LikePlace 기본_보관_장소_엔티티_이름_파라미터(final Member member, final String name) {
        return new LikePlace(member, 기본_외부_공급자_ID, name, 기본_보관_장소_카테고리, 기본_보관_장소_사진_참조, 기본_보관_장소_좌표, 기본_보관_장소_영업_시간, 기본_보관_장소_주소_이름, 기본_보관_장소_전화번호, 기본_보관_장소_웹사이트, 기본_보관_장소_구글_맵_URL, 기본_보관_장소_세부_정보);
    }
}
