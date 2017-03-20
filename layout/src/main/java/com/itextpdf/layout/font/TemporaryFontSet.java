package com.itextpdf.layout.font;

import com.itextpdf.io.font.FontProgram;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TemporaryFontSet {

    private final FontSet mainFontSet;
    private int mainFontSetSize = -1;
    // Due to new logic HashSet can be used instead of List.
    // But FontInfo with or without alias will be the same FontInfo.
    private final Set<FontInfo> fonts = new HashSet<>();
    private final Map<FontInfo, FontProgram> fontPrograms = new HashMap<>();
    private final Map<FontSelectorKey, FontSelector> fontSelectorCache = new HashMap<>();

    /**
     * Create new instance.
     * @param mainFontSet base font set, can be {@code null}.
     */
    public TemporaryFontSet(FontSet mainFontSet) {
        this.mainFontSet = mainFontSet;
        updateMainFontSetSize();
    }

    /**
     * Clone existing fontInfo with alias and add to the {@link FontSet}.
     * Note, font selector will match either original font names and alias.
     *
     * @param fontInfo already created {@link FontInfo}.
     * @param alias    font alias, shall not be null.
     * @return just created {@link FontInfo} on success, otherwise null.
     */
    public FontInfo add(FontInfo fontInfo, String alias) {
        if (alias == null) {
            return null;
        } else {
            return add(FontInfo.create(fontInfo, alias));
        }
    }

    /**
     * Add not supported for auto creating FontPrograms.
     *
     * @param fontProgram {@link FontProgram}
     * @param encoding    FontEncoding for creating {@link com.itextpdf.kernel.font.PdfFont}.
     * @param alias    font alias.
     * @return just created {@link FontInfo} on success, otherwise null.
     */
    public FontInfo add(FontProgram fontProgram, String encoding, String alias) {
        if (fontProgram == null) {
            return null;
        }
        FontInfo fontInfo = add(FontInfo.create(fontProgram, encoding, alias));
        fontPrograms.put(fontInfo, fontProgram);
        return fontInfo;
    }

    /**
     * Set of available fonts.
     * Note, the set is unmodifiable.
     */
    public Collection<FontInfo> getFonts() {
        //TODO create custom unmodifiable collection!
        List<FontInfo> allFonts = new LinkedList<>(mainFontSet.getFonts());
        allFonts.addAll(fonts);
        return Collections.<FontInfo>unmodifiableCollection(allFonts);
    }

    FontProgram getFontProgram(FontInfo fontInfo) {
        FontProgram fontProgram = mainFontSet.getFontProgram(fontInfo);
        if (fontProgram == null) {
            fontProgram = fontPrograms.get(fontInfo);
        }
        return fontProgram;
    }

    FontSelector getCachedFontSelector(FontSelectorKey fontSelectorKey) {
        // FontSelector shall not be get from mainFontSet.
        if (updateMainFontSetSize()) {
            // Cache shall be cleared due to updated font collection.
            fontSelectorCache.clear();
            return null;
        } else {
            return fontSelectorCache.get(fontSelectorKey);
        }
    }

    void putCachedFontSelector(FontSelectorKey fontSelectorKey, FontSelector fontSelector) {
        fontSelectorCache.put(fontSelectorKey, fontSelector);
    }

    private FontInfo add(FontInfo fontInfo) {
        if (fontInfo != null) {
            fonts.add(fontInfo);
            updateMainFontSetSize();
            fontSelectorCache.clear();
        }
        return fontInfo;
    }

    private boolean updateMainFontSetSize() {
        if (this.mainFontSet != null && mainFontSetSize != this.mainFontSet.getFonts().size()) {
            mainFontSetSize = this.mainFontSet.getFonts().size();
            return true;
        }
        return false;
    }
}
