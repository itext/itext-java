package com.itextpdf.model.hyphenation;

/**
 * Interface for customizing the split character.
 */
public interface ISplitCharacters {

    /**
     * Returns <CODE>true</CODE> if the character can split a line. The splitting implementation
     * is free to look ahead or look behind characters to make a decision.
     */
    boolean isSplitCharacter(int charCode, String text, int charTextPos);

}
