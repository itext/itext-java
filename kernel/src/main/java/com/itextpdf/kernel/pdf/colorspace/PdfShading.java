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
package com.itextpdf.kernel.pdf.colorspace;

import com.itextpdf.kernel.PdfException;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfObjectWrapper;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.function.PdfFunction;

/**
 * The abstract PdfShading class that represents the Shading Dictionary PDF object.
 */
public abstract class PdfShading extends PdfObjectWrapper<PdfDictionary> {

    private static final long serialVersionUID = 4781809723744243508L;

    /**constants of shading type {@see ISO-320001 Table 78} */
	private static class ShadingType {
        /** The int value of function-based shading type*/
        public static final int FUNCTION_BASED = 1;
        /** The int value of axial shading type*/
        public static final int AXIAL = 2;
        /** The int value of radial shading type*/
        public static final int RADIAL = 3;
        /** The int value of free-form Gouraud-shaded triangle mesh shading type*/
        public static final int FREE_FORM_GOURAUD_SHADED_TRIANGLE_MESH = 4;
        /** The int value of lattice-form Gouraud-shaded triangle mesh shading type*/
        public static final int LATTICE_FORM_GOURAUD_SHADED_TRIANGLE_MESH = 5;
        /** The int value of coons patch meshes shading type*/
        public static final int COONS_PATCH_MESH = 6;
        /** The int value of tensor-product patch meshes shading type*/
        public static final int TENSOR_PRODUCT_PATCH_MESH = 7;
    }

    /**
     * Creates the {@link PdfShading} object from the existing {@link PdfDictionary} with corresponding type.
     *
     * @param shadingDictionary {@link PdfDictionary} from which the {@link PdfShading} object will be created.
     * @return Created {@link PdfShading} object.
     */
    public static PdfShading makeShading(PdfDictionary shadingDictionary) {
        if (!shadingDictionary.containsKey(PdfName.ShadingType)) {
            throw new PdfException(PdfException.UnexpectedShadingType);
        }
        PdfShading shading;
        switch (shadingDictionary.getAsNumber(PdfName.ShadingType).intValue()) {
            case ShadingType.FUNCTION_BASED:
                shading = new FunctionBased(shadingDictionary);
                break;
            case ShadingType.AXIAL:
                shading = new Axial(shadingDictionary);
                break;
            case ShadingType.RADIAL:
                shading = new Radial(shadingDictionary);
                break;
            case ShadingType.FREE_FORM_GOURAUD_SHADED_TRIANGLE_MESH:
                if (!shadingDictionary.isStream()) {
                    throw new PdfException(PdfException.UnexpectedShadingType);
                }
                shading = new FreeFormGouraudShadedTriangleMesh((PdfStream) shadingDictionary);
                break;
            case ShadingType.LATTICE_FORM_GOURAUD_SHADED_TRIANGLE_MESH:
                if (!shadingDictionary.isStream()) {
                    throw new PdfException(PdfException.UnexpectedShadingType);
                }
                shading = new LatticeFormGouraudShadedTriangleMesh((PdfStream) shadingDictionary);
                break;
            case ShadingType.COONS_PATCH_MESH:
                if (!shadingDictionary.isStream()) {
                    throw new PdfException(PdfException.UnexpectedShadingType);
                }
                shading = new CoonsPatchMesh((PdfStream) shadingDictionary);
                break;
            case ShadingType.TENSOR_PRODUCT_PATCH_MESH:
                if (!shadingDictionary.isStream()) {
                    throw new PdfException(PdfException.UnexpectedShadingType);
                }
                shading = new TensorProductPatchMesh((PdfStream) shadingDictionary);
                break;
            default:
                throw new PdfException(PdfException.UnexpectedShadingType);
        }
        return shading;
    }

    protected PdfShading(PdfDictionary pdfObject) {
        super(pdfObject);
    }

    protected PdfShading(PdfDictionary pdfObject, int type, PdfObject colorSpace) {
        super(pdfObject);
        getPdfObject().put(PdfName.ShadingType, new PdfNumber(type));
        getPdfObject().put(PdfName.ColorSpace, colorSpace);
    }

    /**
     * Gets the shading type.
     *
     * @return int value of {@link ShadingType}.
     */
    public int getShadingType() {
        return (int) getPdfObject().getAsInt(PdfName.ShadingType);
    }

    /**
     * Gets the color space in which colour values shall be expressed.
     *
     * @return {@link PdfObject} Color space
     */
    public PdfObject getColorSpace() {
        return getPdfObject().get(PdfName.ColorSpace);
    }

