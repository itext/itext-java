/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2024 Apryse Group NV
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
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.pdf.EncryptionConstants;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfBoolean;
import com.itextpdf.kernel.pdf.PdfCatalog;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.PdfVersion;
import com.itextpdf.kernel.pdf.tagging.PdfMcr;
import com.itextpdf.kernel.pdf.tagging.PdfNamespace;
import com.itextpdf.kernel.pdf.tagging.PdfStructTreeRoot;
import com.itextpdf.kernel.pdf.tagging.StandardRoles;
import com.itextpdf.kernel.pdf.tagutils.IRoleMappingResolver;
import com.itextpdf.kernel.pdf.tagutils.TagStructureContext;
import com.itextpdf.kernel.pdf.tagutils.TagTreeIterator;
import com.itextpdf.kernel.utils.checkers.FontCheckUtil;
import com.itextpdf.kernel.validation.IValidationChecker;
import com.itextpdf.kernel.validation.context.CanvasBmcValidationContext;
import com.itextpdf.kernel.validation.context.CanvasWritingContentValidationContext;
import com.itextpdf.kernel.validation.context.CryptoValidationContext;
import com.itextpdf.kernel.validation.context.DuplicateIdEntryValidationContext;
import com.itextpdf.kernel.validation.context.FontValidationContext;
import com.itextpdf.kernel.validation.IValidationContext;
import com.itextpdf.kernel.validation.context.PdfDocumentValidationContext;
import com.itextpdf.kernel.validation.context.PdfObjectValidationContext;
import com.itextpdf.kernel.xmp.XMPConst;
import com.itextpdf.kernel.xmp.XMPException;
import com.itextpdf.kernel.xmp.XMPMeta;
import com.itextpdf.kernel.xmp.XMPMetaFactory;
import com.itextpdf.layout.validation.context.LayoutValidationContext;
import com.itextpdf.pdfua.checkers.utils.AnnotationCheckUtil;
import com.itextpdf.pdfua.checkers.utils.BCP47Validator;
import com.itextpdf.pdfua.checkers.utils.FormCheckUtil;
import com.itextpdf.pdfua.checkers.utils.FormulaCheckUtil;
import com.itextpdf.pdfua.checkers.utils.GraphicsCheckUtil;
import com.itextpdf.pdfua.checkers.utils.LayoutCheckUtil;
import com.itextpdf.pdfua.checkers.utils.NoteCheckUtil;
import com.itextpdf.pdfua.checkers.utils.PdfUAValidationContext;
import com.itextpdf.pdfua.checkers.utils.XfaCheckUtil;
import com.itextpdf.pdfua.checkers.utils.headings.HeadingsChecker;
import com.itextpdf.pdfua.checkers.utils.tables.TableCheckUtil;
import com.itextpdf.pdfua.exceptions.PdfUAConformanceException;
import com.itextpdf.pdfua.exceptions.PdfUAExceptionMessageConstants;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

/**
 * The class defines the requirements of the PDF/UA-1 standard.
 * <p>
 * The specification implemented by this class is ISO 14289-1
 */
public class PdfUA1Checker implements IValidationChecker {

    private final PdfDocument pdfDocument;

    private final TagStructureContext tagStructureContext;

    private final HeadingsChecker headingsChecker;

    private final PdfUAValidationContext context;

    /**
     * Creates PdfUA1Checker instance with PDF document which will be validated against PDF/UA-1 standard.
     *
     * @param pdfDocument the document to validate
     */
    public PdfUA1Checker(PdfDocument pdfDocument) {
        this.pdfDocument = pdfDocument;
        this.tagStructureContext = new TagStructureContext(pdfDocument);
        this.context = new PdfUAValidationContext(pdfDocument);
        this.headingsChecker = new HeadingsChecker(context);
    }

