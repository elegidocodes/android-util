package com.elegidocodes.android.util.format;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {

    /**
     * Capitalizes the first letter of the input string.
     * If the input string is null or empty, it returns the original string.
     *
     * @param input the input string
     * @return the string with the first letter capitalized, or the original string if it's null or empty
     */
    public static String capitalizeFirstLetter(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }

    /**
     * Extracts a specific group from a file path based on a provided regex pattern and group number.
     *
     * @param filePath      The file path from which to extract the group content.
     * @param patternString The regex pattern to apply. The pattern should include the groups to extract the desired content.
     * @param groupNumber   The group number in the regex to extract (starting at 1).
     * @return The extracted group content as a string if found; otherwise, returns null.
     * @throws IllegalArgumentException If the group number is invalid or if the pattern does not match.
     */
    public static String extractGroupFromFilePath(String filePath, String patternString, int groupNumber) {
        if (groupNumber < 1) {
            throw new IllegalArgumentException("Group number must be greater than or equal to 1.");
        }

        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(filePath);

        if (matcher.find()) {
            if (groupNumber > matcher.groupCount()) {
                throw new IllegalArgumentException("Group number exceeds the number of capturing groups in the pattern.");
            }
            return matcher.group(groupNumber);
        } else {
            return null;
        }
    }

}
