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
package com.itextpdf.layout.renderer;

import com.itextpdf.io.LogMessageConstant;
import com.itextpdf.io.util.MessageFormatUtil;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNull;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.tagging.PdfNamespace;
import com.itextpdf.kernel.pdf.tagging.PdfStructureAttributes;
import com.itextpdf.kernel.pdf.tagging.StandardNamespaces;
import com.itextpdf.kernel.pdf.tagging.StandardRoles;
import com.itextpdf.kernel.pdf.tagutils.IRoleMappingResolver;
import com.itextpdf.kernel.pdf.tagutils.TagStructureContext;
import com.itextpdf.kernel.pdf.tagutils.TagTreePointer;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.Background;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.property.IListSymbolFactory;
import com.itextpdf.layout.property.ListNumberingType;
import com.itextpdf.layout.property.Property;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.TransparentColor;
import com.itextpdf.layout.property.Underline;
import com.itextpdf.layout.property.UnitValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Generates standard structure attributes for current tag
 * based on the layout element properties and renderer layout results.
 */
public class AccessibleAttributesApplier {

    public static PdfStructureAttributes getLayoutAttributes(AbstractRenderer renderer, TagTreePointer taggingPointer) {
        IRoleMappingResolver resolvedMapping = resolveMappingToStandard(taggingPointer);
        if (resolvedMapping == null) {
            return null;
        }

        String role = resolvedMapping.getRole();
        int tagType = AccessibleTypes.identifyType(role);
        PdfDictionary attributes = new PdfDictionary();
        attributes.put(PdfName.O, PdfName.Layout);

        //TODO WritingMode attribute applying when needed

        applyCommonLayoutAttributes(renderer, attributes);
        if (tagType == AccessibleTypes.BlockLevel) {
            applyBlockLevelLayoutAttributes(role, renderer, attributes);
        }
        if (tagType == AccessibleTypes.InlineLevel) {
            applyInlineLevelLayoutAttributes(renderer, attributes);
        }

        if (tagType == AccessibleTypes.Illustration) {
            applyIllustrationLayoutAttributes(renderer, attributes);
        }

        return attributes.size() > 1 ? new PdfStructureAttributes(attributes) : null;
    }

    public static PdfStructureAttributes getListAttributes(AbstractRenderer renderer, TagTreePointer taggingPointer) {
        IRoleMappingResolver resolvedMapping = null;
        resolvedMapping = resolveMappingToStandard(taggingPointer);
        if (resolvedMapping == null || !StandardRoles.L.equals(resolvedMapping.getRole())) {
            return null;
        }

        PdfDictionary attributes = new PdfDictionary();
        attributes.put(PdfName.O, PdfName.List);

        Object listSymbol = renderer.<Object>getProperty(Property.LIST_SYMBOL);

        boolean tagStructurePdf2 = isTagStructurePdf2(resolvedMapping.getNamespace());
        if (listSymbol instanceof ListNumberingType) {
            ListNumberingType numberingType = (ListNumberingType) listSymbol;
            attributes.put(PdfName.ListNumbering, transformNumberingTypeToName(numberingType, tagStructurePdf2));
        } else if (tagStructurePdf2) {
            if (listSymbol instanceof IListSymbolFactory) {
                attributes.put(PdfName.ListNumbering, PdfName.Ordered);
            } else {
                attributes.put(PdfName.ListNumbering, PdfName.Unordered);
            }
        }

        return attributes.size() > 1 ? new PdfStructureAttributes(attributes) : null;
    }

