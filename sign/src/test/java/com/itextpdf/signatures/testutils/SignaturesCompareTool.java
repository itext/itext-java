/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 iText Group NV
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
package com.itextpdf.signatures.testutils;

import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.io.util.UrlUtil;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.ReaderProperties;
import com.itextpdf.signatures.PdfSignature;
import com.itextpdf.signatures.SignatureUtil;
import com.itextpdf.test.ITextTest;

import java.io.ByteArrayInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.bouncycastle.asn1.ASN1BitString;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1GeneralizedTime;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1UTCTime;
import org.bouncycastle.asn1.util.ASN1Dump;

public class SignaturesCompareTool {
    private static final String OID_MESSAGE_DIGEST = "1.2.840.113549.1.9.4";
    private static final String OID_SIGNED_DATA = "1.2.840.113549.1.7.2";
    private static final String OID_TST_INFO = "1.2.840.113549.1.9.16.1.4";
    private static final String OID_SIGNING_TIME = "1.2.840.113549.1.9.5";
    private static final String OID_SIGNATURE_TIMESTAMP_ATTRIBUTE = "1.2.840.113549.1.9.16.2.14";
    private static final String OID_ADBE_REVOCATION_INFO_ARCHIVAL = "1.2.840.113583.1.1.8";
    private static final String OID_OCSP_RESPONSE = "1.3.6.1.5.5.7.48.1.1";
    private static final String OID_OCSP_NONCE_EXTENSION = "1.3.6.1.5.5.7.48.1.2";

    private static final Set<String> IGNORED_OIDS;

    static {
        HashSet<String> tempSet = new HashSet<>();
        tempSet.add(OID_MESSAGE_DIGEST);
        tempSet.add(OID_TST_INFO);
        tempSet.add(OID_SIGNING_TIME);
        tempSet.add(OID_OCSP_NONCE_EXTENSION);
        IGNORED_OIDS = Collections.unmodifiableSet(tempSet);
    }

    public static String compareSignatures(String dest, String cmp) {
        return compareSignatures(dest, cmp, new ReaderProperties(), new ReaderProperties());
    }

    public static String compareSignatures(String dest, String cmp, ReaderProperties destProperties, ReaderProperties cmpProperties) {
        ITextTest.printOutCmpPdfNameAndDir(dest, cmp);

        StringBuilder errorText = new StringBuilder();

        try (PdfDocument outDocument = new PdfDocument(new PdfReader(dest, destProperties));
             PdfDocument cmpDocument = new PdfDocument(new PdfReader(cmp, cmpProperties))) {
            SignatureUtil outSigUtil = new SignatureUtil(outDocument);
            SignatureUtil cmpSigUtil = new SignatureUtil(cmpDocument);
            if (!cmpSigUtil.getSignatureNames().equals(outSigUtil.getSignatureNames())) {
                addError(errorText, "Signatures lists are different:",
                        String.valueOf(outSigUtil.getSignatureNames()),
                        String.valueOf(cmpSigUtil.getSignatureNames()));
            }

            List<String> signatures = cmpSigUtil.getSignatureNames();
            for (String sig : signatures) {
                ASN1Sequence outSignedData = (ASN1Sequence) getSignatureContent(sig, outSigUtil);
                ASN1Sequence cmpSignedData = (ASN1Sequence) getSignatureContent(sig, cmpSigUtil);

                boolean isEqual = compareSignedData(outSignedData, cmpSignedData, errorText);

                if (!isEqual) {
                    createTxtFilesFromAsn1Sequences(outSignedData, cmpSignedData, dest, sig, errorText);
                }
            }
            compareDssEntries(outDocument, cmpDocument, dest, errorText);
        } catch (Exception e) {
            errorText.append(e.getMessage());
        }

        return errorText.toString().isEmpty() ? null : errorText.toString();
    }

    private static void createTxtFilesFromAsn1Sequences(ASN1Sequence outSignedData, ASN1Sequence cmpSignedData,
                                                        String dest, String sig, StringBuilder errorText) throws IOException {
        String sigFileName = dest.substring(0, dest.lastIndexOf("."));
        String outSigFile = sigFileName + "_" + sig + "_out.txt";
        String cmpSigFile = sigFileName + "_" + sig + "_cmp.txt";
        writeToFile(outSigFile, sig + "\n" + ASN1Dump.dumpAsString(outSignedData, true) + "\n");
        writeToFile(cmpSigFile, sig + "\n" + ASN1Dump.dumpAsString(cmpSignedData, true) + "\n");

        errorText.insert(0, "See signature output files: " +
                "\nout: " + UrlUtil.getNormalizedFileUriString(outSigFile) +
                "\ncmp: " + UrlUtil.getNormalizedFileUriString(cmpSigFile) + "\n");
    }

