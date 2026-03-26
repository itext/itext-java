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
package com.itextpdf.signatures.validation;

import com.itextpdf.io.resolver.resource.DefaultResourceRetriever;
import com.itextpdf.signatures.CrlClientOnline;
import com.itextpdf.signatures.IssuingCertificateRetriever;
import com.itextpdf.signatures.OcspClientBouncyCastle;
import com.itextpdf.test.ExtendedITextTest;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("UnitTest")
public class ValidatorChainBuilderTest extends ExtendedITextTest {

    @Test
    public void defaultClientsTest() {
        ValidatorChainBuilder builder = new ValidatorChainBuilder();
        
        Assertions.assertEquals(DefaultResourceRetriever.class,
                ((CrlClientOnline) builder.getCrlClient()).getResourceRetriever().getClass());
        Assertions.assertEquals(DefaultResourceRetriever.class,
                ((OcspClientBouncyCastle) builder.getOcspClient()).getResourceRetriever().getClass());
        Assertions.assertEquals(DefaultResourceRetriever.class,
                builder.getCertificateRetriever().getResourceRetriever().getClass());
    }

    @Test
    public void customDeprecatedRetrieverUsedOnlyInCertRetrieverTest() {
        ValidatorChainBuilder builder = new ValidatorChainBuilder()
                .withResourceRetriever(() -> new com.itextpdf.styledxmlparser.resolver.resource.IResourceRetriever() {
            @Override
            public InputStream getInputStreamByUrl(URL url) throws IOException {
                return null;
            }

            @Override
            public byte[] getByteArrayByUrl(URL url) throws IOException {
                return new byte[0];
            }
        });

        Assertions.assertEquals(DefaultResourceRetriever.class,
                ((CrlClientOnline) builder.getCrlClient()).getResourceRetriever().getClass());
        Assertions.assertEquals(DefaultResourceRetriever.class,
                ((OcspClientBouncyCastle) builder.getOcspClient()).getResourceRetriever().getClass());
        Assertions.assertNull(builder.getCertificateRetriever().getResourceRetriever());
    }

    @Test
    public void customRetrieverUsedInDefaultClientsTest() {
        ValidatorChainBuilder builder = new ValidatorChainBuilder()
                .withAdvancedResourceRetriever(() -> new CustomResourceRetriever());

        Assertions.assertEquals(CustomResourceRetriever.class,
                ((CrlClientOnline) builder.getCrlClient()).getResourceRetriever().getClass());
        Assertions.assertEquals(CustomResourceRetriever.class,
                ((OcspClientBouncyCastle) builder.getOcspClient()).getResourceRetriever().getClass());
        Assertions.assertEquals(CustomResourceRetriever.class,
                builder.getCertificateRetriever().getResourceRetriever().getClass());
    }

    @Test
    public void customRetrieverNotUsedInCustomClientsTest() {
        ValidatorChainBuilder builder = new ValidatorChainBuilder()
                .withAdvancedResourceRetriever(() -> new CustomResourceRetriever())
                .withCrlClient(() -> new CrlClientOnline())
                .withOcspClient(() -> new OcspClientBouncyCastle())
                .withIssuingCertificateRetrieverFactory(() -> new IssuingCertificateRetriever());

        Assertions.assertEquals(DefaultResourceRetriever.class,
                ((CrlClientOnline) builder.getCrlClient()).getResourceRetriever().getClass());
        Assertions.assertEquals(DefaultResourceRetriever.class,
                ((OcspClientBouncyCastle) builder.getOcspClient()).getResourceRetriever().getClass());
        Assertions.assertEquals(DefaultResourceRetriever.class,
                builder.getCertificateRetriever().getResourceRetriever().getClass());
    }

    private static class CustomResourceRetriever extends DefaultResourceRetriever {
    }
}