    public static PdfStructureAttributes getTableAttributes(AbstractRenderer renderer, TagTreePointer taggingPointer) {
        IRoleMappingResolver resolvedMapping = resolveMappingToStandard(taggingPointer);
        if (resolvedMapping == null ||
                !StandardRoles.TD.equals(resolvedMapping.getRole()) && !StandardRoles.TH.equals(resolvedMapping.getRole())) {
            return null;
        }

        PdfDictionary attributes = new PdfDictionary();
        attributes.put(PdfName.O, PdfName.Table);

        if (renderer.getModelElement() instanceof Cell) {
            Cell cell = (Cell) renderer.getModelElement();
            if (cell.getRowspan() != 1) {
                attributes.put(PdfName.RowSpan, new PdfNumber(cell.getRowspan()));
            }
            if (cell.getColspan() != 1) {
                attributes.put(PdfName.ColSpan, new PdfNumber(cell.getColspan()));
            }
        }

        return attributes.size() > 1 ? new PdfStructureAttributes(attributes) : null;
    }

    private static void applyCommonLayoutAttributes(AbstractRenderer renderer, PdfDictionary attributes) {
        Background background = renderer.<Background>getProperty(Property.BACKGROUND);
        if (background != null && background.getColor() instanceof DeviceRgb) {
            attributes.put(PdfName.BackgroundColor, new PdfArray(background.getColor().getColorValue()));
        }

        //TODO NOTE: applying border attributes for cells is temporarily turned off on purpose. Remove this 'if' in future.
        // The reason is that currently, we can't distinguish if all cells have same border style or not.
        // Therefore for every cell in every table we have to write the same border attributes, which creates lots of clutter.
        if (!(renderer.getModelElement() instanceof Cell)) {
            applyBorderAttributes(renderer, attributes);
        }
        applyPaddingAttribute(renderer, attributes);

        TransparentColor transparentColor = renderer.getPropertyAsTransparentColor(Property.FONT_COLOR);
        if (transparentColor != null && transparentColor.getColor() instanceof DeviceRgb) {
            attributes.put(PdfName.Color, new PdfArray(transparentColor.getColor().getColorValue()));
        }
    }

