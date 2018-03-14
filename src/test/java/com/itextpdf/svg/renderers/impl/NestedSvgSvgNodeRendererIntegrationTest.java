package com.itextpdf.svg.renderers.impl;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfResources;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.WriterProperties;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.svg.converter.SvgConverter;
import com.itextpdf.svg.renderers.ISvgNodeRenderer;
import com.itextpdf.svg.renderers.SvgDrawContext;
import com.itextpdf.test.ITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

@Category(IntegrationTest.class)
public class NestedSvgSvgNodeRendererIntegrationTest {

    @Rule
    public ExpectedException junitExpectedException = ExpectedException.none();

    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/svg/renderers/impl/RootSvgNodeRendererTest/nested/";
    private static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/svg/renderers/impl/RootSvgNodeRendererTest/nested/";

    @BeforeClass
    public static void beforeClass() {
        ITextTest.createDestinationFolder(DESTINATION_FOLDER);
    }

    @Test
    public void singleNestedSvgTest() throws IOException, InterruptedException {
        SvgNodeRendererTestUtility.convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "singleNested");
    }

    @Test
    public void doubleNestedSvgTest() throws IOException, InterruptedException {
        SvgNodeRendererTestUtility.convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER,"doubleNested");
    }

    @Test
    public void twoNestedSvgTest() throws IOException, InterruptedException {
        SvgNodeRendererTestUtility.convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER,"twoNested");
    }

    @Test
    public void emptySvgTest() throws IOException, InterruptedException {
        SvgNodeRendererTestUtility.convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER,"empty");
    }


}