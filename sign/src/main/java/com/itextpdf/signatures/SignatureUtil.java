/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2018 iText Group NV
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

import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.source.RASInputStream;
import com.itextpdf.io.source.RandomAccessFileOrArray;
import com.itextpdf.io.source.RandomAccessSourceFactory;
import com.itextpdf.io.source.WindowRandomAccessSource;
import com.itextpdf.kernel.PdfException;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDate;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfString;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Utility class that provides several convenience methods concerning digital signatures.
 */
// TODO: REFACTOR. At this moment this serves as storage for some signature-related methods from iText 5 AcroFields
public class SignatureUtil {

    private PdfDocument document;
    private PdfAcroForm acroForm;
    private Map<String, int[]> sigNames;
    private List<String> orderedSignatureNames;
    private int totalRevisions;

    /**
     * Creates a SignatureUtil instance. Sets the acroForm field to the acroForm in the PdfDocument.
     * iText will create a new AcroForm if the PdfDocument doesn't contain one.
     *
     * @param document PdfDocument to be inspected
     */
    public SignatureUtil(PdfDocument document) {
        this.document = document;
        this.acroForm = PdfAcroForm.getAcroForm(document, true);
    }

    /**
     * Verifies a signature. Further verification can be done on the returned
     * {@link PdfPKCS7} object.
     *
     * @param name String the signature field name
     * @return PdfPKCS7 object to continue the verification
     */
    public PdfPKCS7 verifySignature(String name) {
        return verifySignature(name, null);
    }

    /**
     * Verifies a signature. Further verification can be done on the returned
     * {@link PdfPKCS7} object.
     *
     * @param name the signature field name
     * @param provider the provider or null for the default provider
     * @return PdfPKCS7 object to continue the verification
     */
    public PdfPKCS7 verifySignature(String name, String provider) {
        PdfSignature signature = getSignature(name);
        if (signature == null)
            return null;
        try {
            PdfName sub = signature.getSubFilter();
            PdfString contents = signature.getContents();
            PdfPKCS7 pk = null;
            if (sub.equals(PdfName.Adbe_x509_rsa_sha1)) {
                PdfString cert = signature.getPdfObject().getAsString(PdfName.Cert);
                if (cert == null)
                    cert = signature.getPdfObject().getAsArray(PdfName.Cert).getAsString(0);
                pk = new PdfPKCS7(PdfEncodings.convertToBytes(contents.getValue(), null), cert.getValueBytes(), provider);
            }
            else
                pk = new PdfPKCS7(PdfEncodings.convertToBytes(contents.getValue(), null), sub, provider);
            updateByteRange(pk, signature);
            PdfString date = signature.getDate();
            if (date != null)
                pk.setSignDate(PdfDate.decode(date.toString()));
            String signName = signature.getName();
            pk.setSignName(signName);
            String reason = signature.getReason();
            if (reason != null)
                pk.setReason(reason);
            String location = signature.getLocation();
            if (location != null)
                pk.setLocation(location);
            return pk;
        }
        catch (Exception e) {
            throw new PdfException(e);
        }
    }

    public PdfSignature getSignature(String name) {
        PdfDictionary sigDict = getSignatureDictionary(name);
        return sigDict != null ? new PdfSignature(sigDict) : null;
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
        if (!sigNames.containsKey(name))
            return null;
        PdfFormField field = acroForm.getField(name);
        PdfDictionary merged = field.getPdfObject();
        return merged.getAsDictionary(PdfName.V);
    }

