package com.elegidocodes.android.util.format;

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

}
