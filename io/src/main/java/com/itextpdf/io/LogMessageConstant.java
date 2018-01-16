/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2017 iText Group NV
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
package com.itextpdf.io;

/**
 * Class containing constants to be used in logging.
 */
public final class LogMessageConstant {

    public static final String ACTION_WAS_SET_TO_LINK_ANNOTATION_WITH_DESTINATION = "Action was set for a link annotation containing destination. The old destination will be cleared.";
    public static final String ALREADY_FLUSHED_INDIRECT_OBJECT_MADE_FREE = "An attempt is made to free already flushed indirect object reference. Indirect reference wasn't freed.";
    public static final String ATTEMPT_TO_MOVE_TO_FLUSHED_PARENT = "An attempt is made to move the tag tree pointer to the tag parent which has been already flushed. Tag tree pointer is moved to the root tag instead.";
    public static final String CALCULATE_HASHCODE_FOR_MODIFIED_PDFNUMBER = "Calculate hashcode for modified PdfNumber.";
    public static final String CLIP_ELEMENT = "Element content was clipped because some height properties are set.";
    public static final String COLOR_ALPHA_CHANNEL_IS_IGNORED = "Alpha channel {0} was ignored during color creation. Note that opacity can be achieved in some places by using 'setOpacity' method or 'TransparentColor' class";
    public static final String COLORANT_INTENSITIES_INVALID = "Some of colorant intensities are invalid: they are bigger than 1 or less than 0. We will force them to become 1 or 0 respectively.";
    public static final String COLOR_NOT_FOUND = "Color \"{0}\" not found.";
    public static final String COLOR_NOT_PARSED = "Color \"{0}\" was not parsed. It has invalid value. Defaulting to black color.";
    public static final String COULD_NOT_FIND_GLYPH_WITH_CODE = "Could not find glyph with the following code: {0}";
    public static final String DESTINATION_NOT_PERMITTED_WHEN_ACTION_IS_SET = "Destinations are not permitted for link annotations that already have actions. The old action will be removed.";
    public static final String DIRECTONLY_OBJECT_CANNOT_BE_INDIRECT = "DirectOnly object cannot be indirect";
    public static final String DOCFONT_HAS_ILLEGAL_DIFFERENCES = "Document Font has illegal differences array. Entry {0} references a glyph ID over 255 and will be ignored.";
    public static final String DOCUMENT_ALREADY_HAS_FIELD = "The document already has field {0}. Annotations of the fields with this name will be added to the existing one as children. If you want to have separate fields, please, rename them manually before copying.";
    public static final String DOCUMENT_SERIALIZATION_EXCEPTION_RAISED = "Unhandled exception while serialization";
    public static final String ELEMENT_DOES_NOT_FIT_AREA = "Element does not fit current area. {0}";
    public static final String ELEMENT_WAS_FORCE_PLACED_KEEP_WITH_NEXT_WILL_BE_IGNORED = "Element was placed in a forced way. Keep with next property will be ignored";
    public static final String ENCOUNTERED_INVALID_MCR = "Corrupted tag structure: encountered invalid marked content reference - it doesn't refer to any page or any mcid. This content reference will be ignored.";
    public static final String EXCEPTION_WHILE_CREATING_DEFAULT_FONT = "Exception while creating default font (Helvetica, WinAnsi)";
    public static final String EXCEPTION_WHILE_UPDATING_XMPMETADATA = "Exception while updating XmpMetadata";
    public static final String FILE_CHANNEL_CLOSING_FAILED = "Closing of the file channel this source is based on failed.";
    public static final String FLUSHED_OBJECT_CONTAINS_REFERENCE_WHICH_NOT_REFER_TO_ANY_OBJECT = "Flushed object contains indirect reference which doesn't refer to any other object (e.g. this reference might be a free reference). Null object will be written instead.";
    public static final String FONT_HAS_INVALID_GLYPH = "Font {0} has invalid glyph: {1}";
    public static final String FONT_PROPERTY_MUST_BE_PDF_FONT_OBJECT = "The Font Property must be a PdfFont object";
    public static final String FONT_SUBSET_ISSUE = "Font subset issue. Full font will be embedded.";
    public static final String FORBID_RELEASE_IS_SET = "ForbidRelease flag is set and release is called. Releasing will not be performed.";
    public static final String FORM_FIELD_WAS_FLUSHED = "A form field was flushed. There's no way to create this field in the AcroForm dictionary.";
    public static final String GRAPHICS_STATE_WAS_DELETED = "Graphics state is always deleted after event dispatching. If you want to preserve it in renderer info, use preserveGraphicsState method after receiving renderer info.";
    public static final String IMAGE_HAS_AMBIGUOUS_SCALE = "The image cannot be auto scaled and scaled by a certain parameter simultaneously";
    public static final String IMAGE_HAS_JBIG2DECODE_FILTER = "Image cannot be inline if it has JBIG2Decode filter. It will be added as an ImageXObject";
    public static final String IMAGE_HAS_JPXDECODE_FILTER = "Image cannot be inline if it has JPXDecode filter. It will be added as an ImageXObject";
    public static final String IMAGE_HAS_MASK = "Image cannot be inline if it has a Mask";
    public static final String IMAGE_SIZE_CANNOT_BE_MORE_4KB = "Inline image size cannot be more than 4KB. It will be added as an ImageXObject";
    public static final String INCORRECT_PAGEROTATION = "Encounterd a page rotation that was not a multiple of 90°/ (Pi/2) when generating default appearances for form fields";
    public static final String INDIRECT_REFERENCE_USED_IN_FLUSHED_OBJECT_MADE_FREE = "An attempt is made to free an indirect reference which was already used in the flushed object. Indirect reference wasn't freed.";
    public static final String INLINE_BLOCK_ELEMENT_WILL_BE_CLIPPED = "Inline block element does not fit into parent element and will be clipped";
    public static final String INPUT_STREAM_CONTENT_IS_LOST_ON_PDFSTREAM_SERIALIZATION = "PdfStream contains not null input stream. It's content will be lost in serialized object.";
    public static final String INVALID_INDIRECT_REFERENCE = "Invalid indirect reference {0} {1} R";
    public static final String INVALID_KEY_VALUE_KEY_0_HAS_NULL_VALUE = "Invalid key value: key {0} has null value.";
    public static final String LAST_ROW_IS_NOT_COMPLETE = "Last row is not completed. Table bottom border may collapse as you do not expect it";
    public static final String MAKE_COPY_OF_CATALOG_DICTIONARY_IS_FORBIDDEN = "Make copy of Catalog dictionary is forbidden.";
    public static final String METHOD_IS_NOT_IMPLEMENTED_BY_DEFAULT_OTHER_METHOD_WILL_BE_USED = "Method {0} is not implemented by default: please, override and implement it. {1} will be used instead.";
    public static final String NAME_ALREADY_EXISTS_IN_THE_NAME_TREE = "Name \"{0}\" already exists in the name tree; old value will be replaced by the new one.";
    public static final String NOT_TAGGED_PAGES_IN_TAGGED_DOCUMENT = "Not tagged pages are copied to the tagged document. Destination document now may contain not tagged content.";
    public static final String NO_FIELDS_IN_ACROFORM = "Required AcroForm entry /Fields does not exist in the document. Empty array /Fields will be created.";
    public static final String NUM_TREE_SHALL_NOT_END_WITH_KEY = "Number tree ends with a key which is invalid according to the PDF specification.";
    public static final String OCCUPIED_AREA_HAS_NOT_BEEN_INITIALIZED = "Occupied area has not been initialized. {0}";
    public static final String OCSP_STATUS_IS_REVOKED = "OCSP status is revoked.";
    public static final String OCSP_STATUS_IS_UNKNOWN = "OCSP status is unknown.";
    public static final String ONE_OF_GROUPED_SOURCES_CLOSING_FAILED = "Closing of one of the grouped sources failed.";
    public static final String ONLY_ONE_OF_ARTBOX_OR_TRIMBOX_CAN_EXIST_IN_THE_PAGE = "Only one of artbox or trimbox can exist on the page. The trimbox will be deleted";
    public static final String OPENTYPE_GDEF_TABLE_ERROR = "OpenType GDEF table error: {0}";
    public static final String PDF_OBJECT_FLUSHING_NOT_PERFORMED = "PdfObject flushing is not performed: PdfDocument is opened in append mode and the object is not marked as modified ( see PdfObject#setModified() ).";
    public static final String PDF_READER_CLOSING_FAILED = "PdfReader closing failed due to the error occurred!";
    public static final String PDF_WRITER_CLOSING_FAILED = "PdfWriter closing failed due to the error occurred!";
    public static final String POPUP_ENTRY_IS_NOT_POPUP_ANNOTATION = "Popup entry in the markup annotations refers not to the annotation with Popup subtype.";
    public static final String PROPERTY_IN_PERCENTS_NOT_SUPPORTED = "Property {0} in percents is not supported";
    public static final String RECTANGLE_HAS_NEGATIVE_OR_ZERO_SIZES = "The {0} rectangle has negative or zero sizes. It will not be displayed.";
    public static final String RECTANGLE_HAS_NEGATIVE_SIZE = "The {0} rectangle has negative size. It will not be displayed.";
    public static final String REGISTERING_DIRECTORY = "Registering directory";
    public static final String REMOVING_PAGE_HAS_ALREADY_BEEN_FLUSHED = "The removing page has already been flushed.";
    public static final String RENDERER_WAS_NOT_ABLE_TO_PROCESS_KEEP_WITH_NEXT = "The renderer was not able to process keep with next property properly";
    public static final String ROTATION_WAS_NOT_CORRECTLY_PROCESSED_FOR_RENDERER = "Rotation was not correctly processed for {0}";
    public static final String SOURCE_DOCUMENT_HAS_ACROFORM_DICTIONARY = "Source document has AcroForm dictionary. The pages you are going to copy may have FormFields, but they will not be copied, because you have not used any IPdfPageExtraCopier";
    public static final String START_MARKER_MISSING_IN_PFB_FILE = "Start marker is missing in the pfb file";
    public static final String SUM_OF_TABLE_COLUMNS_IS_GREATER_THAN_100 = "Sum of table columns is greater than 100%.";
    public static final String TABLE_WIDTH_IS_MORE_THAN_EXPECTED_DUE_TO_MIN_WIDTH = "Table width is more than expected due to min width of cell(s).";
    public static final String TAG_STRUCTURE_CONTEXT_WILL_BE_REINITIALIZED_ON_SERIALIZATION = "Tag structure context is not null and will be reinitialized in the copy of document. The copy may lose some data";
    public static final String TAG_STRUCTURE_INIT_FAILED = "Tag structure initialization failed, tag structure is ignored, it might be corrupted.";
    public static final String TOUNICODE_CMAP_MORE_THAN_2_BYTES_NOT_SUPPORTED = "ToUnicode CMap more than 2 bytes not supported.";
    public static final String UNEXPECTED_BEHAVIOUR_DURING_TABLE_ROW_COLLAPSING = "Unexpected behaviour during table row collapsing. Calculated rowspan was less then 1.";
    public static final String UNKNOWN_CMAP = "Unknown CMap {0}";
    public static final String UNKNOWN_COLOR_FORMAT_MUST_BE_RGB_OR_RRGGBB = "Unknown color format: must be rgb or rrggbb.";
    public static final String UNKNOWN_ERROR_WHILE_PROCESSING_CMAP = "Unknown error while processing CMap.";
    public static final String WRITER_ENCRYPTION_IS_IGNORED_APPEND = "Writer encryption will be ignored, because append mode is used. Document will preserve the original encryption (or will stay unencrypted)";
    public static final String WRITER_ENCRYPTION_IS_IGNORED_PRESERVE = "Writer encryption will be ignored, because preservation of encryption is enabled. Document will preserve the original encryption (or will stay unencrypted)";
    public static final String XREF_ERROR = "Error occurred while reading cross reference table. Cross reference table will be rebuilt.";
}
