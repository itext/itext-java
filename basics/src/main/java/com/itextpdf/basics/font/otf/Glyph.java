/*
 * $Id$
 *
 * This file is part of the iText (R) project.
 * Copyright (c) 1998-2014 iText Group NV
 * Authors: Bruno Lowagie, Paulo Soares, et al.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License version 3
 * as published by the Free Software Foundation with the addition of the
 * following permission added to Section 15 as permitted in Section 7(a):
 * FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
 * ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
 * OF THIRD PARTY RIGHTS
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License
 * along with this program; if not, see http://www.gnu.org/licenses or write to
 * the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
 * Boston, MA, 02110-1301 USA, or download the license from the following URL:
 * http://itextpdf.com/terms-of-use/
 *
 * The interactive user interfaces in modified source and object code versions
 * of this program must display Appropriate Legal Notices, as required under
 * Section 5 of the GNU Affero General Public License.
 *
 * In accordance with Section 7(b) of the GNU Affero General Public License,
 * a covered work must retain the producer line in every PDF that is created
 * or manipulated using iText.
 *
 * You can be released from the requirements of the license by purchasing
 * a commercial license. Buying such a license is mandatory as soon as you
 * develop commercial activities involving the iText software without
 * disclosing the source code of your own applications.
 * These activities include: offering paid services to customers as an ASP,
 * serving PDFs on the fly in a web application, shipping iText with a closed
 * source product.
 *
 * For more information, please contact iText Software Corp. at this
 * address: sales@itextpdf.com
 */
package com.itextpdf.basics.font.otf;

/**
 *  
 * @author <a href="mailto:paawak@gmail.com">Palash Ray</a>
 */
public class Glyph {
    
    /**
     * The <i>code</i> or <i>id</i> by which this is represented in the Font File
     */
    public final int code;
    
    /**
     * The normalized width of this Glyph.
     */
    public final int width;
    
    /**
     * The Unicode text represented by this Glyph
     */
    public final String chars;
    
    public final int XPlacement;
    public final int YPlacement;
    public final int XAdvance;
    public final int YAdvance;
    public final GposAnchorEntryExit entryExit;
    public final boolean IsMark;
    public final int relativeAnchorIndex;

    public Glyph(int code, int width, String chars, boolean IsMark) {
        this.code = code;
        this.width = width;
        this.chars = chars;
        this.XPlacement = 0;
        this.YPlacement = 0;
        this.XAdvance = 0;
        this.YAdvance = 0;
        this.entryExit = null;
        this.IsMark = IsMark;
        this.relativeAnchorIndex = 0;
    }

    public Glyph(Glyph glyph, int XPlacement, int YPlacement, int XAdvance, int YAdvance, int relativeAnchorIndex) {
        this.code = glyph.code;
        this.width = glyph.width;
        this.chars = glyph.chars;
        this.XPlacement = glyph.XPlacement + XPlacement;
        this.YPlacement = glyph.YPlacement + YPlacement;
        this.XAdvance = glyph.XAdvance + XAdvance;
        this.YAdvance = glyph.YAdvance + YAdvance;
        this.entryExit = glyph.entryExit;
        this.IsMark = glyph.IsMark;
        if (relativeAnchorIndex != 0)
            this.relativeAnchorIndex = relativeAnchorIndex;
        else
            this.relativeAnchorIndex = glyph.relativeAnchorIndex;
    }
    
    public Glyph(Glyph glyph, GposAnchorEntryExit entryExit) {
        this.code = glyph.code;
        this.width = glyph.width;
        this.chars = glyph.chars;
        this.XPlacement = glyph.XPlacement;
        this.YPlacement = glyph.YPlacement;
        this.XAdvance = glyph.XAdvance;
        this.YAdvance = glyph.YAdvance;
        this.entryExit = entryExit;
        this.IsMark = glyph.IsMark;
        this.relativeAnchorIndex = glyph.relativeAnchorIndex;
    }
   
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((chars == null) ? 0 : chars.hashCode());
        result = prime * result + code;
        result = prime * result + width;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Glyph other = (Glyph) obj;
        if (chars == null) {
            if (other.chars != null) {
                return false;
            }
        } else if (!chars.equals(other.chars)) {
            return false;
        }
        if (code != other.code) {
            return false;
        }
        if (width != other.width) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return Glyph.class.getSimpleName() + " [id=" + code + ", width=" + width + ", chars=" + chars + "]";
    }
    
}
