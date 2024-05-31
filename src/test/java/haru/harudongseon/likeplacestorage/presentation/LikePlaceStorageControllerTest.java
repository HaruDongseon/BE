package haru.harudongseon.likeplacestorage.presentation;

import static haru.harudongseon.common.fixtures.LikePlaceStorageFixtures.기본_장소_보관함_이름;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.util.Collections;
import java.util.List;

import haru.harudongseon.common.E2ETest;
import haru.harudongseon.common.builder.LikePlaceBuilder;
import haru.harudongseon.common.builder.LikePlaceStorageBuilder;
import haru.harudongseon.common.builder.MemberBuilder;
import haru.harudongseon.likeplace.domain.LikePlace;
import haru.harudongseon.likeplacestorage.application.dto.LikePlaceDeleteRequest;
import haru.harudongseon.likeplacestorage.application.dto.LikePlaceStorageAddRequest;
import haru.harudongseon.likeplacestorage.domain.LikePlaceStorage;
import haru.harudongseon.member.domain.Member;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

class LikePlaceStorageControllerTest extends E2ETest {

    @Autowired
    private MemberBuilder memberBuilder;

    @Autowired
    private LikePlaceBuilder likePlaceBuilder;

    @Autowired
    private LikePlaceStorageBuilder likePlaceStorageBuilder;

    @Nested
    @DisplayName("장소 보관함 추가 시")
    class AddLikePlaceStorage {

        @Test
        @DisplayName("추가에 성공한다.")
        void success() {
            // given
            final Member member = memberBuilder.defaultMember().build();
            final String accessToken = jwtService.createAccessToken(member.getId());
            final LikePlaceStorageAddRequest request = new LikePlaceStorageAddRequest(기본_장소_보관함_이름);

            // when
            final ExtractableResponse<Response> response = ADD_LIKE_PLACE_STORAGE_REQUEST(accessToken, request);

            // then
            assertSoftly(softly -> {
                softly.assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
                softly.assertThat(response.header("Location")).contains("/like-place-storage/");
            });
        }

        @Test
        @DisplayName("중복된 이름을 가진 이름으로 장소 보관함을 추가하면 실패한다.")
        void fail_duplicate_name() {
            // given
            final Member member = memberBuilder.defaultMember().build();
            final String accessToken = jwtService.createAccessToken(member.getId());
            final LikePlaceStorage existLikePlaceStorage = likePlaceStorageBuilder.defaultLikePlaceStorage(member).build(Collections.emptyList());
            final LikePlaceStorageAddRequest duplicateRequest = new LikePlaceStorageAddRequest(existLikePlaceStorage.getName());

            // when
            final ExtractableResponse<Response> response = ADD_LIKE_PLACE_STORAGE_REQUEST(accessToken, duplicateRequest);

            // then
            assertSoftly(softly -> {
                softly.assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                softly.assertThat(response.jsonPath().getString("errorMessage")).contains("중복된 이름을 가진 회원의 장소 보관함이 이미 존재합니다.");
            });
        }

        @ParameterizedTest
        @ValueSource(strings = {"", " "})
        @DisplayName("장소 보관함 이름이 공백이면 실패한다.")
        void fail_blank_name(final String blankName) {
            // given
            final Member member = memberBuilder.defaultMember().build();
            final String accessToken = jwtService.createAccessToken(member.getId());
            final LikePlaceStorageAddRequest blankRequest = new LikePlaceStorageAddRequest(blankName);

            // when
            final ExtractableResponse<Response> response = ADD_LIKE_PLACE_STORAGE_REQUEST(accessToken, blankRequest);

            // then
            assertSoftly(softly -> {
                softly.assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                softly.assertThat(response.jsonPath().getString("errorMessage")).contains("장소 보관함 이름은 공백일 수 없습니다.");
            });
        }

        @Test
        @DisplayName("장소 보관함 이름이 16자 이상이면 실패한다.")
        void fail_over_16_name() {
            // given
            final Member member = memberBuilder.defaultMember().build();
            final String accessToken = jwtService.createAccessToken(member.getId());
            final String overLengthName = "a".repeat(16);
            final LikePlaceStorageAddRequest overLengthRequest = new LikePlaceStorageAddRequest(overLengthName);

            // when
            final ExtractableResponse<Response> response = ADD_LIKE_PLACE_STORAGE_REQUEST(accessToken, overLengthRequest);

            // then
            assertSoftly(softly -> {
                softly.assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                softly.assertThat(response.jsonPath().getString("errorMessage")).contains("장소 보관함 이름은 15자 이하여야 합니다.");
            });
        }
    }

