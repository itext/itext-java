/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2024 Apryse Group NV
    Authors: Apryse Software.

    This program is offered under a commercial and under the AGPL license.
    For commercial licensing, contact us at https://itextpdf.com/sales.  For AGPL licensing, see below.

    AGPL licensing:
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.itextpdf.io.util;

import com.itextpdf.commons.utils.FileUtil;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.security.Security;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class UrlUtilTest extends ExtendedITextTest {

    private static final String destinationFolder = "./target/test/com/itextpdf/io/UrlUtilTest/";

    @BeforeClass
    public static void beforeClass() {
        Security.addProvider(new BouncyCastleProvider());
        createDestinationFolder(destinationFolder);
    }

    // Tests, that getFinalConnection will be redirected some times for other urls, and initialUrl will be different
    // from final url.
    @Test
    public void getFinalConnectionWhileRedirectingTest() throws IOException {
        URL initialUrl = new URL("http://itextpdf.com");
        URL expectedURL = new URL("https://itextpdf.com/");
        URLConnection finalConnection = null;

        try {
            finalConnection = UrlUtil.getFinalConnection(initialUrl);

            Assert.assertNotNull(finalConnection);
            Assert.assertNotEquals(initialUrl, finalConnection.getURL());
            Assert.assertEquals(expectedURL, finalConnection.getURL());
        } finally {
            finalConnection.getInputStream().close();
        }
    }

    // This test checks that when we pass invalid url and trying get stream related to final redirected url,exception
    // would be thrown.
    @Test
    public void getInputStreamOfFinalConnectionThrowExceptionTest() throws IOException {
        URL invalidUrl = new URL("http://itextpdf");

        Assert.assertThrows(UnknownHostException.class, () -> UrlUtil.getInputStreamOfFinalConnection(invalidUrl));
    }

    // This test checks that when we pass valid url and trying get stream related to final redirected url, it would
    // not be null.
    @Test
    public void getInputStreamOfFinalConnectionTest() throws IOException {
        URL initialUrl = new URL("http://itextpdf.com");
        InputStream streamOfFinalConnectionOfInvalidUrl = UrlUtil.getInputStreamOfFinalConnection(initialUrl);

        Assert.assertNotNull(streamOfFinalConnectionOfInvalidUrl);
    }

    @Test
    @org.junit.Ignore
    public void getBaseUriTest() throws IOException {
        String absolutePathRoot = Paths.get("").toAbsolutePath().toUri().toURL().toExternalForm();
        String expected = absolutePathRoot + destinationFolder.substring(1);
        File tempFile = FileUtil.createTempFile(destinationFolder);
        Assert.assertEquals(expected, FileUtil.getParentDirectoryUri(tempFile));
    }

    @Test
    public void nullBaseUriTest() throws IOException {
        String expected = "";
        File tempFile = null;
        Assert.assertEquals(expected, FileUtil.getParentDirectoryUri(tempFile));
    }

    @Test
    public void toAbsoluteUriTest() throws IOException, URISyntaxException {
        String expected = "http://itextpdf.com/";
        Assert.assertEquals(expected, UrlUtil.toAbsoluteURI(new URI(expected)));
    }

    @Test
    public void openStreamTest() throws IOException {
        String resPath = "./src/test/resources/com/itextpdf/io/util/textFile.dat";
        InputStream openStream = UrlUtil.openStream(new File(resPath).toURI().toURL());

        String actual = new String(StreamUtil.inputStreamToArray(openStream), StandardCharsets.UTF_8);
        Assert.assertEquals("Hello world from text file!", actual);

    }
}
