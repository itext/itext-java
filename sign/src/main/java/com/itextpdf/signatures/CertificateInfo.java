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
package com.itextpdf.signatures;

import com.itextpdf.bouncycastleconnector.BouncyCastleFactoryCreator;
import com.itextpdf.commons.bouncycastle.IBouncyCastleFactory;
import com.itextpdf.commons.bouncycastle.asn1.IASN1InputStream;
import com.itextpdf.commons.bouncycastle.asn1.IASN1ObjectIdentifier;
import com.itextpdf.commons.bouncycastle.asn1.IASN1Primitive;
import com.itextpdf.commons.bouncycastle.asn1.IASN1Sequence;
import com.itextpdf.commons.bouncycastle.asn1.IASN1Set;
import com.itextpdf.kernel.exceptions.PdfException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class containing static methods that allow you to get information from
 * an X509 Certificate: the issuer and the subject.
 */
public class CertificateInfo {

    private static final IBouncyCastleFactory BOUNCY_CASTLE_FACTORY = BouncyCastleFactoryCreator.getFactory();

    // Inner classes

    /**
     * Class that holds an X509 name.
     */
    public static class X500Name {
        /**
         * Country code - StringType(SIZE(2)).
         */
        public static final IASN1ObjectIdentifier C = BOUNCY_CASTLE_FACTORY.createASN1ObjectIdentifier("2.5.4.6");

        /**
         * Organization - StringType(SIZE(1..64)).
         */
        public static final IASN1ObjectIdentifier O = BOUNCY_CASTLE_FACTORY.createASN1ObjectIdentifier("2.5.4.10");

        /**
         * Organizational unit name - StringType(SIZE(1..64)).
         */
        public static final IASN1ObjectIdentifier OU = BOUNCY_CASTLE_FACTORY.createASN1ObjectIdentifier("2.5.4.11");

        /**
         * Title.
         */
        public static final IASN1ObjectIdentifier T = BOUNCY_CASTLE_FACTORY.createASN1ObjectIdentifier("2.5.4.12");

        /**
         * Common name - StringType(SIZE(1..64)).
         */
        public static final IASN1ObjectIdentifier CN = BOUNCY_CASTLE_FACTORY.createASN1ObjectIdentifier("2.5.4.3");

        /**
         * Device serial number name - StringType(SIZE(1..64)).
         */
        public static final IASN1ObjectIdentifier SN = BOUNCY_CASTLE_FACTORY.createASN1ObjectIdentifier("2.5.4.5");

        /**
         * Locality name - StringType(SIZE(1..64)).
         */
        public static final IASN1ObjectIdentifier L = BOUNCY_CASTLE_FACTORY.createASN1ObjectIdentifier("2.5.4.7");

        /**
         * State, or province name - StringType(SIZE(1..64)).
         */
        public static final IASN1ObjectIdentifier ST = BOUNCY_CASTLE_FACTORY.createASN1ObjectIdentifier("2.5.4.8");

        /**
         * Naming attribute of type X520name.
         */
        public static final IASN1ObjectIdentifier SURNAME = BOUNCY_CASTLE_FACTORY.createASN1ObjectIdentifier("2.5.4.4");

        /**
         * Naming attribute of type X520name.
         */
        public static final IASN1ObjectIdentifier GIVENNAME = BOUNCY_CASTLE_FACTORY.createASN1ObjectIdentifier(
                "2.5.4.42");

        /**
         * Naming attribute of type X520name.
         */
        public static final IASN1ObjectIdentifier INITIALS = BOUNCY_CASTLE_FACTORY.createASN1ObjectIdentifier(
                "2.5.4.43");

        /**
         * Naming attribute of type X520name.
         */
        public static final IASN1ObjectIdentifier GENERATION = BOUNCY_CASTLE_FACTORY.createASN1ObjectIdentifier(
                "2.5.4.44");

        /**
         * Naming attribute of type X520name.
         */
        public static final IASN1ObjectIdentifier UNIQUE_IDENTIFIER = BOUNCY_CASTLE_FACTORY.createASN1ObjectIdentifier(
                "2.5.4.45");

        /**
         * Email address (RSA PKCS#9 extension) - IA5String.
         * <p>
         * Note: if you're trying to be ultra orthodox, don't use this! It shouldn't be in here.
         */
        public static final IASN1ObjectIdentifier EmailAddress = BOUNCY_CASTLE_FACTORY.createASN1ObjectIdentifier(
                "1.2.840.113549.1.9.1");

