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
package com.itextpdf.pdfa.logs;

/**
 * Class containing pdfa conformance constants to be used in logging.
 */
public final class PdfAConformanceLogMessageConstant {
    public static final String ANNOTATION_OF_TYPE_0_SHOULD_HAVE_CONTENTS_KEY = "Annotation of type {0} should have contents key";
    public static final String CATALOG_SHOULD_CONTAIN_LANG_ENTRY =
            "Catalog dictionary should contain lang entry";

    public static final String EMBEDDED_FILE_SHALL_BE_COMPLIANT_WITH_SPEC =
            "Embedded file shall be compliant with either ISO 19005-1 (PDF-A/1 standard) or ISO 19005-2 "
                    + "(PDF-A/2 standard). Please ensure that fact, because iText doesn't check embedded file.";

    public static final String EMBEDDED_FILE_SHOULD_CONTAIN_PARAMS_KEY = "Embedded file should contain params key ";

    public static final String FILE_SPECIFICATION_DICTIONARY_SHOULD_CONTAIN_DESC_KEY =
            "File specification dictionary should contain desc key";

    public static final String TEXT_ANNOTATIONS_SHOULD_SET_THE_NOZOOM_AND_NOROTATE_FLAG_BITS_OF_THE_F_KEY_TO_1 =
            "Text annotations should set the nozoom and norotate flag bits of the f key to 1";

    public static final String TINT_TRANSFORM_AND_ALTERNATE_SPACE_OF_SEPARATION_ARRAYS_IN_THE_COLORANTS_OF_DEVICE_N_SHOULD_BE_CONSISTENT_WITH_SAME_ATTRIBUTES_OF_DEVICE_N =
            "TintTransform and alternateSpace of separation arrays in the colorants of deviceN should be consistent "
                    + "with same attributes of deviceN";

    private PdfAConformanceLogMessageConstant() {
        //Private constructor will prevent the instantiation of this class directly
    }
}
