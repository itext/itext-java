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
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfCatalog;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.colorspace.PdfSpecialCs;
import com.itextpdf.pdfa.exceptions.PdfAConformanceException;
import com.itextpdf.pdfa.exceptions.PdfaExceptionMessageConstant;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


/**
 * PdfA4Checker defines the requirements of the PDF/A-4 standard and contains a
 * number of methods that override the implementations of its superclass
 * {@link PdfA3Checker}.
 * <p>
 * The specification implemented by this class is ISO 19005-4
 */
public class PdfA4Checker extends PdfA3Checker {
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
                && transparencyObjects.size() > 0
                && (pageDict.getAsDictionary(PdfName.Group) == null || pageDict.getAsDictionary(PdfName.Group).get(PdfName.CS) == null)) {
            checkContentsForTransparency(pageDict);
            checkAnnotationsForTransparency(pageDict.getAsArray(PdfName.Annots));
            checkResourcesForTransparency(pageResources, new HashSet<PdfObject>());
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

    /**
     * {@inheritDoc}
     */
    @Override
    protected void checkAnnotation(PdfDictionary annotDic) {
        super.checkAnnotation(annotDic);

        // Extra check for blending mode
        PdfName blendMode = annotDic.getAsName(PdfName.BM);
        if (blendMode != null && !allowedBlendModes4.contains(blendMode)) {
            throw new PdfAConformanceException(PdfaExceptionMessageConstant.ONLY_STANDARD_BLEND_MODES_SHALL_BE_USED_FOR_THE_VALUE_OF_THE_BM_KEY_IN_A_GRAPHIC_STATE_AND_ANNOTATION_DICTIONARY);
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
        if ("E".equals(conformanceLevel.getConformance())) {
            return forbiddenAnnotations4E;
        } else if ("F".equals(conformanceLevel.getConformance())) {
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
     * {@inheritDoc}
     */
    @Override
    protected void checkAnnotationAgainstActions(PdfDictionary annotDic) {
        if (PdfName.Widget.equals(annotDic.getAsName(PdfName.Subtype)) && annotDic.containsKey(PdfName.A)) {
            throw new PdfAConformanceException(
                    PdfaExceptionMessageConstant.WIDGET_ANNOTATION_DICTIONARY_OR_FIELD_DICTIONARY_SHALL_NOT_INCLUDE_A_ENTRY);
        }
        if (!PdfName.Widget.equals(annotDic.getAsName(PdfName.Subtype)) && annotDic.containsKey(PdfName.AA)) {
            throw new PdfAConformanceException(PdfAConformanceException.AN_ANNOTATION_DICTIONARY_SHALL_NOT_CONTAIN_AA_KEY);
        }
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
            throw new PdfAConformanceException(PdfAConformanceException.ONLY_STANDARD_BLEND_MODES_SHALL_BE_USED_FOR_THE_VALUE_OF_THE_BM_KEY_IN_AN_EXTENDED_GRAPHIC_STATE_DICTIONARY);
        }
    }
}