    /**
     * Gets the function PdfObject that represents color transitions
     * across the shading geometry.
     *
     * @return {@link PdfObject} Function
     */
    public PdfObject getFunction() {
        return getPdfObject().get(PdfName.Function);
    }

    /**
     * Sets the function that represents color transitions
     * across the shading geometry as one object.
     *
     * @param function The {@link PdfFunction} to set.
     */
    public void setFunction(PdfFunction function) {
        getPdfObject().put(PdfName.Function, function.getPdfObject());
        setModified();
    }

    /**
     * Sets the function object that represents color transitions
     * across the shading geometry as an array of functions.
     *
     * @param functions The array of {@link PdfFunction} to be set.
     */
    public void setFunction(PdfFunction[] functions) {
        PdfArray arr = new PdfArray();
        for (PdfFunction func : functions) {
            arr.add(func.getPdfObject());
        }
        getPdfObject().put(PdfName.Function, arr);
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
     * The class that extends {@link PdfShading} class and is in charge of Shading Dictionary with function-based type,
     * that defines color at every point in the domain by a specified mathematical function.
     */
    public static class FunctionBased extends PdfShading {

        private static final long serialVersionUID = -4459197498902558052L;

        /**
         * Creates the new instance of the class from the existing {@link PdfDictionary} object.
         *
         * @deprecated Intended only for private use.
         * You should use {@link PdfShading#makeShading(PdfDictionary)} instead.
         *
         * @param pdfDictionary {@link PdfDictionary} from which the instance is created.
         */
        @Deprecated
		public FunctionBased(PdfDictionary pdfDictionary) {
            super(pdfDictionary);
        }

        /**
         * Creates the new instance of the class.
         *
         * @param colorSpace the {@link PdfColorSpace} object in which colour values shall be expressed.
         * @param function the {@link PdfFunction}, that is used to calculate color transitions.
         */
        public FunctionBased(PdfColorSpace colorSpace, PdfFunction function) {
            this(colorSpace.getPdfObject(), function);
        }

        /**
         * Creates the new instance of the class.
         *
         * @param colorSpace the {@link PdfObject}, that represents color space in which colour values shall be expressed.
         * @param function the {@link PdfFunction}, that is used to calculate color transitions.
         */
        public FunctionBased(PdfObject colorSpace, PdfFunction function) {
            super(new PdfDictionary(), ShadingType.FUNCTION_BASED, colorSpace);

            setFunction(function);
        }

        /**
         * Gets the {@link PdfArray} domain rectangle object that establishes an internal coordinate space
         * for the shading that is independent of the target coordinate space in which it shall be painted.
         *
         * @return {@link PdfArray} domain rectangle.
         */
        public PdfArray getDomain() {
            return getPdfObject().getAsArray(PdfName.Domain);
        }

        /**
         * Sets the {@link PdfArray} domain rectangle object that establishes an internal coordinate space
         * for the shading that is independent of the target coordinate space in which it shall be painted.
         *
         * @param xmin the Xmin coordinate of rectangle.
         * @param xmax the Xmax coordinate of rectangle.
         * @param ymin the Ymin coordinate of rectangle.
         * @param ymax the Ymax coordinate of rectangle.
         */
        public void setDomain(float xmin, float xmax, float ymin, float ymax) {
            getPdfObject().put(PdfName.Domain, new PdfArray(new float[] {xmin, xmax, ymin, ymax}));
            setModified();
        }

        /**
         * Sets the {@link PdfArray} domain rectangle object that establishes an internal coordinate space
         * for the shading that is independent of the target coordinate space in which it shall be painted.
         *
         * @param domain the {@link PdfArray} domain rectangle object to be set.
         */
        public void setDomain(PdfArray domain) {
            getPdfObject().put(PdfName.Domain, domain);
            setModified();
        }

        /**
         * Gets the array of floats that represents the transformation matrix that maps the domain rectangle
         * into a corresponding figure in the target coordinate space.
         *
         * @return the {@code float[]} of transformation matrix (identical matrix by default).
         */
        public float[] getMatrix() {
            PdfArray matrix = getPdfObject().getAsArray(PdfName.Matrix);
            if (matrix == null)
                return new float[] {1, 0, 0, 1, 0, 0};
            float[] result = new float[6];
            for (int i = 0; i < 6; i++)
                result[i] = matrix.getAsNumber(i).floatValue();
            return result;
        }

        /**
         * Sets the array of floats that represents the transformation matrix that maps the domain rectangle
         * into a corresponding figure in the target coordinate space.
         *
         * @param matrix the {@code float[]} of transformation matrix to be set.
         */
        public void setMatrix(float[] matrix) {
            setMatrix(new PdfArray(matrix));
        }

        /**
         * Sets the array of floats that represents the transformation matrix that maps the domain rectangle
         * into a corresponding figure in the target coordinate space.
         *
         * @param matrix the {@link PdfArray} transformation matrix object to be set.
         */
        public void setMatrix(PdfArray matrix) {
            getPdfObject().put(PdfName.Matrix, matrix);
            setModified();
        }
    }

    /**
     * The class that extends {@link PdfShading} class and is in charge of Shading Dictionary with axial type,
     * that define a colour blend that varies along a linear axis between two endpoints
     * and extends indefinitely perpendicular to that axis.
     */
    public static class Axial extends PdfShading {

        private static final long serialVersionUID = 5504688740677023792L;

        /**
         * Creates the new instance of the class from the existing {@link PdfDictionary} object.
         *
         * @deprecated Intended only for private use.
         * You should use {@link PdfShading#makeShading(PdfDictionary)} instead.
         *
         * @param pdfDictionary {@link PdfDictionary} from which the instance is created.
         */
        @Deprecated
		public Axial(PdfDictionary pdfDictionary) {
            super(pdfDictionary);
        }

        /**
         * Creates the new instance of the class.
         *
         * @param cs the {@link PdfColorSpace} object in which colour values shall be expressed.
         *           The special Pattern space isn't excepted.
         * @param x0 the start coordinate of X axis expressed in the shading's target coordinate space.
         * @param y0 the start coordinate of Y axis expressed in the shading's target coordinate space.
         * @param color0 the {@code float[]} that represents the color in the start point.
         * @param x1 the end coordinate of X axis expressed in the shading's target coordinate space.
         * @param y1 the end coordinate of Y axis expressed in the shading's target coordinate space.
         * @param color1 the {@code float[]} that represents the color in the end point.
         */
        public Axial(PdfColorSpace cs, float x0, float y0, float[] color0, float x1, float y1, float[] color1) {
            super(new PdfDictionary(), ShadingType.AXIAL, cs.getPdfObject());

            if (cs instanceof PdfSpecialCs.Pattern)
                throw new IllegalArgumentException("colorSpace");

            setCoords(x0, y0, x1, y1);
            PdfFunction func = new PdfFunction.Type2(new PdfArray(new float[] {0, 1}), null,
                    new PdfArray(color0), new PdfArray(color1), new PdfNumber(1));
            setFunction(func);
        }

        /**
         * Creates the new instance of the class.
         *
         * @param cs the {@link PdfColorSpace} object in which colour values shall be expressed.
         *           The special Pattern space isn't excepted.
         * @param x0 the start coordinate of X axis expressed in the shading's target coordinate space.
         * @param y0 the start coordinate of Y axis expressed in the shading's target coordinate space.
         * @param color0 the {@code float[]} that represents the color in the start point.
         * @param x1 the end coordinate of X axis expressed in the shading's target coordinate space.
         * @param y1 the end coordinate of Y axis expressed in the shading's target coordinate space.
         * @param color1 the {@code float[]} that represents the color in the end point.
         * @param extend the array of two booleans that specified whether to extend the shading
         *               beyond the starting and ending points of the axis, respectively.
         */
        public Axial(PdfColorSpace cs, float x0, float y0, float[] color0, float x1, float y1, float[] color1, boolean[] extend) {
            this(cs, x0, y0, color0, x1, y1, color1);

            if (extend == null || extend.length != 2)
                throw new IllegalArgumentException("extend");

            setExtend(extend[0], extend[1]);
        }

        /**
         * Creates the new instance of the class.
         *
         * @param cs the {@link PdfColorSpace} object in which colour values shall be expressed.
         *           The special Pattern space isn't excepted.
         * @param coords the {@link PdfArray} of four number four numbers [x0 y0 x1 y1] that specified the starting
         *               and the endings coordinates of thew axis, expressed in the shading's target coordinate space.
         * @param function the {@link PdfFunction} object, that is used to calculate color transitions.
         */
        public Axial(PdfColorSpace cs, PdfArray coords, PdfFunction function) {
            super(new PdfDictionary(), ShadingType.AXIAL, cs.getPdfObject());
            setCoords(coords);
            setFunction(function);
        }


        /**
         * Gets the Coords object - a {@link PdfArray} of four numbers [x0 y0 x1 y1] that specified the starting
         * and the endings coordinates of thew axis, expressed in the shading's target coordinate space.
         *
         * @return the {@link PdfArray} Coords object.
         */
        public PdfArray getCoords() {
            return getPdfObject().getAsArray(PdfName.Coords);
        }

        /**
         * Sets the Choords object with the four params expressed in the shading's target coordinate space.
         *
         * @param x0 the start coordinate of X axis to be set.
         * @param y0 the start coordinate of Y axis to be set.
         * @param x1 the end coordinate of X axis to be set.
         * @param y1 the end coordinate of Y axis to be set.
         */
        public void setCoords(float x0, float y0, float x1, float y1) {
            setCoords(new PdfArray(new float[] {x0, y0, x1, y1}));
        }

        /**
         * Sets the Choords object with the {@link PdfArray} of four numbers [x0 y0 x1 y1],
         * that specified the starting and the endings coordinates of thew axis,
         * expressed in the shading's target coordinate space.
         *
         * @param coords the Chords {@link PdfArray} to be set.
         */
        public void setCoords(PdfArray coords) {
            getPdfObject().put(PdfName.Coords, coords);
            setModified();
        }

        /**
         * Gets the array of two {@code float} [t0, t1] that represent the limiting values of a parametric
         * variable t, that becomes an input of color function(s).
         *
         * @return {@code float[]} of Domain object ([0.0 1.0] by default)
         */
        public float[] getDomain() {
            PdfArray domain = getPdfObject().getAsArray(PdfName.Domain);
            if (domain == null)
                return new float[] {0, 1};
            return new float[] {domain.getAsNumber(0).floatValue(), domain.getAsNumber(1).floatValue()};
        }

        /**
         * Sets the Domain with the array of two {@code float} [t0, t1] that represent the limiting values
         * of a parametric variable t, that becomes an input of color function(s).
         *
         * @param t0 first limit of variable t
         * @param t1 second limit of variable t
         */
        public void setDomain(float t0, float t1) {
            getPdfObject().put(PdfName.Domain, new PdfArray(new float[] {t0, t1}));
            setModified();
        }

        /**
         * Gets the array of two {@code boolean} that specified whether to extend the shading
         * beyond the starting and ending points of the axis, respectively.
         *
         * @return {@code boolean[]} of Extended object ([false false] by default)
         */
        public boolean[] getExtend() {
            PdfArray extend = getPdfObject().getAsArray(PdfName.Extend);
            if (extend == null)
                return new boolean[] {false, false};
            return new boolean[] {extend.getAsBoolean(0).getValue(), extend.getAsBoolean(1).getValue()};
        }

        /**
         * Sets the Extend object with the two {@code boolean} value.
         *
         * @param extendStart if true will extend shading beyond the starting point of Coords
         * @param extendEnd if true will extend shading beyond the ending point of Coords
         */
        public void setExtend(boolean extendStart, boolean extendEnd) {
            getPdfObject().put(PdfName.Extend, new PdfArray(new boolean[] {extendStart, extendEnd}));
            setModified();
        }
    }

    /**
     * The class that extends {@link PdfShading} class and is in charge of Shading Dictionary with radial type,
     * that define a colour blend that varies between two circles.
     * This type of shading shall not be used with an Indexed colour space
     */
    public static class Radial extends PdfShading {

        private static final long serialVersionUID = -5012819396006804845L;

        /**
         * Creates the new instance of the class from the existing {@link PdfDictionary} object.
         *
         * @deprecated Intended only for private use.
         * You should use {@link PdfShading#makeShading(PdfDictionary)} instead.
         *
         * @param pdfDictionary - {@link PdfDictionary} from which the instance is created.
         */
        public Radial(PdfDictionary pdfDictionary) {
            super(pdfDictionary);
        }

        /**
         * Creates the new instance of the class.         *
         *
         * @param cs the {@link PdfColorSpace} object in which colour values shall be expressed.
         *           The Indexed color space isn't excepted.
         * @param x0 the X coordinate of starting circle's centre, expressed in in the shading’s target coordinate space.
         * @param y0 the Y coordinate of starting circle's centre, expressed in in the shading’s target coordinate space.
         * @param r0 the radius of starting circle's centre, should be greater or equal to 0.
         *           If 0 then starting circle is treated as point.
         *           If both radii are 0, nothing shall be painted.
         * @param color0 the {@code float[]} that represents the color in the start circle.
         * @param x1 the X coordinate of ending circle's centre, expressed in in the shading’s target coordinate space.
         * @param y1 the Y coordinate of ending circle's centre, expressed in in the shading’s target coordinate space.
         * @param r1 the radius of ending circle's centre, should be greater or equal to 0.
         *           If 0 then ending circle is treated as point.
         *           If both radii are 0, nothing shall be painted.
         * @param color1 the {@code float[]} that represents the color in the end circle.
         */
        public Radial(PdfColorSpace cs, float x0, float y0, float r0, float[] color0, float x1, float y1, float r1, float[] color1) {
            super(new PdfDictionary(), ShadingType.RADIAL, cs.getPdfObject());

            setCoords(x0, y0, r0, x1, y1, r1);
            PdfFunction func = new PdfFunction.Type2(new PdfArray(new float[] {0, 1}), null,
                    new PdfArray(color0), new PdfArray(color1), new PdfNumber(1));
            setFunction(func);
        }

        /**
         * Creates the new instance of the class.         *
         *
         * @param cs the {@link PdfColorSpace} object in which colour values shall be expressed.
         *           The Indexed color space isn't excepted.
         * @param x0 the X coordinate of starting circle's centre, expressed in in the shading’s target coordinate space.
         * @param y0 the Y coordinate of starting circle's centre, expressed in in the shading’s target coordinate space.
         * @param r0 the radius of starting circle's centre, should be greater or equal to 0.
         *           If 0 then starting circle is treated as point.
         *           If both radii are 0, nothing shall be painted.
         * @param color0 the {@code float[]} that represents the color in the start circle.
         * @param x1 the X coordinate of ending circle's centre, expressed in in the shading’s target coordinate space.
         * @param y1 the Y coordinate of ending circle's centre, expressed in in the shading’s target coordinate space.
         * @param r1 the radius of ending circle's centre, should be greater or equal to 0.
         *           If 0 then ending circle is treated as point.
         *           If both radii are 0, nothing shall be painted.
         * @param color1 the {@code float[]} that represents the color in the end circle.
         * @param extend the array of two {@code boolean} that specified whether to extend the shading
         *               beyond the starting and ending points of the axis, respectively.
         */
        public Radial(PdfColorSpace cs, float x0, float y0, float r0, float[] color0, float x1, float y1, float r1, float[] color1, boolean[] extend) {
            this(cs, x0, y0, r0, color0, x1, y1, r1, color1);

            if (extend != null)
                setExtend(extend[0], extend[1]);
        }

        /**
         * Creates the new instance of the class.
         *
         * @param cs the {@link PdfColorSpace} object in which colour values shall be expressed.
         *           The Indexed color space isn't excepted.
         * @param coords the {@link PdfArray} of of six numbers [x0 y0 r0 x1 y1 r1],
         *               specifying the centres and radii of the starting and ending circles,
         *               expressed in the shading’s target coordinate space.
         *               The radii r0 and r1 shall both be greater than or equal to 0.
         *               If one radius is 0, the corresponding circle shall be treated as a point;
         *               if both are 0, nothing shall be painted.
         * @param function the {@link PdfFunction} object, that is used to calculate color transitions.
         */
        public Radial(PdfColorSpace cs, PdfArray coords, PdfFunction function) {
            super(new PdfDictionary(), ShadingType.RADIAL, cs.getPdfObject());
            setCoords(coords);
            setFunction(function);
        }

        /**
         * Gets the coords {@link PdfArray} object - an array of six numbers [x0 y0 r0 x1 y1 r1],
         * specifying the centres and radii of the starting and ending circles,
         * expressed in the shading’s target coordinate space.
         * The radii r0 and r1 shall both be greater than or equal to 0.
         * If one radius is 0, the corresponding circle shall be treated as a point;
         * if both are 0, nothing shall be painted.
         *
         * @return the {@link PdfArray} coords object.
         */
        public PdfArray getCoords() {
            return getPdfObject().getAsArray(PdfName.Coords);
        }

        /**
         * Sets the coords object.
         *
         * @param x0 the X coordinate of starting circle's centre, expressed in in the shading’s target coordinate space.
         * @param y0 the Y coordinate of starting circle's centre, expressed in in the shading’s target coordinate space.
         * @param r0 the radius of starting circle's centre, should be greater or equal to 0.
         *           If 0 then starting circle is treated as point.
         *           If both radii are 0, nothing shall be painted.
         * @param x1 the X coordinate of ending circle's centre, expressed in in the shading’s target coordinate space.
         * @param y1 the Y coordinate of ending circle's centre, expressed in in the shading’s target coordinate space.
         * @param r1 the radius of ending circle's centre, should be greater or equal to 0.
         *           If 0 then ending circle is treated as point.
         *           If both radii are 0, nothing shall be painted.
         */
        public void setCoords(float x0, float y0, float r0, float x1, float y1, float r1) {
            setCoords(new PdfArray(new float[] {x0, y0, r0, x1, y1, r1}));
        }

        /**
         * Sets the coords {@link PdfArray} object - an array of six numbers [x0 y0 r0 x1 y1 r1],
         * specifying the centres and radii of the starting and ending circles,
         * expressed in the shading’s target coordinate space.
         * The radii r0 and r1 shall both be greater than or equal to 0.
         * If one radius is 0, the corresponding circle shall be treated as a point;
         * if both are 0, nothing shall be painted.
         *
         * @param coords - {@link PdfArray} choords object to be set.
         */
        public void setCoords(PdfArray coords) {
            getPdfObject().put(PdfName.Coords, coords);
            setModified();
        }

        /**
         * Gets the array of two {@code float} [t0, t1] that represent the limiting values of a parametric
         * variable t, that becomes an input of color function(s).
         *
         * @return {@code float[]} of Domain object ([0.0 1.0] by default)
         */
        public float[] getDomain() {
            PdfArray domain = getPdfObject().getAsArray(PdfName.Domain);
            if (domain == null)
                return new float[] {0, 1};
            return new float[]{domain.getAsNumber(0).floatValue(), domain.getAsNumber(1).floatValue()};
        }

        /**
         * Sets the Domain with the array of two {@code float} [t0, t1] that represent the limiting values
         * of a parametric variable t, that becomes an input of color function(s).
         *
         * @param t0 first limit of variable t
         * @param t1 second limit of variable t
         */
        public void setDomain(float t0, float t1) {
            getPdfObject().put(PdfName.Domain, new PdfArray(new float[] {t0, t1}));
            setModified();
        }

        /**
         * Gets the array of two {@code boolean} that specified whether to extend the shading
         * beyond the starting and ending circles of the axis, respectively.
         *
         * @return {@code boolean[]} of Extended object ([false false] by default)
         */
        public boolean[] getExtend() {
            PdfArray extend = getPdfObject().getAsArray(PdfName.Extend);
            if (extend == null)
                return new boolean[] {false, false};
            return new boolean[] {extend.getAsBoolean(0).getValue(), extend.getAsBoolean(1).getValue()};
        }

        /**
         * Sets the Extend object with the two {@code boolean} value.
         *
         * @param extendStart if true will extend shading beyond the starting circle of Coords.
         * @param extendEnd if true will extend shading beyond the ending circle of Coords.
         */
        public void setExtend(boolean extendStart, boolean extendEnd) {
            getPdfObject().put(PdfName.Extend, new PdfArray(new boolean[] {extendStart, extendEnd}));
            setModified();
        }
    }

    /**
     * The class that extends {@link PdfShading} class and is in charge of Shading Dictionary with
     * free-form Gouraud-shaded triangle mesh type.
     * The area to be shaded is defined by a path composed entirely of triangles.
     * The colour at each vertex of the triangles is specified,
     * and a technique known as Gouraud interpolation is used to colour the interiors.
     */
    public static class FreeFormGouraudShadedTriangleMesh extends PdfShading {
        
    	private static final long serialVersionUID = -2690557760051875972L;

        /**
         * Creates the new instance of the class from the existing {@link PdfStream} object.
         *
         * @deprecated Intended only for private use.
         * You should use {@link PdfShading#makeShading(PdfDictionary)} instead.
         *
         * @param pdfStream - {@link PdfStream} from which the instance is created.
         */
		public FreeFormGouraudShadedTriangleMesh(PdfStream pdfStream) {
            super(pdfStream);
        }

        public FreeFormGouraudShadedTriangleMesh(PdfColorSpace cs, int bitsPerCoordinate, int bitsPerComponent, int bitsPerFlag, float[] decode) {
            this(cs, bitsPerCoordinate, bitsPerComponent, bitsPerFlag, new PdfArray(decode));
        }

        public FreeFormGouraudShadedTriangleMesh(PdfColorSpace cs, int bitsPerCoordinate, int bitsPerComponent, int bitsPerFlag, PdfArray decode) {
            super(new PdfStream(), ShadingType.FREE_FORM_GOURAUD_SHADED_TRIANGLE_MESH, cs.getPdfObject());

            setBitsPerCoordinate(bitsPerCoordinate);
            setBitsPerComponent(bitsPerComponent);
            setBitsPerFlag(bitsPerFlag);
            setDecode(decode);
        }

        public int getBitsPerCoordinate() {
            return (int) getPdfObject().getAsInt(PdfName.BitsPerCoordinate);
        }

        public void setBitsPerCoordinate(int bitsPerCoordinate) {
            getPdfObject().put(PdfName.BitsPerCoordinate, new PdfNumber(bitsPerCoordinate));
            setModified();
        }

        public int getBitsPerComponent() {
            return (int) getPdfObject().getAsInt(PdfName.BitsPerComponent);
        }

        public void setBitsPerComponent(int bitsPerComponent) {
            getPdfObject().put(PdfName.BitsPerComponent, new PdfNumber(bitsPerComponent));
            setModified();
        }

        public int getBitsPerFlag() {
            return (int) getPdfObject().getAsInt(PdfName.BitsPerFlag);
        }

        public void setBitsPerFlag(int bitsPerFlag) {
            getPdfObject().put(PdfName.BitsPerFlag, new PdfNumber(bitsPerFlag));
            setModified();
        }

        public PdfArray getDecode() {
            return getPdfObject().getAsArray(PdfName.Decode);
        }

        public void setDecode(float[] decode) {
            getPdfObject().put(PdfName.Decode, new PdfArray(decode));
        }

        public void setDecode(PdfArray decode) {
            getPdfObject().put(PdfName.Decode, decode);
        }
    }

    public static class LatticeFormGouraudShadedTriangleMesh extends PdfShading {
        
    	private static final long serialVersionUID = -8776232978423888214L;

        /**
         * Creates the new instance of the class from the existing {@link PdfStream} object.
         *
         * @deprecated Intended only for private use.
         * You should use {@link PdfShading#makeShading(PdfDictionary)} instead.
         *
         * @param pdfStream - {@link PdfStream} from which the instance is created.
         */
		public LatticeFormGouraudShadedTriangleMesh(PdfStream pdfStream) {
            super(pdfStream);
        }

        public LatticeFormGouraudShadedTriangleMesh(PdfColorSpace cs, int bitsPerCoordinate, int bitsPerComponent, int verticesPerRow, float[] decode) {
            this(cs, bitsPerCoordinate, bitsPerComponent, verticesPerRow, new PdfArray(decode));
        }

        public LatticeFormGouraudShadedTriangleMesh(PdfColorSpace cs, int bitsPerCoordinate, int bitsPerComponent, int verticesPerRow, PdfArray decode) {
            super(new PdfStream(), ShadingType.LATTICE_FORM_GOURAUD_SHADED_TRIANGLE_MESH, cs.getPdfObject());

            setBitsPerCoordinate(bitsPerCoordinate);
            setBitsPerComponent(bitsPerComponent);
            setVerticesPerRow(verticesPerRow);
            setDecode(decode);
        }

        public int getBitsPerCoordinate() {
            return (int) getPdfObject().getAsInt(PdfName.BitsPerCoordinate);
        }

        public void setBitsPerCoordinate(int bitsPerCoordinate) {
            getPdfObject().put(PdfName.BitsPerCoordinate, new PdfNumber(bitsPerCoordinate));
            setModified();
        }

        public int getBitsPerComponent() {
            return (int) getPdfObject().getAsInt(PdfName.BitsPerComponent);
        }

        public void setBitsPerComponent(int bitsPerComponent) {
            getPdfObject().put(PdfName.BitsPerComponent, new PdfNumber(bitsPerComponent));
            setModified();
        }

        public int getVerticesPerRow() {
            return (int) getPdfObject().getAsInt(PdfName.VerticesPerRow);
        }

        public void setVerticesPerRow(int verticesPerRow) {
            getPdfObject().put(PdfName.VerticesPerRow, new PdfNumber(verticesPerRow));
            setModified();
        }

        public PdfArray getDecode() {
            return getPdfObject().getAsArray(PdfName.Decode);
        }

        public void setDecode(float[] decode) {
            getPdfObject().put(PdfName.Decode, new PdfArray(decode));
        }

        public void setDecode(PdfArray decode) {
            getPdfObject().put(PdfName.Decode, decode);
        }
    }

    public static class CoonsPatchMesh extends PdfShading {
        
    	private static final long serialVersionUID = 7296891352801419708L;

        /**
         * Creates the new instance of the class from the existing {@link PdfStream} object.
         *
         * @deprecated Intended only for private use.
         * You should use {@link PdfShading#makeShading(PdfDictionary)} instead.
         *
         * @param pdfStream - {@link PdfStream} from which the instance is created.
         */
		public CoonsPatchMesh(PdfStream pdfStream) {
            super(pdfStream);
        }

        public CoonsPatchMesh(PdfColorSpace cs, int bitsPerCoordinate, int bitsPerComponent, int bitsPerFlag, float[] decode) {
            this(cs, bitsPerCoordinate, bitsPerComponent, bitsPerFlag, new PdfArray(decode));
        }

        public CoonsPatchMesh(PdfColorSpace cs, int bitsPerCoordinate, int bitsPerComponent, int bitsPerFlag, PdfArray decode) {
            super(new PdfStream(), ShadingType.COONS_PATCH_MESH, cs.getPdfObject());
            setBitsPerCoordinate(bitsPerCoordinate);
            setBitsPerComponent(bitsPerComponent);
            setBitsPerFlag(bitsPerFlag);
            setDecode(decode);
        }

        public int getBitsPerCoordinate() {
            return (int) getPdfObject().getAsInt(PdfName.BitsPerCoordinate);
        }

        public void setBitsPerCoordinate(int bitsPerCoordinate) {
            getPdfObject().put(PdfName.BitsPerCoordinate, new PdfNumber(bitsPerCoordinate));
            setModified();
        }

        public int getBitsPerComponent() {
            return (int) getPdfObject().getAsInt(PdfName.BitsPerComponent);
        }

        public void setBitsPerComponent(int bitsPerComponent) {
            getPdfObject().put(PdfName.BitsPerComponent, new PdfNumber(bitsPerComponent));
            setModified();
        }

        public int getBitsPerFlag() {
            return (int) getPdfObject().getAsInt(PdfName.BitsPerFlag);
        }

        public void setBitsPerFlag(int bitsPerFlag) {
            getPdfObject().put(PdfName.BitsPerFlag, new PdfNumber(bitsPerFlag));
            setModified();
        }

        public PdfArray getDecode() {
            return getPdfObject().getAsArray(PdfName.Decode);
        }

        public void setDecode(float[] decode) {
            getPdfObject().put(PdfName.Decode, new PdfArray(decode));
        }

        public void setDecode(PdfArray decode) {
            getPdfObject().put(PdfName.Decode, decode);
        }
    }

    public static class TensorProductPatchMesh extends PdfShading {
       
    	private static final long serialVersionUID = -2750695839303504742L;

        /**
         * Creates the new instance of the class from the existing {@link PdfStream} object.
         *
         * @deprecated Intended only for private use.
         * You should use {@link PdfShading#makeShading(PdfDictionary)} instead.
         *
         * @param pdfStream - {@link PdfStream} from which the instance is created.
         */
		public TensorProductPatchMesh(PdfStream pdfStream) {
            super(pdfStream);
        }

        public TensorProductPatchMesh(PdfColorSpace cs, int bitsPerCoordinate, int bitsPerComponent, int bitsPerFlag, float[] decode) {
            this(cs, bitsPerCoordinate, bitsPerComponent, bitsPerFlag, new PdfArray(decode));
        }

        public TensorProductPatchMesh(PdfColorSpace cs, int bitsPerCoordinate, int bitsPerComponent, int bitsPerFlag, PdfArray decode) {
            super(new PdfStream(), ShadingType.TENSOR_PRODUCT_PATCH_MESH, cs.getPdfObject());

            setBitsPerCoordinate(bitsPerCoordinate);
            setBitsPerComponent(bitsPerComponent);
            setBitsPerFlag(bitsPerFlag);
            setDecode(decode);
        }

        public int getBitsPerCoordinate() {
            return (int) getPdfObject().getAsInt(PdfName.BitsPerCoordinate);
        }

        public void setBitsPerCoordinate(int bitsPerCoordinate) {
            getPdfObject().put(PdfName.BitsPerCoordinate, new PdfNumber(bitsPerCoordinate));
            setModified();
        }

        public int getBitsPerComponent() {
            return (int) getPdfObject().getAsInt(PdfName.BitsPerComponent);
        }

        public void setBitsPerComponent(int bitsPerComponent) {
            getPdfObject().put(PdfName.BitsPerComponent, new PdfNumber(bitsPerComponent));
            setModified();
        }

        public int getBitsPerFlag() {
            return (int) getPdfObject().getAsInt(PdfName.BitsPerFlag);
        }

        public void setBitsPerFlag(int bitsPerFlag) {
            getPdfObject().put(PdfName.BitsPerFlag, new PdfNumber(bitsPerFlag));
            setModified();
        }

        public PdfArray getDecode() {
            return getPdfObject().getAsArray(PdfName.Decode);
        }

        public void setDecode(float[] decode) {
            getPdfObject().put(PdfName.Decode, new PdfArray(decode));
        }

        public void setDecode(PdfArray decode) {
            getPdfObject().put(PdfName.Decode, decode);
        }
    }
}
