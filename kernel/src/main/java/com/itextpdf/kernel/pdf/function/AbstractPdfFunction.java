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
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfObjectWrapper;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.colorspace.PdfColorSpace;
import com.itextpdf.kernel.pdf.function.BaseInputOutPutConvertors.IInputConversionFunction;
import com.itextpdf.kernel.pdf.function.BaseInputOutPutConvertors.IOutputConversionFunction;

import java.io.IOException;
import java.util.Arrays;

/**
 * The abstract PdfFunction class that represents the Function Dictionary or Stream PDF object.
 * Holds common properties and methods and a factory method. (see ISO-320001 Chapter 7.10)
 *
 * @param <T> Either a {@link PdfDictionary} or a {@link PdfStream}
 */
public abstract class AbstractPdfFunction<T extends PdfDictionary> extends PdfObjectWrapper<T> implements IPdfFunction {

    private final int functionType;
    private double[] domain;
    private double[] range;

    /**
     * Constructs a PdfFunction from a new PdfObject.
     *
     * @param pdfObject    The new, empty, object, created in a concrete implementation
     * @param functionType The function type, can be 0, 2, 3 or 4
     * @param domain       the valid input domain, input will be clipped to this domain
     *                     contains a min max pair per input component
     * @param range        the valid output range, oputput will be clipped to this range
     *                     contains a min max pair per output component
     */
    protected AbstractPdfFunction(T pdfObject, int functionType, double[] domain, double[] range) {
        super(pdfObject);
        this.functionType = functionType;
        if (domain != null) {
            this.domain = Arrays.copyOf(domain, domain.length);
            pdfObject.put(PdfName.Domain,new PdfArray(domain));
        }
        if (range != null) {
            this.range = Arrays.copyOf(range, range.length);
            pdfObject.put(PdfName.Range,new PdfArray(range));
        }
        pdfObject.put(PdfName.FunctionType,new PdfNumber(functionType));
    }


    /**
     * Constructs a PdfFunction from an existing PdfObject.
     *
     * @param pdfObject Either a {@link PdfDictionary} or a {@link PdfStream}
     */
    protected AbstractPdfFunction(T pdfObject) {
        super(pdfObject);
        final PdfNumber functionTypeObj = pdfObject.getAsNumber(PdfName.FunctionType);
        functionType = functionTypeObj == null ? -1 : functionTypeObj.intValue();
        final PdfArray domainObj = pdfObject.getAsArray(PdfName.Domain);
        domain = domainObj == null ? null : domainObj.toDoubleArray();
        final PdfArray rangeObj = pdfObject.getAsArray(PdfName.Range);
        range = rangeObj == null ? null : rangeObj.toDoubleArray();
    }

    /**
     * The function type, (see ISO-320001 Table 38).
     *
     * @return The function type, either 0, 2, 3 or 4
     */
    @Override
    public int getFunctionType() {
        return functionType;
    }

    /**
     * Chacks wether the output of the function matches in components with the passed by color space.
     *
     * @param alternateSpace The color space to verify against
     *
     * @return True when compatible
     */
    @Override
    public boolean checkCompatibilityWithColorSpace(PdfColorSpace alternateSpace) {
        return getOutputSize() == alternateSpace.getNumberOfComponents();
    }

    /**
     * The number of input components.
     *
     * @return The number of input components
     */
    @Override
    public int getInputSize() {
        return getPdfObject().getAsArray(PdfName.Domain).size() / 2;
    }

    /**
     * The number of output components.
     *
     * @return The number of output components
     */
    @Override
    public int getOutputSize() {
        return range == null ? 0 : (range.length / 2);
    }

    /**
     * The valid input domain, input will be clipped to this domain contains a min max pair per input component.
     *
     * <p>
     * (see ISO-320001 Table 38)
     *
     * @return the input domain
     */
    @Override
    public double[] getDomain() {
        if (domain == null) {
            return null;
        }
        return Arrays.copyOf(domain, domain.length);
    }

    /**
     * The valid input domain, input will be clipped to this domain contains a min max pair per input component.
     *
     * <p>
     * (see ISO-320001 Table 38)
     *
     * @param value the new set of limits
     */
    @Override
    public void setDomain(double[] value) {
        domain = Arrays.copyOf(value, value.length);
        getPdfObject().put(PdfName.Domain, new PdfArray(domain));
    }

    /**
     * the valid output range, output will be clipped to this range contains a min max pair per output component.
     *
     * <p>
     * (see ISO-320001 Table 38)
     *
     * @return the output range
     */
    @Override
    public double[] getRange() {
        if (range != null) {
            return Arrays.copyOf(range, range.length);
        }
        return null;
    }

    /**
     * the valid output range, output will be clipped to this range contains a min max pair per output component.
     *
     * <p>
     * (see ISO-320001 Table 38)
     *
     * @param value the new set of limts
     */
    @Override
    public void setRange(double[] value) {
        if (value == null) {
            getPdfObject().remove(PdfName.Range);
            return;
        }
        range = Arrays.copyOf(value, value.length);
        getPdfObject().put(PdfName.Range, new PdfArray(range));
    }

