package com.itextpdf.io.util;

import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.File;
import java.io.IOException;

@Category(UnitTest.class)
public class UrlUtilTest extends ExtendedITextTest {

    private static final String destinationFolder = "./target/test/com/itextpdf/io/UrlUtilTest/";

    @BeforeClass
    public static void beforeClass() {
        createDestinationFolder(destinationFolder);
    }

    // Tests that after invocation of the getFinalURL method for local files, no handles are left open and the file is free to be removed
    @Test
    public void getFinalURLDoesNotLockFileTest() throws IOException {
        File tempFile = FileUtil.createTempFile(destinationFolder);

        UrlUtil.getFinalURL(UrlUtil.toURL(tempFile.getAbsolutePath()));

        Assert.assertTrue(FileUtil.deleteFile(tempFile));
    }

}
