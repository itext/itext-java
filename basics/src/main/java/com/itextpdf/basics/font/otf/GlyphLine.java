/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.itextpdf.basics.font.otf;

import java.util.List;

/**
 *
 * @author admin
 */
public class GlyphLine {
    public List<Glyph> glyphs;
    public int start;
    //TODO end or length?
    public int end;
    public int idx;
}
