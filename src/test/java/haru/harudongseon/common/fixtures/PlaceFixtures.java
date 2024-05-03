package haru.harudongseon.common.fixtures;

import java.math.BigDecimal;
import java.util.Set;

import haru.harudongseon.place.domain.Coordinates;
import haru.harudongseon.place.domain.Place;
import haru.harudongseon.place.domain.placedetails.ParkingAvailable;
import haru.harudongseon.place.domain.placedetails.PlaceDetails;
import haru.harudongseon.place.domain.placedetails.Reservable;
import haru.harudongseon.place.domain.placedetails.TakeoutAvailable;

public class PlaceFixtures {

    public static final String 기본_장소1_외부_공급자_ID = "ChIJ07n0DkZ8ezUR7wp5kpXtCYQ";
    public static final String 기본_장소2_외부_공급자_ID = "ChIJHR-uLEClfDURWbumsgvILRI";
    public static final String 기본_장소1_이름 = "스타벅스 부평점";
    public static final String 기본_장소2_이름 = "우리마키 성수점";

    public static final String 기본_장소1_카테고리 = "커피숍/커피 전문점";
    public static final String 기본_장소2_카테고리 = "일본 음식점";
    public static final Set<String> 기본_장소1_사진_참조 = Set.of("ATJ83zhSSAtk1", "ATJ83zhSSAtk2", "ATJ83zhSSAtk3");
    public static final Set<String> 기본_장소2_사진_참조 = Set.of("VDSDFVadsfh23", "VDSDFVadsfh24");
    public static final BigDecimal 기본_장소1_위도 = new BigDecimal("127.058970");
    public static final BigDecimal 기본_장소2_위도 = new BigDecimal("37.541962");
    public static final BigDecimal 기본_장소1_경도 = new BigDecimal("37.506051");
    public static final BigDecimal 기본_장소2_경도 = new BigDecimal("127.053891");
    public static final Coordinates 기본_장소1_좌표 = new Coordinates(기본_장소1_위도, 기본_장소1_경도);
    public static final Coordinates 기본_장소2_좌표 = new Coordinates(기본_장소2_위도, 기본_장소2_경도);
    public static final String 기본_장소1_영업_시간 = "월요일: 오전 7:00 ~ 오후 11:00 " +
                                                "화요일: 오전 7:00 ~ 오후 11:00 " +
                                                "수요일: 오전 7:00 ~ 오후 11:00 " +
                                                "목요일: 오전 7:00 ~ 오후 11:00 " +
                                                "금요일: 오전 7:00 ~ 오후 11:00 " +
                                                "토요일: 오전 7:00 ~ 오후 11:00 " +
                                                "일요일: 오전 9:00 ~ 오후 11:00 ";

    public static final String 기본_장소2_영업_시간 = "NONE";

    public static final String 기본_장소1_주소_이름 = "인천광역시 부평구 경원대로 1397";
    public static final String 기본_장소2_주소_이름 = "대한민국 서울특별시 성동구 성수이로7길 32 1 층";
    public static final String 기본_장소1_전화번호 = "1522-3232";
    public static final String 기본_장소2_전화번호 = "050-71470-8338";
    public static final String 기본_장소1_웹사이트 = "http://www.starbucks.co.kr/";
    public static final String 기본_장소2_웹사이트 = "https://www.instagram.com/woorimaki";
    public static final String 기본_장소1_구글_맵_URL = "https://maps.google.com/?cid=9514396914460199663";
    public static final String 기본_장소2_구글_맵_URL = "https://maps.google.com/?cid=1309923019202149209";
    public static final Reservable 기본_장소1_예약_가능_여부 = Reservable.FALSE;
    public static final Reservable 기본_장소2_예약_가능_여부 = Reservable.NONE;
    public static final TakeoutAvailable 기본_장소1_포장_가능_여부 = TakeoutAvailable.TRUE;
    public static final TakeoutAvailable 기본_장소2_포장_가능_여부 = TakeoutAvailable.TRUE;
    public static final ParkingAvailable 기본_장소1_주차_가능_여부 = ParkingAvailable.NONE;
    public static final ParkingAvailable 기본_장소2_주차_가능_여부 = ParkingAvailable.NONE;
    public static final PlaceDetails 기본_장소1_세부_정보 = new PlaceDetails(기본_장소1_예약_가능_여부, 기본_장소1_포장_가능_여부, 기본_장소1_주차_가능_여부);
    public static final PlaceDetails 기본_장소2_세부_정보 = new PlaceDetails(기본_장소2_예약_가능_여부, 기본_장소2_포장_가능_여부, 기본_장소2_주차_가능_여부);

    public static Place 기본_장소1_Entity() {
        return new Place(기본_장소1_외부_공급자_ID, 기본_장소1_이름, 기본_장소1_카테고리, 기본_장소1_사진_참조, 기본_장소1_좌표, 기본_장소1_영업_시간, 기본_장소1_주소_이름, 기본_장소1_전화번호, 기본_장소1_웹사이트, 기본_장소1_구글_맵_URL, 기본_장소1_세부_정보);
    }

    public static Place 기본_장소2_Entity() {
        return new Place(기본_장소2_외부_공급자_ID, 기본_장소2_이름, 기본_장소2_카테고리, 기본_장소2_사진_참조, 기본_장소2_좌표, 기본_장소2_영업_시간, 기본_장소2_주소_이름, 기본_장소2_전화번호, 기본_장소2_웹사이트, 기본_장소2_구글_맵_URL, 기본_장소2_세부_정보);
    }
}
