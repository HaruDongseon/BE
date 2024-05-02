package haru.harudongseon.place.domain.placedetails;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class ReservableTest {

    @Nested
    @DisplayName("name에 해당하는 Reservable 조회 시")
    class FindValue {

        @ParameterizedTest
        @ValueSource(strings = {"NONE", "TRUE", "FALSE"})
        @DisplayName("조회에 성공한다.")
        void success(final String name) {
            // when
            final Reservable findReservable = Reservable.findValue(name);

            // then
            assertThat(findReservable.name()).isEqualTo(name);
        }

        @Test
        @DisplayName("name에 해당하는 Reservable이 존재하지 않으면 예외가 발생한다.")
        void throws_not_exist_reservable() {
            // given
            final String notExistName = "notExist";

            // when & then
            assertThatThrownBy(() -> Reservable.findValue(notExistName))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("해당하는 Reservable이 없습니다.");
        }
    }
}
