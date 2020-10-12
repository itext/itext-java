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
package com.itextpdf.kernel;

import com.itextpdf.io.util.MessageFormatUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Exception class for exceptions in kernel module.
 */
public class PdfException extends RuntimeException {

    public static final String _1_IS_AN_UNKNOWN_GRAPHICS_STATE_DICTIONARY = "{0} is an unknown graphics state dictionary.";
    public static final String _1_IS_NOT_AN_ACCEPTABLE_VALUE_FOR_THE_FIELD_2 = "{0} is not an acceptable value for the field {1}.";
    public static final String _1_IS_NOT_A_VALID_PLACEABLE_WINDOWS_METAFILE = "{0} is not a valid placeable windows metafile.";

    public static final String ANNOTATION_SHALL_HAVE_REFERENCE_TO_PAGE = "Annotation shall have reference to page.";
    public static final String APPEND_MODE_REQUIRES_A_DOCUMENT_WITHOUT_ERRORS_EVEN_IF_RECOVERY_WAS_POSSIBLE = "Append mode requires a document without errors, even if recovery is possible.";
    public static final String AUTHENTICATED_ATTRIBUTE_IS_MISSING_THE_DIGEST = "Authenticated attribute is missing the digest.";
    public static final String AVAILABLE_SPACE_IS_NOT_ENOUGH_FOR_SIGNATURE = "Available space is not enough for signature.";
    public static final String BAD_CERTIFICATE_AND_KEY = "Bad public key certificate and/or private key.";
    public static final String BAD_USER_PASSWORD = "Bad user password. Password is not provided or wrong password provided. Correct password should be passed to PdfReader constructor with properties. See ReaderProperties#setPassword() method.";

