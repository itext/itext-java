package com.itextpdf.model.splitting;

/**
 * Interface for customizing the split character.
 */
public interface ISplitCharacters {

    /**
     * Returns <CODE>true</CODE> if the character can split a line. The splitting implementation
     * is free to look ahead or look behind characters to make a decision.
     *
     * @param charCode unicode character code of the candidate to become a split character
     * @param text an array of unicode char codes which represent current text
     * @param charTextPos the position of {@see charCode} in the {@see text}
     */
    boolean isSplitCharacter(int charCode, int[] text, int charTextPos);

}
