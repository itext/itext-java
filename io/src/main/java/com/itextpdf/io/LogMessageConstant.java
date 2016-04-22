/*
    $Id$

    This file is part of the iText (R) project.
    Copyright (c) 1998-2016 iText Group NV
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
public class LogMessageConstant {

    /**
     * Log message.
     */
    public static final String DOCUMENT_ALREADY_HAS_FIELD = "The document already has field {0}. Annotations of the fields with this name will be added to the existing one as children. If you want to have separate fields, please, rename them manually before copying.";

    /**
     * Log message.
     */
    public static final String DIRECTONLY_OBJECT_CANNOT_BE_INDIRECT = "DirectOnly object cannot be indirect";

    /**
     * Log message.
     */
    public static final String ELEMENT_DOES_NOT_FIT_AREA = "Element does not fit current area. {0}";

    /**
     * Log message.
     */
    public static final String FONT_HAS_INVALID_GLYPH = "Font {0} has invalid glyph: {1}";

    /**
     * Log message.
     */
    public static final String FORBID_RELEASE_IS_SET = "ForbidRelease flag is set and release is called. Releasing will not be performed.";

    /**
     * Log message.
     */
    public static final String IMAGE_HAS_AMBIGUOUS_SCALE = "The image cannot be auto scaled and scaled by a certain parameter simultaneously";

    /**
     * Log message.
     */
    public static final String IMAGE_HAS_JBIG2DECODE_FILTER = "Image cannot be inline if it has JBIG2Decode filter. It will be added as an ImageXObject";

    /**
     * Log message.
     */
    public static final String IMAGE_HAS_MASK = "Image cannot be inline if it has a Mask";

    /**
     * Log message.
     */
    public static final String IMAGE_HAS_JPXDECODE_FILTER = "Image cannot be inline if it has JPXDecode filter. It will be added as an ImageXObject";

    /**
     * Log message.
     */
    public static final String IMAGE_SIZE_CANNOT_BE_MORE_4KB = "Inline image size cannot be more than 4KB. It will be added as an ImageXObject";

    /**
     * Log message.
     */
    public static final String INVALID_INDIRECT_REFERENCE = "Invalid indirect reference";

    /**
     * Log message.
     */
    public static final String INVALID_KEY_VALUE_KEY_0_HAS_NULL_VALUE = "Invalid key value: key {0} has null value.";

    /**
     * Log message.
     */
    public static final String MAKE_COPY_OF_CATALOG_DICTIONARY_IS_FORBIDDEN = "Make copy of Catalog dictionary is forbidden.";

    /**
     * Log message.
     */
    public static final String ONLY_ONE_OF_ARTBOX_OR_TRIMBOX_CAN_EXIST_IN_THE_PAGE = "Only one of artbox or trimbox can exist on the page. The trimbox will be deleted";

    /**
     * Log message.
     */
    public static final String RECTANGLE_HAS_NEGATIVE_OR_ZERO_SIZES = "The {0} rectangle has negative or zero sizes. It will not be displayed.";

    /**
     * Log message.
     */
    public static final String REGISTERING_DIRECTORY = "Registering directory";

    /**
     * Log message.
     */
    public static final String SOURCE_DOCUMENT_HAS_ACROFORM_DICTIONARY = "Source document has AcroForm dictionary. The pages you are going to copy may have FormFields, but they will not be copied, because you have not used any IPdfPageExtraCopier";

    /**
     * Log message.
     */
    public static final String START_MARKER_MISSING_IN_PFB_FILE = "Start marker is missing in the pfb file";

    /**
     * Log message.
     */
    public static final String UNKNOWN_CMAP = "Unknown CMap {0}";

    /**
     * Log message.
     */
    public static final String UNKNOWN_ERROR_WHILE_PROCESSING_CMAP = "Unknown error while processing CMap.";

    /**
     * Log message.
     */
    public static final String TOUNICODE_CMAP_MORE_THAN_2_BYTES_NOT_SUPPORTED = "ToUnicode CMap more than 2 bytes not supported.";

    public static final String WRITER_ENCRYPTION_IS_IGNORED_APPEND = "Writer encryption will be ignored, because append mode is used. Document will preserve the original encryption (or will stay unencrypted)";

    public static final String WRITER_ENCRYPTION_IS_IGNORED_PRESERVE = "Writer encryption will be ignored, because preservation of encryption is enabled. Document will preserve the original encryption (or will stay unencrypted)";
}
