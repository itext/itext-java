/*
    $Id$

    This file is part of the iText (R) project.
    Copyright (c) 1998-2016 iText Group NV
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

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.color.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNull;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.tagging.PdfStructElem;
import com.itextpdf.kernel.pdf.tagutils.AccessibilityProperties;
import com.itextpdf.kernel.pdf.tagutils.IAccessibleElement;
import com.itextpdf.layout.Property;
import com.itextpdf.layout.border.Border;
import com.itextpdf.layout.element.Cell;
import java.util.List;

/**
 * Writes standard structure attributes to the IAccessibleElement based on the layout element properties
 * and renderer layout result.
 */
public class AccessibleAttributesApplier {

    public static void applyLayoutAttributes(PdfName role, AbstractRenderer renderer, PdfDocument doc) {
        if (!(renderer.getModelElement() instanceof IAccessibleElement))
            return;
        int tagType = PdfStructElem.identifyType(doc, role);
        PdfDictionary attributes = new PdfDictionary();
        PdfName attributesType = PdfName.Layout;
        attributes.put(PdfName.O, attributesType);

        PdfDictionary roleMap = doc.getStructTreeRoot().getRoleMap();
        if (roleMap.containsKey(role))
            role = roleMap.getAsName(role);

        //TODO WritingMode attribute applying when needed

        applyCommonLayoutAttributes(renderer, attributes);
        if (tagType == PdfStructElem.BlockLevel) {
            applyBlockLevelLayoutAttributes(role, renderer, attributes, doc);
        }
        if (tagType == PdfStructElem.InlineLevel) {
            applyInlineLevelLayoutAttributes(renderer, attributes);
        }

        if (tagType == PdfStructElem.Illustration) {
            applyIllustrationLayoutAttributes(renderer, attributes);
        }

        if (attributes.size() > 1) {
            AccessibilityProperties properties = ((IAccessibleElement) renderer.getModelElement()).getAccessibilityProperties();
            removeSameAttributesTypeIfPresent(properties, attributesType);
            properties.addAttributes(attributes);
        }
    }

    public static void applyListAttributes(AbstractRenderer renderer) {
        if (!(renderer.getModelElement() instanceof com.itextpdf.layout.element.List))
            return;
        PdfDictionary attributes = new PdfDictionary();
        PdfName attributesType = PdfName.List;
        attributes.put(PdfName.O, attributesType);

        Object listSymbol = renderer.getProperty(Property.LIST_SYMBOL);
        if (listSymbol instanceof Property.ListNumberingType) {
            Property.ListNumberingType numberingType = (Property.ListNumberingType) listSymbol;
            attributes.put(PdfName.ListNumbering, transformNumberingTypeToName(numberingType));
        }

        if (attributes.size() > 1) {
            AccessibilityProperties properties = ((IAccessibleElement) renderer.getModelElement()).getAccessibilityProperties();
            removeSameAttributesTypeIfPresent(properties, attributesType);
            properties.addAttributes(attributes);
        }
    }

    public static void applyTableAttributes(AbstractRenderer renderer) {
        if (!(renderer.getModelElement() instanceof IAccessibleElement))
            return;

        IAccessibleElement accessibleElement = (IAccessibleElement) renderer.getModelElement();

        PdfDictionary attributes = new PdfDictionary();
        PdfName attributesType = PdfName.Table;
        attributes.put(PdfName.O, attributesType);

        if (accessibleElement instanceof Cell) {
            Cell cell = (Cell) accessibleElement;
            if (cell.getRowspan() != 1) {
                attributes.put(PdfName.RowSpan, new PdfNumber(cell.getRowspan()));
            }
            if (cell.getColspan() != 1) {
                attributes.put(PdfName.ColSpan, new PdfNumber(cell.getColspan()));
            }
        }

        if (attributes.size() > 1) {
            AccessibilityProperties properties = accessibleElement.getAccessibilityProperties();
            removeSameAttributesTypeIfPresent(properties, attributesType);
            properties.addAttributes(attributes);
        }
    }

    private static void applyCommonLayoutAttributes(AbstractRenderer renderer, PdfDictionary attributes) {
        Color backgroundColor = renderer.getPropertyAsColor(Property.BACKGROUND);
        if (backgroundColor != null && backgroundColor instanceof DeviceRgb) {
            attributes.put(PdfName.BackgroundColor, new PdfArray(backgroundColor.getColorValue()));
        }

        //TODO NOTE: applying border attributes for cells is temporarily turned off on purpose. Remove this 'if' in future.
        // The reason is that currently, we can't distinguish if all cells have same border style or not.
        // Therefore for every cell in every table we have to write the same border attributes, which creates lots of clutter.
        if (!(renderer.getModelElement() instanceof Cell)) {
            applyBorderAttributes(renderer, attributes);
        }
        applyPaddingAttribute(renderer, attributes);

        Color color = renderer.getPropertyAsColor(Property.FONT_COLOR);
        if (color != null && color instanceof DeviceRgb) {
            attributes.put(PdfName.Color, new PdfArray(color.getColorValue()));
        }
    }

