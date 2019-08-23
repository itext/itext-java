/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
    Authors: Bruno Lowagie, Paulo Soares, et al.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.

    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.

    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
 */
package com.itextpdf.signatures;

import com.itextpdf.kernel.PdfException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.ASN1String;
import org.bouncycastle.asn1.ASN1TaggedObject;

/**
 * Class containing static methods that allow you to get information from
 * an X509 Certificate: the issuer and the subject.
 */
public class CertificateInfo {

    // Inner classes

    /**
     * Class that holds an X509 name.
     */
    public static class X500Name {
        /**
         * Country code - StringType(SIZE(2)).
         */
        public static final ASN1ObjectIdentifier C = new ASN1ObjectIdentifier("2.5.4.6");

        /**
         * Organization - StringType(SIZE(1..64)).
         */
        public static final ASN1ObjectIdentifier O = new ASN1ObjectIdentifier("2.5.4.10");

        /**
         * Organizational unit name - StringType(SIZE(1..64)).
         */
        public static final ASN1ObjectIdentifier OU = new ASN1ObjectIdentifier("2.5.4.11");

        /**
         * Title.
         */
        public static final ASN1ObjectIdentifier T = new ASN1ObjectIdentifier("2.5.4.12");

        /**
         * Common name - StringType(SIZE(1..64)).
         */
        public static final ASN1ObjectIdentifier CN = new ASN1ObjectIdentifier("2.5.4.3");

        /**
         * Device serial number name - StringType(SIZE(1..64)).
         */
        public static final ASN1ObjectIdentifier SN = new ASN1ObjectIdentifier("2.5.4.5");

        /**
         * Locality name - StringType(SIZE(1..64)).
         */
        public static final ASN1ObjectIdentifier L = new ASN1ObjectIdentifier("2.5.4.7");

        /**
         * State, or province name - StringType(SIZE(1..64)).
         */
        public static final ASN1ObjectIdentifier ST = new ASN1ObjectIdentifier("2.5.4.8");

        /**
         * Naming attribute of type X520name.
         */
        public static final ASN1ObjectIdentifier SURNAME = new ASN1ObjectIdentifier("2.5.4.4");

        /**
         * Naming attribute of type X520name.
         */
        public static final ASN1ObjectIdentifier GIVENNAME = new ASN1ObjectIdentifier("2.5.4.42");

        /**
         * Naming attribute of type X520name.
         */
        public static final ASN1ObjectIdentifier INITIALS = new ASN1ObjectIdentifier("2.5.4.43");

        /**
         * Naming attribute of type X520name.
         */
        public static final ASN1ObjectIdentifier GENERATION = new ASN1ObjectIdentifier("2.5.4.44");

        /**
         * Naming attribute of type X520name.
         */
        public static final ASN1ObjectIdentifier UNIQUE_IDENTIFIER = new ASN1ObjectIdentifier("2.5.4.45");

        /**
         * Email address (RSA PKCS#9 extension) - IA5String.
         * <p>
         * Note: if you're trying to be ultra orthodox, don't use this! It shouldn't be in here.
         */
        public static final ASN1ObjectIdentifier EmailAddress = new ASN1ObjectIdentifier("1.2.840.113549.1.9.1");

        /**
         * Email address in Verisign certificates.
         */
        public static final ASN1ObjectIdentifier E = EmailAddress;

        /**
         * Object identifier.
         */
        public static final ASN1ObjectIdentifier DC = new ASN1ObjectIdentifier("0.9.2342.19200300.100.1.25");

        /**
         * LDAP User id.
         */
        public static final ASN1ObjectIdentifier UID = new ASN1ObjectIdentifier("0.9.2342.19200300.100.1.1");

        /**
         * A Map with default symbols.
         */
        public static final Map<ASN1ObjectIdentifier, String> DefaultSymbols = new HashMap<>();

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
        public Map<String, List<String>> values = new HashMap<>();