        /**
         * Email address in Verisign certificates.
         */
        public static final IASN1ObjectIdentifier E = EmailAddress;

        /**
         * Object identifier.
         */
        public static final IASN1ObjectIdentifier DC = BOUNCY_CASTLE_FACTORY.createASN1ObjectIdentifier(
                "0.9.2342.19200300.100.1.25");

        /**
         * LDAP User id.
         */
        public static final IASN1ObjectIdentifier UID = BOUNCY_CASTLE_FACTORY.createASN1ObjectIdentifier(
                "0.9.2342.19200300.100.1.1");

        /**
         * A Map with default symbols.
         */
        public static final Map<IASN1ObjectIdentifier, String> DefaultSymbols = new HashMap<>();

        static {
            DefaultSymbols.put(C, "C");
            DefaultSymbols.put(O, "O");
            DefaultSymbols.put(T, "T");
            DefaultSymbols.put(OU, "OU");
            DefaultSymbols.put(CN, "CN");
            DefaultSymbols.put(L, "L");
            DefaultSymbols.put(ST, "ST");
            DefaultSymbols.put(SN, "SN");
            DefaultSymbols.put(EmailAddress, "E");
            DefaultSymbols.put(DC, "DC");
            DefaultSymbols.put(UID, "UID");
            DefaultSymbols.put(SURNAME, "SURNAME");
            DefaultSymbols.put(GIVENNAME, "GIVENNAME");
            DefaultSymbols.put(INITIALS, "INITIALS");
            DefaultSymbols.put(GENERATION, "GENERATION");
        }

        /**
         * A Map with values.
         */
        private final Map<String, List<String>> values = new HashMap<>();

        /**
         * Constructs an X509 name.
         *
         * @param seq an ASN1 Sequence
         */
        public X500Name(IASN1Sequence seq) {
            @SuppressWarnings("unchecked")
            Enumeration e = seq.getObjects();

            while (e.hasMoreElements()) {
                IASN1Set set = BOUNCY_CASTLE_FACTORY.createASN1Set(e.nextElement());

                for (int i = 0; i < set.size(); i++) {
                    IASN1Sequence s = BOUNCY_CASTLE_FACTORY.createASN1Sequence(set.getObjectAt(i));
                    String id = DefaultSymbols.get(BOUNCY_CASTLE_FACTORY.createASN1ObjectIdentifier(s.getObjectAt(0)));
                    if (id != null) {
                        List<String> vs = values.get(id);
                        if (vs == null) {
                            vs = new ArrayList<>();
                            values.put(id, vs);
                        }
                        vs.add((BOUNCY_CASTLE_FACTORY.createASN1String(s.getObjectAt(1))).getString());
                    }
                }
            }
        }

        /**
         * Constructs an X509 name.
         *
         * @param dirName a directory name
         */
        public X500Name(String dirName) {
            CertificateInfo.X509NameTokenizer nTok = new CertificateInfo.X509NameTokenizer(dirName);

            while (nTok.hasMoreTokens()) {
                String token = nTok.nextToken();
                int index = token.indexOf('=');

                if (index == -1) {
                    throw new IllegalArgumentException(/*MessageLocalization.getComposedMessage("badly.formated
                    .directory.string")*/);
                }

                String id = token.substring(0, index).toUpperCase();
                String value = token.substring(index + 1);
                List<String> vs = values.get(id);
                if (vs == null) {
                    vs = new ArrayList<>();
                    values.put(id, vs);
                }
                vs.add(value);
            }
        }

        /**
         * Gets the first entry from the field array retrieved from the values Map.
         *
         * @param name the field name
         *
         * @return the (first) field value
         */
        public String getField(String name) {
            List<String> vs = values.get(name);
            return vs == null ? null : (String) vs.get(0);
        }

        /**
         * Gets a field array from the values Map.
         *
         * @param name The field name
         *
         * @return List
         */
        public List<String> getFieldArray(String name) {
            return values.get(name);
        }

        /**
         * Getter for values.
         *
         * @return Map with the fields of the X509 name
         */
        public Map<String, List<String>> getFields() {
            return values;
        }

        @Override
        public String toString() {
            return values.toString();
        }
    }

