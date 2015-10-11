package com.itextpdf.signatures;

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
     * a class that holds an X509 name
     */
    public static class X500Name {
        /** country code - StringType(SIZE(2)) */
        public static final ASN1ObjectIdentifier C = new ASN1ObjectIdentifier("2.5.4.6");

        /** organization - StringType(SIZE(1..64)) */
        public static final ASN1ObjectIdentifier O = new ASN1ObjectIdentifier("2.5.4.10");

        /** organizational unit name - StringType(SIZE(1..64)) */
        public static final ASN1ObjectIdentifier OU = new ASN1ObjectIdentifier("2.5.4.11");

        /** Title */
        public static final ASN1ObjectIdentifier T = new ASN1ObjectIdentifier("2.5.4.12");

        /** common name - StringType(SIZE(1..64)) */
        public static final ASN1ObjectIdentifier CN = new ASN1ObjectIdentifier("2.5.4.3");

        /** device serial number name - StringType(SIZE(1..64)) */
        public static final ASN1ObjectIdentifier SN = new ASN1ObjectIdentifier("2.5.4.5");

        /** locality name - StringType(SIZE(1..64)) */
        public static final ASN1ObjectIdentifier L = new ASN1ObjectIdentifier("2.5.4.7");

        /** state, or province name - StringType(SIZE(1..64)) */
        public static final ASN1ObjectIdentifier ST = new ASN1ObjectIdentifier("2.5.4.8");

        /** Naming attribute of type X520name */
        public static final ASN1ObjectIdentifier SURNAME = new ASN1ObjectIdentifier("2.5.4.4");

        /** Naming attribute of type X520name */
        public static final ASN1ObjectIdentifier GIVENNAME = new ASN1ObjectIdentifier("2.5.4.42");

        /** Naming attribute of type X520name */
        public static final ASN1ObjectIdentifier INITIALS = new ASN1ObjectIdentifier("2.5.4.43");

        /** Naming attribute of type X520name */
        public static final ASN1ObjectIdentifier GENERATION = new ASN1ObjectIdentifier("2.5.4.44");

        /** Naming attribute of type X520name */
        public static final ASN1ObjectIdentifier UNIQUE_IDENTIFIER = new ASN1ObjectIdentifier("2.5.4.45");

        /**
         * Email address (RSA PKCS#9 extension) - IA5String.
         * <p>Note: if you're trying to be ultra orthodox, don't use this! It shouldn't be in here.</p>
         */
        public static final ASN1ObjectIdentifier EmailAddress = new ASN1ObjectIdentifier("1.2.840.113549.1.9.1");

        /**
         * email address in Verisign certificates
         */
        public static final ASN1ObjectIdentifier E = EmailAddress;

        /** object identifier */
        public static final ASN1ObjectIdentifier DC = new ASN1ObjectIdentifier("0.9.2342.19200300.100.1.25");

        /** LDAP User id. */
        public static final ASN1ObjectIdentifier UID = new ASN1ObjectIdentifier("0.9.2342.19200300.100.1.1");

        /** A Map with default symbols */
        public static final Map<ASN1ObjectIdentifier, String> DefaultSymbols = new HashMap<ASN1ObjectIdentifier, String>();

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

        /** A HashMap with values */
        public Map<String, ArrayList<String>> values = new HashMap<String, ArrayList<String>>();

        /**
         * Constructs an X509 name
         * @param seq an ASN1 Sequence
         */
        public X500Name(ASN1Sequence seq) {
            @SuppressWarnings("unchecked")
            Enumeration<ASN1Set> e = seq.getObjects();

            while (e.hasMoreElements()) {
                ASN1Set set = e.nextElement();

                for (int i = 0; i < set.size(); i++) {
                    ASN1Sequence s = (ASN1Sequence)set.getObjectAt(i);
                    String id = DefaultSymbols.get(s.getObjectAt(0));
                    if (id == null)
                        continue;
                    ArrayList<String> vs = values.get(id);
                    if (vs == null) {
                        vs = new ArrayList<String>();
                        values.put(id, vs);
                    }
                    vs.add(((ASN1String)s.getObjectAt(1)).getString());
                }
            }
        }

        /**
         * Constructs an X509 name
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
                ArrayList<String> vs = values.get(id);
                if (vs == null) {
                    vs = new ArrayList<String>();
                    values.put(id, vs);
                }
                vs.add(value);
            }

        }

        /**
         * Gets the first entry from the field array retrieved from the values Map.
         * @param	name	the field name
         * @return	the (first) field value
         */
        public String getField(String name) {
            List<String> vs = values.get(name);
            return vs == null ? null : (String)vs.get(0);
        }

        /**
         * Gets a field array from the values Map
         * @param name
         * @return an ArrayList
         */
        public List<String> getFieldArray(String name) {
            return values.get(name);
        }

        /**
         * Getter for values
         * @return a Map with the fields of the X509 name
         */
        public Map<String, ArrayList<String>> getFields() {
            return values;
        }

        /**
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            return values.toString();
        }
    }

    /**
     * class for breaking up an X500 Name into it's component tokens,
     * similar to java.util.StringTokenizer. We need this class as some
     * of the lightweight Java environments don't support classes such
     * as StringTokenizer.
     */
    public static class X509NameTokenizer {
        private String          oid;
        private int             index;
        private StringBuffer    buf = new StringBuffer();

        public X509NameTokenizer(String oid) {
            this.oid = oid;
            this.index = -1;
        }

        public boolean hasMoreTokens() {
            return index != oid.length();
        }

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
     * Get the issuer fields from an X509 Certificate
     * @param cert an X509Certificate
     * @return an X500Name
     */
    public static X500Name getIssuerFields(X509Certificate cert) {
        try {
            return new X500Name((ASN1Sequence)CertificateInfo.getIssuer(cert.getTBSCertificate()));
        }
        catch (Exception e) {
            throw new /*ExceptionConverter*/RuntimeException(e);
        }
    }

    /**
     * Get the "issuer" from the TBSCertificate bytes that are passed in
     * @param enc a TBSCertificate in a byte array
     * @return a ASN1Primitive
     */
    public static ASN1Primitive getIssuer(byte[] enc) {
        try {
            ASN1InputStream in = new ASN1InputStream(new ByteArrayInputStream(enc));
            ASN1Sequence seq = (ASN1Sequence)in.readObject();
            return (ASN1Primitive)seq.getObjectAt(seq.getObjectAt(0) instanceof ASN1TaggedObject ? 3 : 2);
        }
        catch (IOException e) {
            throw new /*ExceptionConverter*/RuntimeException(e);
        }
    }

    // Certificate Subject

    /**
     * Get the subject fields from an X509 Certificate
     * @param cert an X509Certificate
     * @return an X500Name
     */
    public static X500Name getSubjectFields(X509Certificate cert) {
        try {
            if (cert != null)
                return new X500Name((ASN1Sequence)CertificateInfo.getSubject(cert.getTBSCertificate()));
        }
        catch (Exception e) {
            throw new /*ExceptionConverter*/RuntimeException(e);
        }
        return null;
    }

    /**
     * Get the "subject" from the TBSCertificate bytes that are passed in
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
            throw new /*ExceptionConverter*/RuntimeException(e);
        }
    }

}