    @Nested
    @DisplayName("장소 보관함 보관 장소 삭제 시 ")
    class DeleteLikePlace {

        @Test
        @DisplayName("보관 장소 삭제에 성공한다.")
        void success() {
            // given
            final Member member = memberBuilder.defaultMember().build();
            final String accessToken = jwtService.createAccessToken(member.getId());

            final LikePlace likePlace1 = likePlaceBuilder.defaultLikePlace(member).photoReferences(List.of("reference1")).name("베이커리 성수").build();
            final LikePlace likePlace2 = likePlaceBuilder.defaultLikePlace(member).photoReferences(List.of("reference2")).name("스타벅스 성수점").build();
            final LikePlace likePlace3 = likePlaceBuilder.defaultLikePlace(member).photoReferences(List.of("reference3")).name("성수건설").build();
            final LikePlaceStorage likePlaceStorage = likePlaceStorageBuilder.defaultLikePlaceStorage(member).build(List.of(likePlace1, likePlace2, likePlace3));

            final LikePlaceDeleteRequest request = new LikePlaceDeleteRequest(likePlaceStorage.getId(), List.of(likePlace1.getId(), likePlace2.getId()));

            // when
            final ExtractableResponse<Response> response = DELETE_LIKE_PLACE_REQUEST(accessToken, request);

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
        }

        @Test
        @DisplayName("장소 보관함 ID가 공백이면 실패한다.")
        void fail_like_place_storage_id_blank() {
            // given
            final Member member = memberBuilder.defaultMember().build();
            final String accessToken = jwtService.createAccessToken(member.getId());

            final LikePlace likePlace1 = likePlaceBuilder.defaultLikePlace(member).photoReferences(List.of("reference1")).name("베이커리 성수").build();
            final LikePlace likePlace2 = likePlaceBuilder.defaultLikePlace(member).photoReferences(List.of("reference2")).name("스타벅스 성수점").build();
            final LikePlace likePlace3 = likePlaceBuilder.defaultLikePlace(member).photoReferences(List.of("reference3")).name("성수건설").build();
            final LikePlaceStorage likePlaceStorage = likePlaceStorageBuilder.defaultLikePlaceStorage(member).build(List.of(likePlace1, likePlace2, likePlace3));

            final Long blankLikePlaceStorageId = null;
            final LikePlaceDeleteRequest request = new LikePlaceDeleteRequest(blankLikePlaceStorageId, List.of(likePlace1.getId(), likePlace2.getId()));

            // when
            final ExtractableResponse<Response> response = DELETE_LIKE_PLACE_REQUEST(accessToken, request);

            // then
            assertSoftly(softly -> {
                softly.assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                softly.assertThat(response.jsonPath().getString("errorMessage")).contains("장소 보관함 ID는 공백일 수 없습니다.");
            });
        }

        @Test
        @DisplayName("보관 장소 ID 리스트가 비어있으면 실패한다.")
        void fail_like_place_ids_empty() {
            // given
            final Member member = memberBuilder.defaultMember().build();
            final String accessToken = jwtService.createAccessToken(member.getId());

            final LikePlace likePlace1 = likePlaceBuilder.defaultLikePlace(member).photoReferences(List.of("reference1")).name("베이커리 성수").build();
            final LikePlace likePlace2 = likePlaceBuilder.defaultLikePlace(member).photoReferences(List.of("reference2")).name("스타벅스 성수점").build();
            final LikePlace likePlace3 = likePlaceBuilder.defaultLikePlace(member).photoReferences(List.of("reference3")).name("성수건설").build();
            final LikePlaceStorage likePlaceStorage = likePlaceStorageBuilder.defaultLikePlaceStorage(member).build(List.of(likePlace1, likePlace2, likePlace3));

            final LikePlaceDeleteRequest request = new LikePlaceDeleteRequest(likePlaceStorage.getId(), Collections.emptyList());

            // when
            final ExtractableResponse<Response> response = DELETE_LIKE_PLACE_REQUEST(accessToken, request);

            // then
            assertSoftly(softly -> {
                softly.assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                softly.assertThat(response.jsonPath().getString("errorMessage")).contains("보관 장소 ID 리스트는 공백일 수 없습니다.");
            });
        }

