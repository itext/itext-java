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

import com.itextpdf.test.pdfa.VeraPdfValidator;
import com.itextpdf.test.utils.UriUtil;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.Executors;

/**
 * This class is a simple HTTP server that can be used to validate PDF files using VeraPDF.
 * It can be started in two modes:
 * - CLI mode: `java -jar VeraPdfValidatorApp.jar cli "b64encoded path to pdf"`
 * - Server mode: `java -jar VeraPdfValidatorApp.jar server "port"`
 */
// Android-Conversion-Skip-File
public class VeraPdfValidatorApp {

    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(VeraPdfValidatorApp.class);

    private final ConsoleWriter consoleWriter;

    private final String[] args;


    /**
     * Creates a new instance of VeraPdfValidatorApp.
     *
     * @param consoleWriter the console writer to use.
     * @param args          the arguments to use.
     */
    VeraPdfValidatorApp(ConsoleWriter consoleWriter, String[] args) {
        this.consoleWriter = consoleWriter;
        this.args = args;
    }

    /**
     * Main method to run the application.
     *
     * @param args the arguments to use.
     * @throws IOException if an I/O error occurs.
     */
    public static void main(String[] args) throws IOException {
        new VeraPdfValidatorApp(System.out::println, args).run();
    }

    private static boolean isPortAvailable(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            serverSocket.setReuseAddress(true);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Run the application based on the provided arguments.
     *
     * @throws IOException if an I/O error occurs.
     */
    public void run() throws IOException {
        if (args.length == 0) {
            LOGGER.error("No arguments provided");
            return;
        }
        switch (args[0]) {
            case "cli":
                if (args.length < 2) {
                    LOGGER.error("No path provided");
                    return;
                }
                runCli(args[1]);
                break;
            case "server":
                if (args.length < 2) {
                    LOGGER.error("No port provided");
                    return;
                }
                startServer(args[1]);
                break;
            default:
                LOGGER.error("Invalid command, either use 'cli <b64encoded path to pdf>'\n" + "or 'server <port>'");
        }
    }

    private void startServer(String port) throws IOException {
        try {
            int portInt = Integer.parseInt(port);
            if (!isPortAvailable(portInt)) {
                LOGGER.info("Port {} is in use, stopping execution", portInt);
                return;
            }
            final HttpServer server = HttpServer.create(new InetSocketAddress(portInt), 0);

            // Define routes
            server.createContext("/api/validate", new VerifyHandler());
            server.createContext("/api/status", exchange -> {
                String response = VeraPdfValidatorApp.class.getSimpleName();
                exchange.sendResponseHeaders(200, response.length());
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response.getBytes(StandardCharsets.UTF_8));
                }
                exchange.close();
            });
            server.createContext("/api/stop", exchange -> {
                exchange.sendResponseHeaders(200, 0);
                exchange.close();
                server.stop(0);
                LOGGER.info("Server stopping");
                System.exit(0);
            });

            // Use a thread pool for better performance
            server.setExecutor(Executors.newSingleThreadExecutor());
            LOGGER.info("Server started on port {}", portInt);
            server.start();
        } catch (NumberFormatException ignored) {
        }
    }

    private void runCli(String pathB64) {
        //Don't log anything, as the output is parsed by the caller
        VeraPdfValidator validator = new VeraPdfValidator();
        validator.setLogToConsole(false);
        String decodedPath = new String(Base64.getDecoder().decode(pathB64), StandardCharsets.UTF_8);
        String result = validator.validate(decodedPath);
        if (result != null) {
            consoleWriter.write(result);
        }
    }

    /**
     * Interface for writing to the console.
     */
    public interface ConsoleWriter {
        /**
         * Writes a message to the console.
         *
         * @param message The message to write.
         */
        void write(String message);
    }

    static class VerifyHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            final String paramPath = "pathB64";
            if (!"GET".equals(exchange.getRequestMethod())) {
                // Method Not Allowed
                exchange.sendResponseHeaders(405, 0);
                return;
            }

            Map<String, String> params = UriUtil.parseQueryParams(exchange.getRequestURI().getQuery());
            if (!params.containsKey(paramPath)) {
                exchange.sendResponseHeaders(400, 0);
                return;
            }

            String decodedPath = new String(Base64.getDecoder().decode(params.get(paramPath)), StandardCharsets.UTF_8);
            VeraPdfValidator validator = new VeraPdfValidator();
            String result = validator.validate(decodedPath);
            // If the result is null, the validation is ok, but we need to return an empty response and null is
            // not a valid response for the HTTP server so we return no body.
            if (result != null) {
                byte[] responseB64 = Base64.getEncoder().encode(result.getBytes(StandardCharsets.UTF_8));
                exchange.sendResponseHeaders(200, responseB64.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(responseB64);
                }
            } else {
                exchange.sendResponseHeaders(200, 0);
            }
            exchange.close();
        }
    }
}
