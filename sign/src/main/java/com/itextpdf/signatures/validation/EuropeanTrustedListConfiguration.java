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
package com.itextpdf.signatures.validation;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

// TODO DEVSIX-9218 Remove this class when repository with EU Journal certificates is released.
class EuropeanTrustedListConfiguration {
    private static final String euTrustedListUrl = "https://ec.europa.eu/tools/lotl/eu-lotl.xml";

    private final URI euTrustedListUri;

    /**
     * Initializes a new instance of the {@code EuTrustedListConfig} class.
     */
    public EuropeanTrustedListConfiguration() {
        try {
            euTrustedListUri = new URI(euTrustedListUrl);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Returns the URI of the European Union Trusted List.
     *
     * @return the URI of the trusted list
     */
    public URI getTrustedListUri() {
        return euTrustedListUri;
    }


    /**
     * Returns a list of certificates.
     *
     * @return a list of certificate strings
     */
    public List<PemCertificateWithHash> getCertificates() {
        return Arrays.asList(
                new PemCertificateWithHash(
                        Certificates2019C27601.certificate1,
                        Certificates2019C27601.certificate1SHA256),
                new PemCertificateWithHash(Certificates2019C27601.certificate2,
                        Certificates2019C27601.certificate2SHA256),
                new PemCertificateWithHash(Certificates2019C27601.certificate3,
                        Certificates2019C27601.certificate3SHA256),
                new PemCertificateWithHash(Certificates2019C27601.certificate4,
                        Certificates2019C27601.certificate4SHA256),
                new PemCertificateWithHash(Certificates2019C27601.certificate5,
                        Certificates2019C27601.certificate5SHA256),
                new PemCertificateWithHash(Certificates2019C27601.certificate6,
                        Certificates2019C27601.certificate6SHA256),
                new PemCertificateWithHash(Certificates2019C27601.certificate7,
                        Certificates2019C27601.certificate7SHA256),
                new PemCertificateWithHash(Certificates2019C27601.certificate8,
                        Certificates2019C27601.certificate8SHA256)
        );
    }

    /**
     * Represents a PEM certificate along with its SHA-256 base64 encoded hash.
     */
    public static class PemCertificateWithHash {
        private final String pemCertificate;
        private final String hash;

        /**
         * Constructs a new instance of {@code PemCertificateWithHash}.
         *
         * @param pemCertificate the PEM formatted certificate
         * @param hash           the SHA-256 base64 encoded hash of the certificate
         */
        public PemCertificateWithHash(String pemCertificate, String hash) {
            this.pemCertificate = pemCertificate;
            this.hash = hash;
        }

        /**
         * Returns the PEM formatted certificate.
         *
         * @return the PEM certificate
         */
        public String getPemCertificate() {
            return pemCertificate;
        }

        /**
         * Returns the SHA-256 hash base64 encoded of the certificate.
         *
         * @return the hash of the certificate
         */
        public String getHash() {
            return hash;
        }
    }
}
