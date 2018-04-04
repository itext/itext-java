package com.itextpdf.svg.renderers;

import com.itextpdf.svg.renderers.impl.SvgNodeRendererTestUtility;
import com.itextpdf.test.ITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class GUnitTest {

    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/svg/renderers/impl/gunit/";
    private static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/svg/renderers/impl/gunit/";

    @BeforeClass
    public static void beforeClass() {
        ITextTest.createDestinationFolder(DESTINATION_FOLDER);
    }

    @Test
    public void meetTheTeam() throws IOException, InterruptedException {
        List<Exception> assertionErrorsThrown = new ArrayList<>();
        for ( int i = 1; i < 6; i++) {
            try {
                SvgNodeRendererTestUtility.convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "test_00" + i);
            }catch(Exception ae){
                if(ae.getMessage().contains("expected null, but was")){
                    assertionErrorsThrown.add(ae);

                }
            }
        }
        if(assertionErrorsThrown.size() != 0) Assert.fail("At least one compare file was not identical with the result");
    }

    @Test
    public void viewboxTest() throws IOException,InterruptedException {
        SvgNodeRendererTestUtility.convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "test_viewbox");

    }
}