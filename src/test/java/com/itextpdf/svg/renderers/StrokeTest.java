package com.itextpdf.svg.renderers;

import com.itextpdf.svg.renderers.impl.SvgNodeRendererTestUtility;
import com.itextpdf.test.ITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;

import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

@Category(IntegrationTest.class)
public class StrokeTest {

    @Rule
    public ExpectedException junitExpectedException = ExpectedException.none();

    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/svg/renderers/impl/StrokeTest/";
    private static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/svg/renderers/impl/StrokeTest/";

    @BeforeClass
    public static void beforeClass() {
        ITextTest.createDestinationFolder(DESTINATION_FOLDER);
    }

    @Test
    public void normalLineStrokeTest() throws IOException, InterruptedException {
        SvgNodeRendererTestUtility.convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "normalLineStroke");
    }

    @Test
    public void noLineStrokeTest() throws IOException, InterruptedException {
        SvgNodeRendererTestUtility.convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "noLineStroke");
    }

    @Test
    public void noLineStrokeWidthTest() throws IOException, InterruptedException {
        SvgNodeRendererTestUtility.convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "noLineStrokeWidth");
    }
}