    private static void applyBlockLevelLayoutAttributes(String role, AbstractRenderer renderer, PdfDictionary attributes) {
        UnitValue[] margins = {renderer.getPropertyAsUnitValue(Property.MARGIN_TOP),
                renderer.getPropertyAsUnitValue(Property.MARGIN_BOTTOM),
                renderer.getPropertyAsUnitValue(Property.MARGIN_LEFT),
                renderer.getPropertyAsUnitValue(Property.MARGIN_RIGHT)};

        int[] marginsOrder = {0, 1, 2, 3}; //TODO set depending on writing direction

        UnitValue spaceBefore = margins[marginsOrder[0]];
        if (spaceBefore != null) {
            if (!spaceBefore.isPointValue()) {
                Logger logger = LoggerFactory.getLogger(AccessibleAttributesApplier.class);
                logger.error(MessageFormatUtil.format(LogMessageConstant.PROPERTY_IN_PERCENTS_NOT_SUPPORTED, Property.MARGIN_TOP));
            }
            if (0 != spaceBefore.getValue()) {
                attributes.put(PdfName.SpaceBefore, new PdfNumber(spaceBefore.getValue()));
            }
        }

        UnitValue spaceAfter = margins[marginsOrder[1]];
        if (spaceAfter != null) {
            if (!spaceAfter.isPointValue()) {
                Logger logger = LoggerFactory.getLogger(AccessibleAttributesApplier.class);
                logger.error(MessageFormatUtil.format(LogMessageConstant.PROPERTY_IN_PERCENTS_NOT_SUPPORTED, Property.MARGIN_BOTTOM));
            }
            if (0 != spaceAfter.getValue()) {
                attributes.put(PdfName.SpaceAfter, new PdfNumber(spaceAfter.getValue()));
            }
        }


        UnitValue startIndent = margins[marginsOrder[2]];
        if (startIndent != null) {
            if (!startIndent.isPointValue()) {
                Logger logger = LoggerFactory.getLogger(AccessibleAttributesApplier.class);
                logger.error(MessageFormatUtil.format(LogMessageConstant.PROPERTY_IN_PERCENTS_NOT_SUPPORTED, Property.MARGIN_LEFT));
            }
            if (0 != startIndent.getValue()) {
                attributes.put(PdfName.StartIndent, new PdfNumber(startIndent.getValue()));
            }
        }

        UnitValue endIndent = margins[marginsOrder[3]];
        if (endIndent != null) {
            if (!endIndent.isPointValue()) {
                Logger logger = LoggerFactory.getLogger(AccessibleAttributesApplier.class);
                logger.error(MessageFormatUtil.format(LogMessageConstant.PROPERTY_IN_PERCENTS_NOT_SUPPORTED, Property.MARGIN_RIGHT));
            }
            if (0 != endIndent.getValue()) {
                attributes.put(PdfName.EndIndent, new PdfNumber(endIndent.getValue()));
            }
        }

        Float firstLineIndent = renderer.getPropertyAsFloat(Property.FIRST_LINE_INDENT);
        if (firstLineIndent != null && firstLineIndent != 0) {
            attributes.put(PdfName.TextIndent, new PdfNumber((float) firstLineIndent));
        }

        TextAlignment textAlignment = renderer.<TextAlignment>getProperty(Property.TEXT_ALIGNMENT);
        if (textAlignment != null &&
                //for table cells there is an InlineAlign attribute (see below)
                (!role.equals(StandardRoles.TH) && !role.equals(StandardRoles.TD))) {
            attributes.put(PdfName.TextAlign, transformTextAlignmentValueToName(textAlignment));
        }

        // attributes are applied only on the first renderer
        if (renderer.isLastRendererForModelElement) {
            Rectangle bbox = renderer.getOccupiedArea().getBBox();
            attributes.put(PdfName.BBox, new PdfArray(bbox));
        }

        if (role.equals(StandardRoles.TH) || role.equals(StandardRoles.TD) || role.equals(StandardRoles.TABLE)) {
            // For large tables the width can be changed from flush to flush so the Width attribute shouldn't be applied.
            // There are also technical issues with large tables widths being explicitly set as property on element during layouting
            // (even if user didn't explcitly specfied it). This is required due to specificity of large elements implementation,
            // however in this case we cannot distinguish layout-specific and user-specified width properties.
            if (!(renderer instanceof TableRenderer) || ((Table) renderer.getModelElement()).isComplete()) {
                UnitValue width = renderer.<UnitValue>getProperty(Property.WIDTH);
                if (width != null && width.isPointValue()) {
                    attributes.put(PdfName.Width, new PdfNumber(width.getValue()));
                }
            }
            UnitValue height = renderer.<UnitValue>getProperty(Property.HEIGHT);
            if (height != null && height.isPointValue()) {
                attributes.put(PdfName.Height, new PdfNumber(height.getValue()));
            }
        }

        if (role.equals(StandardRoles.TH) || role.equals(StandardRoles.TD)) {
            HorizontalAlignment horizontalAlignment = renderer.<HorizontalAlignment>getProperty(Property.HORIZONTAL_ALIGNMENT);
            if (horizontalAlignment != null) {
                attributes.put(PdfName.BlockAlign, transformBlockAlignToName(horizontalAlignment));
            }

            if (textAlignment != null
                    //there is no justified alignment for InlineAlign attribute
                    && (textAlignment != TextAlignment.JUSTIFIED && textAlignment != TextAlignment.JUSTIFIED_ALL)) {
                attributes.put(PdfName.InlineAlign, transformTextAlignmentValueToName(textAlignment));
            }
        }

    }

