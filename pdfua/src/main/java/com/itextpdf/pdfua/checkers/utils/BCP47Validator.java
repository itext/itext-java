package com.itextpdf.pdfua.checkers.utils;

import java.util.regex.Pattern;

/**
 * This class is a validator for IETF BCP 47 language tag (RFC 5646)
 */
public class BCP47Validator {
    private static String regular = "(art-lojban|cel-gaulish|no-bok|no-nyn|zh-guoyu|zh-hakka|zh-min|zh-min-nan|zh-xiang)";
    private static String irregular = "(en-GB-oed|i-ami|i-bnn|i-default|i-enochian|i-hak|i-klingon|i-lux|i-mingo|i-navajo|i-pwn|i-tao|i-tay|i-tsu|sgn-BE-FR|sgn-BE-NL|sgn-CH-DE)";
    private static String grandfathered = "(?<grandfathered>" + irregular + "|" + regular + ")";
    private static String privateUse = "(?<privateUse>x(-[A-Za-z0-9]{1,8})+)";
    private static String singleton = "[0-9A-WY-Za-wy-z]";
    private static String extension = "(?<extension>" + singleton + "(-[A-Za-z0-9]{2,8})+)";
    private static String variant = "(?<variant>[A-Za-z0-9]{5,8}|[0-9][A-Za-z0-9]{3})";
    private static String region = "(?<region>[A-Za-z]{2}|[0-9]{3})";
    private static String script = "(?<script>[A-Za-z]{4})";
    private static String extlang = "(?<extlang>[A-Za-z]{3}(-[A-Za-z]{3}){0,2})";
    private static String language = "(?<language>([A-Za-z]{2,3}(-" + extlang + ")?)|[A-Za-z]{4}|[A-Za-z]{5,8})";
    private static String langtag = "(" + language + "(-" + script + ")?" + "(-" + region + ")?" + "(-" + variant + ")*" + "(-" + extension + ")*" + "(-" + privateUse + ")?" + ")";
    //Java regex polices doesn't allow duplicate named capture groups, so we have to change the 2nd use <privateUse> to ?<privateUse1>
    private static Pattern languageTagPattern = Pattern.compile("^(" + grandfathered + "|" + langtag + "|" + privateUse.replace("privateUse", "privateUse1") + ")$");

    private BCP47Validator() {}

    /**
     * Validate language tag against RFC 5646.
     *
     * @param languageTag language tag string
     *
     * @return {@code true} if it is a valid tag, {@code false} otherwise
     */
    public static boolean validate(String languageTag) {
        return languageTagPattern.matcher(languageTag).matches();
    }

}
