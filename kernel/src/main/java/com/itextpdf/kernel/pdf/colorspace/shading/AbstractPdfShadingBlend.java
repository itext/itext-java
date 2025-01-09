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
package com.itextpdf.kernel.pdf.colorspace.shading;

import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.colorspace.PdfColorSpace;

/**
 * The PdfShadingBlend class which extends {@link AbstractPdfShading} and represents shadings which are
 * based on a blend, with Coords, Domain and Extend fields in the PDF object.
 */
public abstract class AbstractPdfShadingBlend extends AbstractPdfShading {

    /**
     * Gets the coords {@link PdfArray} object.
     *
     * @return the {@link PdfArray} coords object
     */
    public PdfArray getCoords() {
        return getPdfObject().getAsArray(PdfName.Coords);
    }

    /**
     * Sets the Coords object with the {@link PdfArray},
     * that specified the starting and the endings coordinates of thew axis,
     * expressed in the shading's target coordinate space.
     *
     * @param coords the Chords {@link PdfArray} to be set
     */
    public final void setCoords(PdfArray coords) {
        getPdfObject().put(PdfName.Coords, coords);
        setModified();
    }

    /**
     * Gets the {@link PdfArray} of two {@code float} [t0, t1] that represent the limiting values of a parametric
     * variable t, that becomes an input of color function(s).
     *
     * @return the {@link PdfArray} of Domain object ([0.0 1.0] by default)
     */
    public PdfArray getDomain() {
        PdfArray domain = getPdfObject().getAsArray(PdfName.Domain);
        if (domain == null) {
            domain = new PdfArray(new float[]{0, 1});
            setDomain(domain);
        }
        return domain;
    }

    /**
     * Sets the Domain with the array of two {@code float} [t0, t1] that represent the limiting values
     * of a parametric variable t, that becomes an input of color function(s).
     *
     * @param t0 first limit of variable t
     * @param t1 second limit of variable t
     */
    public final void setDomain(float t0, float t1) {
        setDomain(new PdfArray(new float[] {t0, t1}));
    }

    /**
     * Sets the Domain with the {@link PdfArray} of two {@code float} [t0, t1] that represent the limiting values
     * of a parametric variable t, that becomes an input of color function(s).
     *
     * @param domain the {@link PdfArray} that represents domain
     */
    public final void setDomain(PdfArray domain) {
        getPdfObject().put(PdfName.Domain, domain);
        setModified();
    }

    /**
     * Gets the {@link PdfArray} of two {@code boolean} that specified whether to extend the shading
     * beyond the starting and ending points of the axis, respectively.
     *
     * @return the {@link PdfArray} of Extended object ([false false] by default)
     */
    public PdfArray getExtend() {
        PdfArray extend = getPdfObject().getAsArray(PdfName.Extend);
        if (extend == null) {
            extend = new PdfArray(new boolean[]{false, false});
            setExtend(extend);
        }
        return extend;
    }

    /**
     * Sets the Extend object with the two {@code boolean} value.
     *
     * @param extendStart if true will extend shading beyond the starting point of Coords
     * @param extendEnd if true will extend shading beyond the ending point of Coords
     */
    public final void setExtend(boolean extendStart, boolean extendEnd) {
        setExtend(new PdfArray(new boolean[] {extendStart, extendEnd}));
    }

    /**
     * Sets the Extend object with the {@link PdfArray} of two {@code boolean}.
     * If first is true shading will extend beyond the starting point of Coords.
     * If second is true shading will extend beyond the ending point of Coords.
     *
     * @param extend the {@link PdfArray} representing Extend object
     */
    public final void setExtend(PdfArray extend) {
        getPdfObject().put(PdfName.Extend, extend);
        setModified();
    }

    /**
     * Constructor for PdfShadingBlend object using a PdfDictionary.
     *
     * @param pdfObject input PdfDictionary
     */
    protected AbstractPdfShadingBlend(PdfDictionary pdfObject) {
        super(pdfObject);
    }

    /**
     * Constructor for PdfShadingBlend object using PdfDictionary, shading type and colorspace value.
     *
     * @param pdfObject input PdfDictionary
     * @param shadingType shading type
     * @param cs color space
     */
    protected AbstractPdfShadingBlend(PdfDictionary pdfObject, int shadingType, PdfColorSpace cs) {
        super(pdfObject, shadingType, cs);
    }
}