    private static boolean compareDssEntries(PdfDocument outDocument, PdfDocument cmpDocument,
                                             String dest, StringBuilder errorText) throws IOException {
        PdfDictionary outDss = outDocument.getCatalog().getPdfObject().getAsDictionary(PdfName.DSS);
        PdfDictionary cmpDss = cmpDocument.getCatalog().getPdfObject().getAsDictionary(PdfName.DSS);
        if (outDss == null || cmpDss == null) {
            if (outDss == cmpDss) {
                return true;
            }
            addError(errorText, "DSS dictionaries are different");
            return false;
        }

        boolean ocspCertificatesEqual =
                compareRevocationDataFromDss(
                        outDss, cmpDss, PdfName.OCSPs, dest, errorText,
                        (outSequence, cmpSequence, errorStringBuilder) ->
                                SignaturesCompareTool.compareAsn1Structures(outSequence, cmpSequence, errorStringBuilder));
        boolean crlCertificatesEqual =
                compareRevocationDataFromDss(
                        outDss, cmpDss, PdfName.CRLs, dest, errorText,
                        (outSequence, cmpSequence, errorStringBuilder) ->
                                SignaturesCompareTool.compareSequencesWithSignatureValue(outSequence, cmpSequence, errorStringBuilder));

        return ocspCertificatesEqual && crlCertificatesEqual;
    }

    private static boolean compareRevocationDataFromDss(PdfDictionary outDss, PdfDictionary cmpDss,
                                                        PdfName entryName, String dest,
                                                        StringBuilder errorText,
                                                        SequenceComparator comparator) throws IOException {
        String errorMessage = entryName.getValue() + " entries inside DSS dictionaries are different";
        PdfArray outDssEntry = outDss.getAsArray(entryName);
        PdfArray cmpDssEntry = cmpDss.getAsArray(entryName);
        if (outDssEntry == null || cmpDssEntry == null) {
            if (outDssEntry == cmpDssEntry) {
                return true;
            }
            addError(errorText, errorMessage);
            return false;
        }
        if (outDssEntry.size() != cmpDssEntry.size()) {
            addError(errorText, errorMessage);
            return false;
        }

        for (int i = 0; i < outDssEntry.size(); ++i) {
            PdfStream outDssEntryItem = outDssEntry.getAsStream(i);
            PdfStream cmpDssEntryItem = cmpDssEntry.getAsStream(i);

            if (outDssEntryItem == null || cmpDssEntryItem == null) {
                if (outDssEntryItem == cmpDssEntryItem) {
                    continue;
                }
                addError(errorText, errorMessage);
                return false;
            }

            ASN1Sequence outDecodedItem = (ASN1Sequence) ASN1Sequence.fromByteArray(outDssEntryItem.getBytes());
            ASN1Sequence cmpDecodedItem = (ASN1Sequence) ASN1Sequence.fromByteArray(cmpDssEntryItem.getBytes());

            if (!comparator.compare(outDecodedItem, cmpDecodedItem, errorText)) {
                createTxtFilesFromAsn1Sequences(
                        outDecodedItem, cmpDecodedItem, dest, "DSS_" + entryName.getValue() + "_" + i, errorText);
                return false;
            }
        }
        return true;
    }

    private static boolean compareOcspResponses(ASN1Encodable[] outOcspResponse, ASN1Encodable[] cmpOcspResponse,
                                                StringBuilder errorText) throws IOException {
        if (outOcspResponse.length != 2 || cmpOcspResponse.length != 2) {
            addError(errorText, "OCSP response has unexpected structure");
        }
        ASN1OctetString outResponseString = (ASN1OctetString) outOcspResponse[1];
        ASN1OctetString cmpResponseString = (ASN1OctetString) cmpOcspResponse[1];
        if (outResponseString.equals(cmpResponseString)) {
            return true;
        }

        ASN1Sequence parsedOutResponse = (ASN1Sequence) ASN1Sequence.fromByteArray(outResponseString.getOctets());
        ASN1Sequence parsedCmpResponse = (ASN1Sequence) ASN1Sequence.fromByteArray(cmpResponseString.getOctets());

        return compareSequencesWithSignatureValue(parsedOutResponse, parsedCmpResponse, errorText);
    }

