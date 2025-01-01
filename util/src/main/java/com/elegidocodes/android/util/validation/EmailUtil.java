package com.elegidocodes.android.util.validation;

import android.util.Patterns;

import androidx.annotation.NonNull;

/**
 * Utility class for validating email addresses.
 */
public class EmailUtil {

    /**
     * Validates the given email address and returns a descriptive message.
     * <p>
     * If the email is invalid, one of the provided error messages is returned.
     * Otherwise, the provided "valid" message is returned.
     *
     * @param email                the email address to validate
     * @param emptyEmailMessage    message returned if the email is null or empty
     * @param missingAtMessage     message returned if the email lacks '@'
     * @param invalidFormatMessage message returned if the email format is invalid
     * @param tooLongMessage       message returned if the email length exceeds the maximum
     * @param validMessage         message returned if the email is valid
     * @param maxLength            the maximum allowed email length
     * @return a string describing the validation result (either an error message or the valid message)
     */
    public static @NonNull String getEmailValidationMessage(
            final String email,
            @NonNull final String emptyEmailMessage,
            @NonNull final String missingAtMessage,
            @NonNull final String invalidFormatMessage,
            @NonNull final String tooLongMessage,
            @NonNull final String validMessage,
            final int maxLength
    ) {
        // Trim leading/trailing spaces to avoid false negatives or positives
        if (email == null || email.trim().isEmpty()) {
            return emptyEmailMessage;
        }

        // Check for '@' character
        if (!email.contains("@")) {
            return missingAtMessage;
        }

        // Check email format using Android's Patterns class
        if (!Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) {
            return invalidFormatMessage;
        }

        // Check maximum allowed length
        if (email.length() > maxLength) {
            return tooLongMessage;
        }

        // If all checks pass
        return validMessage;
    }

    /**
     * Checks if the given email address is valid.
     * <p>
     * Returns {@code true} if the email passes all checks, otherwise {@code false}.
     *
     * @param email the email address to check
     * @return {@code true} if the email is valid; {@code false} otherwise
     */
    public static boolean isEmailValid(final String email) {
        // We allow null or empty to be treated as invalid directly
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        if (!email.contains("@")) {
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) {
            return false;
        }
        return email.length() <= 120;
    }

}

