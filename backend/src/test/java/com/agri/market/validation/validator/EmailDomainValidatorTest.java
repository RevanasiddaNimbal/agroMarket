package com.agri.market.validation.validator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("EmailDomainValidator")
class EmailDomainValidatorTest {

    private final EmailDomainValidator validator =
            new EmailDomainValidator(List.of("mailinator", "yopmail"));

    @Test
    void shouldAllowValidEmail() {
        assertThat(validator.isValid("user@gmail.com", null)).isTrue();
    }

    @Test
    void shouldRejectDisposableEmailDomain() {
        assertThat(validator.isValid("user@mailinator", null)).isFalse();
    }

    @Test
    void shouldBeCaseInsensitiveForDisposableDomains() {
        assertThat(validator.isValid("user@YOPMAIL", null)).isFalse();
    }

    @Test
    void shouldAllowNullAndBlankValues() {
        assertThat(validator.isValid(null, null)).isTrue();
        assertThat(validator.isValid("", null)).isTrue();
        assertThat(validator.isValid("   ", null)).isTrue();
    }

    @Test
    void shouldAllowMalformedEmailsToPassThroughThisValidator() {
        assertThat(validator.isValid("not-an-email", null)).isTrue();
        assertThat(validator.isValid("user@", null)).isTrue();
    }
}
