/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2026 Apryse Group NV
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
package com.itextpdf.layout.properties.margins;

import com.itextpdf.kernel.pdf.tagging.StandardRoles;
import com.itextpdf.kernel.pdf.tagutils.AccessibilityProperties;
import com.itextpdf.kernel.pdf.tagutils.DefaultAccessibilityProperties;
import com.itextpdf.layout.Style;
import com.itextpdf.layout.element.AbstractElement;
import com.itextpdf.layout.element.IAbstractElement;
import com.itextpdf.layout.element.IElement;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.BaseDirection;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.renderer.FootnoteRenderer;
import com.itextpdf.layout.renderer.IRenderer;
import com.itextpdf.layout.tagging.IAccessibleElement;

import java.util.HashMap;
import java.util.Map;

/**
 * Class representing a single footnote which is a note placed at the bottom of the page
 * indicated in the text with superscript numbers (or letters or other symbols).
 */
public class Footnote extends AbstractElement<Footnote> implements IAccessibleElement {

    final Map<Integer, IElement> anchors = new HashMap<>();
    private IElement footnoteAnchor = null;
    private DefaultAccessibilityProperties tagProperties;

    /**
     * Creates new {@link Footnote} instance with text.
     *
     * @param text the textual contents of the footnote
     */
    public Footnote(String text) {
        this(new Paragraph(text).setMarginTop(0).setMarginBottom(0));
    }

    /**
     * Creates new {@link Footnote} instance.
     *
     * @param paragraph {@link Paragraph} representing the contents of the footnote
     */
    public Footnote(Paragraph paragraph) {
        super();
        childElements.add(paragraph);
        paragraph.setNeutralRole();
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    public AccessibilityProperties getAccessibilityProperties() {
        if (tagProperties == null) {
            tagProperties = new DefaultAccessibilityProperties(StandardRoles.NOTE);
        }
        return tagProperties;
    }

    /**
     * Gets injected footnote anchor element, which is a copy of a footnote anchor in the main content.
     *
     * @return injected footnote anchor element
     */
    IElement getInjectedFootnoteAnchor() {
        return footnoteAnchor;
    }

    /**
     * Injects footnote anchor before placing this footnote on the specified page.
     *
     * @param pageNum number of the page where this footnote should be placed
     */
    void applyFootnoteAnchor(int pageNum) {
        if (!this.getChildren().isEmpty() && this.getChildren().get(0) instanceof Paragraph) {
            Paragraph paragraph = (Paragraph) this.getChildren().get(0);
            removeFootnoteAnchorFromParagraph(paragraph);
            if (this.anchors.containsKey(pageNum)) {
                this.footnoteAnchor = this.anchors.get(pageNum);
                paragraph.getChildren().add(0, this.footnoteAnchor);
            }
        }
    }

    /**
     * Injects footnote anchor into this footnote before layout.
     *
     * @param footnoteAnchor {@link FootnoteAnchor} to link to this footnote
     */
    void applyFootnoteAnchor(FootnoteAnchor footnoteAnchor) {
        if (!this.getChildren().isEmpty() && this.getChildren().get(0) instanceof Paragraph) {
            Paragraph paragraph = (Paragraph) this.getChildren().get(0);
            removeFootnoteAnchorFromParagraph(paragraph);
            injectFootnoteAnchorIntoParagraph(paragraph, footnoteAnchor);
        }
    }

    /**
     * Resets current {@link FootnoteAnchor} linked to this footnote.
     */
    void resetFootnoteAnchor() {
        if (!this.getChildren().isEmpty() && this.getChildren().get(0) instanceof Paragraph) {
            Paragraph paragraph = (Paragraph) this.getChildren().get(0);
            removeFootnoteAnchorFromParagraph(paragraph);
        }
    }

    private void injectFootnoteAnchorIntoParagraph(Paragraph paragraph, FootnoteAnchor footnoteAnchor) {
        IElement footnoteAnchorSymbol = createAnchorCopy(footnoteAnchor);
        if (footnoteAnchorSymbol == null) {
            return;
        }
        this.footnoteAnchor = footnoteAnchorSymbol;
        paragraph.getChildren().add(0, this.footnoteAnchor);
    }

    private IElement createAnchorCopy(FootnoteAnchor footnoteAnchor) {
        IElement footnoteAnchorSymbol = footnoteAnchor.getFootnoteAnchor();
        Style footnoteAnchorStyle = footnoteAnchor.getFootnoteAnchorLabelStyle();
        if (footnoteAnchorStyle == null) {
            boolean isRtl = BaseDirection.RIGHT_TO_LEFT == this.<BaseDirection>getProperty(Property.BASE_DIRECTION);
            footnoteAnchorStyle = new Style();
            footnoteAnchorStyle.setProperty(isRtl ? Property.MARGIN_LEFT : Property.MARGIN_RIGHT,
                    UnitValue.createPointValue(5F));
        }
        if (footnoteAnchorSymbol instanceof Text) {
            return new Text((Text) footnoteAnchorSymbol).addStyle(footnoteAnchorStyle);
        } else if (footnoteAnchorSymbol instanceof Image) {
            return new Image((Image) footnoteAnchorSymbol).addStyle(footnoteAnchorStyle);
        } else if (footnoteAnchorSymbol instanceof IAbstractElement) {
            return ((AbstractElement<IElement>) footnoteAnchorSymbol).addStyle(footnoteAnchorStyle);
        } else {
            return footnoteAnchorSymbol;
        }
    }

    private void removeFootnoteAnchorFromParagraph(Paragraph paragraph) {
        if (this.footnoteAnchor != null) {
            paragraph.getChildren().remove(this.footnoteAnchor);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    protected IRenderer makeNewRenderer() {
        return new FootnoteRenderer(this);
    }
}
