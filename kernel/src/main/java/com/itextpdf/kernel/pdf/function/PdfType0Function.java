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
package com.itextpdf.kernel.pdf.function;

import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.colorspace.PdfColorSpace;
import com.itextpdf.kernel.pdf.function.utils.AbstractSampleExtractor;

import java.util.Arrays;

public class PdfType0Function extends AbstractPdfFunction<PdfStream> {

    private int[] size;

    private int order;
    private int[] encode;
    private double[] decode;

    private int bitsPerSample;
    private AbstractSampleExtractor sampleExtractor = null;
    private byte[] samples;

    private int outputDimension;
    private long decodeLimit;

    private boolean isValidated = false;
    private String errorMessage = null;

    private double[][] derivatives = null;

    public PdfType0Function(PdfStream pdfObject) {
        super(pdfObject);

        final PdfArray sizeObj = pdfObject.getAsArray(PdfName.Size);
        if (super.getDomain() == null || super.getRange() == null || sizeObj == null) {
            setErrorMessage(KernelExceptionMessageConstant.PDF_TYPE0_FUNCTION_NOT_NULL_PARAMETERS);
            return;
        }

        size = sizeObj.toIntArray();

        final PdfNumber orderObj = pdfObject.getAsNumber(PdfName.Order);
        order = orderObj == null ? 1 : orderObj.intValue();

        final PdfArray encodeObj = pdfObject.getAsArray(PdfName.Encode);
        initializeEncoding(encodeObj);

        final PdfArray decodeObj = pdfObject.getAsArray(PdfName.Decode);
        if (decodeObj == null) {
            decode = super.getRange();
        } else {
            decode = decodeObj.toDoubleArray();
        }
        outputDimension = super.getRange().length >> 1;

        final PdfNumber bitsPerSampleObj = pdfObject.getAsNumber(PdfName.BitsPerSample);
        bitsPerSample = bitsPerSampleObj == null ? 0 : bitsPerSampleObj.intValue();

        decodeLimit = (1L << bitsPerSample) - 1;
        samples = pdfObject.getBytes(true);
        try {
            sampleExtractor = AbstractSampleExtractor.createExtractor(bitsPerSample);
        } catch (IllegalArgumentException e) {
            setErrorMessage(e.getMessage());
        }


    }

    public PdfType0Function(double[] domain, int[] size, double[] range, int order, int bitsPerSample, byte[] samples) {
        this(domain, size, range, order, null, null, bitsPerSample, samples);
    }

    public PdfType0Function(float[] domain, int[] size, float[] range, int order, int bitsPerSample, byte[] samples) {
        this(convertFloatArrayToDoubleArray(domain), size, convertFloatArrayToDoubleArray(range), order,
                bitsPerSample, samples);
    }

    public PdfType0Function(double[] domain, int[] size, double[] range, int order,
            int[] encode, double[] decode, int bitsPerSample, byte[] samples) {
        super(new PdfStream(samples), PdfFunctionFactory.FUNCTION_TYPE_0, domain, range);
        if (size != null) {
            this.size = Arrays.copyOf(size, size.length);
        }

        if (super.getDomain() == null || super.getRange() == null || size == null) {
            setErrorMessage(KernelExceptionMessageConstant.PDF_TYPE0_FUNCTION_NOT_NULL_PARAMETERS);
            return;
        }

        this.size = Arrays.copyOf(size, size.length);
        super.getPdfObject().put(PdfName.Size, new PdfArray(size));

        this.order = order;
        super.getPdfObject().put(PdfName.Order, new PdfNumber(order));

        initializeEncoding(encode);
        super.getPdfObject().put(PdfName.Encode, new PdfArray(this.encode));

        if (decode == null) {
            this.decode = Arrays.copyOf(range, range.length);
        } else {
            this.decode = Arrays.copyOf(decode, decode.length);
        }
        super.getPdfObject().put(PdfName.Decode, new PdfArray(this.decode));

        this.bitsPerSample = bitsPerSample;
        super.getPdfObject().put(PdfName.BitsPerSample, new PdfNumber(bitsPerSample));

        this.outputDimension = super.getRange().length >> 1;
        this.decodeLimit = (1L << bitsPerSample) - 1;
        this.samples = Arrays.copyOf(samples, samples.length);
        try {
            sampleExtractor = AbstractSampleExtractor.createExtractor(bitsPerSample);
        } catch (IllegalArgumentException e) {
            setErrorMessage(e.getMessage());
        }
        if (isInvalid()) {
            throw new IllegalArgumentException(errorMessage);
        }
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
        getPdfObject().put(PdfName.Order, new PdfNumber(order));
        isValidated = false;
    }

