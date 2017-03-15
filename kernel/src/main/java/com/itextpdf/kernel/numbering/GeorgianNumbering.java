package com.itextpdf.kernel.numbering;

/**
 * This class can produce String combinations representing a georgian numeral.
 * See https://en.wikipedia.org/wiki/Georgian_numerals
 */
public class GeorgianNumbering {

    private static final GeorgianDigit[] DIGITS = {
            new GeorgianDigit('\u10D0', 1),
            new GeorgianDigit('\u10D1', 2),
            new GeorgianDigit('\u10D2', 3),
            new GeorgianDigit('\u10D3', 4),
            new GeorgianDigit('\u10D4', 5),
            new GeorgianDigit('\u10D5', 6),
            new GeorgianDigit('\u10D6', 7),
            new GeorgianDigit('\u10F1', 8),
            new GeorgianDigit('\u10D7', 9),
            new GeorgianDigit('\u10D8', 10),
            new GeorgianDigit('\u10D9', 20),
            new GeorgianDigit('\u10DA', 30),
            new GeorgianDigit('\u10DB', 40),
            new GeorgianDigit('\u10DC', 50),
            new GeorgianDigit('\u10F2', 60),
            new GeorgianDigit('\u10DD', 70),
            new GeorgianDigit('\u10DE', 80),
            new GeorgianDigit('\u10DF', 90),
            new GeorgianDigit('\u10E0', 100),
            new GeorgianDigit('\u10E1', 200),
            new GeorgianDigit('\u10E2', 300),
            new GeorgianDigit('\u10F3', 400),
            new GeorgianDigit('\u10E4', 500),
            new GeorgianDigit('\u10E5', 600),
            new GeorgianDigit('\u10E6', 700),
            new GeorgianDigit('\u10E7', 800),
            new GeorgianDigit('\u10E8', 900),
            new GeorgianDigit('\u10E9', 1000),
            new GeorgianDigit('\u10EA', 2000),
            new GeorgianDigit('\u10EB', 3000),
            new GeorgianDigit('\u10EC', 4000),
            new GeorgianDigit('\u10ED', 5000),
            new GeorgianDigit('\u10EE', 6000),
            new GeorgianDigit('\u10F4', 7000),
            new GeorgianDigit('\u10EF', 8000),
            new GeorgianDigit('\u10F0', 9000),
            new GeorgianDigit('\u10F5', 10000)
    };

    private GeorgianNumbering() {
    }

    /**
     * Returns a georgian numeral representation of an integer.
     *
     * @param number a number greater than zero to be converted to georgian notation
     */
    public static String toGeorgian(int number) {
        StringBuilder result = new StringBuilder();
        for (int i = DIGITS.length - 1; i >= 0; i--) {
            GeorgianDigit curDigit = DIGITS[i];
            while (number >= curDigit.value) {
                result.append(curDigit.digit);
                number -= curDigit.value;
            }
        }
        return result.toString();
    }

    private static class GeorgianDigit {
        private char digit;
        private int value;

        GeorgianDigit(char digit, int value) {
            this.digit = digit;
            this.value = value;
        }
    }

}
