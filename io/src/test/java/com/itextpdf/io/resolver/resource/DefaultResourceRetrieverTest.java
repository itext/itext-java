/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2025 Apryse Group NV
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
package com.itextpdf.io.resolver.resource;

import com.itextpdf.commons.utils.StringNormalizer;
import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;

@Tag("IntegrationTest")
class DefaultResourceRetrieverTest extends ExtendedITextTest {

    @Test
    // Android-Conversion-Ignore-Test DEVSIX-6459 Some different random connect exceptions on Android
    public void retrieveResourceReadTimeoutTest() throws IOException, InterruptedException {
        URL url = new URL("http://127.0.0.1:8080/");
        Thread thread = new TestResource();
        thread.start();

        Thread.sleep(250);
        DefaultResourceRetriever resourceRetriever = new DefaultResourceRetriever();
        resourceRetriever.setReadTimeout(500);

        Exception e = Assertions.assertThrows(java.net.SocketTimeoutException.class,
                () -> resourceRetriever.getInputStreamByUrl(url)
        );
        Assertions.assertEquals("read timed out", StringNormalizer.toLowerCase(e.getMessage()));
    }

    @Test
    // Android-Conversion-Ignore-Test DEVSIX-6459 Some different random connect exceptions on Android
    public void retrieveResourceConnectTimeoutTest() throws IOException {
        URL url = new URL("http://10.255.255.1/");

        DefaultResourceRetriever resourceRetriever = new DefaultResourceRetriever();
        resourceRetriever.setConnectTimeout(500);

        Exception e = Assertions.assertThrows(java.net.SocketTimeoutException.class,
                () -> resourceRetriever.getInputStreamByUrl(url)
        );
        Assertions.assertEquals("connect timed out", StringNormalizer.toLowerCase(e.getMessage()));
    }

    @Test
    public void connectTimeoutTest() {
        DefaultResourceRetriever resourceRetriever = new DefaultResourceRetriever();
        resourceRetriever.setConnectTimeout(500);
        Assertions.assertEquals(500, resourceRetriever.getConnectTimeout());
    }

    @Test
    public void readTimeoutTest() {
        DefaultResourceRetriever resourceRetriever = new DefaultResourceRetriever();
        resourceRetriever.setReadTimeout(500);
        Assertions.assertEquals(500, resourceRetriever.getReadTimeout());
    }

    @Test
    public void setResourceByLimitTest() {
        DefaultResourceRetriever resourceRetriever = new DefaultResourceRetriever();
        resourceRetriever.setResourceSizeByteLimit(1000);
        Assertions.assertEquals(1000, resourceRetriever.getResourceSizeByteLimit());
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(
                    messageTemplate = IoLogMessageConstant.RESOURCE_WITH_GIVEN_URL_WAS_FILTERED_OUT)
    })
    public void filterOutFilteredResourcesTest() throws IOException {
        DefaultResourceRetriever resourceRetriever = new FilteredResourceRetriever();
        Assertions.assertFalse(resourceRetriever.urlFilter(new URL("https://example.com/resource")));

        Assertions.assertNull(resourceRetriever.getInputStreamByUrl(new URL("https://example.com/resource")));
    }

    @Test
    // Android-Conversion-Ignore-Test DEVSIX-6459 Some different random connect exceptions on Android
    public void loadGetByteArrayByUrl() throws IOException {
        DefaultResourceRetriever resourceRetriever = new DefaultResourceRetriever();
        byte[] data = resourceRetriever.getByteArrayByUrl(new URL("https://example.com/"));
        Assertions.assertNotNull(data);
        Assertions.assertTrue(data.length > 0);
    }


    private static class FilteredResourceRetriever extends DefaultResourceRetriever {
        @Override
        public boolean urlFilter(URL url) {
            return false; // Filter all resources
        }
    }


    private static class TestResource extends Thread {
        @Override
        public void run() {
            try {
                startServer();
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        private void startServer() throws IOException, InterruptedException {
            ServerSocket server = new ServerSocket(8080);
            Socket clientSocket = server.accept();
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
        }
    }
}