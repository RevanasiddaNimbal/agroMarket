package com.agri.market.security.oauth2.strategy;

import com.agri.market.security.oauth2.client.GitHubOAuthClient;
import com.agri.market.security.oauth2.dto.GitHubEmailResponse;
import com.agri.market.security.oauth2.dto.GitHubUserResponse;
import com.agri.market.security.oauth2.entity.OAuthProvider;
import com.agri.market.security.oauth2.model.OAuthProviderContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.OAuth2AccessToken;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("GitHubOAuthStrategy")
class GitHubOAuthStrategyTest {

    private static final String ACCESS_TOKEN =
            "github-access-token";

    private static final Long USER_ID =
            123456L;

    private static final String LOGIN =
            "revanasidda";

    private static final String FULL_NAME =
            "Revanasidda Nimbal";

    private static final String AVATAR_URL =
            "https://github.com/avatar.png";

    private static final String EMAIL =
            "revanasidda@example.com";

    @Mock
    private GitHubOAuthClient githubOAuthClient;

    @Mock
    private OAuth2AuthorizedClient authorizedClient;

    @Mock
    private OAuth2AccessToken accessToken;

    private GitHubOAuthStrategy strategy;

    @BeforeEach
    void setUp() {

        strategy =
                new GitHubOAuthStrategy(
                        githubOAuthClient
                );
    }

    private OAuthProviderContext createContext() {

        when(authorizedClient.getAccessToken())
                .thenReturn(accessToken);

        when(accessToken.getTokenValue())
                .thenReturn(ACCESS_TOKEN);

        return OAuthProviderContext.builder()
                .authorizedClient(authorizedClient)
                .build();
    }


    @Test
    @DisplayName("should return GitHub provider")
    void shouldReturnGitHubProvider() {

        assertThat(strategy.getProvider())
                .isEqualTo(OAuthProvider.GITHUB);
    }


    @Nested
    @DisplayName("getProviderUserId")
    class GetProviderUserIdTests {

        @Test
        @DisplayName("should return GitHub user ID")
        void shouldReturnGitHubUserId() {

            OAuthProviderContext context =
                    createContext();

            GitHubUserResponse user =
                    GitHubUserResponse.builder()
                            .id(USER_ID)
                            .login(LOGIN)
                            .name(FULL_NAME)
                            .avatarUrl(AVATAR_URL)
                            .build();

            when(githubOAuthClient.getUser(ACCESS_TOKEN))
                    .thenReturn(user);

            String result =
                    strategy.getProviderUserId(context);

            assertThat(result)
                    .isEqualTo(
                            String.valueOf(USER_ID)
                    );

            verify(githubOAuthClient)
                    .getUser(ACCESS_TOKEN);
        }
    }


    @Nested
    @DisplayName("getEmail")
    class GetEmailTests {

        @Test
        @DisplayName("should return primary verified email")
        void shouldReturnPrimaryVerifiedEmail() {

            OAuthProviderContext context =
                    createContext();

            GitHubEmailResponse secondaryEmail =
                    GitHubEmailResponse.builder()
                            .email("secondary@example.com")
                            .verified(true)
                            .primary(false)
                            .build();

            GitHubEmailResponse primaryEmail =
                    GitHubEmailResponse.builder()
                            .email(EMAIL)
                            .verified(true)
                            .primary(true)
                            .build();

            when(githubOAuthClient.getUserEmails(ACCESS_TOKEN))
                    .thenReturn(
                            List.of(
                                    secondaryEmail,
                                    primaryEmail
                            )
                    );

            String result =
                    strategy.getEmail(context);

            assertThat(result)
                    .isEqualTo(EMAIL);

            verify(githubOAuthClient)
                    .getUserEmails(ACCESS_TOKEN);
        }

        @Test
        @DisplayName("should return verified email when only one verified email exists")
        void shouldReturnVerifiedEmail() {

            OAuthProviderContext context =
                    createContext();

            GitHubEmailResponse email =
                    GitHubEmailResponse.builder()
                            .email(EMAIL)
                            .verified(true)
                            .primary(false)
                            .build();

            when(githubOAuthClient.getUserEmails(ACCESS_TOKEN))
                    .thenReturn(
                            List.of(email)
                    );

            String result =
                    strategy.getEmail(context);

            assertThat(result)
                    .isEqualTo(EMAIL);
        }

        @Test
        @DisplayName("should return null when no verified email exists")
        void shouldReturnNullWhenNoVerifiedEmailExists() {

            OAuthProviderContext context =
                    createContext();

            GitHubEmailResponse email =
                    GitHubEmailResponse.builder()
                            .email(EMAIL)
                            .verified(false)
                            .primary(true)
                            .build();

            when(githubOAuthClient.getUserEmails(ACCESS_TOKEN))
                    .thenReturn(
                            List.of(email)
                    );

            String result =
                    strategy.getEmail(context);

            assertThat(result)
                    .isNull();
        }

        @Test
        @DisplayName("should return null when GitHub returns no emails")
        void shouldReturnNullWhenNoEmailsExist() {

            OAuthProviderContext context =
                    createContext();

            when(githubOAuthClient.getUserEmails(ACCESS_TOKEN))
                    .thenReturn(
                            List.of()
                    );

            String result =
                    strategy.getEmail(context);

            assertThat(result)
                    .isNull();
        }