    @Override
    public void validate(IValidationContext context) {
        switch (context.getType()) {
            case PDF_DOCUMENT:
                PdfDocumentValidationContext pdfDocContext = (PdfDocumentValidationContext) context;
                checkCatalog(pdfDocContext.getPdfDocument().getCatalog());
                checkStructureTreeRoot(pdfDocContext.getPdfDocument().getStructTreeRoot());
                checkFonts(pdfDocContext.getDocumentFonts());
                XfaCheckUtil.check(pdfDocContext.getPdfDocument());
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
                checkOnOpeningBeginMarkedContent(bmcContext.getTagStructureStack(), bmcContext.getCurrentBmc());
                break;
            case CANVAS_WRITING_CONTENT:
                CanvasWritingContentValidationContext writingContext = (CanvasWritingContentValidationContext) context;
                checkOnWritingCanvasToContent(writingContext.getTagStructureStack());
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


    private void checkText(String str, PdfFont font) {
        int index = FontCheckUtil.checkGlyphsOfText(str, font, new UaCharacterChecker());

        if (index != -1) {
            throw new PdfUAConformanceException(MessageFormatUtil.format(
                    PdfUAExceptionMessageConstants.GLYPH_IS_NOT_DEFINED_OR_WITHOUT_UNICODE, str.charAt(index)));
        }
    }

    protected void checkMetadata(PdfCatalog catalog) {
        if (catalog.getDocument().getPdfVersion().compareTo(PdfVersion.PDF_1_7) > 0) {
            throw new PdfUAConformanceException(PdfUAExceptionMessageConstants.INVALID_PDF_VERSION);
        }

        PdfObject pdfMetadata = catalog.getPdfObject().get(PdfName.Metadata);
        if (pdfMetadata == null || !pdfMetadata.isStream()) {
            throw new PdfUAConformanceException(PdfUAExceptionMessageConstants.DOCUMENT_SHALL_CONTAIN_XMP_METADATA_STREAM);
        }
        byte[] metaBytes = ((PdfStream) pdfMetadata).getBytes();

        try {
            XMPMeta metadata = XMPMetaFactory.parseFromBuffer(metaBytes);
            Integer part = metadata.getPropertyInteger(XMPConst.NS_PDFUA_ID, XMPConst.PART);
            if (!Integer.valueOf(1).equals(part)) {
                throw new PdfUAConformanceException(PdfUAExceptionMessageConstants.METADATA_SHALL_CONTAIN_UA_VERSION_IDENTIFIER);
            }
            if (metadata.getProperty(XMPConst.NS_DC, XMPConst.TITLE) == null) {
                throw new PdfUAConformanceException(PdfUAExceptionMessageConstants.METADATA_SHALL_CONTAIN_DC_TITLE_ENTRY);
            }
        } catch (XMPException e) {
            throw new PdfUAConformanceException(PdfUAExceptionMessageConstants.DOCUMENT_SHALL_CONTAIN_XMP_METADATA_STREAM, e);
        }
    }

    private void checkViewerPreferences(PdfCatalog catalog) {
        PdfDictionary viewerPreferences = catalog.getPdfObject().getAsDictionary(PdfName.ViewerPreferences);
        if (viewerPreferences == null) {
            throw new PdfUAConformanceException(PdfUAExceptionMessageConstants.MISSING_VIEWER_PREFERENCES);
        }
        PdfObject displayDocTitle = viewerPreferences.get(PdfName.DisplayDocTitle);
        if (!(displayDocTitle instanceof PdfBoolean)) {
            throw new PdfUAConformanceException(PdfUAExceptionMessageConstants.MISSING_VIEWER_PREFERENCES);
        }
        if (PdfBoolean.FALSE.equals(displayDocTitle)) {
            throw new PdfUAConformanceException(PdfUAExceptionMessageConstants.VIEWER_PREFERENCES_IS_FALSE);
        }

    }

    private void checkOnWritingCanvasToContent(Stack<Tuple2<PdfName, PdfDictionary>> tagStack) {
        if (tagStack.isEmpty()) {
            throw new PdfUAConformanceException(
                    PdfUAExceptionMessageConstants.TAG_HASNT_BEEN_ADDED_BEFORE_CONTENT_ADDING);
        }

        final boolean insideRealContent = isInsideRealContent(tagStack);
        final boolean insideArtifact = isInsideArtifact(tagStack);
        if (insideRealContent && insideArtifact) {
            throw new PdfUAConformanceException(
                    PdfUAExceptionMessageConstants.REAL_CONTENT_INSIDE_ARTIFACT_OR_VICE_VERSA);
        } else if (!insideRealContent && !insideArtifact) {
            throw new PdfUAConformanceException(
                    PdfUAExceptionMessageConstants.CONTENT_IS_NOT_REAL_CONTENT_AND_NOT_ARTIFACT);
        }
    }

    private void checkOnOpeningBeginMarkedContent(Stack<Tuple2<PdfName, PdfDictionary>> stack,
            Tuple2<PdfName, PdfDictionary> currentBmc) {

        checkStandardRoleMapping(currentBmc);

        if (stack.isEmpty()) {
            return;
        }

        boolean isRealContent = isRealContent(currentBmc);
        boolean isArtifact = PdfName.Artifact.equals(currentBmc.getFirst());

        if (isArtifact && isInsideRealContent(stack)) {
            throw new PdfUAConformanceException(PdfUAExceptionMessageConstants.ARTIFACT_CANT_BE_INSIDE_REAL_CONTENT);
        }
        if (isRealContent && isInsideArtifact(stack)) {
            throw new PdfUAConformanceException(PdfUAExceptionMessageConstants.REAL_CONTENT_CANT_BE_INSIDE_ARTIFACT);
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

    private boolean isInsideArtifact(Stack<Tuple2<PdfName, PdfDictionary>> tagStack) {
        for (Tuple2<PdfName, PdfDictionary> tag : tagStack) {
            if (PdfName.Artifact.equals(tag.getFirst())) {
                return true;
            }
        }
        return false;
    }

    private boolean isInsideRealContent(Stack<Tuple2<PdfName, PdfDictionary>> tagStack) {
        for (Tuple2<PdfName, PdfDictionary> tag : tagStack) {
            if (isRealContent(tag)) {
                return true;
            }
        }
        return false;
    }

    private boolean isRealContent(Tuple2<PdfName, PdfDictionary> tag) {
        if (PdfName.Artifact.equals(tag.getFirst())) {
            return false;
        }
        PdfDictionary properties = tag.getSecond();
        if (properties == null || !properties.containsKey(PdfName.MCID)) {
            return false;
        }
        PdfMcr mcr = this.pdfDocument.getStructTreeRoot()
                .findMcrByMcid(pdfDocument, (int) properties.getAsInt(PdfName.MCID));
        if (mcr == null) {
            throw new PdfUAConformanceException(
                    PdfUAExceptionMessageConstants.CONTENT_WITH_MCID_BUT_MCID_NOT_FOUND_IN_STRUCT_TREE_ROOT);
        }
        return true;
    }

    private void checkCatalog(PdfCatalog catalog) {
        PdfDictionary catalogDict = catalog.getPdfObject();
        if (!catalogDict.containsKey(PdfName.Metadata)) {
            throw new PdfUAConformanceException(
                    PdfUAExceptionMessageConstants.METADATA_SHALL_BE_PRESENT_IN_THE_CATALOG_DICTIONARY);
        }
        if (!(catalogDict.get(PdfName.Lang) instanceof PdfString) || !BCP47Validator.validate(catalogDict.get(PdfName.Lang).toString())) {
            throw new PdfUAConformanceException(
                    PdfUAExceptionMessageConstants.DOCUMENT_SHALL_CONTAIN_VALID_LANG_ENTRY);
        }
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
        tagTreeIterator.addHandler(new FormulaCheckUtil.FormulaTagHandler(context));
        tagTreeIterator.addHandler(new NoteCheckUtil.NoteTagHandler(context));
        tagTreeIterator.addHandler(new HeadingsChecker.HeadingHandler(context));
        tagTreeIterator.addHandler(new TableCheckUtil.TableHandler(context));
        tagTreeIterator.addHandler(new AnnotationCheckUtil.AnnotationHandler(context));
        tagTreeIterator.addHandler(new FormCheckUtil.FormTagHandler(context));
        tagTreeIterator.traverse();
    }

    private void checkOCProperties(PdfDictionary ocProperties) {
        if (ocProperties == null) {
            return;
        }
        if (!(ocProperties.get(PdfName.Configs) instanceof PdfArray)) {
            throw new PdfUAConformanceException(PdfUAExceptionMessageConstants.OCG_PROPERTIES_CONFIG_SHALL_BE_AN_ARRAY);
        }
        PdfArray configs = ocProperties.getAsArray(PdfName.Configs);
        if (configs != null && !configs.isEmpty()) {
            PdfDictionary d = ocProperties.getAsDictionary(PdfName.D);
            checkOCGNameAndASKey(d);
            for (PdfObject config : configs) {
                checkOCGNameAndASKey((PdfDictionary) config);
            }
            PdfArray ocgsArray = ocProperties.getAsArray(PdfName.OCGs);
            if (ocgsArray != null) {
                for (PdfObject ocg : ocgsArray) {
                    checkOCGNameAndASKey((PdfDictionary) ocg);
                }
            }
        }
    }

    private void checkOCGNameAndASKey(PdfDictionary dict) {
        if (dict == null) {
            return;
        }
        if (dict.get(PdfName.AS) != null) {
            throw new PdfUAConformanceException(PdfUAExceptionMessageConstants.OCG_SHALL_NOT_CONTAIN_AS_ENTRY);
        }
        if (!(dict.get(PdfName.Name) instanceof PdfString) || (((PdfString)dict.get(PdfName.Name)).toString().isEmpty())) {
            throw new PdfUAConformanceException(PdfUAExceptionMessageConstants.NAME_ENTRY_IS_MISSING_OR_EMPTY_IN_OCG);
        }
    }

    private void checkFonts(Collection<PdfFont> fontsInDocument) {
        Set<String> fontNamesThatAreNotEmbedded = new HashSet<>();
        for (PdfFont font : fontsInDocument) {
            if (!font.isEmbedded()) {
                fontNamesThatAreNotEmbedded.add(font.getFontProgram().getFontNames().getFontName());
            }
        }
        if (!fontNamesThatAreNotEmbedded.isEmpty()) {
            throw new PdfUAConformanceException(
                    MessageFormatUtil.format(
                            PdfUAExceptionMessageConstants.FONT_SHOULD_BE_EMBEDDED,
                            String.join(", ", fontNamesThatAreNotEmbedded)
                    ));
        }
    }

    private void checkCrypto(PdfDictionary encryptionDictionary) {
        if (encryptionDictionary != null) {
            if (!(encryptionDictionary.get(PdfName.P) instanceof PdfNumber)) {
                throw new PdfUAConformanceException(
                        PdfUAExceptionMessageConstants.P_VALUE_IS_ABSENT_IN_ENCRYPTION_DICTIONARY);
            }
            int permissions = ((PdfNumber) encryptionDictionary.get(PdfName.P)).intValue();
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

    private static final class UaCharacterChecker implements FontCheckUtil.CharacterChecker {
        @Override
        public boolean check(int ch, PdfFont font) {
            if (font.containsGlyph(ch)) {
                return !font.getGlyph(ch).hasValidUnicode();
            } else {
                return true;
            }
        }
    }
}
