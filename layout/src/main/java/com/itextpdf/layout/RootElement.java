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
package com.itextpdf.layout;

import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.canvas.PdfCanvasConstants;
import com.itextpdf.kernel.pdf.tagging.StandardRoles;
import com.itextpdf.layout.element.BlockElement;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.IBlockElement;
import com.itextpdf.layout.element.IElement;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.font.FontProvider;
import com.itextpdf.layout.properties.FontKerning;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.Leading;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;
import com.itextpdf.layout.renderer.IRenderer;
import com.itextpdf.layout.renderer.RootRenderer;
import com.itextpdf.layout.splitting.DefaultSplitCharacters;
import com.itextpdf.layout.splitting.ISplitCharacters;
import com.itextpdf.layout.tagging.LayoutTaggingHelper;
import com.itextpdf.layout.validation.context.LayoutValidationContext;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A generic abstract root element for a PDF layout object hierarchy.
 *
 * @param <T> this type
 */
public abstract class RootElement<T extends IPropertyContainer> extends ElementPropertyContainer<T> implements Closeable {

    protected boolean immediateFlush = true;
    protected PdfDocument pdfDocument;

    protected List<IElement> childElements = new ArrayList<>();

    protected PdfFont defaultFont;
    protected FontProvider defaultFontProvider;
    protected ISplitCharacters defaultSplitCharacters;

    protected RootRenderer rootRenderer;

    private LayoutTaggingHelper defaultLayoutTaggingHelper;

    /**
     * Creates a new {@link RootElement} instance.
     */
    public RootElement() {
        // Empty constructor.
    }

    /**
     * Adds an element to the root. The element is immediately placed in the contents.
     *
     * @param element an element with spacial margins, tabbing, and alignment
     * @return this element
     * @see BlockElement
     */
    public T add(IBlockElement element) {
        return addElement(element);
    }

    /**
     * Adds an image to the root. The element is immediately placed in the contents.
     *
     * @param image a graphical image element
     * @return this element
     * @see Image
     */
    public T add(Image image) {
        return addElement(image);
    }

    /**
     * Gets {@link FontProvider} if presents.
     *
     * @return instance of {@link FontProvider} if exists, otherwise null.
     */
    public FontProvider getFontProvider() {
        Object fontProvider = this.<Object>getProperty(Property.FONT_PROVIDER);
        if (fontProvider instanceof FontProvider) {
            return (FontProvider) fontProvider;
        }
        return null;
    }

    /**
     * Sets {@link FontProvider}.
     * Note, font provider is inherited property.
     *
     * @param fontProvider instance of {@link FontProvider}.
     */
    public void setFontProvider(FontProvider fontProvider) {
        setProperty(Property.FONT_PROVIDER, fontProvider);
    }

    @Override
    public boolean hasProperty(int property) {
        return hasOwnProperty(property);
    }

    @Override
    public boolean hasOwnProperty(int property) {
        return properties.containsKey(property);
    }

    @Override
    public <T1> T1 getProperty(int property) {
        return this.<T1>getOwnProperty(property);
    }

    @Override
    public <T1> T1 getOwnProperty(int property) {
        return (T1) properties.<T1>get(property);
    }

    @Override
    public <T1> T1 getDefaultProperty(int property) {
        try {
            switch (property) {
                case Property.FONT:
                    if (defaultFont == null) {
                        defaultFont = PdfFontFactory.createFont();
                    }
                    return (T1) (Object) defaultFont;
                case Property.FONT_PROVIDER:
                    if (defaultFontProvider == null) {
                        defaultFontProvider = new FontProvider();
                    }
                    return (T1) (Object) defaultFontProvider;
                case Property.SPLIT_CHARACTERS:
                    if (defaultSplitCharacters == null) {
                        defaultSplitCharacters = new DefaultSplitCharacters();
                    }
                    return (T1) (Object) defaultSplitCharacters;
                case Property.FONT_SIZE:
                    return (T1) (Object) UnitValue.createPointValue(12);
                case Property.TAGGING_HELPER:
                    return (T1) (Object) initTaggingHelperIfNeeded();
                case Property.TEXT_RENDERING_MODE:
                    return (T1) (Object) PdfCanvasConstants.TextRenderingMode.FILL;
                case Property.TEXT_RISE:
                    return (T1) (Object) 0f;
                case Property.SPACING_RATIO:
                    return (T1) (Object) 0.75f;
                default:
                    return (T1) (Object) null;
            }
        } catch (IOException exc) {
            throw new RuntimeException(exc.toString(), exc);
        }
    }

