package com.itextpdf.svg.renderers.impl;

import com.itextpdf.test.ITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;

import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

@Category(IntegrationTest.class)
public class TextSvgNodeRendererTest {

    @Rule
    public ExpectedException junitExpectedException = ExpectedException.none();

    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/svg/renderers/impl/TextNodeRendererTest/";
    private static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/svg/renderers/impl/TextNodeRendererTest/";

    @BeforeClass
    public static void beforeClass() {
        ITextTest.createDestinationFolder(DESTINATION_FOLDER);
    }

    @Test
    public void helloWorldTest() throws IOException, InterruptedException {
        SvgNodeRendererTestUtility.convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "hello_world");
    }

    @Test
    public void tooLongTest() throws IOException, InterruptedException {
        SvgNodeRendererTestUtility.convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "too_long");
    }

    @Test
    public void twoLinesTest() throws IOException, InterruptedException {
        SvgNodeRendererTestUtility.convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "two_lines");
    }

    @Test
    public void twoLinesNewlineTest() throws IOException, InterruptedException {
        SvgNodeRendererTestUtility.convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "two_lines_newline");
    }
}