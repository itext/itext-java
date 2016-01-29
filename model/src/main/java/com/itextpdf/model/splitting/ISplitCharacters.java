package com.itextpdf.model.splitting;

import com.itextpdf.io.font.otf.GlyphLine;

/**
 * Interface for customizing the split character.
 */
public interface ISplitCharacters {

    /**
     * Returns <CODE>true</CODE> if the character can split a line. The splitting implementation
     * is free to look ahead or look behind characters to make a decision.
     * @param glyphPos the position of {@see Glyph} in the {@see GlyphLine}
     * @param text an array of unicode char codes which represent current text
     */
    boolean isSplitCharacter(GlyphLine text, int glyphPos);

}
