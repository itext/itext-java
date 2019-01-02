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

import com.itextpdf.io.LogMessageConstant;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.log.CounterManager;
import com.itextpdf.kernel.log.ICounter;
import com.itextpdf.kernel.pdf.DocumentProperties;
import com.itextpdf.kernel.pdf.IsoKey;
import com.itextpdf.kernel.pdf.PdfAConformanceLevel;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfOutputIntent;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfResources;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.PdfVersion;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.StampingProperties;
import com.itextpdf.kernel.pdf.canvas.CanvasGraphicsState;
import com.itextpdf.kernel.pdf.tagutils.TagStructureContext;
import com.itextpdf.kernel.xmp.XMPConst;
import com.itextpdf.kernel.xmp.XMPException;
import com.itextpdf.kernel.xmp.XMPMeta;
import com.itextpdf.kernel.xmp.XMPMetaFactory;
import com.itextpdf.kernel.xmp.XMPUtils;
import com.itextpdf.pdfa.checker.PdfA1Checker;
import com.itextpdf.pdfa.checker.PdfA2Checker;
import com.itextpdf.pdfa.checker.PdfA3Checker;
import com.itextpdf.pdfa.checker.PdfAChecker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

/**
 * This class extends {@link PdfDocument} and is in charge of creating files
 * that comply with the PDF/A standard.
 *
 * Client code is still responsible for making sure the file is actually PDF/A
 * compliant: multiple steps must be undertaken (depending on the
 * {@link PdfAConformanceLevel}) to ensure that the PDF/A standard is followed.
 *
 * This class will throw exceptions, mostly {@link PdfAConformanceException},
 * and thus refuse to output a PDF/A file if at any point the document does not
 * adhere to the PDF/A guidelines specified by the {@link PdfAConformanceLevel}.
 */
public class PdfADocument extends PdfDocument {

    private static final long serialVersionUID = -5908390625367471894L;
    protected PdfAChecker checker;

    /**
     * Constructs a new PdfADocument for writing purposes, i.e. from scratch. A
     * PDF/A file has a conformance level, and must have an explicit output
     * intent.
     *
     * @param writer the {@link PdfWriter} object to write to
     * @param conformanceLevel the generation and strictness level of the PDF/A that must be followed.
     * @param outputIntent a {@link PdfOutputIntent}
     */
    public PdfADocument(PdfWriter writer, PdfAConformanceLevel conformanceLevel, PdfOutputIntent outputIntent) {
        this(writer, conformanceLevel, outputIntent, new DocumentProperties());
    }

    /**
     * Constructs a new PdfADocument for writing purposes, i.e. from scratch. A
     * PDF/A file has a conformance level, and must have an explicit output
     * intent.
     *
     * @param writer the {@link PdfWriter} object to write to
     * @param conformanceLevel the generation and strictness level of the PDF/A that must be followed.
     * @param outputIntent a {@link PdfOutputIntent}
     * @param properties a {@link com.itextpdf.kernel.pdf.DocumentProperties}
     */
    public PdfADocument(PdfWriter writer, PdfAConformanceLevel conformanceLevel, PdfOutputIntent outputIntent, DocumentProperties properties) {
        super(writer, properties);
        setChecker(conformanceLevel);
        addOutputIntent(outputIntent);
    }

    /**
     * Opens a PDF/A document in the stamping mode.
     *
     * @param reader PDF reader.
     * @param writer PDF writer.
     */
    public PdfADocument(PdfReader reader, PdfWriter writer) {
        this(reader, writer, new StampingProperties());
    }

    /**
     * Open a PDF/A document in stamping mode.
     *
     * @param reader PDF reader.
     * @param writer PDF writer.
     * @param properties properties of the stamping process
     */
    public PdfADocument(PdfReader reader, PdfWriter writer, StampingProperties properties) {
        super(reader, writer, properties);

        byte[] existingXmpMetadata = getXmpMetadata();
        if (existingXmpMetadata == null) {
            throw new PdfAConformanceException(PdfAConformanceException.DOCUMENT_TO_READ_FROM_SHALL_BE_A_PDFA_CONFORMANT_FILE_WITH_VALID_XMP_METADATA);
        }
        XMPMeta meta;
        try {
            meta = XMPMetaFactory.parseFromBuffer(existingXmpMetadata);
        } catch (XMPException exc) {
            throw new PdfAConformanceException(PdfAConformanceException.DOCUMENT_TO_READ_FROM_SHALL_BE_A_PDFA_CONFORMANT_FILE_WITH_VALID_XMP_METADATA);
        }
        PdfAConformanceLevel conformanceLevel = PdfAConformanceLevel.getConformanceLevel(meta);
        if (conformanceLevel == null) {
            throw new PdfAConformanceException(PdfAConformanceException.DOCUMENT_TO_READ_FROM_SHALL_BE_A_PDFA_CONFORMANT_FILE_WITH_VALID_XMP_METADATA);
        }

        setChecker(conformanceLevel);
    }

    @Override
    public void checkIsoConformance(Object obj, IsoKey key) {
        checkIsoConformance(obj, key, null);
    }

