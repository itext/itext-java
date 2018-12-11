package com.itextpdf.svg.renderers.impl;

import com.itextpdf.styledxmlparser.exceptions.StyledXMLParserException;
import com.itextpdf.svg.renderers.SvgIntegrationTest;
import com.itextpdf.test.ITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

import java.io.IOException;

@Category(IntegrationTest.class)
public class TSpanNodeRendererIntegrationTest extends SvgIntegrationTest {

    @Rule
    public ExpectedException junitExpectedException = ExpectedException.none();

    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/svg/renderers/impl/TSpanNodeRendererIntegrationTest/";
    private static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/svg/renderers/impl/TSpanNodeRendererIntegrationTest/";

    @BeforeClass
    public static void beforeClass() {
        ITextTest.createDestinationFolder(DESTINATION_FOLDER);
    }

    //Relative Move tests
    @Test
    public void TSpanRelativeMovePositiveXTest() throws IOException, InterruptedException {
        convertAndCompareVisually(SOURCE_FOLDER, DESTINATION_FOLDER, "textspan-relativeMove-positiveX");
    }

    @Test
    public void TSpanRelativeMoveNegativeXTest() throws IOException, InterruptedException {
        convertAndCompareVisually(SOURCE_FOLDER, DESTINATION_FOLDER, "textspan-relativeMove-negativeX");
    }

    @Test
    public void TSpanRelativeMoveZeroXTest() throws IOException, InterruptedException {
        convertAndCompareVisually(SOURCE_FOLDER, DESTINATION_FOLDER, "textspan-relativeMove-zeroX");
    }

    @Test
    public void TSpanRelativeMoveInvalidXTest() throws IOException, InterruptedException {
        junitExpectedException.expect(StyledXMLParserException.class);
        convertAndCompareVisually(SOURCE_FOLDER, DESTINATION_FOLDER, "textspan-relativeMove-invalidX");
    }

    @Test
    public void TSpanRelativeMovePositiveYTest() throws IOException, InterruptedException {
        convertAndCompareVisually(SOURCE_FOLDER, DESTINATION_FOLDER, "textspan-relativeMove-positiveY");
    }

    @Test
    public void TSpanRelativeMoveNegativeYTest() throws IOException, InterruptedException {
        convertAndCompareVisually(SOURCE_FOLDER, DESTINATION_FOLDER, "textspan-relativeMove-negativeY");
    }

    @Test
    public void TSpanRelativeMoveZeroYTest() throws IOException, InterruptedException {
        convertAndCompareVisually(SOURCE_FOLDER, DESTINATION_FOLDER, "textspan-relativeMove-zeroY");
    }

    @Test
    public void TSpanRelativeMoveInvalidYTest() throws IOException, InterruptedException {
        junitExpectedException.expect(StyledXMLParserException.class);
        convertAndCompareVisually(SOURCE_FOLDER, DESTINATION_FOLDER, "textspan-relativeMove-invalidY");
    }

    @Test
    public void TSpanRelativeMoveXandYTest() throws IOException, InterruptedException {
        convertAndCompareVisually(SOURCE_FOLDER, DESTINATION_FOLDER, "textspan-relativeMove-XandY");
    }
    
    //Absolute Position tests
    @Test
    public void TSpanAbsolutePositionPositiveXTest() throws IOException, InterruptedException {
        convertAndCompareVisually(SOURCE_FOLDER, DESTINATION_FOLDER, "textspan-absolutePosition-positiveX");
    }

    @Test
    public void TSpanAbsolutePositionNegativeXTest() throws IOException, InterruptedException {
        convertAndCompareVisually(SOURCE_FOLDER, DESTINATION_FOLDER, "textspan-absolutePosition-negativeX");
    }

    @Test
    public void TSpanAbsolutePositionZeroXTest() throws IOException, InterruptedException {
        convertAndCompareVisually(SOURCE_FOLDER, DESTINATION_FOLDER, "textspan-absolutePosition-zeroX");
    }
    
    @Test
    public void TSpanAbsolutePositionInvalidXTest() throws IOException, InterruptedException {
        junitExpectedException.expect(StyledXMLParserException.class);
        convertAndCompareVisually(SOURCE_FOLDER, DESTINATION_FOLDER, "textspan-absolutePosition-invalidX");
    }

    @Test
    public void TSpanAbsolutePositionPositiveYTest() throws IOException, InterruptedException {
        convertAndCompareVisually(SOURCE_FOLDER, DESTINATION_FOLDER, "textspan-absolutePosition-positiveY");
    }

    @Test
    public void TSpanAbsolutePositionNegativeYTest() throws IOException, InterruptedException {
        convertAndCompareVisually(SOURCE_FOLDER, DESTINATION_FOLDER, "textspan-absolutePosition-negativeY");
    }

    @Test
    public void TSpanAbsolutePositionZeroYTest() throws IOException, InterruptedException {
        convertAndCompareVisually(SOURCE_FOLDER, DESTINATION_FOLDER, "textspan-absolutePosition-zeroY");
    }

    @Test
    public void TSpanAbsolutePositionInvalidYTest() throws IOException, InterruptedException {
        junitExpectedException.expect(StyledXMLParserException.class);
        convertAndCompareVisually(SOURCE_FOLDER, DESTINATION_FOLDER, "textspan-absolutePosition-invalidY");
    }

    @Test
    public void TSpanAbsolutePositionXandYTest() throws IOException, InterruptedException {
        convertAndCompareVisually(SOURCE_FOLDER, DESTINATION_FOLDER, "textspan-absolutePosition-XandY");
    }

    @Test
    public void TSpanAbsolutePositionNestedTSpanTest() throws IOException, InterruptedException {
        convertAndCompareVisually(SOURCE_FOLDER, DESTINATION_FOLDER, "textspan-absolutePosition-nestedTSpan");
    }

    //Whitespace
    @Test
    public void TSpanWhiteSpaceFunctionalTest() throws IOException, InterruptedException {
        convertAndCompareVisually(SOURCE_FOLDER, DESTINATION_FOLDER, "textspan-whitespace");
    }

    //Relative move and absolute position interplay
    @Test
    public void TSpanAbsolutePositionAndRelativeMoveFunctionalTest() throws IOException, InterruptedException {
        convertAndCompareVisually(SOURCE_FOLDER, DESTINATION_FOLDER, "textspan-absolutePositionAndRelativeMove");
    }
}
