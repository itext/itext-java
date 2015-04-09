package com.itextpdf.model;

import org.junit.BeforeClass;

import java.io.File;

public class OverflowTest {

    static final public String sourceFolder = "./src/test/resources/com/itextpdf/model/OverflowTest/";
    static final public String destinationFolder = "./target/test/com/itextpdf/model/OverflowTest/";

    @BeforeClass
    static public void beforeClass() {
        new File(destinationFolder).mkdirs();
    }


}
