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

import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.fields.PdfFormCreator;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.source.IRandomAccessSource;
import com.itextpdf.io.source.PdfTokenizer;
import com.itextpdf.io.source.RASInputStream;
import com.itextpdf.io.source.RandomAccessFileOrArray;
import com.itextpdf.io.source.RandomAccessSourceFactory;
import com.itextpdf.io.source.WindowRandomAccessSource;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDate;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfIndirectReference;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.signatures.exceptions.SignExceptionMessageConstant;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility class that provides several convenience methods concerning digital signatures.
 */
public class SignatureUtil {

    private final PdfDocument document;
    private final PdfAcroForm acroForm;
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
        // Only create new AcroForm if there is a writer
        this.acroForm = PdfFormCreator.getAcroForm(document, document.getWriter() != null);
    }

    /**
     * Prepares an {@link PdfPKCS7} instance for the given signature.
     * This method handles signature parsing and might throw an exception if
     * signature is malformed.
     * <p>
     * The returned {@link PdfPKCS7} can be used to fetch additional info about the signature
     * and also to perform integrity check of data signed by the given signature field.
     * <p>
     * In order to validate the signature it is required to check if it covers the entire file,
     * otherwise one cannot be sure that signature in question indeed signs the data
     * that constitutes current {@link PdfDocument} with all its contents.
     * In order to check that given signature covers the current {@link PdfDocument} please
     * use {@link #signatureCoversWholeDocument(String)} method.
     *
     * @param signatureFieldName the signature field name
     * @return a {@link PdfPKCS7} instance which can be used to fetch additional info about the signature
     * and also to perform integrity check of data signed by the given signature field.
     */
    public PdfPKCS7 readSignatureData(String signatureFieldName) {
        return readSignatureData(signatureFieldName, null);
    }

    /**
     * Prepares an {@link PdfPKCS7} instance for the given signature.
     * This method handles signature parsing and might throw an exception if
     * signature is malformed.
     * <p>
     * The returned {@link PdfPKCS7} can be used to fetch additional info about the signature
     * and also to perform integrity check of data signed by the given signature field.
     * <p>
     * Prepared {@link PdfPKCS7} instance calculates digest based on signature's /ByteRange entry.
     * In order to check that /ByteRange is properly defined and given signature indeed covers the current PDF document
     * revision please use {@link #signatureCoversWholeDocument(String)} method.
     *
     * @param signatureFieldName the signature field name
     * @param securityProvider the security provider or null for the default provider
     * @return a {@link PdfPKCS7} instance which can be used to fetch additional info about the signature
     * and also to perform integrity check of data signed by the given signature field.
     */
    public PdfPKCS7 readSignatureData(String signatureFieldName, String securityProvider) {
        PdfSignature signature = getSignature(signatureFieldName);
        if (signature == null) {
            return null;
        }
        try {
            PdfName sub = signature.getSubFilter();
            PdfString contents = signature.getContents();
            PdfPKCS7 pk = null;
            if (sub.equals(PdfName.Adbe_x509_rsa_sha1)) {
                PdfString cert = signature.getPdfObject().getAsString(PdfName.Cert);
                if (cert == null) {
                    cert = signature.getPdfObject().getAsArray(PdfName.Cert).getAsString(0);
                }
                pk = new PdfPKCS7(PdfEncodings.convertToBytes(contents.getValue(), null), cert.getValueBytes(),
                        securityProvider);
            } else {
                pk = new PdfPKCS7(PdfEncodings.convertToBytes(contents.getValue(), null), sub, securityProvider);
            }
            updateByteRange(pk, signature);
            PdfString date = signature.getDate();
            if (date != null) {
                pk.setSignDate(PdfDate.decode(date.toString()));
            }
            String signName = signature.getName();
            pk.setSignName(signName);
            String reason = signature.getReason();
            if (reason != null) {
                pk.setReason(reason);
            }
            String location = signature.getLocation();
            if (location != null) {
                pk.setLocation(location);
            }
            return pk;
        } catch (Exception e) {
            throw new PdfException(e);
        }
    }

    /**
     * Get {@link PdfSignature} dictionary based on the provided name.
     *
     * @param name signature name
     *
     * @return {@link PdfSignature} instance corresponding to the provided name. {@code null} otherwise
     */
    public PdfSignature getSignature(String name) {
        PdfDictionary sigDict = getSignatureDictionary(name);
        return sigDict != null
                ? new PdfSignature(sigDict)
                : null;
    }

    /**
     * Gets the signature dictionary, the one keyed by /V.
     *
     * @param name the field name
     * @return the signature dictionary keyed by /V or <CODE>null</CODE> if the field is not
     * a signature
     */
    public PdfDictionary getSignatureDictionary(String name) {
        PdfDictionary merged = getSignatureFormFieldDictionary(name);
        return merged == null ? null : merged.getAsDictionary(PdfName.V);
    }

    public PdfDictionary getSignatureFormFieldDictionary(String name) {
        getSignatureNames();
        if (acroForm == null || !sigNames.containsKey(name)) {
            return null;
        }
        PdfFormField field = acroForm.getField(name);
        return field.getPdfObject();
    }

    /* Updates the /ByteRange with the provided value */
    private void updateByteRange(PdfPKCS7 pkcs7, PdfSignature signature) {
        PdfArray b = signature.getByteRange();
        RandomAccessFileOrArray rf = document.getReader().getSafeFile();
        InputStream rg = null;
        try {
            rg = new RASInputStream(
                    new RandomAccessSourceFactory().createRanged(rf.createSourceView(), b.toLongArray()));
            byte[] buf = new byte[8192];
            int rd;
            while ((rd = rg.read(buf, 0, buf.length)) > 0) {
                pkcs7.update(buf, 0, rd);
            }
        } catch (Exception e) {
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
        if (sigNames != null) {
            return new ArrayList<>(orderedSignatureNames);
        }
        sigNames = new HashMap<>();
        orderedSignatureNames = new ArrayList<>();
        populateSignatureNames();

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
        if (acroForm != null) {
            for (Map.Entry<String, PdfFormField> entry : acroForm.getAllFormFields().entrySet()) {
                PdfFormField field = entry.getValue();
                PdfDictionary merged = field.getPdfObject();
                if (!PdfName.Sig.equals(merged.getAsName(PdfName.FT))) {
                    continue;
                }
                if (sigNames.containsKey(entry.getKey())) {
                    continue;
                }
                sigs.add(entry.getKey());
            }
        }
        return sigs;
    }

    /**
     * Get the amount of signed document revisions.
     *
     * @return {@code int} amount of signed document revisions
     */
    public int getTotalRevisions() {
        getSignatureNames();
        return totalRevisions;
    }

    /**
     * Get signed document revision number, which corresponds to the provided signature name.
     *
     * @param field signature name
     *
     * @return {@code int} revision number
     */
    public int getRevision(String field) {
        getSignatureNames();
        field = getTranslatedFieldName(field);
        if (!sigNames.containsKey(field)) {
            return 0;
        }
        return sigNames.get(field)[1];
    }

    /**
     * Get field name, translated using XFA, if any present in the document.
     *
     * @param name field name to be translated
     *
     * @return translated field name if XFA is present, original name otherwise
     */
    public String getTranslatedFieldName(String name) {
        if (acroForm != null && acroForm.getXfaForm().isXfaPresent()) {
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
     */
    public InputStream extractRevision(String field) {
        getSignatureNames();
        if (!sigNames.containsKey(field)) {
            return null;
        }
        int length = sigNames.get(field)[0];
        RandomAccessFileOrArray raf = document.getReader().getSafeFile();
        return new RASInputStream(new WindowRandomAccessSource(raf.createSourceView(), 0, length));
    }

    /**
     * Checks if the signature covers the entire document (except for signature's Contents) or just a part of it.
     * <p>
     * If this method does not return {@code true} it means that signature in question does not cover the entire
     * contents of current {@link PdfDocument}. Such signatures cannot be considered as verifying the PDF document,
     * because content that is not covered by signature might have been modified since the signature creation.
     * <p>
     * @param name the signature field name
     * @return true if the signature covers the entire document, false if it doesn't
     */
    public boolean signatureCoversWholeDocument(String name) {
        getSignatureNames();
        if (!sigNames.containsKey(name)) {
            return false;
        }
        try {
            ContentsChecker signatureReader = new ContentsChecker(
                    document.getReader().getSafeFile().createSourceView(), document);
            return signatureReader.checkWhetherSignatureCoversWholeDocument(acroForm.getField(name));
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

    private void populateSignatureNames() {
        if (acroForm == null) {
            return;
        }

        List<Object[]> sorter = new ArrayList<>();
        for (Map.Entry<String, PdfFormField> entry : acroForm.getAllFormFields().entrySet()) {
            PdfFormField field = entry.getValue();
            PdfDictionary merged = field.getPdfObject();
            if (!PdfName.Sig.equals(merged.get(PdfName.FT))) {
                continue;
            }
            PdfDictionary v = merged.getAsDictionary(PdfName.V);
            if (v == null) {
                continue;
            }
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
        if (!sorter.isEmpty()) {
            if (((int[]) sorter.get(sorter.size() - 1)[1])[0] == document.getReader().getFileLength()) {
                totalRevisions = sorter.size();
            } else {
                totalRevisions = sorter.size() + 1;
            }
            for (int k = 0; k < sorter.size(); ++k) {
                Object[] objs = sorter.get(k);
                String name = (String) objs[0];
                int[] p = (int[]) objs[1];
                p[1] = k + 1;
                sigNames.put(name, p);
                orderedSignatureNames.add(name);
            }
        }
    }

    private static class SorterComparator implements Comparator<Object[]> {
        @Override
        public int compare(Object[] o1, Object[] o2) {
            int n1 = ((int[]) o1[1])[0];
            int n2 = ((int[]) o2[1])[0];
            return n1 - n2;
        }
    }

    private static class ContentsChecker extends PdfReader {
        public static final int OBJECT_HEADER_OFFSET = 6;
        private long rangeExclusionStart;
        private long rangeExlusionEnd;

        private int currentLevel = 0;
        private int contentsLevel = 1;
        private boolean searchInV = true;

        private boolean rangeIsCorrect = false;

        public ContentsChecker(IRandomAccessSource byteSource, PdfDocument doc) throws IOException {
            super(byteSource, null);
            pdfDocument = doc;
        }

        public boolean checkWhetherSignatureCoversWholeDocument(PdfFormField signatureField) {
            rangeIsCorrect = false;
            PdfDictionary signature = (PdfDictionary) signatureField.getValue();
            int[] byteRange = ((PdfArray) signature.get(PdfName.ByteRange)).toIntArray();
            if (4 != byteRange.length || 0 != byteRange[0]) {
                return false;
            }
            if (tokens.getSafeFile().length() < byteRange[2] + byteRange[3]) {
                return false;
            } else {
                // We allow up to 4 EOL bytes to not be included into byte range.
                tokens.seek(byteRange[2] + byteRange[3]);
                try {
                    String remainingBytes = tokens.readString(5);
                    if (remainingBytes.length() > 4) {
                        return false;
                    }
                    for (byte b : remainingBytes.getBytes(StandardCharsets.UTF_8)) {
                        if (b != '\n' && b != '\r') {
                            return false;
                        }
                    }
                } catch (IOException e) {
                    return false;
                }
            }

            rangeExclusionStart = byteRange[1];
            rangeExlusionEnd = byteRange[2];

            long signatureOffset;
            if (null != signature.getIndirectReference()) {
                signatureOffset = signature.getIndirectReference().getOffset();
                searchInV = true;
            } else {
                signatureOffset = signatureField.getPdfObject().getIndirectReference().getOffset();
                searchInV = false;
                contentsLevel++;
            }

            try {
                tokens.seek(signatureOffset);
                tokens.nextValidToken();
                readObject(false, false);
            } catch (IOException e) {
                // That's not expected because if the signature is invalid, it should have already failed
                return false;
            }

            return rangeIsCorrect;
        }

        @Override
        // The method copies the logic of PdfReader's method.
        // Only Contents related checks have been introduced.
        protected PdfDictionary readDictionary(boolean objStm) throws IOException {
            currentLevel++;
            PdfDictionary dic = new PdfDictionary();
            int contentsEntryCount = 0;
            while (!rangeIsCorrect) {
                tokens.nextValidToken();
                if (tokens.getTokenType() == PdfTokenizer.TokenType.EndDic) {
                    currentLevel--;
                    break;
                }
                if (tokens.getTokenType() != PdfTokenizer.TokenType.Name) {
                    tokens.throwError(
                            SignExceptionMessageConstant.DICTIONARY_THIS_KEY_IS_NOT_A_NAME, tokens.getStringValue());
                }
                PdfName name = readPdfName(true);
                PdfObject obj;
                if (PdfName.Contents.equals(name) && searchInV && contentsLevel == currentLevel) {
                    contentsEntryCount++;
                    if (contentsEntryCount > 1) {
                        rangeIsCorrect = false;
                        break;
                    }
                    long contentsValueStart;
                    obj = readObject(true, objStm);
                    long contentsValueEnd;
                    if (obj.isIndirectReference()) {
                        PdfIndirectReference ref = (PdfIndirectReference) obj;
                        contentsValueStart = ref.getOffset() + countDigits(ref.getObjNumber()) +
                                countDigits(ref.getGenNumber()) + OBJECT_HEADER_OFFSET;
                        contentsValueEnd = contentsValueStart +
                                //*2 + 2 to account for hex encoding
                                ((PdfString) ref.getRefersTo()).getValueBytes().length * 2L + 2L;

                    } else {
                        contentsValueEnd = tokens.getPosition();
                        //*2 + 2 to account for hex encoding
                        contentsValueStart = contentsValueEnd -(((PdfString)obj).getValueBytes().length * 2L + 2L);
                    }
                    if (contentsValueEnd == rangeExlusionEnd && contentsValueStart  == rangeExclusionStart) {
                        rangeIsCorrect = true;
                    }
                } else if (PdfName.V.equals(name) && !searchInV && 1 == currentLevel) {
                    searchInV = true;
                    obj = readObject(true, objStm);
                    searchInV = false;
                } else {
                    obj = readObject(true, objStm);
                }
                if (obj == null) {
                    if (tokens.getTokenType() == PdfTokenizer.TokenType.EndDic)
                        tokens.throwError(SignExceptionMessageConstant.UNEXPECTED_GT_GT);
                    if (tokens.getTokenType() == PdfTokenizer.TokenType.EndArray)
                        tokens.throwError(SignExceptionMessageConstant.UNEXPECTED_CLOSE_BRACKET);
                }
                dic.put(name, obj);
            }
            return dic;
        }

        private static long countDigits(int number) {
            int x = number;
            if (x == 0) {
                x = 1;
            }
            int l = 0;
            while (x>0) {
                x /= 10;
                l++;
            }
            return l;

        }
    }
}
