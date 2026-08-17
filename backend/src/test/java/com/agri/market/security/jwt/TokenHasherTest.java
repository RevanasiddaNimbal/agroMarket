package com.agri.market.security.jwt;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("TokenHasher")
class TokenHasherTest {

    private final TokenHasher tokenHasher = new TokenHasher();

    @Test
    void shouldReturnSameHashForSameInput() {
        String first = tokenHasher.hash("token-value");
        String second = tokenHasher.hash("token-value");

        assertThat(first).isEqualTo(second);
    }

    @Test
    void shouldReturnDifferentHashForDifferentInput() {
        String first = tokenHasher.hash("token-value-1");
        String second = tokenHasher.hash("token-value-2");

        assertThat(first).isNotEqualTo(second);
    }

    @Test
    void shouldReturnSha256HexHash() {
        String hash = tokenHasher.hash("");

        assertThat(hash).hasSize(64);
        assertThat(hash).matches("[0-9a-f]{64}");
    }

    @Test
    void shouldThrowWhenInputIsNull() {
        assertThatThrownBy(() -> tokenHasher.hash(null))
                .isInstanceOf(NullPointerException.class);
    }
}

