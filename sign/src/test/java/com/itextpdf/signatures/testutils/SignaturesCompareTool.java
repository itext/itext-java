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
package com.itextpdf.signatures.testutils;

import com.itextpdf.bouncycastleconnector.BouncyCastleFactoryCreator;
import com.itextpdf.commons.bouncycastle.IBouncyCastleFactory;
import com.itextpdf.commons.bouncycastle.asn1.IASN1Encodable;
import com.itextpdf.commons.bouncycastle.asn1.IASN1InputStream;
import com.itextpdf.commons.bouncycastle.asn1.IASN1ObjectIdentifier;
import com.itextpdf.commons.bouncycastle.asn1.IASN1OctetString;
import com.itextpdf.commons.bouncycastle.asn1.IASN1Primitive;
import com.itextpdf.commons.bouncycastle.asn1.IASN1Sequence;
import com.itextpdf.commons.bouncycastle.asn1.IASN1Set;
import com.itextpdf.commons.bouncycastle.asn1.IASN1TaggedObject;
import com.itextpdf.commons.bouncycastle.asn1.util.IASN1Dump;
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

public class SignaturesCompareTool {
    private static final IBouncyCastleFactory BOUNCY_CASTLE_FACTORY = BouncyCastleFactoryCreator.getFactory();

    private static final String OID_MESSAGE_DIGEST = "1.2.840.113549.1.9.4";
    private static final String OID_SIGNED_DATA = "1.2.840.113549.1.7.2";
    private static final String OID_TST_INFO = "1.2.840.113549.1.9.16.1.4";
    private static final String OID_SIGNING_TIME = "1.2.840.113549.1.9.5";
    private static final String OID_SIGNATURE_TIMESTAMP_ATTRIBUTE = "1.2.840.113549.1.9.16.2.14";
    private static final String OID_ADBE_REVOCATION_INFO_ARCHIVAL = "1.2.840.113583.1.1.8";
    private static final String OID_OCSP_RESPONSE = "1.3.6.1.5.5.7.48.1.1";
    private static final String OID_OCSP_NONCE_EXTENSION = "1.3.6.1.5.5.7.48.1.2";
    private static final String ID_ATTR_PDF_MAC_DATA = "1.0.32004.1.2";

    private static final IASN1Dump DUMP = BOUNCY_CASTLE_FACTORY.createASN1Dump();

    private static final Set<String> IGNORED_OIDS;

    static {
        HashSet<String> tempSet = new HashSet<>();
        tempSet.add(OID_MESSAGE_DIGEST);
        tempSet.add(OID_TST_INFO);
        tempSet.add(OID_SIGNING_TIME);
        tempSet.add(OID_OCSP_NONCE_EXTENSION);
        tempSet.add(ID_ATTR_PDF_MAC_DATA);
        IGNORED_OIDS = Collections.unmodifiableSet(tempSet);
    }

    public static String compareSignatures(String dest, String cmp) {
        return compareSignatures(dest, cmp, new ReaderProperties(), new ReaderProperties());
    }