    @Override
    public void deleteOwnProperty(int property) {
        properties.remove(property);
    }

    @Override
    public void setProperty(int property, Object value) {
        properties.put(property, value);
    }

    /**
     * Gets the rootRenderer attribute, a specialized {@link IRenderer} that
     * acts as the root object that other {@link IRenderer renderers} descend
     * from.
     *
     * @return the {@link RootRenderer} attribute
     */
    public RootRenderer getRenderer() {
        return ensureRootRendererNotNull();
    }

    /**
     * Convenience method to write a text aligned about the specified point
     *
     * @param text      text to be placed to the page
     * @param x         the point about which the text will be aligned and rotated
     * @param y         the point about which the text will be aligned and rotated
     * @param textAlign horizontal alignment about the specified point
     * @return this object
     */
    public T showTextAligned(String text, float x, float y, TextAlignment textAlign) {
        return showTextAligned(text, x, y, textAlign, 0);
    }

    /**
     * Convenience method to write a text aligned about the specified point
     *
     * @param text      text to be placed to the page
     * @param x         the point about which the text will be aligned and rotated
     * @param y         the point about which the text will be aligned and rotated
     * @param textAlign horizontal alignment about the specified point
     * @param angle     the angle of rotation applied to the text, in radians
     * @return this object
     */
    public T showTextAligned(String text, float x, float y, TextAlignment textAlign, float angle) {
        return showTextAligned(text, x, y, textAlign, VerticalAlignment.BOTTOM, angle);
    }

    /**
     * Convenience method to write a text aligned about the specified point
     *
     * @param text      text to be placed to the page
     * @param x         the point about which the text will be aligned and rotated
     * @param y         the point about which the text will be aligned and rotated
     * @param textAlign horizontal alignment about the specified point
     * @param vertAlign vertical alignment about the specified point
     * @param angle     the angle of rotation applied to the text, in radians
     * @return this object
     */
    public T showTextAligned(String text, float x, float y, TextAlignment textAlign, VerticalAlignment vertAlign, float angle) {
        Paragraph p = new Paragraph(text).setMultipliedLeading(1).setMargin(0);
        return showTextAligned(p, x, y, pdfDocument.getNumberOfPages(), textAlign, vertAlign, angle);
    }

    /**
     * Convenience method to write a kerned text aligned about the specified point
     *
     * @param text      text to be placed to the page
     * @param x         the point about which the text will be aligned and rotated
     * @param y         the point about which the text will be aligned and rotated
     * @param textAlign horizontal alignment about the specified point
     * @param vertAlign vertical alignment about the specified point
     * @param radAngle  the angle of rotation applied to the text, in radians
     * @return this object
     */
    public T showTextAlignedKerned(String text, float x, float y, TextAlignment textAlign, VerticalAlignment vertAlign, float radAngle) {
        Paragraph p = new Paragraph(text).setMultipliedLeading(1).setMargin(0).setFontKerning(FontKerning.YES);
        return showTextAligned(p, x, y, pdfDocument.getNumberOfPages(), textAlign, vertAlign, radAngle);
    }

    /**
     * Convenience method to write a text aligned about the specified point
     *
     * @param p         paragraph of text to be placed to the page. By default it has no leading and is written in single line.
     *                  Set width to write multiline text.
     * @param x         the point about which the text will be aligned and rotated
     * @param y         the point about which the text will be aligned and rotated
     * @param textAlign horizontal alignment about the specified point
     * @return this object
     */
    public T showTextAligned(Paragraph p, float x, float y, TextAlignment textAlign) {
        return showTextAligned(p, x, y, pdfDocument.getNumberOfPages(), textAlign, VerticalAlignment.BOTTOM, 0);
    }

    /**
     * Convenience method to write a text aligned about the specified point
     *
     * @param p         paragraph of text to be placed to the page. By default it has no leading and is written in single line.
     *                  Set width to write multiline text.
     * @param x         the point about which the text will be aligned and rotated
     * @param y         the point about which the text will be aligned and rotated
     * @param textAlign horizontal alignment about the specified point
     * @param vertAlign vertical alignment about the specified point
     * @return this object
     */
    public T showTextAligned(Paragraph p, float x, float y, TextAlignment textAlign, VerticalAlignment vertAlign) {
        return showTextAligned(p, x, y, pdfDocument.getNumberOfPages(), textAlign, vertAlign, 0);
    }

