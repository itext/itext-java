package com.itextpdf.signatures;

import com.itextpdf.basics.PdfException;
import com.itextpdf.basics.font.PdfEncodings;
import com.itextpdf.basics.io.RASInputStream;
import com.itextpdf.basics.io.RandomAccessFileOrArray;
import com.itextpdf.basics.io.RandomAccessSourceFactory;
import com.itextpdf.basics.io.WindowRandomAccessSource;
import com.itextpdf.core.pdf.*;
import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.fields.PdfFormField;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

// TODO: REFACTOR. At this moment this serves as
// storage for some signature-related methods from iText 5 AcroFields
public class SignatureUtil {

    private PdfDocument document;
    private PdfAcroForm acroForm;
    private HashMap<String, int[]> sigNames;
    private ArrayList<String> orderedSignatureNames;
    private int totalRevisions;

    public SignatureUtil(PdfDocument document) {
        this.document = document;
        this.acroForm = PdfAcroForm.getAcroForm(document, true);
    }

    /**
     * Verifies a signature. An example usage is:
     * <p>
     * <pre>
     * KeyStore kall = PdfPKCS7.loadCacertsKeyStore();
     * PdfReader reader = new PdfReader("my_signed_doc.pdf");
     * AcroFields af = reader.getAcroFields();
     * ArrayList names = af.getSignatureNames();
     * for (int k = 0; k &lt; names.size(); ++k) {
     *    String name = (String)names.get(k);
     *    System.out.println("Signature name: " + name);
     *    System.out.println("Signature covers whole document: " + af.signatureCoversWholeDocument(name));
     *    PdfPKCS7 pk = af.verifySignature(name);
     *    Calendar cal = pk.getSignDate();
     *    Certificate pkc[] = pk.getCertificates();
     *    System.out.println("Subject: " + PdfPKCS7.getSubjectFields(pk.getSigningCertificate()));
     *    System.out.println("Document modified: " + !pk.verify());
     *    Object fails[] = PdfPKCS7.verifyCertificates(pkc, kall, null, cal);
     *    if (fails == null)
     *        System.out.println("Certificates verified against the KeyStore");
     *    else
     *        System.out.println("Certificate failed: " + fails[1]);
     * }
     * </pre>
     *
     * @param name the signature field name
     * @return a <CODE>PdfPKCS7</CODE> class to continue the verification
     */
    public PdfPKCS7 verifySignature(String name) {
        return verifySignature(name, null);
    }

