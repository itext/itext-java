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
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.layout.property.ListNumberingType;
import com.itextpdf.layout.property.Property;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.ListItem;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.layout.LayoutContext;
import com.itextpdf.layout.layout.LayoutResult;
import com.itextpdf.kernel.numbering.EnglishAlphabetNumbering;
import com.itextpdf.kernel.numbering.GreekAlphabetNumbering;
import com.itextpdf.kernel.numbering.RomanNumbering;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ListRenderer extends BlockRenderer {

    public ListRenderer(com.itextpdf.layout.element.List modelElement) {
        super(modelElement);
    }

    // TODO underlying should not be applied
    // https://jira.itextsupport.com/browse/SUP-952

    @Override
    public LayoutResult layout(LayoutContext layoutContext) {
        if (!hasOwnProperty(Property.LIST_SYMBOLS_INITIALIZED)) {
            List<IRenderer> symbolRenderers = new ArrayList<>();
            int listItemNum = (int) this.<Integer>getProperty(Property.LIST_START, 1);
            for (int i = 0; i < childRenderers.size(); i++) {
                if (childRenderers.get(i).getModelElement() instanceof ListItem) {
                    childRenderers.get(i).setParent(this);
                    IRenderer currentSymbolRenderer = makeListSymbolRenderer(listItemNum++, childRenderers.get(i));
                    childRenderers.get(i).setParent(null);
                    symbolRenderers.add(currentSymbolRenderer);
                    LayoutResult listSymbolLayoutResult = currentSymbolRenderer.setParent(this).layout(layoutContext);
                    currentSymbolRenderer.setParent(null);
                    if (listSymbolLayoutResult.getStatus() != LayoutResult.FULL) {
                        return new LayoutResult(LayoutResult.NOTHING, null, null, this, listSymbolLayoutResult.getCauseOfNothing());
                    }
                }
            }
            float maxSymbolWidth = 0;
            for (IRenderer symbolRenderer : symbolRenderers) {
                maxSymbolWidth = Math.max(maxSymbolWidth, symbolRenderer.getOccupiedArea().getBBox().getWidth());
            }
            Float symbolIndent = modelElement.<Float>getProperty(Property.LIST_SYMBOL_INDENT);
            listItemNum = 0;
            for (IRenderer childRenderer : childRenderers) {
                childRenderer.deleteOwnProperty(Property.MARGIN_LEFT);
                childRenderer.setProperty(Property.MARGIN_LEFT, childRenderer.getProperty(Property.MARGIN_LEFT, (Float)0f) + maxSymbolWidth + (symbolIndent != null ? symbolIndent : 0f));
                if (childRenderer.getModelElement() instanceof ListItem) {
                    IRenderer symbolRenderer = symbolRenderers.get(listItemNum++);
                    ((ListItemRenderer) childRenderer).addSymbolRenderer(symbolRenderer, maxSymbolWidth);
                }
            }
        }

        return super.layout(layoutContext);
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
        Object defaultListSymbol = renderer.<Object>getProperty(Property.LIST_SYMBOL);
        if (defaultListSymbol instanceof Text) {
            return new TextRenderer((Text) defaultListSymbol);
        } else if (defaultListSymbol instanceof Image) {
            return new ImageRenderer((Image) defaultListSymbol);
        } else if (defaultListSymbol instanceof ListNumberingType) {
            ListNumberingType numberingType = (ListNumberingType) defaultListSymbol;
            String numberText;
            switch (numberingType) {
                case DECIMAL:
                    numberText = String.valueOf(index);
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
                        } catch (IOException ignored) {}
                        super.draw(drawContext);
                    }
                };
                try {
                    textRenderer.setProperty(Property.FONT, PdfFontFactory.createFont(constantFont));
                } catch (IOException exc) {}
            } else {
                textRenderer = new TextRenderer(textElement);
            }
            return textRenderer;
        } else {
            throw new IllegalStateException();
        }
    }
}
