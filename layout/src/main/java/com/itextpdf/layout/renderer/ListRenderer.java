/*

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

import com.itextpdf.io.font.FontConstants;
import com.itextpdf.io.util.TextUtil;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.numbering.EnglishAlphabetNumbering;
import com.itextpdf.kernel.numbering.GreekAlphabetNumbering;
import com.itextpdf.kernel.numbering.RomanNumbering;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.ListItem;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.layout.LayoutArea;
import com.itextpdf.layout.layout.LayoutContext;
import com.itextpdf.layout.layout.LayoutResult;
import com.itextpdf.layout.property.ListNumberingType;
import com.itextpdf.layout.property.ListSymbolPosition;
import com.itextpdf.layout.property.Property;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ListRenderer extends BlockRenderer {

    /**
     * Creates a ListRenderer from its corresponding layout object.
     *
     * @param modelElement the {@link com.itextpdf.layout.element.List} which this object should manage
     */
    public ListRenderer(com.itextpdf.layout.element.List modelElement) {
        super(modelElement);
    }

    @Override
    public LayoutResult layout(LayoutContext layoutContext) {
        overrideHeightProperties();
        if (!hasOwnProperty(Property.LIST_SYMBOLS_INITIALIZED)) {
            List<IRenderer> symbolRenderers = new ArrayList<>();
            int listItemNum = (int) this.<Integer>getProperty(Property.LIST_START, 1);
            for (int i = 0; i < childRenderers.size(); i++) {
                childRenderers.get(i).setParent(this);
                IRenderer currentSymbolRenderer = makeListSymbolRenderer(listItemNum++, childRenderers.get(i));
                childRenderers.get(i).setParent(null);

                currentSymbolRenderer.setParent(this);
                // Workaround for the case when font is specified as string
                if (currentSymbolRenderer instanceof AbstractRenderer && currentSymbolRenderer.<Object>getProperty(Property.FONT) instanceof String) {
                    PdfFont actualPdfFont = ((AbstractRenderer)currentSymbolRenderer).resolveFirstPdfFont();
                    currentSymbolRenderer.setProperty(Property.FONT, actualPdfFont);
                }
                symbolRenderers.add(currentSymbolRenderer);
                LayoutResult listSymbolLayoutResult = currentSymbolRenderer.layout(layoutContext);
                currentSymbolRenderer.setParent(null);
                if (listSymbolLayoutResult.getStatus() != LayoutResult.FULL) {
                    return new LayoutResult(LayoutResult.NOTHING, null, null, this, listSymbolLayoutResult.getCauseOfNothing());
                }
            }

            float maxSymbolWidth = 0;
            for (int i = 0; i < childRenderers.size(); i++) {
                IRenderer symbolRenderer = symbolRenderers.get(i);
                IRenderer listItemRenderer = childRenderers.get(i);
                if ((ListSymbolPosition)listItemRenderer.<Object>getProperty(Property.LIST_SYMBOL_POSITION) != ListSymbolPosition.INSIDE) {
                    maxSymbolWidth = Math.max(maxSymbolWidth, symbolRenderer.getOccupiedArea().getBBox().getWidth());
                }
            }

            Float symbolIndent = this.getPropertyAsFloat(Property.LIST_SYMBOL_INDENT);
            listItemNum = 0;
            for (IRenderer childRenderer : childRenderers) {
                childRenderer.setParent(this);
                childRenderer.deleteOwnProperty(Property.MARGIN_LEFT);
                float calculatedMargin = (float) childRenderer.getProperty(Property.MARGIN_LEFT, (Float) 0f);
                if ((ListSymbolPosition)childRenderer.<Object>getProperty(Property.LIST_SYMBOL_POSITION) == ListSymbolPosition.DEFAULT) {
                    calculatedMargin += maxSymbolWidth + (float) (symbolIndent != null ? symbolIndent : 0f);
                }
                childRenderer.setProperty(Property.MARGIN_LEFT, calculatedMargin);
                IRenderer symbolRenderer = symbolRenderers.get(listItemNum++);
                ((ListItemRenderer) childRenderer).addSymbolRenderer(symbolRenderer, maxSymbolWidth);
            }
        }
        LayoutResult result = super.layout(layoutContext);
        // cannot place even the first ListItemRenderer
        if (Boolean.TRUE.equals(getPropertyAsBoolean(Property.FORCED_PLACEMENT)) && null != result.getCauseOfNothing()) {
            if (LayoutResult.FULL == result.getStatus()) {
                result = correctListSplitting(this, null, result.getCauseOfNothing(), result.getOccupiedArea());
            } else if (LayoutResult.PARTIAL == result.getStatus()) {
                result = correctListSplitting(result.getSplitRenderer(), result.getOverflowRenderer(), result.getCauseOfNothing(), result.getOccupiedArea());
            }
        }
        return result;
    }

    @Override
    public IRenderer getNextRenderer() {
        return new ListRenderer((com.itextpdf.layout.element.List) modelElement);
    }

    @Override
    protected AbstractRenderer createSplitRenderer(int layoutResult) {
        AbstractRenderer splitRenderer = super.createSplitRenderer(layoutResult);
        splitRenderer.setProperty(Property.LIST_SYMBOLS_INITIALIZED, Boolean.TRUE);
        return splitRenderer;
    }

    @Override
    protected AbstractRenderer createOverflowRenderer(int layoutResult) {
        AbstractRenderer overflowRenderer = super.createOverflowRenderer(layoutResult);
        overflowRenderer.setProperty(Property.LIST_SYMBOLS_INITIALIZED, Boolean.TRUE);
        return overflowRenderer;
    }

    protected IRenderer makeListSymbolRenderer(int index, IRenderer renderer) {
        IRenderer symbolRenderer = createListSymbolRenderer(index, renderer);
        // underlying should not be applied
        if (symbolRenderer != null) {
            symbolRenderer.setProperty(Property.UNDERLINE, false);
        }
        return symbolRenderer;
    }

    private IRenderer createListSymbolRenderer(int index, IRenderer renderer) {
        Object defaultListSymbol = renderer.<Object>getProperty(Property.LIST_SYMBOL);
        if (defaultListSymbol instanceof Text) {
            return new TextRenderer((Text) defaultListSymbol);
        } else if (defaultListSymbol instanceof Image) {
            return new ImageRenderer((Image) defaultListSymbol);
        } else if (defaultListSymbol instanceof ListNumberingType) {
            ListNumberingType numberingType = (ListNumberingType) defaultListSymbol;
            String numberText;
            switch (numberingType) {
                case DISC:
                    numberText = "\u2022";
                    break;
                case CIRCLE:
                    numberText = "\u25E6";
                    break;
                case SQUARE:
                    numberText = "\u25AA";
                    break;
                case DECIMAL:
                    numberText = String.valueOf(index);
                    break;
                case DECIMAL_LEADING_ZERO:
                    numberText = (index < 10 ? "0" : "") + String.valueOf(index);
                    break;
                case ROMAN_LOWER:
                    numberText = RomanNumbering.toRomanLowerCase(index);
                    break;
                case ROMAN_UPPER:
                    numberText = RomanNumbering.toRomanUpperCase(index);
                    break;
                case ENGLISH_LOWER:
                    numberText = EnglishAlphabetNumbering.toLatinAlphabetNumberLowerCase(index);
                    break;
                case ENGLISH_UPPER:
                    numberText = EnglishAlphabetNumbering.toLatinAlphabetNumberUpperCase(index);
                    break;
                case GREEK_LOWER:
                    numberText = GreekAlphabetNumbering.toGreekAlphabetNumberLowerCase(index);
                    break;
                case GREEK_UPPER:
                    numberText = GreekAlphabetNumbering.toGreekAlphabetNumberUpperCase(index);
                    break;
                case ZAPF_DINGBATS_1:
                    numberText = TextUtil.charToString((char) (index + 171));
                    break;
                case ZAPF_DINGBATS_2:
                    numberText = TextUtil.charToString((char) (index + 181));
                    break;
                case ZAPF_DINGBATS_3:
                    numberText = TextUtil.charToString((char) (index + 191));
                    break;
                case ZAPF_DINGBATS_4:
                    numberText = TextUtil.charToString((char) (index + 201));
                    break;
                default:
                    throw new IllegalStateException();
            }
            Text textElement = new Text(renderer.<String>getProperty(Property.LIST_SYMBOL_PRE_TEXT) + numberText + renderer.<String>getProperty(Property.LIST_SYMBOL_POST_TEXT));
            IRenderer textRenderer;
            // Be careful. There is a workaround here. For Greek symbols we first set a dummy font with document=null
            // in order for the metrics to be taken into account correctly during layout.
            // Then on draw we set the correct font with actual document in order for the font objects to be created.
            if (numberingType == ListNumberingType.GREEK_LOWER || numberingType == ListNumberingType.GREEK_UPPER ||
                    numberingType == ListNumberingType.ZAPF_DINGBATS_1 || numberingType == ListNumberingType.ZAPF_DINGBATS_2 ||
                    numberingType == ListNumberingType.ZAPF_DINGBATS_3 || numberingType == ListNumberingType.ZAPF_DINGBATS_4) {

                final String constantFont = (numberingType == ListNumberingType.GREEK_LOWER || numberingType == ListNumberingType.GREEK_UPPER) ?
                        FontConstants.SYMBOL : FontConstants.ZAPFDINGBATS;

                textRenderer = new TextRenderer(textElement) {
                    @Override
                    public void draw(DrawContext drawContext) {
                        try {
                            setProperty(Property.FONT, PdfFontFactory.createFont(constantFont));
                        } catch (IOException ignored) {
                        }
                        super.draw(drawContext);
                    }
                };
                try {
                    textRenderer.setProperty(Property.FONT, PdfFontFactory.createFont(constantFont));
                } catch (IOException exc) {
                }
            } else {
                textRenderer = new TextRenderer(textElement);
            }
            return textRenderer;
        } else {
            throw new IllegalStateException();
        }
    }

    /**
     * <p>
     * Corrects split and overflow renderers when {@link com.itextpdf.layout.property.Property#FORCED_PLACEMENT} is applied.
     * We assume that {@link com.itextpdf.layout.property.Property#FORCED_PLACEMENT} is applied when the first
     * {@link com.itextpdf.layout.renderer.ListItemRenderer} cannot be fully layouted.
     * This means that the problem has occurred in one of first list item renderer's child.
     * We consider the right solution to force placement of all first item renderer's childs before the one,
     * which was the cause of {@link com.itextpdf.layout.layout.LayoutResult#NOTHING}, including this child.
     * </p>
     * <p>
     * Notice that we do not expect {@link com.itextpdf.layout.property.Property#FORCED_PLACEMENT} to be applied
     * if we can render the first item renderer and strongly recommend not to set
     * {@link com.itextpdf.layout.property.Property#FORCED_PLACEMENT} manually.
     * </p>
     *
     * @param splitRenderer    the {@link IRenderer split renderer} before correction
     * @param overflowRenderer the {@link IRenderer overflow renderer} before correction
     * @param causeOfNothing   the {@link com.itextpdf.layout.layout.LayoutResult#causeOfNothing cause of nothing renderer}
     * @param occupiedArea     the area occupied by layouting before correction
     * @return corrected {@link com.itextpdf.layout.layout.LayoutResult layout result}
     */
    private LayoutResult correctListSplitting(IRenderer splitRenderer, IRenderer overflowRenderer, IRenderer causeOfNothing, LayoutArea occupiedArea) {
        // the first not rendered child
        int firstNotRendered = splitRenderer.getChildRenderers().get(0).getChildRenderers().indexOf(causeOfNothing);

        if (-1 == firstNotRendered) {
            return new LayoutResult(null == overflowRenderer ? LayoutResult.FULL : LayoutResult.PARTIAL,
                    occupiedArea, splitRenderer, overflowRenderer, this);
        }

        // Notice that placed item is a son of the first ListItemRenderer (otherwise there would be now FORCED_PLACEMENT applied)
        IRenderer firstListItemRenderer = splitRenderer.getChildRenderers().get(0);

        ListRenderer newOverflowRenderer = (ListRenderer) createOverflowRenderer(LayoutResult.PARTIAL);
        newOverflowRenderer.deleteOwnProperty(Property.FORCED_PLACEMENT);
        // ListItemRenderer for not rendered children of firstListItemRenderer
        newOverflowRenderer.childRenderers.add(new ListItemRenderer((ListItem) firstListItemRenderer.getModelElement()));
        newOverflowRenderer.childRenderers.addAll(splitRenderer.getChildRenderers().subList(1, splitRenderer.getChildRenderers().size()));

        List<IRenderer> childrenStillRemainingToRender =
                new ArrayList<>(firstListItemRenderer.getChildRenderers().subList(firstNotRendered + 1, firstListItemRenderer.getChildRenderers().size()));

        // 'this' renderer will become split renderer
        splitRenderer.getChildRenderers().removeAll(splitRenderer.getChildRenderers().subList(1, splitRenderer.getChildRenderers().size()));

        if (0 != childrenStillRemainingToRender.size()) {
            newOverflowRenderer.getChildRenderers().get(0).getChildRenderers().addAll(childrenStillRemainingToRender);
            splitRenderer.getChildRenderers().get(0).getChildRenderers().removeAll(childrenStillRemainingToRender);
            newOverflowRenderer.getChildRenderers().get(0).setProperty(Property.MARGIN_LEFT, splitRenderer.getChildRenderers().get(0).<Float>getProperty(Property.MARGIN_LEFT));
        } else {
            newOverflowRenderer.childRenderers.remove(0);
        }

        if (null != overflowRenderer) {
            newOverflowRenderer.childRenderers.addAll(overflowRenderer.getChildRenderers());
        }

        if (0 != newOverflowRenderer.childRenderers.size()) {
            return new LayoutResult(LayoutResult.PARTIAL, occupiedArea, splitRenderer, newOverflowRenderer, this);
        } else {
            return new LayoutResult(LayoutResult.FULL, occupiedArea, null, null, this);
        }
    }
}
