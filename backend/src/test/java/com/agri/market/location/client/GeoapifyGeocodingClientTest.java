package com.agri.market.location.client;

import com.agri.market.common.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClient;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GeoapifyGeocodingClientTest {

    @Mock
    private RestClient.Builder restClientBuilder;

    @Mock
    private RestClient restClient;

    @Mock
    private RestClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private RestClient.ResponseSpec responseSpec;

    private GeoapifyGeocodingClient client;

    @BeforeEach
    void setUp() {
        client = new GeoapifyGeocodingClient(restClientBuilder);

        ReflectionTestUtils.setField(
                client,
                "baseUrl",
                "https://api.geoapify.com"
        );

        ReflectionTestUtils.setField(
                client,
                "apiKey",
                "test-api-key"
        );

        ReflectionTestUtils.setField(
                client,
                "language",
                "en"
        );

        ReflectionTestUtils.setField(
                client,
                "searchLimit",
                5
        );
    }

    @Test
    void shouldReturnEmptyListWhenSearchQueryIsNull() {
        List<GeocodingResult> result = client.search(null);

        assertThat(result).isEmpty();

        verifyNoInteractions(restClientBuilder);
    }

    @Test
    void shouldReturnEmptyListWhenSearchQueryIsBlank() {
        List<GeocodingResult> result = client.search("   ");

        assertThat(result).isEmpty();

        verifyNoInteractions(restClientBuilder);
    }

    @Test
    void shouldReturnEmptyListWhenGeoapifySearchResponseIsNull() {
        mockRestClient();

        when(responseSpec.body(GeoapifyResponse.class))
                .thenReturn(null);

        List<GeocodingResult> result =
                client.search("Atharga");

        assertThat(result).isEmpty();
    }

    @Test
    void shouldReturnEmptyListWhenGeoapifySearchReturnsNoResults() {
        mockRestClient();

        GeoapifyResponse response = new GeoapifyResponse();
        response.setResults(List.of());

        when(responseSpec.body(GeoapifyResponse.class))
                .thenReturn(response);

        List<GeocodingResult> result =
                client.search("Atharga");

        assertThat(result).isEmpty();
    }

    @Test
    void shouldReturnSearchResults() {
        mockRestClient();

        GeoapifyFeature feature = new GeoapifyFeature();

        feature.setLat(16.9871578);
        feature.setLon(75.8854156);
        feature.setCountry("India");
        feature.setCountryCode("in");
        feature.setState("Karnataka");
        feature.setDistrict("Vijayapura");
        feature.setCity("Atharga");
        feature.setVillage("Atharga");
        feature.setPostcode("586112");
        feature.setFormatted("Atharga, Karnataka, India");

        GeoapifyResponse response = new GeoapifyResponse();
        response.setResults(List.of(feature));

        when(responseSpec.body(GeoapifyResponse.class))
                .thenReturn(response);

        List<GeocodingResult> result =
                client.search("Atharga");

        assertThat(result).hasSize(1);

        GeocodingResult geocodingResult = result.getFirst();

        assertThat(geocodingResult.getLatitude())
                .isEqualTo(16.9871578);

        assertThat(geocodingResult.getLongitude())
                .isEqualTo(75.8854156);

        assertThat(geocodingResult.getCountry())
                .isEqualTo("India");

        assertThat(geocodingResult.getCountryCode())
                .isEqualTo("in");

        assertThat(geocodingResult.getState())
                .isEqualTo("Karnataka");

        assertThat(geocodingResult.getDistrict())
                .isEqualTo("Vijayapura");

        assertThat(geocodingResult.getCity())
                .isEqualTo("Atharga");

        assertThat(geocodingResult.getVillage())
                .isEqualTo("Atharga");

        assertThat(geocodingResult.getPincode())
                .isEqualTo("586112");

        assertThat(geocodingResult.getDisplayName())
                .isEqualTo("Atharga, Karnataka, India");
    }

    @Test
    void shouldIgnoreNullFeaturesFromSearchResponse() {
        mockRestClient();

        GeoapifyFeature feature = new GeoapifyFeature();

        feature.setLat(16.9871578);
        feature.setLon(75.8854156);
        feature.setCountry("India");
        feature.setCountryCode("in");
        feature.setState("Karnataka");
        feature.setCity("Atharga");

        GeoapifyResponse response = new GeoapifyResponse();

        response.setResults(
                Arrays.asList(null, feature)
        );

        when(responseSpec.body(GeoapifyResponse.class))
                .thenReturn(response);

        List<GeocodingResult> result =
                client.search("Atharga");

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getCity())
                .isEqualTo("Atharga");
    }

    @Test
    void shouldThrowGeocodingFailedWhenSearchRequestFails() {
        mockRestClient();

        when(responseSpec.body(GeoapifyResponse.class))
                .thenThrow(new RuntimeException("API failure"));

        assertThatThrownBy(() -> client.search("Atharga"))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void shouldThrowWhenSearchBaseUrlIsMissing() {
        ReflectionTestUtils.setField(
                client,
                "baseUrl",
                ""
        );

        assertThatThrownBy(() -> client.search("Atharga"))
                .isInstanceOf(BusinessException.class);

        verifyNoInteractions(restClientBuilder);
    }

    @Test
    void shouldThrowWhenSearchApiKeyIsMissing() {
        ReflectionTestUtils.setField(
                client,
                "apiKey",
                ""
        );

        assertThatThrownBy(() -> client.search("Atharga"))
                .isInstanceOf(BusinessException.class);

        verifyNoInteractions(restClientBuilder);
    }

    @Test
    void shouldThrowWhenReverseLatitudeIsInvalid() {
        assertThatThrownBy(() ->
                client.reverseGeocode(91.0, 75.8854156)
        ).isInstanceOf(BusinessException.class);

        verifyNoInteractions(restClientBuilder);
    }

    @Test
    void shouldThrowWhenReverseLongitudeIsInvalid() {
        assertThatThrownBy(() ->
                client.reverseGeocode(16.9871578, 181.0)
        ).isInstanceOf(BusinessException.class);

        verifyNoInteractions(restClientBuilder);
    }

    @Test
    void shouldThrowWhenReverseResponseIsNull() {
        mockRestClient();

        when(responseSpec.body(GeoapifyResponse.class))
                .thenReturn(null);

        assertThatThrownBy(() ->
                client.reverseGeocode(
                        16.9871578,
                        75.8854156
                )
        ).isInstanceOf(BusinessException.class);
    }

    @Test
    void shouldThrowWhenReverseResponseHasNoResults() {
        mockRestClient();

        GeoapifyResponse response = new GeoapifyResponse();
        response.setResults(List.of());

        when(responseSpec.body(GeoapifyResponse.class))
                .thenReturn(response);

        assertThatThrownBy(() ->
                client.reverseGeocode(
                        16.9871578,
                        75.8854156
                )
        ).isInstanceOf(BusinessException.class);
    }

    @Test
    void shouldThrowWhenReverseResponseContainsNullFeature() {
        mockRestClient();

        GeoapifyResponse response = new GeoapifyResponse();

        response.setResults(
                Collections.singletonList(null)
        );

        when(responseSpec.body(GeoapifyResponse.class))
                .thenReturn(response);

        assertThatThrownBy(() ->
                client.reverseGeocode(
                        16.9871578,
                        75.8854156
                )
        ).isInstanceOf(BusinessException.class);
    }

    @Test
    void shouldReturnReverseGeocodingResult() {
        mockRestClient();

        GeoapifyFeature feature = new GeoapifyFeature();

        feature.setLat(16.9871578);
        feature.setLon(75.8854156);
        feature.setCountry("India");
        feature.setCountryCode("in");
        feature.setState("Karnataka");
        feature.setDistrict("Vijayapura");
        feature.setCity("Atharga");
        feature.setVillage("Atharga");
        feature.setPostcode("586112");
        feature.setFormatted(
                "Atharga, Vijayapura, Karnataka, India"
        );

        GeoapifyResponse response = new GeoapifyResponse();
        response.setResults(List.of(feature));

        when(responseSpec.body(GeoapifyResponse.class))
                .thenReturn(response);

        GeocodingResult result =
                client.reverseGeocode(
                        16.9871578,
                        75.8854156
                );

        assertThat(result).isNotNull();

        assertThat(result.getLatitude())
                .isEqualTo(16.9871578);

        assertThat(result.getLongitude())
                .isEqualTo(75.8854156);

        assertThat(result.getCountry())
                .isEqualTo("India");

        assertThat(result.getCountryCode())
                .isEqualTo("in");

        assertThat(result.getState())
                .isEqualTo("Karnataka");

        assertThat(result.getDistrict())
                .isEqualTo("Vijayapura");

        assertThat(result.getCity())
                .isEqualTo("Atharga");

        assertThat(result.getVillage())
                .isEqualTo("Atharga");

        assertThat(result.getPincode())
                .isEqualTo("586112");

        assertThat(result.getDisplayName())
                .isEqualTo(
                        "Atharga, Vijayapura, Karnataka, India"
                );
    }

    @Test
    void shouldThrowGeocodingFailedWhenReverseRequestFails() {
        mockRestClient();

        when(responseSpec.body(GeoapifyResponse.class))
                .thenThrow(new RuntimeException("API failure"));

        assertThatThrownBy(() ->
                client.reverseGeocode(
                        16.9871578,
                        75.8854156
                )
        ).isInstanceOf(BusinessException.class);
    }

    @Test
    void shouldUseVillageWhenAvailable() {
        mockRestClient();

        GeoapifyFeature feature = new GeoapifyFeature();

        feature.setLat(16.9871578);
        feature.setLon(75.8854156);
        feature.setVillage("Atharga");
        feature.setSuburb("Some Suburb");

        GeoapifyResponse response = new GeoapifyResponse();
        response.setResults(List.of(feature));

        when(responseSpec.body(GeoapifyResponse.class))
                .thenReturn(response);

        GeocodingResult result =
                client.reverseGeocode(
                        16.9871578,
                        75.8854156
                );

        assertThat(result.getVillage())
                .isEqualTo("Atharga");
    }

    @Test
    void shouldUseSuburbWhenVillageIsBlank() {
        mockRestClient();

        GeoapifyFeature feature = new GeoapifyFeature();

        feature.setLat(16.9871578);
        feature.setLon(75.8854156);
        feature.setVillage(" ");
        feature.setSuburb("Atharga");

        GeoapifyResponse response = new GeoapifyResponse();
        response.setResults(List.of(feature));

        when(responseSpec.body(GeoapifyResponse.class))
                .thenReturn(response);

        GeocodingResult result =
                client.reverseGeocode(
                        16.9871578,
                        75.8854156
                );

        assertThat(result.getVillage())
                .isEqualTo("Atharga");
    }

    @Test
    void shouldTrimGeocodingValues() {
        mockRestClient();

        GeoapifyFeature feature = new GeoapifyFeature();

        feature.setLat(16.9871578);
        feature.setLon(75.8854156);
        feature.setCountry(" India ");
        feature.setCountryCode(" in ");
        feature.setState(" Karnataka ");
        feature.setDistrict(" Vijayapura ");
        feature.setCity(" Atharga ");
        feature.setPostcode(" 586112 ");
        feature.setFormatted(" Atharga, Karnataka, India ");

        GeoapifyResponse response = new GeoapifyResponse();
        response.setResults(List.of(feature));

        when(responseSpec.body(GeoapifyResponse.class))
                .thenReturn(response);

        GeocodingResult result =
                client.reverseGeocode(
                        16.9871578,
                        75.8854156
                );

        assertThat(result.getCountry())
                .isEqualTo("India");

        assertThat(result.getCountryCode())
                .isEqualTo("in");

        assertThat(result.getState())
                .isEqualTo("Karnataka");

        assertThat(result.getDistrict())
                .isEqualTo("Vijayapura");

        assertThat(result.getCity())
                .isEqualTo("Atharga");

        assertThat(result.getPincode())
                .isEqualTo("586112");

        assertThat(result.getDisplayName())
                .isEqualTo("Atharga, Karnataka, India");
    }

    private void mockRestClient() {
        when(restClientBuilder.baseUrl(any(String.class)))
                .thenReturn(restClientBuilder);

        when(restClientBuilder.build())
                .thenReturn(restClient);

        when(restClient.get())
                .thenReturn(requestHeadersUriSpec);

        when(requestHeadersUriSpec.uri(any(
                java.util.function.Function.class
        ))).thenReturn(requestHeadersUriSpec);

        when(requestHeadersUriSpec.retrieve())
                .thenReturn(responseSpec);
    }
}