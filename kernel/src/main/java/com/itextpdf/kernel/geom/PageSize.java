/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2020 iText Group NV
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
package com.itextpdf.kernel.geom;

import java.io.Serializable;

public class PageSize extends Rectangle implements Cloneable, Serializable {

    private static final long serialVersionUID = 485375591249386160L;

    public static PageSize A0 = new PageSize(2384, 3370);
    public static PageSize A1 = new PageSize(1684, 2384);
    public static PageSize A2 = new PageSize(1190, 1684);
    public static PageSize A3 = new PageSize(842, 1190);
    public static PageSize A4 = new PageSize(595, 842);
    public static PageSize A5 = new PageSize(420, 595);
    public static PageSize A6 = new PageSize(298, 420);
    public static PageSize A7 = new PageSize(210, 298);
    public static PageSize A8 = new PageSize(148, 210);
    public static PageSize A9 = new PageSize(105, 547);
    public static PageSize A10 = new PageSize(74, 105);

    public static PageSize B0 = new PageSize(2834, 4008);
    public static PageSize B1 = new PageSize(2004, 2834);
    public static PageSize B2 = new PageSize(1417, 2004);
    public static PageSize B3 = new PageSize(1000, 1417);
    public static PageSize B4 = new PageSize(708, 1000);
    public static PageSize B5 = new PageSize(498, 708);
    public static PageSize B6 = new PageSize(354, 498);
    public static PageSize B7 = new PageSize(249, 354);
    public static PageSize B8 = new PageSize(175, 249);
    public static PageSize B9 = new PageSize(124, 175);
    public static PageSize B10 = new PageSize(88, 124);

    public static PageSize LETTER = new PageSize(612, 792);
    public static PageSize LEGAL = new PageSize(612, 1008);
    public static PageSize TABLOID = new PageSize(792, 1224);
    public static PageSize LEDGER = new PageSize(1224, 792);
    public static PageSize EXECUTIVE = new PageSize(522, 756);

    public static final Rectangle NOTE = new PageSize(540, 720);
    public static final Rectangle POSTCARD = new PageSize(283, 416);
    public static final Rectangle ARCH_E = new PageSize(2592, 3456);
    public static final Rectangle ARCH_D = new PageSize(1728, 2592);
    public static final Rectangle ARCH_C = new PageSize(1296, 1728);
    public static final Rectangle ARCH_B = new PageSize(864, 1296);
    public static final Rectangle ARCH_A = new PageSize(648, 864);
    /** This is the American Foolscap format. */
    public static final Rectangle FLSA = new PageSize(612, 936);
    /** This is the European Foolscap format. */
    public static final Rectangle FLSE = new PageSize(648, 936);
    public static final Rectangle HALFLETTER = new PageSize(396, 612);
    public static final Rectangle _11X17 = new PageSize(792, 1224);
    /** This is the ISO 7810 ID-1 format (85.60 x 53.98 mm or 3.370 x 2.125 inch). */
    public static final Rectangle ID_1 = new PageSize(242.65f, 153);
    /** This is the ISO 7810 ID-2 format (A7 rotated). */
    public static final Rectangle ID_2 = new PageSize(297, 210);
    /** This is the ISO 7810 ID-3 format (B7 rotated). */
    public static final Rectangle ID_3 = new PageSize(354, 249);
    public static final Rectangle CROWN_QUARTO = new PageSize(535, 697);
    public static final Rectangle LARGE_CROWN_QUARTO = new PageSize(569, 731);
    public static final Rectangle DEMY_QUARTO = new PageSize(620, 782);
    public static final Rectangle ROYAL_QUARTO = new PageSize(671, 884);
    public static final Rectangle CROWN_OCTAVO = new PageSize(348, 527);
    public static final Rectangle LARGE_CROWN_OCTAVO = new PageSize(365, 561);
    public static final Rectangle DEMY_OCTAVO = new PageSize(391, 612);
    public static final Rectangle ROYAL_OCTAVO = new PageSize(442, 663);
    public static final Rectangle SMALL_PAPERBACK = new PageSize(314, 504);
    public static final Rectangle PENGUIN_SMALL_PAPERBACK = new PageSize(314, 513);
    public static final Rectangle PENGUIN_LARGE_PAPERBACK = new PageSize(365, 561);

    public static PageSize Default = A4;

    public PageSize(float width, float height) {
        super(0, 0, width, height);
    }


    public PageSize(Rectangle box) {
        super(box.getX(), box.getY(), box.getWidth(), box.getHeight());
    }

    /**
     * Rotates {@link PageSize} clockwise.
     *
     * @return the rotated {@link PageSize}.
     */
    public PageSize rotate() {
        return new PageSize(height, width);
    }

    /**
     * Creates a "deep copy" of this PageSize, meaning the object returned by this method will be independent
     * of the object being cloned.
     * Note that although the return type of this method is {@link Rectangle},
     * the actual type of the returned object is {@link PageSize}.
     *
     * @return the copied PageSize.
     */
    @Override
    public Rectangle clone() {
        // super.clone is safe to return since all of the PagSize's fields are primitive.
        return super.clone();
    }
}