    /**
     * SignedData is top-level CMS-object for signatures, see "5.1. SignedData Type" at
     * https://datatracker.ietf.org/doc/html/rfc5652#section-5.1 .
     *
     * @param outSignedData current output signed data
     * @param cmpSignedData reference signed data used for comparison as a ground truth
     * @param errorText     string builder in order to accumulate errors
     *
     * @return true if signed data objects are the similar, false otherwise
     *
     * @throws IOException is thrown if object data parsing failed
     */
    private static boolean compareSignedData(ASN1Sequence outSignedData, ASN1Sequence cmpSignedData,
                                                   StringBuilder errorText) throws IOException {
        if (outSignedData.size() != cmpSignedData.size() || outSignedData.size() != 2) {
            addError(errorText, "Signature top level elements count is incorrect (should be exactly 2):",
                    String.valueOf(outSignedData.size()),
                    String.valueOf(cmpSignedData.size()));
            return false;
        }

        ASN1ObjectIdentifier outObjId = (ASN1ObjectIdentifier) outSignedData.getObjectAt(0);
        ASN1ObjectIdentifier cmpObjId = (ASN1ObjectIdentifier) cmpSignedData.getObjectAt(0);
        if (!outObjId.equals(cmpObjId) || !outObjId.getId().equals(OID_SIGNED_DATA)) {
            addError(errorText, "Signatures object identifier is incorrect (should be "
                            + OID_SIGNED_DATA  + ")",
                    String.valueOf(outObjId.getId()),
                    String.valueOf(cmpObjId.getId()));
            return false;
        }

        ASN1Sequence outContent = (ASN1Sequence) ((ASN1TaggedObject) outSignedData.getObjectAt(1)).getBaseObject().toASN1Primitive();
        ASN1Sequence cmpContent = (ASN1Sequence) ((ASN1TaggedObject) cmpSignedData.getObjectAt(1)).getBaseObject().toASN1Primitive();
        if (outContent.size() != cmpContent.size()) {
            addError(errorText, "Signatures base elements counts are different",
                    String.valueOf(outContent.size()),
                    String.valueOf(cmpContent.size()));
            return false;
        }

        int signerInfoIndex = getSignerInfoIndex(cmpContent);
        if (!(outContent.getObjectAt(signerInfoIndex) instanceof ASN1Set)) {
            addError(errorText, "SignerInfo object indexes are different", null, null);
            return false;
        }

        for (int i = 0; i < cmpContent.size(); i++) {

            // SignerInfo objects will be compared separately
            if (i == signerInfoIndex) {
                continue;
            }

            // Sequences and sets related to timestamp token info should be ignored.
            if (OID_TST_INFO.equals(getASN1ObjectId(cmpContent.getObjectAt(i).toASN1Primitive())) &&
                    OID_TST_INFO.equals(getASN1ObjectId(outContent.getObjectAt(i).toASN1Primitive()))) {
                continue;
            }

            if (!cmpContent.getObjectAt(i).equals(outContent.getObjectAt(i))) {
                addError(errorText, "SignedData objects are different", null, null);
                return false;
            }
        }

        ASN1Set cmpSignerInfos = (ASN1Set) cmpContent.getObjectAt(signerInfoIndex);
        ASN1Set outSignerInfos = (ASN1Set) outContent.getObjectAt(signerInfoIndex);

        // Currently, iText signature validation mechanism do not support signatures,
        // containing more than one SignerInfo entry. However, it is still valid signature.
        if (cmpSignerInfos.size() != outSignerInfos.size() || cmpSignerInfos.size() != 1) {
            addError(errorText, "Incorrect SignerInfos objects count", String.valueOf(outSignerInfos.size()),
                    String.valueOf(cmpSignerInfos.size()));
            return false;
        }

        ASN1Sequence outSignerInfo = (ASN1Sequence) outSignerInfos.getObjectAt(0);
        ASN1Sequence cmpSignerInfo = (ASN1Sequence) cmpSignerInfos.getObjectAt(0);

        return compareSequencesWithSignatureValue(outSignerInfo, cmpSignerInfo, errorText);
    }

