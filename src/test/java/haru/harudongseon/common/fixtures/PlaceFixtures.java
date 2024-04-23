package haru.harudongseon.common.fixtures;

import java.math.BigDecimal;
import java.util.List;

import haru.harudongseon.place.domain.Coordinates;
import haru.harudongseon.place.domain.PhotoReferences;
import haru.harudongseon.place.domain.Place;
import haru.harudongseon.place.domain.placedetails.DeliveryAvailable;
import haru.harudongseon.place.domain.placedetails.PlaceDetails;
import haru.harudongseon.place.domain.placedetails.Reservable;
import haru.harudongseon.place.domain.placedetails.TakeoutAvailable;

public class PlaceFixtures {

    public static final String 기본_외부_공급자_ID = "ChIJs5ydyTiuEmsR0fRSlU0C7k0";
    public static final String 기본_장소_이름 = "장생당약국";
    public static final String 기본_장소_카테고리 = "약국";
    public static final PhotoReferences 기본_장소_사진_참조 = new PhotoReferences(List.of("ATJ83zhSSAtk1", "ATJ83zhSSAtk2", "ATJ83zhSSAtk3"));
    public static final BigDecimal 기본_장소_위도 = new BigDecimal("127.058970");
    public static final BigDecimal 기본_장소_경도 = new BigDecimal("37.506051");
    public static final Coordinates 기본_장소_좌표 = new Coordinates(기본_장소_위도, 기본_장소_경도);
    public static final String 기본_장소_주소_이름 = "서울 대치동";
    public static final String 기본_장소_전화번호 = "1234-1234";
    public static final String 기본_장소_웹사이트 = "http://www.starbucks.co.kr/";
    public static final String 기본_장소_URL = "https://maps.google.com/?cid=9514396914460199663";
    public static final PlaceDetails 기본_장소_세부_정보 = new PlaceDetails(Reservable.FALSE, TakeoutAvailable.TRUE, DeliveryAvailable.NONE);

    public static Place 기본_장소_Entity() {
        return new Place(기본_외부_공급자_ID, 기본_장소_이름, 기본_장소_사진_참조, 기본_장소_좌표, 기본_장소_주소_이름, 기본_장소_전화번호, 기본_장소_웹사이트, 기본_장소_URL, 기본_장소_세부_정보);
    }
}
