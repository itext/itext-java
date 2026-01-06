/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2026 Apryse Group NV
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
import com.itextpdf.commons.utils.StringNormalizer;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.TestUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.BindException;
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
// Android-Conversion-Skip-Line (Security provider is required for working getFinalConnection through SSL on Android)
// Android-Conversion-Replace import java.security.Security;
// Android-Conversion-Replace import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("UnitTest")
public class UrlUtilTest extends ExtendedITextTest {

    private static final String destinationFolder = TestUtil.getOutputPath() + "/io/UrlUtilTest/";

    @BeforeAll
    public static void beforeClass() {
        // Android-Conversion-Skip-Line (Security provider is required for working getFinalConnection through SSL on Android)
        // Android-Conversion-Replace Security.addProvider(new BouncyCastleProvider());
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
            finalConnection = UrlUtil.getFinalConnection(initialUrl, 0, 0, null);

            Assertions.assertNotNull(finalConnection);
            Assertions.assertNotEquals(initialUrl, finalConnection.getURL());
            Assertions.assertEquals(expectedURL, finalConnection.getURL());
        } finally {
            finalConnection.getInputStream().close();
        }
    }

    // This test checks that when we pass invalid url and trying get stream related to final redirected url,exception
    // would be thrown.
    @Test
    public void getInputStreamOfFinalConnectionThrowExceptionTest() throws IOException {
        URL invalidUrl = new URL("http://itextpdf");

        Assertions.assertThrows(UnknownHostException.class, () -> UrlUtil.getInputStreamOfFinalConnection(invalidUrl));
    }

    // This test checks that when we pass valid url and trying get stream related to final redirected url, it would
    // not be null.
    @Test
    public void getInputStreamOfFinalConnectionTest() throws IOException {
        URL initialUrl = new URL("http://itextpdf.com");
        InputStream streamOfFinalConnectionOfInvalidUrl = UrlUtil.getInputStreamOfFinalConnection(initialUrl);

        Assertions.assertNotNull(streamOfFinalConnectionOfInvalidUrl);
    }

    @Test
    // Android-Conversion-Ignore-Test (TODO DEVSIX-7371 investigate different behavior of a few iTextCore tests on Java and Android)
    public void getBaseUriTest() throws IOException {
        String absolutePathRoot = Paths.get("").toAbsolutePath().toUri().toURL().toExternalForm();
        // Android-Conversion-Skip-Line (TODO DEVSIX-7371 investigate different behavior of a few iTextCore tests on Java and Android)
        String expected = absolutePathRoot + destinationFolder; // Android-Conversion-Replace String expected = absolutePathRoot + destinationFolder.substring(1);
        File tempFile = FileUtil.createTempFile(destinationFolder);
        Assertions.assertEquals(expected, FileUtil.getParentDirectoryUri(tempFile));
    }

    @Test
    public void nullBaseUriTest() throws IOException {
        String expected = "";
        File tempFile = null;
        Assertions.assertEquals(expected, FileUtil.getParentDirectoryUri(tempFile));
    }

    @Test
    public void toAbsoluteUriTest() throws IOException, URISyntaxException {
        String expected = "http://itextpdf.com/";
        Assertions.assertEquals(expected, UrlUtil.toAbsoluteURI(new URI(expected)));
    }

    @Test
    public void openStreamTest() throws IOException {
        String resPath = "./src/test/resources/com/itextpdf/io/util/textFile.dat";
        InputStream openStream = UrlUtil.openStream(new File(resPath).toURI().toURL());

        String actual = new String(StreamUtil.inputStreamToArray(openStream), StandardCharsets.UTF_8);
        Assertions.assertEquals("Hello world from text file!", actual);
    }

    @Test
    // Android-Conversion-Ignore-Test DEVSIX-6459 Some different random connect exceptions on Android
    public void openStreamReadTimeoutTest() throws Exception {
        TestResource thread = new TestResource();
        thread.start();
        while (!thread.isStarted() && !thread.isFailed()) {
            Thread.sleep(250);
        }
        Assertions.assertFalse(thread.failed);
        Inet4Address.getLocalHost().getHostAddress();
        URL url = new URL("http://127.0.0.1:" + thread.port + "/");
        Exception e = Assertions.assertThrows(java.net.SocketTimeoutException.class,
                () -> UrlUtil.getInputStreamOfFinalConnection(url, 0, 500)
        );
        Assertions.assertEquals("read timed out", StringNormalizer.toLowerCase(e.getMessage()));
    }

    @Test
    // Android-Conversion-Ignore-Test DEVSIX-6459 Some different random connect exceptions on Android
    public void openStreamConnectTimeoutTest() throws IOException {
        URL url = new URL("http://10.255.255.1/");

        Exception e = Assertions.assertThrows(java.net.SocketTimeoutException.class,
                () -> UrlUtil.getInputStreamOfFinalConnection(url, 500, 0)
        );
        Assertions.assertEquals("connect timed out", StringNormalizer.toLowerCase(e.getMessage()));
    }

    private static class TestResource extends Thread {
        private int port = 8000;
        private boolean started = false;
        private boolean failed = false;
        @Override
        public void run() {
            try {
                startServer();
            } catch (IOException | InterruptedException e) {
                failed = true;
                System.out.println("Error starting ServerSocket: " + e);
                throw new RuntimeException(e);
            }
        }

        public int getPort() {
            return port;
        }

        public boolean isStarted() {
            return started;
        }

        public boolean isFailed() {
            return failed;
        }

        private void startServer() throws IOException, InterruptedException {
            int tryCount = 0;
            while (!started) {
                try (ServerSocket server = new ServerSocket(port, 10, Inet4Address.getLoopbackAddress())) {
                    tryCount++;
                    started = true;
                    server.setSoTimeout(20000);
                    started = true;
                    try (Socket clientSocket = server.accept()) {
                        Thread.sleep(1000);
                        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                        String response = "HTTP/1.1 OK OKrnContent-Type: text/html; charset=UTF-8rnrn" +
                                "<!DOCTYPE html>\n" +
                                "<html>\n" +
                                "<body>\n" +
                                "\n" +
                                "</body>\n" +
                                "</html>\n";
                        out.print(response);
                        out.flush();
                        server.accept();
                    }
                } catch (BindException ex) {
                    if (tryCount > 100) {
                        failed = true;
                        throw ex;
                    }
                    port++;
                }
            }
        }
    }
}
