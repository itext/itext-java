package com.itextpdf.svg.css;

import com.itextpdf.io.exceptions.IOException;
import com.itextpdf.svg.renderers.SvgIntegrationTest;
import com.itextpdf.test.ITextTest;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("IntegrationTest")
public class CssVariableTest extends SvgIntegrationTest {
    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/svg/css/CssVariableTest/";
    private static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/svg/css/CssVariableTest/";

    @BeforeAll
    public static void beforeClass() {
        ITextTest.createDestinationFolder(DESTINATION_FOLDER);
    }

    @Test
    public void circleWithVariablesTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "circleWithVariables");
    }

    @Test
    public void circleWithVariablesInDefsTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "circleWithVariablesInDefs");
    }

    @Test
    public void circleWithVariablesInDefsWithInnerSvgTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "circleWithVariablesInDefsWithInnerSvg");
    }

    @Test
    public void svgWithVariablesInShorthandTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "svgWithVariablesInShorthand");
    }

    @Test
    public void svgWithVariablesAsShorthandTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "svgWithVariablesAsShorthand");
    }

    @Test
    public void rootSelectorVariablesTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "rootSelectorVariables");
    }

    @Test
    public void variablesInStyleAttributeTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "variablesInStyleAttribute");
    }

    @Test
    public void symbolInheritanceTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "symbolInheritance");
    }
}
