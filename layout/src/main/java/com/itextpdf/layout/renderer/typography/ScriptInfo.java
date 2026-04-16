package com.itextpdf.layout.renderer.typography;

import java.lang.Character.UnicodeScript;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

/**
 * Class containing information about script support in pdfCalligraph.
 * It contains information about supported scripts and their requirements.
 * <p>
 * This class is intended for internal usage.
 */
public final class ScriptInfo {

    private static final ScriptInfoData scriptInfoData;
    

    static {
        scriptInfoData = new ScriptInfoData(80);
        //supported scripts
        ScriptRequirements marksOnly = new ScriptRequirements(
                Collections.<String>emptyList(), Arrays.<String>asList("mark", "mkmk"),false);
        scriptInfoData.addRequirements(UnicodeScript.ARABIC,
                new ScriptRequirements(Collections.<String>singletonList("arab"),
                Arrays.<String>asList("init", "medi", "fina", "rlig", "rclt", "isol"),
                Arrays.<String>asList("mark", "mkmk"), true, true));
        scriptInfoData.addRequirements(UnicodeScript.ARMENIAN, marksOnly.withOtfScriptNames("armn"));
        scriptInfoData.addRequirements(UnicodeScript.CYRILLIC, marksOnly.withOtfScriptNames("cyrl"));
        scriptInfoData.addRequirements(UnicodeScript.GEORGIAN, marksOnly.withOtfScriptNames("geor"));
        scriptInfoData.addRequirements(UnicodeScript.GREEK, marksOnly.withOtfScriptNames("grek"));
        scriptInfoData.addRequirements(UnicodeScript.LATIN, marksOnly.withOtfScriptNames("latn"));
        scriptInfoData.addRequirements(UnicodeScript.RUNIC, marksOnly.withOtfScriptNames("runr"));
        scriptInfoData.addRequirements(UnicodeScript.OGHAM, marksOnly.withOtfScriptNames("ogam"));
        ScriptRequirements indicReqs = new ScriptRequirements(
                Arrays.<String>asList("akhn", "blw", "half", "pres", "abvs", "blw", "haln", "pstf", "abvm",
                        "calt", "hist", "psts"), Collections.<String>emptyList(), false);
        scriptInfoData.addRequirements(UnicodeScript.DEVANAGARI, indicReqs.withOtfScriptNames("dev2", "deva"));
        scriptInfoData.addRequirements(UnicodeScript.TAMIL, indicReqs.withOtfScriptNames("tml2", "taml"));
        scriptInfoData.addRequirements(UnicodeScript.GURMUKHI, indicReqs.withOtfScriptNames("gur2", "guru"));
        scriptInfoData.addRequirements(UnicodeScript.ORIYA, indicReqs.withOtfScriptNames("ory2", "orya"));
        scriptInfoData.addRequirements(UnicodeScript.BENGALI, indicReqs.withOtfScriptNames("bng2", "beng"));
        scriptInfoData.addRequirements(UnicodeScript.MALAYALAM, indicReqs.withOtfScriptNames("mlm2", "mlym"));
        scriptInfoData.addRequirements(UnicodeScript.TELUGU, indicReqs.withOtfScriptNames("tel2", "telu"));
        scriptInfoData.addRequirements(UnicodeScript.GUJARATI, indicReqs.withOtfScriptNames("gjr2", "gurj"));
        scriptInfoData.addRequirements(UnicodeScript.KANNADA, indicReqs.withOtfScriptNames("knd2", "knda"));
        scriptInfoData.addRequirements(UnicodeScript.SINHALA,indicReqs.withOtfScriptNames("sinh"));

        scriptInfoData.addRequirements(UnicodeScript.KHMER, indicReqs.withOtfScriptNames("khmr").withIsHardcoded(true));

        scriptInfoData.addRequirements(UnicodeScript.HEBREW,
                new ScriptRequirements(Collections.<String>singletonList("hebr"), Collections.<String>emptyList(),
                        Arrays.<String>asList("mark", "mkmk"), true, true));
        scriptInfoData.addRequirements(UnicodeScript.MYANMAR, new ScriptRequirements(Collections.singletonList("mym2"),
                Arrays.<String>asList("locl", "ccmp", "rphf", "pref", "blwf", "pstf", "rlig", "clig"),
                Collections.<String>emptyList(), true, true));
        scriptInfoData.addRequirements(UnicodeScript.THAI,
                new ScriptRequirements(Collections.<String>singletonList("thai"),
                Collections.<String>emptyList(), Collections.<String>emptyList(), true, true));
        scriptInfoData.addRequirements(UnicodeScript.TIBETAN,
                new ScriptRequirements(Collections.<String>singletonList("tibt"),
                Collections.<String>emptyList(), Collections.<String>emptyList(), true, true));

        //non supported scripts
        scriptInfoData.addRequirements(UnicodeScript.BALINESE,
                ScriptRequirements.createUnsupported(Collections.<String>singletonList("bali")));
        scriptInfoData.addRequirements(UnicodeScript.BOPOMOFO,
                ScriptRequirements.createUnsupported(Collections.<String>singletonList("bopo")));
        scriptInfoData.addRequirements(UnicodeScript.BRAILLE,
                ScriptRequirements.createUnsupported(Collections.<String>singletonList("brai")));
        scriptInfoData.addRequirements(UnicodeScript.BUGINESE,
                ScriptRequirements.createUnsupported(Collections.<String>singletonList("bugi")));
        scriptInfoData.addRequirements(UnicodeScript.BUHID,
                ScriptRequirements.createUnsupported(Collections.<String>singletonList("buhd")));
        // SCRIPT_REQ_FEATURE.put(UnicodeScript.BYZANTINE_MUSIC, ScriptRequirements.createUnsupported(Collections
        // .singletonList("byzm")));
        // SCRIPT_REQ_FEATURE.put(UnicodeScript.Canadian SYLLABICS, ScriptRequirements.createUnsupported(Collections
        // .singletonList("cans")));
        scriptInfoData.addRequirements(UnicodeScript.CARIAN,
                ScriptRequirements.createUnsupported(Collections.<String>singletonList("cari")));
        scriptInfoData.addRequirements(UnicodeScript.CHAM,
                ScriptRequirements.createUnsupported(Collections.<String>singletonList("cham")));
        scriptInfoData.addRequirements(UnicodeScript.CHEROKEE,
                ScriptRequirements.createUnsupported(Collections.<String>singletonList("cher")));
        // SCRIPT_REQ_FEATURE.put(UnicodeScript.CJK IDEOGRAPHIC, ScriptRequirements.createUnsupported(Collections
        // .singletonList("hani")));
        scriptInfoData.addRequirements(UnicodeScript.COPTIC,
                ScriptRequirements.createUnsupported(Collections.<String>singletonList("copt")));
        // SCRIPT_REQ_FEATURE.put(UnicodeScript.Cypriot SYLLABARY, ScriptRequirements.createUnsupported(Collections
        // .singletonList("cprt")));
        scriptInfoData.addRequirements(UnicodeScript.DESERET,
                ScriptRequirements.createUnsupported(Collections.<String>singletonList("dsrt")));
        scriptInfoData.addRequirements(UnicodeScript.ETHIOPIC,
                ScriptRequirements.createUnsupported(Collections.<String>singletonList("ethi")));
        scriptInfoData.addRequirements(UnicodeScript.GLAGOLITIC,
                ScriptRequirements.createUnsupported(Collections.<String>singletonList("glag")));
        scriptInfoData.addRequirements(UnicodeScript.GOTHIC,
                ScriptRequirements.createUnsupported(Collections.<String>singletonList("goth")));
        scriptInfoData.addRequirements(UnicodeScript.HANGUL,
                ScriptRequirements.createUnsupported(Arrays.<String>asList("hang", "jamo")));
        scriptInfoData.addRequirements(UnicodeScript.HANUNOO,
                ScriptRequirements.createUnsupported(Collections.<String>singletonList("hano")));
        scriptInfoData.addRequirements(UnicodeScript.HIRAGANA,
                ScriptRequirements.createUnsupported(Collections.<String>singletonList("kana")));
        scriptInfoData.addRequirements(UnicodeScript.JAVANESE,
                ScriptRequirements.createUnsupported(Collections.<String>singletonList("java")));
        scriptInfoData.addRequirements(UnicodeScript.KATAKANA,
                ScriptRequirements.createUnsupported(Collections.<String>singletonList("kana")));
        scriptInfoData.addRequirements(UnicodeScript.KAYAH_LI,
                ScriptRequirements.createUnsupported(Collections.<String>singletonList("kali")));
        scriptInfoData.addRequirements(UnicodeScript.KHAROSHTHI,
                ScriptRequirements.createUnsupported(Collections.<String>singletonList("khar")));
        scriptInfoData.addRequirements(UnicodeScript.LAO,
                ScriptRequirements.createUnsupported(Collections.<String>singletonList("lao ")).withIsHardcoded(true));
        scriptInfoData.addRequirements(UnicodeScript.LEPCHA,
                ScriptRequirements.createUnsupported(Collections.<String>singletonList("lepc")));
        scriptInfoData.addRequirements(UnicodeScript.LIMBU,
                ScriptRequirements.createUnsupported(Collections.<String>singletonList("limb")));
        scriptInfoData.addRequirements(UnicodeScript.LINEAR_B,
                ScriptRequirements.createUnsupported(Collections.<String>singletonList("linb")));
        scriptInfoData.addRequirements(UnicodeScript.LYCIAN,
                ScriptRequirements.createUnsupported(Collections.<String>singletonList("lyci")));
        scriptInfoData.addRequirements(UnicodeScript.LYDIAN,
                ScriptRequirements.createUnsupported(Collections.<String>singletonList("lydi")));
        // SCRIPT_REQ_FEATURE.put(UnicodeScript.Mathematical Alphanumeric SYMBOLS, ScriptRequirements
        // .createUnsupported(Collections.<String>singletonList("math")));
        scriptInfoData.addRequirements(UnicodeScript.MONGOLIAN,
                ScriptRequirements.createUnsupported(Collections.<String>singletonList("mong")));
        // SCRIPT_REQ_FEATURE.put(UnicodeScript.Musical SYMBOLS, ScriptRequirements.createUnsupported(Collections
        // .singletonList("musc")));
        scriptInfoData.addRequirements(UnicodeScript.NEW_TAI_LUE,
                ScriptRequirements.createUnsupported(Collections.<String>singletonList("talu")));
        scriptInfoData.addRequirements(UnicodeScript.NKO,
                ScriptRequirements.createUnsupported(Collections.<String>singletonList("nko ")));
        scriptInfoData.addRequirements(UnicodeScript.OL_CHIKI,
                ScriptRequirements.createUnsupported(Collections.<String>singletonList("olck")));
        scriptInfoData.addRequirements(UnicodeScript.OLD_ITALIC,
                ScriptRequirements.createUnsupported(Collections.<String>singletonList("ital")));
        // "Old Persian Cuneiform" script name
        scriptInfoData.addRequirements(UnicodeScript.OLD_PERSIAN,
                ScriptRequirements.createUnsupported(Collections.<String>singletonList("xpeo")));
        scriptInfoData.addRequirements(UnicodeScript.OSMANYA,
                ScriptRequirements.createUnsupported(Collections.<String>singletonList("osma")));
        scriptInfoData.addRequirements(UnicodeScript.PHAGS_PA,
                ScriptRequirements.createUnsupported(Collections.<String>singletonList("phag")));
        scriptInfoData.addRequirements(UnicodeScript.PHOENICIAN,
                ScriptRequirements.createUnsupported(Collections.<String>singletonList("phnx")));
        scriptInfoData.addRequirements(UnicodeScript.REJANG,
                ScriptRequirements.createUnsupported(Collections.<String>singletonList("rjng")));
        scriptInfoData.addRequirements(UnicodeScript.SAURASHTRA,
                ScriptRequirements.createUnsupported(Collections.<String>singletonList("saur")));
        scriptInfoData.addRequirements(UnicodeScript.SHAVIAN,
                ScriptRequirements.createUnsupported(Collections.<String>singletonList("shaw")));
        // SCRIPT_REQ_FEATURE.put(UnicodeScript.Sumero-Akkadian CUNEIFORM, ScriptRequirements.createUnsupported
        // (Collections.<String>singletonList("xsux")));
        scriptInfoData.addRequirements(UnicodeScript.SUNDANESE,
                ScriptRequirements.createUnsupported(Collections.<String>singletonList("sund")));
        scriptInfoData.addRequirements(UnicodeScript.SYLOTI_NAGRI,
                ScriptRequirements.createUnsupported(Collections.<String>singletonList("sylo")));
        scriptInfoData.addRequirements(UnicodeScript.SYRIAC,
                ScriptRequirements.createUnsupported(Collections.<String>singletonList("syrc")));
        scriptInfoData.addRequirements(UnicodeScript.TAGALOG,
                ScriptRequirements.createUnsupported(Collections.<String>singletonList("tglg")));
        scriptInfoData.addRequirements(UnicodeScript.TAGBANWA,
                ScriptRequirements.createUnsupported(Collections.<String>singletonList("tagb")));
        scriptInfoData.addRequirements(UnicodeScript.TAI_LE,
                ScriptRequirements.createUnsupported(Collections.<String>singletonList("tale")));
        scriptInfoData.addRequirements(UnicodeScript.THAANA,
                ScriptRequirements.createUnsupported(Collections.<String>singletonList("thaa")));
        scriptInfoData.addRequirements(UnicodeScript.TIFINAGH,
                ScriptRequirements.createUnsupported(Collections.<String>singletonList("tfng")));
        // "Ugaritic Cuneiform" script name
        scriptInfoData.addRequirements(UnicodeScript.UGARITIC,
                ScriptRequirements.createUnsupported(Collections.<String>singletonList("ugar")));
        scriptInfoData.addRequirements(UnicodeScript.VAI,
                ScriptRequirements.createUnsupported(Collections.<String>singletonList("vai ")));
        scriptInfoData.addRequirements(UnicodeScript.YI,
                ScriptRequirements.createUnsupported(Collections.<String>singletonList("yi  ")));
    }

    private ScriptInfo() {
        // do nothing
    }

    /**
     * Checks if the script is supported by pdfCalligraph.
     * Supported script have requirements set in {@link ScriptRequirements}.
     *
     * @param script the script to check
     * @return <code>true</code> if the script is supported by pdfCalligraph and <code>false</code> otherwise
     */
    public static boolean scriptSupported(UnicodeScript script) {
        return scriptInfoData.scriptSupported(script);
    }

    /**
     * Returns requirements for the script. Only supported script have the requirements set.
     * @param script the script to get requirements for
     * @return requirements for the script. Only supported script have the requirements set
     */
    public static ScriptRequirements getRequirements(UnicodeScript script) {
        return scriptInfoData.get(script);
    }

    /**
     * Returns a set if scripts supported by pdfCalligraph.
     *
     * @return a set if scripts supported by pdfCalligraph
     */
    public static Set<UnicodeScript> getSupportedScripts() {
        return scriptInfoData.getSupportedScripts();
    }

}