    public int[] getSize() {
        return size;
    }

    public void setSize(int[] size) {
        this.size = size;
        getPdfObject().put(PdfName.Size, new PdfArray(size));
        isValidated = false;
    }

    public int[] getEncode() {
        return encode;
    }

    public void setEncode(int[] encode) {
        initializeEncoding(encode);
        getPdfObject().put(PdfName.Encode, new PdfArray(encode));
        isValidated = false;
    }

    public double[] getDecode() {
        return decode;
    }

    public void setDecode(double[] decode) {
        this.decode = decode;
        getPdfObject().put(PdfName.Decode, new PdfArray(decode));
        isValidated = false;
    }

    @Override
    public boolean checkCompatibilityWithColorSpace(PdfColorSpace alternateSpace) {
        return getInputSize() == 1 && getOutputSize() == alternateSpace.getNumberOfComponents();
    }

    @Override
    public void setDomain(double[] domain) {
        super.setDomain(domain);
        isValidated = false;
    }

    @Override
    public void setRange(double[] range) {
        super.setRange(range);
        isValidated = false;
    }

    @Override
    public double[] calculate(double[] input) {
        if (isInvalid()) {
            throw new IllegalArgumentException(errorMessage);
        }

        double[] normal = normalize(input, getDomain());
        int[] floor = getFloor(normal, encode);

        double[] result;

        if (order == 3 && size.length == 1 && encode[1] - encode[0] > 1) {
            result = interpolateByCubicSpline(normal[0], floor[0]);
        } else {
            result = interpolate(normal, floor);
        }

        return clip(result, getRange());
    }

    /**
     * Encode normalized input value.
     *
     * @param normal    input normalized value
     * @param encodeMin encode min value
     * @param encodeMax encode max value
     *
     * @return encoded value
     */
    static double encode(double normal, int encodeMin, int encodeMax) {
        return encodeMin + normal * (encodeMax - encodeMin);
    }

    /**
     * Calculates floor sample coordinates for the normalized  input array.
     *
     * @param normal input array normalized to domain
     * @param encode encode mapping
     *
     * @return encoded sample coordinates of the nearest left interpolation point
     */
    static int[] getFloor(double[] normal, int[] encode) {
        int[] result = new int[normal.length];
        for (int i = 0; i < normal.length; ++i) {
            final int j = i << 1;
            final int floor = (int) encode(normal[i], encode[j], encode[j + 1]);

            result[i] = Math.min(Math.max(0, encode[j + 1] - 1), floor);
        }
        return result;
    }

    /**
     * Maps sample coordinates to linear position in samples table.
     *
     * @param sample sample encoded coordinates
     * @param size   number of samples in each input dimension
     *
     * @return position in samples table
     */
    static int getSamplePosition(int[] sample, int[] size) {
        int position = sample[size.length - 1];
        for (int i = size.length - 2; i >= 0; --i) {
            position = sample[i] + size[i] * position;
        }
        return position;
    }

    /**
     * Calculated component-by-component normalized distances from input array to the nearest left interpolation point.
     * Input array shall be normalized to domain
     *
     * @param normal input array normalized to domain
     * @param encode encode mapping
     *
     * @return component-by-component normalized distances from input array to the nearest left interpolation point
     */
    static double[] getFloorWeights(double[] normal, int[] encode) {
        double[] result = new double[normal.length];
        for (int i = 0; i < normal.length; i++) {
            result[i] = getFloorWeight(normal[i], encode[2 * i], encode[2 * i + 1]);
        }
        return result;
    }

    /**
     * Calculates normalized distance from input value to the nearest left interpolation point.
     * Input value shall be normalized to domain component
     *
     * @param normal    input value normalized to domain component
     * @param encodeMin encode min value
     * @param encodeMax encode max value
     *
     * @return normalized distance from input value to the nearest left interpolation point
     */
    static double getFloorWeight(double normal, int encodeMin, int encodeMax) {
        final double value = encode(normal, encodeMin, encodeMax);
        return value - Math.min(encodeMax - 1, (int) value);
    }

