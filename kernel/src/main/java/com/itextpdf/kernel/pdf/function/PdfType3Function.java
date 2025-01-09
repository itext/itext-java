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
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.colorspace.PdfColorSpace;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * This class represents Pdf type 3 function that defines a stitching of the subdomains
 * of several 1-input functions to produce a single new 1-input function.
 *
 * <p>
 * For more info see ISO 32000-1, section 7.10.4 "Type 3 (Stitching) Functions".
 */
public class PdfType3Function extends AbstractPdfFunction<PdfDictionary> {

    private static final IPdfFunctionFactory DEFAULT_FUNCTION_FACTORY = (PdfObject pdfObject) ->
    {
        return PdfFunctionFactory.create(pdfObject);
    };
    private final IPdfFunctionFactory functionFactory;

    private List<IPdfFunction> functions;

    private double[] bounds;

    private double[] encode;


    /**
     * Instantiates a new PdfType3Function instance based on passed PdfDictionary instance.
     *
     * @param dict the function dictionary
     */
    public PdfType3Function(PdfDictionary dict) {
        this(dict, DEFAULT_FUNCTION_FACTORY);
    }

    /**
     * (see ISO-320001 Table 41).
     *
     * @param domain    the valid input domain, input will be clipped to this domain
     *                  contains a min max pair per input component
     * @param range     the valid output range, oputput will be clipped to this range
     *                  contains a min max pair per output component
     * @param functions The list of functions to stitch*
     * @param bounds    (Required) An array of k − 1 numbers that, in combination with Domain, shall define
     *                  the intervals to which each function from the Functions array shall apply.
     *                  Bounds elements shall be in order of increasing value, and each value shall be within
     *                  the domain defined by Domain.
     * @param encode    (Required) An array of 2 × k numbers that, taken in pairs, shall map each subset of the domain
     *                  defined by Domain and the Bounds array to the domain of the corresponding function.
     */
    public PdfType3Function(double[] domain, double[] range,
            List<AbstractPdfFunction<? extends PdfDictionary>> functions, double[] bounds, double[] encode) {
        super(new PdfDictionary(), PdfFunctionFactory.FUNCTION_TYPE_3, domain, range);
        functionFactory = DEFAULT_FUNCTION_FACTORY;
        final PdfArray funcs = new PdfArray();
        for (final AbstractPdfFunction<? extends PdfDictionary> func : functions) {
            funcs.add(func.getPdfObject());
        }
        super.getPdfObject().put(PdfName.Functions, funcs);
        super.getPdfObject().put(PdfName.Bounds, new PdfArray(bounds));
        super.getPdfObject().put(PdfName.Encode, new PdfArray(encode));
    }

    /**
     * (see ISO-320001 Table 41).
     *
     * @param domain    the valid input domain, input will be clipped to this domain
     *                  contains a min max pair per input component
     * @param range     the valid output range, oputput will be clipped to this range
     *                  contains a min max pair per output component
     * @param functions The list of functions to stitch*
     * @param bounds    (Required) An array of k − 1 numbers that, in combination with Domain, shall define
     *                  the intervals to which each function from the Functions array shall apply.
     *                  Bounds elements shall be in order of increasing value, and each value shall be within
     *                  the domain defined by Domain.
     * @param encode    (Required) An array of 2 × k numbers that, taken in pairs, shall map each subset of the domain
     *                  defined by Domain and the Bounds array to the domain of the corresponding function.
     */
    public PdfType3Function(float[] domain, float[] range,
            List<AbstractPdfFunction<? extends PdfDictionary>> functions, float[] bounds, float[] encode) {
        this(convertFloatArrayToDoubleArray(domain), convertFloatArrayToDoubleArray(range),
                functions, convertFloatArrayToDoubleArray(bounds), convertFloatArrayToDoubleArray(encode));
    }


    PdfType3Function(PdfDictionary dict, IPdfFunctionFactory functionFactory) {
        super(dict);
        this.functionFactory = functionFactory;

        final PdfArray functionsArray = dict.getAsArray(PdfName.Functions);
        functions = Collections.unmodifiableList(checkAndGetFunctions(functionsArray));

        if (super.getDomain().length < 2) {
            throw new PdfException(KernelExceptionMessageConstant.INVALID_TYPE_3_FUNCTION_DOMAIN);
        }

        final PdfArray boundsArray = dict.getAsArray(PdfName.Bounds);
        bounds = checkAndGetBounds(boundsArray);

        final PdfArray encodeArray = dict.getAsArray(PdfName.Encode);
        encode = checkAndGetEncode(encodeArray);
    }