    public static final String CANNOT_ADD_CELL_TO_COMPLETED_LARGE_TABLE = "The large table was completed. It's prohibited to use it anymore. Created different Table instance instead.";
    public static final String CANNOT_ADD_KID_TO_THE_FLUSHED_ELEMENT = "Cannot add kid to the flushed element.";
    @Deprecated
    public static final String CANNOT_ADD_NON_DICTIONARY_EXTGSTATE_TO_RESOURCES_1 = "Cannot add graphic state to resources. The PdfObject type is {0}, but should be PdfDictionary.";
    public static final String CANNOT_ADD_NON_DICTIONARY_PATTERN_TO_RESOURCES_1 = "Cannot add pattern to resources. The PdfObject type is {0}, but should be PdfDictionary or PdfStream.";
    public static final String CANNOT_ADD_NON_DICTIONARY_PROPERTIES_TO_RESOURCES_1 = "Cannot add properties to resources. The PdfObject type is {0}, but should be PdfDictionary.";
    public static final String CANNOT_ADD_NON_DICTIONARY_SHADING_TO_RESOURCES_1 = "Cannot add shading to resources. The PdfObject type is {0}, but should be PdfDictionary or PdfStream.";
    public static final String CANNOT_ADD_NON_STREAM_FORM_TO_RESOURCES_1 = "Cannot add form to resources. The PdfObject type is {0}, but should be PdfStream.";
    public static final String CANNOT_ADD_NON_STREAM_IMAGE_TO_RESOURCES_1 = "Cannot add image to resources. The PdfObject type is {0}, but should be PdfStream.";
    public static final String CANNOT_BE_EMBEDDED_DUE_TO_LICENSING_RESTRICTIONS = "{0} cannot be embedded due to licensing restrictions.";
    public static final String CANNOT_CLOSE_DOCUMENT = "Cannot close document.";
    public static final String CANNOT_CLOSE_DOCUMENT_WITH_ALREADY_FLUSHED_PDF_CATALOG = "Cannot close document with already flushed PDF Catalog.";
    public static final String CANNOT_CONVERT_PDF_ARRAY_TO_AN_ARRAY_OF_BOOLEANS = "Cannot convert PdfArray to an array of booleans";
    public static final String CANNOT_CONVERT_PDF_ARRAY_TO_DOUBLE_ARRAY = "Cannot convert PdfArray to an array of doubles.";
    public static final String CANNOT_CONVERT_PDF_ARRAY_TO_INT_ARRAY = "Cannot convert PdfArray to an array of integers.";
    public static final String CANNOT_CONVERT_PDF_ARRAY_TO_FLOAT_ARRAY = "Cannot convert PdfArray to an array of floats.";
    public static final String CANNOT_CONVERT_PDF_ARRAY_TO_LONG_ARRAY = "Cannot convert PdfArray to an array of longs.";
    public static final String CANNOT_CONVERT_PDF_ARRAY_TO_RECTANGLE = "Cannot convert PdfArray to Rectangle.";
    public static final String CANNOT_COPY_FLUSHED_OBJECT = "Cannot copy flushed object.";
    public static final String CANNOT_COPY_FLUSHED_TAG = "Cannot copy flushed tag.";
    public static final String CANNOT_COPY_OBJECT_CONTENT = "Cannot copy object content.";
    public static final String CANNOT_COPY_INDIRECT_OBJECT_FROM_THE_DOCUMENT_THAT_IS_BEING_WRITTEN = "Cannot copy indirect object from the document that is being written.";
    public static final String CANNOT_COPY_TO_DOCUMENT_OPENED_IN_READING_MODE = "Cannot copy to document opened in reading mode.";
    public static final String CANNOT_CREATE_FONT_FROM_NULL_PDF_DICTIONARY = "Cannot create font from null pdf dictionary.";
    public static final String CANNOT_CREATE_LAYOUT_IMAGE_BY_WMF_IMAGE = "Cannot create layout image by WmfImage instance. First convert the image into FormXObject and then use the corresponding layout image constructor.";
    public static final String CANNOT_CREATE_PDF_IMAGE_XOBJECT_BY_WMF_IMAGE = "Cannot create PdfImageXObject instance by WmfImage. Use PdfFormXObject constructor instead.";
    public static final String CANNOT_CREATE_PDFSTREAM_BY_INPUT_STREAM_WITHOUT_PDF_DOCUMENT = "Cannot create pdfstream by InputStream without PdfDocument.";
    public static final String CANNOT_CREATE_TYPE_0_FONT_WITH_TRUE_TYPE_FONT_PROGRAM_WITHOUT_EMBEDDING_IT = "Cannot create Type0 font with true type font program without embedding it.";
    public static final String CANNOT_DRAW_ELEMENTS_ON_ALREADY_FLUSHED_PAGES = "Cannot draw elements on already flushed pages.";
    public static final String CANNOT_EMBED_STANDARD_FONT = "Standard fonts cannot be embedded.";
    public static final String CANNOT_EMBED_TYPE_0_FONT_WITH_CID_FONT_PROGRAM = "Cannot embed Type0 font with CID font program based on non-generic predefined CMap.";
    public static final String CANNOT_GET_CONTENT_BYTES = "Cannot get content bytes.";
    public static final String CANNOT_GET_PDF_STREAM_BYTES = "Cannot get PdfStream bytes.";
    public static final String CANNOT_OPERATE_WITH_FLUSHED_PDF_STREAM = "Cannot operate with the flushed PdfStream.";
    public static final String CANNOT_RETRIEVE_MEDIA_BOX_ATTRIBUTE = "Invalid PDF. There is no media box attribute for page or its parents.";
    public static final String CANNOT_FIND_IMAGE_DATA_OR_EI = "Cannot find image data or EI.";
    public static final String CANNOT_FLUSH_DOCUMENT_ROOT_TAG_BEFORE_DOCUMENT_IS_CLOSED = "Cannot flush document root tag before document is closed.";
    public static final String CANNOT_FLUSH_OBJECT = "Cannot flush object.";
    public static final String CANNOT_MOVE_FLUSHED_TAG = "Cannot move flushed tag";
    public static final String CANNOT_MOVE_TO_FLUSHED_KID = "Cannot move to flushed kid.";
    public static final String CANNOT_MOVE_TO_MARKED_CONTENT_REFERENCE = "Cannot move to marked content reference.";
    public static final String CANNOT_MOVE_TO_PARENT_CURRENT_ELEMENT_IS_ROOT = "Cannot move to parent current element is root.";
    public static final String CANNOT_MOVE_PAGES_IN_PARTLY_FLUSHED_DOCUMENT = "Cannot move pages in partly flushed document. Page number {0} is already flushed.";
    public static final String CANNOT_OPEN_DOCUMENT = "Cannot open document.";
    public static final String CANNOT_PARSE_CONTENT_STREAM = "Cannot parse content stream.";
    public static final String CANNOT_READ_A_STREAM_IN_ORDER_TO_APPEND_NEW_BYTES = "Cannot read a stream in order to append new bytes.";
    public static final String CANNOT_READ_PDF_OBJECT = "Cannot read PdfObject.";
    public static final String CANNOT_RECOGNISE_DOCUMENT_FONT_WITH_ENCODING = "Cannot recognise document font {0} with {1} encoding";
    public static final String CANNOT_RELOCATE_ROOT_TAG = "Cannot relocate root tag.";
    public static final String CANNOT_RELOCATE_TAG_WHICH_IS_ALREADY_FLUSHED = "Cannot relocate tag which is already flushed.";
    public static final String CANNOT_RELOCATE_TAG_WHICH_PARENT_IS_ALREADY_FLUSHED = "Cannot relocate tag which parent is already flushed.";
    public static final String CANNOT_REMOVE_DOCUMENT_ROOT_TAG = "Cannot remove document root tag.";
    public static final String CANNOT_REMOVE_MARKED_CONTENT_REFERENCE_BECAUSE_ITS_PAGE_WAS_ALREADY_FLUSHED = "Cannot remove marked content reference, because its page has been already flushed.";
    public static final String CANNOT_REMOVE_TAG_BECAUSE_ITS_PARENT_IS_FLUSHED = "Cannot remove tag, because its parent is flushed.";
    @Deprecated
    public static final String CANNOT_SET_DATA_TO_PDFSTREAM_WHICH_WAS_CREATED_BY_INPUT_STREAM = "Cannot set data to PdfStream which was created by InputStream.";
    public static final String CANNOT_SET_DATA_TO_PDF_STREAM_WHICH_WAS_CREATED_BY_INPUT_STREAM = "Cannot set data to PdfStream which was created by InputStream.";
    public static final String CANNOT_SET_ENCRYPTED_PAYLOAD_TO_DOCUMENT_OPENED_IN_READING_MODE = "Cannot set encrypted payload to a document opened in read only mode.";
    public static final String CANNOT_SET_ENCRYPTED_PAYLOAD_TO_ENCRYPTED_DOCUMENT = "Cannot set encrypted payload to an encrypted document.";
    public static final String CANNOT_SPLIT_DOCUMENT_THAT_IS_BEING_WRITTEN = "Cannot split document that is being written.";
    public static final String CANNOT_WRITE_TO_PDF_STREAM = "Cannot write to PdfStream.";
    public static final String CANNOT_WRITE_OBJECT_AFTER_IT_WAS_RELEASED = "Cannot write object after it was released. In normal situation the object must be read once again before being written.";
    public static final String CANNOT_DECODE_PKCS7_SIGNEDDATA_OBJECT = "Cannot decode PKCS#7 SignedData object.";
    public static final String CANNOT_FIND_SIGNING_CERTIFICATE_WITH_SERIAL_1 = "Cannot find signing certificate with serial {0}.";
    public static final String CERTIFICATE_IS_NOT_PROVIDED_DOCUMENT_IS_ENCRYPTED_WITH_PUBLIC_KEY_CERTIFICATE = "Certificate is not provided. Document is encrypted with public key certificate, it should be passed to PdfReader constructor with properties. See ReaderProperties#setPublicKeySecurityParams() method.";
    public static final String CERTIFICATION_SIGNATURE_CREATION_FAILED_DOC_SHALL_NOT_CONTAIN_SIGS = "Certification signature creation failed. Document shall not contain any certification or approval signatures before signing with certification signature.";
    public static final String CF_NOT_FOUND_ENCRYPTION = "/CF not found (encryption)";
    public static final String CODABAR_MUST_HAVE_AT_LEAST_START_AND_STOP_CHARACTER = "Codabar must have at least start and stop character.";
    public static final String CODABAR_MUST_HAVE_ONE_ABCD_AS_START_STOP_CHARACTER = "Codabar must have one of 'ABCD' as start/stop character.";
    public static final String COLOR_SPACE_NOT_FOUND = "ColorSpace not found.";
    public static final String CONTENT_STREAM_MUST_NOT_INVOKE_OPERATORS_THAT_SPECIFY_COLORS_OR_OTHER_COLOR_RELATED_PARAMETERS = "Content stream must not invoke operators that specify colors or other color related parameters in the graphics state.";
    public static final String DATA_HANDLER_COUNTER_HAS_BEEN_DISABLED = "Data handler counter has been disabled";
    public static final String DECODE_PARAMETER_TYPE_1_IS_NOT_SUPPORTED = "Decode parameter type {0} is not supported.";
    public static final String DEFAULT_APPEARANCE_NOT_FOUND = "DefaultAppearance is required but not found";
    public static final String DEFAULT_CRYPT_FILTER_NOT_FOUND_ENCRYPTION = "/DefaultCryptFilter not found (encryption).";
    public static final String DICTIONARY_KEY_1_IS_NOT_A_NAME = "Dictionary key {0} is not a name.";
    public static final String DICTIONARY_DOES_NOT_HAVE_1_FONT_DATA = "Dictionary doesn't have {0} font data.";
    public static final String DICTIONARY_DOES_NOT_HAVE_SUPPORTED_FONT_DATA = "Dictionary doesn't have supported font data.";
    public static final String DOCUMENT_ALREADY_PRE_CLOSED = "Document has been already pre closed.";
    public static final String DOCUMENT_CLOSED_IT_IS_IMPOSSIBLE_TO_EXECUTE_ACTION = "Document was closed. It is impossible to execute action.";
    public static final String DOCUMENT_DOES_NOT_CONTAIN_STRUCT_TREE_ROOT = "Document doesn't contain StructTreeRoot.";
    public static final String DOCUMENT_HAS_NO_PAGES = "Document has no pages.";
    public static final String DOCUMENT_HAS_NO_PDF_CATALOG_OBJECT = "Document has no PDF Catalog object.";
    public static final String DOCUMENT_HAS_NOT_BEEN_READ_YET = "The PDF document has not been read yet. Document reading occurs in PdfDocument class constructor";
    public static final String DOCUMENT_MUST_BE_PRE_CLOSED = "Document must be preClosed.";
    public static final String DOCUMENT_FOR_COPY_TO_CANNOT_BE_NULL = "Document for copyTo cannot be null.";
    public static final String DURING_DECOMPRESSION_MULTIPLE_STREAMS_IN_SUM_OCCUPIED_MORE_MEMORY_THAN_ALLOWED = "During decompression multiple streams in sum occupied more memory than allowed. Please either check your pdf or increase the allowed single decompressed pdf stream maximum size value by setting the appropriate parameter of ReaderProperties's MemoryLimitsAwareHandler.";
    public static final String DURING_DECOMPRESSION_SINGLE_STREAM_OCCUPIED_MORE_MEMORY_THAN_ALLOWED = "During decompression a single stream occupied more memory than allowed. Please either check your pdf or increase the allowed multiple decompressed pdf streams maximum size value by setting the appropriate parameter of ReaderProperties's MemoryLimitsAwareHandler.";
    public static final String DURING_DECOMPRESSION_SINGLE_STREAM_OCCUPIED_MORE_THAN_MAX_INTEGER_VALUE = "During decompression a single stream occupied more than a maximum integer value. Please check your pdf.";
    public static final String END_OF_CONTENT_STREAM_REACHED_BEFORE_END_OF_IMAGE_DATA = "End of content stream reached before end of image data.";
    public static final String ERROR_WHILE_READING_OBJECT_STREAM = "Error while reading Object Stream.";
    public static final String ENCRYPTED_PAYLOAD_FILE_SPEC_DOES_NOT_HAVE_ENCRYPTED_PAYLOAD_DICTIONARY = "Encrypted payload file spec shall have encrypted payload dictionary.";
    public static final String ENCRYPTED_PAYLOAD_FILE_SPEC_SHALL_BE_INDIRECT = "Encrypted payload file spec shall be indirect.";
    public static final String ENCRYPTED_PAYLOAD_FILE_SPEC_SHALL_HAVE_EF_DICTIONARY = "Encrypted payload file spec shall have 'EF' key. The value of such key shall be a dictionary that contains embedded file stream.";
    public static final String ENCRYPTED_PAYLOAD_FILE_SPEC_SHALL_HAVE_TYPE_EQUAL_TO_FILESPEC = "Encrypted payload file spec shall have 'Type' key. The value of such key shall be 'Filespec'.";
    public static final String ENCRYPTED_PAYLOAD_SHALL_HAVE_TYPE_EQUALS_TO_ENCRYPTED_PAYLOAD_IF_PRESENT = "Encrypted payload dictionary shall have field 'Type' equal to 'EncryptedPayload' if present";
    public static final String ENCRYPTED_PAYLOAD_SHALL_HAVE_SUBTYPE = "Encrypted payload shall have 'Subtype' field specifying crypto filter";
    public static final String ExternalEntityElementFoundInXml =
            "External entity element found in XML. This entity will not be parsed to prevent XML attacks.";