    /**
     * Solves the system of linear equations by sweep method where the matrix is 3-diagonal.
     * Main diagonal elements are 4, lower and upper diagonals: 1.
     *
     * <p>
     * x[0] = 0,
     * x[0]   + 4*x[1] + x[2]   = f[0],
     * x[1]   + 4*x[2] + x[3]   = f[1],
     * ...
     * x[n-1] + 4*x[n] + x[n+1] = f[n-1],
     * x[n] = 0
     *
     * @param f right hand side
     *
     * @return solution, first and last values are zeroes
     */
    static double[] specialSweepMethod(double[] f) {
        assert (f.length > 0);

        double[] x = new double[f.length + 2];
        x[1] = 4;
        for (int i = 1; i < f.length; ++i) {
            x[0] = 1 / x[i];
            x[i + 1] = 4 - x[0];
            f[i] = f[i] - x[0] * f[i - 1];
        }

        x[f.length] = f[f.length - 1] / x[f.length];
        for (int i = f.length - 1; i > 0; --i) {
            x[i] = (f[i - 1] - x[i + 1]) / x[i];
        }

        x[0] = x[x.length - 1] = 0;
        return x;
    }

    private void initializeEncoding(PdfArray encodeObj) {
        if (encodeObj == null) {
            encode = getDefaultEncoding();
        } else {
            encode = encodeObj.toIntArray();
            for (int i = 0; i < size.length; ++i) {
                final int j = i << 1;
                encode[j] = Math.max(0, encode[j]);
                encode[j + 1] = Math.min(size[i] - 1, encode[j + 1]);
            }
        }
    }

    private void initializeEncoding(int[] encode) {
        if (encode == null) {
            this.encode = getDefaultEncoding();
        } else {
            this.encode = new int[encode.length];
            for (int i = 0; i < size.length; ++i) {
                final int j = i << 1;
                this.encode[j] = Math.max(0, encode[j]);
                this.encode[j + 1] = Math.min(size[i] - 1, encode[j + 1]);
            }
        }
    }

    private int[] getDefaultEncoding() {
        int[] result = new int[this.size.length << 1];
        int i = 0;
        for (final int sizeItem : size) {
            result[i++] = 0;
            result[i++] = sizeItem - 1;
        }
        return result;
    }

    private double[] interpolate(double[] normal, int[] floor) {
        final int floorPosition = getSamplePosition(floor, size);
        final double[] x = getFloorWeights(normal, encode);
        final int[] steps = getInputDimensionSteps();
        double[] result = new double[outputDimension];
        switch (order) {
            case 1:
                for (int dim = 0; dim < outputDimension; dim++) {
                    result[dim] = interpolateOrder1(x, floorPosition, steps, steps.length, dim);
                }
                return result;
            case 3:
                for (int dim = 0; dim < outputDimension; dim++) {
                    result[dim] = interpolateOrder3(x, floor, floorPosition, steps, steps.length, dim);
                }
                return result;
            default:
                throw new PdfException(KernelExceptionMessageConstant.PDF_TYPE0_FUNCTION_INVALID_ORDER);
        }
    }

    private double interpolateOrder1(double[] x, int floorPosition, int[] steps, int inDim, int outDim) {
        if (inDim == 0) {
            return getValue(outDim, floorPosition);
        }
        final int step = steps[--inDim];
        int encodeIndex = inDim << 1;

        final double value0 = interpolateOrder1(x, floorPosition, steps, inDim, outDim);
        if (encode[encodeIndex] == encode[encodeIndex + 1]) {
            return value0;
        }

        final int ceilPosition = floorPosition + step;
        final double value1 = interpolateOrder1(x, ceilPosition, steps, inDim, outDim);

        return calculateLinearInterpolationFormula(x[inDim], value0, value1);
    }

