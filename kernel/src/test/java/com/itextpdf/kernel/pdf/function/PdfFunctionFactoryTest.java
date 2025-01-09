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

import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.test.ExtendedITextTest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("IntegrationTest")
public class PdfFunctionFactoryTest extends ExtendedITextTest {

    @Test
    public void testCreateFunctionType0() {
        PdfStream stream = new PdfStream(new byte[] {0, 0, 0});
        stream.put(PdfName.FunctionType,new PdfNumber(0));
        stream.put(PdfName.Domain, new PdfArray(new double[] {0, 0, 0, 0, 0, 0}));
        stream.put(PdfName.Size, new PdfArray(new int[] {2, 1, 3}));
        stream.put(PdfName.Range, new PdfArray(
                new double[] {1,2,3,4,5,6}));
        stream.put(PdfName.BitsPerSample, new PdfNumber(1));
        IPdfFunction function = PdfFunctionFactory.create(stream);

        Assertions.assertTrue(function instanceof PdfType0Function);
    }

    @Test
    public void testCreateFunctionType2() {
        PdfDictionary object = new PdfDictionary();
        object.put(PdfName.FunctionType, new PdfNumber(2));
        PdfArray domain = new PdfArray(new int[] {0, 1});
        object.put(PdfName.Domain, domain);
        object.put(PdfName.N, new PdfNumber(2));
        IPdfFunction function = PdfFunctionFactory.create(object);

        Assertions.assertTrue(function instanceof PdfType2Function);
    }

    @Test
    public void testCreateFunctionType3() {
        PdfDictionary object = new PdfDictionary();
        object.put(PdfName.FunctionType, new PdfNumber(3));
        PdfArray domain = new PdfArray(new int[] {0, 1});
        object.put(PdfName.Domain, domain);
        PdfArray functions = new PdfArray(PdfFunctionUtil.createMinimalPdfType2FunctionDict());
        PdfDictionary minimalType2Func = PdfFunctionUtil.createMinimalPdfType2FunctionDict();
        minimalType2Func.put(PdfName.N, new PdfNumber(1));
        functions.add(minimalType2Func);
        object.put(PdfName.Functions, functions);
        object.put(PdfName.Bounds, new PdfArray(new double[] {0.5}));
        object.put(PdfName.Encode, new PdfArray(new double[] {0, 1, 0, 1}));
        IPdfFunction function = PdfFunctionFactory.create(object);

        Assertions.assertTrue(function instanceof PdfType3Function);
    }


    @Test
    public void testCreateFunctionType4() {
        PdfStream stream = new PdfStream(new byte[] {0, 0, 0});
        stream.put(PdfName.FunctionType,new PdfNumber(4));
        stream.put(PdfName.Domain, new PdfArray(new double[] {0, 0, 0, 0, 0, 0}));
        stream.put(PdfName.Size, new PdfArray(new int[] {2, 1, 3}));
        stream.put(PdfName.Range, new PdfArray(
                new double[] {1,2,3,4,5,6}));
        stream.put(PdfName.BitsPerSample, new PdfNumber(1));
        IPdfFunction function = PdfFunctionFactory.create(stream);

        Assertions.assertTrue(function instanceof PdfType4Function);
    }


    @Test
    public void testInvalidFunctionTypeThrowsException() {
        PdfStream stream = new PdfStream(new byte[] {0, 0, 0});
        stream.put(PdfName.FunctionType,new PdfNumber(1));
        stream.put(PdfName.Domain, new PdfArray(new double[] {0, 0, 0, 0, 0, 0}));
        stream.put(PdfName.Size, new PdfArray(new int[] {2, 1, 3}));
        stream.put(PdfName.Range, new PdfArray(
                new double[] {1,2,3,4,5,6}));
        stream.put(PdfName.BitsPerSample, new PdfNumber(1));
        Exception ex = Assertions.assertThrows(PdfException.class, () -> PdfFunctionFactory.create(stream));

        Assertions.assertEquals("Invalid function type 1", ex.getMessage());
    }

    @Test
    public void testDictionaryForType0Throws() {
        PdfDictionary dict = new PdfDictionary();
        dict.put(PdfName.FunctionType,new PdfNumber(0));
        dict.put(PdfName.Domain, new PdfArray(new double[] {0, 0, 0, 0, 0, 0}));
        dict.put(PdfName.Size, new PdfArray(new int[] {2, 1, 3}));
        dict.put(PdfName.Range, new PdfArray(
                new double[] {1,2,3,4,5,6}));
        dict.put(PdfName.BitsPerSample, new PdfNumber(1));
        Exception ex = Assertions.assertThrows(PdfException.class, () -> PdfFunctionFactory.create(dict));

        Assertions.assertEquals("Invalid object type, a function type 0 requires a stream object", ex.getMessage());
    }

    @Test
    public void testDictionaryForType4Throws() {
        PdfDictionary dict = new PdfDictionary();
        dict.put(PdfName.FunctionType,new PdfNumber(4));
        dict.put(PdfName.Domain, new PdfArray(new double[] {0, 0, 0, 0, 0, 0}));
        dict.put(PdfName.Size, new PdfArray(new int[] {2, 1, 3}));
        dict.put(PdfName.Range, new PdfArray(
                new double[] {1,2,3,4,5,6}));
        dict.put(PdfName.BitsPerSample, new PdfNumber(1));
        Exception ex = Assertions.assertThrows(PdfException.class, () -> PdfFunctionFactory.create(dict));

        Assertions.assertEquals("Invalid object type, a function type 4 requires a stream object", ex.getMessage());
    }

    @Test
    public void testArrayThrows() {
        PdfArray array = new PdfArray();
        Exception ex = Assertions.assertThrows(PdfException.class, () -> PdfFunctionFactory.create(array));

        Assertions.assertEquals("Invalid object type, a function must be either a Dictionary or a Stream", ex.getMessage());
    }
}