    private static boolean compareSequencesWithSignatureValue(ASN1Sequence outSequence, ASN1Sequence cmpSequence,
                                                              StringBuilder errorText) throws IOException {
        if (cmpSequence.size() != outSequence.size()) {
            addError(errorText, "Incorrect SignerInfo entries count", String.valueOf(outSequence.size()),
                    String.valueOf(cmpSequence.size()));
            return false;
        }

        for (int i = 0; i < cmpSequence.size(); i++) {
            // Skipping comparison of encoded strings fields which are SignatureValue fields.
            // They are expected to be different.
            if (outSequence.getObjectAt(i) instanceof ASN1OctetString ||
                    outSequence.getObjectAt(i) instanceof ASN1BitString) {
                if (outSequence.getObjectAt(i).getClass().equals(cmpSequence.getObjectAt(i).getClass())) {
                    continue;
                } else {
                    addError(errorText, "Signature values indexes are different!", null, null);
                    return false;
                }
            }

            if (!compareAsn1Structures(
                    outSequence.getObjectAt(i).toASN1Primitive(),
                    cmpSequence.getObjectAt(i).toASN1Primitive(), errorText)) {
                return false;
            }
        }
        return true;
    }

    private static boolean compareAsn1Structures(ASN1Primitive out, ASN1Primitive cmp,
                                                 StringBuilder errorText) throws IOException {
        if (!out.getClass().equals(cmp.getClass())) {
            addError(errorText, "ASN1 objects types are different", out.getClass().getName(),
                    cmp.getClass().getName());
            return false;
        }

        if (cmp instanceof ASN1TaggedObject) {
            return compareAsn1Structures(
                    ((ASN1TaggedObject) out).getBaseObject().toASN1Primitive(), ((ASN1TaggedObject) cmp).getBaseObject().toASN1Primitive(), errorText);
        } else if (cmp instanceof ASN1Sequence) {
            if (!compareContainers(((ASN1Sequence) out).toArray(), ((ASN1Sequence) cmp).toArray(), errorText)) {
                addError(errorText, "ASN1Sequence objects are different");
                return false;
            }
        } else if (cmp instanceof ASN1Set) {
            if (!compareContainers(((ASN1Set) out).toArray(), ((ASN1Set) cmp).toArray(), errorText)) {
                addError(errorText, "ASN1Set objects are different");
                return false;
            }
        } else if (cmp instanceof ASN1GeneralizedTime || cmp instanceof ASN1UTCTime) {
            // Ignore time values since usually they shouldn't be equal
            return true;
        } else {
            if (!cmp.equals(out)) {
                addError(errorText, "ASN1 objects are different",
                        ASN1Dump.dumpAsString(out, true), ASN1Dump.dumpAsString(cmp, true));
                return false;
            }
        }
        return true;
    }

    private static boolean compareContainers(ASN1Encodable[] outArray,
                                             ASN1Encodable[] cmpArray, StringBuilder errorText) throws IOException {
        if (cmpArray.length != outArray.length) {
            addError(errorText, "Container lengths are different",
                    Integer.toString(outArray.length), Integer.toString(cmpArray.length));
            return false;
        }

        String cmpASN1ObjectId = getASN1ObjectId(cmpArray);
        String outASN1ObjectId = getASN1ObjectId(outArray);
        if (!Objects.equals(cmpASN1ObjectId, outASN1ObjectId)) {
            addError(errorText, "Containers ids are different", outASN1ObjectId, cmpASN1ObjectId);
            return false;
        }

        if (IGNORED_OIDS.contains(cmpASN1ObjectId)) {
            return true;
        }
        if (OID_SIGNATURE_TIMESTAMP_ATTRIBUTE.equals(cmpASN1ObjectId)) {
            return compareTimestampAttributes(outArray, cmpArray, errorText);
        }
        if (OID_OCSP_RESPONSE.equals(cmpASN1ObjectId)) {
            return compareOcspResponses(outArray, cmpArray, errorText);
        }
        if (OID_ADBE_REVOCATION_INFO_ARCHIVAL.equals(cmpASN1ObjectId)) {
            return compareRevocationInfoArchivalAttribute(outArray, cmpArray, errorText);
        }
        for (int i = 0; i < cmpArray.length; i++) {
            if (!compareAsn1Structures(outArray[i].toASN1Primitive(), cmpArray[i].toASN1Primitive(), errorText)) {
                return false;
            }
        }
        return true;
    }

