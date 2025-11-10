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
package com.itextpdf.styledxmlparser.resolver.resource;

import com.itextpdf.commons.utils.StringNormalizer;
import com.itextpdf.test.ExtendedITextTest;

import java.net.BindException;
import java.net.InetAddress;
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
    @org.junit.jupiter.api.Disabled
    public void retrieveResourceReadTimeoutTest() throws IOException, InterruptedException {
        TestResource thread = new TestResource();
        thread.start();
        while (!thread.isStarted() && !thread.isFailed()) {
            Thread.sleep(250);
        }
        Assertions.assertFalse(thread.failed);
        URL url = new URL("http://127.0.0.1:" + thread.port + "/");

        DefaultResourceRetriever resourceRetriever = new DefaultResourceRetriever();
        resourceRetriever.setReadTimeout(500);

        Exception e = Assertions.assertThrows(java.net.SocketTimeoutException.class,
                () -> resourceRetriever.getInputStreamByUrl(url)
        );
        Assertions.assertEquals("read timed out", StringNormalizer.toLowerCase(e.getMessage()));
    }

    @Test
    @org.junit.jupiter.api.Disabled
    public void retrieveResourceConnectTimeoutTest() throws IOException {
        URL url = new URL("http://10.255.255.1/");

        DefaultResourceRetriever resourceRetriever = new DefaultResourceRetriever();
        resourceRetriever.setConnectTimeout(500);

        Exception e = Assertions.assertThrows(java.net.SocketTimeoutException.class,
                () -> resourceRetriever.getInputStreamByUrl(url)
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
                try (ServerSocket server = new ServerSocket(port, 10, InetAddress.getLoopbackAddress())) {
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