    /* Updates the /ByteRange with the provided value */
    private void updateByteRange(PdfPKCS7 pkcs7, PdfSignature signature) {
        PdfArray b = signature.getByteRange();
        RandomAccessFileOrArray rf = document.getReader().getSafeFile();
        InputStream rg = null;
        try {
            rg = new RASInputStream(new RandomAccessSourceFactory().createRanged(rf.createSourceView(), asLongArray(b)));
            byte[] buf = new byte[8192];
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
     * @return List containing the field names that have signatures and are signed
     */
    public List<String> getSignatureNames() {
        if (sigNames != null)
            return new ArrayList<>(orderedSignatureNames);
        sigNames = new HashMap<>();
        orderedSignatureNames = new ArrayList<>();
        List<Object[]> sorter = new ArrayList<>();
        for (Map.Entry<String, PdfFormField> entry : acroForm.getFormFields().entrySet()) {
            PdfFormField field = entry.getValue();
            PdfDictionary merged = field.getPdfObject();
            if (!PdfName.Sig.equals(merged.get(PdfName.FT)))
                continue;
            PdfDictionary v = merged.getAsDictionary(PdfName.V);
            if (v == null)
                continue;
            PdfString contents = v.getAsString(PdfName.Contents);
            if (contents == null) {
                continue;
            } else {
              contents.markAsUnencryptedObject();
            }
            PdfArray ro = v.getAsArray(PdfName.ByteRange);
            if (ro == null)
                continue;
            int rangeSize = ro.size();
            if (rangeSize < 2)
                continue;
            int length = ro.getAsNumber(rangeSize - 1).intValue() + ro.getAsNumber(rangeSize - 2).intValue();
            sorter.add(new Object[]{entry.getKey(), new int[]{length, 0}});
        }
        Collections.sort(sorter, new SorterComparator());
        if (sorter.size() > 0) {
            try {
                if (((int[])sorter.get(sorter.size() - 1)[1])[0] == document.getReader().getFileLength())
                    totalRevisions = sorter.size();
                else
                    totalRevisions = sorter.size() + 1;
            } catch (IOException e) {
                // TODO: add exception handling (at least some logger)
            }
            for (int k = 0; k < sorter.size(); ++k) {
                Object[] objs = sorter.get(k);
                String name = (String)objs[0];
                int[] p = (int[])objs[1];
                p[1] = k + 1;
                sigNames.put(name, p);
                orderedSignatureNames.add(name);
            }
        }
        return new ArrayList<>(orderedSignatureNames);
    }

    /**
     * Gets the field names that have blank signatures.
     *
     * @return List containing the field names that have blank signatures
     */
    public List<String> getBlankSignatureNames() {
        getSignatureNames();
        List<String> sigs = new ArrayList<>();
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

    public int getTotalRevisions() {
        getSignatureNames();
        return totalRevisions;
    }


    public int getRevision(String field) {
        getSignatureNames();
        field = getTranslatedFieldName(field);
        if (!sigNames.containsKey(field))
            return 0;
         return sigNames.get(field)[1];
    }

    public String getTranslatedFieldName(String name) {
        if (acroForm.getXfaForm().isXfaPresent()) {
            String namex = acroForm.getXfaForm().findFieldName(name);
            if (namex != null)
                name = namex;
        }
        return name;
    }

    /**
     * Extracts a revision from the document.
     *
     * @param field the signature field name
     * @return an InputStream covering the revision. Returns null if it's not a signature field
     * @throws IOException
     */
    public InputStream extractRevision(String field) throws IOException {
        getSignatureNames();
        if (!sigNames.containsKey(field))
            return null;
        int length = sigNames.get(field)[0];
        RandomAccessFileOrArray raf = document.getReader().getSafeFile();
        return new RASInputStream(new WindowRandomAccessSource(raf.createSourceView(), 0, length));
    }

    /**
     * Checks if the signature covers the entire document or just part of it.
     *
     * @param name the signature field name
     * @return true if the signature covers the entire document, false if it doesn't
     */
    public boolean signatureCoversWholeDocument(String name) {
        getSignatureNames();
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
     *
     * @param name name of the field
     * @return boolean does the signature field exist
     */
    public boolean doesSignatureFieldExist(String name) {
        return getBlankSignatureNames().contains(name) || getSignatureNames().contains(name);
    }


    /**
     * Converts a {@link com.itextpdf.kernel.pdf.PdfArray} to an array of longs
     *
     * @param pdfArray PdfArray to be converted
     * @return long[] containing the PdfArray values
     */
    // TODO: copied from iText 5 PdfArray.asLongArray
    public static long[] asLongArray(PdfArray pdfArray) {
        long[] rslt = new long[pdfArray.size()];

        for (int k = 0; k < rslt.length; ++k) {
            rslt[k] = pdfArray.getAsNumber(k).longValue();
        }

        return rslt;
    }

    private static class SorterComparator implements Comparator<Object[]> {
        @Override
        public int compare(Object[] o1, Object[] o2) {
            int n1 = ((int[])o1[1])[0];
            int n2 = ((int[])o2[1])[0];
            return n1 - n2;
        }
    }
}
