package com.itextpdf.svg.renderers.impl;

import com.itextpdf.svg.renderers.SvgIntegrationTest;
import com.itextpdf.test.ITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;

import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class DefsSvgNodeRendererIntegrationTest extends SvgIntegrationTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/svg/renderers/impl/DefsSvgNodeRendererTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/svg/renderers/impl/DefsSvgNodeRendererTest/";

    @BeforeClass
    public static void beforeClass() {
        ITextTest.createDestinationFolder(destinationFolder);
    }

    @Test
    public void defsWithNoChildrenTest() throws IOException, InterruptedException {
        convertAndCompareSinglePageVisually(sourceFolder, destinationFolder, "onlyDefsWithNoChildren");
    }

    @Test
    public void defsWithOneChildTest() throws IOException, InterruptedException {
        convertAndCompareSinglePageVisually(sourceFolder, destinationFolder, "onlyDefsWithOneChild");
    }

    @Test
    public void defsWithMultipleChildrenTest() throws IOException, InterruptedException {
        convertAndCompareSinglePageVisually(sourceFolder, destinationFolder, "onlyDefsWithMultipleChildren");
    }

    @Test
    public void defsWithOneChildAndNonDefsBeingDrawnTest() throws IOException, InterruptedException {
        convertAndCompareSinglePageVisually(sourceFolder, destinationFolder, "defsWithOneChildAndNonDefsBeingDrawn");
    }
}