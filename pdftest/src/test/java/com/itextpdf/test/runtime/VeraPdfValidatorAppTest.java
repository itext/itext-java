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
package com.itextpdf.test.runtime;

import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.pdfa.VeraPdfValidator;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertEquals;

// Android-Conversion-Skip-File (TODO DEVSIX-7377 introduce pdf\a validation on Android)
@Tag("IntegrationTest")
public class VeraPdfValidatorAppTest extends ExtendedITextTest {

    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/test/VeraPdfValidatorAppTest/";

    private static final boolean isNative = System.getProperty("org.graalvm.nativeimage.imagecode") != null;

    @Test
    public void cliValidPdf() throws IOException {
        if (isNative){
            return;
        }
        String path = SOURCE_FOLDER + "testf001.pdf";
        String result1 = new VeraPdfValidator().validate(path);
        String pathB64 = Base64.getEncoder().encodeToString(path.getBytes());

        ToStringConsoleWriter consoleWriter = new ToStringConsoleWriter();
        VeraPdfValidatorApp app = new VeraPdfValidatorApp(consoleWriter, new String[]{"cli", pathB64});

        app.run();
        String result2 = consoleWriter.result;
        assertEquals(result1, result2);

    }

    @Test
    public void cliInvalidPdf() throws IOException {
        if (isNative){
            return;
        }
        String path = SOURCE_FOLDER + "testfail.pdf";
        String result1 = new VeraPdfValidator().validate(path);
        String pathB64 = Base64.getEncoder().encodeToString(path.getBytes());

        ToStringConsoleWriter consoleWriter = new ToStringConsoleWriter();
        VeraPdfValidatorApp app = new VeraPdfValidatorApp(consoleWriter, new String[]{"cli", pathB64});

        app.run();
        String result2 = consoleWriter.result;
        assertEquals(result1, result2);
    }


    @Test
    public void serverValidPdf() throws IOException, InterruptedException {
        if (isNative){
            return;
        }
        String path = SOURCE_FOLDER + "testf001.pdf";
        String result1 = new VeraPdfValidator().validate(path);
        String pathB64 = Base64.getEncoder().encodeToString(path.getBytes());

        ToStringConsoleWriter consoleWriter = new ToStringConsoleWriter();
        VeraPdfValidatorApp app = new VeraPdfValidatorApp(consoleWriter, new String[]{"server", "8079"});

        Thread thread = new Thread(() -> {
            try {
                app.run();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        thread.start();
        Thread.sleep(1000);

        //Http request to server  to validate pdf

        String uri = "http://localhost:8079/api/validate?pathB64=" + pathB64;
        String response = sendGetRequest(uri);
        thread.interrupt();
        String result2 = new String(Base64.getDecoder().decode(response));


        assertEquals(result1, result2);

    }

    @Test
    public void serverInValidPdf() throws IOException, InterruptedException {
        if (isNative){
            return;
        }
        String path = SOURCE_FOLDER + "testfail.pdf";
        String result1 = new VeraPdfValidator().validate(path);
        String pathB64 = Base64.getEncoder().encodeToString(path.getBytes());

        ToStringConsoleWriter consoleWriter = new ToStringConsoleWriter();
        VeraPdfValidatorApp app = new VeraPdfValidatorApp(consoleWriter, new String[]{"server", "8078"});

        Thread thread = new Thread(() -> {
            try {
                app.run();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        thread.start();

        Thread.sleep(1000);
        //Http request to server  to validate pdf

        String uri = "http://localhost:8078/api/validate?pathB64=" + pathB64;
        String response = sendGetRequest(uri);
        thread.interrupt();
        String result2 = new String(Base64.getDecoder().decode(response));
        assertEquals(result1, result2);

    }


    public static String sendGetRequest(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            return response.toString();
        } else {
            return "GET request failed with response code: " + responseCode;
        }
    }

    private static class ToStringConsoleWriter implements VeraPdfValidatorApp.ConsoleWriter {
        String result = "";

        @Override
        public void write(String message) {
            result += message;
        }
    }
}