    /**
     * See ISO 32000-2, 12.8.3.3.2 "Revocation of CMS-based signatures"
     *
     * @param out       out signature revocation info attribute value
     * @param cmp       cmp signature revocation info attribute value
     * @param errorText string builder in order to accumulate errors
     *
     * @return true if signed data objects are the similar, false otherwise
     */
    private static boolean compareRevocationInfoArchivalAttribute(ASN1Encodable[] out, ASN1Encodable[] cmp,
            StringBuilder errorText) throws IOException {
        String structureIsInvalidError = "Signature revocation info archival attribute structure is invalid";
        if (!isExpectedRevocationInfoArchivalAttributeStructure(out)
                || !isExpectedRevocationInfoArchivalAttributeStructure(cmp)) {
            addError(errorText, structureIsInvalidError,
                    String.join("", Arrays.stream(out).map(e -> ASN1Dump.dumpAsString(e)).collect(Collectors.toList())),
                    String.join("", Arrays.stream(cmp).map(e -> ASN1Dump.dumpAsString(e)).collect(Collectors.toList())));
            return false;
        }

        ASN1Sequence outSequence = ((ASN1Sequence) ((ASN1Set) out[1]).getObjectAt(0).toASN1Primitive());
        ASN1Sequence cmpSequence = ((ASN1Sequence) ((ASN1Set) cmp[1]).getObjectAt(0).toASN1Primitive());
        if (outSequence.size() != cmpSequence.size()) {
            addError(errorText,
                    "Signature revocation info archival attributes have different sets of revocation info types (different sizes)",
                    String.valueOf(outSequence.size()), String.valueOf(cmpSequence.size()));
            return false;
        }

        for (int i = 0; i < outSequence.size(); i++) {
            if (!(outSequence.getObjectAt(i) instanceof ASN1TaggedObject)
                    || !(cmpSequence.getObjectAt(i) instanceof ASN1TaggedObject)) {
                addError(errorText, structureIsInvalidError,
                        String.join("", Arrays.stream(out).map(e -> ASN1Dump.dumpAsString(e)).collect(Collectors.toList())),
                        String.join("", Arrays.stream(cmp).map(e -> ASN1Dump.dumpAsString(e)).collect(Collectors.toList())));
                return false;
            }
            ASN1TaggedObject outTaggedObject = (ASN1TaggedObject) outSequence.getObjectAt(i);
            ASN1TaggedObject cmpTaggedObject = (ASN1TaggedObject) cmpSequence.getObjectAt(i);
            if (outTaggedObject.getTagNo() != cmpTaggedObject.getTagNo()) {
                addError(errorText,
                        "Signature revocation info archival attributes have different tagged objects tag numbers",
                        String.valueOf(outTaggedObject.getTagNo()), String.valueOf(cmpTaggedObject.getTagNo()));
                return false;
            }

            if (!(outTaggedObject.getBaseObject().toASN1Primitive() instanceof ASN1Sequence)
                    || !(cmpTaggedObject.getBaseObject().toASN1Primitive() instanceof ASN1Sequence)) {
                addError(errorText, structureIsInvalidError,
                        String.join("", Arrays.stream(out).map(e -> ASN1Dump.dumpAsString(e)).collect(Collectors.toList())),
                        String.join("", Arrays.stream(cmp).map(e -> ASN1Dump.dumpAsString(e)).collect(Collectors.toList())));
                return false;
            }

            // revocation entries can be either CRLs or OCSPs in most cases
            ASN1Sequence outRevocationEntries = (ASN1Sequence) outTaggedObject.getBaseObject().toASN1Primitive();
            ASN1Sequence cmpRevocationEntries = (ASN1Sequence) cmpTaggedObject.getBaseObject().toASN1Primitive();
            if (outRevocationEntries.size() != cmpRevocationEntries.size()) {
                addError(errorText,
                        "Signature revocation info archival attributes have different number of entries",
                        String.valueOf(outRevocationEntries.size()), String.valueOf(cmpRevocationEntries.size()));
                return false;
            }

            if (outTaggedObject.getTagNo() == 0) {
                // CRL revocation info case
                for (int j = 0; j < outRevocationEntries.size(); j++) {
                    if (!(outRevocationEntries.getObjectAt(j) instanceof ASN1Sequence)
                            || !(outRevocationEntries.getObjectAt(j) instanceof ASN1Sequence)) {
                        addError(errorText,
                                "Signature revocation info attribute has unexpected CRL entry type",
                                outRevocationEntries.getObjectAt(j).getClass().getName().toString(),
                                cmpRevocationEntries.getObjectAt(j).getClass().getName().toString());
                        return false;
                    }
                    if (!compareSequencesWithSignatureValue(
                            ((ASN1Sequence) outRevocationEntries.getObjectAt(j)),
                            ((ASN1Sequence) cmpRevocationEntries.getObjectAt(j)), errorText)) {
                        addError(errorText,
                                MessageFormatUtil.format(
                                        "Signature revocation info attribute CRLs at {0} are different",
                                        String.valueOf(j)));
                        return false;
                    }
                }
            } else {
                if (!compareAsn1Structures(outRevocationEntries, cmpRevocationEntries, errorText)) {
                    addError(errorText, "Revocation info attribute entries are different");
                    return false;
                }
            }
        }
        return true;
    }

