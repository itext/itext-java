package com.itextpdf.svg.renderers.impl;

import com.itextpdf.svg.processors.impl.SvgConverterProperties;
import com.itextpdf.svg.renderers.SvgIntegrationTest;
import com.itextpdf.test.ITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;

import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class GroupSvgNodeRendererIntegrationTest extends SvgIntegrationTest {

    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/svg/renderers/impl/GroupRendererTest/";
    private static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/svg/renderers/impl/GroupRendererTest/";

    private SvgConverterProperties properties;

    @BeforeClass
    public static void beforeClass() {
        ITextTest.createDestinationFolder(DESTINATION_FOLDER);
    }

    @Test
    public void nestedGroupReuseTest() throws IOException, InterruptedException {
        convertAndCompareSinglePageVisually(SOURCE_FOLDER, DESTINATION_FOLDER, "nestedGroupReuse");
    }

    @Test
    public void nestedGroupWithoutReuseTest() throws IOException, InterruptedException {
        convertAndCompareSinglePageVisually(SOURCE_FOLDER, DESTINATION_FOLDER, "nestedGroupWithoutReuse");
    }

    @Test
    public void simpleGroupReuseTest() throws IOException, InterruptedException {
        convertAndCompareSinglePageVisually(SOURCE_FOLDER, DESTINATION_FOLDER, "simpleGroupReuse");
    }

    @Test
    public void nestedGroupTest() throws IOException, InterruptedException {
        convertAndCompareSinglePageVisually(SOURCE_FOLDER, DESTINATION_FOLDER, "nestedGroup");
    }

    @Test
    public void overlayingGroupsTest() throws IOException, InterruptedException {
        convertAndCompareSinglePageVisually(SOURCE_FOLDER, DESTINATION_FOLDER, "overlayingGroups");
    }

    @Test
    public void overlappingBorderTest() throws IOException, InterruptedException {
        convertAndCompareSinglePageVisually(SOURCE_FOLDER, DESTINATION_FOLDER, "overlappingBorder");
    }

    @Test
    public void moreOverlappingBorderTest() throws IOException, InterruptedException {
        convertAndCompareSinglePageVisually(SOURCE_FOLDER, DESTINATION_FOLDER, "moreOverlappingBorder");
    }

    @Test
    public void moreOverlappingBorderWithCenterSquareTest() throws IOException, InterruptedException {
        convertAndCompareSinglePageVisually(SOURCE_FOLDER, DESTINATION_FOLDER, "moreOverlappingBorderWithCenterSquare");
    }

    @Test
    public void moreOverlappingBorderWithTwoSideSquaresTest() throws IOException, InterruptedException {
        convertAndCompareSinglePageVisually(SOURCE_FOLDER, DESTINATION_FOLDER, "moreOverlappingBorderWithTwoSideSquares");
    }

    @Test
    public void completeOverlappingBorderTest() throws IOException, InterruptedException {
        convertAndCompareSinglePageVisually(SOURCE_FOLDER, DESTINATION_FOLDER, "completeOverlappingBorder");
    }

    @Test
    public void translatedGroupTest() throws IOException, InterruptedException {
        convertAndCompareSinglePageVisually(SOURCE_FOLDER, DESTINATION_FOLDER, "translated");
    }

    @Test
    public void multipleTransformationsTest() throws IOException, InterruptedException {
        convertAndCompareSinglePageVisually(SOURCE_FOLDER, DESTINATION_FOLDER, "multipleTransformations");
    }
}