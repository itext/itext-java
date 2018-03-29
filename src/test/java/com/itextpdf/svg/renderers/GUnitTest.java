package com.itextpdf.svg.renderers;

import com.itextpdf.svg.renderers.impl.SvgNodeRendererTestUtility;
import com.itextpdf.test.ITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class GUnitTest {

    private static final String SOURCE_FOLDER = "C:\\Temp\\demo\\resources\\";
    private static final String DESTINATION_FOLDER = "C:\\Temp\\demo\\resources\\out\\";

    @BeforeClass
    public static void beforeClass() {
        ITextTest.createDestinationFolder(DESTINATION_FOLDER);
    }

    @Test
    public void meetTheTeam() throws IOException {
        for ( int i = 1; i < 6; i++) {
            SvgNodeRendererTestUtility.convert(new FileInputStream(SOURCE_FOLDER + "test_00" + i + ".svg"), new FileOutputStream(DESTINATION_FOLDER + "test_00" + i + ".pdf"));
        }
    }
}