        @Test
        @DisplayName("해당하는 장소 보관함이 없으면 실패한다.")
        void fail_not_exist_like_place_storage() {
            // given
            final Member member = memberBuilder.defaultMember().build();
            final String accessToken = jwtService.createAccessToken(member.getId());

            final LikePlace likePlace1 = likePlaceBuilder.defaultLikePlace(member).photoReferences(List.of("reference1")).name("베이커리 성수").build();
            final LikePlace likePlace2 = likePlaceBuilder.defaultLikePlace(member).photoReferences(List.of("reference2")).name("스타벅스 성수점").build();
            final LikePlace likePlace3 = likePlaceBuilder.defaultLikePlace(member).photoReferences(List.of("reference3")).name("성수건설").build();
            final LikePlaceStorage likePlaceStorage = likePlaceStorageBuilder.defaultLikePlaceStorage(member).build(List.of(likePlace1, likePlace2, likePlace3));

            final Long notExistLikePlaceStorageId = -1L;

            final LikePlaceDeleteRequest request = new LikePlaceDeleteRequest(notExistLikePlaceStorageId, List.of(likePlace1.getId(), likePlace2.getId()));

            // when
            final ExtractableResponse<Response> response = DELETE_LIKE_PLACE_REQUEST(accessToken, request);

            // then
            assertSoftly(softly -> {
                softly.assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
                softly.assertThat(response.jsonPath().getString("errorMessage")).contains("해당하는 장소 보관함을 찾을 수 없습니다.");
            });
        }

        @Test
        @DisplayName("장소 보관함에 해당 장소 리스트가 없으면 실패한다.")
        void fail_not_exist_like_place_in_like_place_storage() {
            // given
            final Member member = memberBuilder.defaultMember().build();
            final String accessToken = jwtService.createAccessToken(member.getId());

            final LikePlace likePlace1 = likePlaceBuilder.defaultLikePlace(member).photoReferences(List.of("reference1")).name("베이커리 성수").build();
            final LikePlace likePlace2 = likePlaceBuilder.defaultLikePlace(member).photoReferences(List.of("reference2")).name("스타벅스 성수점").build();
            final LikePlace likePlace3 = likePlaceBuilder.defaultLikePlace(member).photoReferences(List.of("reference3")).name("성수건설").build();
            final LikePlaceStorage likePlaceStorage = likePlaceStorageBuilder.defaultLikePlaceStorage(member).build(List.of(likePlace1, likePlace2, likePlace3));

            final LikePlace notExistInStorageLikePlace = likePlaceBuilder.defaultLikePlace(member).photoReferences(List.of("reference4")).name("스타벅스 부평점").build();

            final LikePlaceDeleteRequest request = new LikePlaceDeleteRequest(likePlaceStorage.getId(), List.of(likePlace1.getId(), notExistInStorageLikePlace.getId()));

            // when
            final ExtractableResponse<Response> response = DELETE_LIKE_PLACE_REQUEST(accessToken, request);

            // then
            assertSoftly(softly -> {
                softly.assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
                softly.assertThat(response.jsonPath().getString("errorMessage")).contains("장소 보관함에 해당하는 장소가 존재하지 않습니다.");
            });
        }
    }

    private static ExtractableResponse<Response> ADD_LIKE_PLACE_STORAGE_REQUEST(final String accessToken, final LikePlaceStorageAddRequest request) {
        return RestAssured.given().log().all()
                .header(HttpHeaders.AUTHORIZATION, JWT_PREFIX + accessToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .when().log().all()
                .post("/like-place-storage")
                .then().log().all()
                .extract();
    }

    private static ExtractableResponse<Response> DELETE_LIKE_PLACE_REQUEST(final String accessToken, final LikePlaceDeleteRequest request) {
        return RestAssured.given().log().all()
                .header(HttpHeaders.AUTHORIZATION, JWT_PREFIX + accessToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .when().log().all()
                .delete("/like-place-storage/like-places")
                .then().log().all()
                .extract();
    }
}