    private static void applyInlineLevelLayoutAttributes(AbstractRenderer renderer, PdfDictionary attributes) {
        Float textRise = renderer.getPropertyAsFloat(Property.TEXT_RISE);
        if (textRise != null && textRise != 0) {
            attributes.put(PdfName.BaselineShift, new PdfNumber((float) textRise));
        }

        Object underlines = renderer.<Object>getProperty(Property.UNDERLINE);
        if (underlines != null) {
            UnitValue fontSize = renderer.getPropertyAsUnitValue(Property.FONT_SIZE);
            if (!fontSize.isPointValue()) {
                Logger logger = LoggerFactory.getLogger(AccessibleAttributesApplier.class);
                logger.error(MessageFormatUtil.format(LogMessageConstant.PROPERTY_IN_PERCENTS_NOT_SUPPORTED, Property.FONT_SIZE));
            }
            Underline underline = null;
            if (underlines instanceof List
                    && ((List) underlines).size() > 0
                    && ((List) underlines).get(0) instanceof Underline) {
                // in standard attributes only one text decoration could be described for an element. That's why we take only the first underline from the list.
                underline = (Underline) ((List) underlines).get(0);
            } else if (underlines instanceof Underline) {
                underline = (Underline) underlines;
            }
            if (underline != null) {
                attributes.put(PdfName.TextDecorationType, underline.getYPosition(fontSize.getValue()) > 0 ? PdfName.LineThrough : PdfName.Underline);
                if (underline.getColor() instanceof DeviceRgb) {
                    attributes.put(PdfName.TextDecorationColor, new PdfArray(underline.getColor().getColorValue()));
                }

                attributes.put(PdfName.TextDecorationThickness, new PdfNumber(underline.getThickness(fontSize.getValue())));
            }
        }
    }

    private static void applyIllustrationLayoutAttributes(AbstractRenderer renderer, PdfDictionary attributes) {
        Rectangle bbox = renderer.getOccupiedArea().getBBox();
        attributes.put(PdfName.BBox, new PdfArray(bbox));

        UnitValue width = renderer.<UnitValue>getProperty(Property.WIDTH);
        if (width != null && width.isPointValue()) {
            attributes.put(PdfName.Width, new PdfNumber(width.getValue()));
        } else {
            attributes.put(PdfName.Width, new PdfNumber(bbox.getWidth()));
        }

        UnitValue height = renderer.<UnitValue>getProperty(Property.HEIGHT);
        if (height != null) {
            attributes.put(PdfName.Height, new PdfNumber(height.getValue()));
        } else {
            attributes.put(PdfName.Height, new PdfNumber(bbox.getHeight()));
        }
    }

    private static void applyPaddingAttribute(AbstractRenderer renderer, PdfDictionary attributes) {
        UnitValue[] paddingsUV = {
                renderer.getPropertyAsUnitValue(Property.PADDING_TOP),
                renderer.getPropertyAsUnitValue(Property.PADDING_RIGHT),
                renderer.getPropertyAsUnitValue(Property.PADDING_BOTTOM),
                renderer.getPropertyAsUnitValue(Property.PADDING_LEFT),
        };

        if (!paddingsUV[0].isPointValue()) {
            Logger logger = LoggerFactory.getLogger(AccessibleAttributesApplier.class);
            logger.error(MessageFormatUtil.format(LogMessageConstant.PROPERTY_IN_PERCENTS_NOT_SUPPORTED, Property.PADDING_TOP));
        }
        if (!paddingsUV[1].isPointValue()) {
            Logger logger = LoggerFactory.getLogger(AccessibleAttributesApplier.class);
            logger.error(MessageFormatUtil.format(LogMessageConstant.PROPERTY_IN_PERCENTS_NOT_SUPPORTED, Property.PADDING_RIGHT));
        }
        if (!paddingsUV[2].isPointValue()) {
            Logger logger = LoggerFactory.getLogger(AccessibleAttributesApplier.class);
            logger.error(MessageFormatUtil.format(LogMessageConstant.PROPERTY_IN_PERCENTS_NOT_SUPPORTED, Property.PADDING_BOTTOM));
        }
        if (!paddingsUV[3].isPointValue()) {
            Logger logger = LoggerFactory.getLogger(AccessibleAttributesApplier.class);
            logger.error(MessageFormatUtil.format(LogMessageConstant.PROPERTY_IN_PERCENTS_NOT_SUPPORTED, Property.PADDING_LEFT));
        }

        float[] paddings = new float[]{paddingsUV[0].getValue(), paddingsUV[1].getValue(), paddingsUV[2].getValue(), paddingsUV[3].getValue()};
        PdfObject padding = null;
        if (paddings[0] == paddings[1] && paddings[0] == paddings[2] && paddings[0] == paddings[3]) {
            if (paddings[0] != 0) {
                padding = new PdfNumber(paddings[0]);
            }
        } else {
            PdfArray paddingArray = new PdfArray();
            int[] paddingsOrder = {0, 1, 2, 3}; //TODO set depending on writing direction
            for (int i : paddingsOrder) {
                paddingArray.add(new PdfNumber(paddings[i]));
            }
            padding = paddingArray;
        }

        if (padding != null) {
            attributes.put(PdfName.Padding, padding);
        }
    }

