package com.agri.market.security.oauth2.client;

import com.agri.market.security.oauth2.dto.GitHubEmailResponse;
import com.agri.market.security.oauth2.dto.GitHubUserResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class GitHubOAuthClient {

    private static final String GITHUB_API_BASE_URL =
            "https://api.github.com";

    private static final String USER_ENDPOINT =
            "/user";

    private static final String EMAILS_ENDPOINT =
            "/user/emails";

    private final RestClient.Builder restClientBuilder;

    public GitHubUserResponse getUser(
            final String accessToken
    ) {

        log.debug("Requesting GitHub user profile");

        return createClient()
                .get()
                .uri(USER_ENDPOINT)
                .headers(headers -> applyAuthorization(
                        headers,
                        accessToken
                ))
                .retrieve()
                .body(GitHubUserResponse.class);
    }

    public List<GitHubEmailResponse> getUserEmails(
            final String accessToken
    ) {

        log.debug("Requesting GitHub user email information");

        final GitHubEmailResponse[] response =
                createClient()
                        .get()
                        .uri(EMAILS_ENDPOINT)
                        .headers(headers -> applyAuthorization(
                                headers,
                                accessToken
                        ))
                        .retrieve()
                        .body(GitHubEmailResponse[].class);

        return response == null
                ? List.of()
                : Arrays.asList(response);
    }

    private RestClient createClient() {

        return restClientBuilder
                .baseUrl(GITHUB_API_BASE_URL)
                .defaultHeader(
                        HttpHeaders.ACCEPT,
                        MediaType.APPLICATION_JSON_VALUE
                )
                .build();
    }

    private void applyAuthorization(
            final HttpHeaders headers,
            final String accessToken
    ) {

        headers.setBearerAuth(accessToken);
    }
}