    /**
     * (Required) An array of k 1-input functions that shall make up the stitching function.
     * The output dimensionality of all functions shall be the same, and compatible with the value
     * of Range if Range is present.
     *
     * <p>
     * (see ISO-320001 Table 41)
     *
     * @return the list of functions
     */
    public Collection<IPdfFunction> getFunctions() {
        return functions;
    }

    /**
     * (Required) An array of k 1-input functions that shall make up the stitching function.
     * The output dimensionality of all functions shall be the same, and compatible with the value
     * of Range if Range is present.
     *
     * <p>
     * (see ISO-320001 Table 41)
     *
     * @param value the list of functions
     */
    public void setFunctions(Iterable<AbstractPdfFunction<? extends PdfDictionary>> value) {
        final PdfArray pdfFunctions = new PdfArray();
        for (final AbstractPdfFunction<? extends PdfDictionary> f : value) {
            pdfFunctions.add(f.getPdfObject().getIndirectReference());
        }
        getPdfObject().put(PdfName.Functions, pdfFunctions);
    }

    /**
     * An array of k − 1 numbers that, in combination with Domain, shall define
     * the intervals to which each function from the Functions array shall apply.
     * Bounds elements shall be in order of increasing value, and each value shall be within
     * the domain defined by Domain.
     *
     * <p>
     * (see ISO-320001 Table 41)
     *
     * @return the bounds
     */
    public double[] getBounds() {
        return bounds;
    }

    /**
     * (Required) An array of k − 1 numbers that, in combination with Domain, shall define
     * the intervals to which each function from the Functions array shall apply.
     * Bounds elements shall be in order of increasing value, and each value shall be within
     * the domain defined by Domain.
     *
     * <p>
     * (see ISO-320001 Table 41)
     *
     * @param value the new set of bounds
     */

    public void setBounds(double[] value) {
        bounds = Arrays.copyOf(value, value.length);
    }

    /**
     * An array of 2 × k numbers that, taken in pairs, shall map each subset of the domain defined
     * by Domain and the Bounds array to the domain of the corresponding function.
     *
     * <p>
     * (see ISO-320001 Table 41)
     *
     * @return the encode values
     */
    public double[] getEncode() {
        return getPdfObject().getAsArray(PdfName.Encode).toDoubleArray();
    }

    /**
     * (Required) An array of 2 × k numbers that, taken in pairs, shall map each subset of the domain defined
     * by Domain and the Bounds array to the domain of the corresponding function.
     *
     * <p>
     * (see ISO-320001 Table 41)
     *
     * @param value the new set of encodings
     */
    public void setEncode(double[] value) {
        getPdfObject().put(PdfName.Encode, new PdfArray(value));
    }

    @Override
    public boolean checkCompatibilityWithColorSpace(PdfColorSpace alternateSpace) {
        return false;
    }

    /**
     * Gets output size of function.
     *
     * <p>
     * If Range field is absent, the output size of functions will be returned.
     *
     * @return output size of function
     */
    @Override
    public int getOutputSize() {
        return getRange() == null ? functions.get(0).getOutputSize() : getRange().length / 2;
    }

    @Override
    public double[] calculate(double[] input) {
        if (input == null || input.length != 1) {
            throw new PdfException(KernelExceptionMessageConstant.INVALID_INPUT_FOR_TYPE_3_FUNCTION);
        }
        double[] clipped = clipInput(input);
        double x = clipped[0];
        final int subdomain = calculateSubdomain(x);
        final double[] subdomainBorders = getSubdomainBorders(subdomain);
        x = mapValueFromActualRangeToExpected(x, subdomainBorders[0], subdomainBorders[1], encode[subdomain * 2],
                encode[(subdomain * 2) + 1]);

        final double[] output = functions.get(subdomain).calculate(new double[] {x});
        return clipOutput(output);
    }

    private int calculateSubdomain(double inputValue) {
        if (bounds.length > 0) {
            if (areThreeDoubleEqual(bounds[0], getDomain()[0], inputValue)) {
                return 0;
            }
            if (areThreeDoubleEqual(bounds[bounds.length - 1], getDomain()[1], inputValue)) {
                return bounds.length;
            }
        }

        for (int i = 0; i < bounds.length; i++) {
            if (inputValue < bounds[i]) {
                return i;
            }
        }
        return bounds.length;
    }

