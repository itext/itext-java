package com.itextpdf.svg.renderers.impl;

import com.itextpdf.styledxmlparser.jsoup.nodes.Document;
import com.itextpdf.styledxmlparser.node.impl.jsoup.node.JsoupDocumentNode;
import com.itextpdf.svg.processors.ISvgConverterProperties;
import com.itextpdf.svg.processors.impl.SvgConverterProperties;
import com.itextpdf.svg.renderers.SvgIntegrationTest;
import com.itextpdf.test.ITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;

@Category(IntegrationTest.class)
public class ImageSvgNodeRendererIntegrationTest extends SvgIntegrationTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/svg/renderers/impl/ImageSvgNodeRendererTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/svg/renderers/impl/ImageSvgNodeRendererTest/";

    private ISvgConverterProperties properties;

    @BeforeClass
    public static void beforeClass() {
        ITextTest.createDestinationFolder(destinationFolder);
    }

    @Before
    public void before() {
        properties = new SvgConverterProperties(new JsoupDocumentNode(new Document( "" )))
                .setBaseUri(sourceFolder);
    }

    @Test
    public void singleImageTest() throws IOException, InterruptedException {
        convertAndCompareSinglePageVisually(sourceFolder, destinationFolder, "singleImage", properties);
    }

    @Test
    public void imageWithRectangleTest() throws IOException, InterruptedException {
        convertAndCompareSinglePageVisually(sourceFolder, destinationFolder, "imageWithRectangle", properties);
    }

    @Test
    public void imageWithMultipleShapesTest() throws IOException, InterruptedException {
        convertAndCompareSinglePageVisually(sourceFolder, destinationFolder, "imageWithMultipleShapes", properties);
    }

    @Test
    public void imageXYTest() throws IOException, InterruptedException {
        convertAndCompareSinglePageVisually(sourceFolder, destinationFolder, "imageXY", properties);
    }

    @Test
    public void multipleImagesTest() throws IOException, InterruptedException {
        convertAndCompareSinglePageVisually(sourceFolder, destinationFolder, "multipleImages", properties);
    }

    @Test
    public void nonSquareImageTest() throws IOException, InterruptedException {
        convertAndCompareSinglePageVisually(sourceFolder, destinationFolder, "nonSquareImage", properties);
    }

    @Test
    public void singleImageTranslateTest() throws IOException, InterruptedException {
        convertAndCompareSinglePageVisually(sourceFolder, destinationFolder, "singleImageTranslate", properties);
    }

    @Test
    public void singleImageRotateTest() throws IOException, InterruptedException {
        convertAndCompareSinglePageVisually(sourceFolder, destinationFolder, "singleImageRotate", properties);
    }

    @Test
    public void singleImageScaleUpTest() throws IOException, InterruptedException {
        convertAndCompareSinglePageVisually(sourceFolder, destinationFolder, "singleImageScaleUp", properties);
    }

    @Test
    public void singleImageScaleDownTest() throws IOException, InterruptedException {
        convertAndCompareSinglePageVisually(sourceFolder, destinationFolder, "singleImageScaleDown", properties);
    }

    @Test
    public void singleImageMultipleTransformationsTest() throws IOException, InterruptedException {
        convertAndCompareSinglePageVisually(sourceFolder, destinationFolder, "singleImageMultipleTransformations", properties);
    }

    @Test
    public void twoImagesWithTransformationsTest() throws IOException, InterruptedException {
        convertAndCompareSinglePageVisually(sourceFolder, destinationFolder, "twoImagesWithTransformations", properties);
    }

    @Test
    @Ignore("RND-876")
    public void differentDimensionsTest() throws IOException, InterruptedException {
        convertAndCompareSinglePageVisually(sourceFolder, destinationFolder, "differentDimensions", properties);
    }
}