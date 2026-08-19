package com.agri.market.security.oauth2.factory;

import com.agri.market.security.oauth2.entity.OAuthProvider;
import com.agri.market.security.oauth2.strategy.OAuthProviderStrategy;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
public class OAuthStrategyFactory {

    private final Map<OAuthProvider, OAuthProviderStrategy> strategies;

    public OAuthStrategyFactory(
            final List<OAuthProviderStrategy> strategyList
    ) {
        this.strategies = new EnumMap<>(OAuthProvider.class);

        strategyList.forEach(strategy ->
                strategies.put(
                        strategy.getProvider(),
                        strategy
                )
        );
    }

    public OAuthProviderStrategy getStrategy(
            final OAuthProvider provider
    ) {
        OAuthProviderStrategy strategy =
                strategies.get(provider);

        if (strategy == null) {
            throw new IllegalArgumentException(
                    "Unsupported OAuth provider: " + provider
            );
        }

        return strategy;
    }
}