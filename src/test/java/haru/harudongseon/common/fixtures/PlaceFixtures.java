package haru.harudongseon.common.fixtures;

import java.math.BigDecimal;

import haru.harudongseon.place.domain.Place;

public class PlaceFixtures {

    public static final String 기본_외부_공급자_ID = "ChIJs5ydyTiuEmsR0fRSlU0C7k0";
    public static final String 기본_장소_이름 = "장생당약국";
    public static final String 기본_장소_카테고리 = "약국";
    public static final String 기본_장소_사진_참조 = "ATJ83zhSSAtk";
    public static final BigDecimal 기본_장소_위도 = new BigDecimal("127.058970");
    public static final BigDecimal 기본_장소_경도 = new BigDecimal("37.506051");
    public static final String 기본_장소_주소_이름 = "서울 대치동";


    public static Place 기본_장소_Entity() {
        return new Place(기본_외부_공급자_ID, 기본_장소_이름, 기본_장소_카테고리, 기본_장소_사진_참조, 기본_장소_위도, 기본_장소_경도, 기본_장소_주소_이름);
    }
}