    /**
     * Verifies a signature. An example usage is:
     * <p>
     * <pre>
     * KeyStore kall = PdfPKCS7.loadCacertsKeyStore();
     * PdfReader reader = new PdfReader("my_signed_doc.pdf");
     * AcroFields af = reader.getAcroFields();
     * ArrayList names = af.getSignatureNames();
     * for (int k = 0; k &lt; names.size(); ++k) {
     *    String name = (String)names.get(k);
     *    System.out.println("Signature name: " + name);
     *    System.out.println("Signature covers whole document: " + af.signatureCoversWholeDocument(name));
     *    PdfPKCS7 pk = af.verifySignature(name);
     *    Calendar cal = pk.getSignDate();
     *    Certificate pkc[] = pk.getCertificates();
     *    System.out.println("Subject: " + PdfPKCS7.getSubjectFields(pk.getSigningCertificate()));
     *    System.out.println("Document modified: " + !pk.verify());
     *    Object fails[] = PdfPKCS7.verifyCertificates(pkc, kall, null, cal);
     *    if (fails == null)
     *        System.out.println("Certificates verified against the KeyStore");
     *    else
     *        System.out.println("Certificate failed: " + fails[1]);
     * }
     * </pre>
     *
     * @param name the signature field name
     * @param provider the provider or <code>null</code> for the default provider
     * @return a <CODE>PdfPKCS7</CODE> class to continue the verification
     */
    public PdfPKCS7 verifySignature(String name, String provider) {
        PdfDictionary v = getSignatureDictionary(name);
        if (v == null)
            return null;
        try {
            PdfName sub = v.getAsName(PdfName.SubFilter);
            PdfString contents = v.getAsString(PdfName.Contents);
            PdfPKCS7 pk = null;
            if (sub.equals(PdfName.Adbe_x509_rsa_sha1)) {
                PdfString cert = v.getAsString(PdfName.Cert);
                if (cert == null)
                    cert = v.getAsArray(PdfName.Cert).getAsString(0);
                pk = new PdfPKCS7(PdfEncodings.convertToBytes(contents.getValue(), null), cert.getValueBytes(), provider);
            }
            else
                pk = new PdfPKCS7(PdfEncodings.convertToBytes(contents.getValue(), null), sub, provider);
            updateByteRange(pk, v);
            PdfString str = v.getAsString(PdfName.M);
            if (str != null)
                pk.setSignDate(PdfDate.decode(str.toString()));
            PdfObject obj = v.get(PdfName.Name);
            if (obj != null) {
                if (obj.isString())
                    pk.setSignName(((PdfString)obj).toUnicodeString());
                else if(obj.isName())
                    pk.setSignName(((PdfName) obj).getValue());
            }
            str = v.getAsString(PdfName.Reason);
            if (str != null)
                pk.setReason(str.toUnicodeString());
            str = v.getAsString(PdfName.Location);
            if (str != null)
                pk.setLocation(str.toUnicodeString());
            return pk;
        }
        catch (Exception e) {
            throw new PdfException(e);
        }
    }

    /**
     * Gets the signature dictionary, the one keyed by /V.
     *
     * @param name the field name
     * @return the signature dictionary keyed by /V or <CODE>null</CODE> if the field is not
     * a signature
     */
    public PdfDictionary getSignatureDictionary(String name) {
        getSignatureNames();
/*        name = getTranslatedFieldName(name);*/
        if (!sigNames.containsKey(name))
            return null;
        PdfFormField field = acroForm.getField(name);
        PdfDictionary merged = field.getPdfObject();
        return merged.getAsDictionary(PdfName.V);
    }


    private void updateByteRange(PdfPKCS7 pkcs7, PdfDictionary v) {
        PdfArray b = v.getAsArray(PdfName.ByteRange);
        RandomAccessFileOrArray rf = document.getReader().getSafeFile();
        InputStream rg = null;
        try {
            rg = new RASInputStream(new RandomAccessSourceFactory().createRanged(rf.createSourceView(), asLongArray(b)));
            byte buf[] = new byte[8192];
            int rd;
            while ((rd = rg.read(buf, 0, buf.length)) > 0) {
                pkcs7.update(buf, 0, rd);
            }
        }
        catch (Exception e) {
            throw new PdfException(e);
        } finally {
            try {
                if (rg != null) rg.close();
            } catch (IOException e) {
                // this really shouldn't ever happen - the source view we use is based on a Safe view, which is a no-op anyway
                throw new PdfException(e);
            }
        }
    }