    public static final String FAILED_TO_GET_TSA_RESPONSE_FROM_1 = "Failed to get TSA response from {0}.";
    public static final String FIELD_FLATTENING_IS_NOT_SUPPORTED_IN_APPEND_MODE = "Field flattening is not supported in append mode.";
    public static final String FIELD_ALREADY_SIGNED = "Field has been already signed.";

    public static final String FIELD_NAMES_CANNOT_CONTAIN_A_DOT = "Field names cannot contain a dot.";
    public static final String FIELD_TYPE_IS_NOT_A_SIGNATURE_FIELD_TYPE = "Field type is not a signature field type.";
    public static final String FILTER_1_IS_NOT_SUPPORTED = "Filter {0} is not supported.";
    public static final String FILE_POSITION_1_CROSS_REFERENCE_ENTRY_IN_THIS_XREF_SUBSECTION = "file position {0} cross reference entry in this xref subsection.";
    public static final String FILTER_CCITTFAXDECODE_IS_ONLY_SUPPORTED_FOR_IMAGES = "Filter CCITTFaxDecode is only supported for images";
    public static final String FILTER_IS_NOT_A_NAME_OR_ARRAY = "filter is not a name or array.";
    public static final String FLUSHED_PAGE_CANNOT_BE_ADDED_OR_INSERTED = "Flushed page cannot be added or inserted.";
    public static final String FLUSHED_PAGE_CANNOT_BE_REMOVED = "Flushed page cannot be removed from a document which is tagged or has an AcroForm";
    public static final String FLUSHING_HELPER_FLUSHING_MODE_IS_NOT_FOR_DOC_READING_MODE = "Flushing writes the object to the output stream and releases it from memory. It is only possible for documents that have a PdfWriter associated with them. Use PageFlushingHelper#releaseDeep method instead.";
    public static final String FONT_AND_SIZE_MUST_BE_SET_BEFORE_WRITING_ANY_TEXT = "Font and size must be set before writing any text.";
    public static final String FONT_EMBEDDING_ISSUE = "Font embedding issue.";
    public static final String FONT_PROVIDER_NOT_SET_FONT_FAMILY_NOT_RESOLVED = "FontProvider and FontSet are empty. Cannot resolve font family name (see ElementPropertyContainer#setFontFamily) without initialized FontProvider (see RootElement#setFontProvider).";
    @Deprecated
    public static final String FONT_SIZE_IS_TOO_SMALL = "Font size is too small.";
    public static final String FORM_XOBJECT_MUST_HAVE_BBOX = "Form XObject must have BBox.";
    public static final String FUNCTION_IS_NOT_COMPATIBLE_WITH_COLOR_SPACE = "Function is not compatible with ColorSpace.";
    @Deprecated
    public static final String GIVEN_ACCESSIBLE_ELEMENT_IS_NOT_CONNECTED_TO_ANY_TAG = "Given accessible element is not connected to any tag.";
    public static final String ILLEGAL_CHARACTER_IN_ASCIIHEXDECODE = "illegal character in ASCIIHexDecode.";
    public static final String ILLEGAL_CHARACTER_IN_ASCII85DECODE = "Illegal character in ASCII85Decode.";
    public static final String ILLEGAL_CHARACTER_IN_CODABAR_BARCODE = "Illegal character in Codabar Barcode.";
    public static final String ILLEGAL_LENGTH_VALUE = "Illegal length value.";
    public static final String ILLEGAL_R_VALUE = "Illegal R value.";
    public static final String ILLEGAL_V_VALUE = "Illegal V value.";
    public static final String IN_A_PAGE_LABEL_THE_PAGE_NUMBERS_MUST_BE_GREATER_OR_EQUAL_TO_1 = "In a page label the page numbers must be greater or equal to 1.";
    public static final String IN_CODABAR_START_STOP_CHARACTERS_ARE_ONLY_ALLOWED_AT_THE_EXTREMES = "In Codabar, start/stop characters are only allowed at the extremes.";
    public static final String INVALID_HTTP_RESPONSE_1 = "Invalid http response {0}.";
    public static final String INVALID_TSA_1_RESPONSE_CODE_2 = "Invalid TSA {0} response code {1}.";
    public static final String INCORRECT_NUMBER_OF_COMPONENTS = "Incorrect number of components.";
    public static final String INVALID_CODEWORD_SIZE = "Invalid codeword size.";
    public static final String INVALID_CROSS_REFERENCE_ENTRY_IN_THIS_XREF_SUBSECTION = "Invalid cross reference entry in this xref subsection.";
    public static final String INVALID_INDIRECT_REFERENCE_1 = "Invalid indirect reference {0}.";
    public static final String INVALID_MEDIA_BOX_VALUE = "Tne media box object has incorrect values.";
    public static final String INVALID_PAGE_STRUCTURE_1 = "Invalid page structure {0}.";
    public static final String INVALID_PAGE_STRUCTURE_PAGES_MUST_BE_PDF_DICTIONARY = "Invalid page structure. /Pages must be PdfDictionary.";
    public static final String INVALID_RANGE_ARRAY = "Invalid range array.";
    public static final String INVALID_OFFSET_FOR_OBJECT_1 = "Invalid offset for object {0}.";
    public static final String INVALID_XREF_STREAM = "Invalid xref stream.";
    public static final String INVALID_XREF_TABLE = "Invalid xref table.";
    public static final String IO_EXCEPTION = "I/O exception.";
    public static final String IO_EXCEPTION_WHILE_CREATING_FONT = "I/O exception while creating Font";
    public static final String LZW_DECODER_EXCEPTION = "LZW decoder exception.";
    public static final String LZW_FLAVOUR_NOT_SUPPORTED = "LZW flavour not supported.";
    public static final String MACRO_SEGMENT_ID_MUST_BE_GT_OR_EQ_ZERO = "macroSegmentId must be >= 0";
    public static final String MACRO_SEGMENT_ID_MUST_BE_GT_ZERO = "macroSegmentId must be > 0";
    public static final String MACRO_SEGMENT_ID_MUST_BE_LT_MACRO_SEGMENT_COUNT = "macroSegmentId must be < macroSemgentCount";
    public static final String MUST_BE_A_TAGGED_DOCUMENT = "Must be a tagged document.";
    public static final String NUMBER_OF_ENTRIES_IN_THIS_XREF_SUBSECTION_NOT_FOUND = "Number of entries in this xref subsection not found.";
    public static final String NO_COMPATIBLE_ENCRYPTION_FOUND = "No compatible encryption found.";
    public static final String NO_CRYPTO_DICTIONARY_DEFINED = "No crypto dictionary defined.";
    public static final String NO_GLYPHS_DEFINED_FOR_TYPE_3_FONT = "No glyphs defined for type3 font.";
    public static final String NO_KID_WITH_SUCH_ROLE = "No kid with such role.";
    /**
     * @deprecated Now we log a warning rather than throw an exception.
     */
    @Deprecated
    public static final String NO_MAX_LEN_PRESENT = "No /MaxLen has been set even though the Comb flag has been set.";
    public static final String NONINVERTIBLE_MATRIX_CANNOT_BE_PROCESSED = "A noninvertible matrix has been parsed. The behaviour is unpredictable.";
    public static final String NOT_A_PLACEABLE_WINDOWS_METAFILE = "Not a placeable windows metafile.";
    public static final String NOT_A_VALID_PKCS7_OBJECT_NOT_A_SEQUENCE = "Not a valid PKCS#7 object - not a sequence";
    public static final String NOT_A_VALID_PKCS7_OBJECT_NOT_SIGNED_DATA = "Not a valid PKCS#7 object - not signed data.";
    public static final String NOT_A_WMF_IMAGE = "Not a WMF image.";
    public static final String NO_VALID_ENCRYPTION_MODE = "No valid encryption mode.";
    public static final String NUMBER_OF_BOOLEANS_IN_THE_ARRAY_DOES_NOT_CORRESPOND_WITH_THE_NUMBER_OF_FIELDS = "The number of booleans in the array doesn't correspond with the number of fields.";
    public static final String OBJECT_MUST_BE_INDIRECT_TO_WORK_WITH_THIS_WRAPPER = "Object must be indirect to work with this wrapper.";
    public static final String OBJECT_NUMBER_OF_THE_FIRST_OBJECT_IN_THIS_XREF_SUBSECTION_NOT_FOUND = "Object number of the first object in this xref subsection not found.";
    public static final String ONLY_IDENTITY_CMAPS_SUPPORTS_WITH_TRUETYPE = "Only Identity CMaps supports with truetype";
    public static final String ONLY_BMP_CAN_BE_WRAPPED_IN_WMF = "Only BMP can be wrapped in WMF.";
    public static final String OPERATOR_EI_NOT_FOUND_AFTER_END_OF_IMAGE_DATA = "Operator EI not found after the end of image data.";
    public static final String PAGE_1_CANNOT_BE_ADDED_TO_DOCUMENT_2_BECAUSE_IT_BELONGS_TO_DOCUMENT_3 = "Page {0} cannot be added to document {1}, because it belongs to document {2}.";
    public static final String PAGE_IS_NOT_SET_FOR_THE_PDF_TAG_STRUCTURE = "Page is not set for the pdf tag structure.";
    public static final String PAGE_ALREADY_FLUSHED = "The page has been already flushed.";
    public static final String PAGE_ALREADY_FLUSHED_USE_ADD_FIELD_APPEARANCE_TO_PAGE_METHOD_BEFORE_PAGE_FLUSHING = "The page has been already flushed. Use PdfAcroForm#addFieldAppearanceToPage() method before page flushing.";
    public static final String PDF_ENCODINGS = "PdfEncodings exception.";
    public static final String PDF_ENCRYPTION = "PdfEncryption exception.";
    public static final String PDF_DECRYPTION = "Exception occurred with PDF document decryption. One of the possible reasons is wrong password or wrong public key certificate and private key.";
    public static final String PDF_DOCUMENT_MUST_BE_OPENED_IN_STAMPING_MODE = "PdfDocument must be opened in stamping mode.";
    public static final String PDF_FORM_XOBJECT_HAS_INVALID_BBOX = "PdfFormXObject has invalid BBox.";
    public static final String PDF_OBJECT_STREAM_REACH_MAX_SIZE = "PdfObjectStream reach max size.";
    public static final String PDF_PAGES_TREE_COULD_BE_GENERATED_ONLY_ONCE = "PdfPages tree could be generated only once.";
    public static final String PDF_READER_HAS_BEEN_ALREADY_UTILIZED = "Given PdfReader instance has already been utilized. The PdfReader cannot be reused, please create a new instance.";
    public static final String PDF_STARTXREF_IS_NOT_FOLLOWED_BY_A_NUMBER = "PDF startxref is not followed by a number.";
    public static final String PDF_STARTXREF_NOT_FOUND = "PDF startxref not found.";
    public static final String PDF_INDIRECT_OBJECT_BELONGS_TO_OTHER_PDF_DOCUMENT = "Pdf indirect object belongs to other PDF document. Copy object to current pdf document.";
    public static final String PDF_VERSION_NOT_VALID = "PDF version is not valid.";
    public static final String REF_ARRAY_ITEMS_IN_STRUCTURE_ELEMENT_DICTIONARY_SHALL_BE_INDIRECT_OBJECTS = "Ref array items in structure element dictionary shall be indirect objects.";
    public static final String REQUESTED_PAGE_NUMBER_IS_OUT_OF_BOUNDS = "Requested page number {0} is out of bounds.";
    public static final String PNG_FILTER_UNKNOWN = "PNG filter unknown.";
    public static final String PRINT_SCALING_ENFORCE_ENTRY_INVALID = "/PrintScaling shall may appear in the Enforce array only if the corresponding entry in the viewer preferences dictionary specifies a valid value other than AppDefault";
    public static final String RESOURCES_CANNOT_BE_NULL = "Resources cannot be null.";
    public static final String RESOURCES_DO_NOT_CONTAIN_EXTGSTATE_ENTRY_UNABLE_TO_PROCESS_OPERATOR_1 = "Resources do not contain ExtGState entry. Unable to process operator {0}.";
    public static final String ROLE_IS_NOT_MAPPED_TO_ANY_STANDARD_ROLE = "Role \"{0}\" is not mapped to any standard role.";
    public static final String ROLE_IN_NAMESPACE_IS_NOT_MAPPED_TO_ANY_STANDARD_ROLE = "Role \"{0}\" in namespace {1} is not mapped to any standard role.";
    public static final String SHADING_TYPE_NOT_FOUND = "Shading type not found.";
    public static final String SIGNATURE_WITH_NAME_1_IS_NOT_THE_LAST_IT_DOESNT_COVER_WHOLE_DOCUMENT = "Signature with name {0} is not the last. It doesn't cover the whole document.";
    public static final String STDCF_NOT_FOUND_ENCRYPTION = "/StdCF not found (encryption)";
    public static final String STRUCT_PARENT_INDEX_NOT_FOUND_IN_TAGGED_OBJECT = "StructParent index not found in tagged object.";
    public static final String STRUCTURE_ELEMENT_IN_STRUCTURE_DESTINATION_SHALL_BE_AN_INDIRECT_OBJECT = "Structure element referenced by a structure destination shall be an indirect object.";
    public static final String STRUCTURE_ELEMENT_SHALL_CONTAIN_PARENT_OBJECT = "StructureElement shall contain parent object.";
    public static final String STRUCTURE_ELEMENT_DICTIONARY_SHALL_BE_AN_INDIRECT_OBJECT_IN_ORDER_TO_HAVE_CHILDREN = "Structure element dictionary shall be an indirect object in order to have children.";
    public static final String TAG_CANNOT_BE_MOVED_TO_THE_ANOTHER_DOCUMENTS_TAG_STRUCTURE = "Tag cannot be moved to the another document's tag structure.";
    public static final String TAG_FROM_THE_EXISTING_TAG_STRUCTURE_IS_FLUSHED_CANNOT_ADD_COPIED_PAGE_TAGS = "Tag from the existing tag structure is flushed. Cannot add copied page tags.";
    public static final String TAG_STRUCTURE_COPYING_FAILED_IT_MIGHT_BE_CORRUPTED_IN_ONE_OF_THE_DOCUMENTS = "Tag structure copying failed: it might be corrupted in one of the documents.";
    public static final String TAG_STRUCTURE_FLUSHING_FAILED_IT_MIGHT_BE_CORRUPTED = "Tag structure flushing failed: it might be corrupted.";
    public static final String TAG_TREE_POINTER_IS_IN_INVALID_STATE_IT_POINTS_AT_FLUSHED_ELEMENT_USE_MOVE_TO_ROOT = "TagTreePointer is in invalid state: it points at flushed element. Use TagTreePointer#moveToRoot.";
    public static final String TAG_TREE_POINTER_IS_IN_INVALID_STATE_IT_POINTS_AT_REMOVED_ELEMENT_USE_MOVE_TO_ROOT = "TagTreePointer is in invalid state: it points at removed element use TagTreePointer#moveToRoot.";
    public static final String TEXT_CANNOT_BE_NULL = "Text cannot be null.";
    public static final String TEXT_IS_TOO_BIG = "Text is too big.";
    public static final String TEXT_MUST_BE_EVEN = "The text length must be even.";
    public static final String TWO_BARCODE_MUST_BE_EXTERNALLY = "The two barcodes must be composed externally.";
    public static final String THERE_ARE_ILLEGAL_CHARACTERS_FOR_BARCODE_128_IN_1 = "There are illegal characters for barcode 128 in {0}.";
    public static final String THERE_IS_NO_ASSOCIATE_PDF_WRITER_FOR_MAKING_INDIRECTS = "There is no associate PdfWriter for making indirects.";
    public static final String THERE_IS_NO_FIELD_IN_THE_DOCUMENT_WITH_SUCH_NAME_1 = "There is no field in the document with such name: {0}.";
    public static final String THIS_PKCS7_OBJECT_HAS_MULTIPLE_SIGNERINFOS_ONLY_ONE_IS_SUPPORTED_AT_THIS_TIME = "This PKCS#7 object has multiple SignerInfos. Only one is supported at this time.";
    public static final String THIS_INSTANCE_OF_PDF_SIGNER_ALREADY_CLOSED = "This instance of PdfSigner has been already closed.";
    public static final String TO_FLUSH_THIS_WRAPPER_UNDERLYING_OBJECT_MUST_BE_ADDED_TO_DOCUMENT = "To manually flush this wrapper, you have to ensure that the object behind this wrapper is added to the document, i.e. it has an indirect reference.";
    public static final String TSA_1_FAILED_TO_RETURN_TIME_STAMP_TOKEN_2 = "TSA {0} failed to return time stamp token: {1}.";
    public static final String TRAILER_NOT_FOUND = "Trailer not found.";
    public static final String TRAILER_PREV_ENTRY_POINTS_TO_ITS_OWN_CROSS_REFERENCE_SECTION = "Trailer prev entry points to its own cross reference section.";
    public static final String UNBALANCED_BEGIN_END_MARKED_CONTENT_OPERATORS = "Unbalanced begin/end marked content operators.";
    public static final String UNBALANCED_LAYER_OPERATORS = "Unbalanced layer operators.";
    public static final String UNBALANCED_SAVE_RESTORE_STATE_OPERATORS = "Unbalanced save restore state operators.";
    public static final String UNEXPECTED_CHARACTER_1_FOUND_AFTER_ID_IN_INLINE_IMAGE = "Unexpected character {0} found after ID in inline image.";
    public static final String UNEXPECTED_CLOSE_BRACKET = "Unexpected close bracket.";
    public static final String UNEXPECTED_COLOR_SPACE_1 = "Unexpected ColorSpace: {0}.";
    public static final String UNEXPECTED_END_OF_FILE = "Unexpected end of file.";
    public static final String UNEXPECTED_GT_GT = "unexpected >>.";
    public static final String UNEXPECTED_SHADING_TYPE = "Unexpected shading type.";
    public static final String UNKNOWN_ENCRYPTION_TYPE_R_EQ_1 = "Unknown encryption type R == {0}.";
    public static final String UNKNOWN_ENCRYPTION_TYPE_V_EQ_1 = "Unknown encryption type V == {0}.";
    public static final String UNKNOWN_PDF_EXCEPTION = "Unknown PdfException.";
    public static final String UNKNOWN_HASH_ALGORITHM_1 = "Unknown hash algorithm: {0}.";
    public static final String UNKNOWN_KEY_ALGORITHM_1 = "Unknown key algorithm: {0}.";
    @Deprecated
    public static final String UNSUPPORTED_DEFAULT_COLOR_SPACE_NAME_1 = "Unsupported default color space name. Was {0}, but should be DefaultCMYK, DefaultGray or DefaultRGB";
    public static final String UNSUPPORTED_FONT_EMBEDDING_STRATEGY = "Unsupported font embedding strategy.";
    public static final String UNSUPPORTED_XOBJECT_TYPE = "Unsupported XObject type.";
    public static final String VERIFICATION_ALREADY_OUTPUT = "Verification already output.";
    public static final String WHEN_ADDING_OBJECT_REFERENCE_TO_THE_TAG_TREE_IT_MUST_BE_CONNECTED_TO_NOT_FLUSHED_OBJECT = "When adding object reference to the tag tree, it must be connected to not flushed object.";
    public static final String WHITE_POINT_IS_INCORRECTLY_SPECIFIED = "White point is incorrectly specified.";
    public static final String WMF_IMAGE_EXCEPTION = "WMF image exception.";
    public static final String WRONG_FORM_FIELD_ADD_ANNOTATION_TO_THE_FIELD = "Wrong form field. Add annotation to the field.";
    /**
     * @deprecated in favour of more informative named constant
     */
    @Deprecated
    public static final String WRONG_MEDIA_BOX_SIZE_1 = "Wrong media box size: {0}.";
    public static final String WRONGMEDIABOXSIZETOOFEWARGUMENTS = "Wrong media box size: {0}. Need at least 4 arguments";
    public static final String XREF_SUBSECTION_NOT_FOUND = "xref subsection not found.";
    public static final String YOU_HAVE_TO_DEFINE_A_BOOLEAN_ARRAY_FOR_THIS_COLLECTION_SORT_DICTIONARY = "You have to define a boolean array for this collection sort dictionary.";
    public static final String YOU_MUST_SET_A_VALUE_BEFORE_ADDING_A_PREFIX = "You must set a value before adding a prefix.";
    public static final String YOU_NEED_A_SINGLE_BOOLEAN_FOR_THIS_COLLECTION_SORT_DICTIONARY = "You need a single boolean for this collection sort dictionary.";
    public static final String QUAD_POINT_ARRAY_LENGTH_IS_NOT_A_MULTIPLE_OF_EIGHT = "The QuadPoint Array length is not a multiple of 8.";

