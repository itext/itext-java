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

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.IPropertyContainer;
import com.itextpdf.layout.layout.LayoutArea;
import com.itextpdf.layout.layout.LayoutContext;
import com.itextpdf.layout.layout.LayoutResult;

import java.util.List;

/**
 * A renderer object is responsible for drawing a corresponding layout object on
 * a document or canvas. Every layout object has a renderer, by default one of
 * the corresponding type, e.g. you can ask an {@link com.itextpdf.layout.element.Image}
 * for its {@link ImageRenderer}.
 * 
 * Renderers are designed to be extensible, and custom implementations can be
 * seeded to layout objects (or their custom subclasses) at runtime.
 */
public interface IRenderer extends IPropertyContainer {

    /**
     * Adds a child to the current renderer
     * @param renderer a child to be added
     */
    void addChild(IRenderer renderer);

    /**
     * This method simulates positioning of the renderer, including all of its children, and returns
     * the {@link LayoutResult}, representing the layout result, including occupied area, status, i.e.
     * if there was enough place to fit the renderer subtree, etc.
     * {@link LayoutResult} can be extended to return custom layout results for custom elements, e.g.
     * {@link TextRenderer} uses {@link com.itextpdf.layout.layout.TextLayoutResult} as its result.
     *
     * This method can be called standalone to learn how much area the renderer subtree needs, or can be called
     * before {@link #draw(DrawContext)}, to prepare the renderer to be flushed to the output stream.
     *
     * @param layoutContext the description of layout area and any other additional information
     * @return result of the layout process
     */
    LayoutResult layout(LayoutContext layoutContext);

    /**
     * Flushes the renderer subtree contents, i.e. draws itself on canvas,
     * adds necessary objects to the {@link PdfDocument} etc.
     * @param drawContext contains the {@link PdfDocument} to which the renderer subtree if flushed,
     *                    the {@link PdfCanvas} on which the renderer subtree is drawn and other additional parameters
     *                    needed to perform drawing
     */
    void draw(DrawContext drawContext);

    /**
     * Gets the resultant occupied area after the last call to the {@link #layout(LayoutContext)} method.
     * @return {@link LayoutArea} instance
     */
    LayoutArea getOccupiedArea();

    /**
     * Gets a property from this entity or one of its hierarchical parents.
     * If the property is not found, {@code defaultValue} will be returned.
     * @param <T1> the return type associated with the property
     * @param property the property to be retrieved
     * @param defaultValue a fallback value
     * @return the value of the given property
     */
    <T1> T1 getProperty(int property, T1 defaultValue);

    /**
     * Explicitly sets this object as the child of another {@link IRenderer} in
     * the renderer hierarchy. Some implementations also use this method
     * internally to create a consistent hierarchy tree.
     * 
     * @param parent the object to place higher in the renderer hierarchy
     * @return by default, this object
     */
    IRenderer setParent(IRenderer parent);

    /**
     * Gets the parent {@link IRenderer}.
     *
     * @return direct parent {@link IRenderer renderer} of this instance
     */
    IRenderer getParent();

    /**
     * Gets the model element associated with this renderer.
     * 
     * @return the model element, as a {@link IPropertyContainer container of properties}
     */
    IPropertyContainer getModelElement();

    /**
     * Gets the child {@link IRenderer}s.
     * 
     * @return a list of direct child {@link IRenderer renderers} of this instance
     */
    List<IRenderer> getChildRenderers();

    /**
     * Indicates whether this renderer is flushed or not, i.e. if {@link #draw(DrawContext)} has already
     * been called.
     * @return whether the renderer has been flushed
     */
    boolean isFlushed();

    /**
     * Moves the renderer subtree by the specified offset. This method affects occupied area of the renderer.
     * @param dx the x-axis offset in points. Positive value will move the renderer subtree to the right.
     * @param dy the y-axis offset in points. Positive value will move the renderer subtree to the top.
     */
    void move(float dx, float dy);

    /**
     * Gets a new instance of this class to be used as a next renderer, after this renderer is used, if
     * {@link #layout(LayoutContext)} is called more than once.
     * @return new renderer instance
     */
    IRenderer getNextRenderer();
}