    private static void applyBorderAttributes(AbstractRenderer renderer, PdfDictionary attributes) {
        boolean specificBorderProperties = renderer.<Border>getProperty(Property.BORDER_TOP) != null
                || renderer.<Border>getProperty(Property.BORDER_RIGHT) != null
                || renderer.<Border>getProperty(Property.BORDER_BOTTOM) != null
                || renderer.<Border>getProperty(Property.BORDER_LEFT) != null;

        boolean generalBorderProperties = !specificBorderProperties && renderer.<Object>getProperty(Property.BORDER) != null;

        if (generalBorderProperties) {
            Border generalBorder = renderer.<Border>getProperty(Property.BORDER);
            Color generalBorderColor = generalBorder.getColor();
            int borderType = generalBorder.getType();
            float borderWidth = generalBorder.getWidth();

            if (generalBorderColor instanceof DeviceRgb) {
                attributes.put(PdfName.BorderColor, new PdfArray(generalBorderColor.getColorValue()));
                attributes.put(PdfName.BorderStyle, transformBorderTypeToName(borderType));
                attributes.put(PdfName.BorderThickness, new PdfNumber(borderWidth));
            }
        }

        if (specificBorderProperties) {
            PdfArray borderColors = new PdfArray();
            PdfArray borderTypes = new PdfArray();
            PdfArray borderWidths = new PdfArray();
            boolean atLeastOneRgb = false;
            Border[] borders = renderer.getBorders();

            boolean allColorsEqual = true;
            boolean allTypesEqual = true;
            boolean allWidthsEqual = true;

            for (int i = 1; i < borders.length; i++) {
                Border border = borders[i];
                if (border != null) {
                    if (null == borders[0] || !border.getColor().equals(borders[0].getColor())) {
                        allColorsEqual = false;
                    }

                    if (null == borders[0] || border.getWidth() != borders[0].getWidth()) {
                        allWidthsEqual = false;
                    }

                    if (null == borders[0] || border.getType() != borders[0].getType()) {
                        allTypesEqual = false;
                    }
                }
            }

            int[] borderOrder = {0, 1, 2, 3}; //TODO set depending on writing direction
            for (int i : borderOrder) {
                if (borders[i] != null) {
                    if (borders[i].getColor() instanceof DeviceRgb) {
                        borderColors.add(new PdfArray(borders[i].getColor().getColorValue()));
                        atLeastOneRgb = true;
                    } else {
                        borderColors.add(PdfNull.PDF_NULL);
                    }
                    borderTypes.add(transformBorderTypeToName(borders[i].getType()));
                    borderWidths.add(new PdfNumber(borders[i].getWidth()));
                } else {
                    borderColors.add(PdfNull.PDF_NULL);
                    borderTypes.add(PdfName.None);
                    borderWidths.add(PdfNull.PDF_NULL);
                }
            }

            if (atLeastOneRgb) {
                if (allColorsEqual) {
                    attributes.put(PdfName.BorderColor, borderColors.get(0));
                } else {
                    attributes.put(PdfName.BorderColor, borderColors);
                }
            }

            if (allTypesEqual) {
                attributes.put(PdfName.BorderStyle, borderTypes.get(0));
            } else {
                attributes.put(PdfName.BorderStyle, borderTypes);
            }

            if (allWidthsEqual) {
                attributes.put(PdfName.BorderThickness, borderWidths.get(0));
            } else {
                attributes.put(PdfName.BorderThickness, borderWidths);
            }
        }
    }

