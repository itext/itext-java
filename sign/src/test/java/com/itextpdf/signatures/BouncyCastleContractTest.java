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
package com.itextpdf.signatures;

import com.itextpdf.bouncycastleconnector.BouncyCastleFactoryCreator;
import com.itextpdf.commons.bouncycastle.IBouncyCastleFactory;
import com.itextpdf.commons.bouncycastle.asn1.IASN1Primitive;
import com.itextpdf.commons.bouncycastle.asn1.ocsp.IBasicOCSPResponse;
import com.itextpdf.commons.bouncycastle.cert.ocsp.IBasicOCSPResp;
import com.itextpdf.commons.bouncycastle.cert.ocsp.ISingleResp;
import com.itextpdf.commons.utils.Base64;
import com.itextpdf.test.AssertUtil;
import com.itextpdf.test.ExtendedITextTest;

import java.io.IOException;
import java.security.Security;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * The aim of this test suite is to test compatibility with bouncy castle versions
 * and our assumptions around bouncy castle
 */

@Tag("BouncyCastleUnitTest")

public class BouncyCastleContractTest extends  ExtendedITextTest {

    private final static String OCSP_RESPONSE_NO_EXTENSIONS =
            "MIIJ1DCBtqIWBBSlSsYnGD/2cJEkPYlC3Yn+j4y/sBgPMjAyMzEwMjQwNzU5MzBaMF0wWzBCMAkGBSsO"
                    + "AwIaBQAEFCKaLxQhIQkIPsGoR8JEH9gl8SK9BBRjz3fZvAhlWlKKF9oe131ScgWjRwIJSA45/r2qTa8I"
                    + "gAAYDzIwMjMxMDI0MDc1OTMwWqECMAChLDAqMBcGCSsGAQUFBzABAgQKBAiqk9Hs20cZ4zAPBgkrBgEF"
                    + "BQcwAQkEAgUAMA0GCSqGSIb3DQEBDQUAA4IBAQChwMwQNYj5iEsXVSbFRGQ0vgXSfWOOdq0T3aGaRo63"
                    + "fAI5vF2yFaUd/qSm+msvlxhSB3yg0ZWhI9X0bi3ob9mEjXeMjQkiIsnApRwoOuDa4sLJCN+BV0U6ULev"
                    + "CGUK1zUor+r0euNStqBUzY3NuapxIkPyHLauN4L01gGHfhyYBjKsoSdbgQ0MbMcCa+AvYLNNAbLadE3K"
                    + "57EUjyFHoy/7WZxHVPvKDzr0UdxZaHk0PZUZurjT3a7/VGovCf5jZ+iay4MjvU7Cf5bdTDoDjIAoze1x"
                    + "tietC3CxSRHQr7Omk69TBnQpLXVQwRb4EVZH3uqjO+K3qmnTKGshD5HBGPV4oIIIAzCCB/8wggf7MIIF"
                    + "46ADAgECAglmWaXj47QjgJgwDQYJKoZIhvcNAQENBQAwfzELMAkGA1UEBhMCUlMxEDAOBgNVBAcMB0Jl"
                    + "b2dyYWQxGDAWBgNVBGEMD1ZBVFJTLTEwMDAwMjgwMzEnMCUGA1UECgweSmF2bm8gcHJlZHV6ZcSHZSBQ"
                    + "b8WhdGEgU3JiaWplMRswGQYDVQQDDBJQb8WhdGEgU3JiaWplIENBIDEwHhcNMTkwNjI2MTE1OTU1WhcN"
                    + "MjQwNjI2MTE1OTU1WjCBkDELMAkGA1UEBhMCUlMxFzAVBgNVBGEMDk1COlJTLTA3NDYxNDI5MRgwFgYD"
                    + "VQRhDA9WQVRSUy0xMDAwMDI4MDMxJzAlBgNVBAoMHkphdm5vIHByZWR1emXEh2UgUG/FoXRhIFNyYmlq"
                    + "ZTElMCMGA1UEAwwcUG/FoXRhIFNyYmlqZSBPQ1NQIFJlc3BvbmRlcjCCASIwDQYJKoZIhvcNAQEBBQAD"
                    + "ggEPADCCAQoCggEBAK+B4cNtT0LmfO0PVpf3BTH7SLsnorgl5niQSakbYHkT7MZSA6frtAKcB/S+B4F1"
                    + "v6XoMWuvnXCbzsIDuWo68FAF5zIArJPUxVbZYAHovIG1LBVHIW2h/oSmumQWTa0DIEMkzddKGTvFj/x/"
                    + "XwaShuZKW3nmWx4YvyfbKXXoSqlPuYQnsTPDvxB6GYtdX/YAEFtVX0dCqhiX68rRQZOO+9BV1Thwru7h"
                    + "d3oTzgHZjGBBfEsymN9tWFicq9ueOBabiErhbky4tg1GFoqKS0XF52hq9R36969xZabngmrPOBznVEWg"
                    + "b41JJqqLABS+5qut68O4qni1D/9OJ+lLWbxXf08CAwEAAaOCA2YwggNiMAkGA1UdEwQCMAAwDgYDVR0P"
                    + "AQH/BAQDAgbAMBMGA1UdJQQMMAoGCCsGAQUFBwMJMB8GA1UdIwQYMBaAFGPPd9m8CGVaUooX2h7XfVJy"
                    + "BaNHMB0GA1UdDgQWBBSlSsYnGD/2cJEkPYlC3Yn+j4y/sDCCARIGA1UdHwSCAQkwggEFMIIBAaCB/qCB"
                    + "+4aBwmxkYXA6Ly9sZGFwLW9jc3AuY2EucG9zdGEucnMvQ049UG8lYzUlYTF0YSUyMFNyYmlqZSUyMENB"
                    + "JTIwMSxPPUphdm5vJTIwcHJlZHV6ZSVjNCU4N2UlMjBQbyVjNSVhMXRhJTIwU3JiaWplLG9yZ2FuaXph"
                    + "dGlvbklkZW50aWZpZXI9VkFUUlMtMTAwMDAyODAzLEw9QmVvZ3JhZCxDPVJTP2NlcnRpZmljYXRlUmV2"
                    + "b2NhdGlvbkxpc3Q7YmluYXJ5hjRodHRwOi8vcmVwb3NpdG9yeS5jYS5wb3N0YS5ycy9jcmwvUG9zdGFT"
                    + "cmJpamVDQTEuY3JsMIIBVAYIKwYBBQUHAQEEggFGMIIBQjCBwwYIKwYBBQUHMAKGgbZsZGFwOi8vbGRh"
                    + "cC1vY3NwLmNhLnBvc3RhLnJzL0NOPVBvJWM1JWExdGElMjBTcmJpamUlMjBDQSUyMDEsTz1KYXZubyUy"
                    + "MHByZWR1emUlYzQlODdlJTIwUG8lYzUlYTF0YSUyMFNyYmlqZSxvcmdhbml6YXRpb25JZGVudGlmaWVy"
                    + "PVZBVFJTLTEwMDAwMjgwMyxMPUJlb2dyYWQsQz1SUz9jQUNlcnRpZmljYXRlO2JpbmFyeTBLBggrBgEF"
                    + "BQcwAoY/aHR0cDovL3JlcG9zaXRvcnkuY2EucG9zdGEucnMvY2Etc2VydGlmaWthdGkvUG9zdGFTcmJp"
                    + "amVDQTEuZGVyMC0GCCsGAQUFBzABhiFodHRwOi8vbGRhcC1vY3NwLmNhLnBvc3RhLnJzL29jc3AwTgYD"
                    + "VR0gBEcwRTBDBgwrBgEEAfo4CoMcAQAwMzAxBggrBgEFBQcCARYlaHR0cHM6Ly93d3cuY2EucG9zdGEu"
                    + "cnMvZG9rdW1lbnRhY2lqYTAhBgNVHREEGjAYgRZjYS1wb2Ryc2thQGNhLnBvc3RhLnJzMA8GCSsGAQUF"
                    + "BzABBQQCBQAwDQYJKoZIhvcNAQENBQADggIBAIa9Bc2gDwzb5/mg13w5eIaBz9BI0fs47OMq+4phpR2T"
                    + "0PTDMV86yF7ZVQaWbUUY8oo1puHsdFOeEY/k+SB+Ke0vcWqiBm5+sltpGGCwgx2bPQDqIvNFjHHdR8A7"
                    + "bEyiGuJPlGC6v9r1jNX8ktXVXxksMxZKgmSRe9LJD6xYzbGSlUYWLpNFLluYF+exQwmpLvKQMDrk58Fw"
                    + "17Xsa2or4LABT/K2aurGDDdonUNwiTRjxrcLCNTuuUd4/JBui0d1eLQqYxyVsVdyHjuvF4jHTpPSwAIq"
                    + "pZqekYrF1QGVtr6R6xoAHf6bz+kgS+xbnZluFuM8kh/mHg5D8U0jY0ffvlz4NSt8iddBRXqcSTeCO6QV"
                    + "bSIIR3IEwpVKcf4SgXP6HieBfG4sTMDCXZomA7L3jJP3VTQmZgnhHJera/oRaZmMf5U+wzRRjFx0FTQF"
                    + "eQ2EebzBIG+v3C3xeTmaZjL4JTtEVrEtuB9kzEKVU47sNrvU3GDsG+D3XsxoI11kYhOhpYyXyG5nP+2E"
                    + "GS9VrU5N2wLrTEo5q1Z8PtP0S6dSbBjRm+XNJP982QQIG4inbi8YY2c1vch+oNOmu2JhQxHXbFDYZsUR"
                    + "o2bvkq6Sq29RT6jN6U9iVe1WvTItMbhEPk0c6XCKWXcQMp21Qg5lk0IrJVkQVQGIeEogh/OVBvUvLvIS";

    private static final IBouncyCastleFactory FACTORY = BouncyCastleFactoryCreator.getFactory();

    @Test
    public void ocspResponseWithMissingExtensionIsParsedProperlyTest() throws IOException {

        byte[] encodedResponse = Base64.decode(OCSP_RESPONSE_NO_EXTENSIONS);
        IASN1Primitive asn1Primitive = FACTORY.createASN1Primitive(encodedResponse);
        IBasicOCSPResponse basicReponse = FACTORY.createBasicOCSPResponse(asn1Primitive);
        IBasicOCSPResp ocspResponse = FACTORY.createBasicOCSPResp(basicReponse);
        AssertUtil.doesNotThrow( ()-> ocspResponse.getResponses());
    }
}
