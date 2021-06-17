/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2021 iText Group NV
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

import com.itextpdf.io.util.UrlUtil;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.signatures.PdfSignature;
import com.itextpdf.signatures.SignatureUtil;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.util.ASN1Dump;

import java.io.ByteArrayInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class SignaturesCompareTool {
    private static final String OID_MESSAGE_DIGEST = "1.2.840.113549.1.9.4";
    private static final String OID_SIGNED_DATA = "1.2.840.113549.1.7.2";

    public static String compareSignatures(String dest, String cmp) throws IOException {
        StringBuilder errorText = new StringBuilder();

        try (PdfDocument outDocument = new PdfDocument(new PdfReader(dest));
             PdfDocument cmpDocument = new PdfDocument(new PdfReader(cmp))) {
            SignatureUtil outSigUtil = new SignatureUtil(outDocument);
            SignatureUtil cmpSigUtil = new SignatureUtil(cmpDocument);
            if (!cmpSigUtil.getSignatureNames().equals(outSigUtil.getSignatureNames())) {
                addError(errorText, "Signatures lists are different:",
                        String.valueOf(outSigUtil.getSignatureNames()),
                        String.valueOf(cmpSigUtil.getSignatureNames()));
            }

            List<String> signatures = cmpSigUtil.getSignatureNames();
            for (String sig : signatures) {
                boolean isFailed = false;
                ASN1Sequence outSignedData = (ASN1Sequence) getSignatureContent(sig, outSigUtil);
                ASN1Sequence cmpSignedData = (ASN1Sequence) getSignatureContent(sig, cmpSigUtil);
                if (outSignedData.size() != cmpSignedData.size() || outSignedData.size() != 2) {
                    addError(errorText, "Signature top level elements count is incorrect (should be exactly 2):",
                            String.valueOf(outSignedData.size()),
                            String.valueOf(cmpSignedData.size()));
                    isFailed = true;
                }

                ASN1ObjectIdentifier outObjId = (ASN1ObjectIdentifier) outSignedData.getObjectAt(0);
                ASN1ObjectIdentifier cmpObjId = (ASN1ObjectIdentifier) cmpSignedData.getObjectAt(0);
                if (!outObjId.equals(cmpObjId) || !outObjId.getId().equals(OID_SIGNED_DATA)) {
                    addError(errorText, "Signatures object identifier is incorrect (should be "
                                    + OID_SIGNED_DATA  + ")",
                            String.valueOf(outObjId.getId()),
                            String.valueOf(cmpObjId.getId()));
                    isFailed = true;
                }

                ASN1Sequence outContent = (ASN1Sequence) ((ASN1TaggedObject) outSignedData.getObjectAt(1)).getObject();
                ASN1Sequence cmpContent = (ASN1Sequence) ((ASN1TaggedObject) cmpSignedData.getObjectAt(1)).getObject();
                if (outContent.size() != cmpContent.size()) {
                    addError(errorText, "Signatures base elements counts are different",
                            String.valueOf(outContent.size()),
                            String.valueOf(cmpContent.size()));
                    isFailed = true;
                }

                int signerInfoIndex = getSignerInfoIndex(cmpContent);
                if (outContent.getObjectAt(signerInfoIndex) instanceof ASN1TaggedObject) {
                    addError(errorText, "SignerInfo object indexes are different", null, null);
                    isFailed = true;
                }

                for (int i = 0; i < cmpContent.size(); i++) {

                    // SignerInfo objects will be compared separately
                    if (i == signerInfoIndex) {
                        continue;
                    }

                    if (!cmpContent.getObjectAt(i).equals(outContent.getObjectAt(i))) {
                        addError(errorText, "SignedData objects are different", null, null);
                        isFailed = true;
                    }
                }

                ASN1Set cmpSignerInfos = (ASN1Set) cmpContent.getObjectAt(signerInfoIndex);
                ASN1Set outSignerInfos = (ASN1Set) outContent.getObjectAt(signerInfoIndex);

                // Currently, iText signature validation mechanism do not support signatures,
                // containing more than one SignerInfo entry. However, it is still valid signature.
                if (cmpSignerInfos.size() != outSignerInfos.size() || cmpSignerInfos.size() != 1) {
                    addError(errorText, "Incorrect SignerInfos objects count", String.valueOf(outSignerInfos.size()),
                            String.valueOf(cmpSignerInfos.size()));
                    isFailed = true;
                }

                ASN1Sequence outSignerInfo = (ASN1Sequence) cmpSignerInfos.getObjectAt(0);
                ASN1Sequence cmpSignerInfo = (ASN1Sequence) outSignerInfos.getObjectAt(0);
                if (cmpSignerInfo.size() != outSignerInfo.size()) {
                    addError(errorText, "Incorrect SignerInfo entries count", String.valueOf(outSignerInfo.size()),
                            String.valueOf(cmpSignerInfo.size()));
                    isFailed = true;
                }

                for (int i = 0; i < cmpSignerInfo.size(); i++) {

                    // Skipping comparison of ASN1OctetString fields in SignerInfo. SignerInfo is expected to have
                    // a single field of ASN1OctetString which is SignatureValue, that is expected to be
                    // different in each signature instance.
                    if (outSignerInfo.getObjectAt(i) instanceof ASN1OctetString) {
                        if (cmpSignerInfo.getObjectAt(i) instanceof ASN1OctetString) {
                            continue;
                        } else {
                            addError(errorText, "Signature values indexes are different!", null, null);
                            isFailed = true;
                        }
                    }

                    if (!isFailed) {
                        isFailed = compareAsn1Structures(outSignerInfo.getObjectAt(i).toASN1Primitive(),
                                cmpSignerInfo.getObjectAt(i).toASN1Primitive(), errorText);
                    }
                }

                if (isFailed) {
                    String sigFileName = dest.substring(0, dest.lastIndexOf("."));
                    String outSigFile = sigFileName + "_out.txt";
                    String cmpSigFile = sigFileName + "_cmp.txt";
                    writeToFile(outSigFile, sig + "\n" + ASN1Dump.dumpAsString(outSignedData, true)+ "\n");
                    writeToFile(cmpSigFile, sig + "\n" + ASN1Dump.dumpAsString(cmpSignedData, true)+ "\n");

                    errorText.insert(0, "See signature output files: \nout: "
                            + UrlUtil.getNormalizedFileUriString(outSigFile) + "\ncmp: "
                            + UrlUtil.getNormalizedFileUriString(cmpSigFile) + "\n");
                }
            }
        } catch (Exception e) {
            errorText.append(e.getMessage()).append("\n");
        }

        if (!errorText.toString().isEmpty()) {
            return errorText.toString();
        } else {
            return null;
        }
    }

    private static void writeToFile(String path, String content) throws IOException {
        try (FileWriter writer = new FileWriter(path, true)) {
            writer.write(content);
        }
    }

    private static int getSignerInfoIndex(ASN1Sequence baseElement) {
        for (int i = 3; i < baseElement.size(); i++) {
            if (!(baseElement.getObjectAt(i) instanceof ASN1TaggedObject)) {
                return i;
            }
        }

        throw new IllegalStateException("SignerInfo entry has not been found.");
    }

    private static boolean compareAsn1Structures(ASN1Primitive out, ASN1Primitive cmp, StringBuilder errorText) {
        boolean isFailed = false;
        if (!out.getClass().equals(cmp.getClass())) {
            addError(errorText, "ASN1 objects types are different", out.getClass().getName(),
                    cmp.getClass().getName());
            isFailed = true;
        }

        if (cmp instanceof ASN1TaggedObject || cmp instanceof ASN1Sequence) {
            ASN1Sequence cmpObject;
            ASN1Sequence outObject;
            if (cmp instanceof ASN1TaggedObject) {
                ASN1TaggedObject cmpTag = (ASN1TaggedObject) cmp;
                ASN1TaggedObject outTag = (ASN1TaggedObject) out;
                if (!(cmpTag.getObject() instanceof ASN1Sequence)) {
                    if (!cmpTag.getObject().equals(outTag.getObject())) {
                        addError(errorText, "ASN1 objects are different", ASN1Dump.dumpAsString(outTag, true),
                                ASN1Dump.dumpAsString(cmpTag, true));
                        isFailed = true;
                    }

                    return isFailed;
                }

                cmpObject = (ASN1Sequence) (cmpTag).getObject();
                outObject = (ASN1Sequence) (outTag).getObject();
            } else {
                cmpObject = (ASN1Sequence) cmp;
                outObject = (ASN1Sequence) out;
            }

            if (cmpObject.getObjectAt(0) instanceof ASN1ObjectIdentifier) {
                ASN1ObjectIdentifier objectIdentifier = (ASN1ObjectIdentifier) (cmpObject.getObjectAt(0));

                // Message digest should be ignored during comparing
                if (objectIdentifier.getId().equals(OID_MESSAGE_DIGEST)) {
                    return isFailed;
                }
            }
            for (int i = 0; i < cmpObject.size(); i++) {
                if (!isFailed) {
                    isFailed = compareAsn1Structures(outObject.getObjectAt(i).toASN1Primitive(),
                            cmpObject.getObjectAt(i).toASN1Primitive(), errorText);
                }
            }
        } else if (cmp instanceof ASN1Set) {
            ASN1Set cmpSet = (ASN1Set) cmp;
            ASN1Set outSet = (ASN1Set) out;
            if (!isFailed) {
                isFailed = compareAsn1Structures(cmpSet.getObjectAt(0).toASN1Primitive(),
                        outSet.getObjectAt(0).toASN1Primitive(), errorText);
            }
        } else {
            if (!cmp.equals(out)) {
                addError(errorText, "ASN1 objects are different", ASN1Dump.dumpAsString(out, true),
                        ASN1Dump.dumpAsString(cmp, true));
                isFailed = true;
            }
        }

        return isFailed;
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

    private static ASN1Primitive getSignatureContent(String signatureName, SignatureUtil util) throws IOException {
        PdfSignature signature = util.getSignature(signatureName);
        byte[] contents = signature.getContents().getValueBytes();
        ASN1InputStream inputStream = new ASN1InputStream(new ByteArrayInputStream(contents));
        return inputStream.readObject();
    }
}
