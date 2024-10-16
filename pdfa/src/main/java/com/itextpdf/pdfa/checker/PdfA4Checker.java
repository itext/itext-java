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
package com.itextpdf.pdfa.checker;

import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.io.colors.IccProfile;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.pdf.PdfAConformance;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfCatalog;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.canvas.CanvasGraphicsState;
import com.itextpdf.kernel.pdf.colorspace.PdfCieBasedCs;
import com.itextpdf.kernel.pdf.colorspace.PdfColorSpace;
import com.itextpdf.kernel.pdf.colorspace.PdfSpecialCs;
import com.itextpdf.kernel.xmp.XMPConst;
import com.itextpdf.kernel.xmp.XMPException;
import com.itextpdf.kernel.xmp.XMPMeta;
import com.itextpdf.kernel.xmp.XMPMetaFactory;
import com.itextpdf.kernel.xmp.properties.XMPProperty;
import com.itextpdf.pdfa.PdfAXMPUtil;
import com.itextpdf.pdfa.exceptions.PdfAConformanceException;
import com.itextpdf.pdfa.exceptions.PdfaExceptionMessageConstant;
import com.itextpdf.pdfa.logs.PdfAConformanceLogMessageConstant;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * PdfA4Checker defines the requirements of the PDF/A-4 standard and contains a
 * number of methods that override the implementations of its superclass
 * {@link PdfA3Checker}.
 * <p>
 * The specification implemented by this class is ISO 19005-4
 */
public class PdfA4Checker extends PdfA3Checker {
    private static final String CALRGB_COLOR_SPACE = "CalRGB";

    private static final Set<PdfName> forbiddenAnnotations4 = Collections
            .unmodifiableSet(new HashSet<>(Arrays.asList(
                    PdfName._3D,
                    PdfName.RichMedia,
                    PdfName.FileAttachment,
                    PdfName.Sound,
                    PdfName.Screen,
                    PdfName.Movie)));

    private static final Set<PdfName> forbiddenAnnotations4E = Collections
            .unmodifiableSet(new HashSet<>(Arrays.asList(
                    PdfName.FileAttachment,
                    PdfName.Sound,
                    PdfName.Screen,
                    PdfName.Movie)));

    private static final Set<PdfName> forbiddenAnnotations4F = Collections
            .unmodifiableSet(new HashSet<>(Arrays.asList(
                    PdfName._3D,
                    PdfName.RichMedia,
                    PdfName.Sound,
                    PdfName.Screen,
                    PdfName.Movie)));

    private static final Set<PdfName> apLessAnnotations = Collections.unmodifiableSet(
            new HashSet<>(Arrays.asList(PdfName.Popup, PdfName.Link, PdfName.Projection)));

    private static final Set<PdfName> allowedBlendModes4 = Collections
            .unmodifiableSet(new HashSet<>(Arrays.asList(
                    PdfName.Normal,
                    PdfName.Multiply,
                    PdfName.Screen,
                    PdfName.Overlay,
                    PdfName.Darken,
                    PdfName.Lighten,
                    PdfName.ColorDodge,
                    PdfName.ColorBurn,
                    PdfName.HardLight,
                    PdfName.SoftLight,
                    PdfName.Difference,
                    PdfName.Exclusion,
                    PdfName.Hue,
                    PdfName.Saturation,
                    PdfName.Color,
                    PdfName.Luminosity)));

    private static final String TRANSPARENCY_ERROR_MESSAGE =
            PdfaExceptionMessageConstant.THE_DOCUMENT_AND_THE_PAGE_DO_NOT_CONTAIN_A_PDFA_OUTPUTINTENT_BUT_PAGE_CONTAINS_TRANSPARENCY_AND_DOES_NOT_CONTAIN_BLENDING_COLOR_SPACE;

    private static final Logger LOGGER = LoggerFactory.getLogger(PdfAChecker.class);


    private static final Set<PdfName> forbiddenActionsE = Collections
            .unmodifiableSet(new HashSet<>(Arrays.asList(
                    PdfName.Launch,
                    PdfName.Sound,
                    PdfName.Movie,
                    PdfName.ResetForm,
                    PdfName.ImportData,
                    PdfName.JavaScript,
                    PdfName.Hide,
                    PdfName.Rendition,
                    PdfName.Trans
            )));
    private static final Set<PdfName> allowedEntriesInAAWhenNonWidget = Collections
            .unmodifiableSet(new HashSet<>(Arrays.asList(
                    PdfName.E,
                    PdfName.X,
                    PdfName.D,
                    PdfName.U,
                    PdfName.Fo,
                    PdfName.Bl
            )));

