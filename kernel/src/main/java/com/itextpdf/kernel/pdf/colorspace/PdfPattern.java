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
package com.itextpdf.kernel.pdf.colorspace;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfObjectWrapper;
import com.itextpdf.kernel.pdf.PdfResources;
import com.itextpdf.kernel.pdf.PdfStream;

public abstract class PdfPattern extends PdfObjectWrapper<PdfDictionary> {

    private static final long serialVersionUID = -6771280634868639993L;

	public PdfPattern(PdfDictionary pdfObject) {
        super(pdfObject);
    }

    public static PdfPattern getPatternInstance(PdfDictionary pdfObject) {
        PdfNumber type = pdfObject.getAsNumber(PdfName.PatternType);
        if (type.intValue() == 1 && pdfObject instanceof PdfStream)
            return new Tiling((PdfStream)pdfObject);
        else if (type.intValue() == 2)
            return new Shading(pdfObject);
        throw new IllegalArgumentException("pdfObject");
    }

    public PdfArray getMatrix() {
        return getPdfObject().getAsArray(PdfName.Matrix);
    }

    public void setMatrix(PdfArray matrix) {
        getPdfObject().put(PdfName.Matrix, matrix);
        setModified();
    }

    @Override
    protected boolean isWrappedObjectMustBeIndirect() {
        return true;
    }

    public static class Tiling extends PdfPattern {

        private static final long serialVersionUID = 1450379837955897673L;
		
        private PdfResources resources = null;

        public static class PaintType {
            public static final int COLORED = 1;
            public static final int UNCOLORED = 2;
        }

        public static class TilingType {
            public static final int CONSTANT_SPACING = 1;
            public static final int NO_DISTORTION = 2;
            public static final int CONSTANT_SPACING_AND_FASTER_TILING = 3;
        }

        public Tiling(PdfStream pdfObject) {
            super(pdfObject);
        }

        public Tiling(float width, float height) {
            this(width, height, true);
        }

        public Tiling(float width, float height, boolean colored) {
            this(new Rectangle(width, height), colored);
        }

        public Tiling(Rectangle bbox) {
            this(bbox, true);
        }

        public Tiling(Rectangle bbox, boolean colored) {
            this(bbox, bbox.getWidth(), bbox.getHeight(), colored);
        }

        public Tiling(float width, float height, float xStep, float yStep) {
            this(width, height, xStep, yStep, true);
        }

        public Tiling(float width, float height, float xStep, float yStep, boolean colored) {
            this(new Rectangle(width, height), xStep, yStep, colored);
        }

        public Tiling(Rectangle bbox, float xStep, float yStep) {
            this(bbox, xStep, yStep, true);
        }

        public Tiling(Rectangle bbox, float xStep, float yStep, boolean colored) {
            super(new PdfStream());
            getPdfObject().put(PdfName.Type, PdfName.Pattern);
            getPdfObject().put(PdfName.PatternType, new PdfNumber(1));
            getPdfObject().put(PdfName.PaintType, new PdfNumber(colored ? PaintType.COLORED : PaintType.UNCOLORED));
            getPdfObject().put(PdfName.TilingType, new PdfNumber(TilingType.CONSTANT_SPACING));
            getPdfObject().put(PdfName.BBox, new PdfArray(bbox));
            getPdfObject().put(PdfName.XStep, new PdfNumber(xStep));
            getPdfObject().put(PdfName.YStep, new PdfNumber(yStep));
            resources = new PdfResources();
            getPdfObject().put(PdfName.Resources, resources.getPdfObject());
        }

        public boolean isColored() {
            return getPdfObject().getAsNumber(PdfName.PaintType).intValue() == PaintType.COLORED;
        }

        public void setColored(boolean colored) {
            getPdfObject().put(PdfName.PaintType, new PdfNumber(colored ? PaintType.COLORED : PaintType.UNCOLORED));
            setModified();
        }

        public int getTilingType() {
            return getPdfObject().getAsNumber(PdfName.TilingType).intValue();
        }

        public void setTilingType(int tilingType) {
            if (tilingType != TilingType.CONSTANT_SPACING && tilingType != TilingType.NO_DISTORTION &&
                    tilingType != TilingType.CONSTANT_SPACING_AND_FASTER_TILING)
                throw new IllegalArgumentException("tilingType");
            getPdfObject().put(PdfName.TilingType, new PdfNumber(tilingType));
            setModified();
        }

        public Rectangle getBBox() {
            return getPdfObject().getAsArray(PdfName.BBox).toRectangle();
        }

        public void setBBox(Rectangle bbox) {
            getPdfObject().put(PdfName.BBox, new PdfArray(bbox));
            setModified();
        }

        public float getXStep() {
            return getPdfObject().getAsNumber(PdfName.XStep).floatValue();
        }

        public void setXStep(float xStep) {
            getPdfObject().put(PdfName.XStep, new PdfNumber(xStep));
            setModified();
        }

        public float getYStep() {
            return getPdfObject().getAsNumber(PdfName.YStep).floatValue();
        }

        public void setYStep(float yStep) {
            getPdfObject().put(PdfName.YStep, new PdfNumber(yStep));
            setModified();
        }

        public PdfResources getResources() {
            if (this.resources == null) {
                PdfDictionary resources = getPdfObject().getAsDictionary(PdfName.Resources);
                if (resources == null) {
                    resources = new PdfDictionary();
                    getPdfObject().put(PdfName.Resources, resources);
                }
                this.resources = new PdfResources(resources);
            }
            return resources;
        }

        @Override
        public void flush() {
            resources = null;
            super.flush();
        }
    }

    public static class Shading extends PdfPattern {

        private static final long serialVersionUID = -4289411438737403786L;

		public Shading(PdfDictionary pdfObject) {
            super(pdfObject);
        }

        public Shading(com.itextpdf.kernel.pdf.colorspace.PdfShading shading) {
            super(new PdfDictionary());
            getPdfObject().put(PdfName.Type, PdfName.Pattern);
            getPdfObject().put(PdfName.PatternType, new PdfNumber(2));
            getPdfObject().put(PdfName.Shading, shading.getPdfObject());
        }

        public PdfDictionary getShading() {
            return (PdfDictionary) getPdfObject().get(PdfName.Shading);
        }

        public void setShading(com.itextpdf.kernel.pdf.colorspace.PdfShading shading) {
            getPdfObject().put(PdfName.Shading, shading.getPdfObject());
            setModified();
        }

        public void setShading(PdfDictionary shading) {
            getPdfObject().put(PdfName.Shading, shading);
            setModified();
        }
    }
}