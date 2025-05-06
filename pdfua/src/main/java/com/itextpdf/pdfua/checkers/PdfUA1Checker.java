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
package com.itextpdf.pdfua.checkers;

import com.itextpdf.commons.datastructures.Tuple2;
import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.io.font.TrueTypeFont;
import com.itextpdf.kernel.pdf.EncryptionConstants;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfBoolean;
import com.itextpdf.kernel.pdf.PdfCatalog;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfVersion;
import com.itextpdf.kernel.pdf.tagging.PdfNamespace;
import com.itextpdf.kernel.pdf.tagging.PdfStructTreeRoot;
import com.itextpdf.kernel.pdf.tagging.StandardRoles;
import com.itextpdf.kernel.pdf.tagutils.IRoleMappingResolver;
import com.itextpdf.kernel.pdf.tagutils.TagStructureContext;
import com.itextpdf.kernel.pdf.tagutils.TagTreeIterator;
import com.itextpdf.kernel.utils.checkers.PdfCheckersUtil;
import com.itextpdf.kernel.validation.IValidationContext;
import com.itextpdf.kernel.validation.context.CanvasBmcValidationContext;
import com.itextpdf.kernel.validation.context.CanvasWritingContentValidationContext;
import com.itextpdf.kernel.validation.context.CryptoValidationContext;
import com.itextpdf.kernel.validation.context.DuplicateIdEntryValidationContext;
import com.itextpdf.kernel.validation.context.FontValidationContext;
import com.itextpdf.kernel.validation.context.PdfDocumentValidationContext;
import com.itextpdf.kernel.validation.context.PdfObjectValidationContext;
import com.itextpdf.kernel.xmp.XMPConst;
import com.itextpdf.kernel.xmp.XMPException;
import com.itextpdf.kernel.xmp.XMPMeta;
import com.itextpdf.layout.validation.context.LayoutValidationContext;
import com.itextpdf.pdfua.checkers.utils.GraphicsCheckUtil;
import com.itextpdf.pdfua.checkers.utils.LayoutCheckUtil;
import com.itextpdf.pdfua.checkers.utils.PdfUAValidationContext;
import com.itextpdf.pdfua.checkers.utils.tables.TableCheckUtil;
import com.itextpdf.pdfua.checkers.utils.ua1.PdfUA1AnnotationChecker;
import com.itextpdf.pdfua.checkers.utils.ua1.PdfUA1FormChecker;
import com.itextpdf.pdfua.checkers.utils.ua1.PdfUA1FormulaChecker;
import com.itextpdf.pdfua.checkers.utils.ua1.PdfUA1HeadingsChecker;
import com.itextpdf.pdfua.checkers.utils.ua1.PdfUA1ListChecker;
import com.itextpdf.pdfua.checkers.utils.ua1.PdfUA1NotesChecker;
import com.itextpdf.pdfua.checkers.utils.ua1.PdfUA1XfaCheckUtil;
import com.itextpdf.pdfua.exceptions.PdfUAConformanceException;
import com.itextpdf.pdfua.exceptions.PdfUAExceptionMessageConstants;

import java.util.Map;
import java.util.Stack;

/**
 * The class defines the requirements of the PDF/UA-1 standard and contains
 * method implementations from the abstract {@link PdfUAChecker} class.
 *
 * <p>
 * The specification implemented by this class is ISO 14289-1.
 */
public class PdfUA1Checker extends PdfUAChecker {

    private final PdfDocument pdfDocument;
    private final TagStructureContext tagStructureContext;
    private final PdfUA1HeadingsChecker headingsChecker;

    private final PdfUAValidationContext context;