    public static String compareSignatures(String dest, String cmp, ReaderProperties destProperties,
            ReaderProperties cmpProperties) {
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
                IASN1Sequence outSignedData = BOUNCY_CASTLE_FACTORY.createASN1Sequence(
                        getSignatureContent(sig, outSigUtil));
                IASN1Sequence cmpSignedData = BOUNCY_CASTLE_FACTORY.createASN1Sequence(
                        getSignatureContent(sig, cmpSigUtil));

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

    private static void createTxtFilesFromAsn1Sequences(IASN1Sequence outSignedData, IASN1Sequence cmpSignedData,
            String dest, String sig, StringBuilder errorText) throws IOException {
        String sigFileName = dest.substring(0, dest.lastIndexOf("."));
        String outSigFile = sigFileName + "_" + sig + "_out.txt";
        String cmpSigFile = sigFileName + "_" + sig + "_cmp.txt";
        writeToFile(outSigFile,
                sig + "\n" + DUMP.dumpAsString(outSignedData, true) + "\n");
        writeToFile(cmpSigFile,
                sig + "\n" + DUMP.dumpAsString(cmpSignedData, true) + "\n");

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
                                SignaturesCompareTool.compareAsn1Structures(outSequence, cmpSequence,
                                        errorStringBuilder));
        boolean crlCertificatesEqual =
                compareRevocationDataFromDss(
                        outDss, cmpDss, PdfName.CRLs, dest, errorText,
                        (outSequence, cmpSequence, errorStringBuilder) ->
                                SignaturesCompareTool.compareSequencesWithSignatureValue(outSequence, cmpSequence,
                                        errorStringBuilder));

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

            IASN1Sequence outDecodedItem = BOUNCY_CASTLE_FACTORY.createASN1Sequence(outDssEntryItem.getBytes());
            IASN1Sequence cmpDecodedItem = BOUNCY_CASTLE_FACTORY.createASN1Sequence(cmpDssEntryItem.getBytes());

            if (!comparator.compare(outDecodedItem, cmpDecodedItem, errorText)) {
                createTxtFilesFromAsn1Sequences(
                        outDecodedItem, cmpDecodedItem, dest, "DSS_" + entryName.getValue() + "_" + i, errorText);
                return false;
            }
        }
        return true;
    }

    private static boolean compareOcspResponses(IASN1Encodable[] outOcspResponse, IASN1Encodable[] cmpOcspResponse,
            StringBuilder errorText) throws IOException {
        if (outOcspResponse.length != 2 || cmpOcspResponse.length != 2) {
            addError(errorText, "OCSP response has unexpected structure");
        }
        IASN1OctetString outResponseString = BOUNCY_CASTLE_FACTORY.createASN1OctetString(outOcspResponse[1]);
        IASN1OctetString cmpResponseString = BOUNCY_CASTLE_FACTORY.createASN1OctetString(cmpOcspResponse[1]);
        if (outResponseString.equals(cmpResponseString)) {
            return true;
        }

        IASN1Sequence parsedOutResponse = BOUNCY_CASTLE_FACTORY.createASN1Sequence(outResponseString.getOctets());
        IASN1Sequence parsedCmpResponse = BOUNCY_CASTLE_FACTORY.createASN1Sequence(cmpResponseString.getOctets());

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
    private static boolean compareSignedData(IASN1Sequence outSignedData, IASN1Sequence cmpSignedData,
            StringBuilder errorText) throws IOException {
        if (outSignedData.size() != cmpSignedData.size() || outSignedData.size() != 2) {
            addError(errorText, "Signature top level elements count is incorrect (should be exactly 2):",
                    String.valueOf(outSignedData.size()),
                    String.valueOf(cmpSignedData.size()));
            return false;
        }

        IASN1ObjectIdentifier outObjId = BOUNCY_CASTLE_FACTORY.createASN1ObjectIdentifier(outSignedData.getObjectAt(0));
        IASN1ObjectIdentifier cmpObjId = BOUNCY_CASTLE_FACTORY.createASN1ObjectIdentifier(cmpSignedData.getObjectAt(0));
        if (!outObjId.equals(cmpObjId) || !outObjId.getId().equals(OID_SIGNED_DATA)) {
            addError(errorText, "Signatures object identifier is incorrect (should be "
                            + OID_SIGNED_DATA + ")",
                    String.valueOf(outObjId.getId()),
                    String.valueOf(cmpObjId.getId()));
            return false;
        }

        IASN1Sequence outContent = BOUNCY_CASTLE_FACTORY.createASN1Sequence(
                BOUNCY_CASTLE_FACTORY.createASN1TaggedObject(outSignedData.getObjectAt(1)).getObject());
        IASN1Sequence cmpContent = BOUNCY_CASTLE_FACTORY.createASN1Sequence(
                BOUNCY_CASTLE_FACTORY.createASN1TaggedObject(cmpSignedData.getObjectAt(1)).getObject());
        if (outContent.size() != cmpContent.size()) {
            addError(errorText, "Signatures base elements counts are different",
                    String.valueOf(outContent.size()),
                    String.valueOf(cmpContent.size()));
            return false;
        }

        int signerInfoIndex = getSignerInfoIndex(cmpContent);
        if (BOUNCY_CASTLE_FACTORY.createASN1Set(outContent.getObjectAt(signerInfoIndex)) == null) {
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

        IASN1Set cmpSignerInfos = BOUNCY_CASTLE_FACTORY.createASN1Set(cmpContent.getObjectAt(signerInfoIndex));
        IASN1Set outSignerInfos = BOUNCY_CASTLE_FACTORY.createASN1Set(outContent.getObjectAt(signerInfoIndex));

        // Currently, iText signature validation mechanism do not support signatures,
        // containing more than one SignerInfo entry. However, it is still valid signature.
        if (cmpSignerInfos.size() != outSignerInfos.size() || cmpSignerInfos.size() != 1) {
            addError(errorText, "Incorrect SignerInfos objects count", String.valueOf(outSignerInfos.size()),
                    String.valueOf(cmpSignerInfos.size()));
            return false;
        }

        IASN1Sequence outSignerInfo = BOUNCY_CASTLE_FACTORY.createASN1Sequence(outSignerInfos.getObjectAt(0));
        IASN1Sequence cmpSignerInfo = BOUNCY_CASTLE_FACTORY.createASN1Sequence(cmpSignerInfos.getObjectAt(0));

        return compareSequencesWithSignatureValue(outSignerInfo, cmpSignerInfo, errorText);
    }

    private static boolean compareSequencesWithSignatureValue(IASN1Sequence outSequence, IASN1Sequence cmpSequence,
            StringBuilder errorText) throws IOException {
        if (cmpSequence.size() != outSequence.size()) {
            addError(errorText, "Incorrect SignerInfo entries count", String.valueOf(outSequence.size()),
                    String.valueOf(cmpSequence.size()));
            return false;
        }

        for (int i = 0; i < cmpSequence.size(); i++) {
            // Skipping comparison of encoded strings fields which are SignatureValue fields.
            // They are expected to be different.
            if (BOUNCY_CASTLE_FACTORY.createASN1OctetString(outSequence.getObjectAt(i)) != null ||
                    BOUNCY_CASTLE_FACTORY.createASN1BitString(outSequence.getObjectAt(i)) != null) {
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

    private static boolean compareAsn1Structures(IASN1Primitive out, IASN1Primitive cmp,
            StringBuilder errorText) throws IOException {
        if (!out.getClass().equals(cmp.getClass())) {
            addError(errorText, "ASN1 objects types are different", out.getClass().getName(),
                    cmp.getClass().getName());
            return false;
        }

        if (BOUNCY_CASTLE_FACTORY.createASN1TaggedObject(cmp) != null) {
            return compareAsn1Structures(
                    BOUNCY_CASTLE_FACTORY.createASN1TaggedObject(out).getObject(),
                    BOUNCY_CASTLE_FACTORY.createASN1TaggedObject(cmp).getObject(), errorText);
        } else if (BOUNCY_CASTLE_FACTORY.createASN1Sequence(cmp) != null) {
            if (!compareContainers(BOUNCY_CASTLE_FACTORY.createASN1Sequence(out).toArray(),
                    BOUNCY_CASTLE_FACTORY.createASN1Sequence(cmp).toArray(), errorText)) {
                addError(errorText, "ASN1Sequence objects are different");
                return false;
            }
        } else if (BOUNCY_CASTLE_FACTORY.createASN1Set(cmp) != null) {
            if (!compareContainers(BOUNCY_CASTLE_FACTORY.createASN1Set(out).toArray(),
                    BOUNCY_CASTLE_FACTORY.createASN1Set(cmp).toArray(), errorText)) {
                addError(errorText, "ASN1Set objects are different");
                return false;
            }
        } else if (BOUNCY_CASTLE_FACTORY.createASN1GeneralizedTime(cmp) != null
                || BOUNCY_CASTLE_FACTORY.createASN1UTCTime(cmp) != null) {
            // Ignore time values since usually they shouldn't be equal
            return true;
        } else {
            if (!cmp.equals(out)) {
                addError(errorText, "ASN1 objects are different",
                        DUMP.dumpAsString(out, true),
                        DUMP.dumpAsString(cmp, true));
                return false;
            }
        }
        return true;
    }

    private static boolean compareContainers(IASN1Encodable[] outArray,
            IASN1Encodable[] cmpArray, StringBuilder errorText) throws IOException {
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
    private static boolean compareRevocationInfoArchivalAttribute(IASN1Encodable[] out, IASN1Encodable[] cmp,
            StringBuilder errorText) throws IOException {
        String structureIsInvalidError = "Signature revocation info archival attribute structure is invalid";
        if (!isExpectedRevocationInfoArchivalAttributeStructure(out)
                || !isExpectedRevocationInfoArchivalAttributeStructure(cmp)) {
            addError(errorText, structureIsInvalidError,
                    String.join("", Arrays.stream(out).map(e -> DUMP.dumpAsString(e))
                            .collect(Collectors.toList())),
                    String.join("", Arrays.stream(cmp).map(e -> DUMP.dumpAsString(e))
                            .collect(Collectors.toList())));
            return false;
        }

        IASN1Sequence outSequence = BOUNCY_CASTLE_FACTORY.createASN1Sequence(BOUNCY_CASTLE_FACTORY.createASN1Set(out[1]).getObjectAt(0).toASN1Primitive());
        IASN1Sequence cmpSequence = BOUNCY_CASTLE_FACTORY.createASN1Sequence(BOUNCY_CASTLE_FACTORY.createASN1Set(cmp[1]).getObjectAt(0).toASN1Primitive());
        if (outSequence.size() != cmpSequence.size()) {
            addError(errorText,
                    "Signature revocation info archival attributes have different sets of revocation info types "
                            + "(different sizes)",
                    String.valueOf(outSequence.size()), String.valueOf(cmpSequence.size()));
            return false;
        }

        for (int i = 0; i < outSequence.size(); i++) {
            if (BOUNCY_CASTLE_FACTORY.createASN1TaggedObject(outSequence.getObjectAt(i)) == null
                    || BOUNCY_CASTLE_FACTORY.createASN1TaggedObject(cmpSequence.getObjectAt(i)) == null) {
                addError(errorText, structureIsInvalidError,
                        String.join("",
                                Arrays.stream(out).map(e -> DUMP.dumpAsString(e))
                                        .collect(Collectors.toList())),
                        String.join("",
                                Arrays.stream(cmp).map(e -> DUMP.dumpAsString(e))
                                        .collect(Collectors.toList())));
                return false;
            }
            IASN1TaggedObject outTaggedObject = BOUNCY_CASTLE_FACTORY.createASN1TaggedObject(outSequence.getObjectAt(i));
            IASN1TaggedObject cmpTaggedObject = BOUNCY_CASTLE_FACTORY.createASN1TaggedObject(cmpSequence.getObjectAt(i));
            if (outTaggedObject.getTagNo() != cmpTaggedObject.getTagNo()) {
                addError(errorText,
                        "Signature revocation info archival attributes have different tagged objects tag numbers",
                        String.valueOf(outTaggedObject.getTagNo()), String.valueOf(cmpTaggedObject.getTagNo()));
                return false;
            }

            if (BOUNCY_CASTLE_FACTORY.createASN1Sequence(outTaggedObject.getObject()) == null
                    || BOUNCY_CASTLE_FACTORY.createASN1Sequence(cmpTaggedObject.getObject()) == null) {
                addError(errorText, structureIsInvalidError,
                        String.join("",
                                Arrays.stream(out).map(e -> DUMP.dumpAsString(e))
                                        .collect(Collectors.toList())),
                        String.join("",
                                Arrays.stream(cmp).map(e -> DUMP.dumpAsString(e))
                                        .collect(Collectors.toList())));
                return false;
            }

            // revocation entries can be either CRLs or OCSPs in most cases
            IASN1Sequence outRevocationEntries = BOUNCY_CASTLE_FACTORY.createASN1Sequence(outTaggedObject.getObject());
            IASN1Sequence cmpRevocationEntries = BOUNCY_CASTLE_FACTORY.createASN1Sequence(cmpTaggedObject.getObject());
            if (outRevocationEntries.size() != cmpRevocationEntries.size()) {
                addError(errorText,
                        "Signature revocation info archival attributes have different number of entries",
                        String.valueOf(outRevocationEntries.size()), String.valueOf(cmpRevocationEntries.size()));
                return false;
            }

            if (outTaggedObject.getTagNo() == 0) {
                // CRL revocation info case
                for (int j = 0; j < outRevocationEntries.size(); j++) {
                    if (BOUNCY_CASTLE_FACTORY.createASN1Sequence(outRevocationEntries.getObjectAt(j)) == null
                            || BOUNCY_CASTLE_FACTORY.createASN1Sequence(outRevocationEntries.getObjectAt(j)) == null) {
                        addError(errorText,
                                "Signature revocation info attribute has unexpected CRL entry type",
                                outRevocationEntries.getObjectAt(j).getClass().getName().toString(),
                                cmpRevocationEntries.getObjectAt(j).getClass().getName().toString());
                        return false;
                    }
                    if (!compareSequencesWithSignatureValue(
                            BOUNCY_CASTLE_FACTORY.createASN1Sequence(outRevocationEntries.getObjectAt(j)),
                            BOUNCY_CASTLE_FACTORY.createASN1Sequence(cmpRevocationEntries.getObjectAt(j)), errorText)) {
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

    private static boolean isExpectedRevocationInfoArchivalAttributeStructure(IASN1Encodable[] container) {
        return container.length == 2
                && BOUNCY_CASTLE_FACTORY.createASN1Set(container[1]) != null
                && BOUNCY_CASTLE_FACTORY.createASN1Set(container[1]).size() == 1
                && BOUNCY_CASTLE_FACTORY.createASN1Sequence(
                BOUNCY_CASTLE_FACTORY.createASN1Set(container[1]).getObjectAt(0).toASN1Primitive()) != null;
    }

    private static boolean compareTimestampAttributes(IASN1Encodable[] out, IASN1Encodable[] cmp,
            StringBuilder errorText) throws IOException {
        if (cmp.length == 2) {
            if (BOUNCY_CASTLE_FACTORY.createASN1Set(cmp[1]) != null &&
                    BOUNCY_CASTLE_FACTORY.createASN1Set(out[1]) != null) {
                IASN1Primitive outSequence = BOUNCY_CASTLE_FACTORY.createASN1Set(out[1]).getObjectAt(0)
                        .toASN1Primitive();
                IASN1Primitive cmpSequence = BOUNCY_CASTLE_FACTORY.createASN1Set(cmp[1]).getObjectAt(0)
                        .toASN1Primitive();

                if (BOUNCY_CASTLE_FACTORY.createASN1Sequence(outSequence) != null &&
                        BOUNCY_CASTLE_FACTORY.createASN1Sequence(cmpSequence) != null) {
                    return compareSignedData(BOUNCY_CASTLE_FACTORY.createASN1Sequence(outSequence),
                            BOUNCY_CASTLE_FACTORY.createASN1Sequence(cmpSequence), errorText);
                }
            }
        }

        addError(errorText, "Signature timestamp attribute structure is invalid",
                String.join("", Arrays.stream(out).map(e -> DUMP.dumpAsString(e))
                        .collect(Collectors.toList())),
                String.join("", Arrays.stream(cmp).map(e -> DUMP.dumpAsString(e))
                        .collect(Collectors.toList())));
        return false;
    }

    private static int getSignerInfoIndex(IASN1Sequence baseElement) {
        for (int i = 3; i < baseElement.size(); i++) {
            if (BOUNCY_CASTLE_FACTORY.createASN1Set(baseElement.getObjectAt(i)) != null) {
                return i;
            }
        }

        throw new IllegalStateException("SignerInfo entry has not been found.");
    }

    private static String getASN1ObjectId(IASN1Primitive primitive) {
        if (BOUNCY_CASTLE_FACTORY.createASN1Sequence(primitive) != null) {
            return getASN1ObjectId(BOUNCY_CASTLE_FACTORY.createASN1Sequence(primitive).toArray());
        }
        if (BOUNCY_CASTLE_FACTORY.createASN1Set(primitive) != null) {
            return getASN1ObjectId(BOUNCY_CASTLE_FACTORY.createASN1Set(primitive).toArray());
        }
        return null;
    }

    private static String getASN1ObjectId(IASN1Encodable[] primitives) {
        if (primitives.length != 0 && BOUNCY_CASTLE_FACTORY.createASN1ObjectIdentifier(primitives[0]) != null) {
            return BOUNCY_CASTLE_FACTORY.createASN1ObjectIdentifier(primitives[0]).getId();
        }
        return null;
    }

    private static IASN1Primitive getSignatureContent(String signatureName, SignatureUtil util) throws IOException {
        PdfSignature signature = util.getSignature(signatureName);
        byte[] contents = signature.getContents().getValueBytes();
        IASN1InputStream inputStream = BOUNCY_CASTLE_FACTORY.createASN1InputStream(new ByteArrayInputStream(contents));
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
        boolean compare(IASN1Sequence outSequence, IASN1Sequence cmpSequence, StringBuilder errorText)
                throws IOException;
    }
}
