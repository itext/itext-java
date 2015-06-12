package com.itextpdf.model.renderer;

import com.itextpdf.basics.font.FontConstants;
import com.itextpdf.basics.font.Type1Font;
import com.itextpdf.canvas.PdfCanvas;
import com.itextpdf.core.font.PdfType1Font;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.model.Property;
import com.itextpdf.model.element.BlockElement;
import com.itextpdf.model.element.Image;
import com.itextpdf.model.element.ListItem;
import com.itextpdf.model.element.Text;
import com.itextpdf.model.layout.LayoutContext;
import com.itextpdf.model.layout.LayoutResult;
import com.itextpdf.model.numbering.EnglishAlphabetNumbering;
import com.itextpdf.model.numbering.GreekAlphabetNumbering;
import com.itextpdf.model.numbering.RomanNumbering;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ListRenderer extends BlockRenderer {

    public ListRenderer(BlockElement modelElement) {
        super(modelElement);
    }

    // TODO underlying should not be applied
    // https://jira.itextsupport.com/browse/SUP-952

    @Override
    public LayoutResult layout(LayoutContext layoutContext) {
        List<IRenderer> symbolRenderers = new ArrayList<>();
        int listItemCount = 0;
        for (int i = 0; i < childRenderers.size(); i++) {
            if (childRenderers.get(i).getModelElement() instanceof ListItem) {
                IRenderer currentSymbolRenderer = makeListSymbolRenderer(++listItemCount);
                symbolRenderers.add(currentSymbolRenderer);
                currentSymbolRenderer.layout(layoutContext);
            }
        }
        float maxSymbolWidth = 0;
        for (IRenderer symbolRenderer : symbolRenderers) {
            maxSymbolWidth = Math.max(maxSymbolWidth, symbolRenderer.getOccupiedArea().getBBox().getWidth());
        }
        Float symbolIndent = (Float)modelElement.getProperty(Property.LIST_SYMBOL_INDENT);
        listItemCount = 0;
        for (IRenderer childRenderer : childRenderers) {
            childRenderer.setProperty(Property.MARGIN_LEFT, childRenderer.getProperty(Property.MARGIN_LEFT, 0f) + maxSymbolWidth + (symbolIndent != null ? symbolIndent : 0f));
            if (childRenderer.getModelElement() instanceof ListItem) {
                IRenderer symbolRenderer = symbolRenderers.get(listItemCount++);
                ((ListItemRenderer)childRenderer).addSymbolRenderer(symbolRenderer, maxSymbolWidth);
            }
        }

        return super.layout(layoutContext);
    }

    protected IRenderer makeListSymbolRenderer(int index) {
        Object defaultListSymbol = modelElement.getProperty(Property.LIST_SYMBOL);
        if (defaultListSymbol instanceof Text) {
            return new TextRenderer((Text) defaultListSymbol).setParent(this);
        } else if (defaultListSymbol instanceof Image) {
            return new ImageRenderer((Image) defaultListSymbol).setParent(this);
        } else if (defaultListSymbol instanceof Property.ListNumberingType) {
            Property.ListNumberingType numberingType = (Property.ListNumberingType) defaultListSymbol;
            String numberText;
            com.itextpdf.model.element.List listModelElement = (com.itextpdf.model.element.List) getModelElement();
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
                default:
                    throw new IllegalStateException();
            }
            Text textElement = new Text(listModelElement.getPreSymbolText() + numberText + listModelElement.getPostSymbolText());
            IRenderer textRenderer;
            // Be careful. There is a workaround here. For Greek symbols we first set a dummy font with document=null
            // in order for the metrics to be taken into account correctly during layout.
            // Then on draw we set the correct font with actual document in order for the font objects to be created.
            if (numberingType == Property.ListNumberingType.GREEK_LOWER || numberingType == Property.ListNumberingType.GREEK_UPPER) {
                textRenderer = new TextRenderer(textElement) {
                    @Override
                    public void draw(PdfDocument document, PdfCanvas canvas) {
                        try {
                            setProperty(Property.FONT, new PdfType1Font(document, new Type1Font(FontConstants.SYMBOL, "")));
                        } catch (IOException exc) {}
                        super.draw(document, canvas);
                    }
                }.setParent(this);
                try {
                    textRenderer.setProperty(Property.FONT, new PdfType1Font(null, new Type1Font(FontConstants.SYMBOL, "")));
                } catch (IOException exc) {}
            } else {
                textRenderer = new TextRenderer(textElement).setParent(this);
            }
            return textRenderer;
        } else {
            throw new IllegalStateException();
        }
    }
}
