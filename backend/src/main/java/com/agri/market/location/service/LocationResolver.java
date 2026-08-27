package com.agri.market.location.service;

import com.agri.market.location.client.GeocodingResult;
import com.agri.market.location.entity.District;
import com.agri.market.location.entity.State;
import com.agri.market.location.entity.Taluk;
import com.agri.market.location.repository.DistrictRepository;
import com.agri.market.location.repository.StateRepository;
import com.agri.market.location.repository.TalukRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class LocationResolver {

    private static final Map<String, String> ADMIN_NAME_ALIASES = Map.ofEntries(
            Map.entry("bangalore", "bengaluru"),
            Map.entry("mysore", "mysuru"),
            Map.entry("mangalore", "mangaluru"),
            Map.entry("belgaum", "belagavi"),
            Map.entry("hubli", "hubballi"),
            Map.entry("gulbarga", "kalaburagi"),
            Map.entry("bijapur", "vijayapura"),
            Map.entry("bellary", "ballari"),
            Map.entry("shimoga", "shivamogga"),
            Map.entry("chikmagalur", "chikkamagaluru"),
            Map.entry("tumkur", "tumakuru")
    );

    private final StateRepository stateRepository;
    private final DistrictRepository districtRepository;
    private final TalukRepository talukRepository;

    @Transactional(readOnly = true)
    public GeocodingResult resolve(GeocodingResult result) {
        if (result == null) {
            return null;
        }

        State state = findState(result.getState());

        District district = null;
        if (state != null) {
            district = findDistrict(state, result);
        }

        Taluk taluk = null;
        if (district != null) {
            taluk = findTaluk(district, result);
        }

        String village = resolveVillage(result, district, taluk);
        String city = resolveCity(result, village);

        return GeocodingResult.builder()
                .latitude(result.getLatitude())
                .longitude(result.getLongitude())
                .country(result.getCountry())
                .countryCode(result.getCountryCode())
                .state(state != null ? state.getName() : result.getState())
                .district(district != null ? district.getName() : null)
                .taluk(taluk != null ? taluk.getName() : null)
                .village(village)
                .city(city)
                .pincode(result.getPincode())
                .displayName(result.getDisplayName())
                .county(result.getCounty())
                .stateDistrict(result.getStateDistrict())
                .build();
    }

    private State findState(String value) {
        if (isBlank(value)) {
            return null;
        }

        String normalized = normalizeAdministrativeName(value);

        return stateRepository
                .findAllByActiveTrueOrderByNameAsc()
                .stream()
                .filter(state -> normalizeAdministrativeName(state.getName()).equals(normalized))
                .findFirst()
                .orElse(null);
    }

    private District findDistrict(State state, GeocodingResult result) {
        if (state == null || result == null) {
            return null;
        }

        List<District> districts = districtRepository
                .findAllByStateIdAndActiveTrueOrderByNameAsc(state.getId());

        if (districts.isEmpty()) {
            return null;
        }

        List<String> candidates = new ArrayList<>();
        addCandidate(candidates, result.getStateDistrict());
        addCandidate(candidates, result.getDistrict());
        addCandidate(candidates, result.getCity());

        for (String candidate : candidates) {
            District district = findDistrictByName(districts, candidate);
            if (district != null) {
                return district;
            }
        }

        return null;
    }

    private District findDistrictByName(List<District> districts, String candidate) {
        if (isBlank(candidate)) {
            return null;
        }

        String normalizedCandidate = normalizeAdministrativeName(candidate);

        for (District district : districts) {
            String normalizedDistrict = normalizeAdministrativeName(district.getName());
            if (normalizedDistrict.equals(normalizedCandidate)) {
                return district;
            }
        }

        return null;
    }

    private Taluk findTaluk(District district, GeocodingResult result) {
        if (district == null || result == null) {
            return null;
        }

        List<Taluk> taluks = talukRepository
                .findAllByDistrictIdAndActiveTrueOrderByNameAsc(district.getId());

        if (taluks.isEmpty()) {
            return null;
        }

        List<String> candidates = buildTalukCandidates(result);

        for (String candidate : candidates) {
            if (isBlank(candidate)) {
                continue;
            }

            String normalizedCandidate = normalizeAdministrativeName(candidate);

            for (Taluk taluk : taluks) {
                String normalizedTaluk = normalizeAdministrativeName(taluk.getName());
                if (normalizedTaluk.equals(normalizedCandidate)) {
                    return taluk;
                }
            }
        }

        return null;
    }

    private List<String> buildTalukCandidates(GeocodingResult result) {
        List<String> candidates = new ArrayList<>();
        addCandidate(candidates, result.getTaluk());
        addCandidate(candidates, result.getCounty());
        return candidates;
    }

    private void addCandidate(List<String> candidates, String value) {
        if (!isBlank(value) && !candidates.contains(value)) {
            candidates.add(value);
        }
    }

    private String resolveVillage(GeocodingResult result, District district, Taluk taluk) {
        if (result == null) {
            return null;
        }

        String village = result.getVillage();
        if (isBlank(village)) {
            return null;
        }

        String normalizedVillage = normalizeAdministrativeName(village);

        if (district != null
                && normalizeAdministrativeName(district.getName()).equals(normalizedVillage)) {
            return null;
        }

        if (taluk != null
                && normalizeAdministrativeName(taluk.getName()).equals(normalizedVillage)) {
            return null;
        }

        return village.trim();
    }

    private String resolveCity(GeocodingResult result, String village) {
        if (result == null || isBlank(result.getCity())) {
            return null;
        }

        String city = result.getCity().trim();

        if (village != null
                && normalizeAdministrativeName(city).equals(normalizeAdministrativeName(village))) {
            return village;
        }

        return city;
    }

    private String normalizeAdministrativeName(String value) {
        if (isBlank(value)) {
            return "";
        }

        String normalized = value.trim().toLowerCase();

        normalized = applyAliases(normalized);

        normalized = normalized.replaceAll("[^a-z0-9 ]", "");

        normalized = removeSuffix(normalized, " subdistrict");
        normalized = removeSuffix(normalized, " taluka");
        normalized = removeSuffix(normalized, " taluku");
        normalized = removeSuffix(normalized, " taluk");
        normalized = removeSuffix(normalized, " tehsil");
        normalized = removeSuffix(normalized, " district");

        normalized = normalized.replaceAll("[^a-z0-9]", "");

        return normalized;
    }

    private String applyAliases(String value) {
        String result = value;
        for (Map.Entry<String, String> entry : ADMIN_NAME_ALIASES.entrySet()) {
            result = result.replace(entry.getKey(), entry.getValue());
        }
        return result;
    }

    private String removeSuffix(String value, String suffix) {
        if (value.endsWith(suffix) && value.length() > suffix.length()) {
            return value.substring(0, value.length() - suffix.length());
        }
        return value;
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}