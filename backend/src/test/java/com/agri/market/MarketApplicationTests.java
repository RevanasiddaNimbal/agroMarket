package com.agri.market;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Application smoke test")
class MarketApplicationTests {

    @Test
    void shouldLoadApplicationClass() {
        assertThat(Application.class).isNotNull();
    }
}

