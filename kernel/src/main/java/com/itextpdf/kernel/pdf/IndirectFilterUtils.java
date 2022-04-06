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
