package haru.harudongseon.likeplace.domain.placedetails;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class ParkingAvailableTest {

    @Nested
    @DisplayName("name에 해당하는 ParkingAvailable 조회 시")
    class FindValue {

        @ParameterizedTest
        @ValueSource(strings = {"NONE", "TRUE", "FALSE"})
        @DisplayName("조회에 성공한다.")
        void success(final String name) {
            // when
            final ParkingAvailable findParkingAvailable = ParkingAvailable.findValue(name);

            // then
            assertThat(findParkingAvailable.name()).isEqualTo(name);
        }

        @Test
        @DisplayName("name에 해당하는 ParkingAvailable이 존재하지 않으면 예외가 발생한다.")
        void throws_not_exist_parking_available() {
            // given
            final String notExistName = "notExist";

            // when & then
            assertThatThrownBy(() -> ParkingAvailable.findValue(notExistName))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("해당하는 ParkingAvailable이 없습니다.");
        }
    }
}
