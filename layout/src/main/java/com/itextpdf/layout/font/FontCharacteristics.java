/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
    Authors: Bruno Lowagie, Paulo Soares, et al.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.

    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.

    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
 */
package com.itextpdf.layout.font;

public final class FontCharacteristics {

    private boolean isItalic = false;
    private boolean isBold = false;
    private short fontWeight = 400;
    private boolean undefined = true;
    private boolean isMonospace = false;

    public FontCharacteristics() {
    }

    public FontCharacteristics(FontCharacteristics other) {
        this();
        this.isItalic = other.isItalic;
        this.isBold = other.isBold;
        this.fontWeight = other.fontWeight;
        this.undefined = other.undefined;
    }

    /**
     * Sets preferred font weight
     *
     * @param fw font weight in css notation.
     * @see com.itextpdf.io.font.constants.FontWeights
     * @return this instance.
     */
    public FontCharacteristics setFontWeight(short fw) {
        if (fw > 0) {
            this.fontWeight = FontCharacteristicsUtils.normalizeFontWeight(fw);
            modified();
        }
        return this;
    }

    public FontCharacteristics setFontWeight(String fw) {
        return setFontWeight(FontCharacteristicsUtils.parseFontWeight(fw));
    }

    public FontCharacteristics setBoldFlag(boolean isBold) {
        this.isBold = isBold;
        if (this.isBold) modified();
        return this;
    }

    public FontCharacteristics setItalicFlag(boolean isItalic) {
        this.isItalic = isItalic;
        if (this.isItalic) modified();
        return this;
    }

    public FontCharacteristics setMonospaceFlag(boolean isMonospace) {
        this.isMonospace = isMonospace;
        if (this.isMonospace) modified();
        return this;
    }

    /**
     * Set font style
     * @param fs shall be 'normal', 'italic' or 'oblique'.
     * @return this element
     */
    public FontCharacteristics setFontStyle(String fs) {
        if (fs != null && fs.length() > 0) {
            fs = fs.trim().toLowerCase();
            if (fs.equals("normal")) {
                isItalic = false;
            } else if (fs.equals("italic") || fs.equals("oblique")) {
                isItalic = true;
            }
        }
        if (isItalic) modified();
        return this;
    }

    public boolean isItalic() {
        return isItalic;
    }

    public boolean isBold() {
        return isBold || fontWeight > 500;
    }

    public boolean isMonospace() {
        return isMonospace;
    }

    public short getFontWeight() {
        return fontWeight;
    }

    public boolean isUndefined() {
        return undefined;
    }

    private void modified() {
        undefined = false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FontCharacteristics that = (FontCharacteristics) o;

        return isItalic == that.isItalic
                && isBold == that.isBold
                && fontWeight == that.fontWeight;
    }

    @Override
    public int hashCode() {
        int result = (isItalic ? 1 : 0);
        result = 31 * result + (isBold ? 1 : 0);
        result = 31 * result + (int) fontWeight;
        return result;
    }
}
