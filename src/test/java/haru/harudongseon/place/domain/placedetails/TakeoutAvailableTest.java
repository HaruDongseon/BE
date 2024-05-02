package haru.harudongseon.place.domain.placedetails;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class TakeoutAvailableTest {

    @Nested
    @DisplayName("name에 해당하는 TakeoutAvailable 조회 시")
    class FindValue {

        @ParameterizedTest
        @ValueSource(strings = {"NONE", "TRUE", "FALSE"})
        @DisplayName("조회에 성공한다.")
        void success(final String name) {
            // when
            final TakeoutAvailable findTakeoutAvailable = TakeoutAvailable.findValue(name);

            // then
            assertThat(findTakeoutAvailable.name()).isEqualTo(name);
        }

        @Test
        @DisplayName("name에 해당하는 TakeoutAvailable이 존재하지 않으면 예외가 발생한다.")
        void throws_not_exist_takeout_available() {
            // given
            final String notExistName = "notExist";

            // when & then
            assertThatThrownBy(() -> TakeoutAvailable.findValue(notExistName))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("해당하는 TakeoutAvailable이 없습니다.");
        }
    }
}