    /**
     * Creates PdfUA1Checker instance with PDF document which will be validated against PDF/UA-1 standard.
     *
     * @param pdfDocument the document to validate
     */
    public PdfUA1Checker(PdfDocument pdfDocument) {
        super();
        this.pdfDocument = pdfDocument;
        this.tagStructureContext = new TagStructureContext(pdfDocument);
        this.context = new PdfUAValidationContext(pdfDocument);
        this.headingsChecker = new PdfUA1HeadingsChecker(context);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void validate(IValidationContext context) {
        switch (context.getType()) {
            case PDF_DOCUMENT:
                PdfDocumentValidationContext pdfDocContext = (PdfDocumentValidationContext) context;
                checkCatalog(pdfDocContext.getPdfDocument().getCatalog());
                checkStructureTreeRoot(pdfDocContext.getPdfDocument().getStructTreeRoot());
                checkFonts(pdfDocContext.getDocumentFonts());
                PdfUA1XfaCheckUtil.check(pdfDocContext.getPdfDocument());
                break;
            case PDF_OBJECT:
                PdfObjectValidationContext objContext = (PdfObjectValidationContext) context;
                checkPdfObject(objContext.getObject());
                break;
            case CRYPTO:
                CryptoValidationContext cryptoContext = (CryptoValidationContext) context;
                checkCrypto((PdfDictionary) cryptoContext.getCrypto());
                break;
            case FONT:
                FontValidationContext fontContext = (FontValidationContext) context;
                checkText(fontContext.getText(), fontContext.getFont());
                break;
            case CANVAS_BEGIN_MARKED_CONTENT:
                CanvasBmcValidationContext bmcContext = (CanvasBmcValidationContext) context;
                checkLogicalStructureInBMC(bmcContext.getTagStructureStack(), bmcContext.getCurrentBmc(),
                        this.pdfDocument);
                break;
            case CANVAS_WRITING_CONTENT:
                CanvasWritingContentValidationContext writingContext = (CanvasWritingContentValidationContext) context;
                checkContentInCanvas(writingContext.getTagStructureStack(), this.pdfDocument);
                break;
            case LAYOUT:
                LayoutValidationContext layoutContext = (LayoutValidationContext) context;
                new LayoutCheckUtil(this.context).checkRenderer(layoutContext.getRenderer());
                headingsChecker.checkLayoutElement(layoutContext.getRenderer());
                break;
            case DUPLICATE_ID_ENTRY:
                DuplicateIdEntryValidationContext idContext = (DuplicateIdEntryValidationContext) context;
                throw new PdfUAConformanceException(MessageFormatUtil.format(
                        PdfUAExceptionMessageConstants.NON_UNIQUE_ID_ENTRY_IN_STRUCT_TREE_ROOT, idContext.getId()));
        }
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean isPdfObjectReadyToFlush(PdfObject object) {
        return false;
    }

    /**
     * Verify the conformity of the file specification dictionary.
     *
     * @param fileSpec the {@link PdfDictionary} containing file specification to be checked
     */
    protected void checkFileSpec(PdfDictionary fileSpec) {
        if (fileSpec.containsKey(PdfName.EF)) {
            if (!fileSpec.containsKey(PdfName.F) || !fileSpec.containsKey(PdfName.UF)) {
                throw new PdfUAConformanceException(PdfUAExceptionMessageConstants.FILE_SPECIFICATION_DICTIONARY_SHALL_CONTAIN_F_KEY_AND_UF_KEY);
            }
        }
    }

    /**
     * Checks that the {@code Catalog} dictionary of a conforming file (the version number of a file may be any value
     * from 1.0 to 1.7) contains the {@code Metadata} key whose value is a metadata stream. Also checks that the value
     * of {@code pdfuaid:part} is 1 for conforming PDF files.
     *
     * <p>
     * Checks that the {@code Metadata} stream in the document catalog dictionary includes a {@code dc:title} entry
     * reflecting the title of the document.
     *
     * @param catalog {@link PdfCatalog} document catalog dictionary
     */
    protected void checkMetadata(PdfCatalog catalog) {
        if (catalog.getDocument().getPdfVersion().compareTo(PdfVersion.PDF_1_7) > 0) {
            throw new PdfUAConformanceException(PdfUAExceptionMessageConstants.INVALID_PDF_VERSION);
        }

        try {
            XMPMeta metadata = catalog.getDocument().getXmpMetadata();
            if (metadata == null) {
                throw new PdfUAConformanceException(
                        PdfUAExceptionMessageConstants.DOCUMENT_SHALL_CONTAIN_XMP_METADATA_STREAM);
            }

            Integer part = metadata.getPropertyInteger(XMPConst.NS_PDFUA_ID, XMPConst.PART);
            if (!Integer.valueOf(1).equals(part)) {
                throw new PdfUAConformanceException(
                        PdfUAExceptionMessageConstants.METADATA_SHALL_CONTAIN_UA_VERSION_IDENTIFIER);
            }
            if (metadata.getProperty(XMPConst.NS_DC, XMPConst.TITLE) == null) {
                throw new PdfUAConformanceException(
                        PdfUAExceptionMessageConstants.METADATA_SHALL_CONTAIN_DC_TITLE_ENTRY);
            }
        } catch (XMPException e) {
            throw new PdfUAConformanceException(
                    PdfUAExceptionMessageConstants.DOCUMENT_SHALL_CONTAIN_XMP_METADATA_STREAM, e);
        }
    }

    @Override
    void checkOCProperties(PdfDictionary ocProperties) {
        if (ocProperties != null && !(ocProperties.get(PdfName.Configs) instanceof PdfArray)) {
            throw new PdfUAConformanceException(PdfUAExceptionMessageConstants.OCG_PROPERTIES_CONFIG_SHALL_BE_AN_ARRAY);
        }
        super.checkOCProperties(ocProperties);
    }

    @Override
    void checkLogicalStructureInBMC(Stack<Tuple2<PdfName, PdfDictionary>> stack,
                                    Tuple2<PdfName, PdfDictionary> currentBmc, PdfDocument document) {
        checkStandardRoleMapping(currentBmc);
        super.checkLogicalStructureInBMC(stack, currentBmc, document);
    }

    /**
     * For all non-symbolic TrueType fonts used for rendering, the embedded TrueType font program shall contain one or
     * several non-symbolic cmap entries such that all necessary glyph lookups can be carried out.
     *
     * @param fontProgram the embedded TrueType font program to check
     */
    @Override
    void checkNonSymbolicCmapSubtable(TrueTypeFont fontProgram) {
        if ((fontProgram.isCmapPresent(3, 0) && fontProgram.getNumberOfCmaps() == 1) ||
                fontProgram.getNumberOfCmaps() == 0) {
            throw new PdfUAConformanceException(
                    PdfUAExceptionMessageConstants.NON_SYMBOLIC_TTF_SHALL_CONTAIN_NON_SYMBOLIC_CMAP);
        }
    }

    /**
     * Checks cmap entries present in the embedded TrueType font program of the symbolic TrueType font.
     *
     * <p>
     * The “cmap” table in the embedded font program shall either contain exactly one encoding or it shall contain,
     * at least, the Microsoft Symbol (3,0 – Platform ID = 3, Encoding ID = 0) encoding.
     *
     * @param fontProgram the embedded TrueType font program to check
     */
    @Override
    void checkSymbolicCmapSubtable(TrueTypeFont fontProgram) {
        if (!fontProgram.isCmapPresent(3, 0) && fontProgram.getNumberOfCmaps() != 1) {
            throw new PdfUAConformanceException(PdfUAExceptionMessageConstants.
                    SYMBOLIC_TTF_SHALL_CONTAIN_EXACTLY_ONE_OR_AT_LEAST_MICROSOFT_SYMBOL_CMAP);
        }
    }

    private void checkStandardRoleMapping(Tuple2<PdfName, PdfDictionary> tag) {
        final PdfNamespace namespace = tagStructureContext.getDocumentDefaultNamespace();
        final String role = tag.getFirst().getValue();
        if (!StandardRoles.ARTIFACT.equals(role) && !tagStructureContext.checkIfRoleShallBeMappedToStandardRole(role,
                namespace)) {
            throw new PdfUAConformanceException(
                    MessageFormatUtil.format(
                            PdfUAExceptionMessageConstants.TAG_MAPPING_DOESNT_TERMINATE_WITH_STANDARD_TYPE, role));
        }
    }

    private void checkCatalog(PdfCatalog catalog) {
        PdfDictionary catalogDict = catalog.getPdfObject();
        if (!catalogDict.containsKey(PdfName.Metadata)) {
            throw new PdfUAConformanceException(
                    PdfUAExceptionMessageConstants.METADATA_SHALL_BE_PRESENT_IN_THE_CATALOG_DICTIONARY);
        }
        checkLang(catalog);
        PdfCheckersUtil.validateLang(catalogDict, EXCEPTION_SUPPLIER);

        PdfDictionary markInfo = catalogDict.getAsDictionary(PdfName.MarkInfo);
        if (markInfo != null && markInfo.containsKey(PdfName.Suspects)) {
            PdfBoolean markInfoSuspects = markInfo.getAsBoolean(PdfName.Suspects);
            if (markInfoSuspects != null && markInfoSuspects.getValue()) {
                throw new PdfUAConformanceException(
                        PdfUAExceptionMessageConstants.
                                SUSPECTS_ENTRY_IN_MARK_INFO_DICTIONARY_SHALL_NOT_HAVE_A_VALUE_OF_TRUE);
            }
        }
        checkViewerPreferences(catalog);
        checkMetadata(catalog);
        checkOCProperties(catalogDict.getAsDictionary(PdfName.OCProperties));
    }

    private void checkStructureTreeRoot(PdfStructTreeRoot structTreeRoot) {
        PdfDictionary roleMap = structTreeRoot.getRoleMap();
        for (Map.Entry<PdfName, PdfObject> entry : roleMap.entrySet()) {
            final String role = entry.getKey().getValue();
            final IRoleMappingResolver roleMappingResolver = pdfDocument.getTagStructureContext()
                    .getRoleMappingResolver(role);

            if (roleMappingResolver.currentRoleIsStandard()) {
                throw new PdfUAConformanceException(PdfUAExceptionMessageConstants.ONE_OR_MORE_STANDARD_ROLE_REMAPPED);
            }
        }

        TagTreeIterator tagTreeIterator = new TagTreeIterator(structTreeRoot);
        tagTreeIterator.addHandler(new GraphicsCheckUtil.GraphicsHandler(context));
        tagTreeIterator.addHandler(new PdfUA1FormulaChecker.PdfUA1FormulaTagHandler(context));
        tagTreeIterator.addHandler(new PdfUA1NotesChecker.PdfUA1NotesTagHandler(context));
        tagTreeIterator.addHandler(new PdfUA1HeadingsChecker.PdfUA1HeadingHandler(context));
        tagTreeIterator.addHandler(new TableCheckUtil.TableHandler(context));
        tagTreeIterator.addHandler(new PdfUA1AnnotationChecker.PdfUA1AnnotationHandler(context));
        tagTreeIterator.addHandler(new PdfUA1FormChecker.PdfUA1FormTagHandler(context));
        tagTreeIterator.addHandler(new PdfUA1ListChecker.PdfUA1ListHandler(context));
        tagTreeIterator.traverse();
    }

    private void checkCrypto(PdfDictionary encryptionDictionary) {
        if (encryptionDictionary != null) {
            if (!(encryptionDictionary.get(PdfName.P) instanceof PdfNumber)) {
                throw new PdfUAConformanceException(
                        PdfUAExceptionMessageConstants.P_VALUE_IS_ABSENT_IN_ENCRYPTION_DICTIONARY);
            }
            int permissions = (int) ((PdfNumber) encryptionDictionary.get(PdfName.P)).longValue();
            if ((EncryptionConstants.ALLOW_SCREENREADERS & permissions) == 0) {
                throw new PdfUAConformanceException(
                        PdfUAExceptionMessageConstants.TENTH_BIT_OF_P_VALUE_IN_ENCRYPTION_SHOULD_BE_NON_ZERO);
            }
        }
    }

    /**
     * This method checks the requirements that must be fulfilled by a COS
     * object in a PDF/UA document.
     *
     * @param obj the COS object that must be checked
     */
    private void checkPdfObject(PdfObject obj) {
        if (obj.getType() == PdfObject.DICTIONARY) {
            PdfDictionary dict = (PdfDictionary) obj;
            PdfName type = dict.getAsName(PdfName.Type);
            if (PdfName.Filespec.equals(type)) {
                checkFileSpec(dict);
            }
        }
    }
}
