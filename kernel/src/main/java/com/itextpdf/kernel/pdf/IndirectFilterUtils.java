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
package com.itextpdf.kernel.pdf;

import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.kernel.exceptions.PdfException;

import org.slf4j.Logger;

final class IndirectFilterUtils {
    private IndirectFilterUtils() {}

    static void throwFlushedFilterException(PdfStream stream) {
        throw new PdfException(
                MessageFormatUtil.format(
                        KernelExceptionMessageConstant.FLUSHED_STREAM_FILTER_EXCEPTION,
                        stream.getIndirectReference().getObjNumber(),
                        stream.getIndirectReference().getGenNumber()));
    }

    static void logFilterWasAlreadyFlushed(Logger logger, PdfStream stream) {
        logger.info(MessageFormatUtil.format(IoLogMessageConstant.FILTER_WAS_ALREADY_FLUSHED,
                stream.getIndirectReference().getObjNumber(), stream.getIndirectReference().getGenNumber()));
    }
}
