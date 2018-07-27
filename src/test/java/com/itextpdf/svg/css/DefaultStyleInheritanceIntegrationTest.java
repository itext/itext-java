package com.itextpdf.svg.css;

import com.itextpdf.svg.renderers.SvgIntegrationTest;
import com.itextpdf.test.ITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;

@Category(IntegrationTest.class)
public class DefaultStyleInheritanceIntegrationTest extends SvgIntegrationTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/svg/css/DefaultInheritance/";
    public static final String destinationFolder = "./target/test/com/itextpdf/svg/css/DefaultInheritance/";

    @BeforeClass
    public static void beforeClass() {
        ITextTest.createDestinationFolder(destinationFolder);
    }

    //Css inheritance
    @Test
    public void simpleGroupInheritanceTest() throws IOException, InterruptedException {
        convertAndCompareVisually(sourceFolder,destinationFolder,"simpleGroupInheritance");
    }
    //Inheritance in use tags
    @Test
    public void useFillInheritanceTest() throws IOException, InterruptedException {
        convertAndCompareVisually(sourceFolder,destinationFolder,"useFillInheritance");
    }
    //Inheritance and g-tags
    @Test
    public void groupInheritanceTest() throws IOException, InterruptedException {
        convertAndCompareVisually(sourceFolder,destinationFolder,"groupInheritance");
    }

    @Test
    public void useInheritanceNotOverridingTest() throws IOException, InterruptedException {
        convertAndCompareVisually(sourceFolder,destinationFolder,"useInheritanceNotOverriding");
    }

}
