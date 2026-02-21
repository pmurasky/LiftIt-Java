package com.liftit.user;

/**
 * Value object representing a normalised email address.
 *
 * <p>Email addresses are normalised to lowercase on creation to ensure
 * consistent comparison and storage. Basic format validation is applied
 * (presence of {@code @}, non-empty local part, non-empty domain).
 */
public record Email(String value) {

    /**
     * Canonical constructor — validates and stores the normalised email.
     *
     * @param value the email address string; must be non-null, non-blank, and valid format
     * @throws IllegalArgumentException if {@code value} is null, blank, or not a valid email format
     */
    public Email {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Email must not be null or blank");
        }
        validateFormat(value);
    }

    /**
     * Creates a normalised {@code Email} from the given address string.
     *
     * @param address the email address; must not be null or blank
     * @return a new {@code Email} with the address normalised to lowercase
     * @throws IllegalArgumentException if {@code address} is null, blank, or not valid format
     */
    public static Email of(String address) {
        if (address == null) {
            throw new IllegalArgumentException("Email must not be null or blank");
        }
        return new Email(address.toLowerCase());
    }

    private static void validateFormat(String value) {
        int atIndex = value.indexOf('@');
        if (atIndex <= 0 || atIndex == value.length() - 1) {
            throw new IllegalArgumentException("Email must contain a valid format (local@domain): " + value);
        }
    }
}