    /**
     * Convenience method to write a text aligned about the specified point
     *
     * @param p          paragraph of text to be placed to the page. By default it has no leading and is written in single line.
     *                   Set width to write multiline text.
     * @param x          the point about which the text will be aligned and rotated
     * @param y          the point about which the text will be aligned and rotated
     * @param pageNumber the page number to write the text
     * @param textAlign  horizontal alignment about the specified point
     * @param vertAlign  vertical alignment about the specified point
     * @param radAngle   the angle of rotation applied to the text, in radians
     * @return this object
     */
    public T showTextAligned(Paragraph p, float x, float y, int pageNumber, TextAlignment textAlign, VerticalAlignment vertAlign, float radAngle) {
        Div div = new Div();
        div.setTextAlignment(textAlign).setVerticalAlignment(vertAlign);
        if (radAngle != 0) {
            div.setRotationAngle(radAngle);
        }
        div.setProperty(Property.ROTATION_POINT_X, x);
        div.setProperty(Property.ROTATION_POINT_Y, y);

        float divSize = 5e3f;
        float divX = x, divY = y;
        if (textAlign == TextAlignment.CENTER) {
            divX = x - divSize / 2;
            p.setHorizontalAlignment(HorizontalAlignment.CENTER);
        } else if (textAlign == TextAlignment.RIGHT) {
            divX = x - divSize;
            p.setHorizontalAlignment(HorizontalAlignment.RIGHT);
        }

        if (vertAlign == VerticalAlignment.MIDDLE) {
            divY = y - divSize / 2;
        } else if (vertAlign == VerticalAlignment.TOP) {
            divY = y - divSize;
        }

        if (pageNumber == 0)
            pageNumber = 1;
        div.setFixedPosition(pageNumber, divX, divY, divSize).setMinHeight(divSize);
        if (p.<Leading>getProperty(Property.LEADING) == null) {
            p.setMultipliedLeading(1);
        }
        div.add(p.setMargins(0, 0, 0, 0));
        div.getAccessibilityProperties().setRole(StandardRoles.ARTIFACT);
        this.add(div);

        return (T) (Object) this;
    }

    protected abstract RootRenderer ensureRootRendererNotNull();

    protected void createAndAddRendererSubTree(IElement element) {
        IRenderer rendererSubTreeRoot = element.createRendererSubTree();
        LayoutTaggingHelper taggingHelper = initTaggingHelperIfNeeded();
        if (taggingHelper != null) {
            taggingHelper.addKidsHint(pdfDocument.getTagStructureContext().getAutoTaggingPointer(), Collections.<IRenderer>singletonList(rendererSubTreeRoot));
        }
        ensureRootRendererNotNull().addChild(rendererSubTreeRoot);
        traverseAndCallIso(pdfDocument, rendererSubTreeRoot);
    }

    /**
     * Ensures that the root tag is created in the tagging structure.
     */
    protected final void ensureRootTagIsCreated() {
        if (this.pdfDocument == null) {
            return;
        }
        if (!this.pdfDocument.isTagged()) {
            return;
        }
        if (pdfDocument.getWriter() == null){
            return;
        }
        this.pdfDocument.getTagStructureContext().normalizeDocumentRootTag();
    }

    private LayoutTaggingHelper initTaggingHelperIfNeeded() {
        return defaultLayoutTaggingHelper == null && pdfDocument.isTagged() ? defaultLayoutTaggingHelper = new LayoutTaggingHelper(pdfDocument, immediateFlush) : defaultLayoutTaggingHelper;
    }

    private T addElement(IElement element) {
        childElements.add(element);
        createAndAddRendererSubTree(element);
        if (immediateFlush) {
            childElements.remove(childElements.size() - 1);
        }
        return (T) (Object) this;
    }

    private static void traverseAndCallIso(PdfDocument pdfDocument, IRenderer renderer) {
        if (renderer == null) {
            return;
        }
        pdfDocument.checkIsoConformance(new LayoutValidationContext(renderer));
        List<IRenderer> renderers = renderer.getChildRenderers();
        if (renderers == null) {
            return;
        }
        for (IRenderer childRenderer : renderers) {
            traverseAndCallIso(pdfDocument, childRenderer);
        }
    }
}