    private double[] getSubdomainBorders(int subdomain) {
        if (bounds.length == 0) {
            return getDomain();
        }
        if (subdomain == 0) {
            return new double[] {getDomain()[0], bounds[0]};
        } else if (subdomain == bounds.length) {
            return new double[] {bounds[bounds.length - 1], getDomain()[1]};
        } else {
            return new double[] {bounds[subdomain - 1], bounds[subdomain]};
        }
    }

    private List<IPdfFunction> checkAndGetFunctions(PdfArray functionsArray) {
        if (functionsArray == null || functionsArray.size() == 0) {
            throw new PdfException(KernelExceptionMessageConstant.INVALID_TYPE_3_FUNCTION_NULL_FUNCTIONS);
        }

        Integer tempOutputSize = null;
        if (getRange()!= null)
        {
            tempOutputSize = getOutputSize();
        }
        final List<IPdfFunction> tempFunctions = new ArrayList<>();
        for (PdfObject funcObj : functionsArray) {
            if (!(funcObj instanceof PdfDictionary)) {
                continue;
            }
            final PdfDictionary funcDict = (PdfDictionary) funcObj;
            final IPdfFunction tempFunc = functionFactory.create(funcDict);
            if (tempOutputSize == null) {
                tempOutputSize = tempFunc.getOutputSize();
            }
            if (tempOutputSize != tempFunc.getOutputSize()) {
                throw new PdfException(KernelExceptionMessageConstant.INVALID_TYPE_3_FUNCTION_FUNCTIONS_OUTPUT);
            }
            if (tempFunc.getInputSize() != 1) {
                throw new PdfException(KernelExceptionMessageConstant.INVALID_TYPE_3_FUNCTION_FUNCTIONS_INPUT);
            }

            tempFunctions.add(tempFunc);
        }
        return tempFunctions;
    }

    private double[] checkAndGetBounds(PdfArray boundsArray) {
        if (boundsArray == null || boundsArray.size() != (functions.size() - 1)) {
            throw new PdfException(KernelExceptionMessageConstant.INVALID_TYPE_3_FUNCTION_NULL_BOUNDS);
        }
        final double[] bounds = boundsArray.toDoubleArray();

        boolean areBoundsInvalid = false;
        for (int i = 0; i < bounds.length; i++) {
            areBoundsInvalid |= i == 0 ? bounds[i] < getDomain()[0] : bounds[i] <= getDomain()[0];
            areBoundsInvalid |= i == bounds.length - 1 ? getDomain()[1] < bounds[i] : getDomain()[1] <= bounds[i];
            areBoundsInvalid |= (i != 0 && bounds[i] <= bounds[i - 1]);
        }
        if (areBoundsInvalid) {
            throw new PdfException(KernelExceptionMessageConstant.INVALID_TYPE_3_FUNCTION_BOUNDS);
        }
        return bounds;
    }

    private double[] checkAndGetEncode(PdfArray encodeArray) {
        if (encodeArray == null || encodeArray.size() < (functions.size() * 2)) {
            throw new PdfException(KernelExceptionMessageConstant.INVALID_TYPE_3_FUNCTION_NULL_ENCODE);
        }
        return encodeArray.toDoubleArray();
    }

    /**
     * Maps passed value from actual range to expected range.
     *
     * @param value  the value to map
     * @param aStart the start of actual range
     * @param aEnd   the end of actual range
     * @param eStart the start of expected range
     * @param eEnd   the end of expected range
     *
     * @return the mapped value
     */
    private static double mapValueFromActualRangeToExpected(double value, double aStart, double aEnd, double eStart,
            double eEnd) {

        // Present ranges [start, end] as [0, ...RangeLength].
        final double actualRangeLength = aEnd - aStart;
        if (actualRangeLength == 0) {
            return eStart;
        }
        final double expectedRangeLength = eEnd - eStart;

        // New input value = value - actual.start.
        final double x = value - aStart;
        final double y = (expectedRangeLength / actualRangeLength) * x;

        // Map y from range [0, expectedRangeLength] to [eStart, eEnd].
        return eStart + y;
    }

    private static boolean areThreeDoubleEqual(double first, double second, double third) {
        return Double.compare(first, second) == 0 && Double.compare(second, third) == 0;
    }
}
