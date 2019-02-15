/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
    Authors: iText Software.

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
package com.itextpdf.io.font.constants;

public final class FontStretches {

    private FontStretches() {
    }

    private static final int FWIDTH_ULTRA_CONDENSED = 1;
    private static final int FWIDTH_EXTRA_CONDENSED	= 2;
    private static final int FWIDTH_CONDENSED = 3;
    private static final int FWIDTH_SEMI_CONDENSED = 4;
    private static final int FWIDTH_NORMAL = 5;
    private static final int FWIDTH_SEMI_EXPANDED = 6;
    private static final int FWIDTH_EXPANDED = 7;
    private static final int FWIDTH_EXTRA_EXPANDED = 8;
    private static final int FWIDTH_ULTRA_EXPANDED = 9;


    public static final String ULTRA_CONDENSED = "UltraCondensed";

    public static final String EXTRA_CONDENSED = "ExtraCondensed";

    public static final String CONDENSED = "Condensed";

    public static final String SEMI_CONDENSED = "SemiCondensed";

    public static final String NORMAL = "Normal";

    public static final String SEMI_EXPANDED = "SemiExpanded";

    public static final String EXPANDED = "Expanded";

    public static final String EXTRA_EXPANDED = "ExtraExpanded";

    public static final String ULTRA_EXPANDED = "UltraExpanded";

    /**
     * Convert from Open Type font width class notation.
     * <p>
     * https://www.microsoft.com/typography/otspec/os2.htm#wdc
     *
     * @param fontWidth Open Type font width.
     * @return one of the {@link FontStretches} constants.
     */
    public static String fromOpenTypeWidthClass(int fontWidth) {
        String fontWidthValue = NORMAL;
        switch (fontWidth) {
            case FWIDTH_ULTRA_CONDENSED:
                fontWidthValue = ULTRA_CONDENSED;
                break;
            case FWIDTH_EXTRA_CONDENSED:
                fontWidthValue = EXTRA_CONDENSED;
                break;
            case FWIDTH_CONDENSED:
                fontWidthValue = CONDENSED;
                break;
            case FWIDTH_SEMI_CONDENSED:
                fontWidthValue = SEMI_CONDENSED;
                break;
            case FWIDTH_NORMAL:
                fontWidthValue = NORMAL;
                break;
            case FWIDTH_SEMI_EXPANDED:
                fontWidthValue = SEMI_EXPANDED;
                break;
            case FWIDTH_EXPANDED:
                fontWidthValue = EXPANDED;
                break;
            case FWIDTH_EXTRA_EXPANDED:
                fontWidthValue = EXTRA_EXPANDED;
                break;
            case FWIDTH_ULTRA_EXPANDED:
                fontWidthValue = ULTRA_EXPANDED;
                break;
        }
        return fontWidthValue;
    }
}
