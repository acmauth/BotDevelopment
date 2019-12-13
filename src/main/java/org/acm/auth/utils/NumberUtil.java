package org.acm.auth.utils;

public class NumberUtil {
    /**
     * Compare the given string to a regular expression that matches 1 or more digits.
     * @param str The string to check if it's a valid positive integer.
     * @return true if the string represents a positive integer, false otherwise.
     */
    public static boolean isPositiveInteger(String str) {
        return str.matches("\\d+");
    }
}