    private static void applyBlockLevelLayoutAttributes(PdfName role, AbstractRenderer renderer, PdfDictionary attributes, PdfDocument doc) {
        Float[] margins = {renderer.getPropertyAsFloat(Property.MARGIN_TOP),
                renderer.getPropertyAsFloat(Property.MARGIN_BOTTOM),
                renderer.getPropertyAsFloat(Property.MARGIN_LEFT),
                renderer.getPropertyAsFloat(Property.MARGIN_RIGHT) };

        int[] marginsOrder = {0, 1, 2, 3}; //TODO set depending on writing direction

        Float spaceBefore = margins[marginsOrder[0]];
        if (spaceBefore != null && spaceBefore != 0) {
            attributes.put(PdfName.SpaceBefore, new PdfNumber(spaceBefore));
        }

        Float spaceAfter = margins[marginsOrder[1]];
        if (spaceAfter != null && spaceAfter != 0) {
            attributes.put(PdfName.SpaceAfter, new PdfNumber(spaceAfter));
        }

        Float startIndent = margins[marginsOrder[2]];
        if (startIndent != null && startIndent != 0) {
            attributes.put(PdfName.StartIndent, new PdfNumber(startIndent));
        }

        Float endIndent = margins[marginsOrder[3]];
        if (endIndent != null && endIndent != 0) {
            attributes.put(PdfName.EndIndent, new PdfNumber(endIndent));
        }


        Float firstLineIndent = renderer.getProperty(Property.FIRST_LINE_INDENT);
        if (firstLineIndent != null && firstLineIndent != 0) {
            attributes.put(PdfName.TextIndent, new PdfNumber(firstLineIndent));
        }

        Property.TextAlignment textAlignment = renderer.getProperty(Property.TEXT_ALIGNMENT);
        if (textAlignment != null &&
                //for table cells there is an InlineAlign attribute (see below)
                (!role.equals(PdfName.TH) && !role.equals(PdfName.TD))) {
            attributes.put(PdfName.TextAlign, transformTextAlignmentValueToName(textAlignment));
        }

        boolean connectedToTag = doc.getTagStructureContext().isElementConnectedToTag((IAccessibleElement) renderer.getModelElement());
        boolean elementIsOnSinglePage = !connectedToTag && renderer.isLastRendererForModelElement;
        if (elementIsOnSinglePage) {
            Rectangle bbox = renderer.getOccupiedArea().getBBox();
            attributes.put(PdfName.BBox, new PdfArray(bbox));
        }

        if (role.equals(PdfName.TH) || role.equals(PdfName.TD) || role.equals(PdfName.Table)) {
            Property.UnitValue width = renderer.getProperty(Property.WIDTH);
            if (width != null && width.isPointValue()) {
                attributes.put(PdfName.Width, new PdfNumber(width.getValue()));
            }

            Float height = renderer.getPropertyAsFloat(Property.HEIGHT);
            if (height != null) {
                attributes.put(PdfName.Height, new PdfNumber(height));
            }
        }

        if (role.equals(PdfName.TH) || role.equals(PdfName.TD)) {
            Property.HorizontalAlignment horizontalAlignment = renderer.getProperty(Property.HORIZONTAL_ALIGNMENT);
            if (horizontalAlignment != null) {
                attributes.put(PdfName.BlockAlign, transformBlockAlignToName(horizontalAlignment));
            }

            if (textAlignment != null
                    //there is no justified alignment for InlineAlign attribute
                    && (textAlignment != Property.TextAlignment.JUSTIFIED && textAlignment != Property.TextAlignment.JUSTIFIED_ALL)) {
                attributes.put(PdfName.InlineAlign, transformTextAlignmentValueToName(textAlignment));
            }
        }

    }