    private static IRoleMappingResolver resolveMappingToStandard(TagTreePointer taggingPointer) {
        TagStructureContext tagContext = taggingPointer.getDocument().getTagStructureContext();
        PdfNamespace namespace = taggingPointer.getProperties().getNamespace();
        return tagContext.resolveMappingToStandardOrDomainSpecificRole(taggingPointer.getRole(), namespace);
    }

    private static boolean isTagStructurePdf2(PdfNamespace namespace) {
        return namespace != null && StandardNamespaces.PDF_2_0.equals(namespace.getNamespaceName());
    }

    private static PdfName transformTextAlignmentValueToName(TextAlignment textAlignment) {
        //TODO set rightToLeft value according with actual text content if it is possible.
        boolean isLeftToRight = true;
        switch (textAlignment) {
            case LEFT:
                if (isLeftToRight) {
                    return PdfName.Start;
                } else {
                    return PdfName.End;
                }
            case CENTER:
                return PdfName.Center;
            case RIGHT:
                if (isLeftToRight) {
                    return PdfName.End;
                } else {
                    return PdfName.Start;
                }
            case JUSTIFIED:
            case JUSTIFIED_ALL:
                return PdfName.Justify;
            default:
                return PdfName.Start;
        }
    }

    private static PdfName transformBlockAlignToName(HorizontalAlignment horizontalAlignment) {
        //TODO set rightToLeft value according with actual text content if it is possible.
        boolean isLeftToRight = true;
        switch (horizontalAlignment) {
            case LEFT:
                if (isLeftToRight) {
                    return PdfName.Before;
                } else {
                    return PdfName.After;
                }
            case CENTER:
                return PdfName.Middle;
            case RIGHT:
                if (isLeftToRight) {
                    return PdfName.After;
                } else {
                    return PdfName.Before;
                }
            default:
                return PdfName.Before;
        }
    }

    private static PdfName transformBorderTypeToName(int borderType) {
        switch (borderType) {
            case Border.SOLID:
                return PdfName.Solid;
            case Border.DASHED:
                return PdfName.Dashed;
            case Border.DOTTED:
                return PdfName.Dotted;
            case Border.ROUND_DOTS:
                return PdfName.Dotted;
            case Border.DOUBLE:
                return PdfName.Double;
            case Border._3D_GROOVE:
                return PdfName.Groove;
            case Border._3D_INSET:
                return PdfName.Inset;
            case Border._3D_OUTSET:
                return PdfName.Outset;
            case Border._3D_RIDGE:
                return PdfName.Ridge;
            default:
                return PdfName.Solid;

        }
    }

    private static PdfName transformNumberingTypeToName(ListNumberingType numberingType, boolean isTagStructurePdf2) {
        switch (numberingType) {
            case DECIMAL:
            case DECIMAL_LEADING_ZERO:
                return PdfName.Decimal;
            case ROMAN_UPPER:
                return PdfName.UpperRoman;
            case ROMAN_LOWER:
                return PdfName.LowerRoman;
            case ENGLISH_UPPER:
            case GREEK_UPPER:
                return PdfName.UpperAlpha;
            case ENGLISH_LOWER:
            case GREEK_LOWER:
                return PdfName.LowerAlpha;
            default:
                if (isTagStructurePdf2) {
                    return PdfName.Ordered;
                } else {
                    return PdfName.None;
                }
        }
    }
}
