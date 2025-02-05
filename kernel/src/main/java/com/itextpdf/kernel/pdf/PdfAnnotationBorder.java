/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2025 Apryse Group NV
    Authors: Apryse Software.

    This program is offered under a commercial and under the AGPL license.
    For commercial licensing, contact us at https://itextpdf.com/sales.  For AGPL licensing, see below.

    AGPL licensing:
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.itextpdf.kernel.pdf;

/**
 * Specifying the characteristics of the annotation’s border.
 * See ISO 32000-1 12.5.2, Table 164 - Entries common to all annotation dictionaries, Key - border.
 *
 * <p>
 * Note (PDF 1.2): The dictionaries for some annotation types  can include the BS (border style) entry.
 * That entry specifies a border style dictionary that has more settings than this class.
 * If an annotation has BS entry, then {@link PdfAnnotationBorder} is ignored.
 */
public class PdfAnnotationBorder extends PdfObjectWrapper<PdfArray> {


    /**
     * Creates a {@link PdfAnnotationBorder} with three numbers defining the horizontal
     * corner radius, vertical corner radius, and border width, all in default user
     * space units. If the corner radii are 0, the border has square (not rounded)
     * corners; if the border width is 0, no border is drawn.
     *
     * @param hRadius horizontal corner radius
     * @param vRadius vertical corner radius
     * @param width width of the border
     */
	public PdfAnnotationBorder(float hRadius, float vRadius, float width) {
        this(hRadius, vRadius, width, null);
    }

    /**
     * Creates a {@link PdfAnnotationBorder} with three numbers defining the horizontal
     * corner radius, vertical corner radius, and border width, all in default user
     * space units and a dash pattern for the border lines. If the corner radii are 0,
     * the border has square (not rounded) corners; if the border width is 0, no border is drawn.
     *
     * @param hRadius horizontal corner radius
     * @param vRadius vertical corner radius
     * @param width width of the border
     * @param dash the dash pattern
     */
    public PdfAnnotationBorder(float hRadius, float vRadius, float width, PdfDashPattern dash) {
        super(new PdfArray(new float[]{hRadius, vRadius, width}));
        if (dash != null) {
            PdfArray dashArray = new PdfArray();
            getPdfObject().add(dashArray);
            if (dash.getDash() >= 0) {
                dashArray.add(new PdfNumber(dash.getDash()));
            }
            if (dash.getGap() >= 0) {
                dashArray.add(new PdfNumber(dash.getGap()));
            }
            if (dash.getPhase() >= 0) {
                getPdfObject().add(new PdfNumber(dash.getPhase()));
            }
        }
    }

    @Override
    protected boolean isWrappedObjectMustBeIndirect() {
        return false;
    }
}
