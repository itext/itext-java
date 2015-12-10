/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.itextpdf.basics.font.otf;

import com.itextpdf.basics.io.RandomAccessFileOrArray;

import java.io.IOException;

/**
 *
 * @author admin
 */
public class OpenTypeGdefTableReader {

    private final int GLYPH_SKIP_BASE = 1;
    private final int GLYPH_SKIP_MARK = 2;
    private final int GLYPH_SKIP_LIGATURE = 3;
    private final int FLAG_IGNORE_BASE = 2;
    private final int FLAG_IGNORE_LIGATURE = 4;
    private final int FLAG_IGNORE_MARK = 8;
    
    private final int tableLocation;
    private final RandomAccessFileOrArray rf;
    private OtfClass glyphClass;
    private OtfClass markAttachmentClass;
    
    public OpenTypeGdefTableReader(RandomAccessFileOrArray rf, int tableLocation) {
        this.rf = rf;
        this.tableLocation = tableLocation;
    }
    
    public void readTable() throws IOException {
        if (tableLocation > 0) {
            rf.seek(tableLocation);
            rf.readUnsignedInt(); //version, we only support 0x00010000
            int glyphClassDefOffset = rf.readUnsignedShort();
            rf.readUnsignedShort(); //skip Attachment Point List Table
            rf.readUnsignedShort(); //skip Ligature Caret List Table
            int markAttachClassDefOffset = rf.readUnsignedShort();
            if (glyphClassDefOffset > 0) {
                glyphClass = new OtfClass(rf, glyphClassDefOffset + tableLocation);
            }
            if (markAttachClassDefOffset > 0) {
                markAttachmentClass = new OtfClass(rf, markAttachClassDefOffset + tableLocation);
            }
        }
    }
    
    public boolean isSkip(int glyph, int flag) {
        if (glyphClass != null && (flag & (FLAG_IGNORE_BASE | FLAG_IGNORE_LIGATURE | FLAG_IGNORE_MARK)) != 0) {
            int cla = glyphClass.getOtfClass(glyph);
            if (cla == GLYPH_SKIP_BASE && (flag & FLAG_IGNORE_BASE) != 0) {
                return true;
            }
            if (cla == GLYPH_SKIP_MARK && (flag & FLAG_IGNORE_MARK) != 0) {
                return true;
            }
            if (cla == GLYPH_SKIP_LIGATURE && (flag & FLAG_IGNORE_LIGATURE) != 0) {
                return true;
            }
        }
        if (markAttachmentClass != null && (flag >> 8) > 0) {
            return markAttachmentClass.getOtfClass(glyph) != (flag >> 8);
        }
        return false;
    }
}
