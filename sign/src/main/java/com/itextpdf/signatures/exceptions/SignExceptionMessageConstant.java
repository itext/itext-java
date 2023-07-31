/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 Apryse Group NV
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
package com.itextpdf.signatures.exceptions;

/**
 * Class that bundles all the error message templates as constants.
 */
public final class SignExceptionMessageConstant {
    public static final String ALGORITHMS_NOT_SUPPORTED = "Signing algorithms {0} and {1} are not supported.";
    
    public static final String AUTHENTICATED_ATTRIBUTE_IS_MISSING_THE_DIGEST = "Authenticated attribute is missing "
            + "the digest.";
    public static final String AVAILABLE_SPACE_IS_NOT_ENOUGH_FOR_SIGNATURE = "Available space is not enough for "
            + "signature.";
    public static final String CANNOT_DECODE_PKCS7_SIGNED_DATA_OBJECT = "Cannot decode PKCS#7 SignedData object.";
    public static final String CANNOT_FIND_SIGNING_CERTIFICATE_WITH_THIS_SERIAL = "Cannot find signing certificate "
            + "with serial {0}.";
    public static final String CANNOT_BE_VERIFIED_CERTIFICATE_CHAIN = "Cannot be verified against the KeyStore or the "
            + "certificate chain.";
    public static final String CERTIFICATION_SIGNATURE_CREATION_FAILED_DOC_SHALL_NOT_CONTAIN_SIGS = "Certification "
            + "signature creation failed. Document shall not contain any certification or approval signatures before "
            + "signing with certification signature.";
    public static final String CERTIFICATE_TEMPLATE_FOR_EXCEPTION_MESSAGE = "Certificate {0} failed: {1}";
    public static final String DICTIONARY_THIS_KEY_IS_NOT_A_NAME = "Dictionary key {0} is not a name.";
    public static final String DOCUMENT_ALREADY_PRE_CLOSED = "Document has been already pre closed.";
    public static final String DOCUMENT_MUST_BE_PRE_CLOSED = "Document must be preClosed.";
    public static final String DOCUMENT_MUST_HAVE_READER = "Document must have reader.";
    public static final String FAILED_TO_GET_TSA_RESPONSE = "Failed to get TSA response from {0}.";
    public static final String FIELD_ALREADY_SIGNED = "Field has been already signed.";
    public static final String FIELD_NAMES_CANNOT_CONTAIN_A_DOT = "Field names cannot contain a dot.";
    public static final String FIELD_TYPE_IS_NOT_A_SIGNATURE_FIELD_TYPE = "Field type is not a signature field type.";
    public static final String INVALID_HTTP_RESPONSE = "Invalid http response {0}.";
    public static final String INVALID_STATE_WHILE_CHECKING_CERT_CHAIN = "Invalid state. Possible circular "
            + "certificate chain.";
    public static final String INVALID_TSA_RESPONSE = "Invalid TSA {0} response code {1}.";
    public static final String NO_CRYPTO_DICTIONARY_DEFINED = "No crypto dictionary defined.";
    public static final String NOT_A_VALID_PKCS7_OBJECT_NOT_A_SEQUENCE = "Not a valid PKCS#7 object - not a sequence";
    public static final String NOT_A_VALID_PKCS7_OBJECT_NOT_SIGNED_DATA = "Not a valid PKCS#7 object - not signed "
            + "data.";
    public static final String NOT_ENOUGH_SPACE = "Not enough space.";
    public static final String SIGNATURE_WITH_THIS_NAME_IS_NOT_THE_LAST_IT_DOES_NOT_COVER_WHOLE_DOCUMENT = "Signature "
            + "with name {0} is not the last. It doesn't cover the whole document.";
    public static final String THE_NAME_OF_THE_DIGEST_ALGORITHM_IS_NULL = "The name of the digest algorithm is null.";
    public static final String THERE_IS_NO_FIELD_IN_THE_DOCUMENT_WITH_SUCH_NAME = "There is no field in the document "
            + "with such name: {0}.";
    public static final String THIS_PKCS7_OBJECT_HAS_MULTIPLE_SIGNERINFOS_ONLY_ONE_IS_SUPPORTED_AT_THIS_TIME = "This "
            + "PKCS#7 object has multiple SignerInfos. Only one is supported at this time.";
    public static final String THIS_INSTANCE_OF_PDF_SIGNER_ALREADY_CLOSED = "This instance of PdfSigner has been "
            + "already closed.";
    public static final String THIS_TSA_FAILED_TO_RETURN_TIME_STAMP_TOKEN = "TSA {0} failed to return time stamp "
            + "token: {1}.";
    public static final String TOO_BIG_KEY = "The key is too big.";
    public static final String UNEXPECTED_CLOSE_BRACKET = "Unexpected close bracket.";
    public static final String UNEXPECTED_GT_GT = "unexpected >>.";
    public static final String UNKNOWN_HASH_ALGORITHM = "Unknown hash algorithm: {0}.";
    public static final String COULD_NOT_DETERMINE_SIGNATURE_MECHANISM_OID = "Could not determine OID for signature algorithm {0} with digest {1}.";
    public static final String VERIFICATION_ALREADY_OUTPUT = "Verification already output.";

    public static final String ALGO_REQUIRES_SPECIFIC_HASH = "{0} requires the document to be digested using {1}, not {2}";

    public static final String ONLY_MGF1_SUPPORTED_IN_RSASSA_PSS = "Only MGF1 is supported in RSASSA-PSS";
    public static final String RSASSA_PSS_DIGESTMISSMATCH = "Digest algorithm in RSASSA-PSS parameters is {0} while" +
            " ambientdigest algorithm is {1}";
    public static final String DISGEST_ALGORITM_MGF_MISMATCH = "Digest algorithm in MGF1 parameters is {0} while" +
            " ambient digest algorithm is {1}";
    public static final String INVALID_ARGUMENTS = "Invalid parameters provided.";

    private SignExceptionMessageConstant(){
        // Private constructor will prevent the instantiation of this class directly
    }
}
