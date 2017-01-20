/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2016 iText Group NV
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
package com.itextpdf.io.font;

public class FontProgramDescriptor {

    private String fontName;

    private String style = "";
    private int macStyle;
    private int weight = FontNames.FW_NORMAL;
    private float italicAngle = 0;
    private boolean isMonospace;

    private String fullNameLowerCase;
    private String fontNameLowerCase;
    private String familyNameLowerCase;

    FontProgramDescriptor(FontNames fontNames, float italicAngle, boolean isMonospace) {
        this.fontName = fontNames.getFontName();
        this.fontNameLowerCase = this.fontName.toLowerCase();
        this.fullNameLowerCase = fontNames.getFullName()[0][3].toLowerCase();
        this.familyNameLowerCase = fontNames.getFamilyName() != null && fontNames.getFamilyName()[0][3] != null ? fontNames.getFamilyName()[0][3].toLowerCase() : null;
        this.style = fontNames.getStyle();
        this.weight = fontNames.getFontWeight();
        this.macStyle = fontNames.getMacStyle();
        this.italicAngle = italicAngle;
        this.isMonospace = isMonospace;
    }

    FontProgramDescriptor(FontNames fontNames, FontMetrics fontMetrics) {
        this(fontNames, fontMetrics.getItalicAngle(), fontMetrics.isFixedPitch());
    }

    public String getFontName() {
        return fontName;
    }

    public String getStyle() {
        return style;
    }

    public int getFontWeight() {
        return weight;
    }

    public float getItalicAngle() {
        return italicAngle;
    }

    public boolean isMonospace() {
        return isMonospace;
    }

    public boolean isBold() {
        return (macStyle & FontNames.BOLD_FLAG) != 0;
    }

    public boolean isItalic() {
        return (macStyle & FontNames.ITALIC_FLAG) != 0;
    }

    public String getFullNameLowerCase() {
        return fullNameLowerCase;
    }

    public String getFontNameLowerCase() {
        return fontNameLowerCase;
    }

    public String getFamilyNameLowerCase() {
        return familyNameLowerCase;
    }

    void setItalicAngle(float italicAngle) {
        this.italicAngle = italicAngle;
    }

    void setMonospace(boolean monospace) {
        isMonospace = monospace;
    }
}