    private double interpolateOrder3(double[] x, int[] floor, int floorPosition, int[] steps, int inDim, int outDim) {
        if (inDim == 0) {
            return getValue(outDim, floorPosition);
        }
        int step = steps[--inDim];
        int encodeIndex = inDim << 1;

        double value1 = interpolateOrder3(x, floor, floorPosition, steps, inDim, outDim);
        if (encode[encodeIndex] == encode[encodeIndex + 1]) {
            return value1;
        }

        int ceilPosition = floorPosition + step;
        double value2 = interpolateOrder3(x, floor, ceilPosition, steps, inDim, outDim);

        if (encode[encodeIndex + 1] - encode[encodeIndex] == 1) {
            return calculateLinearInterpolationFormula(x[inDim], value1, value2);
        }

        double value0;
        if (floor[inDim] > encode[encodeIndex]) {
            value0 = interpolateOrder3(x, floor, floorPosition - step, steps, inDim, outDim);
        } else {
            value0 = 2 * value1 - value2;
        }

        double value3;
        if (floor[inDim] < encode[encodeIndex + 1] - encode[encodeIndex] - 1) {
            value3 = interpolateOrder3(x, floor, ceilPosition + step, steps, inDim, outDim);
        } else {
            value3 = 2 * value2 - value1;
        }

        return calculateCubicInterpolationFormula(x[inDim], value0, value1, value2, value3);
    }

    private double[] interpolateByCubicSpline(double normal, int position) {
        if (derivatives == null) {
            calculateSecondDerivatives();
        }

        double x = getFloorWeight(normal, encode[0], encode[1]);
        return calculateCubicSplineFormula(x, position);
    }

    private double[] calculateCubicSplineFormula(double x, int position) {
        double[] result = new double[outputDimension];
        for (int dim = 0; dim < outputDimension; dim++) {
            result[dim] = calculateCubicSplineFormula(x,
                    getValue(dim, position), getValue(dim, position + 1),
                    derivatives[dim][position - encode[0]], derivatives[dim][position - encode[0] + 1]);
        }
        return result;
    }

    /**
     * Calculates second derivatives at each interpolation point by sweep method with 3-diagonal matrix.
     */
    private void calculateSecondDerivatives() {
        derivatives = new double[outputDimension][];
        for (int dim = 0; dim < outputDimension; ++dim) {
            double[] f = new double[encode[1] - encode[0] - 1];
            for (int pos = encode[0]; pos < encode[1] - 1; ++pos) {
                f[pos - encode[0]] = 6 * (getValue(dim, pos) - 2 * getValue(dim, pos + 1) + getValue(dim, pos + 2));
            }
            derivatives[dim] = specialSweepMethod(f);
        }
    }

    /**
     * Calculates function decoded values.
     *
     * <p>
     * Function values are stored sequentially in samples table. For a function with multidimensional input
     * (more than one input variables), the sample values in the first dimension vary fastest,
     * and the values in the last dimension vary slowest. Order example for size array [4, 4, 4]:
     * f(0,0,0), f(1,0,0), f(2,0,0), f(3,0,0), f(0,1,0), f(1,1,0), ..., f(3,3,0), f(3,3,1), f(3,3,2), f(3,3,3).
     * For example in this case f(1,1,0) has position 5.
     * If the function has multiple output values each value shall occupy bitsPerSample bits and
     * stored sequentially as well.
     *
     * @param dim output dimension coordinate (values from [0, ..., outputDimension - 1])
     * @param pos position in samples table
     *
     * @return function decoded value
     */
    private double getValue(int dim, int pos) {
        return decode(sampleExtractor.extract(samples, dim + outputDimension * pos), dim);
    }

    /**
     * Gets a minimal distance between samples of same dimension in samples table for each dimension.
     *
     * @return for each dimension a minimal distance between samples of same dimension in samples table
     */
    private int[] getInputDimensionSteps() {
        int[] steps = new int[size.length];
        steps[0] = 1;
        for (int i = 1; i < steps.length; ++i) {
            steps[i] = steps[i - 1] * size[i - 1];
        }
        return steps;
    }

    /**
     * Decode sampled value.
     *
     * @param x   sampled value
     * @param dim output dimension coordinate (values from [0, ..., outputDimension - 1])
     *
     * @return decoded value
     */
    private double decode(long x, int dim) {
        final int index = dim << 1;
        return decode[index] + (decode[index + 1] - decode[index]) * x / decodeLimit;
    }

    private void setErrorMessage(String message) {
        errorMessage = message;
        isValidated = true;
    }

