package com.agri.market.security.oauth2.strategy;

import com.agri.market.security.oauth2.client.GitHubOAuthClient;
import com.agri.market.security.oauth2.dto.GitHubEmailResponse;
import com.agri.market.security.oauth2.dto.GitHubUserResponse;
import com.agri.market.security.oauth2.entity.OAuthProvider;
import com.agri.market.security.oauth2.model.OAuthProviderContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class GitHubOAuthStrategy implements OAuthProviderStrategy {

    private final GitHubOAuthClient githubOAuthClient;

    @Override
    public OAuthProvider getProvider() {
        return OAuthProvider.GITHUB;
    }

    @Override
    public String getProviderUserId(
            final OAuthProviderContext context
    ) {

        final GitHubUserResponse user =
                getGitHubUser(context);

        return String.valueOf(user.getId());
    }

    @Override
    public String getEmail(
            final OAuthProviderContext context
    ) {

        return findPrimaryVerifiedEmail(
                context
        );
    }

    @Override
    public String getFullName(
            final OAuthProviderContext context
    ) {

        final GitHubUserResponse user =
                getGitHubUser(context);

        if (user.getName() != null &&
                !user.getName().isBlank()) {

            return user.getName();
        }

        return user.getLogin();
    }

    @Override
    public String getProfilePictureUrl(
            final OAuthProviderContext context
    ) {

        final GitHubUserResponse user =
                getGitHubUser(context);

        return user.getAvatarUrl();
    }

    @Override
    public boolean isEmailVerified(
            final OAuthProviderContext context
    ) {

        return findVerifiedEmail(
                context
        ) != null;
    }

    private GitHubUserResponse getGitHubUser(
            final OAuthProviderContext context
    ) {

        final String accessToken =
                context.getAuthorizedClient()
                        .getAccessToken()
                        .getTokenValue();

        return githubOAuthClient.getUser(
                accessToken
        );
    }

    private String findPrimaryVerifiedEmail(
            final OAuthProviderContext context
    ) {

        final GitHubEmailResponse email =
                findVerifiedEmail(context);

        return email == null
                ? null
                : email.getEmail();
    }

    private GitHubEmailResponse findVerifiedEmail(
            final OAuthProviderContext context
    ) {

        final String accessToken =
                context.getAuthorizedClient()
                        .getAccessToken()
                        .getTokenValue();

        final List<GitHubEmailResponse> emails =
                githubOAuthClient.getUserEmails(
                        accessToken
                );

        return emails.stream()
                .filter(GitHubEmailResponse::isVerified)
                .sorted(
                        Comparator.comparing(
                                GitHubEmailResponse::isPrimary
                        ).reversed()
                )
                .findFirst()
                .orElse(null);
    }
}