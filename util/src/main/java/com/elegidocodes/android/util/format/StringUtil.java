package com.elegidocodes.android.util.format;

import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
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

    /**
     * Checks if a given string is empty or consists only of whitespace characters.
     *
     * <p>This method performs the following checks:
     * <ul>
     *     <li>If the string is null, it returns {@code false}.</li>
     *     <li>If the string contains only whitespace characters, it returns {@code true}.</li>
     *     <li>If the string has any non-whitespace characters, it returns {@code false}.</li>
     * </ul>
     *
     * @param string The string to be checked.
     * @return {@code true} if the string is not null and consists only of whitespace characters;
     * otherwise, {@code false}.
     *
     * <p>Example usage:
     * <pre>{@code
     * boolean result1 = empty(null);           // Output: false
     * boolean result2 = empty("");             // Output: true
     * boolean result3 = empty("   ");          // Output: true
     * boolean result4 = empty("non-empty");    // Output: false
     * }</pre>
     */
    public static boolean empty(String string) {
        return string != null && string.trim().isEmpty();
    }

    /**
     * Retrieves the trimmed text from a TextView (including EditText and TextInputEditText).
     *
     * <p>This method checks if the text is non-null and not empty after trimming whitespace.
     * If valid text exists, it returns the trimmed text; otherwise, it returns an empty string.
     *
     * @param textView The TextView (or its subclasses, like EditText or TextInputEditText) from which to retrieve the text.
     * @return A trimmed string containing the text from the TextView, or an empty string if the text is null or empty.
     *
     * <p>Example usage:
     * <pre>{@code
     * EditText editText = findViewById(R.id.edit_text);
     * TextInputEditText inputEditText = findViewById(R.id.text_input_edit_text);
     *
     * String text1 = getTrimmedText(editText);
     * String text2 = getTrimmedText(inputEditText);
     * }</pre>
     */
    public static String getTrimmedText(TextView textView) {
        if (textView != null && textView.getText() != null) {
            String text = textView.getText().toString().trim();
            return text.isEmpty() ? "" : text;
        }
        return "";
    }

    /**
     * Capitalizes the first letter of each word in the given string while converting the rest of the letters to lowercase.
     *
     * <p>Words are determined by whitespace characters (e.g., spaces, tabs). Each word's first letter is
     * converted to uppercase, and all subsequent letters are converted to lowercase.</p>
     *
     * <p>Example usage:
     * <pre>{@code
     * String input = "hello world! THIS is a Test.";
     * String result = capitalizeWords(input);
     * System.out.println(result); // Output: "Hello World! This Is A Test."
     * }</pre>
     *
     * @param input The input string to transform.
     * @return A string with the first letter of each word capitalized and the rest in lowercase.
     * If the input is null or empty, an empty string is returned.
     */
    public static String capitalizeWords(String input) {
        if (input == null || input.isEmpty()) {
            return "";
        }

        input = input.toLowerCase();

        StringBuilder sb = new StringBuilder();
        boolean capitalizeNext = true;

        for (char c : input.toCharArray()) {
            if (Character.isWhitespace(c)) {
                capitalizeNext = true;
                sb.append(c);
            } else if (capitalizeNext) {
                c = Character.toUpperCase(c);
                capitalizeNext = false;
                sb.append(c);
            } else {
                c = Character.toLowerCase(c);
                sb.append(c);
            }
        }

        return sb.toString();
    }

    /**
     * Splits a given string into a list of words based on uppercase letters.
     *
     * <p>This method identifies words in the input string by detecting uppercase letters. When an
     * uppercase letter is encountered (excluding the first character), it is treated as the beginning
     * of a new word. The resulting list contains each word as a separate string.</p>
     *
     * <p>Example usage:
     * <pre>{@code
     * String input = "CamelCaseStringExample";
     * List<String> words = getListOfWords(input);
     * System.out.println(words); // Output: ["Camel", "Case", "String", "Example"]
     * }</pre>
     *
     * @param input The input string to be split into words.
     * @return A list of words derived from the input string. If the input is null or empty,
     * an empty list is returned.
     */
    public static List<String> getListOfWords(String input) {
        if (input == null || input.isEmpty()) {
            return new ArrayList<>(); // Return an empty list for null or empty input
        }

        List<String> separatedWords = new ArrayList<>();
        StringBuilder currentWord = new StringBuilder();

        for (int i = 0; i < input.length(); i++) {
            char currentChar = input.charAt(i);

            if (Character.isUpperCase(currentChar) && i > 0) {
                separatedWords.add(currentWord.toString());
                currentWord = new StringBuilder();
            }

            currentWord.append(currentChar);
        }

        // Add the last word to the list
        if (currentWord.length() > 0) {
            separatedWords.add(currentWord.toString());
        }

        return separatedWords;
    }

    /**
     * Splits a camel-case string into individual words and joins them with spaces.
     *
     * <p>This method uses {@link #getListOfWords(String)} to split the input string into words based on
     * uppercase letters, and then combines the words into a single string separated by spaces.</p>
     *
     * <p>Example usage:
     * <pre>{@code
     * String input = "CamelCaseStringExample";
     * String result = uniteWordsWithSpace(input);
     * System.out.println(result); // Output: "Camel Case String Example"
     * }</pre>
     *
     * @param input The camel-case input string to be processed.
     * @return A string where the words are separated by spaces. If the input is null or empty, an empty string is returned.
     */
    public static String uniteWordsWithSpace(String input) {
        if (input == null || input.isEmpty()) {
            return ""; // Return an empty string for null or empty input
        }

        List<String> result = getListOfWords(input);

        // Use String.join for cleaner concatenation
        return String.join(" ", result);
    }

}
