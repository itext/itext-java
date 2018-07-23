package com.itextpdf.signatures.sign;

import com.itextpdf.signatures.CrlClientOnline;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;

public class CrlClientOnlineTest {

    private static final String destinationFolder = "./target/test/com/itextpdf/signatures/sign/";

    @Test
    public void crlClientOnlineURLConstructorTest() throws MalformedURLException {

        String PROTOCOL = "file://";
        URL[] urls = new URL[]{
                new URL(PROTOCOL + destinationFolder + "duplicateFolder"),
                new URL(PROTOCOL + destinationFolder + "duplicateFolder"),
                new URL(PROTOCOL + destinationFolder + "uniqueFolder"),
        };
        CrlClientOnline crlClientOnline = new CrlClientOnline(urls);

        Assert.assertTrue(crlClientOnline.getUrlsSize() ==  2);
    }
}