    @Override
    public void checkIsoConformance(Object obj, IsoKey key, PdfResources resources) {
        CanvasGraphicsState gState;
        PdfDictionary currentColorSpaces = null;
        if (resources != null) {
            currentColorSpaces = resources.getPdfObject().getAsDictionary(PdfName.ColorSpace);
        }
        switch (key) {
            case CANVAS_STACK:
                checker.checkCanvasStack((char) obj);
                break;
            case PDF_OBJECT:
                checker.checkPdfObject((PdfObject) obj);
                break;
            case RENDERING_INTENT:
                checker.checkRenderingIntent((PdfName) obj);
                break;
            case INLINE_IMAGE:
                checker.checkInlineImage((PdfStream) obj, currentColorSpaces);
                break;
            case EXTENDED_GRAPHICS_STATE:
                gState = (CanvasGraphicsState) obj;
                checker.checkExtGState(gState);
                break;
            case FILL_COLOR:
                gState = (CanvasGraphicsState) obj;
                checker.checkColor(gState.getFillColor(), currentColorSpaces, true);
                break;
            case PAGE:
                checker.checkSinglePage((PdfPage) obj);
                break;
            case STROKE_COLOR:
                gState = (CanvasGraphicsState) obj;
                checker.checkColor(gState.getStrokeColor(), currentColorSpaces, false);
                break;
            case TAG_STRUCTURE_ELEMENT:
                checker.checkTagStructureElement((PdfObject) obj);
                break;
        }
    }

    /**
     * Gets the PdfAConformanceLevel set in the constructor or in the metadata
     * of the {@link PdfReader}.
     *
     * @return a {@link PdfAConformanceLevel}
     */
    public PdfAConformanceLevel getConformanceLevel() {
        return checker.getConformanceLevel();
    }

    @Override
    protected void addCustomMetadataExtensions(XMPMeta xmpMeta) {
        if (this.isTagged()) {
            try {
                if (xmpMeta.getPropertyInteger(XMPConst.NS_PDFUA_ID, XMPConst.PART) != null) {
                    XMPMeta taggedExtensionMeta = XMPMetaFactory.parseFromString(PdfAXMPUtil.PDF_UA_EXTENSION);
                    XMPUtils.appendProperties(taggedExtensionMeta, xmpMeta, true, false);
                }
            } catch (XMPException exc) {
                Logger logger = LoggerFactory.getLogger(PdfADocument.class);
                logger.error(LogMessageConstant.EXCEPTION_WHILE_UPDATING_XMPMETADATA, exc);
            }
        }
    }

    @Override
    protected void updateXmpMetadata() {
        try {
            XMPMeta xmpMeta = updateDefaultXmpMetadata();
            xmpMeta.setProperty(XMPConst.NS_PDFA_ID, XMPConst.PART, checker.getConformanceLevel().getPart());
            xmpMeta.setProperty(XMPConst.NS_PDFA_ID, XMPConst.CONFORMANCE, checker.getConformanceLevel().getConformance());
            addCustomMetadataExtensions(xmpMeta);
            setXmpMetadata(xmpMeta);
        } catch (XMPException e) {
            Logger logger = LoggerFactory.getLogger(PdfADocument.class);
            logger.error(LogMessageConstant.EXCEPTION_WHILE_UPDATING_XMPMETADATA, e);
        }
    }

    @Override
    protected void checkIsoConformance() {
        checker.checkDocument(catalog);
    }

    @Override
    protected void flushObject(PdfObject pdfObject, boolean canBeInObjStm) throws IOException {
        markObjectAsMustBeFlushed(pdfObject);
        if (isClosing || checker.objectIsChecked(pdfObject)) {
            super.flushObject(pdfObject, canBeInObjStm);
        } else {
            //suppress the call
            //TODO log unsuccessful call
        }
    }

    @Override
    protected void flushFonts() {
        for (PdfFont pdfFont : getDocumentFonts()) {
            checker.checkFont(pdfFont);
        }
        super.flushFonts();
    }

    protected void setChecker(PdfAConformanceLevel conformanceLevel) {
        switch (conformanceLevel.getPart()) {
            case "1":
                checker = new PdfA1Checker(conformanceLevel);
                break;
            case "2":
                checker = new PdfA2Checker(conformanceLevel);
                break;
            case "3":
                checker = new PdfA3Checker(conformanceLevel);
                break;
        }
    }

    protected void initTagStructureContext() {
        tagStructureContext = new TagStructureContext(this, getPdfVersionForPdfA(checker.getConformanceLevel()));
    }

    @Deprecated
    @Override
    protected List<ICounter> getCounters() {
        return CounterManager.getInstance().getCounters(PdfADocument.class);
    }

    private static PdfVersion getPdfVersionForPdfA(PdfAConformanceLevel conformanceLevel) {
        PdfVersion version;
        switch (conformanceLevel.getPart()) {
            case "1":
                version = PdfVersion.PDF_1_4;
                break;
            case "2":
                version = PdfVersion.PDF_1_7;
                break;
            case "3":
                version = PdfVersion.PDF_1_7;
                break;
            default:
                version = PdfVersion.PDF_1_4;
                break;
        }
        return version;
    }
}