    /**
     * Performs the calculation in bulk on a set of raw data and returns a new set of raw data.
     *
     * @param bytes                The uninterpreted set of data to be transformed
     * @param offset               Where to start converting the data
     * @param length               How many of the input bytes should be converted
     * @param wordSizeInputLength  How many bytes represents one input value
     * @param wordSizeOutputLength How many bytes represents one output value
     *
     * @return the transformed result as a raw byte array
     *
     * @throws IOException on data reading errors
     */
    @Override
    public byte[] calculateFromByteArray(byte[] bytes, int offset, int length, int wordSizeInputLength,
            int wordSizeOutputLength) throws IOException {
        return calculateFromByteArray(bytes, offset, length, wordSizeInputLength, wordSizeOutputLength, null, null);
    }

    /**
     * Performs the calculation in bulk on a set of raw data and returns a new set of raw data.
     *
     * @param bytes                The uninterpreted set of data to be transformed
     * @param offset               Where to start converting the data
     * @param length               How many of the input bytes should be converted
     * @param wordSizeInputLength  How many bytes represents one input value
     * @param wordSizeOutputLength How many bytes represents one output value
     * @param inputConvertor       a custom input convertor
     * @param outputConvertor      a custom output convertor
     *
     * @return the transformed result as a raw byte array
     *
     * @throws IOException on data reading errors
     */
    @Override
    public byte[] calculateFromByteArray(byte[] bytes, int offset, int length, int wordSizeInputLength,
            int wordSizeOutputLength, IInputConversionFunction inputConvertor,
            IOutputConversionFunction outputConvertor) throws IOException {
        final int bytesPerInputWord = (int) Math.ceil(wordSizeInputLength / 8.0);
        final int bytesPerOutputWord = (int) Math.ceil(wordSizeOutputLength / 8.0);
        final int inputSize = getInputSize();
        final int outputSize = getOutputSize();

        IInputConversionFunction actualInputConvertor = inputConvertor;
        if (actualInputConvertor == null) {
            actualInputConvertor = BaseInputOutPutConvertors.getInputConvertor(bytesPerInputWord, 1);
        }

        IOutputConversionFunction actualOutputConvertor = outputConvertor;
        if (actualOutputConvertor == null) {
            actualOutputConvertor = BaseInputOutPutConvertors.getOutputConvertor(bytesPerOutputWord, 1.0);
        }

        final double[] inValues = actualInputConvertor.convert(bytes, offset, length);
        final double[] outValues = new double[inValues.length / inputSize * outputSize];
        int outIndex = 0;
        for (int i = 0; i < inValues.length; i += inputSize) {
            final double[] singleRes = calculate(Arrays.copyOfRange(inValues, i, i + inputSize));
            System.arraycopy(singleRes, 0, outValues, outIndex, singleRes.length);
            outIndex+= singleRes.length;
        }
        return actualOutputConvertor.convert(outValues);
    }

    /**
     * Clip input values to the allowed domain.
     *
     * <p>
     * (see ISO-320001 Table 38)
     *
     * @param input the input values to be clipped
     *
     * @return the values clipped between the boundaries defined in the domain
     */
    @Override
    public double[] clipInput(double[] input) {
        if (input.length * 2 != domain.length) {
            throw new IllegalArgumentException(KernelExceptionMessageConstant.INPUT_NOT_MULTIPLE_OF_DOMAIN_SIZE);
        }
        return clip(input, domain);
    }

    /**
     * Clip output values to the allowed range, if there is a range.
     *
     * <p>
     * (see ISO-320001 Table 38)
     *
     * @param input the output values to be clipped
     *
     * @return the values clipped between the boundaries defined in the range
     */
    @Override
    public double[] clipOutput(double[] input) {
        if (range == null) {
            return input;
        }
        if (input.length * 2 != range.length) {
            throw new IllegalArgumentException(KernelExceptionMessageConstant.INPUT_NOT_MULTIPLE_OF_RANGE_SIZE);
        }

        return clip(input, range);
    }

    @Override
    public PdfObject getAsPdfObject() {
        return super.getPdfObject();
    }

    @Override
    protected boolean isWrappedObjectMustBeIndirect() {
        return true;
    }

    protected static double[] clip(double[] values, double[] limits) {
        assert (values.length * 2 == limits.length);

        double[] result = new double[values.length];
        int j = 0;
        for (int i = 0; i < values.length; ++i) {
            final double lowerBound = limits[j++];
            final double upperBound = limits[j++];

            result[i] = Math.min(Math.max(lowerBound, values[i]), upperBound);
        }
        return result;
    }

    protected static double[] normalize(double[] values, double[] limits) {
        assert (values.length * 2 == limits.length);

        double[] normal = new double[values.length];
        int j = 0;
        for (int i = 0; i < values.length; ++i) {
            final double lowerBound = limits[j++];
            final double upperBound = Math.max(lowerBound + Double.MIN_VALUE, limits[j++]);

            normal[i] = Math.min(Math.max(0, (values[i] - lowerBound) / (upperBound - lowerBound)), 1);
        }
        return normal;
    }

    protected static double[] convertFloatArrayToDoubleArray(float[] array) {
        if (array == null) {
            return null;
        }
        double[] arrayDouble = new double[array.length];
        for (int i = 0; i < array.length; i++) {
            arrayDouble[i] = array[i];
        }
        return arrayDouble;
    }
}
