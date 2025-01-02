package com.elegidocodes.android.util.date;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class DateUtil {

    /**
     * Utility method to retrieve the current date and time formatted according to the specified format.
     *
     * @param outputFormat The desired format for the output date string.
     * @return A formatted date string representing the current date and time.
     */
    public static String getCurrentDate(String outputFormat) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(outputFormat, Locale.getDefault());

        // Return the formatted date string representing the current date and time.
        return simpleDateFormat.format(new Date());
    }

    // ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~

    /**
     * Utility method to calculate the time difference between a given {@link Date} object and
     * the current date/time in a specified {@link TimeUnit}.
     *
     * <p>Examples of usage:</p>
     * <pre>{@code
     *   // If you want the difference in hours:
     *   long hoursDiff = getTimeDifference(myDate, TimeUnit.HOURS);
     *
     *   // If you want the difference in minutes:
     *   long minutesDiff = getTimeDifference(myDate, TimeUnit.MINUTES);
     * }</pre>
     *
     * @param date The {@link Date} object from which to calculate the time difference.
     * @param unit The {@link TimeUnit} (e.g. HOURS, MINUTES, SECONDS) in which to return the difference.
     * @return The absolute difference between the provided {@link Date} and the current date/time,
     * expressed in the specified {@link TimeUnit}.
     */
    public static long getTimeDifference(Date date, TimeUnit unit) {
        long diffInMillis = Math.abs(new Date().getTime() - date.getTime());
        return unit.convert(diffInMillis, TimeUnit.MILLISECONDS);
    }

    /**
     * Utility method to calculate the time difference between a given date string (parsed with the
     * specified format) and the current date/time, returning the result in the specified {@link TimeUnit}.
     *
     * <pre>{@code
     * // Usage examples:
     * long hoursDiff = getTimeDifference("2025-01-02 15:30:00", "yyyy-MM-dd HH:mm:ss", TimeUnit.HOURS);
     * long minutesDiff = getTimeDifference("2025-01-02 15:30:00", "yyyy-MM-dd HH:mm:ss", TimeUnit.MINUTES);
     * }</pre>
     *
     * @param dateString The date/time string to parse (e.g. "2025-01-02 15:30:00").
     * @param format     The {@link SimpleDateFormat} pattern to use when parsing (e.g. "yyyy-MM-dd HH:mm:ss").
     * @param unit       The {@link TimeUnit} (e.g. HOURS, MINUTES) in which to return the difference.
     * @return The absolute difference between the parsed date/time and the current date/time,
     * expressed in the specified {@link TimeUnit}.
     * @throws ParseException If the {@code dateString} cannot be parsed using the provided {@code format}.
     */
    public static long getTimeDifference(String dateString, String format, TimeUnit unit) throws ParseException {
        // 1. Parse the date string into a Date object using the specified pattern.
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
        Date parsedDate = sdf.parse(dateString);

        return parsedDate != null ? getTimeDifference(parsedDate, unit) : 0;
    }

    // ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~

    /**
     * Utility method to format the time difference between a given {@link Date} object and
     * the current date/time. Depending on the total elapsed time (in seconds), the output format changes:
     * <ul>
     *     <li><b>Less than 60 seconds</b>: <code>XXs</code></li>
     *     <li><b>Less than 60 minutes</b>: <code>MM:SS</code></li>
     *     <li><b>Less than 24 hours</b>: <code>HH:MM:SS</code></li>
     *     <li><b>24 hours or more</b>: <code>X days, HH:MM:SS</code></li>
     * </ul>
     *
     * @param date The {@link Date} from which the time difference is calculated.
     * @return A formatted string representing the time difference.
     */
    public static String getFormattedTime(Date date) {
        // 1. Calculate the absolute difference in milliseconds
        long timeInMillis = Math.abs(new Date().getTime() - date.getTime());

        // 2. Convert to total seconds for simpler calculations
        long totalSeconds = timeInMillis / 1000;

        // 3. Check different thresholds and format accordingly

        // Case 1: Less than 60 seconds -> "XXs"
        if (totalSeconds < 60) {
            return String.format(Locale.getDefault(), "%ds", totalSeconds);
        }

        // Case 2: Less than 60 minutes -> "MM:SS"
        if (totalSeconds < 3600) {
            long minutes = totalSeconds / 60;
            long seconds = totalSeconds % 60;
            return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        }

        // Case 3: Less than 24 hours -> "HH:MM:SS"
        if (totalSeconds < 86400) {
            long hours = totalSeconds / 3600;
            long remainder = totalSeconds % 3600;
            long minutes = remainder / 60;
            long seconds = remainder % 60;
            return String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);
        }

        // Case 4: 24 hours or more -> "X days, HH:MM:SS"
        long days = totalSeconds / 86400; // Number of full days
        long remainder = totalSeconds % 86400;
        long hours = remainder / 3600;
        remainder = remainder % 3600;
        long minutes = remainder / 60;
        long seconds = remainder % 60;

        return String.format(Locale.getDefault(), "%d day%s, %02d:%02d:%02d",
                days,
                (days == 1 ? "" : "s"), // Pluralize "day" if needed
                hours,
                minutes,
                seconds
        );
    }

    /**
     * Parses the given date string using the specified format pattern and then returns a formatted
     * time difference string (via {@link #getFormattedTime(Date)}).
     *
     * <p>If parsing fails (i.e., if {@code dateString} is not in the specified {@code format}),
     * a {@link ParseException} is thrown. If parsing returns {@code null}, an empty string is returned.</p>
     *
     * <p>Example usage:</p>
     * <pre>{@code
     *   try {
     *       String result = getFormattedTime("2025-01-02 15:30:00", "yyyy-MM-dd HH:mm:ss");
     *       System.out.println(result); // e.g. "1 day, 02:05:37" if that's the time difference
     *   } catch (ParseException e) {
     *       // Handle the exception (invalid format, etc.)
     *   }
     * }</pre>
     *
     * @param dateString A string representing a date/time, e.g. "2025-01-02 15:30:00".
     * @param format     The {@link SimpleDateFormat} pattern for parsing {@code dateString},
     *                   e.g. "yyyy-MM-dd HH:mm:ss".
     * @return A formatted string representing the time difference based on
     * {@link #getFormattedTime(Date)} logic. If parsing returns a null date, returns an empty string.
     * @throws ParseException if the text cannot be parsed using the given {@code format}.
     * @see #getFormattedTime(Date)
     */
    public static String getFormattedTime(String dateString, String format) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
        Date parsedDate = sdf.parse(dateString);
        return parsedDate != null ? getFormattedTime(parsedDate) : "";
    }

    // ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~

    /**
     * Returns the absolute time difference between two {@link Date} objects,
     * converted to a specified {@link TimeUnit}.
     *
     * <p>Example usage:</p>
     * <pre>{@code
     *   Date fromDate = ...; // e.g., some previous date/time
     *   Date toDate   = ...; // e.g., current date/time
     *
     *   long hoursDiff = getTimeBetweenDates(fromDate, toDate, TimeUnit.HOURS);
     *   long secondsDiff = getTimeBetweenDates(fromDate, toDate, TimeUnit.SECONDS);
     * }</pre>
     *
     * @param fromDate the first date/time
     * @param toDate   the second date/time
     * @param unit     the {@link TimeUnit} in which to express the result (HOURS, MINUTES, SECONDS, etc.)
     * @return the absolute difference between {@code fromDate} and {@code toDate} in the specified {@link TimeUnit}
     * @throws IllegalArgumentException if either {@code fromDate} or {@code toDate} is null
     */
    public static long getTimeBetweenDates(Date fromDate, Date toDate, TimeUnit unit) {
        if (fromDate == null || toDate == null) {
            throw new IllegalArgumentException("Date parameters cannot be null");
        }

        long diffInMillis = Math.abs(fromDate.getTime() - toDate.getTime());
        return unit.convert(diffInMillis, TimeUnit.MILLISECONDS);
    }

    /**
     * Parses two date/time strings using two specified format patterns and calculates the
     * absolute time difference between them, expressed in a given {@link TimeUnit}.
     * <p>
     * The method first attempts to parse {@code stringFromDate} using {@code formatFromDate},
     * and {@code stringToDate} using {@code formatToDate}. If both are parsed successfully,
     * it delegates to {@link #getTimeBetweenDates(Date, Date, TimeUnit)} to compute and return
     * the absolute difference.
     * </p>
     *
     * <h3>Examples</h3>
     * <pre>{@code
     *  try {
     *      String from = "2025-01-02 08:30";
     *      String to   = "2025/01/02 10:45:00";
     *
     *      long diffInMinutes = getTimeBetweenDates(
     *              from,
     *              to,
     *              "yyyy-MM-dd HH:mm",
     *              "yyyy/MM/dd HH:mm:ss",
     *              TimeUnit.MINUTES
     *      );
     *      System.out.println("Difference in minutes: " + diffInMinutes);
     *  } catch (ParseException e) {
     *      e.printStackTrace();
     *  }
     * }</pre>
     *
     * @param stringFromDate A string representing the starting date/time (e.g. "2025-01-02 08:30").
     * @param stringToDate   A string representing the ending date/time   (e.g. "2025/01/02 10:45:00").
     * @param formatFromDate The pattern to parse {@code stringFromDate}  (e.g. "yyyy-MM-dd HH:mm").
     * @param formatToDate   The pattern to parse {@code stringToDate}    (e.g. "yyyy/MM/dd HH:mm:ss").
     * @param unit           The {@link TimeUnit} (HOURS, MINUTES, SECONDS, etc.) for the result.
     * @return The absolute time difference between the two dates, expressed in the specified
     * {@code TimeUnit}, or {@code 0} if either parsed date is {@code null}.
     * @throws ParseException if either {@code stringFromDate} or {@code stringToDate} cannot be
     *                        parsed using the provided formats.
     * @see #getTimeBetweenDates(Date, Date, TimeUnit)
     */
    public static long getTimeBetweenDates(String stringFromDate,
                                           String stringToDate,
                                           String formatFromDate,
                                           String formatToDate,
                                           TimeUnit unit) throws ParseException {

        // Create SimpleDateFormats with the specified locale and patterns
        SimpleDateFormat sdf1 = new SimpleDateFormat(formatFromDate, Locale.getDefault());
        Date parsedDate1 = sdf1.parse(stringFromDate);

        SimpleDateFormat sdf2 = new SimpleDateFormat(formatToDate, Locale.getDefault());
        Date parsedDate2 = sdf2.parse(stringToDate);

        // If both parse successfully, calculate the time difference; otherwise, return 0
        return (parsedDate1 != null && parsedDate2 != null)
                ? getTimeBetweenDates(parsedDate1, parsedDate2, unit)
                : 0;
    }

    // ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~

    /**
     * Formats the provided {@link Date} object into a string using the specified format pattern.
     *
     * <p>The {@code outputFormat} parameter should be a valid
     * {@link java.text.SimpleDateFormat} pattern (e.g. <em>yyyy-MM-dd HH:mm:ss</em>).</p>
     *
     * <p><strong>Note:</strong> {@link SimpleDateFormat} is not thread-safe.
     * In multi-threaded environments, each thread should use its own {@link SimpleDateFormat} instance,
     * or you should use synchronization to ensure safety.</p>
     *
     * @param date         the {@link Date} to be formatted. Must not be {@code null}.
     * @param outputFormat the desired output format pattern (e.g., <em>"yyyy-MM-dd"</em>).
     * @return a string representation of the date in the specified format,
     * or an empty string if {@code date} is {@code null}.
     */
    public static String formatDateAsString(Date date, String outputFormat) {
        if (date == null) {
            return "";
        }
        SimpleDateFormat outputSdf = new SimpleDateFormat(outputFormat, Locale.getDefault());
        return outputSdf.format(date);
    }

    /**
     * Parses the provided {@code dateString} using {@code inputFormat} and then formats it
     * into a string with the specified {@code outputFormat}.
     * <p>
     * Example usage:
     * <pre>{@code
     *   String original = "2025-01-02 15:30:00";
     *   String reformatted = formatDateAsString(original, "yyyy-MM-dd HH:mm:ss", "MMM d, yyyy hh:mm a");
     *   // reformatted might be "Jan 2, 2025 03:30 PM"
     * }</pre>
     *
     * @param dateString   the date string to parse (e.g. "2025-01-02 15:30:00").
     * @param inputFormat  the expected format of the input date string (e.g. "yyyy-MM-dd HH:mm:ss").
     * @param outputFormat the format to which the parsed {@link Date} should be converted
     *                     (e.g. "MMM d, yyyy hh:mm a").
     * @return a string representation of the parsed date in the desired output format;
     * returns an empty string if the parsed date is {@code null}.
     * @throws ParseException if {@code dateString} cannot be parsed using the given {@code inputFormat}.
     * @see #formatDateAsString(Date, String) formatDateAsString(Date, String) for formatting an already-parsed {@link Date}.
     */
    public static String formatDateAsString(String dateString,
                                            String inputFormat,
                                            String outputFormat) throws ParseException {

        // 1. Parse the input string into a Date object
        SimpleDateFormat inputSdf = new SimpleDateFormat(inputFormat, Locale.getDefault());
        Date parsedDate = inputSdf.parse(dateString);

        // 2. Format the parsed date using the existing formatDateAsString(Date, String) method
        return parsedDate != null ? formatDateAsString(parsedDate, outputFormat) : "";
    }

    // ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~

    /**
     * Formats and localizes a {@link Date} object using the specified language and region.
     *
     * <p>This method constructs a {@link Locale} from the given {@code language} and {@code region}
     * codes, retrieves a {@link DateFormat} instance in {@link DateFormat#FULL} style for that locale,
     * and formats the provided {@code date}. If {@code date} is {@code null}, the method uses
     * the current system date/time.</p>
     *
     * <p><strong>Example Usage:</strong></p>
     * <pre>{@code
     * // Suppose 'date' is January 1, 2025:
     *
     * String resultEnUs = changeDateFormat(date, "en", "US");
     * // Returns something like: "Wednesday, January 1, 2025"
     *
     * String resultEsMx = changeDateFormat(date, "es", "MX");
     * // Returns something like: "miércoles, 1 de enero de 2025"
     * }</pre>
     *
     * @param date     the {@link Date} to format (may be null)
     * @param language the ISO 639 language code (e.g. "en", "es", "fr")
     * @param region   the ISO 3166 country/region code (e.g. "US", "MX", "FR")
     * @return a localized date string in the {@code DateFormat.FULL} style for the specified locale,
     * or the current date/time if {@code date} is null
     */
    public static String changeDateFormat(Date date, String language, String region) {
        // Build a Locale using the provided language and region
        Locale locale = new Locale.Builder()
                .setLanguage(language)
                .setRegion(region)
                .build();

        // Use the FULL style to get a very verbose, localized date format
        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.FULL, locale);

        // If 'date' is null, format the current date/time instead
        return date != null ? dateFormat.format(date) : dateFormat.format(new Date());
    }

    /**
     * Parses a date string using the specified {@code dateFormat} pattern, then delegates to
     * {@link #changeDateFormat(Date, String, String)} to format the parsed date for a given
     * {@code language} and {@code region}.
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * try {
     *     String dateStr    = "2025-01-02 15:30:00";
     *     String inputFmt   = "yyyy-MM-dd HH:mm:ss";
     *     String language   = "es"; // Spanish
     *     String region     = "MX"; // Mexico
     *
     *     // e.g. "jueves, 2 de enero de 2025"
     *     String localizedDate = changeDateFormat(dateStr, inputFmt, language, region);
     *     System.out.println(localizedDate);
     * } catch (ParseException e) {
     *     // Handle the exception: dateStr didn't match inputFmt
     * }
     * }</pre>
     *
     * @param dateString the date/time string to parse (e.g., "2025-01-02 15:30:00").
     * @param dateFormat the pattern used by {@link SimpleDateFormat} to parse {@code dateString}
     *                   (e.g., "yyyy-MM-dd HH:mm:ss").
     * @param language   the ISO 639 language code (e.g., "en", "es", "fr").
     * @param region     the ISO 3166 country/region code (e.g., "US", "MX", "FR").
     * @return a fully localized date string in the {@link java.text.DateFormat#FULL} style for the
     * specified locale, or an empty string if the parsing fails.
     * @throws ParseException if {@code dateString} cannot be parsed using the provided {@code dateFormat}.
     * @see #changeDateFormat(Date, String, String)
     */
    public static String changeDateFormat(String dateString,
                                          String dateFormat,
                                          String language,
                                          String region) throws ParseException {
        // 1. Parse the input date string
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.getDefault());
        Date parsedDate = sdf.parse(dateString);

        // 2. If parsing was successful, re-format using the overloaded method that takes a Date
        return parsedDate != null ? changeDateFormat(parsedDate, language, region) : "";
    }

    // ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~

    /**
     * Formats the specified {@link Date} object into a localized date string using the provided
     * {@code style} and {@link Locale}.
     *
     * <p>
     * This method calls {@link DateFormat#getDateInstance(int, Locale)}. If the {@code style} is
     * {@link DateFormat#FULL}, you'll typically see the most verbose date output (e.g.,
     * "Wednesday, January 1, 2025" in U.S. English). If the style is something other than
     * {@code FULL}, such as {@link DateFormat#LONG}, {@link DateFormat#MEDIUM}, or
     * {@link DateFormat#SHORT}, the resulting date string is progressively less verbose.
     * For example:
     * </p>
     * <ul>
     *   <li><b>FULL:</b> &quot;Wednesday, January 1, 2025&quot;</li>
     *   <li><b>LONG:</b> &quot;January 1, 2025&quot;</li>
     *   <li><b>MEDIUM:</b> &quot;Jan 1, 2025&quot;</li>
     *   <li><b>SHORT:</b> &quot;1/1/25&quot; (depending on the locale)</li>
     * </ul>
     *
     * <h3>Example Usage:</h3>
     * <pre>{@code
     *   Date now = new Date();
     *   // For example, using FULL style in US English:
     *   String fullStyle = changeDateFormat(now, DateFormat.FULL, Locale.US);
     *   // e.g., "Wednesday, January 1, 2025"
     *
     *   // Using SHORT style in US English:
     *   String shortStyle = changeDateFormat(now, DateFormat.SHORT, Locale.US);
     *   // e.g., "1/1/25"
     * }</pre>
     *
     * @param date   the {@link Date} to format (must not be {@code null}).
     * @param style  one of the predefined date formatting styles in {@link DateFormat},
     *               such as {@link DateFormat#FULL}, {@link DateFormat#LONG},
     *               {@link DateFormat#MEDIUM}, or {@link DateFormat#SHORT}.
     * @param locale the {@link Locale} to apply when formatting the date.
     * @return a localized date string based on the given {@code style} and {@code locale}.
     * @throws NullPointerException if {@code date} or {@code locale} is {@code null}.
     * @see DateFormat#getDateInstance(int, Locale)
     */
    public static String changeDateFormat(Date date, int style, Locale locale) {
        DateFormat dateFormat = DateFormat.getDateInstance(style, locale);
        return dateFormat.format(date);
    }

    /**
     * Parses a date string according to {@code dateFormat}, then delegates to the overloaded
     * {@link #changeDateFormat(Date, int, Locale)} method to reformat the resulting date
     * using the specified {@code style} and {@link Locale}.
     *
     * <p>Internally, this method uses {@link SimpleDateFormat} with the system default locale
     * to parse the date string. If parsing succeeds, it calls
     * {@code changeDateFormat(parsedDate, style, locale)}, which uses
     * {@link DateFormat#getDateInstance(int, Locale)} to produce the final date string in
     * the desired style (e.g., {@link DateFormat#FULL}, {@link DateFormat#LONG},
     * {@link DateFormat#MEDIUM}, or {@link DateFormat#SHORT}).</p>
     *
     * <h3>Example Usage:</h3>
     * <pre>{@code
     *   try {
     *       // Suppose dateString is "2025-01-02 15:30:00"
     *       // and dateFormat is "yyyy-MM-dd HH:mm:ss"
     *
     *       // Style: DateFormat.FULL, Locale: US
     *       String result = changeDateFormat(
     *           "2025-01-02 15:30:00",
     *           "yyyy-MM-dd HH:mm:ss",
     *           DateFormat.FULL,
     *           Locale.US
     *       );
     *       // e.g., "Thursday, January 2, 2025"
     *       System.out.println(result);
     *   } catch (ParseException e) {
     *       e.printStackTrace();
     *   }
     * }</pre>
     *
     * @param dateString The date/time string to parse (e.g., "2025-01-02 15:30:00").
     * @param dateFormat The {@link SimpleDateFormat} pattern used to parse {@code dateString}
     *                   (e.g., "yyyy-MM-dd HH:mm:ss").
     * @param style      The {@link DateFormat} style for the output (e.g., {@link DateFormat#FULL},
     *                   {@link DateFormat#LONG}, {@link DateFormat#MEDIUM}, {@link DateFormat#SHORT}).
     * @param locale     The {@link Locale} to apply for the final formatted string.
     * @return A localized date string in the specified {@code style}, or an empty string
     * if the parsing returns a {@code null} date.
     * @throws ParseException if {@code dateString} cannot be parsed using {@code dateFormat}.
     * @see #changeDateFormat(Date, int, Locale) changeDateFormat(Date, int, Locale)
     */
    public static String changeDateFormat(String dateString,
                                          String dateFormat,
                                          int style,
                                          Locale locale) throws ParseException {
        // 1. Parse the input date string using the given pattern and default locale
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.getDefault());
        Date parsedDate = sdf.parse(dateString);

        // 2. If parsing was successful, re-format using the overloaded method
        return parsedDate != null ? changeDateFormat(parsedDate, style, locale) : "";
    }

    // ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~

    /**
     * Defines a set of date/time format patterns to be used across the application.
     * <p>
     * Each enum constant encapsulates a specific format pattern and can also return
     * a {@link DateTimeFormatter} instance based on that pattern.
     * </p>
     *
     * <p>Usage Example:
     * <pre>{@code
     *   String formattedDate = LocalDateTime.now()
     *       .format(DateFormats.DATETIME_WITH_MICROS.getFormatter());
     * }</pre>
     * </p>
     *
     * @author Fernando Canul Caballero
     * @version 1.0.0
     */
    public enum DateFormats {

        /**
         * Format: 2025-01-02 15:04:05.123456
         */
        DATETIME_WITH_MICROS("yyyy-MM-dd HH:mm:ss.SSSSSS"),

        /**
         * Format: 2025-01-02 15:04:05
         */
        DATETIME_SECONDS("yyyy-MM-dd HH:mm:ss"),

        /**
         * Format: 2025-01-02 15:04
         */
        DATETIME_NO_SECONDS("yyyy-MM-dd HH:mm"),

        /**
         * Format: 2025-01-02 15
         */
        DATETIME_HOUR_ONLY("yyyy-MM-dd HH"),

        /**
         * Format: 2025-01-02
         */
        DATE_DASH("yyyy-MM-dd"),

        /**
         * Format: 2025-01
         */
        YEAR_MONTH("yyyy-MM"),

        /**
         * Format: 2025
         */
        YEAR("yyyy"),

        /**
         * Format: 01
         */
        MONTH("MM"),

        /**
         * Format: 02
         */
        DAY_OF_MONTH("dd"),

        /**
         * Format: 15
         */
        HOUR_24("HH"),

        /**
         * Format: 15:04
         */
        TIME_NO_SECONDS("HH:mm"),

        /**
         * Format: 15:04:05
         */
        TIME_SECONDS("HH:mm:ss"),

        /**
         * Format: 15:04:05.123456
         */
        TIME_WITH_MICROS("HH:mm:ss.SSSSSS"),

        /**
         * Format: 2025/01/02
         */
        DATE_SLASH("yyyy/MM/dd"),

        /**
         * Format: 02/01/2025
         */
        DATE_SLASH_DMY("dd/MM/yyyy"),

        /**
         * ISO 8601 format with time zone offset: 2025-01-02T15:04:05.123+01:00
         *
         * <p>This is just an example pattern if you want to include time zone
         * offset or use an ISO standard format. Adjust if needed.</p>
         */
        ISO_WITH_OFFSET("yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ");

        private final String pattern;

        DateFormats(String pattern) {
            this.pattern = pattern;
        }

        /**
         * Returns the raw format pattern (e.g., "yyyy-MM-dd HH:mm:ss.SSSSSS").
         *
         * @return A non-localized format pattern.
         */
        public String getPattern() {
            return pattern;
        }

        /**
         * Creates and returns a {@link DateTimeFormatter} for this pattern.
         *
         * @return A DateTimeFormatter instance for this pattern.
         */
        @RequiresApi(api = Build.VERSION_CODES.O)
        public DateTimeFormatter getFormatter() {
            return DateTimeFormatter.ofPattern(pattern);
        }

    }

}
