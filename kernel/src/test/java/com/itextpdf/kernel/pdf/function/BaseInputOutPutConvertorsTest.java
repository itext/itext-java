package com.itextpdf.kernel.pdf.function;

import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.colorspace.PdfSpecialCs.Separation;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

@Category(UnitTest.class)
public class BaseInputOutPutConvertorsTest extends ExtendedITextTest {

    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/kernel/pdf/function/BaseInputOutPutConvertorsTest/";
    private static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/kernel/pdf/function/BaseInputOutPutConvertorsTest/";


    @Test
    public void testByteInputConvertor() throws IOException {
        BaseInputOutPutConvertors.IInputConversionFunction inputConvertor = BaseInputOutPutConvertors.getInputConvertor(1, 1);

        BaseInputOutPutConvertors.IOutputConversionFunction outputConvertor = BaseInputOutPutConvertors.getOutputConvertor(1, 1);

        byte[] original = Files.readAllBytes(Paths.get(SOURCE_FOLDER, "texture-time-gray scale medium.data"));


        PdfType2Function
        fnct1 = new PdfType2Function(new double[] {0,1},
                new double[] {0,1,0,1,0,1},
                new double[] {0, 0, 0},
                new double[] {0, 0.5, 0, },
                1);
        Separation sep1 = new Separation(new PdfName("SEP_RGB"), PdfName.DeviceRGB, fnct1.getPdfObject());

        byte[] calc = sep1.getTintTransformation().calculateFromByteArray(original,0, original.length,1,1);
        
        double[] result = inputConvertor.convert(original,0, original.length);

        byte[] roundtrip = outputConvertor.convert(result);

        assertArrayEquals(original, roundtrip);
    }


    @Test
    public void testInvalidOffsetAndLength() throws IOException {
        BaseInputOutPutConvertors.IInputConversionFunction inputConvertor = BaseInputOutPutConvertors.getInputConvertor(1, 1);

        BaseInputOutPutConvertors.IOutputConversionFunction outputConvertor = BaseInputOutPutConvertors.getOutputConvertor(1, 1);

        byte[] original = Files.readAllBytes(Paths.get(SOURCE_FOLDER, "texture-time-gray scale medium.data"));


        PdfType2Function
                fnct1 = new PdfType2Function(new double[] {0,1},
                new double[] {0,1,0,1,0,1},
                new double[] {0, 0, 0},
                new double[] {0, 0.5, 0, },
                1);
        Separation sep1 = new Separation(new PdfName("SEP_RGB"), PdfName.DeviceRGB, fnct1.getPdfObject());

        IPdfFunction func = sep1.getTintTransformation();
        Exception ex = Assert.assertThrows(IllegalArgumentException.class, () ->
                func.calculateFromByteArray(original, 10, original.length, 1, 1));

        assertEquals(KernelExceptionMessageConstant.INVALID_LENGTH, ex.getMessage());
    }



    @Test
    public void testInvalidLengthForWordSize() throws IOException {
        BaseInputOutPutConvertors.IInputConversionFunction inputConvertor = BaseInputOutPutConvertors.getInputConvertor(1, 1);

        BaseInputOutPutConvertors.IOutputConversionFunction outputConvertor = BaseInputOutPutConvertors.getOutputConvertor(1, 1);

        byte[] original = Files.readAllBytes(Paths.get(SOURCE_FOLDER, "texture-time-gray scale medium.data"));


        PdfType2Function
                fnct1 = new PdfType2Function(new double[] {0,1},
                new double[] {0,1,0,1,0,1},
                new double[] {0, 0, 0},
                new double[] {0, 0.5, 0, },
                1);
        Separation sep1 = new Separation(new PdfName("SEP_RGB"), PdfName.DeviceRGB, fnct1.getPdfObject());

        IPdfFunction func = sep1.getTintTransformation();
        Exception ex = Assert.assertThrows(IllegalArgumentException.class, () ->
                func.calculateFromByteArray(original, 0, original.length, 11*8, 1));

        assertEquals(MessageFormatUtil.format(KernelExceptionMessageConstant.INVALID_LENGTH_FOR_WORDSIZE, 11), ex.getMessage());
    }

}