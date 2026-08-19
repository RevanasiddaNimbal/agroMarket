package com.agri.market.security.oauth2.factory;

import com.agri.market.security.oauth2.entity.OAuthProvider;
import com.agri.market.security.oauth2.strategy.OAuthProviderStrategy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class OAuthStrategyFactoryTest {

    @Nested
    @DisplayName("Constructor - strategy registration")
    class ConstructorTests {

        @Test
        @DisplayName("Should register single strategy successfully")
        void shouldRegisterSingleStrategy() {
            OAuthProviderStrategy googleStrategy = mock(OAuthProviderStrategy.class);
            when(googleStrategy.getProvider()).thenReturn(OAuthProvider.GOOGLE);

            OAuthStrategyFactory factory =
                    new OAuthStrategyFactory(List.of(googleStrategy));

            OAuthProviderStrategy resolved = factory.getStrategy(OAuthProvider.GOOGLE);

            assertThat(resolved).isEqualTo(googleStrategy);
        }

        @Test
        @DisplayName("Should register multiple strategies successfully")
        void shouldRegisterMultipleStrategies() {
            OAuthProviderStrategy googleStrategy = mock(OAuthProviderStrategy.class);
            when(googleStrategy.getProvider()).thenReturn(OAuthProvider.GOOGLE);

            OAuthProviderStrategy githubStrategy = mock(OAuthProviderStrategy.class);
            when(githubStrategy.getProvider()).thenReturn(OAuthProvider.GITHUB);

            OAuthStrategyFactory factory =
                    new OAuthStrategyFactory(List.of(googleStrategy, githubStrategy));

            assertThat(factory.getStrategy(OAuthProvider.GOOGLE)).isEqualTo(googleStrategy);
            assertThat(factory.getStrategy(OAuthProvider.GITHUB)).isEqualTo(githubStrategy);
        }

        @Test
        @DisplayName("Should handle empty strategy list without throwing")
        void shouldHandleEmptyStrategyList() {
            OAuthStrategyFactory factory =
                    new OAuthStrategyFactory(Collections.emptyList());

            assertThatThrownBy(() -> factory.getStrategy(OAuthProvider.GOOGLE))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("Should let later strategy overwrite earlier one for same provider")
        void shouldOverwriteDuplicateProviderStrategy() {
            OAuthProviderStrategy firstStrategy = mock(OAuthProviderStrategy.class);
            when(firstStrategy.getProvider()).thenReturn(OAuthProvider.GOOGLE);

            OAuthProviderStrategy secondStrategy = mock(OAuthProviderStrategy.class);
            when(secondStrategy.getProvider()).thenReturn(OAuthProvider.GOOGLE);

            OAuthStrategyFactory factory =
                    new OAuthStrategyFactory(List.of(firstStrategy, secondStrategy));

            OAuthProviderStrategy resolved = factory.getStrategy(OAuthProvider.GOOGLE);

            assertThat(resolved).isEqualTo(secondStrategy);
        }
    }

    @Nested
    @DisplayName("getStrategy - resolution scenarios")
    class GetStrategyTests {

        @Test
        @DisplayName("Should return correct strategy for registered provider")
        void shouldReturnCorrectStrategyForRegisteredProvider() {
            OAuthProviderStrategy githubStrategy = mock(OAuthProviderStrategy.class);
            when(githubStrategy.getProvider()).thenReturn(OAuthProvider.GITHUB);

            OAuthStrategyFactory factory =
                    new OAuthStrategyFactory(List.of(githubStrategy));

            assertThat(factory.getStrategy(OAuthProvider.GITHUB)).isSameAs(githubStrategy);
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException for unregistered provider")
        void shouldThrowExceptionForUnregisteredProvider() {
            OAuthProviderStrategy googleStrategy = mock(OAuthProviderStrategy.class);
            when(googleStrategy.getProvider()).thenReturn(OAuthProvider.GOOGLE);

            OAuthStrategyFactory factory =
                    new OAuthStrategyFactory(List.of(googleStrategy));

            assertThatThrownBy(() -> factory.getStrategy(OAuthProvider.GITHUB))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Unsupported OAuth provider")
                    .hasMessageContaining("GITHUB");
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when provider is null")
        void shouldThrowExceptionWhenProviderIsNull() {
            OAuthProviderStrategy googleStrategy = mock(OAuthProviderStrategy.class);
            when(googleStrategy.getProvider()).thenReturn(OAuthProvider.GOOGLE);

            OAuthStrategyFactory factory =
                    new OAuthStrategyFactory(List.of(googleStrategy));

            assertThatThrownBy(() -> factory.getStrategy(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Unsupported OAuth provider");
        }

        @Test
        @DisplayName("Should return same strategy instance on repeated calls")
        void shouldReturnSameInstanceOnRepeatedCalls() {
            OAuthProviderStrategy googleStrategy = mock(OAuthProviderStrategy.class);
            when(googleStrategy.getProvider()).thenReturn(OAuthProvider.GOOGLE);

            OAuthStrategyFactory factory =
                    new OAuthStrategyFactory(List.of(googleStrategy));

            OAuthProviderStrategy firstCall = factory.getStrategy(OAuthProvider.GOOGLE);
            OAuthProviderStrategy secondCall = factory.getStrategy(OAuthProvider.GOOGLE);

            assertThat(firstCall).isSameAs(secondCall);
        }

        @Test
        @DisplayName("Should resolve independently for each distinct registered provider")
        void shouldResolveIndependentlyForEachProvider() {
            OAuthProviderStrategy googleStrategy = mock(OAuthProviderStrategy.class);
            when(googleStrategy.getProvider()).thenReturn(OAuthProvider.GOOGLE);

            OAuthProviderStrategy githubStrategy = mock(OAuthProviderStrategy.class);
            when(githubStrategy.getProvider()).thenReturn(OAuthProvider.GITHUB);

            List<OAuthProviderStrategy> strategyList =
                    new ArrayList<>(List.of(googleStrategy, githubStrategy));

            OAuthStrategyFactory factory = new OAuthStrategyFactory(strategyList);

            assertThat(factory.getStrategy(OAuthProvider.GOOGLE)).isSameAs(googleStrategy);
            assertThat(factory.getStrategy(OAuthProvider.GITHUB)).isSameAs(githubStrategy);
        }
    }
}