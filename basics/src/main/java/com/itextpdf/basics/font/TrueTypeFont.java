package com.itextpdf.basics.font;

import com.itextpdf.basics.IntHashtable;
import com.itextpdf.basics.LogMessageConstant;
import com.itextpdf.basics.PdfException;
import com.itextpdf.basics.Utilities;
import com.itextpdf.basics.font.indic.BasePosition;
import com.itextpdf.basics.font.indic.DevanagariCluster;
import com.itextpdf.basics.font.indic.IndicCategory;
import com.itextpdf.basics.font.indic.IndicConfig;
import com.itextpdf.basics.font.indic.IndicPosition;
import com.itextpdf.basics.font.indic.IndicSyllabicCategory;
import com.itextpdf.basics.font.indic.IndicTable;
import com.itextpdf.basics.font.indic.IndicUtil;
import com.itextpdf.basics.font.indic.RephMode;
import com.itextpdf.basics.font.indic.RephPosition;
import com.itextpdf.basics.font.otf.FeatureRecord;
import com.itextpdf.basics.font.otf.Glyph;
import com.itextpdf.basics.font.otf.GlyphLine;
import com.itextpdf.basics.font.otf.GlyphSubstitutionTableReader;
import com.itextpdf.basics.font.otf.LanguageRecord;
import com.itextpdf.basics.font.otf.OpenTableLookup;
import com.itextpdf.basics.font.otf.OpenTypeGdefTableReader;
import com.itextpdf.basics.font.otf.ScriptRecord;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

