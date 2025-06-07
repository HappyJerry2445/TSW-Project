// src/main/java/com/yourpackage/utils/DateTimeFormatterUtil.java
package com.cardhaven.cardhaven.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;

public class DateTimeFormatterUtil {

    public static String formatLocalDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "";
        }
        // You can customize the locale and style as needed
        DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM, FormatStyle.SHORT)
                .withLocale(Locale.getDefault()); // Or Locale.ITALY for specific
        return dateTime.format(formatter);
    }

    public static String formatLocalDateTime(LocalDateTime dateTime, String pattern) {
        if (dateTime == null) {
            return "";
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return dateTime.format(formatter);
    }

    public static String capitalizeFirstLetter(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        // Handle potential leading spaces if necessary, but typically formatters don't produce them.
        return text.substring(0, 1).toUpperCase() + text.substring(1);
    }
}