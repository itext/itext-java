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

/**
 * Class containing pdfa conformance constants to be used in logging.
 */
public final class PdfAConformanceLogMessageConstant {
    public static final String CATALOG_SHOULD_CONTAIN_LANG_ENTRY = "Catalog dictionary should contain lang entry";
    public static final String EMBEDDED_FILE_SHALL_BE_COMPLIANT_WITH_SPEC = "Embedded file shall be compliant with either ISO 19005-1 (PDF-A/1 standard) or ISO 19005-2 (PDF-A/2 standard). Please ensure that fact, because iText doesn't check embedded file.";
    public static final String EMBEDDED_FILE_SHOULD_CONTAIN_PARAMS_KEY = "Embedded file should contain params key ";
    public static final String FILE_SPECIFICATION_DICTIONARY_SHOULD_CONTAIN_DESC_KEY = "File specification dictionary should contain desc key";
    public static final String TEXT_ANNOTATIONS_SHOULD_SET_THE_NOZOOM_AND_NOROTATE_FLAG_BITS_OF_THE_F_KEY_TO_1 = "Text annotations should set the nozoom and norotate flag bits of the f key to 1";
    public static final String TINT_TRANSFORM_AND_ALTERNATE_SPACE_OF_SEPARATION_ARRAYS_IN_THE_COLORANTS_OF_DEVICE_N_SHOULD_BE_CONSISTENT_WITH_SAME_ATTRIBUTES_OF_DEVICE_N = "TintTransform and alternateSpace of separation arrays in the colorants of deviceN should be consistent with same attributes of deviceN";
}
