package com.agri.market.security.oauth2.client;

import com.agri.market.security.oauth2.dto.GitHubEmailResponse;
import com.agri.market.security.oauth2.dto.GitHubUserResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@DisplayName("GitHubOAuthClient")
class GitHubOAuthClientTest {

    private static final String BASE_URL =
            "https://api.github.com";

    private static final String ACCESS_TOKEN =
            "github-access-token";

    private final RestClient.Builder restClientBuilder =
            RestClient.builder();

    private final MockRestServiceServer server =
            MockRestServiceServer.bindTo(restClientBuilder)
                    .build();

    private final GitHubOAuthClient githubOAuthClient =
            new GitHubOAuthClient(restClientBuilder);

    @Nested
    @DisplayName("getUser")
    class GetUserTests {

        @Test
        @DisplayName("should return GitHub user")
        void shouldReturnGitHubUser() {

            String response = """
                    {
                        "id": 12345,
                        "login": "testuser",
                        "name": "Test User",
                        "email": "test@example.com"
                    }
                    """;

            server.expect(
                            requestTo(BASE_URL + "/user")
                    )
                    .andExpect(method(GET))
                    .andExpect(
                            header(
                                    HttpHeaders.AUTHORIZATION,
                                    "Bearer " + ACCESS_TOKEN
                            )
                    )
                    .andExpect(
                            header(
                                    HttpHeaders.ACCEPT,
                                    MediaType.APPLICATION_JSON_VALUE
                            )
                    )
                    .andRespond(
                            withSuccess(
                                    response,
                                    MediaType.APPLICATION_JSON
                            )
                    );

            GitHubUserResponse result =
                    githubOAuthClient.getUser(ACCESS_TOKEN);

            assertNotNull(result);
            assertEquals(12345, result.getId());
            assertEquals("testuser", result.getLogin());
            assertEquals("Test User", result.getName());
            assertEquals("test@example.com", result.getEmail());

            server.verify();
        }

        @Test
        @DisplayName("should send access token as bearer token")
        void shouldSendAccessTokenAsBearerToken() {

            String response = """
                    {
                        "id": 12345,
                        "login": "testuser",
                        "name": "Test User"
                    }
                    """;

            server.expect(
                            requestTo(BASE_URL + "/user")
                    )
                    .andExpect(method(GET))
                    .andExpect(
                            header(
                                    HttpHeaders.AUTHORIZATION,
                                    "Bearer " + ACCESS_TOKEN
                            )
                    )
                    .andRespond(
                            withSuccess(
                                    response,
                                    MediaType.APPLICATION_JSON
                            )
                    );

            GitHubUserResponse result =
                    githubOAuthClient.getUser(ACCESS_TOKEN);

            assertNotNull(result);

            server.verify();
        }
    }

    @Nested
    @DisplayName("getUserEmails")
    class GetUserEmailsTests {

        @Test
        @DisplayName("should return GitHub user emails")
        void shouldReturnGitHubUserEmails() {

            String response = """
                    [
                        {
                            "email": "primary@example.com",
                            "primary": true,
                            "verified": true
                        },
                        {
                            "email": "secondary@example.com",
                            "primary": false,
                            "verified": true
                        }
                    ]
                    """;

            server.expect(
                            requestTo(BASE_URL + "/user/emails")
                    )
                    .andExpect(method(GET))
                    .andExpect(
                            header(
                                    HttpHeaders.AUTHORIZATION,
                                    "Bearer " + ACCESS_TOKEN
                            )
                    )
                    .andExpect(
                            header(
                                    HttpHeaders.ACCEPT,
                                    MediaType.APPLICATION_JSON_VALUE
                            )
                    )
                    .andRespond(
                            withSuccess(
                                    response,
                                    MediaType.APPLICATION_JSON
                            )
                    );

            List<GitHubEmailResponse> result =
                    githubOAuthClient.getUserEmails(ACCESS_TOKEN);

            assertNotNull(result);
            assertEquals(2, result.size());
            assertEquals(
                    "primary@example.com",
                    result.get(0).getEmail()
            );
            assertEquals(
                    "secondary@example.com",
                    result.get(1).getEmail()
            );

            server.verify();
        }

        @Test
        @DisplayName("should return empty list when GitHub returns empty array")
        void shouldReturnEmptyListWhenGitHubReturnsEmptyArray() {

            server.expect(
                            requestTo(BASE_URL + "/user/emails")
                    )
                    .andExpect(method(GET))
                    .andExpect(
                            header(
                                    HttpHeaders.AUTHORIZATION,
                                    "Bearer " + ACCESS_TOKEN
                            )
                    )
                    .andRespond(
                            withSuccess(
                                    "[]",
                                    MediaType.APPLICATION_JSON
                            )
                    );

            List<GitHubEmailResponse> result =
                    githubOAuthClient.getUserEmails(ACCESS_TOKEN);

            assertNotNull(result);
            assertEquals(0, result.size());

            server.verify();
        }

        @Test
        @DisplayName("should send access token as bearer token")
        void shouldSendAccessTokenAsBearerToken() {

            server.expect(
                            requestTo(BASE_URL + "/user/emails")
                    )
                    .andExpect(method(GET))
                    .andExpect(
                            header(
                                    HttpHeaders.AUTHORIZATION,
                                    "Bearer " + ACCESS_TOKEN
                            )
                    )
                    .andRespond(
                            withSuccess(
                                    "[]",
                                    MediaType.APPLICATION_JSON
                            )
                    );

            List<GitHubEmailResponse> result =
                    githubOAuthClient.getUserEmails(ACCESS_TOKEN);

            assertNotNull(result);

            server.verify();
        }
    }
}