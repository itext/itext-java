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
package com.itextpdf.kernel.utils.annotationsflattening;

import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.logs.KernelLogMessageConstant;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is used to warn user that annotation will not be flattened.
 */
public class WarnFormfieldFlattener implements IAnnotationFlattener {

    private static final Logger LOGGER = LoggerFactory.getLogger(WarnFormfieldFlattener.class);

    /**
     * Creates a new {@link WarnFormfieldFlattener} instance.
     */
    public WarnFormfieldFlattener() {
        //empty constructor
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean flatten(PdfAnnotation annotation, PdfPage page) {
        if (annotation == null) {
            throw new PdfException(
                    MessageFormatUtil.format(KernelExceptionMessageConstant.ARG_SHOULD_NOT_BE_NULL, "annotation"));
        }
        if (page == null) {
            throw new PdfException(
                    MessageFormatUtil.format(KernelExceptionMessageConstant.ARG_SHOULD_NOT_BE_NULL, "page"));
        }
        LOGGER.warn(KernelLogMessageConstant.FORMFIELD_ANNOTATION_WILL_NOT_BE_FLATTENED);
        return false;
    }
}
