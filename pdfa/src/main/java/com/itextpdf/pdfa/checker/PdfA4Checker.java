/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 Apryse Group NV
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
package com.itextpdf.pdfa.checker;

import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.kernel.pdf.PdfAConformanceLevel;
import com.itextpdf.kernel.pdf.PdfCatalog;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.colorspace.PdfSpecialCs;
import com.itextpdf.pdfa.exceptions.PdfAConformanceException;
import com.itextpdf.pdfa.exceptions.PdfaExceptionMessageConstant;


/**
 * PdfA4Checker defines the requirements of the PDF/A-4 standard and contains a
 * number of methods that override the implementations of its superclass
 * {@link PdfA3Checker}.
 * <p>
 * The specification implemented by this class is ISO 19005-4
 */
public class PdfA4Checker extends PdfA3Checker {
    /**
     * Creates a PdfA4Checker with the required conformance level
     *
     * @param conformanceLevel the required conformance level
     */
    public PdfA4Checker(PdfAConformanceLevel conformanceLevel) {
        super(conformanceLevel);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void checkTrailer(PdfDictionary trailer) {
        super.checkTrailer(trailer);

        if (trailer.get(PdfName.Info) != null) {
            PdfDictionary info = trailer.getAsDictionary(PdfName.Info);
            if (info.size() != 1 || info.get(PdfName.ModDate) == null) {
                throw new PdfAConformanceException(PdfaExceptionMessageConstant.DOCUMENT_INFO_DICTIONARY_SHALL_ONLY_CONTAIN_MOD_DATE);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void checkCatalog(PdfCatalog catalog) {
        if ('2' != catalog.getDocument().getPdfVersion().toString().charAt(4)) {
            throw new PdfAConformanceException(
                    MessageFormatUtil.format(PdfaExceptionMessageConstant.THE_FILE_HEADER_SHALL_CONTAIN_RIGHT_PDF_VERSION, "2"));
        }
        PdfDictionary trailer = catalog.getDocument().getTrailer();
        if (trailer.get(PdfName.Info) != null) {
            if (catalog.getPdfObject().get(PdfName.PieceInfo) == null) {
                throw new PdfAConformanceException(PdfaExceptionMessageConstant.DOCUMENT_SHALL_NOT_CONTAIN_INFO_UNLESS_THERE_IS_PIECE_INFO);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void checkCatalogValidEntries(PdfDictionary catalogDict) {
        super.checkCatalogValidEntries(catalogDict);
        PdfString version = catalogDict.getAsString(PdfName.Version);
        if (version != null && (version.toString().charAt(0) != '2'
                || version.toString().charAt(1) != '.' || !Character.isDigit(version.toString().charAt(2)))) {
            throw new PdfAConformanceException(
                    MessageFormatUtil.format(PdfaExceptionMessageConstant.THE_CATALOG_VERSION_SHALL_CONTAIN_RIGHT_PDF_VERSION, "2"));
        }
    }

    //There are no limits for numbers in pdf-a/4
    /**
     * {@inheritDoc}
     */
    @Override
    protected void checkPdfNumber(PdfNumber number) {

    }

    //There is no limit for canvas stack in pdf-a/4
    /**
     * {@inheritDoc}
     */
    @Override
    public void checkCanvasStack(char stackOperation) {

    }

    //There is no limit for String length in pdf-a/4
    /**
     * {@inheritDoc}
     */
    @Override
    protected int getMaxStringLength() {
        return Integer.MAX_VALUE;
    }

    //There is no limit for DeviceN components count in pdf-a/4
    /**
     * {@inheritDoc}
     */
    @Override
    protected void checkNumberOfDeviceNComponents(PdfSpecialCs.DeviceN deviceN) {

    }
}
