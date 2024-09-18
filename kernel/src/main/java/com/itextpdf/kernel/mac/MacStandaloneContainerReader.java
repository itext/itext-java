package com.itextpdf.kernel.mac;

import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;

class MacStandaloneContainerReader extends MacContainerReader {
    MacStandaloneContainerReader(PdfDictionary authDictionary) {
        super(authDictionary);
    }

    @Override
    byte[] parseSignature(PdfDictionary authDictionary) {
        return null;
    }

    @Override
    long[] parseByteRange(PdfDictionary authDictionary) {
        return authDictionary.getAsArray(PdfName.ByteRange).toLongArray();
    }

    @Override
    byte[] parseMacContainer(PdfDictionary authDictionary) {
        if (authDictionary.getAsString(PdfName.MAC) == null) {
            throw new PdfException(KernelExceptionMessageConstant.MAC_NOT_SPECIFIED);
        }
        return authDictionary.getAsString(PdfName.MAC).getValueBytes();
    }
}
