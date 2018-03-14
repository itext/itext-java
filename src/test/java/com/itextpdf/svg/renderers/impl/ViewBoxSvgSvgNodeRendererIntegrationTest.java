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
public class ViewBoxSvgSvgNodeRendererIntegrationTest {

    @Rule
    public ExpectedException junitExpectedException = ExpectedException.none();

    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/svg/renderers/impl/RootSvgNodeRendererTest/viewbox/";
    private static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/svg/renderers/impl/RootSvgNodeRendererTest/viewbox/";

    @BeforeClass
    public static void beforeClass() {
        ITextTest.createDestinationFolder(DESTINATION_FOLDER);
    }

    @Test
    public void viewBox50() throws IOException, InterruptedException {
        SvgNodeRendererTestUtility.convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER,"viewbox_50");
    }

    @Test
    public void viewBox100() throws IOException, InterruptedException {
        SvgNodeRendererTestUtility.convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER,"viewbox_100");
    }

    @Test
    public void viewBox200() throws IOException, InterruptedException {
        SvgNodeRendererTestUtility.convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER,"viewbox_200");
    }

    @Test
    public void viewBox400() throws IOException, InterruptedException {
        SvgNodeRendererTestUtility.convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER,"viewbox_400");
    }
}