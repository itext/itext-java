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
package com.itextpdf.kernel.pdf.colorspace;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfObjectWrapper;
import com.itextpdf.kernel.pdf.PdfResources;
import com.itextpdf.kernel.pdf.PdfStream;

/**
 * Dictionary wrapper that represent special type of color space, that uses pattern objects
 * as the equivalent of colour values instead of the numeric component values used with other spaces.
 * A pattern object shall be a dictionary or a stream, depending on the type of pattern.
 * For mor information see paragraph 8.7 in ISO-32000-1.
 */
public abstract class PdfPattern extends PdfObjectWrapper<PdfDictionary> {

    private static final long serialVersionUID = -6771280634868639993L;

	protected PdfPattern(PdfDictionary pdfObject) {
        super(pdfObject);
    }

    /**
     * Creates the instance wrapper of correct type from the {@link PdfDictionary}
     *
     * @param pdfObject the {@link PdfDictionary} that represent Pattern
     * @return new wrapper instance.
     */
    public static PdfPattern getPatternInstance(PdfDictionary pdfObject) {
        PdfNumber type = pdfObject.getAsNumber(PdfName.PatternType);
        if (type.intValue() == 1 && pdfObject instanceof PdfStream)
            return new Tiling((PdfStream)pdfObject);
        else if (type.intValue() == 2)
            return new Shading(pdfObject);
        throw new IllegalArgumentException("pdfObject");
    }

    /**
     * Gets a transformation matrix that maps the pattern’s internal coordinate system
     * to the default coordinate system of the pattern’s parent content stream.
     * The concatenation of the pattern matrix with that of the parent content stream
     * establishes the pattern coordinate space, within which all graphics objects in the pattern shall be interpreted.
     *
     * @return pattern matrix
     */
    public PdfArray getMatrix() {
        return getPdfObject().getAsArray(PdfName.Matrix);
    }

    /**
     * Sets a transformation matrix that maps the pattern’s internal coordinate system
     * to the default coordinate system of the pattern’s parent content stream.
     * The concatenation of the pattern matrix with that of the parent content stream
     * establishes the pattern coordinate space, within which all graphics objects in the pattern shall be interpreted.
     *
     * @param matrix pattern matrix to set
     */
    public void setMatrix(PdfArray matrix) {
        getPdfObject().put(PdfName.Matrix, matrix);
        setModified();
    }

    /**
     * To manually flush a {@code PdfObject} behind this wrapper, you have to ensure
     * that this object is added to the document, i.e. it has an indirect reference.
     * Basically this means that before flushing you need to explicitly call {@link #makeIndirect(PdfDocument)}.
     * For example: wrapperInstance.makeIndirect(document).flush();
     * Note that not every wrapper require this, only those that have such warning in documentation.
     */
    @Override
    public void flush() {
        super.flush();
    }

    @Override
    protected boolean isWrappedObjectMustBeIndirect() {
        return true;
    }

    /**
     * Wrapper that represents tiling pattern of color space. This pattern consists of a small graphical figure (cells).
     * Painting with the pattern replicates the cell at fixed horizontal and vertical intervals to fill an area.
     * The pattern cell can include graphical elements such as filled areas, text, and sampled images.
     * Its shape need not be rectangular, and the spacing of tiles can differ from the dimensions of the cell itself.
     * The appearance of the pattern cell shall be defined by a content stream
     * containing the painting operators needed to paint one instance of the cell
     */
    public static class Tiling extends PdfPattern {

        private static final long serialVersionUID = 1450379837955897673L;
		
        private PdfResources resources = null;

        /**
         * A code that determines how the colour of the pattern cell shall be specified
         */
        public static class PaintType {
            /**
             * The pattern’s content stream shall specify the colours used to paint the pattern cell.
             */
            public static final int COLORED = 1;

            /**
             * The pattern’s content stream shall not specify any colour information.
             * Instead, the entire cell is painted with a separately specified colour each time the pattern is used.
             */
            public static final int UNCOLORED = 2;
        }

        /**
         * A code that controls adjustments to the spacing of tiles relative to the device pixel grid
         */
        public static class TilingType {
            /**
             * Pattern cells shall be spaced consistently—that is, by a multiple of a device pixel.
             * To achieve this, the conforming reader may need to distort the pattern cell slightly.
             */
            public static final int CONSTANT_SPACING = 1;

            /**
             * The pattern cell shall not be distorted,
             * but the spacing between pattern cells may vary by as much as 1 device pixel.
             */
            public static final int NO_DISTORTION = 2;

            /**
             * Pattern cells shall be spaced consistently as in tiling type 1,
             * but with additional distortion permitted to enable a more efficient implementation.
             */
            public static final int CONSTANT_SPACING_AND_FASTER_TILING = 3;
        }

        /**
         * Creates new instance from the {@link PdfStream} object.
         * This stream should have PatternType equals to 1.
         *
         * @param pdfObject the {@link PdfStream} that represents Tiling Pattern.
         */
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

        /**
         * Checks if this pattern have colored paint type.
         *
         * @return {@code true} if this pattern's paint type is {@link PaintType#COLORED} and {@code false} otherwise.
         */
        public boolean isColored() {
            return getPdfObject().getAsNumber(PdfName.PaintType).intValue() == PaintType.COLORED;
        }

        /**
         * Sets the paint type.
         *
         * @param colored if {@code true} then the paint type will be set as {@link PaintType#COLORED},
         *                and {@link PaintType#UNCOLORED} otherwise.
         */
        public void setColored(boolean colored) {
            getPdfObject().put(PdfName.PaintType, new PdfNumber(colored ? PaintType.COLORED : PaintType.UNCOLORED));
            setModified();
        }

        /**
         * Gets the tiling type.
         *
         * @return int value of {@link TilingType}
         */
        public int getTilingType() {
            return getPdfObject().getAsNumber(PdfName.TilingType).intValue();
        }

        /**
         * Sets the tiling type.
         *
         * @param tilingType int value of {@link TilingType} to set.
         * @throws IllegalArgumentException in case of wrong value.
         */
        public void setTilingType(int tilingType) {
            if (tilingType != TilingType.CONSTANT_SPACING && tilingType != TilingType.NO_DISTORTION &&
                    tilingType != TilingType.CONSTANT_SPACING_AND_FASTER_TILING)
                throw new IllegalArgumentException("tilingType");
            getPdfObject().put(PdfName.TilingType, new PdfNumber(tilingType));
            setModified();
        }

        /**
         * Gets the pattern cell's bounding box. These boundaries shall be used to clip the pattern cell.
         *
         * @return pattern cell's bounding box.
         */
        public Rectangle getBBox() {
            return getPdfObject().getAsArray(PdfName.BBox).toRectangle();
        }

        /**
         * Sets the pattern cell's bounding box. These boundaries shall be used to clip the pattern cell.
         *
         * @param bbox pattern cell's bounding box to set.
         */
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
                PdfDictionary resourcesDict = getPdfObject().getAsDictionary(PdfName.Resources);
                if (resourcesDict == null) {
                    resourcesDict = new PdfDictionary();
                    getPdfObject().put(PdfName.Resources, resourcesDict);
                }
                this.resources = new PdfResources(resourcesDict);
            }
            return resources;
        }

        /**
         * {@inheritDoc}
         */
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
