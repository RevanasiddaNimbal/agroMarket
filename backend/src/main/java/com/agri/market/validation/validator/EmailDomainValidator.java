package com.agri.market.validation.validator;

import com.agri.market.validation.annotation.NonDisposableEmail;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Value;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

public class EmailDomainValidator
        implements ConstraintValidator<NonDisposableEmail, String> {

    private final Set<String> blockedDomains;

    public EmailDomainValidator(
            @Value("${app.security.disposable-email:}") final List<String> domains
    ) {
        this.blockedDomains = domains == null
                ? Collections.emptySet()
                : domains.stream()
                .filter(domain -> domain != null && !domain.isBlank())
                .map(String::trim)
                .map(domain -> domain.toLowerCase(Locale.ROOT))
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public boolean isValid(
            final String email,
            final ConstraintValidatorContext context
    ) {
        if (email == null || email.isBlank()) {
            return true;
        }

        final int atIndex = email.lastIndexOf('@');

        if (atIndex <= 0 || atIndex == email.length() - 1) {
            return true;
        }

        final String domain = email
                .substring(atIndex + 1)
                .toLowerCase(Locale.ROOT);

        return !blockedDomains.contains(domain);
    }
}