package com.itextpdf.svg.renderers.impl;

import com.itextpdf.styledxmlparser.resolver.resource.ResourceResolver;
import com.itextpdf.svg.processors.impl.DefaultSvgConverterProperties;
import com.itextpdf.svg.renderers.SvgIntegrationTest;
import com.itextpdf.test.ITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;

import java.io.IOException;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class ImageSvgNodeRendererIntegrationTest extends SvgIntegrationTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/svg/renderers/impl/ImageSvgNodeRendererTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/svg/renderers/impl/ImageSvgNodeRendererTest/";

    private DefaultSvgConverterProperties properties;

    @BeforeClass
    public static void beforeClass() {
        ITextTest.createDestinationFolder(destinationFolder);
    }

    @Before
    public void before() {
        ResourceResolver resourceResolver = new ResourceResolver(sourceFolder);
        properties = new DefaultSvgConverterProperties();
        properties.setResourceResolver(resourceResolver);
    }

    @Test
    public void singleImageTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(sourceFolder, destinationFolder, "singleImage", properties);
    }

    @Test
    public void imageWithRectangleTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(sourceFolder, destinationFolder, "imageWithRectangle", properties);
    }

    @Test
    public void imageWithMultipleShapesTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(sourceFolder, destinationFolder, "imageWithMultipleShapes", properties);
    }

    @Test
    public void imageXYTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(sourceFolder, destinationFolder, "imageXY", properties);
    }

    @Test
    public void multipleImagesTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(sourceFolder, destinationFolder, "multipleImages", properties);
    }

    @Test
    public void nonSquareImageTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(sourceFolder, destinationFolder, "nonSquareImage", properties);
    }

    @Test
    public void singleImageTranslateTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(sourceFolder, destinationFolder, "singleImageTranslate", properties);
    }

    @Test
    public void singleImageRotateTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(sourceFolder, destinationFolder, "singleImageRotate", properties);
    }

    @Test
    public void singleImageScaleUpTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(sourceFolder, destinationFolder, "singleImageScaleUp", properties);
    }

    @Test
    public void singleImageScaleDownTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(sourceFolder, destinationFolder, "singleImageScaleDown", properties);
    }

    @Test
    public void singleImageMultipleTransformationsTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(sourceFolder, destinationFolder, "singleImageMultipleTransformations", properties);
    }

    @Test
    public void twoImagesWithTransformationsTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(sourceFolder, destinationFolder, "twoImagesWithTransformations", properties);
    }

    @Test
    @Ignore("RND-876")
    public void differentDimensionsTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(sourceFolder, destinationFolder, "differentDimensions", properties);
    }
}