    private static boolean isExpectedRevocationInfoArchivalAttributeStructure(ASN1Encodable[] container) {
        return container.length == 2
                && container[1] instanceof ASN1Set
                && ((ASN1Set) container[1]).size() == 1
                && ((ASN1Set) container[1]).getObjectAt(0).toASN1Primitive() instanceof ASN1Sequence;
    }

    private static boolean compareTimestampAttributes(ASN1Encodable[] out, ASN1Encodable[] cmp,
                                                      StringBuilder errorText) throws IOException {
        if (cmp.length == 2) {
            if (cmp[1] instanceof ASN1Set && out[1] instanceof ASN1Set) {
                ASN1Primitive outSequence = ((ASN1Set) out[1]).getObjectAt(0).toASN1Primitive();
                ASN1Primitive cmpSequence = ((ASN1Set) cmp[1]).getObjectAt(0).toASN1Primitive();

                if (outSequence instanceof ASN1Sequence && cmpSequence instanceof ASN1Sequence) {
                    return compareSignedData((ASN1Sequence) outSequence, (ASN1Sequence) cmpSequence, errorText);
                }
            }
        }

        addError(errorText, "Signature timestamp attribute structure is invalid",
                String.join("", Arrays.stream(out).map(e -> ASN1Dump.dumpAsString(e)).collect(Collectors.toList())),
                String.join("", Arrays.stream(cmp).map(e -> ASN1Dump.dumpAsString(e)).collect(Collectors.toList())));
        return false;
    }

    private static int getSignerInfoIndex(ASN1Sequence baseElement) {
        for (int i = 3; i < baseElement.size(); i++) {
            if (baseElement.getObjectAt(i) instanceof ASN1Set) {
                return i;
            }
        }

        throw new IllegalStateException("SignerInfo entry has not been found.");
    }

    private static String getASN1ObjectId(ASN1Primitive primitive) {
        if (primitive instanceof ASN1Sequence) {
            return getASN1ObjectId(((ASN1Sequence) primitive).toArray());
        }
        if (primitive instanceof ASN1Set) {
            return getASN1ObjectId(((ASN1Set) primitive).toArray());
        }
        return null;
    }

    private static String getASN1ObjectId(ASN1Encodable[] primitives) {
        if (primitives.length != 0 && primitives[0] instanceof ASN1ObjectIdentifier) {
            return ((ASN1ObjectIdentifier) primitives[0]).getId();
        }
        return null;
    }

    private static ASN1Primitive getSignatureContent(String signatureName, SignatureUtil util) throws IOException {
        PdfSignature signature = util.getSignature(signatureName);
        byte[] contents = signature.getContents().getValueBytes();
        ASN1InputStream inputStream = new ASN1InputStream(new ByteArrayInputStream(contents));
        return inputStream.readObject();
    }

    private static void writeToFile(String path, String content) throws IOException {
        try (FileWriter writer = new FileWriter(path, true)) {
            writer.write(content);
        }
    }

    private static void addError(StringBuilder errorBuilder, String errorText) {
        addError(errorBuilder, errorText, null, null);
    }

    private static void addError(StringBuilder errorBuilder, String errorText, String out, String cmp) {
        errorBuilder.append(errorText);
        if (null != out) {
            errorBuilder.append("\nout: ").append(out);
        }

        if (null != cmp) {
            errorBuilder.append("\ncmp: ").append(cmp);
        }

        errorBuilder.append("\n\n");
    }

    @FunctionalInterface
    interface SequenceComparator {
        boolean compare(ASN1Sequence outSequence, ASN1Sequence cmpSequence, StringBuilder errorText) throws IOException;
    }
}
