/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
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
package com.itextpdf.pdfa;

import com.itextpdf.kernel.PdfException;

/**
 * Exception that is thrown when the PDF Document doesn't adhere to the PDF/A specification.
 */
public class PdfAConformanceException extends PdfException {

    private static final long serialVersionUID = -5951503441486657717L;

    public static final String _0_ACTIONS_ARE_NOT_ALLOWED = "{0} actions are not allowed";
    public static final String A_CATALOG_DICTIONARY_SHALL_CONTAIN_METADATA_ENTRY = "A catalog dictionary shall contain metadata entry";
    public static final String A_CATALOG_DICTIONARY_SHALL_NOT_CONTAIN_AA_ENTRY = "A catalog dictionary shall not contain aa entry";
    public static final String A_CATALOG_DICTIONARY_SHALL_NOT_CONTAIN_ALTERNATEPRESENTATIONS_NAMES_ENTRY = "A catalog dictionary shall not contain alternatepresentations names entry";
    public static final String A_CATALOG_DICTIONARY_SHALL_NOT_CONTAIN_OCPROPERTIES_KEY = "A catalog dictionary shall not contain the ocproperties key";
    public static final String A_CATALOG_DICTIONARY_SHALL_NOT_CONTAIN_REQUIREMENTS_ENTRY = "A catalog dictionary shall not contain a requirements entry";
    public static final String A_CATALOG_SHALL_INCLUDE_MARK_INFO_DICTIONARY_WITH_MARKED_TRUE_VALUE = "A catalog dictionary shall include a markinfo dictionary whose entry marked shall have a value of true";
    public static final String A_FORM_XOBJECT_DICTIONARY_SHALL_NOT_CONTAIN_OPI_KEY = "A form xobject dictionary shall not contain opi key";
    public static final String A_FORM_XOBJECT_DICTIONARY_SHALL_NOT_CONTAIN_PS_KEY = "A form xobject dictionary shall not contain PS key";
    public static final String A_FORM_XOBJECT_DICTIONARY_SHALL_NOT_CONTAIN_SUBTYPE2_KEY_WITH_A_VALUE_OF_PS = "A form xobject dictionary shall not contain subtype2 key with a value of PS";
    public static final String A_GROUP_OBJECT_WITH_AN_S_KEY_WITH_A_VALUE_OF_TRANSPARENCY_SHALL_NOT_BE_INCLUDED_IN_A_FORM_XOBJECT = "A group object with an s key with a value of transparency shall not be included in a form xobject";
    public static final String A_GROUP_OBJECT_WITH_AN_S_KEY_WITH_A_VALUE_OF_TRANSPARENCY_SHALL_NOT_BE_INCLUDED_IN_A_PAGE_XOBJECT = "A group object with an s key with a value of transparency shall not be included in a page xobject";
    public static final String A_NAME_DICTIONARY_SHALL_NOT_CONTAIN_THE_EMBEDDED_FILES_KEY = "A name dictionary shall not contain the EmbeddedFiles key";
    public static final String ALL_COLOUR_CHANNELS_IN_THE_JPEG2000_DATA_SHALL_HAVE_THE_SAME_BIT_DEPTH = "All colour channels in the jpeg2000 data shall have the same bit-depth";
    public static final String ALL_HALFTONES_SHALL_HAVE_HALFTONETYPE_1_OR_5 = "All halftones shall have halftonetype 1 or 5";
    public static final String ALL_NON_SYMBOLIC_TRUE_TYPE_FONT_SHALL_SPECIFY_MAC_ROMAN_ENCODING_OR_WIN_ANSI_ENCODING = "All non-symbolic TrueType fonts shall specify MacRomanEncoding or WinAnsiEncoding as the value of the Encoding entry in the font dictionary ";
    public static final String ALL_NON_SYMBOLIC_TRUE_TYPE_FONT_SHALL_SPECIFY_MAC_ROMAN_OR_WIN_ANSI_ENCODING_AS_THE_ENCODING_ENTRY = "All non-symbolic TrueType fonts shall specify MacRomanEncoding or WinAnsiEncoding as the value of the Encoding entry in the font dictionary  This also means that Encoding entry in the font dictionary shall not be an encoding dictionary ";
    public static final String ALL_SYMBOLIC_TRUE_TYPE_FONTS_SHALL_NOT_SPECIFY_ENCODING = "All symbolic TrueType fonts shall not specify an Encoding entry in the font dictionary ";
    public static final String ALL_THE_FONTS_MUST_BE_EMBEDDED_THIS_ONE_IS_NOT_0 = "All the fonts must be embedded. This one is not: {0}";
    public static final String AN_ANNOTATION_DICTIONARY_SHALL_CONTAIN_THE_F_KEY = "An annotation dictionary shall contain the f key";
    public static final String AN_ANNOTATION_DICTIONARY_SHALL_NOT_CONTAIN_AA_KEY = "An annotation dictionary shall not contain aa key";
    public static final String AN_ANNOTATION_DICTIONARY_SHALL_NOT_CONTAIN_THE_CA_KEY_WITH_A_VALUE_OTHER_THAN_1 = "An annotation dictionary shall not contain the ca key with a value other than 1";
    public static final String AN_EXTGSTATE_DICTIONARY_SHALL_NOT_CONTAIN_THE_HTP_KEY = "An extgstate dictionary shall not contain the HTP key";
    public static final String AN_EXTGSTATE_DICTIONARY_SHALL_NOT_CONTAIN_THE_TR_2_KEY_WITH_A_VALUE_OTHER_THAN_DEFAULT = "An extgstate dictionary shall not contain the TR2 key with a value other than default";
    public static final String AN_EXTGSTATE_DICTIONARY_SHALL_NOT_CONTAIN_THE_TR_KEY = "An extgstate dictionary shall not contain the tr key";
    public static final String AN_IMAGE_DICTIONARY_SHALL_NOT_CONTAIN_ALTERNATES_KEY = "An image dictionary shall not contain alternates key";
    public static final String AN_IMAGE_DICTIONARY_SHALL_NOT_CONTAIN_OPI_KEY = "An image dictionary shall not contain opi key";
    public static final String ANNOTATION_OF_TYPE_0_SHOULD_HAVE_CONTENTS_KEY = "Annotation of type {0} should have contents key";
    public static final String ANNOTATION_TYPE_0_IS_NOT_PERMITTED = "Annotation type {0} is not permitted";
    public static final String APPEARANCE_DICTIONARY_OF_WIDGET_SUBTYPE_AND_BTN_FIELD_TYPE_SHALL_CONTAIN_ONLY_THE_N_KEY_WITH_DICTIONARY_VALUE = "Appearance dictionary of widget subtype and btn field type shall contain only the n key with dictionary value";
    public static final String APPEARANCE_DICTIONARY_SHALL_CONTAIN_ONLY_THE_N_KEY_WITH_STREAM_VALUE = "Appearance dictionary shall contain only the n key with stream value";
    public static final String BLEND_MODE_SHALL_HAVE_VALUE_NORMAL_OR_COMPATIBLE = "Blend mode shall have value normal or compatible";
    public static final String COLOR_SPACE_0_SHALL_BE_DEVICE_INDEPENDENT = "Color space {0} shall be device independent";
    public static final String COLOR_SPACE_0_SHALL_HAVE_1_COMPONENTS = "Color space {0} shall have {1} components";
    public static final String CRYPT_FILTER_IS_NOT_PERMITTED_INLINE_IMAGE = "Crypt filter is not permitted inline image";
    public static final String DEPRECATED_SETSTATE_AND_NOOP_ACTIONS_ARE_NOT_ALLOWED = "Deprecated setstate and noop actions are not allowed";
    public static final String DESTOUTPUTPROFILE_IN_THE_PDFA1_OUTPUTINTENT_DICTIONARY_SHALL_BE_RGB = "Destoutputprofile in the pdfa1 outputintent dictionary shall be rgb";
    public static final String DEVICECMYK_MAY_BE_USED_ONLY_IF_THE_FILE_HAS_A_CMYK_PDFA_OUTPUT_INTENT = "Devicecmyk may be used only if the file has a cmyk pdfa outputIntent";
    public static final String DEVICECMYK_MAY_BE_USED_ONLY_IF_THE_FILE_HAS_A_CMYK_PDFA_OUTPUT_INTENT_OR_DEFAULTCMYK_IN_USAGE_CONTEXT = "Devicecmyk may be used only if the file has a cmyk pdfa outputIntent or defaultcmyk in usage context";
    public static final String DEVICERGB_AND_DEVICECMYK_COLORSPACES_CANNOT_BE_USED_BOTH_IN_ONE_FILE = "Devicergb and devicecmyk colorspaces cannot be used both in one file";
    public static final String DEVICERGB_MAY_BE_USED_ONLY_IF_THE_FILE_HAS_A_RGB_PDFA_OUTPUT_INTENT = "Devicergb may be used only if the file has a rgb pdfa outputIntent";
    public static final String DEVICERGB_MAY_BE_USED_ONLY_IF_THE_FILE_HAS_A_RGB_PDFA_OUTPUT_INTENT_OR_DEFAULTRGB_IN_USAGE_CONTEXT = "Devicergb may be used only if the file has a rgb pdfa outputIntent or defaultrgb in usage context";
    public static final String DOCUMENT_TO_READ_FROM_SHALL_BE_A_PDFA_CONFORMANT_FILE_WITH_VALID_XMP_METADATA = "Document to read from shall be a pdfa conformant file with valid xmp metadata";
    public static final String EF_KEY_OF_FILE_SPECIFICATION_DICTIONARY_SHALL_CONTAIN_DICTIONARY_WITH_VALID_F_KEY = "Ef key of file specification dictionary shall contain dictionary with valid f key";
    public static final String EMBEDDED_FILE_SHALL_CONTAIN_PARAMS_KEY_WITH_DICTIONARY_AS_VALUE = "Embedded file shall contain params key with dictionary as value";
    public static final String EMBEDDED_FILE_SHALL_CONTAIN_PARAMS_KEY_WITH_VALID_MODDATE_KEY = "Embedded file shall contain params key with valid moddate key";
    public static final String KEYWORD_ENCRYPT_SHALL_NOT_BE_USED_IN_THE_TRAILER_DICTIONARY = "Keyword encrypt shall not be used in the trailer dictionary";
    public static final String EVERY_ANNOTATION_SHALL_HAVE_AT_LEAST_ONE_APPEARANCE_DICTIONARY = "Every annotation shall have at least one appearance dictionary";
    public static final String EXACTLY_ONE_COLOUR_SPACE_SPECIFICATION_SHALL_HAVE_THE_VALUE_0X01_IN_THE_APPROX_FIELD = "Exactly one colour space specification shall have the value 0x01 in the approx field";
    public static final String FILE_SPECIFICATION_DICTIONARY_SHALL_CONTAIN_F_KEY_AND_UF_KEY = "File specification dictionary shall contain f key and uf key";
    public static final String FILE_SPECIFICATION_DICTIONARY_SHALL_CONTAIN_ONE_OF_THE_PREDEFINED_AFRELATIONSHIP_KEYS = "File specification dictionary shall contain one of the predefined afrelationship keys";
    public static final String FILE_SPECIFICATION_DICTIONARY_SHALL_NOT_CONTAIN_THE_EF_KEY = "File specification dictionary shall not contain the EF key";
    public static final String GRAPHICS_STATE_STACK_DEPTH_IS_GREATER_THAN_28 = "Graphics state stack depth is greater than 28";
    public static final String HALFTONES_SHALL_NOT_CONTAIN_HALFTONENAME = "Halftones shall not contain halftonename";
    public static final String IF_DEVICE_RGB_CMYK_GRAY_USED_IN_FILE_THAT_FILE_SHALL_CONTAIN_PDFA_OUTPUTINTENT = "If device rgb cmyk gray used in file, that file shall contain pdfa outputintent";
    public static final String IF_DEVICE_RGB_CMYK_GRAY_USED_IN_FILE_THAT_FILE_SHALL_CONTAIN_PDFA_OUTPUTINTENT_OR_DEFAULT_RGB_CMYK_GRAY_IN_USAGE_CONTEXT = "If device rgb cmyk gray used in file that file shall contain pdfa outputintent orDefaultRgb Cmyk Gray in usage context";
    public static final String IF_OUTPUTINTENTS_ARRAY_HAS_MORE_THAN_ONE_ENTRY_WITH_DESTOUTPUTPROFILE_KEY_THE_SAME_INDIRECT_OBJECT_SHALL_BE_USED_AS_THE_VALUE_OF_THAT_OBJECT = "If outputintents array has more than one entry with destoutputprofile key the same indirect object shall be used as the value of that object";
    public static final String IF_SPECIFIED_RENDERING_SHALL_BE_ONE_OF_THE_FOLLOWING_RELATIVECOLORIMETRIC_ABSOLUTECOLORIMETRIC_PERCEPTUAL_OR_SATURATION = "If specified rendering shall be one of the following relativecolorimetric absolutecolorimetric perceptual or saturation";
    public static final String THE_DOCUMENT_DOES_NOT_CONTAIN_A_PDFA_OUTPUTINTENT_BUT_PAGE_CONTAINS_TRANSPARENCY_AND_DOES_NOT_CONTAIN_BLENDING_COLOR_SPACE = "If the document does not contain a OutputIntent, then page with transparency shall include the dictionary with Group key that include a CS with blending colour space";
    public static final String JPEG2000_ENUMERATED_COLOUR_SPACE_19_CIEJAB_SHALL_NOT_BE_USED = "jpeg2000 enumerated colour space 19 (CIEJab) shall not be used";
    public static final String LZWDECODE_FILTER_IS_NOT_PERMITTED = "LZWDecode filter is not permitted";
    public static final String MIME_TYPE_SHALL_BE_SPECIFIED_USING_THE_SUBTYPE_KEY_OF_THE_FILE_SPECIFICATION_STREAM_DICTIONARY = "Mime type shall be specified using the subtype key of the file specification stream dictionary";
    public static final String N_KEY_SHALL_BE_APPEARANCE_SUBDICTIONARY = "If an annotation dictionary's Subtype key has a value of Widget and its FT key has a value of Btn, the value of the N key shall be an appearance subdictionary";
    public static final String NAMED_ACTION_TYPE_0_IS_NOT_ALLOWED = "Named action type {0} not allowed";
    public static final String NEEDAPPEARANCES_FLAG_OF_THE_INTERACTIVE_FORM_DICTIONARY_SHALL_EITHER_NOT_BE_PRESENTED_OR_SHALL_BE_FALSE = "Needappearances flag of the interactive form dictionary shall either not be presented or shall be false";
    public static final String NO_KEYS_OTHER_THAN_UR3_AND_DOC_MDP_SHALL_BE_PRESENT_IN_A_PERMISSIONS_DICTIONARY = "No keys other than UR3 and DocMDP shall be present in a permissions dictionary";
    public static final String NOT_IDENTITY_CRYPT_FILTER_IS_NOT_PERMITTED = "Not identity crypt filter is not permitted";
    public static final String ONLY_JPX_BASELINE_SET_OF_FEATURES_SHALL_BE_USED = "Only jpx baseline set of features shall be used";
    public static final String ONLY_STANDARD_BLEND_MODES_SHALL_BE_USED_FOR_THE_VALUE_OF_THE_BM_KEY_IN_AN_EXTENDED_GRAPHIC_STATE_DICTIONARY = "Only standard blend modes shall be used for the value of the BM key in an extended graphic state dictionary";
    public static final String OPTIONAL_CONTENT_CONFIGURATION_DICTIONARY_SHALL_CONTAIN_NAME_ENTRY = "Optional content configuration dictionary shall contain name entry";
    public static final String ORDER_ARRAY_SHALL_CONTAIN_REFERENCES_TO_ALL_OCGS = "Order array shall contain references to all ocgs";
    public static final String OUTPUT_INTENT_COLOR_SPACE_SHALL_BE_EITHER_GRAY_RGB_OR_CMYK = "Output intent color space shall be either gray rgb or cmyk";
    public static final String OVERPRINT_MODE_SHALL_NOT_BE_ONE_WHEN_AN_ICCBASED_CMYK_COLOUR_SPACE_IS_USED_AND_WHEN_OVERPRINTING_IS_SET_TO_TRUE = "Overprint mode shall not be one when an ICCBased CMYK colour space is used and when overprinting is set to true";
    public static final String PDF_STRING_IS_TOO_LONG = "PdfString is too long";
    public static final String PROFILE_STREAM_OF_OUTPUTINTENT_SHALL_BE_OUTPUT_PROFILE_PRTR_OR_MONITOR_PROFILE_MNTR = "Profile stream of outputintent shall be output profile (prtr) or monitor profile (mntr)";
    public static final String REAL_NUMBER_IS_OUT_OF_RANGE = "Real number is out of range";
    public static final String SIGNATURE_REFERENCES_DICTIONARY_SHALL_NOT_CONTAIN_DIGESTLOCATION_DIGESTMETHOD_DIGESTVALUE = "Signature references dictionary shall not contain digestlocation digestmethod digestvalue";
    public static final String STREAM_OBJECT_DICTIONARY_SHALL_NOT_CONTAIN_THE_F_FFILTER_OR_FDECODEPARAMS_KEYS = "Stream object dictionary shall not contain the f ffilter or fdecodeparams keys";
    public static final String THE_AS_KEY_SHALL_NOT_APPEAR_IN_ANY_OPTIONAL_CONTENT_CONFIGURATION_DICTIONARY = "The as key shall not appear in any optional content configuration dictionary";
    public static final String THE_BIT_DEPTH_OF_THE_JPEG2000_DATA_SHALL_HAVE_A_VALUE_IN_THE_RANGE_1_TO_38 = "The bit-depth of the jpeg2000 data shall have a value in the range 1 to 38";
    public static final String THE_CATALOG_DICTIONARY_SHALL_NOT_CONTAIN_THE_NEEDSRENDERING_KEY = "The catalog dictionary shall not contain the needsrendering key";
    public static final String THE_F_KEYS_PRINT_FLAG_BIT_SHALL_BE_SET_TO_1_AND_ITS_HIDDEN_INVISIBLE_AND_NOVIEW_FLAG_BITS_SHALL_BE_SET_TO_0 = "The f keys print flag bit shall be set to 1 and its hidden invisible and noview flag bits shall be set to 0";
    public static final String THE_F_KEYS_PRINT_FLAG_BIT_SHALL_BE_SET_TO_1_AND_ITS_HIDDEN_INVISIBLE_NOVIEW_AND_TOGGLENOVIEW_FLAG_BITS_SHALL_BE_SET_TO_0 = "The f keys print flag bit shall be set to 1 and its hidden invisible noview and togglenoview flag bits shall be set to 0";
    public static final String THE_INTERACTIVE_FORM_DICTIONARY_SHALL_NOT_CONTAIN_THE_XFA_KEY = "The interactive form dictionary shall not contain the xfa key";
    public static final String THE_NUMBER_OF_COLOUR_CHANNELS_IN_THE_JPEG2000_DATA_SHALL_BE_1_3_OR_4 = "The number of colour channels in the jpeg2000 data shall be 1, 3 or 4";
    public static final String THE_PAGE_DICTIONARY_SHALL_NOT_CONTAIN_AA_ENTRY = "The page dictionary shall not contain aa entry";
    public static final String THE_PAGE_DICTIONARY_SHALL_NOT_CONTAIN_PRESSTEPS_ENTRY = "The page dictionary shall not contain pressteps entry";
    public static final String THE_PAGE_LESS_3_UNITS_NO_GREATER_14400_IN_EITHER_DIRECTION = "The page less 3 units no greater 14400 in either direction";
    public static final String THE_SMASK_KEY_IS_NOT_ALLOWED_IN_EXTGSTATE = "The smask key is not allowed in extgstate";
    public static final String THE_SMASK_KEY_IS_NOT_ALLOWED_IN_XOBJECTS = "The smask key is not allowed in xobjects";
    public static final String THE_VALUE_OF_INTERPOLATE_KEY_SHALL_BE_FALSE = "The value of interpolate key shall not be true";
    public static final String THE_VALUE_OF_THE_METH_ENTRY_IN_COLR_BOX_SHALL_BE_1_2_OR_3 = "The value of the meth entry in colr box shall be 1, 2 or 3";
    public static final String TINT_TRANSFORM_AND_ALTERNATE_SPACE_SHALL_BE_THE_SAME_FOR_THE_ALL_SEPARATION_CS_WITH_THE_SAME_NAME = "TintTransform and alternateSpace shall be the same for the all separation cs with the same name";
    public static final String TRANSPARENCY_IS_NOT_ALLOWED_AND_CA_SHALL_BE_EQUAL_TO_1 = "Transparency is not allowed. ca shall be equal to 1";
    public static final String TRANSPARENCY_IS_NOT_ALLOWED_CA_SHALL_BE_EQUAL_TO_1 = "Transparency is not allowed. CA shall be equal to 1";
    public static final String VALUE_OF_NAME_ENTRY_SHALL_BE_UNIQUE_AMONG_ALL_OPTIONAL_CONTENT_CONFIGURATION_DICTIONARIES = "Value of name entry shall be unique among all optional content configuration dictionaries";
    public static final String WIDGET_ANNOTATION_DICTIONARY_OR_FIELD_DICTIONARY_SHALL_NOT_INCLUDE_A_OR_AA_ENTRY = "Widget annotation dictionary or field dictionary shall not include a or aa entry";
    
    @Deprecated
    public static final String IF_THE_DOCUMENT_DOES_NOT_CONTAIN_A_PDFA_OUTPUTINTENT_TRANSPARENCY_IS_FORBIDDEN = "If the document does not contain a pdfa outputintent transparency is forbidden";
    /**
     * Creates a PdfAConformanceException.
     *
     * @param message the error message
     */
    public PdfAConformanceException(String message) {
        super(message);
    }

    /**
     * Creates a PdfAConformanceException.
     *
     * @param message the error message
     * @param object  an object
     */
    public PdfAConformanceException(String message, Object object) {
        super(message, object);
    }
}