        /**
         * Constructs an X509 name.
         *
         * @param seq an ASN1 Sequence
         */
        public X500Name(ASN1Sequence seq) {
            @SuppressWarnings("unchecked")
            Enumeration e = seq.getObjects();

            while (e.hasMoreElements()) {
                ASN1Set set = (ASN1Set)e.nextElement();

                for (int i = 0; i < set.size(); i++) {
                    ASN1Sequence s = (ASN1Sequence)set.getObjectAt(i);
                    String id = DefaultSymbols.get((ASN1ObjectIdentifier)s.getObjectAt(0));
                    if (id == null)
                        continue;
                    List<String> vs = values.get(id);
                    if (vs == null) {
                        vs = new ArrayList<>();
                        values.put(id, vs);
                    }
                    vs.add(((ASN1String)s.getObjectAt(1)).getString());
                }
            }
        }

        /**
         * Constructs an X509 name.
         *
         * @param dirName a directory name
         */
        public X500Name(String dirName) {
            CertificateInfo.X509NameTokenizer   nTok = new CertificateInfo.X509NameTokenizer(dirName);

            while (nTok.hasMoreTokens()) {
                String  token = nTok.nextToken();
                int index = token.indexOf('=');

                if (index == -1) {
                    throw new IllegalArgumentException(/*MessageLocalization.getComposedMessage("badly.formated.directory.string")*/);
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
         * @return the (first) field value
         */
        public String getField(String name) {
            List<String> vs = values.get(name);
            return vs == null ? null : (String)vs.get(0);
        }

        /**
         * Gets a field array from the values Map.
         *
         * @param name      The field name
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
        private String          oid;
        private int             index;
        private StringBuffer    buf = new StringBuffer();

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

            int     end = index + 1;
            boolean quoted = false;
            boolean escaped = false;

            buf.setLength(0);

            while (end != oid.length()) {
                char    c = oid.charAt(end);

                if (c == '"') {
                    if (!escaped) {
                        quoted = !quoted;
                    }
                    else {
                        buf.append(c);
                    }
                    escaped = false;
                }
                else {
                    if (escaped || quoted) {
                        buf.append(c);
                        escaped = false;
                    }
                    else if (c == '\\') {
                        escaped = true;
                    }
                    else if (c == ',') {
                        break;
                    }
                    else {
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
     * @return an X500Name
     */
    public static X500Name getIssuerFields(X509Certificate cert) {
        try {
            return new X500Name((ASN1Sequence)CertificateInfo.getIssuer(cert.getTBSCertificate()));
        }
        catch (Exception e) {
            throw new PdfException(e);
        }
    }

    /**
     * Get the "issuer" from the TBSCertificate bytes that are passed in.
     *
     * @param enc a TBSCertificate in a byte array
     * @return an ASN1Primitive
     */
    public static ASN1Primitive getIssuer(byte[] enc) {
        try {
            ASN1InputStream in = new ASN1InputStream(new ByteArrayInputStream(enc));
            ASN1Sequence seq = (ASN1Sequence)in.readObject();
            return (ASN1Primitive)seq.getObjectAt(seq.getObjectAt(0) instanceof ASN1TaggedObject ? 3 : 2);
        }
        catch (IOException e) {
            throw new PdfException(e);
        }
    }

    // Certificate Subject

    /**
     * Get the subject fields from an X509 Certificate.
     *
     * @param cert an X509Certificate
     * @return an X500Name
     */
    public static X500Name getSubjectFields(X509Certificate cert) {
        try {
            if (cert != null)
                return new X500Name((ASN1Sequence)CertificateInfo.getSubject(cert.getTBSCertificate()));
        }
        catch (Exception e) {
            throw new PdfException(e);
        }
        return null;
    }

    /**
     * Get the "subject" from the TBSCertificate bytes that are passed in.
     *
     * @param enc A TBSCertificate in a byte array
     * @return a ASN1Primitive
     */
    public static ASN1Primitive getSubject(byte[] enc) {
        try {
            ASN1InputStream in = new ASN1InputStream(new ByteArrayInputStream(enc));
            ASN1Sequence seq = (ASN1Sequence)in.readObject();
            return (ASN1Primitive)seq.getObjectAt(seq.getObjectAt(0) instanceof ASN1TaggedObject ? 5 : 4);
        }
        catch (IOException e) {
            throw new PdfException(e);
        }
    }

}
