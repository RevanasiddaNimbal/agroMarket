package com.agri.market.location.service;

import com.agri.market.location.client.GeocodingResult;
import com.agri.market.location.entity.District;
import com.agri.market.location.entity.State;
import com.agri.market.location.entity.Taluk;
import com.agri.market.location.repository.DistrictRepository;
import com.agri.market.location.repository.StateRepository;
import com.agri.market.location.repository.TalukRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LocationResolverTest {

    @Mock
    private StateRepository stateRepository;

    @Mock
    private DistrictRepository districtRepository;

    @Mock
    private TalukRepository talukRepository;

    @InjectMocks
    private LocationResolver locationResolver;

    private State state;
    private District district;
    private Taluk taluk;

    @BeforeEach
    void setUp() {
        state = new State();
        state.setId("KA");
        state.setName("Karnataka");

        district = new District();
        district.setId("VIJAYAPURA");
        district.setName("Vijayapura");
        district.setState(state);

        taluk = new Taluk();
        taluk.setId("INDI");
        taluk.setName("Indi");
        taluk.setDistrict(district);
    }

    // ============================================================
    // NULL INPUT
    // ============================================================

    @Test
    void shouldReturnNullWhenInputIsNull() {

        GeocodingResult result = locationResolver.resolve(null);

        assertThat(result).isNull();

        verifyNoInteractions(
                stateRepository,
                districtRepository,
                talukRepository
        );
    }


    @Test
    void shouldResolveState() {

        GeocodingResult input = GeocodingResult.builder()
                .state("Karnataka")
                .country("India")
                .countryCode("IN")
                .build();

        when(stateRepository.findAllByActiveTrueOrderByNameAsc())
                .thenReturn(List.of(state));

        when(districtRepository
                .findAllByStateIdAndActiveTrueOrderByNameAsc("KA"))
                .thenReturn(List.of());

        GeocodingResult result = locationResolver.resolve(input);

        assertThat(result).isNotNull();
        assertThat(result.getState()).isEqualTo("Karnataka");
        assertThat(result.getDistrict()).isNull();
        assertThat(result.getTaluk()).isNull();
        assertThat(result.getVillage()).isNull();

        verify(stateRepository)
                .findAllByActiveTrueOrderByNameAsc();

        verify(districtRepository)
                .findAllByStateIdAndActiveTrueOrderByNameAsc("KA");

        verifyNoInteractions(talukRepository);
    }

    @Test
    void shouldReturnOriginalStateWhenStateIsNotFound() {

        GeocodingResult input = GeocodingResult.builder()
                .state("Unknown State")
                .build();

        when(stateRepository.findAllByActiveTrueOrderByNameAsc())
                .thenReturn(List.of(state));

        GeocodingResult result = locationResolver.resolve(input);

        assertThat(result.getState())
                .isEqualTo("Unknown State");

        assertThat(result.getDistrict()).isNull();
        assertThat(result.getTaluk()).isNull();

        verify(stateRepository)
                .findAllByActiveTrueOrderByNameAsc();

        verifyNoInteractions(
                districtRepository,
                talukRepository
        );
    }

    @Test
    void shouldHandleBlankState() {

        GeocodingResult input = GeocodingResult.builder()
                .state("   ")
                .build();

        GeocodingResult result = locationResolver.resolve(input);

        assertThat(result).isNotNull();
        assertThat(result.getState()).isBlank();

        verifyNoInteractions(
                stateRepository,
                districtRepository,
                talukRepository
        );
    }

    // ============================================================
    // STATE ALIASES
    // ============================================================

    @Test
    void shouldResolveBangaloreAlias() {

        State bengaluruState = new State();
        bengaluruState.setId("KA");
        bengaluruState.setName("Karnataka");

        GeocodingResult input = GeocodingResult.builder()
                .state("Karnataka")
                .city("Bangalore")
                .build();

        when(stateRepository.findAllByActiveTrueOrderByNameAsc())
                .thenReturn(List.of(bengaluruState));

        GeocodingResult result = locationResolver.resolve(input);

        assertThat(result.getState())
                .isEqualTo("Karnataka");
    }

    // ============================================================
    // DISTRICT
    // ============================================================

    @Test
    void shouldResolveDistrictUsingDistrictField() {

        GeocodingResult input = GeocodingResult.builder()
                .state("Karnataka")
                .district("Vijayapura")
                .build();

        when(stateRepository.findAllByActiveTrueOrderByNameAsc())
                .thenReturn(List.of(state));

        when(districtRepository
                .findAllByStateIdAndActiveTrueOrderByNameAsc("KA"))
                .thenReturn(List.of(district));

        when(talukRepository
                .findAllByDistrictIdAndActiveTrueOrderByNameAsc(district.getId()))
                .thenReturn(List.of());

        GeocodingResult result = locationResolver.resolve(input);

        assertThat(result).isNotNull();

        assertThat(result.getState())
                .isEqualTo("Karnataka");

        assertThat(result.getDistrict())
                .isEqualTo("Vijayapura");

        assertThat(result.getTaluk())
                .isNull();

        verify(stateRepository)
                .findAllByActiveTrueOrderByNameAsc();

        verify(districtRepository)
                .findAllByStateIdAndActiveTrueOrderByNameAsc("KA");

    }

    @Test
    void shouldResolveDistrictUsingStateDistrictField() {

        GeocodingResult input = GeocodingResult.builder()
                .state("Karnataka")
                .stateDistrict("Vijayapura District")
                .build();

        when(stateRepository.findAllByActiveTrueOrderByNameAsc())
                .thenReturn(List.of(state));

        when(districtRepository
                .findAllByStateIdAndActiveTrueOrderByNameAsc("KA"))
                .thenReturn(List.of(district));

        GeocodingResult result = locationResolver.resolve(input);

        assertThat(result.getDistrict())
                .isEqualTo("Vijayapura");
    }

    @Test
    void shouldResolveDistrictUsingCityWhenDistrictFieldsAreMissing() {

        GeocodingResult input = GeocodingResult.builder()
                .state("Karnataka")
                .city("Vijayapura")
                .build();

        when(stateRepository.findAllByActiveTrueOrderByNameAsc())
                .thenReturn(List.of(state));

        when(districtRepository
                .findAllByStateIdAndActiveTrueOrderByNameAsc("KA"))
                .thenReturn(List.of(district));

        GeocodingResult result = locationResolver.resolve(input);

        assertThat(result.getDistrict())
                .isEqualTo("Vijayapura");
    }

    @Test
    void shouldReturnNullDistrictWhenNoDistrictMatches() {

        GeocodingResult input = GeocodingResult.builder()
                .state("Karnataka")
                .district("Unknown District")
                .build();

        when(stateRepository.findAllByActiveTrueOrderByNameAsc())
                .thenReturn(List.of(state));

        when(districtRepository
                .findAllByStateIdAndActiveTrueOrderByNameAsc("KA"))
                .thenReturn(List.of(district));

        GeocodingResult result = locationResolver.resolve(input);

        assertThat(result.getDistrict()).isNull();
        assertThat(result.getTaluk()).isNull();

        verifyNoInteractions(talukRepository);
    }

    @Test
    void shouldHandleEmptyDistrictList() {

        GeocodingResult input = GeocodingResult.builder()
                .state("Karnataka")
                .district("Vijayapura")
                .build();

        when(stateRepository.findAllByActiveTrueOrderByNameAsc())
                .thenReturn(List.of(state));

        when(districtRepository
                .findAllByStateIdAndActiveTrueOrderByNameAsc("KA"))
                .thenReturn(List.of());

        GeocodingResult result = locationResolver.resolve(input);

        assertThat(result.getDistrict()).isNull();
        assertThat(result.getTaluk()).isNull();

        verifyNoInteractions(talukRepository);
    }

    // ============================================================
    // DISTRICT NORMALIZATION
    // ============================================================

    @Test
    void shouldIgnoreDistrictSuffixDistrict() {

        GeocodingResult input = GeocodingResult.builder()
                .state("Karnataka")
                .district("Vijayapura District")
                .build();

        when(stateRepository.findAllByActiveTrueOrderByNameAsc())
                .thenReturn(List.of(state));

        when(districtRepository
                .findAllByStateIdAndActiveTrueOrderByNameAsc("KA"))
                .thenReturn(List.of(district));

        GeocodingResult result = locationResolver.resolve(input);

        assertThat(result.getDistrict())
                .isEqualTo("Vijayapura");
    }

    @Test
    void shouldIgnoreCaseAndSpacesInDistrictName() {

        GeocodingResult input = GeocodingResult.builder()
                .state("Karnataka")
                .district(" VIJAYAPURA ")
                .build();

        when(stateRepository.findAllByActiveTrueOrderByNameAsc())
                .thenReturn(List.of(state));

        when(districtRepository
                .findAllByStateIdAndActiveTrueOrderByNameAsc("KA"))
                .thenReturn(List.of(district));

        GeocodingResult result = locationResolver.resolve(input);

        assertThat(result.getDistrict())
                .isEqualTo("Vijayapura");
    }

    // ============================================================
    // TALUK
    // ============================================================

    @Test
    void shouldResolveTalukUsingTalukField() {

        GeocodingResult input = GeocodingResult.builder()
                .state("Karnataka")
                .district("Vijayapura")
                .taluk("Indi")
                .build();

        when(stateRepository.findAllByActiveTrueOrderByNameAsc())
                .thenReturn(List.of(state));

        when(districtRepository
                .findAllByStateIdAndActiveTrueOrderByNameAsc("KA"))
                .thenReturn(List.of(district));

        when(talukRepository
                .findAllByDistrictIdAndActiveTrueOrderByNameAsc("VIJAYAPURA"))
                .thenReturn(List.of(taluk));

        GeocodingResult result = locationResolver.resolve(input);

        assertThat(result.getDistrict())
                .isEqualTo("Vijayapura");

        assertThat(result.getTaluk())
                .isEqualTo("Indi");
    }

    @Test
    void shouldResolveTalukUsingCountyField() {

        GeocodingResult input = GeocodingResult.builder()
                .state("Karnataka")
                .district("Vijayapura")
                .county("Indi Taluk")
                .build();

        when(stateRepository.findAllByActiveTrueOrderByNameAsc())
                .thenReturn(List.of(state));

        when(districtRepository
                .findAllByStateIdAndActiveTrueOrderByNameAsc("KA"))
                .thenReturn(List.of(district));

        when(talukRepository
                .findAllByDistrictIdAndActiveTrueOrderByNameAsc("VIJAYAPURA"))
                .thenReturn(List.of(taluk));

        GeocodingResult result = locationResolver.resolve(input);

        assertThat(result.getTaluk())
                .isEqualTo("Indi");
    }

    @Test
    void shouldReturnNullTalukWhenTalukDoesNotMatch() {

        GeocodingResult input = GeocodingResult.builder()
                .state("Karnataka")
                .district("Vijayapura")
                .taluk("Unknown Taluk")
                .build();

        when(stateRepository.findAllByActiveTrueOrderByNameAsc())
                .thenReturn(List.of(state));

        when(districtRepository
                .findAllByStateIdAndActiveTrueOrderByNameAsc("KA"))
                .thenReturn(List.of(district));

        when(talukRepository
                .findAllByDistrictIdAndActiveTrueOrderByNameAsc("VIJAYAPURA"))
                .thenReturn(List.of(taluk));

        GeocodingResult result = locationResolver.resolve(input);

        assertThat(result.getTaluk()).isNull();
    }

    @Test
    void shouldReturnNullTalukWhenTalukListIsEmpty() {

        GeocodingResult input = GeocodingResult.builder()
                .state("Karnataka")
                .district("Vijayapura")
                .taluk("Indi")
                .build();

        when(stateRepository.findAllByActiveTrueOrderByNameAsc())
                .thenReturn(List.of(state));

        when(districtRepository
                .findAllByStateIdAndActiveTrueOrderByNameAsc("KA"))
                .thenReturn(List.of(district));

        when(talukRepository
                .findAllByDistrictIdAndActiveTrueOrderByNameAsc("VIJAYAPURA"))
                .thenReturn(List.of());

        GeocodingResult result = locationResolver.resolve(input);

        assertThat(result.getTaluk()).isNull();
    }

    // ============================================================
    // VILLAGE
    // ============================================================

    @Test
    void shouldResolveVillage() {

        GeocodingResult input = GeocodingResult.builder()
                .state("Karnataka")
                .district("Vijayapura")
                .taluk("Indi")
                .village("Atharga")
                .build();

        when(stateRepository.findAllByActiveTrueOrderByNameAsc())
                .thenReturn(List.of(state));

        when(districtRepository
                .findAllByStateIdAndActiveTrueOrderByNameAsc("KA"))
                .thenReturn(List.of(district));

        when(talukRepository
                .findAllByDistrictIdAndActiveTrueOrderByNameAsc("VIJAYAPURA"))
                .thenReturn(List.of(taluk));

        GeocodingResult result = locationResolver.resolve(input);

        assertThat(result.getVillage())
                .isEqualTo("Atharga");
    }

    @Test
    void shouldReturnNullVillageWhenVillageIsBlank() {

        GeocodingResult input = GeocodingResult.builder()
                .state("Karnataka")
                .district("Vijayapura")
                .village("   ")
                .build();

        when(stateRepository.findAllByActiveTrueOrderByNameAsc())
                .thenReturn(List.of(state));

        when(districtRepository
                .findAllByStateIdAndActiveTrueOrderByNameAsc("KA"))
                .thenReturn(List.of(district));

        when(talukRepository
                .findAllByDistrictIdAndActiveTrueOrderByNameAsc(district.getId()))
                .thenReturn(List.of());

        GeocodingResult result = locationResolver.resolve(input);

        assertThat(result).isNotNull();

        assertThat(result.getVillage())
                .isNull();

        assertThat(result.getState())
                .isEqualTo("Karnataka");

        assertThat(result.getDistrict())
                .isEqualTo("Vijayapura");

        assertThat(result.getTaluk())
                .isNull();

        verify(stateRepository)
                .findAllByActiveTrueOrderByNameAsc();

        verify(districtRepository)
                .findAllByStateIdAndActiveTrueOrderByNameAsc("KA");

       
    }

    @Test
    void shouldRemoveVillageWhenSameAsDistrict() {

        GeocodingResult input = GeocodingResult.builder()
                .state("Karnataka")
                .district("Vijayapura")
                .village("Vijayapura")
                .build();

        when(stateRepository.findAllByActiveTrueOrderByNameAsc())
                .thenReturn(List.of(state));

        when(districtRepository
                .findAllByStateIdAndActiveTrueOrderByNameAsc("KA"))
                .thenReturn(List.of(district));

        GeocodingResult result = locationResolver.resolve(input);

        assertThat(result.getVillage()).isNull();
    }

    @Test
    void shouldRemoveVillageWhenSameAsTaluk() {

        GeocodingResult input = GeocodingResult.builder()
                .state("Karnataka")
                .district("Vijayapura")
                .taluk("Indi")
                .village("Indi")
                .build();

        when(stateRepository.findAllByActiveTrueOrderByNameAsc())
                .thenReturn(List.of(state));

        when(districtRepository
                .findAllByStateIdAndActiveTrueOrderByNameAsc("KA"))
                .thenReturn(List.of(district));

        when(talukRepository
                .findAllByDistrictIdAndActiveTrueOrderByNameAsc("VIJAYAPURA"))
                .thenReturn(List.of(taluk));

        GeocodingResult result = locationResolver.resolve(input);

        assertThat(result.getVillage()).isNull();
    }

    // ============================================================
    // CITY
    // ============================================================

    @Test
    void shouldResolveCity() {

        GeocodingResult input = GeocodingResult.builder()
                .state("Karnataka")
                .city("Atharga")
                .build();

        when(stateRepository.findAllByActiveTrueOrderByNameAsc())
                .thenReturn(List.of(state));

        GeocodingResult result = locationResolver.resolve(input);

        assertThat(result.getCity())
                .isEqualTo("Atharga");
    }

    @Test
    void shouldReturnNullCityWhenCityIsBlank() {

        GeocodingResult input = GeocodingResult.builder()
                .state("Karnataka")
                .city("   ")
                .build();

        when(stateRepository.findAllByActiveTrueOrderByNameAsc())
                .thenReturn(List.of(state));

        GeocodingResult result = locationResolver.resolve(input);

        assertThat(result.getCity()).isNull();
    }

    @Test
    void shouldUseVillageWhenCityAndVillageAreSame() {

        GeocodingResult input = GeocodingResult.builder()
                .state("Karnataka")
                .village("Atharga")
                .city("Atharga")
                .build();

        when(stateRepository.findAllByActiveTrueOrderByNameAsc())
                .thenReturn(List.of(state));

        GeocodingResult result = locationResolver.resolve(input);

        assertThat(result.getCity())
                .isEqualTo("Atharga");

        assertThat(result.getVillage())
                .isEqualTo("Atharga");
    }

    // ============================================================
    // COMPLETE RESOLUTION
    // ============================================================

    @Test
    void shouldResolveCompleteLocation() {

        GeocodingResult input = GeocodingResult.builder()
                .latitude(16.9871578)
                .longitude(75.8854156)
                .country("India")
                .countryCode("in")
                .state("Karnataka")
                .district("Vijayapura")
                .taluk("Indi")
                .village("Atharga")
                .city("Atharga")
                .pincode("586101")
                .displayName("Atharga, Indi, Karnataka, India")
                .county("Indi Taluk")
                .stateDistrict("Vijayapura District")
                .build();

        when(stateRepository.findAllByActiveTrueOrderByNameAsc())
                .thenReturn(List.of(state));

        when(districtRepository
                .findAllByStateIdAndActiveTrueOrderByNameAsc("KA"))
                .thenReturn(List.of(district));

        when(talukRepository
                .findAllByDistrictIdAndActiveTrueOrderByNameAsc("VIJAYAPURA"))
                .thenReturn(List.of(taluk));

        GeocodingResult result = locationResolver.resolve(input);

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

        assertThat(result.getTaluk())
                .isEqualTo("Indi");

        assertThat(result.getVillage())
                .isEqualTo("Atharga");

        assertThat(result.getCity())
                .isEqualTo("Atharga");

        assertThat(result.getPincode())
                .isEqualTo("586101");

        assertThat(result.getDisplayName())
                .isEqualTo("Atharga, Indi, Karnataka, India");

        assertThat(result.getCounty())
                .isEqualTo("Indi Taluk");

        assertThat(result.getStateDistrict())
                .isEqualTo("Vijayapura District");
    }

    // ============================================================
    // ORIGINAL DATA PRESERVATION
    // ============================================================

    @Test
    void shouldPreserveCoordinatesAndOtherFields() {

        GeocodingResult input = GeocodingResult.builder()
                .latitude(16.998412)
                .longitude(75.8547213)
                .country("India")
                .countryCode("in")
                .state("Karnataka")
                .city("Atharga")
                .pincode("586216")
                .displayName("Atharga")
                .build();

        when(stateRepository.findAllByActiveTrueOrderByNameAsc())
                .thenReturn(List.of(state));

        GeocodingResult result = locationResolver.resolve(input);

        assertThat(result.getLatitude())
                .isEqualTo(input.getLatitude());

        assertThat(result.getLongitude())
                .isEqualTo(input.getLongitude());

        assertThat(result.getCountry())
                .isEqualTo("India");

        assertThat(result.getCountryCode())
                .isEqualTo("in");

        assertThat(result.getPincode())
                .isEqualTo("586216");

        assertThat(result.getDisplayName())
                .isEqualTo("Atharga");
    }
}