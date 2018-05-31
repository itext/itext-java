package com.itextpdf.svg.renderers.impl;

import com.itextpdf.styledxmlparser.resolver.resource.ResourceResolver;
import com.itextpdf.svg.processors.impl.DefaultSvgConverterProperties;
import com.itextpdf.svg.renderers.SvgIntegrationTest;
import com.itextpdf.test.ITextTest;

import java.io.IOException;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class UseIntegrationTest extends SvgIntegrationTest {

    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/svg/renderers/impl/UseIntegrationTest/";
    private static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/svg/renderers/impl/UseIntegrationTest/";

    private DefaultSvgConverterProperties properties;

    @BeforeClass
    public static void beforeClass() {
        ITextTest.createDestinationFolder(DESTINATION_FOLDER);
    }

    @Before
    public void before() {
        ResourceResolver resourceResolver = new ResourceResolver(SOURCE_FOLDER);
        properties = new DefaultSvgConverterProperties();
        properties.setResourceResolver(resourceResolver);
    }

    @Test
    public void singleUseTest() throws IOException,InterruptedException {
        convertAndCompareSinglePageVisually(SOURCE_FOLDER, DESTINATION_FOLDER, "singleUse");
    }

    @Test
    public void singleUseFillTest() throws IOException,InterruptedException {
        // will break on implementation of RND-880
        convertAndCompareSinglePageVisually(SOURCE_FOLDER, DESTINATION_FOLDER, "singleUseFill");
    }

    @Test
    public void doubleNestedUseFillTest() throws IOException,InterruptedException {
        // will break on implementation of RND-880
        convertAndCompareSinglePageVisually(SOURCE_FOLDER, DESTINATION_FOLDER, "doubleNestedUseFill");
    }

    @Test
    public void singleUseStrokeTest() throws IOException,InterruptedException {
        // will break on implementation of RND-880
        convertAndCompareSinglePageVisually(SOURCE_FOLDER, DESTINATION_FOLDER, "singleUseStroke");
    }

    @Test
    public void doubleNestedUseStrokeTest() throws IOException,InterruptedException {
        // will break on implementation of RND-880
        convertAndCompareSinglePageVisually(SOURCE_FOLDER, DESTINATION_FOLDER, "doubleNestedUseStroke");
    }

    @Test
    public void translateUseTest() throws IOException,InterruptedException {
        convertAndCompareSinglePageVisually(SOURCE_FOLDER, DESTINATION_FOLDER, "translateUse");
    }

    @Test
    public void multipleTransformationsUseTest() throws IOException,InterruptedException {
        convertAndCompareSinglePageVisually(SOURCE_FOLDER, DESTINATION_FOLDER, "multipleTransformationsUse");
    }

    @Test
    public void coordinatesUseTest() throws IOException,InterruptedException {
        convertAndCompareSinglePageVisually(SOURCE_FOLDER, DESTINATION_FOLDER, "coordinatesUse");
    }

    @Test
    public void imageUseTest() throws IOException,InterruptedException {
        convertAndCompareSinglePageVisually(SOURCE_FOLDER, DESTINATION_FOLDER, "imageUse", properties);
    }

    @Test
    public void svgUseTest() throws IOException,InterruptedException {
        convertAndCompareSinglePageVisually(SOURCE_FOLDER, DESTINATION_FOLDER, "svgUse", properties);
    }

    @Test
    public void complexUseTest() throws IOException,InterruptedException {
        convertAndCompareSinglePageVisually(SOURCE_FOLDER, DESTINATION_FOLDER, "complexUse", properties);
    }

    @Test
    public void UseWithoutDefsTest() throws IOException,InterruptedException {
        convertAndCompareSinglePageVisually(SOURCE_FOLDER, DESTINATION_FOLDER, "useWithoutDefs", properties);
    }

    @Test
    public void UseWithoutDefsUsedElementAfterUseTest() throws IOException,InterruptedException {
        convertAndCompareSinglePageVisually(SOURCE_FOLDER, DESTINATION_FOLDER, "useWithoutDefsUsedElementAfterUse", properties);
    }

    public void simpleRectReuseTest() throws IOException,InterruptedException {
        convertAndCompareSinglePageVisually(SOURCE_FOLDER, DESTINATION_FOLDER, "simpleRectReuse", properties);
    }

    @Test
    public void transitiveTest() throws IOException,InterruptedException {
        convertAndCompareSinglePageVisually(SOURCE_FOLDER, DESTINATION_FOLDER, "transitive", properties);
    }

    @Test
    public void circularTest() throws IOException,InterruptedException {
        convertAndCompareSinglePageVisually(SOURCE_FOLDER, DESTINATION_FOLDER, "circular", properties);
    }

    @Test
    public void complexReferencesTest() throws IOException,InterruptedException {
        convertAndCompareSinglePageVisually(SOURCE_FOLDER, DESTINATION_FOLDER, "complexReferences", properties);
    }

    @Test
    public void transformationsOnTransformationsTest() throws IOException,InterruptedException {
        convertAndCompareSinglePageVisually(SOURCE_FOLDER, DESTINATION_FOLDER, "transformationsOnTransformations", properties);
    }

    @Test
    public void reuseLinesTest() throws IOException,InterruptedException {
        convertAndCompareSinglePageVisually(SOURCE_FOLDER, DESTINATION_FOLDER, "reuseLines", properties);
    }

    @Test
    public void missingHashtagTest() throws IOException,InterruptedException {
        convertAndCompareSinglePageVisually(SOURCE_FOLDER, DESTINATION_FOLDER, "missingHashtag", properties);
    }
}