package com.agri.market.security.oauth2.strategy;

import com.agri.market.security.oauth2.entity.OAuthProvider;
import com.agri.market.security.oauth2.model.OAuthProviderContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.core.user.OAuth2User;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("GoogleOAuthStrategy")
class GoogleOAuthStrategyTest {

    private static final String PROVIDER_USER_ID =
            "google-user-123";

    private static final String EMAIL =
            "user@gmail.com";

    private static final String FULL_NAME =
            "Revanasidda Nimbal";

    private static final String PROFILE_PICTURE_URL =
            "https://example.com/profile.jpg";
    private final GoogleOAuthStrategy strategy =
            new GoogleOAuthStrategy();
    @Mock
    private OAuth2User oauth2User;
    private OAuthProviderContext context;

    private void createContext() {
        context =
                OAuthProviderContext.builder()
                        .oauth2User(oauth2User)
                        .build();
    }

    @Test
    @DisplayName("should return Google provider")
    void shouldReturnGoogleProvider() {

        assertThat(strategy.getProvider())
                .isEqualTo(OAuthProvider.GOOGLE);
    }

    @Nested
    @DisplayName("getProviderUserId")
    class GetProviderUserIdTests {

        @Test
        @DisplayName("should return Google provider user ID")
        void shouldReturnProviderUserId() {

            createContext();

            when(oauth2User.getAttribute("sub"))
                    .thenReturn(PROVIDER_USER_ID);

            String result =
                    strategy.getProviderUserId(context);

            assertThat(result)
                    .isEqualTo(PROVIDER_USER_ID);

            verify(oauth2User)
                    .getAttribute("sub");
        }

        @Test
        @DisplayName("should return null when provider user ID is missing")
        void shouldReturnNullWhenProviderUserIdMissing() {

            createContext();

            when(oauth2User.getAttribute("sub"))
                    .thenReturn(null);

            String result =
                    strategy.getProviderUserId(context);

            assertThat(result)
                    .isNull();

            verify(oauth2User)
                    .getAttribute("sub");
        }
    }

    @Nested
    @DisplayName("getEmail")
    class GetEmailTests {

        @Test
        @DisplayName("should return Google email")
        void shouldReturnEmail() {

            createContext();

            when(oauth2User.getAttribute("email"))
                    .thenReturn(EMAIL);

            String result =
                    strategy.getEmail(context);

            assertThat(result)
                    .isEqualTo(EMAIL);

            verify(oauth2User)
                    .getAttribute("email");
        }

        @Test
        @DisplayName("should return null when email is missing")
        void shouldReturnNullWhenEmailMissing() {

            createContext();

            when(oauth2User.getAttribute("email"))
                    .thenReturn(null);

            String result =
                    strategy.getEmail(context);

            assertThat(result)
                    .isNull();
        }
    }

    @Nested
    @DisplayName("getFullName")
    class GetFullNameTests {

        @Test
        @DisplayName("should return Google full name")
        void shouldReturnFullName() {

            createContext();

            when(oauth2User.getAttribute("name"))
                    .thenReturn(FULL_NAME);

            String result =
                    strategy.getFullName(context);

            assertThat(result)
                    .isEqualTo(FULL_NAME);

            verify(oauth2User)
                    .getAttribute("name");
        }

        @Test
        @DisplayName("should return null when name is missing")
        void shouldReturnNullWhenNameMissing() {

            createContext();

            when(oauth2User.getAttribute("name"))
                    .thenReturn(null);

            String result =
                    strategy.getFullName(context);

            assertThat(result)
                    .isNull();
        }
    }

    @Nested
    @DisplayName("getProfilePictureUrl")
    class GetProfilePictureUrlTests {

        @Test
        @DisplayName("should return Google profile picture URL")
        void shouldReturnProfilePictureUrl() {

            createContext();

            when(oauth2User.getAttribute("picture"))
                    .thenReturn(PROFILE_PICTURE_URL);

            String result =
                    strategy.getProfilePictureUrl(context);

            assertThat(result)
                    .isEqualTo(PROFILE_PICTURE_URL);

            verify(oauth2User)
                    .getAttribute("picture");
        }

        @Test
        @DisplayName("should return null when profile picture is missing")
        void shouldReturnNullWhenProfilePictureMissing() {

            createContext();

            when(oauth2User.getAttribute("picture"))
                    .thenReturn(null);

            String result =
                    strategy.getProfilePictureUrl(context);

            assertThat(result)
                    .isNull();
        }
    }

    @Nested
    @DisplayName("isEmailVerified")
    class IsEmailVerifiedTests {

        @Test
        @DisplayName("should return true when email is verified")
        void shouldReturnTrueWhenEmailIsVerified() {

            createContext();

            when(oauth2User.getAttribute("email_verified"))
                    .thenReturn(Boolean.TRUE);

            boolean result =
                    strategy.isEmailVerified(context);

            assertThat(result)
                    .isTrue();

            verify(oauth2User)
                    .getAttribute("email_verified");
        }

        @Test
        @DisplayName("should return false when email is not verified")
        void shouldReturnFalseWhenEmailIsNotVerified() {

            createContext();

            when(oauth2User.getAttribute("email_verified"))
                    .thenReturn(Boolean.FALSE);

            boolean result =
                    strategy.isEmailVerified(context);

            assertThat(result)
                    .isFalse();
        }

        @Test
        @DisplayName("should return false when email_verified attribute is null")
        void shouldReturnFalseWhenVerificationAttributeIsNull() {

            createContext();

            when(oauth2User.getAttribute("email_verified"))
                    .thenReturn(null);

            boolean result =
                    strategy.isEmailVerified(context);

            assertThat(result)
                    .isFalse();
        }
    }
}