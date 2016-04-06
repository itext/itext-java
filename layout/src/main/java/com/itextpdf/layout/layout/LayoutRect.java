/*
    $Id$

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
package com.itextpdf.layout.layout;


public class LayoutRect implements Cloneable {

    protected Float x;
    protected Float y;
    protected Float width;
    protected Float height;

    public LayoutRect(Float x, Float y, Float width, Float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    /**
     * Calculates the common rectangle which includes all the input rectangles.
     * @param rectangles list of input rectangles.
     * @return common rectangle.
     */
    static public LayoutRect getCommonRectangle(LayoutRect... rectangles) {
        Float ury = -Float.MAX_VALUE;
        Float llx = Float.MAX_VALUE;
        Float lly = Float.MAX_VALUE;
        Float urx = -Float.MAX_VALUE;
        for (LayoutRect rectangle : rectangles) {
            LayoutRect rec = (LayoutRect) rectangle.clone();
            if (rec.getHeight() == null)
                rec.setHeight(0f);
            if (rec.getWidth() == null)
                rec.setWidth(0f);
            if (rec.getY() < lly)
                lly = rec.getY();
            if (rec.getX() < llx)
                llx = rec.getX();
            if (rec.getY() + rec.getHeight() > ury)
                ury = rec.getY() + rec.getHeight();
            if (rec.getX() + rec.getWidth() > urx)
                urx = rec.getX() + rec.getWidth();
        }

        return new LayoutRect(llx, lly, urx-llx, ury-lly);
    }

    public LayoutRect(Float width, Float height) {
        this((float) 0, (float)0, width, height);
    }

    public Float getX() {
        return x;
    }

    public void setX(Float x) {
        this.x = x;
    }

    public Float getY() {
        return y;
    }

    public void setY(Float y) {
        this.y = y;
    }

    public Float getWidth() {
        return width;
    }

    public void setWidth(Float width) {
        this.width = width;
    }

    public Float getHeight() {
        return height;
    }

    public void setHeight(Float height) {
        this.height = height;
    }

    @Override
    protected Object clone() {
        return new LayoutRect(x, y, width, height);
    }



}