    private static void applyInlineLevelLayoutAttributes(AbstractRenderer renderer, PdfDictionary attributes) {
        Float textRise = renderer.getPropertyAsFloat(Property.TEXT_RISE);
        if (textRise != null && textRise != 0) {
            attributes.put(PdfName.BaselineShift, new PdfNumber(textRise));
        }

        Object underlines = renderer.getProperty(Property.UNDERLINE);
        if (underlines != null) {
            Float fontSize = renderer.getPropertyAsFloat(Property.FONT_SIZE);
            Property.Underline underline = null;
            if (underlines instanceof List
                    && !((List) underlines).isEmpty()
                    && ((List) underlines).get(0) instanceof Property.Underline) {
                // in standard attributes only one text decoration could be described for an element. That's why we take only the first underline from the list.
                underline = (Property.Underline) ((List) underlines).get(0);
            } else if (underlines instanceof Property.Underline) {
                underline = (Property.Underline) underlines;
            }
            if (underline != null) {
                attributes.put(PdfName.TextDecorationType, underline.getYPosition(fontSize) > 0 ? PdfName.LineThrough : PdfName.Underline);
                if (underline.getColor() instanceof DeviceRgb) {
                    attributes.put(PdfName.TextDecorationColor, new PdfArray(underline.getColor().getColorValue()));
                }

                attributes.put(PdfName.TextDecorationThickness, new PdfNumber(underline.getThickness(fontSize)));
            }
        }
    }

    private static void applyIllustrationLayoutAttributes(AbstractRenderer renderer, PdfDictionary attributes) {
        Rectangle bbox = renderer.getOccupiedArea().getBBox();
        attributes.put(PdfName.BBox, new PdfArray(bbox));

        Property.UnitValue width = renderer.getProperty(Property.WIDTH);
        if (width != null && width.isPointValue()) {
            attributes.put(PdfName.Width, new PdfNumber(width.getValue()));
        } else {
            attributes.put(PdfName.Width, new PdfNumber(bbox.getWidth()));
        }

        Float height = renderer.getPropertyAsFloat(Property.HEIGHT);
        if (height != null) {
            attributes.put(PdfName.Height, new PdfNumber(height));
        } else {
            attributes.put(PdfName.Height, new PdfNumber(bbox.getHeight()));
        }
    }

    private static void applyPaddingAttribute(AbstractRenderer renderer, PdfDictionary attributes) {
        float[] paddings = {
                renderer.getPropertyAsFloat(Property.PADDING_TOP),
                renderer.getPropertyAsFloat(Property.PADDING_RIGHT),
                renderer.getPropertyAsFloat(Property.PADDING_BOTTOM),
                renderer.getPropertyAsFloat(Property.PADDING_LEFT),
        };

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
        boolean specificBorderProperties = renderer.getProperty(Property.BORDER_TOP) != null
                || renderer.getProperty(Property.BORDER_RIGHT) != null
                || renderer.getProperty(Property.BORDER_BOTTOM) != null
                || renderer.getProperty(Property.BORDER_LEFT) != null;

        boolean generalBorderProperties = !specificBorderProperties && renderer.getProperty(Property.BORDER) != null;

        if (generalBorderProperties) {
            Border generalBorder = renderer.getProperty(Property.BORDER);
            Color generalBorderColor = generalBorder.getColor();
            int borderType = generalBorder.getType();
            float borderWidth = generalBorder.getWidth();

            if (generalBorderColor instanceof DeviceRgb) {
                attributes.put(PdfName.BorderColor, new PdfArray(generalBorderColor.getColorValue()));
                attributes.put(PdfName.BorderStyle, transformBorderTypeToName(borderType));
                attributes.put(PdfName.BorderThikness, new PdfNumber(borderWidth));
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
                    if (!border.getColor().equals(borders[0].getColor())) {
                        allColorsEqual = false;
                    }

                    if (border.getWidth() != borders[0].getWidth()) {
                        allWidthsEqual = false;
                    }

                    if (border.getType() != borders[0].getType()) {
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
                attributes.put(PdfName.BorderThikness, borderWidths.get(0));
            } else {
                attributes.put(PdfName.BorderThikness, borderWidths);
            }
        }
    }

    private static PdfName transformTextAlignmentValueToName(Property.TextAlignment textAlignment) {
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

    private static PdfName transformBlockAlignToName(Property.HorizontalAlignment horizontalAlignment) {
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

    private static PdfName transformNumberingTypeToName(Property.ListNumberingType numberingType) {
        switch (numberingType) {
            case DECIMAL:
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
                return PdfName.None;
        }
    }

    /**
     * The same layout element instance can be added several times to the document.
     * In that case it will already have attributes which belong to the previous positioning on the page, and because of
     * that we want to remove those old irrelevant attributes.
     */
    private static void removeSameAttributesTypeIfPresent(AccessibilityProperties properties, PdfName attributesType) {
        List<PdfDictionary> attributesList = properties.getAttributesList();
        int i;
        for (i = 0; i < attributesList.size(); i++) {
            PdfDictionary attr = attributesList.get(i);
            if (attributesType.equals(attr.get(PdfName.O))) {
                break;
            }
        }
        if (i < attributesList.size()) {
            attributesList.remove(i);
        }
    }
}