    // Map pdfObject using CMYK - list of CMYK icc profile streams
    private Map<PdfObject, List<PdfStream>> iccBasedCmykObjects = new HashMap<>();
    /**
     * Creates a PdfA4Checker with the required conformance
     *
     * @param aConformance the required conformance
     */
    public PdfA4Checker(PdfAConformance aConformance) {
        super(aConformance);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void checkColorSpace(PdfColorSpace colorSpace, PdfObject pdfObject, PdfDictionary currentColorSpaces,
            boolean checkAlternate, Boolean fill) {
        if (colorSpace instanceof PdfCieBasedCs.IccBased) {
            // 6.2.4.2: An ICCBased colour space shall not be used where the profile is a CMYK destination profile and is
            // identical to that in the current PDF/A OutputIntent or the current transparency blending colorspace.
            PdfStream iccStream = ((PdfArray) colorSpace.getPdfObject()).getAsStream(1);
            byte[] iccBytes = iccStream.getBytes();
            // If not CMYK - we don't care
            if (ICC_COLOR_SPACE_CMYK.equals(IccProfile.getIccColorSpaceName(iccBytes))) {
                if (!iccBasedCmykObjects.containsKey(pdfObject)) {
                    iccBasedCmykObjects.put(pdfObject, new ArrayList<>());
                }
                iccBasedCmykObjects.get(pdfObject).add(iccStream);
            }
        }

        super.checkColorSpace(colorSpace, pdfObject, currentColorSpaces,checkAlternate, fill);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void checkPageColorsUsages(PdfDictionary pageDict, PdfDictionary pageResources) {
        // Get page pdf/a output intent output profile
        PdfStream pageDestOutputProfile = null;
        PdfArray outputIntents = pageDict.getAsArray(PdfName.OutputIntents);
        if (outputIntents != null) {
            PdfDictionary pdfAPageOutputIntent = getPdfAOutputIntent(outputIntents);
            if (pdfAPageOutputIntent != null) {
                pageDestOutputProfile = pdfAPageOutputIntent.getAsStream(PdfName.DestOutputProfile);
            }
        }

        if (pageDestOutputProfile == null) {
            pageDestOutputProfile = pdfAOutputIntentDestProfile;
        }

        // Page blending colorspace should be taken into account while checking objects using device dependent colors
        PdfColorSpace pageTransparencyBlendingCS = getDeviceIndependentTransparencyBlendingCSIfRbgOrCmykBased(pageDict);

        // We don't know on which pages these objects are so that we have to go inside anyway
        if (!rgbUsedObjects.isEmpty() || !cmykUsedObjects.isEmpty() || !grayUsedObjects.isEmpty() ||
                !iccBasedCmykObjects.isEmpty()) {
            checkPageContentsForColorUsages(pageDict, pageDestOutputProfile, pageTransparencyBlendingCS);
            checkAnnotationsForColorUsages(pageDict.getAsArray(PdfName.Annots), pageDestOutputProfile,
                    pageTransparencyBlendingCS);
            checkResourcesForColorUsages(pageResources, pageDestOutputProfile, pageTransparencyBlendingCS);
        }
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
                throw new PdfAConformanceException(
                        PdfaExceptionMessageConstant.DOCUMENT_INFO_DICTIONARY_SHALL_ONLY_CONTAIN_MOD_DATE);
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
                    MessageFormatUtil.format(
                            PdfaExceptionMessageConstant.THE_FILE_HEADER_SHALL_CONTAIN_RIGHT_PDF_VERSION, "2"));
        }
        PdfDictionary trailer = catalog.getDocument().getTrailer();
        if (trailer.get(PdfName.Info) != null) {
            if (catalog.getPdfObject().get(PdfName.PieceInfo) == null) {
                throw new PdfAConformanceException(
                        PdfaExceptionMessageConstant.DOCUMENT_SHALL_NOT_CONTAIN_INFO_UNLESS_THERE_IS_PIECE_INFO);
            }
        }

        if ("F".equals(conformance.getLevel())) {
            if (!catalog.nameTreeContainsKey(PdfName.EmbeddedFiles)) {
                throw new PdfAConformanceException(
                        PdfaExceptionMessageConstant.NAME_DICTIONARY_SHALL_CONTAIN_EMBEDDED_FILES_KEY);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void checkPageObject(PdfDictionary pageDict, PdfDictionary pageResources) {
        super.checkPageObject(pageDict, pageResources);
        PdfStream xmpMeta = pageDict.getAsStream(PdfName.Metadata);
        if (xmpMeta != null && !PdfAXMPUtil.isUtf8(xmpMeta.getBytes())) {
            throw new PdfAConformanceException(PdfaExceptionMessageConstant.INVALID_XMP_METADATA_ENCODING);
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
                    MessageFormatUtil.format(
                            PdfaExceptionMessageConstant.THE_CATALOG_VERSION_SHALL_CONTAIN_RIGHT_PDF_VERSION, "2"));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void checkFileSpec(PdfDictionary fileSpec) {
        if (fileSpec.getAsName(PdfName.AFRelationship) == null) {
            throw new PdfAConformanceException(
                    PdfaExceptionMessageConstant.FILE_SPECIFICATION_DICTIONARY_SHALL_CONTAIN_AFRELATIONSHIP_KEY);
        }
        if (!fileSpec.containsKey(PdfName.F) || !fileSpec.containsKey(PdfName.UF)) {
            throw new PdfAConformanceException(
                    PdfaExceptionMessageConstant.FILE_SPECIFICATION_DICTIONARY_SHALL_CONTAIN_F_KEY_AND_UF_KEY);
        }
        if (!fileSpec.containsKey(PdfName.Desc)) {
            LOGGER.warn(PdfAConformanceLogMessageConstant.FILE_SPECIFICATION_DICTIONARY_SHOULD_CONTAIN_DESC_KEY);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void checkPageTransparency(PdfDictionary pageDict, PdfDictionary pageResources) {
        // Get page pdf/a output intent
        PdfDictionary pdfAPageOutputIntent = null;
        PdfArray outputIntents = pageDict.getAsArray(PdfName.OutputIntents);
        if (outputIntents != null) {
            pdfAPageOutputIntent = getPdfAOutputIntent(outputIntents);
        }
        if (pdfAOutputIntentColorSpace == null && pdfAPageOutputIntent == null
                && !transparencyObjects.isEmpty()
                && (pageDict.getAsDictionary(PdfName.Group) == null
                || pageDict.getAsDictionary(PdfName.Group).get(PdfName.CS) == null)) {
            checkContentsForTransparency(pageDict);
            checkAnnotationsForTransparency(pageDict.getAsArray(PdfName.Annots));
            checkResourcesForTransparency(pageResources, new HashSet<PdfObject>());
        }
    }

    /**
     * Check the conformity of the AA dictionary on catalog level.
     *
     * @param dict the catalog dictionary
     */
    @Override
    protected void checkCatalogAAConformance(PdfDictionary dict) {
        final PdfDictionary aa = dict.getAsDictionary(PdfName.AA);
        if (aa != null && hasAAIllegalEntries(aa)) {
            throw new PdfAConformanceException(
                    PdfaExceptionMessageConstant.CATALOG_AA_DICTIONARY_SHALL_CONTAIN_ONLY_ALLOWED_KEYS);
        }
    }


    /**
     * Check the conformity of the AA dictionary on catalog level.
     *
     * @param dict the catalog dictionary
     */
    @Override
    protected void checkPageAAConformance(PdfDictionary dict) {
        final PdfDictionary aa = dict.getAsDictionary(PdfName.AA);
        if (aa != null && hasAAIllegalEntries(aa)) {
            throw new PdfAConformanceException(
                    PdfaExceptionMessageConstant.PAGE_AA_DICTIONARY_SHALL_CONTAIN_ONLY_ALLOWED_KEYS);
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

    /**
     * {@inheritDoc}
     */
    @Override
    public void checkSignatureType(boolean isCAdES) {
        if (!isCAdES) {
            throw new PdfAConformanceException(PdfaExceptionMessageConstant.SIGNATURE_SHALL_CONFORM_TO_ONE_OF_THE_PADES_PROFILE);
        }
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

    @Override
    public void checkExtGState(CanvasGraphicsState extGState, PdfStream contentStream) {
        super.checkExtGState(extGState, contentStream);
        if (extGState.getHalftone() instanceof PdfDictionary) {
            PdfDictionary halftoneDict = (PdfDictionary) extGState.getHalftone();
            if (halftoneDict.containsKey(PdfName.TransferFunction)) {
                throw new PdfAConformanceException(PdfaExceptionMessageConstant.ALL_HALFTONES_CONTAINING_TRANSFER_FUNCTION_SHALL_HAVE_HALFTONETYPE_5);
            }
            int halftoneType = halftoneDict.getAsInt(PdfName.HalftoneType).intValue();
            if (halftoneType == 5) {
                for (Map.Entry<PdfName, PdfObject> entry : halftoneDict.entrySet()) {
                    //see ISO_32000_2;2020 table 132
                    if (PdfName.Type.equals(entry.getKey()) || PdfName.HalftoneType.equals(entry.getKey()) || PdfName.HalftoneName.equals(entry.getKey())) {
                        continue;
                    }
                    if (entry.getValue() instanceof PdfDictionary && isCMYKColorant(entry.getKey()) && entry.getValue() instanceof PdfDictionary && ((PdfDictionary)entry.getValue()).containsKey(PdfName.TransferFunction)) {
                        throw new PdfAConformanceException(PdfaExceptionMessageConstant.ALL_HALFTONES_CONTAINING_TRANSFER_FUNCTION_SHALL_HAVE_HALFTONETYPE_5);
                    }
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void checkFormXObject(PdfStream form, PdfStream contentStream) {
        if (isAlreadyChecked(form)) {
            return;
        }
        if (form.containsKey(PdfName.OPI)) {
            throw new PdfAConformanceException(PdfaExceptionMessageConstant.A_FORM_XOBJECT_DICTIONARY_SHALL_NOT_CONTAIN_OPI_KEY);
        }
        if (form.containsKey(PdfName.Ref)) {
            throw new PdfAConformanceException(PdfaExceptionMessageConstant.A_FORM_XOBJECT_DICTIONARY_SHALL_NOT_CONTAIN_REF_KEY);
        }
        checkTransparencyGroup(form, contentStream);
        checkResources(form.getAsDictionary(PdfName.Resources), form);
        checkContentStream(form);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void checkAnnotation(PdfDictionary annotDic) {
        super.checkAnnotation(annotDic);

        // Extra check for blending mode
        PdfName blendMode = annotDic.getAsName(PdfName.BM);
        if (blendMode != null && !allowedBlendModes4.contains(blendMode)) {
            throw new PdfAConformanceException(
                    PdfaExceptionMessageConstant.ONLY_STANDARD_BLEND_MODES_SHALL_BE_USED_FOR_THE_VALUE_OF_THE_BM_KEY_IN_A_GRAPHIC_STATE_AND_ANNOTATION_DICTIONARY);
        }

        // And then treat the annotation as an object with transparency
        if (blendMode != null && !PdfName.Normal.equals(blendMode)) {
            transparencyObjects.add(annotDic);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Set<PdfName> getForbiddenAnnotations() {
        if ("E".equals(conformance.getLevel())) {
            return forbiddenAnnotations4E;
        } else if ("F".equals(conformance.getLevel())) {
            return forbiddenAnnotations4F;
        }
        return forbiddenAnnotations4;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Set<PdfName> getAppearanceLessAnnotations() {
        return apLessAnnotations;
    }


    /**
     * Check the conformity of the AA dictionary on widget level.
     *
     * @param dict the widget dictionary
     */
    protected void checkWidgetAAConformance(PdfDictionary dict) {
        if (!PdfName.Widget.equals(dict.getAsName(PdfName.Subtype)) && dict.containsKey(PdfName.AA)) {
            final PdfObject additionalActions = dict.get(PdfName.AA);
            if (additionalActions.isDictionary() && hasAAIllegalEntries((PdfDictionary) additionalActions)) {
                throw new PdfAConformanceException(
                        PdfaExceptionMessageConstant.ANNOTATION_AA_DICTIONARY_SHALL_CONTAIN_ONLY_ALLOWED_KEYS);
            }
        }
    }

    /**
     * @param catalog the catalog {@link PdfDictionary} to check
     */
    @Override
    protected void checkMetaData(PdfDictionary catalog) {
        super.checkMetaData(catalog);
        try {
            final PdfStream xmpMetadata = catalog.getAsStream(PdfName.Metadata);
            byte[] bytes = xmpMetadata.getBytes();
            isValidEncoding(bytes);
            checkPacketHeader(bytes);
            final XMPMeta meta = XMPMetaFactory.parse(new ByteArrayInputStream(bytes));
            checkVersionIdentification(meta);
            checkFileProvenanceSpec(meta);
        } catch (XMPException ex) {
            throw new PdfException(ex);
        }
    }

    /**
     *  {@inheritDoc}
     */
    @Override
    protected void checkOutputIntents(PdfDictionary catalog) {
        super.checkOutputIntents(catalog);
        final PdfArray outputIntents = catalog.getAsArray(PdfName.OutputIntents);
        if (outputIntents == null) {
            return;
        }
        for (int i = 0; i < outputIntents.size(); ++i) {
            final PdfDictionary outputIntent = outputIntents.getAsDictionary(i);
            if (outputIntent.containsKey(new PdfName("DestOutputProfileRef"))) {
                throw new PdfAConformanceException(
                        PdfaExceptionMessageConstant.OUTPUTINTENT_SHALL_NOT_CONTAIN_DESTOUTPUTPROFILEREF_KEY);
            }
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void checkAnnotationAgainstActions(PdfDictionary annotDic) {
        if (PdfName.Widget.equals(annotDic.getAsName(PdfName.Subtype)) && annotDic.containsKey(PdfName.A)) {
            throw new PdfAConformanceException(
                    PdfaExceptionMessageConstant.WIDGET_ANNOTATION_DICTIONARY_OR_FIELD_DICTIONARY_SHALL_NOT_INCLUDE_A_ENTRY);
        }
        checkWidgetAAConformance(annotDic);
    }

    private static boolean hasAAIllegalEntries(PdfDictionary aa) {
        for (final PdfName key : aa.keySet()) {
            if (!allowedEntriesInAAWhenNonWidget.contains(key)) {
                return true;
            }
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Set<PdfName> getForbiddenActions() {
        if ("E".equals(conformance.getLevel())) {
            return forbiddenActionsE;
        }
        return super.getForbiddenActions();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void checkContentConfigurationDictAgainstAsKey(PdfDictionary config) {
        // Do nothing because in PDF/A-4 AS key may appear in any optional content configuration dictionary.
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getTransparencyErrorMessage() {
        return TRANSPARENCY_ERROR_MESSAGE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void checkBlendMode(PdfName blendMode) {
        if (!allowedBlendModes4.contains(blendMode)) {
            throw new PdfAConformanceException(
                    PdfaExceptionMessageConstant.ONLY_STANDARD_BLEND_MODES_SHALL_BE_USED_FOR_THE_VALUE_OF_THE_BM_KEY_IN_AN_EXTENDED_GRAPHIC_STATE_DICTIONARY);
        }
    }

    /**
     * {@inheritDoc}
     */
    protected int getMaxNameLength() {
        return Integer.MAX_VALUE;
    }

    private static void isValidEncoding(byte[] data) {
        if (!PdfAXMPUtil.isUtf8(data)) {
            throw new PdfAConformanceException(PdfaExceptionMessageConstant.INVALID_XMP_METADATA_ENCODING);
        }
    }

    private static boolean isValidXmpConformance(String value) {
        if (value == null) {
            return false;
        }
        if (value.length() != 1) {
            return false;
        }
        return "F".equals(value) || "E".equals(value);
    }

    private static boolean isValidXmpRevision(String value) {
        if (value == null) {
            return false;
        }
        if (value.length() != 4) {
            return false;
        }
        for (final char c : value.toCharArray()) {
            if (!Character.isDigit(c)) {
                return false;
            }
        }
        return true;
    }

    private void checkPacketHeader(byte[] meta) {
        if (meta == null) {
            return;
        }
        final String metAsStr = new String(meta);
        final String regex = "<\\?xpacket.*encoding|bytes.*\\?>";
        final Pattern pattern = Pattern.compile(regex);
        if (pattern.matcher(metAsStr).find()) {
            throw new PdfAConformanceException(
                    PdfaExceptionMessageConstant
                            .XMP_METADATA_HEADER_PACKET_MAY_NOT_CONTAIN_BYTES_OR_ENCODING_ATTRIBUTE);
        }
    }

    private void checkFileProvenanceSpec(XMPMeta meta) {
        try {
            XMPProperty history = meta.getProperty(XMPConst.NS_XMP_MM, XMPConst.HISTORY);
            if (history == null) {
                return;
            }
            if (!history.getOptions().isArray()) {
                return;
            }
            final int amountOfEntries = meta.countArrayItems(XMPConst.NS_XMP_MM, XMPConst.HISTORY);
            for (int i = 0; i < amountOfEntries; i++) {
                int nameSpaceIndex = i + 1;
                if (!meta.doesPropertyExist(XMPConst.NS_XMP_MM,
                        XMPConst.HISTORY + "[" + nameSpaceIndex + "]/stEvt:action")) {
                    throw new PdfAConformanceException(MessageFormatUtil.format(
                            PdfaExceptionMessageConstant.XMP_METADATA_HISTORY_ENTRY_SHALL_CONTAIN_KEY,
                            "stEvt:action"));
                }
                if (!meta.doesPropertyExist(XMPConst.NS_XMP_MM,
                        XMPConst.HISTORY + "[" + nameSpaceIndex + "]/stEvt:when")) {
                    throw new PdfAConformanceException(MessageFormatUtil.format(
                            PdfaExceptionMessageConstant.XMP_METADATA_HISTORY_ENTRY_SHALL_CONTAIN_KEY,
                            "stEvt:when"));
                }
            }
        } catch (XMPException e) {
            throw new PdfException(e);
        }
    }

    private void checkVersionIdentification(XMPMeta meta) {
        try {
            XMPProperty prop = meta.getProperty(XMPConst.NS_PDFA_ID, XMPConst.PART);
            if (prop == null || !getAConformance().getPart().equals(prop.getValue())) {
                throw new PdfAConformanceException(MessageFormatUtil.format(
                        PdfaExceptionMessageConstant.XMP_METADATA_HEADER_SHALL_CONTAIN_VERSION_IDENTIFIER_PART,
                        getAConformance().getPart()));
            }
        } catch (XMPException e) {
            throw new PdfAConformanceException(MessageFormatUtil.format(
                    PdfaExceptionMessageConstant.XMP_METADATA_HEADER_SHALL_CONTAIN_VERSION_IDENTIFIER_PART,
                    getAConformance().getPart()));
        }

        try {
            XMPProperty prop = meta.getProperty(XMPConst.NS_PDFA_ID, XMPConst.REV);
            if (prop == null || !isValidXmpRevision(prop.getValue())) {
                throw new PdfAConformanceException(
                        PdfaExceptionMessageConstant.XMP_METADATA_HEADER_SHALL_CONTAIN_VERSION_IDENTIFIER_REV);
            }
        } catch (XMPException e) {
            throw new PdfAConformanceException(
                    PdfaExceptionMessageConstant.XMP_METADATA_HEADER_SHALL_CONTAIN_VERSION_IDENTIFIER_REV);
        }

        try {
            XMPProperty prop = meta.getProperty(XMPConst.NS_PDFA_ID, XMPConst.CONFORMANCE);
            if (prop != null && !isValidXmpConformance(prop.getValue())) {
                throw new PdfAConformanceException(
                        PdfaExceptionMessageConstant.XMP_METADATA_HEADER_SHALL_CONTAIN_VERSION_IDENTIFIER_CONFORMANCE);
            }
        } catch (XMPException e) {
            // ignored because it is not required
        }
    }

    private boolean isCMYKColorant(PdfName colourant) {
        return PdfName.Cyan.equals(colourant) || PdfName.Magenta.equals(colourant)
                || PdfName.Yellow.equals(colourant) || PdfName.Black.equals(colourant);
    }

    private void checkPageContentsForColorUsages(PdfDictionary pageDict, PdfStream pageIntentProfile,
            PdfColorSpace pageTransparencyBlendingCS) {
        PdfStream contentStream = pageDict.getAsStream(PdfName.Contents);
        if (contentStream != null) {
            checkContentForColorUsages(contentStream, pageIntentProfile, pageTransparencyBlendingCS);
        } else {
            PdfArray contentSteamArray = pageDict.getAsArray(PdfName.Contents);
            if (contentSteamArray != null) {
                for (int i = 0; i < contentSteamArray.size(); i++) {
                    checkContentForColorUsages(contentSteamArray.get(i), pageIntentProfile, pageTransparencyBlendingCS);
                }
            }
        }
    }

    private void checkAnnotationsForColorUsages(PdfArray annotations, PdfStream pageIntentProfile,
            PdfColorSpace pageTransparencyBlendingCS) {
        if (annotations == null) {
            return;
        }
        for (int i = 0; i < annotations.size(); ++i) {
            PdfDictionary annot = annotations.getAsDictionary(i);
            PdfDictionary ap = annot.getAsDictionary(PdfName.AP);
            if (ap != null) {
                checkAppearanceStreamForColorUsages(ap, pageIntentProfile, pageTransparencyBlendingCS);
            }
        }
    }

    private void checkAppearanceStreamForColorUsages(PdfDictionary ap, PdfStream pageIntentProfile,
            PdfColorSpace pageTransparencyBlendingCS) {
        checkContentForColorUsages(ap, pageIntentProfile, pageTransparencyBlendingCS);
        for (final PdfObject val : ap.values()) {
            checkContentForColorUsages(val, pageIntentProfile, pageTransparencyBlendingCS);
            if (val.isDictionary()) {
                checkAppearanceStreamForColorUsages((PdfDictionary) val, pageIntentProfile, pageTransparencyBlendingCS);
            } else if (val.isStream()) {
                checkObjectWithResourcesForColorUsages(val, pageIntentProfile, pageTransparencyBlendingCS);
            }
        }
    }

    private void checkObjectWithResourcesForColorUsages(PdfObject objectWithResources, PdfStream pageIntentProfile,
            PdfColorSpace pageTransparencyBlendingCS) {
        checkContentForColorUsages(objectWithResources, pageIntentProfile, pageTransparencyBlendingCS);

        if (objectWithResources instanceof PdfDictionary) {
            checkResourcesForColorUsages(((PdfDictionary) objectWithResources).getAsDictionary(PdfName.Resources),
                    pageIntentProfile, pageTransparencyBlendingCS);
        }
    }

    private void checkResourcesForColorUsages(PdfDictionary resources, PdfStream pageIntentProfile,
            PdfColorSpace pageTransparencyBlendingCS) {
        if (resources != null) {
            checkSingleResourceTypeForColorUsages(resources.getAsDictionary(PdfName.XObject), pageIntentProfile,
                    pageTransparencyBlendingCS);
            checkSingleResourceTypeForColorUsages(resources.getAsDictionary(PdfName.Pattern), pageIntentProfile,
                    pageTransparencyBlendingCS);
        }
    }

    private void checkSingleResourceTypeForColorUsages(PdfDictionary singleResourceDict, PdfStream pageIntentProfile,
            PdfColorSpace pageTransparencyBlendingCS) {
        if (singleResourceDict != null) {
            for (PdfObject resource : singleResourceDict.values()) {
                checkObjectWithResourcesForColorUsages(resource, pageIntentProfile, pageTransparencyBlendingCS);
            }
        }
    }

    private void checkContentForColorUsages(PdfObject pdfObject, PdfStream pageIntentProfile,
            PdfColorSpace pageTransparencyBlendingCS) {
        String pageIntentCSType = pageIntentProfile == null ? null :
                IccProfile.getIccColorSpaceName(pageIntentProfile.getBytes());

        PdfColorSpace currentTransparencyBlendingCS = pdfObject instanceof PdfDictionary ?
                getDeviceIndependentTransparencyBlendingCSIfRbgOrCmykBased((PdfDictionary)pdfObject) : null;
        // Step 1 - 6.2.4.3: check if device dependent color in the object is allowed
        // Current output intent, page blending colorspace and object blending colorspace should be taken into account
        // Step 1.1 - check if any excuse exists
        if (pageIntentCSType == null && pageTransparencyBlendingCS == null && currentTransparencyBlendingCS == null) {
            if (rgbUsedObjects.contains(pdfObject)) {
                throw new PdfAConformanceException(
                        PdfaExceptionMessageConstant.DEVICERGB_SHALL_ONLY_BE_USED_IF_CURRENT_RGB_PDFA_OUTPUT_INTENT_OR_DEFAULTRGB_IN_USAGE_CONTEXT);
            } else if (cmykUsedObjects.contains(pdfObject)) {
                throw new PdfAConformanceException(
                        PdfaExceptionMessageConstant.DEVICECMYK_SHALL_ONLY_BE_USED_IF_CURRENT_CMYK_PDFA_OUTPUT_INTENT_OR_DEFAULTCMYK_IN_USAGE_CONTEXT);
            }
        }
        // pageTransparencyBlendingCS currentTransparencyBlendingCS don't help for DeviceGray
        if (grayUsedObjects.contains(pdfObject) && pageIntentCSType == null) {
            throw new PdfAConformanceException(
                    PdfaExceptionMessageConstant.DEVICEGRAY_SHALL_ONLY_BE_USED_IF_CURRENT_PDFA_OUTPUT_INTENT_OR_DEFAULTGRAY_IN_USAGE_CONTEXT);
        }

        String pageTransparencyBlendingCSType = getColorspaceTypeIfIccBasedOrCalRgb(pageTransparencyBlendingCS);
        String currentTransparencyBlendingCSType = getColorspaceTypeIfIccBasedOrCalRgb(currentTransparencyBlendingCS);
        // Step 1.2 - check for RGB
        if (rgbUsedObjects.contains(pdfObject) && !ICC_COLOR_SPACE_RGB.equals(pageIntentCSType) &&
                !ICC_COLOR_SPACE_RGB.equals(pageTransparencyBlendingCSType) &&
                !ICC_COLOR_SPACE_RGB.equals(currentTransparencyBlendingCSType) &&
                !CALRGB_COLOR_SPACE.equals(pageTransparencyBlendingCSType) &&
                !CALRGB_COLOR_SPACE.equals(currentTransparencyBlendingCSType)) {
            throw new PdfAConformanceException(
                    PdfaExceptionMessageConstant.DEVICERGB_SHALL_ONLY_BE_USED_IF_CURRENT_RGB_PDFA_OUTPUT_INTENT_OR_DEFAULTRGB_IN_USAGE_CONTEXT);
        }

        // Step 1.3 - check for CMYK
        if (cmykUsedObjects.contains(pdfObject) && !ICC_COLOR_SPACE_CMYK.equals(pageIntentCSType) &&
                !ICC_COLOR_SPACE_CMYK.equals(pageTransparencyBlendingCSType) &&
                !ICC_COLOR_SPACE_CMYK.equals(currentTransparencyBlendingCSType)) {
            throw new PdfAConformanceException(
                    PdfaExceptionMessageConstant.DEVICECMYK_SHALL_ONLY_BE_USED_IF_CURRENT_CMYK_PDFA_OUTPUT_INTENT_OR_DEFAULTCMYK_IN_USAGE_CONTEXT);
        }

        // Step 2 - 6.2.4.2: An ICCBased colour space shall not be used where the profile is a CMYK destination profile and is
        // identical to that in the current PDF/A OutputIntent or the current transparency blending colorspace.
        List<PdfStream> currentICCBasedProfiles = iccBasedCmykObjects.get(pdfObject);
        if (currentICCBasedProfiles == null) {
            return;
        }
        for (PdfStream currentICCBasedProfile : currentICCBasedProfiles) {
            throwIfIdenticalProfiles(currentICCBasedProfile, pageIntentProfile);
            if (ICC_COLOR_SPACE_CMYK.equals(currentTransparencyBlendingCSType)) {
                PdfStream iccStream = ((PdfArray) currentTransparencyBlendingCS.getPdfObject()).getAsStream(1);
                throwIfIdenticalProfiles(currentICCBasedProfile, iccStream);
            }
            if (ICC_COLOR_SPACE_CMYK.equals(pageTransparencyBlendingCSType)) {
                PdfStream iccStream = ((PdfArray) pageTransparencyBlendingCS.getPdfObject()).getAsStream(1);
                throwIfIdenticalProfiles(currentICCBasedProfile, iccStream);
            }
        }
    }

    private static void throwIfIdenticalProfiles(PdfStream iccBasedProfile1, PdfStream iccBasedProfile2) {
        if (iccBasedProfile1 != null && iccBasedProfile2 != null &&
                (iccBasedProfile1.equals(iccBasedProfile2) ||
                        Arrays.equals(iccBasedProfile1.getBytes(), iccBasedProfile2.getBytes()))) {
            throw new PdfAConformanceException(
                    PdfaExceptionMessageConstant.ICCBASED_COLOUR_SPACE_SHALL_NOT_BE_USED_IF_IT_IS_CMYK_AND_IS_IDENTICAL_TO_CURRENT_PROFILE);
        }
    }

    private static String getColorspaceTypeIfIccBasedOrCalRgb(PdfColorSpace colorspace) {
        if (colorspace instanceof PdfCieBasedCs.CalRgb) {
            return CALRGB_COLOR_SPACE;
        }

        if (colorspace instanceof PdfCieBasedCs.IccBased) {
            // 6.2.4.2: An ICCBased colour space shall not be used where the profile is a CMYK destination profile and is
            // identical to that in the current PDF/A OutputIntent or the current transparency blending colorspace.
            PdfStream iccStream = ((PdfArray) colorspace.getPdfObject()).getAsStream(1);
            return IccProfile.getIccColorSpaceName(iccStream.getBytes());
        }

        return null;
    }

    private static PdfColorSpace getDeviceIndependentTransparencyBlendingCSIfRbgOrCmykBased(PdfDictionary pageDict) {
        if (!isContainsTransparencyGroup(pageDict)) {
            return null;
        }

        PdfObject cs = pageDict.getAsDictionary(PdfName.Group).get(PdfName.CS);
        if (cs == null) {
            return null;
        }

        PdfColorSpace transparencyBlendingCS = PdfColorSpace.makeColorSpace(cs);
        if (transparencyBlendingCS instanceof PdfCieBasedCs.CalRgb ||
                transparencyBlendingCS instanceof PdfCieBasedCs.IccBased) {
            // Do not take others into account
            return transparencyBlendingCS;
        }

        return null;
    }
}
