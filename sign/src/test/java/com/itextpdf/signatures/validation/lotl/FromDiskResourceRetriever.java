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
package com.itextpdf.signatures.validation.lotl;

import com.itextpdf.bouncycastleconnector.BouncyCastleFactoryCreator;
import com.itextpdf.commons.bouncycastle.IBouncyCastleFactory;
import com.itextpdf.io.resolver.resource.IResourceRetriever;
import com.itextpdf.kernel.crypto.DigestAlgorithms;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;

public class FromDiskResourceRetriever implements IResourceRetriever {

    private static final IBouncyCastleFactory BOUNCY_CASTLE_FACTORY = BouncyCastleFactoryCreator.getFactory();
    private final String resourcePath;

    public FromDiskResourceRetriever(String resourcePath) {
        this.resourcePath = resourcePath;
    }

    @Override
    public InputStream getInputStreamByUrl(URL url) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public byte[] getByteArrayByUrl(URL url) throws IOException {
        //escape url so it can be used as a complete filename
        String urlString = url.toString();
        urlString = urlString.replaceAll(" ", "%20");
        String fileName = urlString.toString().replaceAll("[^a-zA-Z0-9]", "_");

        String fileNameHash = createHash(fileName);
        String filePath = resourcePath + fileNameHash;
        if (Files.exists(Paths.get(filePath))) {
            return Files.readAllBytes(Paths.get(filePath));
        }

        return null;
    }

    private static String createHash(String input) {
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
            byte[] hash = DigestAlgorithms.digest(bais, DigestAlgorithms.SHA256, BOUNCY_CASTLE_FACTORY.getProviderName());
            StringBuilder stringBuilder = new StringBuilder();
            for (byte b : hash) {
                char c = (char) ('a' + (b & 0x0F) % 26);
                stringBuilder.append(c);
            }
            return stringBuilder.toString();
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException(e.getMessage());
        }

    }
}