    public static final String CORRUPTED_OUTLINE_NO_PARENT_ENTRY =
            "Document outline is corrupted: some outline (PDF object: \"{0}\") lacks the required parent entry.";
    public static final String CORRUPTED_OUTLINE_NO_TITLE_ENTRY =
            "Document outline is corrupted: some outline (PDF object: \"{0}\") lacks the required title entry.";

    /**
     * Object for more details
     */
    protected Object object;

    private List<Object> messageParams;

    /**
     * Creates a new instance of PdfException.
     *
     * @param message the detail message.
     */
    public PdfException(String message) {
        super(message);
    }

    /**
     * Creates a new instance of PdfException.
     *
     * @param cause the cause (which is saved for later retrieval by {@link #getCause()} method).
     */
    public PdfException(Throwable cause) {
        this(UNKNOWN_PDF_EXCEPTION, cause);
    }

    /**
     * Creates a new instance of PdfException.
     *
     * @param message the detail message.
     * @param obj     an object for more details.
     */
    public PdfException(String message, Object obj) {
        this(message);
        this.object = obj;
    }

    /**
     * Creates a new instance of PdfException.
     *
     * @param message the detail message.
     * @param cause   the cause (which is saved for later retrieval by {@link #getCause()} method).
     */
    public PdfException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates a new instance of PdfException.
     *
     * @param message the detail message.
     * @param cause   the cause (which is saved for later retrieval by {@link #getCause()} method).
     * @param obj     an object for more details.
     */
    public PdfException(String message, Throwable cause, Object obj) {
        this(message, cause);
        this.object = obj;
    }

    @Override
    public String getMessage() {
        if (messageParams == null || messageParams.size() == 0) {
            return super.getMessage();
        } else {
            return MessageFormatUtil.format(super.getMessage(), getMessageParams());
        }
    }

    /**
     * Sets additional params for Exception message.
     *
     * @param messageParams additional params.
     * @return object itself.
     */
    public PdfException setMessageParams(Object... messageParams) {
        this.messageParams = new ArrayList<>();
        Collections.addAll(this.messageParams, messageParams);
        return this;
    }

    /**
     * Gets additional params for Exception message.
     * @return array of additional params
     */
    protected Object[] getMessageParams() {
        Object[] parameters = new Object[messageParams.size()];
        for (int i = 0; i < messageParams.size(); i++) {
            parameters[i] = messageParams.get(i);
        }
        return parameters;
    }
}