    /**
     * Gets the field names that have signatures and are signed.
     *
     * @return the field names that have signatures and are signed
     */
    public ArrayList<String> getSignatureNames() {
        if (sigNames != null)
            return new ArrayList<String>(orderedSignatureNames);
        sigNames = new HashMap<String, int[]>();
        orderedSignatureNames = new ArrayList<String>();
        ArrayList<Object[]> sorter = new ArrayList<Object[]>();
        for (Map.Entry<String, PdfFormField> entry : acroForm.getFormFields().entrySet()) {
            PdfFormField field = entry.getValue();
            PdfDictionary merged = field.getPdfObject();
            if (!PdfName.Sig.equals(merged.get(PdfName.FT)))
                continue;
            PdfDictionary v = merged.getAsDictionary(PdfName.V);
            if (v == null)
                continue;
            PdfString contents = v.getAsString(PdfName.Contents);
            if (contents == null)
                continue;
            PdfArray ro = v.getAsArray(PdfName.ByteRange);
            if (ro == null)
                continue;
            int rangeSize = ro.size();
            if (rangeSize < 2)
                continue;
            int length = ro.getAsNumber(rangeSize - 1).getIntValue() + ro.getAsNumber(rangeSize - 2).getIntValue();
            sorter.add(new Object[]{entry.getKey(), new int[]{length, 0}});
        }
        Collections.sort(sorter, new SorterComparator());
        if (!sorter.isEmpty()) {
            try {
                if (((int[])sorter.get(sorter.size() - 1)[1])[0] == document.getReader().getFileLength())
                    totalRevisions = sorter.size();
                else
                    totalRevisions = sorter.size() + 1;
            } catch (IOException e) {
                // TODO: add exception handling (at least some logger)
            }
            for (int k = 0; k < sorter.size(); ++k) {
                Object objs[] = sorter.get(k);
                String name = (String)objs[0];
                int p[] = (int[])objs[1];
                p[1] = k + 1;
                sigNames.put(name, p);
                orderedSignatureNames.add(name);
            }
        }
        return new ArrayList<String>(orderedSignatureNames);
    }

    /**
     * Gets the field names that have blank signatures.
     *
     * @return the field names that have blank signatures
     */
    public ArrayList<String> getBlankSignatureNames() {
        getSignatureNames();
        ArrayList<String> sigs = new ArrayList<String>();
        for (Map.Entry<String, PdfFormField> entry : acroForm.getFormFields().entrySet()) {
            PdfFormField field = entry.getValue();
            PdfDictionary merged = field.getPdfObject();
            if (!PdfName.Sig.equals(merged.getAsName(PdfName.FT)))
                continue;
            if (sigNames.containsKey(entry.getKey()))
                continue;
            sigs.add(entry.getKey());
        }
        return sigs;
    }

    /**
     * Extracts a revision from the document.
     *
     * @param field the signature field name
     * @return an <CODE>InputStream</CODE> covering the revision. Returns <CODE>null</CODE> if
     * it's not a signature field
     * @throws IOException on error
     */
    public InputStream extractRevision(String field) throws IOException {
        getSignatureNames();
/*        field = getTranslatedFieldName(field);*/
        if (!sigNames.containsKey(field))
            return null;
        int length = sigNames.get(field)[0];
        RandomAccessFileOrArray raf = document.getReader().getSafeFile();
        return new RASInputStream(new WindowRandomAccessSource(raf.createSourceView(), 0, length));
    }

    /**
     * Checks is the signature covers the entire document or just part of it.
     *
     * @param name the signature field name
     * @return <CODE>true</CODE> if the signature covers the entire document,
     * <CODE>false</CODE> otherwise
     */
    public boolean signatureCoversWholeDocument(String name) {
        getSignatureNames();
  /*      name = getTranslatedFieldName(name);*/
        if (!sigNames.containsKey(name))
            return false;
        try {
            return sigNames.get(name)[0] == document.getReader().getFileLength();
        } catch (IOException e) {
            throw new PdfException(e);
        }
    }

    /**
     * Checks whether a name exists as a signature field or not. It checks both signed fields and blank signatures.
     * @param name String
     * @return boolean does the signature field exist
     * @since 5.5.1
     */
    public boolean doesSignatureFieldExist(String name) {
        return getBlankSignatureNames().contains(name) || getSignatureNames().contains(name);
    }

    // TODO: copied from iText 5 PdfArray.asLongArray
    public static long[] asLongArray(PdfArray arr) {
        long[] rslt = new long[arr.size()];

        for (int k = 0; k < rslt.length; ++k) {
            rslt[k] = arr.getAsNumber(k).getLongValue();
        }

        return rslt;
    }

    private static class SorterComparator implements Comparator<Object[]> {
        public int compare(Object[] o1, Object[] o2) {
            int n1 = ((int[])o1[1])[0];
            int n2 = ((int[])o2[1])[0];
            return n1 - n2;
        }
    }
}