    /**
     * Class for breaking up an X500 Name into it's component tokens, similar to {@link java.util.StringTokenizer}.
     * We need this class as some of the lightweight Java environments don't support classes such as StringTokenizer.
     */
    public static class X509NameTokenizer {
        private String oid;
        private int index;
        private StringBuffer buf = new StringBuffer();

        /**
         * Creates an X509NameTokenizer.
         *
         * @param oid the oid that needs to be parsed
         */
        public X509NameTokenizer(String oid) {
            this.oid = oid;
            this.index = -1;
        }

        /**
         * Checks if the tokenizer has any tokens left.
         *
         * @return true if there are any tokens left, false if there aren't
         */
        public boolean hasMoreTokens() {
            return index != oid.length();
        }

        /**
         * Returns the next token.
         *
         * @return the next token
         */
        public String nextToken() {
            if (index == oid.length()) {
                return null;
            }

            int end = index + 1;
            boolean quoted = false;
            boolean escaped = false;

            buf.setLength(0);

            while (end != oid.length()) {
                char c = oid.charAt(end);

                if (c == '"') {
                    if (escaped) {
                        buf.append(c);
                    } else {
                        quoted = !quoted;
                    }
                    escaped = false;
                } else {
                    if (escaped || quoted) {
                        buf.append(c);
                        escaped = false;
                    } else if (c == '\\') {
                        escaped = true;
                    } else if (c == ',') {
                        break;
                    } else {
                        buf.append(c);
                    }
                }
                end++;
            }

            index = end;
            return buf.toString().trim();
        }
    }

    // Certificate issuer

    /**
     * Get the issuer fields from an X509 Certificate.
     *
     * @param cert an X509Certificate
     *
     * @return an X500Name
     */
    public static X500Name getIssuerFields(X509Certificate cert) {
        try {
            return new X500Name(
                    BOUNCY_CASTLE_FACTORY.createASN1Sequence(CertificateInfo.getIssuer(cert.getTBSCertificate())));
        } catch (Exception e) {
            throw new PdfException(e);
        }
    }

    /**
     * Get the "issuer" from the TBSCertificate bytes that are passed in.
     *
     * @param enc a TBSCertificate in a byte array
     *
     * @return an IASN1Primitive
     */
    public static IASN1Primitive getIssuer(byte[] enc) {
        try {
            IASN1Sequence seq;
            try (IASN1InputStream in = BOUNCY_CASTLE_FACTORY.createASN1InputStream(new ByteArrayInputStream(enc))) {
                seq = BOUNCY_CASTLE_FACTORY.createASN1Sequence(in.readObject());
            }
            return BOUNCY_CASTLE_FACTORY.createASN1Primitive(
                    seq.getObjectAt(BOUNCY_CASTLE_FACTORY.createASN1TaggedObject(seq.getObjectAt(0)) == null ? 2 : 3));
        } catch (IOException e) {
            throw new PdfException(e);
        }
    }

    // Certificate Subject

    /**
     * Get the subject fields from an X509 Certificate.
     *
     * @param cert an X509Certificate
     *
     * @return an X500Name
     */
    public static X500Name getSubjectFields(X509Certificate cert) {
        try {
            if (cert != null) {
                return new X500Name(
                        BOUNCY_CASTLE_FACTORY.createASN1Sequence(CertificateInfo.getSubject(cert.getTBSCertificate())));
            }
        } catch (Exception e) {
            throw new PdfException(e);
        }
        return null;
    }

    /**
     * Get the "subject" from the TBSCertificate bytes that are passed in.
     *
     * @param enc A TBSCertificate in a byte array
     *
     * @return a IASN1Primitive
     */
    public static IASN1Primitive getSubject(byte[] enc) {
        try {
            IASN1Sequence seq;
            try (IASN1InputStream in = BOUNCY_CASTLE_FACTORY.createASN1InputStream(new ByteArrayInputStream(enc))) {
                seq = BOUNCY_CASTLE_FACTORY.createASN1Sequence(in.readObject());
            }
            return BOUNCY_CASTLE_FACTORY.createASN1Primitive(
                    seq.getObjectAt(BOUNCY_CASTLE_FACTORY.createASN1TaggedObject(seq.getObjectAt(0)) == null ? 4 : 5));
        } catch (IOException e) {
            throw new PdfException(e);
        }
    }
}