        @Test
        @DisplayName("should ignore unverified primary email")
        void shouldIgnoreUnverifiedPrimaryEmail() {

            OAuthProviderContext context =
                    createContext();

            GitHubEmailResponse unverifiedPrimary =
                    GitHubEmailResponse.builder()
                            .email("primary@example.com")
                            .verified(false)
                            .primary(true)
                            .build();

            GitHubEmailResponse verifiedSecondary =
                    GitHubEmailResponse.builder()
                            .email(EMAIL)
                            .verified(true)
                            .primary(false)
                            .build();

            when(githubOAuthClient.getUserEmails(ACCESS_TOKEN))
                    .thenReturn(
                            List.of(
                                    unverifiedPrimary,
                                    verifiedSecondary
                            )
                    );

            String result =
                    strategy.getEmail(context);

            assertThat(result)
                    .isEqualTo(EMAIL);
        }
    }


    @Nested
    @DisplayName("getFullName")
    class GetFullNameTests {

        @Test
        @DisplayName("should return GitHub name when available")
        void shouldReturnGitHubNameWhenAvailable() {

            OAuthProviderContext context =
                    createContext();

            GitHubUserResponse user =
                    GitHubUserResponse.builder()
                            .id(USER_ID)
                            .login(LOGIN)
                            .name(FULL_NAME)
                            .avatarUrl(AVATAR_URL)
                            .build();

            when(githubOAuthClient.getUser(ACCESS_TOKEN))
                    .thenReturn(user);

            String result =
                    strategy.getFullName(context);

            assertThat(result)
                    .isEqualTo(FULL_NAME);

            verify(githubOAuthClient)
                    .getUser(ACCESS_TOKEN);
        }

        @Test
        @DisplayName("should fall back to login when name is null")
        void shouldFallBackToLoginWhenNameIsNull() {

            OAuthProviderContext context =
                    createContext();

            GitHubUserResponse user =
                    GitHubUserResponse.builder()
                            .id(USER_ID)
                            .login(LOGIN)
                            .name(null)
                            .avatarUrl(AVATAR_URL)
                            .build();

            when(githubOAuthClient.getUser(ACCESS_TOKEN))
                    .thenReturn(user);

            String result =
                    strategy.getFullName(context);

            assertThat(result)
                    .isEqualTo(LOGIN);
        }

        @Test
        @DisplayName("should fall back to login when name is blank")
        void shouldFallBackToLoginWhenNameIsBlank() {

            OAuthProviderContext context =
                    createContext();

            GitHubUserResponse user =
                    GitHubUserResponse.builder()
                            .id(USER_ID)
                            .login(LOGIN)
                            .name("   ")
                            .avatarUrl(AVATAR_URL)
                            .build();

            when(githubOAuthClient.getUser(ACCESS_TOKEN))
                    .thenReturn(user);

            String result =
                    strategy.getFullName(context);

            assertThat(result)
                    .isEqualTo(LOGIN);
        }
    }


    @Nested
    @DisplayName("getProfilePictureUrl")
    class GetProfilePictureUrlTests {

        @Test
        @DisplayName("should return GitHub avatar URL")
        void shouldReturnGitHubAvatarUrl() {

            OAuthProviderContext context =
                    createContext();

            GitHubUserResponse user =
                    GitHubUserResponse.builder()
                            .id(USER_ID)
                            .login(LOGIN)
                            .name(FULL_NAME)
                            .avatarUrl(AVATAR_URL)
                            .build();

            when(githubOAuthClient.getUser(ACCESS_TOKEN))
                    .thenReturn(user);

            String result =
                    strategy.getProfilePictureUrl(context);

            assertThat(result)
                    .isEqualTo(AVATAR_URL);

            verify(githubOAuthClient)
                    .getUser(ACCESS_TOKEN);
        }
    }


    @Nested
    @DisplayName("isEmailVerified")
    class IsEmailVerifiedTests {

        @Test
        @DisplayName("should return true when verified email exists")
        void shouldReturnTrueWhenVerifiedEmailExists() {

            OAuthProviderContext context =
                    createContext();

            GitHubEmailResponse email =
                    GitHubEmailResponse.builder()
                            .email(EMAIL)
                            .verified(true)
                            .primary(false)
                            .build();

            when(githubOAuthClient.getUserEmails(ACCESS_TOKEN))
                    .thenReturn(
                            List.of(email)
                    );

            boolean result =
                    strategy.isEmailVerified(context);

            assertThat(result)
                    .isTrue();

            verify(githubOAuthClient)
                    .getUserEmails(ACCESS_TOKEN);
        }

        @Test
        @DisplayName("should return false when no verified email exists")
        void shouldReturnFalseWhenNoVerifiedEmailExists() {

            OAuthProviderContext context =
                    createContext();

            GitHubEmailResponse email =
                    GitHubEmailResponse.builder()
                            .email(EMAIL)
                            .verified(false)
                            .primary(true)
                            .build();

            when(githubOAuthClient.getUserEmails(ACCESS_TOKEN))
                    .thenReturn(
                            List.of(email)
                    );

            boolean result =
                    strategy.isEmailVerified(context);

            assertThat(result)
                    .isFalse();

            verify(githubOAuthClient)
                    .getUserEmails(ACCESS_TOKEN);
        }

        @Test
        @DisplayName("should return false when email list is empty")
        void shouldReturnFalseWhenEmailListIsEmpty() {

            OAuthProviderContext context =
                    createContext();

            when(githubOAuthClient.getUserEmails(ACCESS_TOKEN))
                    .thenReturn(
                            List.of()
                    );

            boolean result =
                    strategy.isEmailVerified(context);

            assertThat(result)
                    .isFalse();
        }
    }
}