    private boolean isInvalid() {
        if (isValidated) {
            return errorMessage != null;
        }
        if (super.getDomain() == null || super.getRange() == null || size == null) {
            setErrorMessage(KernelExceptionMessageConstant.PDF_TYPE0_FUNCTION_NOT_NULL_PARAMETERS);
            return true;
        }
        if (order != 1 && order != 3) {
            setErrorMessage(KernelExceptionMessageConstant.PDF_TYPE0_FUNCTION_INVALID_ORDER);
            return true;
        }
        if (getDomain().length == 0 || getDomain().length % 2 == 1) {
            setErrorMessage(KernelExceptionMessageConstant.PDF_TYPE0_FUNCTION_INVALID_DOMAIN);
            return true;
        }
        if (getRange().length == 0 || getRange().length % 2 == 1) {
            setErrorMessage(KernelExceptionMessageConstant.PDF_TYPE0_FUNCTION_INVALID_RANGE);
            return true;
        }

        final int inputDimension = getDomain().length >> 1;
        if (size == null || size.length != inputDimension) {
            setErrorMessage(KernelExceptionMessageConstant.PDF_TYPE0_FUNCTION_INVALID_SIZE);
            return true;
        }
        for (final int s : size) {
            if (s <= 0) {
                setErrorMessage(KernelExceptionMessageConstant.PDF_TYPE0_FUNCTION_INVALID_SIZE);
                return true;
            }
        }
        if (encode.length != getDomain().length) {
            setErrorMessage(KernelExceptionMessageConstant.PDF_TYPE0_FUNCTION_INVALID_ENCODE);
            return true;
        }
        for (int i = 0; i < encode.length; i += 2) {
            if (encode[i + 1] < encode[i]) {
                setErrorMessage(KernelExceptionMessageConstant.PDF_TYPE0_FUNCTION_INVALID_ENCODE);
                return true;
            }
        }
        if (decode.length != getRange().length) {
            setErrorMessage(KernelExceptionMessageConstant.PDF_TYPE0_FUNCTION_INVALID_DECODE);
            return true;
        }
        final int samplesMinLength = (Arrays.stream(size).reduce(
                outputDimension * bitsPerSample, (x, y) -> x * y) + 7) / 8;
        if (samples == null || samples.length < samplesMinLength) {
            setErrorMessage(
                    KernelExceptionMessageConstant.PDF_TYPE0_FUNCTION_INVALID_SAMPLES);
            return true;
        }

        isValidated = true;
        return false;
    }

    /**
     * Interpolates function by linear interpolation formula using function values at neighbouring points.
     *
     * @param x  input normalized to [0, 1] by neighbouring points
     * @param f0 function value at the left neighbouring point
     * @param f1 function value at the right neighbouring point
     *
     * @return function value obtained by linear interpolation
     */
    private static double calculateLinearInterpolationFormula(double x, double f0, double f1) {
        return (1.0 - x) * f0 + x * f1;
    }

    /**
     * Interpolates function by cubic interpolation formula using function values at neighbouring points.
     *
     * @param x  input normalized to [0, 1] by neighbouring points
     * @param f0 function value at the next to left neighbouring point
     * @param f1 function value at the left neighbouring point
     * @param f2 function value at the right neighbouring point
     * @param f3 function value at the next to right neighbouring point
     *
     * @return function value obtained by cubic interpolation
     */
    private static double calculateCubicInterpolationFormula(double x, double f0, double f1, double f2, double f3) {
        return f1 + 0.5 * x * (f2 - f0 + x * (2 * f0 - 5 * f1 + 4 * f2 - f3 + x * (3 * (f1 - f2) + f3 - f0)));
    }

    /**
     * Interpolates function by cubic spline formula using function and its second derivative values at neighbouring
     * points.
     *
     * @param x  input normalized to [0, 1] by neighbouring points
     * @param f0 function value in the left neighbouring point
     * @param f1 function value in the right neighbouring point
     * @param d0 second derivative value in the left neighbouring point
     * @param d1 second derivative value in the right neighbouring point
     *
     * @return function value interpolated by cubic spline formula
     */
    private static double calculateCubicSplineFormula(double x, double f0, double f1, double d0, double d1) {
        final double y = 1 - x;
        return f1 * x + f0 * y - x * y * (d0 * (y + 1) + d1 * (x + 1)) / 6;
    }
}
