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
package com.itextpdf.kernel.geom;

import java.io.Serializable;

public class PageSize extends Rectangle implements Serializable {

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

    public static PageSize Default = A4;

    public PageSize(float width, float height) {
        super(0, 0, width, height);
    }


    public PageSize(Rectangle box) {
        super(box.getX(), box.getY(), box.getWidth(), box.getHeight());
    }

    /**
     * Rotates PageSize clockwise.
     */
    public PageSize rotate() {
        return new PageSize(height, width);
    }

    @Override
    public Rectangle clone() {
        return new PageSize(this);
    }
}
