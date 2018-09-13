package com.itextpdf.svg.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SvgRegexUtils {

    public static boolean ContainsAtLeastOneMatch(Pattern regexPattern, String stringToExamine){
        Matcher matcher = regexPattern.matcher(stringToExamine);
        return matcher.find();
    }
}