>>>>>>> Initial Devanagari syllables clusterization. Shaping implementation prototyping
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TrueTypeFont extends FontProgram {

    private OpenTypeParser fontParser;

    private boolean isSymbol;

    protected int[][] bBoxes;

    protected boolean isVertical;

    private GlyphSubstitutionTableReader gsubTable;
    private OpenTypeGdefTableReader gdefTable;

    private boolean applyLigatures = false;
    private Character.UnicodeScript script = null;
    private String otfScriptTag = null;

    /**
     * The map containing the kerning information. It represents the content of
     * table 'kern'. The key is an <CODE>Integer</CODE> where the top 16 bits
     * are the glyph number for the first character and the lower 16 bits are the
     * glyph number for the second character. The value is the amount of kerning in
     * normalized 1000 units as an <CODE>Integer</CODE>. This value is usually negative.
     */
    protected IntHashtable kerning = new IntHashtable();

    private byte[] fontStreamBytes;

    public TrueTypeFont(String path) throws IOException {
        fontParser = new OpenTypeParser(path);
        initializeFontProperties();
    }

    TrueTypeFont(String ttcPath, int ttcIndex) throws IOException {
        fontParser = new OpenTypeParser(ttcPath, ttcIndex);
        initializeFontProperties();
    }

    TrueTypeFont(byte[] ttc, int ttcIndex) throws IOException {
        fontParser = new OpenTypeParser(ttc, ttcIndex);
        initializeFontProperties();
    }

    public TrueTypeFont(byte[] ttf) throws IOException {
        fontParser = new OpenTypeParser(ttf);
        initializeFontProperties();
    }

    public boolean isApplyLigatures() {
        return applyLigatures;
    }

    public void setApplyLigatures(boolean applyLigatures) {
        this.applyLigatures = applyLigatures;
    }

    public void setScriptForOTF(Character.UnicodeScript script) {
        this.script = script;
        switch (script) {
            case LATIN:
                otfScriptTag = "latn";
                break;
            case ARABIC:
                otfScriptTag = "arab";
                break;
            case DEVANAGARI:
                otfScriptTag = "dev2"; // TODO there is also old-style "deva" implementation
                break;
            default:
                otfScriptTag = null;
                break;
        }
    }

    public String getOtfScript() {
        return otfScriptTag;
    }

    public boolean applyOtfScript(GlyphLine glyphLine) {
        if (otfScriptTag == null) {
            return false;
        }
        if (gsubTable != null) {
            LanguageRecord languageRecord = getLanguageRecord();
            if (languageRecord == null) {
                return false;
            }
            boolean transformed = false;

            if (Character.UnicodeScript.ARABIC.equals(script)) {
                List<OpenTableLookup> init = null;
                List<OpenTableLookup> medi = null;
                List<OpenTableLookup> fina = null;
                List<OpenTableLookup> rlig = null;
                for (int featureIndex : languageRecord.features) {
                    FeatureRecord feature = gsubTable.getFeatureRecords().get(featureIndex);
                    switch (feature.tag) {
                        case "init":
                            init = gsubTable.getLookups(new FeatureRecord[]{feature});
                            break;
                        case "medi":
                            medi = gsubTable.getLookups(new FeatureRecord[]{feature});
                            break;
                        case "fina":
                            fina = gsubTable.getLookups(new FeatureRecord[]{feature});
                            break;
                        case "rlig":
                            rlig = gsubTable.getLookups(new FeatureRecord[]{feature});
                            break;
                    }
                }

                if (applyInitMediFinaShaping(glyphLine, init, medi, fina)) {
                    transformed = true;
                }
                if (applyRligFeature(glyphLine, rlig)) {
                    transformed = true;
                }
            } else if (Character.UnicodeScript.DEVANAGARI.equals(script)) {
                List<DevanagariCluster> clusters = splitDevanagariGlyphLineIntoClusters(glyphLine);

                if (clusters != null && clusters.size() > 0) {
                    for (DevanagariCluster cluster : clusters) {

                        // TODO this is too long, move up
                        Map<String, List<OpenTableLookup>> features = new LinkedHashMap<>();
                        for (int featureIndex : languageRecord.features) {
                            FeatureRecord feature = gsubTable.getFeatureRecords().get(featureIndex);
                            List<OpenTableLookup> lookups = gsubTable.getLookups(new FeatureRecord[]{feature});
                            features.put(feature.tag, lookups);
                        }

                        setIndicProperties(cluster);

                        IndicConfig devanagariConfig = new IndicConfig.DevanagariConfig();
                        boolean isOldSpec = devanagariConfig.hasOldSpec() && !otfScriptTag.endsWith("2");

                        // TODO reordering
                        initialReordering(cluster, devanagariConfig, features.get("rphf"), isOldSpec);

                        int start = glyphLine.start;
                        int end = glyphLine.end;

                        // TODO do it cluster by cluster. end might be shifted as a result of applying a feature to a cluster, so be careful.

                        // Localized forms
                        if (transform(features.get("locl"), glyphLine)) {
                            transformed = true;
                        }

                        // Basic Shaping forms
                        String[] basicShapingForms = new String[] {"nukt", "akhn", "rphf", "rkrf", "blwf", "half", "vatu", "cjct"};
                        for (String feature : basicShapingForms) {
                            if (transform(features.get(feature), glyphLine)) {
                                transformed = true;
                            }
                        }

                        // TODO final reordering

                        String[] presentationForms = new String[] {"pres", "abvs", "blws", "psts", "haln", "calt"};
                        for (String feature : presentationForms) {
                            if (transform(features.get(feature), glyphLine)) {
                                transformed = true;
                            }
                        }
                    }

                    GlyphLine newGlyphLine = new GlyphLine(glyphLine);
                    List<Glyph> glyphs = new ArrayList<>();
                    for (int i = 0; i < glyphLine.start; i++) {
                        glyphs.add(glyphLine.glyphs.get(i));
                    }
                    int lastFinish = glyphLine.start;
                    for (DevanagariCluster cluster : clusters) {
                        if (cluster.glyphLineStart > lastFinish) {
                            for (int j = lastFinish; j < cluster.glyphLineStart; j++) {
                                glyphs.add(glyphLine.glyphs.get(j));
                            }
                        }
                        for (int j = 0; j < cluster.glyphs.size(); j++) {
                            glyphs.add(cluster.glyphs.get(j));
                        }
                        lastFinish = cluster.glyphLineEnd;
                    }
                    for (int j = lastFinish; j < glyphLine.end; j++) {
                        glyphs.add(glyphLine.glyphs.get(j));
                    }
                    newGlyphLine.glyphs = glyphs;
                    newGlyphLine.end = glyphs.size();
                    for (int j = glyphLine.end; j < glyphLine.glyphs.size(); j++) {
                        newGlyphLine.glyphs.add(glyphLine.glyphs.get(j));
                    }

                    glyphLine.glyphs.clear();
                    glyphLine.glyphs.addAll(newGlyphLine.glyphs);
                    glyphLine.end = newGlyphLine.end;
                    transformed = true;
                }


            }

            return transformed;
        }
        return false;
    }

    private void setIndicProperties(DevanagariCluster cluster) {
        StringBuilder newClasses = new StringBuilder();
        for (int i = 0; i < cluster.glyphs.size(); i++) {
            int u = cluster.glyphs.get(i).unicode;
            int type = IndicTable.getCategories(u);
            int cat = type & 0x7F;
            int pos = type >> 8;

            /*
             * Re-assign category
             */


            /* The spec says U+0952 is OT_A.  However, testing shows that Uniscribe
             * treats a whole bunch of characters similarly.
             * TESTS: For example, for U+0951:
             * U+092E,U+0947,U+0952
             * U+092E,U+0952,U+0947
             * U+092E,U+0947,U+0951
             * U+092E,U+0951,U+0947
             * U+092E,U+0951,U+0952
             * U+092E,U+0952,U+0951
             */
            if (IndicUtil.inRanges (u, 0x0951, 0x0952,
                    0x1CD0, 0x1CD2,
                    0x1CD4, 0x1CE1) ||
                    u == 0x1CF4)
                cat = IndicCategory.OT_A;
            /* The following act more like the Bindus. */
            else if (IndicUtil.inRange (u, 0x0953, 0x0954))
                cat = IndicCategory.OT_SM;
            /* The following act like consonants. */
            else if (IndicUtil.inRanges (u, 0x0A72, 0x0A73,
                    0x1CF5, 0x1CF6))
                cat = IndicCategory.OT_C;
            /* TODO: The following should only be allowed after a Visarga.
             * For now, just treat them like regular tone marks. */
            else if (IndicUtil.inRange (u, 0x1CE2, 0x1CE8))
                cat = IndicCategory.OT_A;
           /* TODO: The following should only be allowed after some of
            * the nasalization marks, maybe only for U+1CE9..U+1CF1.
            * For now, just treat them like tone marks. */
            else if (u == 0x1CED)
                cat = IndicCategory.OT_A;
            /* The following take marks in standalone clusters, similar to Avagraha. */
            else if (IndicUtil.inRanges (u, 0xA8F2, 0xA8F7,
                    0x1CE9, 0x1CEC,
                    0x1CEE, 0x1CF1))
            {
                cat = IndicCategory.OT_Symbol;
                assert (IndicSyllabicCategory.INDIC_SYLLABIC_CATEGORY_AVAGRAHA == IndicCategory.OT_Symbol);
            }
            else if (IndicUtil.inRange(u, 0x17CD, 0x17D1) ||
                    u == 0x17CB || u == 0x17D3 || u == 0x17DD) /* Khmer Various signs */
            {
            /* These are like Top Matras. */
                cat = IndicCategory.OT_M;
                pos = IndicPosition.POS_ABOVE_C;
            }
            else if (u == 0x17C6) cat = IndicCategory.OT_N; /* Khmer Bindu doesn't like to be repositioned. */
            else if (u == 0x17D2) cat = IndicCategory.OT_Coeng; /* Khmer coeng */
            else if (IndicUtil.inRange (u, 0x2010, 0x2011))
                cat = IndicCategory.OT_PLACEHOLDER;
            else if (u == 0x25CC) cat = IndicCategory.OT_DOTTEDCIRCLE;
            else if (u == 0xA982) cat = IndicCategory.OT_SM; /* Javanese repha. */
            else if (u == 0xA9BE) cat = IndicCategory.OT_CM2; /* Javanese medial ya. */
            else if (u == 0xA9BD) { cat = IndicCategory.OT_M; pos = IndicPosition.POS_POST_C; } /* Javanese vocalic r. */


            /*
             * Re-assign position.
             */
            if ((flag(cat) & CONSONANT_FLAGS) != 0) {
                pos = IndicPosition.POS_BASE_C;
                if (isRa(u))
                    cat = IndicCategory.OT_Ra;
            } else if (cat == IndicCategory.OT_M) {
                pos = matraPosition(u, pos);
            } else if ((flag(cat) & (flag(IndicCategory.OT_SM) | flag(IndicCategory.OT_VD) | flag(IndicCategory.OT_A) | flag(IndicCategory.OT_Symbol))) != 0) {
                pos = IndicPosition.POS_SMVD;
            }

            if (u == 0x0B01) pos = IndicPosition.POS_BEFORE_SUB; /* Oriya Bindu is BeforeSub in the spec. */

            newClasses.append((char)cat);
            cluster.indicPos.set(i, pos);
        }
        cluster.classes = newClasses.toString();
    }

    private int matraPosition(int u, int side) {
        switch (side) {
            case IndicPosition.POS_PRE_C:
                return IndicPosition.MatraPosLeft(u);
            case IndicPosition.POS_POST_C:
                return IndicPosition.MatraPosRight(u);
            case IndicPosition.POS_ABOVE_C:
                return IndicPosition.MatraPosTop(u);
            case IndicPosition.POS_BELOW_C:
                return IndicPosition.MatraPosBottom(u);
        }
        return side;
    }

    private void initialReordering(final DevanagariCluster cluster, final IndicConfig config, List<OpenTableLookup> rphf, boolean isOldSpec) {
       // TODO at some point reassigning categories takes place. Not sure when

      /* 1. Find base consonant:
       *
       * The shaping engine finds the base consonant of the syllable, using the
       * following algorithm: starting from the end of the syllable, move backwards
       * until a consonant is found that does not have a below-base or post-base
       * form (post-base forms have to follow below-base forms), or that is not a
       * pre-base reordering Ra, or arrive at the first consonant. The consonant
       * stopped at will be the base.
       *
       *   o If the syllable starts with Ra + Halant (in a script that has Reph)
       *     and has more than one consonant, Ra is excluded from candidates for
       *     base consonants.
       */

        int start = 0;
        int end = cluster.glyphs.size();
        int base = end;
        boolean hasReph = false; // Ra + Halant

        {
        /* -> If the syllable starts with Ra + Halant (in a script that has Reph)
         *    and has more than one consonant, Ra is excluded from candidates for
         *    base consonants. */
            int limit = start;
            if (config.getRephPosition() != RephPosition.REPH_POS_DONT_CARE &&
                /* TODO indic_plan->mask_array[RPHF] &&*/
                    cluster.glyphs.size() >= 3 &&
                    (
                            (config.getRephMode() == RephMode.REPH_MODE_IMPLICIT && !isJoiner(cluster.glyphs.get(start + 2), cluster.classes.charAt(start + 2))) ||
                                    (config.getRephMode() == RephMode.REPH_MODE_EXPLICIT && cluster.classes.charAt(start + 2) == IndicCategory.OT_ZWJ)
                    )) {
                          /* See if it matches the 'rphf' feature. */
                List<Glyph> glyphs = new ArrayList<Glyph>() {{
                    add(cluster.glyphs.get(0));
                    add(cluster.glyphs.get(1));
                    add(config.getRephMode() == RephMode.REPH_MODE_EXPLICIT ? cluster.glyphs.get(2) : null);
                }};
                if (wouldSubstitute(rphf, glyphs, 2) ||
                        (config.getRephMode() == RephMode.REPH_MODE_EXPLICIT &&
                                wouldSubstitute(rphf, glyphs, 3))) {
                    limit += 2;
                    while (limit < end && isJoiner(cluster.glyphs.get(limit), cluster.classes.charAt(limit)))
                        limit++;
                    base = start;
                    hasReph = true;
                }
            } else if (config.getRephMode() == RephMode.REPH_MODE_LOG_REPHA && cluster.classes.charAt(start) == IndicCategory.OT_Repha) {
                limit += 1;
                while (limit < end && isJoiner(cluster.glyphs.get(limit), cluster.classes.charAt(limit)))
                    limit++;
                base = start;
                hasReph = true;
            }

            switch (config.getBasePosition()) {
                case BasePosition.BASE_POS_LAST: {
                /* -> starting from the end of the syllable, move backwards */
                    int i = end;
                    boolean seenBelow = false;
                    do {
                        i--;
                    /* -> until a consonant is found */
                        if (isConsonant(cluster.glyphs.get(i), cluster.classes.charAt(i))) {
                    /* -> that does not have a below-base or post-base form
                     * (post-base forms have to follow below-base forms), */
                            if (cluster.indicPos.get(i) != IndicPosition.POS_BELOW_C &&
                                    (cluster.indicPos.get(i) != IndicPosition.POS_POST_C || seenBelow)) {
                                base = i;
                                break;
                            }
                            if (cluster.indicPos.get(i) == IndicPosition.POS_BELOW_C)
                                seenBelow = true;

                        /* -> or that is not a pre-base reordering Ra,
                         *
                         * IMPLEMENTATION NOTES:
                         *
                         * Our pre-base reordering Ra's are marked POS_POST_C, so will be skipped
                         * by the logic above already.
                         */

                        /* -> or arrive at the first consonant. The consonant stopped at will
                         * be the base. */
                            base = i;
                        } else {
                        /* A ZWJ after a Halant stops the base search, and requests an explicit
                         * half form.
                         * A ZWJ before a Halant, requests a subjoined form instead, and hence
                         * search continues.  This is particularly important for Bengali
                         * sequence Ra,H,Ya that should form Ya-Phalaa by subjoining Ya. */
                            if (start < i &&
                                    cluster.classes.charAt(i) == IndicCategory.OT_ZWJ &&
                                    cluster.classes.charAt(i - 1) == IndicCategory.OT_H)
                                break;
                        }
                    } while (i > limit);
                }
                break;
                // TODO
                default:
                    throw new IllegalStateException();
            }

        }


        /* 2. Decompose and reorder Matras:
         *
         * Each matra and any syllable modifier sign in the cluster are moved to the
         * appropriate position relative to the consonant(s) in the cluster. The
         * shaping engine decomposes two- or three-part matras into their constituent
         * parts before any repositioning. Matra characters are classified by which
         * consonant in a conjunct they have affinity for and are reordered to the
         * following positions:
         *
         *   o Before first half form in the syllable
         *   o After subjoined consonants
         *   o After post-form consonant
         *   o After main consonant (for above marks)
         *
         * IMPLEMENTATION NOTES:
         *
         * The normalize() routine has already decomposed matras for us, so we don't
         * need to worry about that.
         */

        /* 3.  Reorder marks to canonical order:
         *
         * Adjacent nukta and halant or nukta and vedic sign are always repositioned
         * if necessary, so that the nukta is first.
         *
         * IMPLEMENTATION NOTES:
         *
         * We don't need to do this: the normalize() routine already did this for us.
         */


        /* Reorder characters */

        for (int i = start; i < base; i++)
        cluster.indicPos.set(i, Math.min(IndicPosition.POS_PRE_C, cluster.indicPos.get (i)));

        if (base < end)
            cluster.indicPos.set(base, IndicPosition.POS_BASE_C);

        /* Mark final consonants.  A final consonant is one appearing after a matra,
         * like in Khmer. */
        for (int i = base + 1; i < end; i++)
        if (cluster.classes.charAt(i) == IndicCategory.OT_M) {
            for (int j = i + 1; j < end; j++)
            if (isConsonant(cluster.glyphs.get(j), cluster.classes.charAt(j))) {
                cluster.indicPos.set(j, IndicPosition.POS_FINAL_C);
                break;
            }
            break;
        }

          /* Handle beginning Ra */
        if (hasReph)
            cluster.indicPos.set(start, IndicPosition.POS_RA_TO_BECOME_REPH);

        /* For old-style Indic script tags, move the first post-base Halant after
         * last consonant.
         *
         * Reports suggest that in some scripts Uniscribe does this only if there
         * is *not* a Halant after last consonant already (eg. Kannada), while it
         * does it unconditionally in other scripts (eg. Malayalam).  We don't
         * currently know about other scripts, so we single out Malayalam for now.
         *
         * Kannada test case:
         * U+0C9A,U+0CCD,U+0C9A,U+0CCD
         * With some versions of Lohit Kannada.
         * https://bugs.freedesktop.org/show_bug.cgi?id=59118
         *
         * Malayalam test case:
         * U+0D38,U+0D4D,U+0D31,U+0D4D,U+0D31,U+0D4D
         * With lohit-ttf-20121122/Lohit-Malayalam.ttf
         */

        if (isOldSpec) {
            boolean disallowDoubleHalants = true; // TODO!! buffer->props.script != HB_SCRIPT_MALAYALAM;
            for (int i = base + 1; i < end; i++)
                if (cluster.classes.charAt(i) == IndicCategory.OT_H) {
                    int j;
                    for (j = end - 1; j > i; j--)
                        if (isConsonant(cluster.glyphs.get(j), cluster.classes.charAt(j)) ||
                                (disallowDoubleHalants && cluster.classes.charAt(j) == IndicCategory.OT_H))
                            break;
                    if (cluster.classes.charAt(j) != IndicCategory.OT_H && j > i) {
                    /* Move Halant to after last consonant. */
                        Glyph tG = cluster.glyphs.get(i);
                        int tPos = cluster.indicPos.get(i);
                        char tCat = cluster.classes.charAt(i);
                        cluster.memMove(i, i + 1, j - i);
                        cluster.set(j, tG, tCat, tPos);
                    }
                    break;
                }
        }

        /* Attach misc marks to previous char to move with them. */
        {
            int last_pos = IndicPosition.POS_START;
            for (int i = start; i < end; i++) {
                if ((flag(cluster.classes.charAt(i)) & (JOINER_FLAGS | flag(IndicCategory.OT_N) | flag(IndicCategory.OT_RS) | MEDIAL_FLAGS | HALANT_OR_COENG_FLAGS)) != 0) {
                    cluster.indicPos.set(i, last_pos);
                    if (cluster.classes.charAt(i) == IndicCategory.OT_H && cluster.indicPos.get(i) == IndicPosition.POS_PRE_M) {
                          /*
                           * Uniscribe doesn't move the Halant with Left Matra.
                           * TEST: U+092B,U+093F,U+094DE
                           * We follow.  This is important for the Sinhala
                           * U+0DDA split matra since it decomposes to U+0DD9,U+0DCA
                           * where U+0DD9 is a left matra and U+0DCA is the virama.
                           * We don't want to move the virama with the left matra.
                           * TEST: U+0D9A,U+0DDA
                           */
                        for (int j = i; j > start; j--)
                            if (cluster.indicPos.get(j - 1) != IndicPosition.POS_PRE_M) {
                                cluster.indicPos.set(i, cluster.indicPos.get(j - 1));
                                break;
                            }
                    }
                } else if (cluster.indicPos.get(i) != IndicPosition.POS_SMVD) {
                    last_pos = cluster.indicPos.get(i);
                }
            }
        }

        /* For post-base consonants let them own anything before them
         * since the last consonant or matra. */
        {
            int last = base;
            for (int i = base + 1; i < end; i++)
                if (isConsonant(cluster.glyphs.get(i), cluster.classes.charAt(i))) {
                    for (int j = last + 1; j < i; j++)
                        if (cluster.indicPos.get(j) < IndicPosition.POS_SMVD)
                            cluster.indicPos.set(j, cluster.indicPos.get(i));
                    last = i;
                } else if (cluster.classes.charAt(i) == IndicCategory.OT_M)
                    last = i;
        }

        {
            /* Use syllable() for sort accounting temporarily. */
            // TODO do we need this?
            // int syllable = info[start].syllable();
            // for (unsigned int i = start; i < end; i++)
            // info[i].syllable() = i - start;

            /* Sit tight, rock 'n roll! */
            cluster.sortIndicOrder();
            /* Find base again */
            base = end;
            for (int i = start; i < end; i++)
                if (cluster.indicPos.get(i) == IndicPosition.POS_BASE_C) {
                    base = i;
                    break;
                }
            /* Things are out-of-control for post base positions, they may shuffle
             * around like crazy.  In old-spec mode, we move halants around, so in
             * that case merge all clusters after base.  Otherwise, check the sort
             * order and merge as needed.
             * For pre-base stuff, we handle cluster issues in final reordering.
             *
             * We could use buffer->sort() for this, if there was no special
             * reordering of pre-base stuff happening later...
             */
            // TODO
//            if (isOldSpec || end - base > 127)
//                buffer->merge_clusters (base, end);
//            else
//            {
//            /* Note!  syllable() is a one-byte field. */
//                for (int i = base; i < end; i++)
//                if (info[i].syllable() != 255)
//                {
//                    int max = i;
//                    int j = start + info[i].syllable();
//                    while (j != i)
//                    {
//                        max = MAX (max, j);
//                        int next = start + info[j].syllable();
//                        info[j].syllable() = 255; /* So we don't process j later again. */
//                        j = next;
//                    }
//                    if (i != max)
//                        buffer->merge_clusters (i, max + 1);
//                }
//            }

            /* Put syllable back in. */
            // TODO do we need this?
//            for (unsigned int i = start; i < end; i++)
//            info[i].syllable() = syllable;
        }

        /* Setup masks now */

        // TODO
//        {
//            hb_mask_t mask;
//
//            /* Reph */
//            for (int i = start; i < end && info[i].indic_position() == IndicPosition.POS_RA_TO_BECOME_REPH; i++)
//                info[i].mask |= indic_plan -> mask_array[RPHF];
//
//            /* Pre-base */
//            mask = indic_plan -> mask_array[HALF];
//            if (!indic_plan -> is_old_spec &&
//                    config.getBlwfMode() == BlwfMode.BLWF_MODE_PRE_AND_POST)
//                mask |= indic_plan -> mask_array[BLWF];
//            for (int i = start; i<base; i++)
//            info[i].mask |= mask;
//            /* Base */
//            mask = 0;
//            if (base < end)
//                info[base].mask |= mask;
//            /* Post-base */
//            mask = indic_plan -> mask_array[BLWF] | indic_plan -> mask_array[ABVF] | indic_plan -> mask_array[PSTF];
//            for (int i = base + 1; i < end; i++)
//                info[i].mask |= mask;
//        }

        // TODO
//        if (indic_plan->is_old_spec &&
//                buffer->props.script == HB_SCRIPT_DEVANAGARI)
//        {
//            /* Old-spec eye-lash Ra needs special handling.  From the
//             * spec:
//             *
//             * "The feature 'below-base form' is applied to consonants
//             * having below-base forms and following the base consonant.
//             * The exception is vattu, which may appear below half forms
//             * as well as below the base glyph. The feature 'below-base
//             * form' will be applied to all such occurrences of Ra as well."
//             *
//             * Test case: U+0924,U+094D,U+0930,U+094d,U+0915
//             * with Sanskrit 2003 font.
//             *
//             * However, note that Ra,Halant,ZWJ is the correct way to
//             * request eyelash form of Ra, so we wouldbn't inhibit it
//             * in that sequence.
//             *
//             * Test case: U+0924,U+094D,U+0930,U+094d,U+200D,U+0915
//             */
//            for (int i = start; i + 1 < base; i++)
//                if (cluster.classes.charAt(i) == IndicCategory.OT_Ra &&
//                        cluster.classes.charAt(i + 1) == IndicCategory.OT_H &&
//                        (i + 2 == base ||
//                                cluster.classes.charAt(i + 2) != IndicCategory.OT_ZWJ)) {
//                    info[i].mask |= indic_plan -> mask_array[BLWF];
//                    info[i + 1].mask |= indic_plan -> mask_array[BLWF];
//                }
//        }

        // TODO
//        int pref_len = config.getPrefLen();
//        if (indic_plan -> mask_array[PREF] && base + pref_len < end) {
//            assert (1 <= pref_len && pref_len <= 2);
//            /* Find a Halant,Ra sequence and mark it for pre-base reordering processing. */
//            for (int i = base + 1; i + pref_len - 1 < end; i++) {
//                hb_codepoint_t glyphs[ 2];
//                for (int j = 0; j < pref_len; j++)
//                    glyphs[j] = info[i + j].codepoint;
//                if (indic_plan -> pref.would_substitute(glyphs, pref_len, face)) {
//                    for (int j = 0; j < pref_len; j++)
//                        info[i++].mask |= indic_plan -> mask_array[PREF];
//
//                        /* Mark the subsequent stuff with 'cfar'.  Used in Khmer.
//                         * Read the feature spec.
//                         * This allows distinguishing the following cases with MS Khmer fonts:
//                         * U+1784,U+17D2,U+179A,U+17D2,U+1782
//                         * U+1784,U+17D2,U+1782,U+17D2,U+179A
//                         */
//                    if (indic_plan -> mask_array[CFAR])
//                        for (; i < end; i++)
//                            info[i].mask |= indic_plan -> mask_array[CFAR];
//
//                    break;
//                }
//            }
//        }

        /* Apply ZWJ/ZWNJ effects */
        // TODO
//        for (int i = start + 1; i < end; i++)
//            if (isJoiner(cluster.glyphs.get(i), cluster.classes.charAt(i))) {
//                boolean non_joiner = cluster.classes.charAt(i) == IndicCategory.OT_ZWNJ;
//                int j = i;
//
//                do {
//                    j--;
//
//                /* ZWJ/ZWNJ should disable CJCT.  They do that by simply
//                 * being there, since we don't skip them for the CJCT
//                 * feature (ie. F_MANUAL_ZWJ) */
//
//                /* A ZWNJ disables HALF. */
//                    if (non_joiner)
//                        info[j].mask &= ~indic_plan -> mask_array[HALF];
//
//                } while (j > start && !isConsonant(cluster.glyphs.get(j), cluster.classes.charAt(j)));
//            }
    }

    private boolean wouldSubstitute(List<OpenTableLookup> feature, List<Glyph> glyphs, int glyphCount) {
        // TODO
        return false;
    }

    private static long flag(int category) {
        return 1L << category;
    }

    private static final long MEDIAL_FLAGS = flag(IndicCategory.OT_CM) | flag(IndicCategory.OT_CM2);
    /* Note:
     *
     * We treat Vowels and placeholders as if they were consonants.  This is safe because Vowels
     * cannot happen in a consonant syllable.  The plus side however is, we can call the
     * consonant syllable logic from the vowel syllable function and get it all right! */
    private static final long CONSONANT_FLAGS = flag(IndicCategory.OT_C) | flag(IndicCategory.OT_Ra) | MEDIAL_FLAGS |
            flag(IndicCategory.OT_V) | flag(IndicCategory.OT_PLACEHOLDER) | flag(IndicCategory.OT_DOTTEDCIRCLE);
    private static final long JOINER_FLAGS = flag(IndicCategory.OT_ZWJ) | flag(IndicCategory.OT_ZWNJ);
    private static final long HALANT_OR_COENG_FLAGS = flag(IndicCategory.OT_H) | flag(IndicCategory.OT_Coeng);

    private boolean isConsonant(Glyph glyph, char category) {
        return isOneOf(category, CONSONANT_FLAGS);
    }

    /* XXX
 * This is a hack for now.  We should move this data into the main Indic table.
 * Or completely remove it and just check in the tables.
 */
    static private final int[] raChars = {
            0x0930, /* Devanagari */
            0x09B0, /* Bengali */
            0x09F0, /* Bengali */
            0x0A30, /* Gurmukhi */	/* No Reph */
            0x0AB0, /* Gujarati */
            0x0B30, /* Oriya */
            0x0BB0, /* Tamil */		/* No Reph */
            0x0C30, /* Telugu */		/* Reph formed only with ZWJ */
            0x0CB0, /* Kannada */
            0x0D30, /* Malayalam */	/* No Reph, Logical Repha */

            0x0DBB, /* Sinhala */		/* Reph formed only with ZWJ */

            0x179A, /* Khmer */		/* No Reph, Visual Repha */
    };

    private boolean isRa(int u) {
        for (int i = 0; i < raChars.length; i++)
        if (u == raChars[i])
            return true;
        return false;
    }

    private boolean isJoiner(Glyph glyph, char category) {
        return isOneOf(category, JOINER_FLAGS);
    }

    private boolean isOneOf(char category, long flags) {
        return ((1L << (int)category) & flags) != 0;
    }

    private boolean isLigated(Glyph glyph, char category) {
        // TODO
        return glyph.chars.length() > 1;
    }

    public boolean applyLigaFeature(GlyphLine glyphLine, boolean scriptSpecific) {
        if (gsubTable != null) {
            List<FeatureRecord> ligaFeatures = new ArrayList<>();

            if (scriptSpecific) {
                LanguageRecord languageRecord = getLanguageRecord();
                if (languageRecord == null) {
                    return false;
                }
                for (int featureIndex : languageRecord.features) {
                    FeatureRecord feature = gsubTable.getFeatureRecords().get(featureIndex);
                    if (feature.tag.equals("liga")) {
                        ligaFeatures.add(feature);
                    }
                }
            } else {
                for (FeatureRecord featureRecord : gsubTable.getFeatureRecords()) {
                    if (featureRecord.tag.equals("liga")) {
                        ligaFeatures.add(featureRecord);
                    }
                }
            }

            if (ligaFeatures.size() > 0) {
                boolean transformed = false;
                if (glyphLine != null) {
                    List<OpenTableLookup> lookups = gsubTable.getLookups(ligaFeatures.toArray(new FeatureRecord[ligaFeatures.size()]));
                    for (OpenTableLookup lookup : lookups) {
                        if (lookup != null && lookup.transformLine(glyphLine)) {
                            transformed = true;
                        }
                        glyphLine.idx = 0;
                    }
                }
                return transformed;
            }
        }
        return false;
    }

    @Override
    public GlyphLine createGlyphLine(String text) {
        ArrayList<Glyph> glyphs = new ArrayList<>(text.length());
        if (isFontSpecific()) {
            byte[] bytes = PdfEncodings.convertToBytes(text, "symboltt");
            for (byte b : bytes) {
                glyphs.add(getMetrics(b & 0xff));
            }
        } else {
            for (int k = 0; k < text.length(); ++k) {
                int val;
                if (Utilities.isSurrogatePair(text, k)) {
                    val = Utilities.convertToUtf32(text, k);
                    k++;
                } else {
                    val = text.charAt(k);
                }
                glyphs.add(getMetrics(val));
            }
        }
        return new GlyphLine(glyphs);
    }

    public GlyphLine createGlyphLine(char[] glyphs, Integer length) {
        ArrayList<Glyph> glyphLine = new ArrayList<>(length);
        for (int k = 0; k < length; k++) {
            int index = glyphs[k];
            Glyph glyph = getGlyphByCode(index);
            if (glyph == null) {
                glyph = getGlyphByCode(0);
            }
            glyphLine.add(glyph);
        }
        return new GlyphLine(glyphLine);
    }

    @Override
    public boolean hasKernPairs() {
        return kerning.size() > 0;
    }

    /**
     * Gets the kerning between two glyphs.
     *
     * @param first the first glyph
     * @param second the second glyph
     * @return the kerning to be applied
     */
    @Override
    public int getKerning(Glyph first, Glyph second) {
        if (first == null || second == null) {
            return 0;
        }
        return kerning.get((first.index << 16) + second.index);
    }

    /**
     * Gets the glyph index and metrics for a character.
     *
     * @param charCode the character code
     * @return an {@code int} array with {glyph index, width}
     */
    //TODO remove this method. Generation of notdef glyphs should be done by PdfFont
    public Glyph getMetrics(int charCode) {
        Glyph result = unicodeToGlyph.get(charCode);
        // special use case for not found glyphs
        // in this case we should use .notdef glyph, but correct unicode value.
        if (result == null) {
            result = new Glyph(codeToGlyph.get(0), charCode);
            unicodeToGlyph.put(charCode, result);
        }
        return result;
    }

    public boolean isCff() {
        return fontParser.isCff();
    }

    public HashMap<Integer, int[]> getActiveCmap() {
        OpenTypeParser.CmapTable cmaps = fontParser.getCmapTable();
        if (cmaps.cmapExt != null) {
            return cmaps.cmapExt;
        } else if (!cmaps.fontSpecific && cmaps.cmap31 != null) {
            return cmaps.cmap31;
        } else if (cmaps.fontSpecific && cmaps.cmap10 != null) {
            return cmaps.cmap10;
        } else if (cmaps.cmap31 != null) {
            return cmaps.cmap31;
        } else {
            return cmaps.cmap10;
        }
    }

    public byte[] getFontStreamBytes() {
        if (fontStreamBytes != null)
            return fontStreamBytes;
        try {
            if (fontParser.isCff()) {
                fontStreamBytes = fontParser.readCffFont();
            } else {
                fontStreamBytes = fontParser.getFullFont();
            }
        } catch (IOException e) {
            fontStreamBytes = null;
            throw new PdfException(PdfException.IoException, e);
        }
        return fontStreamBytes;
    }

    @Override
    public int getPdfFontFlags() {
        int flags = 0;
        if (fontMetrics.isFixedPitch()) {
            flags |= 1;
        }
        flags |= isFontSpecific() ? 4 : 32;
        if (fontNames.isItalic()) {
            flags |= 64;
        }
        if (fontNames.isBold() || fontNames.getFontWeight() > 500) {
            flags |= 262144;
        }
        return flags;
    }

    public boolean isFontSpecific() {
        return isSymbol;
    }

    /**
     * The offset from the start of the file to the table directory.
     * It is 0 for TTF and may vary for TTC depending on the chosen font.
     */
    public int getDirectoryOffset() {
        return fontParser.directoryOffset;
    }

    public byte[] getSubset(Set<Integer> glyphs, boolean subset) {
        try {
            return fontParser.getSubset(glyphs, subset);
        } catch (IOException e) {
            throw new PdfException(PdfException.IoException, e);
        }
    }

    protected void readGdefTable() throws IOException {
        int[] gdef = fontParser.tables.get("GDEF");
        if (gdef != null) {
            gdefTable = new OpenTypeGdefTableReader(fontParser.raf, gdef[0]);
        } else {
            gdefTable = new OpenTypeGdefTableReader(fontParser.raf, 0);
        }
    }

    protected void readGsubTable() throws IOException {
        int[] gsub = fontParser.tables.get("GSUB");
        if (gsub != null) {
            gsubTable = new GlyphSubstitutionTableReader(fontParser.raf, gsub[0], gdefTable, codeToGlyph);
        }
    }

    private void initializeFontProperties() throws IOException {

        // initialize sfnt tables
        OpenTypeParser.HeaderTable head = fontParser.getHeadTable();
        OpenTypeParser.HorizontalHeader hhea = fontParser.getHheaTable();
        OpenTypeParser.WindowsMetrics os_2 = fontParser.getOs_2Table();
        OpenTypeParser.PostTable post = fontParser.getPostTable();
        isSymbol = fontParser.getCmapTable().fontSpecific;
        kerning = fontParser.readKerning(head.unitsPerEm);
        bBoxes = fontParser.readBbox(head.unitsPerEm);

        // font names group
        fontNames.setAllNames(fontParser.getAllNameEntries());
        fontNames.setFontName(fontParser.getPsFontName());
        fontNames.setFullName(fontNames.getNames(4));
        String[][] otfFamilyName = fontNames.getNames(16);
        if (otfFamilyName != null) {
            fontNames.setFamilyName(otfFamilyName);
        } else {
            fontNames.setFamilyName(fontNames.getNames(1));
        }
        String[][] subfamily = fontNames.getNames(2);
        if (subfamily != null) {
            fontNames.setStyle(subfamily[0][3]);
        }
        String[][] otfSubFamily = fontNames.getNames(17);
        if (otfFamilyName != null) {
            fontNames.setSubfamily(otfSubFamily);
        } else {
            fontNames.setSubfamily(subfamily);
        }
        String[][] cidName = fontNames.getNames(20);
        if (cidName != null) {
            fontNames.setCidFontName(cidName[0][3]);
        }
        fontNames.setWeight(os_2.usWeightClass);
        fontNames.setWidth(os_2.usWidthClass);
        fontNames.setMacStyle(head.macStyle);
        fontNames.setAllowEmbedding(os_2.fsType != 2);

        // font metrics group
        fontMetrics.setUnitsPerEm(head.unitsPerEm);
        fontMetrics.updateBbox(head.xMin, head.yMin, head.xMax, head.yMax);
        fontMetrics.setMaxGlyphId(fontParser.readMaxGlyphId());
        fontMetrics.setGlyphWidths(fontParser.getGlyphWidthsByIndex());
        fontMetrics.setTypoAscender(os_2.sTypoAscender);
        fontMetrics.setTypoDescender(os_2.sTypoDescender);
        fontMetrics.setCapHeight(os_2.sCapHeight);
        fontMetrics.setXHeight(os_2.sxHeight);
        fontMetrics.setItalicAngle(post.italicAngle);
        fontMetrics.setAscender(hhea.Ascender);
        fontMetrics.setDescender(hhea.Descender);
        fontMetrics.setLineGap(hhea.LineGap);
        fontMetrics.setWinAscender(os_2.usWinAscent);
        fontMetrics.setWinDescender(os_2.usWinDescent);
        fontMetrics.setAdvanceWidthMax(hhea.advanceWidthMax);
        fontMetrics.setUnderlinePosition((post.underlinePosition - post.underlineThickness) / 2);
        fontMetrics.setUnderlineThickness(post.underlineThickness);
        fontMetrics.setStrikeoutPosition(os_2.yStrikeoutPosition);
        fontMetrics.setStrikeoutSize(os_2.yStrikeoutSize);
        fontMetrics.setSubscriptOffset(-os_2.ySubscriptYOffset);
        fontMetrics.setSubscriptSize(os_2.ySubscriptYSize);
        fontMetrics.setSuperscriptOffset(os_2.ySuperscriptYOffset);
        fontMetrics.setSuperscriptSize(os_2.ySuperscriptYSize);
        fontMetrics.setIsFixedPitch(post.isFixedPitch);

        // font identification group
        String[][] ttfVersion = fontNames.getNames(5);
        if (ttfVersion != null) {
            fontIdentification.setTtfVersion(ttfVersion[0][3]);
        }
        String[][] ttfUniqueId = fontNames.getNames(3);
        if (ttfUniqueId != null) {
            fontIdentification.setTtfVersion(ttfUniqueId[0][3]);
        }
        fontIdentification.setPanose(os_2.panose);

        HashMap<Integer, int[]> cmap = getActiveCmap();
        int[] glyphWidths = fontParser.getGlyphWidthsByIndex();
        unicodeToGlyph = new LinkedHashMap<>(cmap.size());
        codeToGlyph = new LinkedHashMap<>(glyphWidths.length);
        for (Integer charCode : cmap.keySet()) {
            int index = cmap.get(charCode)[0];
            if (index >= glyphWidths.length) {
                Logger LOGGER = LoggerFactory.getLogger(TrueTypeFont.class);
                LOGGER.warn(MessageFormat.format(LogMessageConstant.FONT_HAS_INVALID_GLYPH, getFontNames().getFontName(), index));
                continue;
            }
            Glyph glyph = new Glyph(index, glyphWidths[index], charCode, bBoxes != null ? bBoxes[index] : null);
            unicodeToGlyph.put(charCode, glyph);
            codeToGlyph.put(index, glyph);
        }

        for (int index = 0; index < glyphWidths.length; index++) {
            if (codeToGlyph.containsKey(index)) {
                continue;
            }
            Glyph glyph = new Glyph(index, glyphWidths[index], null);
            codeToGlyph.put(index, glyph);
        }

        readGdefTable();
        readGsubTable();

        isVertical = false;
    }

    private List<Integer> splitArabicGlyphLineIntoWords(GlyphLine glyphLine, List<OpenTableLookup> medi, List<OpenTableLookup> fina) {
        List<Integer> words = new ArrayList<>(glyphLine.glyphs.size());
        boolean started = false;
        for (int i = 0; i < glyphLine.glyphs.size(); i++) {
            Glyph medialForm = transform(medi, glyphLine.glyphs.get(i));
            Glyph finalForm = transform(fina, glyphLine.glyphs.get(i));
            if (medialForm == null || (finalForm != null && medialForm.index == finalForm.index)) {
                if (started) {
                    // if the glyph has no fina form, it is not an arabic glyph
                    words.add(finalForm != null ? i + 1 : i);
                    started = false;
                }
            } else {
                if (!started) {
                    words.add(i);
                    started = true;
                }
            }
        }
        if (words.size() % 2 != 0) {
            words.add(glyphLine.glyphs.size());
        }
        return words;
    }

    private boolean applyInitMediFinaShaping(GlyphLine glyphLine, List<OpenTableLookup> init, List<OpenTableLookup> medi, List<OpenTableLookup> fina) {
        if (init == null || medi == null || fina == null) {
            return false;
        }
        boolean transformed = false;
        List<Integer> words = splitArabicGlyphLineIntoWords(glyphLine, medi, fina);
        for (OpenTableLookup lookup : init) {
            if (lookup != null) {
                for (int i = 0; i < words.size(); i += 2) {
                    if (words.get(i) + 1 != words.get(i + 1)) {
                        glyphLine.idx = words.get(i);
                        if (lookup.transformOne(glyphLine)) {
                            transformed = true;
                        }
                    }
                }
            }
        }
        for (OpenTableLookup lookup : medi) {
            if (lookup != null) {
                for (int i = 0; i < words.size(); i += 2) {
                    for (int k = words.get(i) + 1; k < words.get(i + 1) - 1; k++) {
                        glyphLine.idx = k;
                        if (lookup.transformOne(glyphLine)) {
                            transformed = true;
                        }
                    }
                }
            }
        }
        for (OpenTableLookup lookup : fina) {
            if (lookup != null) {
                for (int i = 0; i < words.size(); i += 2) {
                    if (words.get(i) + 1 != words.get(i + 1)) {
                        glyphLine.idx = words.get(i + 1) - 1;
                        if (lookup.transformOne(glyphLine)) {
                            transformed = true;
                        }
                    }
                }
            }
        }
        return transformed;
    }

    private boolean applyRligFeature(GlyphLine glyphLine, List<OpenTableLookup> rlig) {
        boolean transformed = false;
        if (glyphLine != null && rlig != null) {
            for (OpenTableLookup lookup : rlig) {
                if (lookup != null && lookup.transformLine(glyphLine)) {
                    transformed = true;
                }
            }
        }
        return transformed;
    }

    private Glyph transform(List<OpenTableLookup> feature, Glyph glyph) {
        if (feature != null) {
            for (OpenTableLookup lookup : feature) {
                if (lookup != null) {
                    if (lookup.hasSubstitution(glyph.index)) {
                        GlyphLine gl = new GlyphLine();
                        gl.start = 0;
                        gl.end = 1;
                        gl.idx = 0;
                        gl.glyphs = Arrays.asList(glyph);
                        lookup.transformOne(gl);
                        return gl.glyphs.get(0);
                    }
                }
            }
        }
        return null;
    }

    private boolean transform(List<OpenTableLookup> feature, GlyphLine glyphLine) {
        boolean transformed = false;
        if (feature != null) {
            for (OpenTableLookup lookup : feature) {
                if (lookup != null) {
                    if (lookup.transformLine(glyphLine)) {
                        transformed = true;
                    }
                }
            }
        }
        return transformed;
    }

    private LanguageRecord getLanguageRecord() {
        LanguageRecord languageRecord = null;
        if (otfScriptTag != null) {
            for (ScriptRecord record : gsubTable.getScriptRecords()) {
                if (otfScriptTag.equals(record.tag)) {
                    languageRecord = record.defaultLanguage;
                    break;
                }
            }
        }
        return languageRecord;
    }

    private List<DevanagariCluster> splitDevanagariGlyphLineIntoClusters(GlyphLine glyphLine) {
        StringBuilder sb = new StringBuilder();
        for (int i = glyphLine.start; i < glyphLine.end; i++) {
            Glyph glyph = glyphLine.glyphs.get(i);
            sb.append(glyph.unicode == null ? 'X' : IndicCategory.getDevanagariClassChar(glyph.unicode));
        }
        String classes = sb.toString();

        Pattern pattern = Pattern.compile(IndicCategory.getSyllableRegex());
        Matcher matcher = pattern.matcher("X" + classes);

        List<DevanagariCluster> clusters = new ArrayList<>();

        while (matcher.find()) {
            StringBuilder currentClasses = new StringBuilder();
            for (int i = matcher.start() - 1; i < matcher.end() - 1; i++) {
                currentClasses.append((char)IndicCategory.getDevanagariClass(glyphLine.glyphs.get(glyphLine.start + i).unicode));
            }
            clusters.add(new DevanagariCluster(glyphLine, glyphLine.start + matcher.start() - 1, glyphLine.start + matcher.end() - 1, currentClasses.toString()));
        }

        // TODO sorting and eliminating intersections?

        return clusters;
    }


}
