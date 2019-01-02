/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
    Authors: iText Software.

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
package com.itextpdf.svg.renderers;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.font.FontProvider;
import com.itextpdf.layout.font.FontSet;
import com.itextpdf.styledxmlparser.resolver.font.BasicFontProvider;
import com.itextpdf.styledxmlparser.resolver.resource.ResourceResolver;
import com.itextpdf.svg.exceptions.SvgLogMessageConstant;
import com.itextpdf.svg.exceptions.SvgProcessingException;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * The SvgDrawContext keeps a stack of {@link PdfCanvas} instances, which
 * represent all levels of XObjects that are added to the root canvas.
 */
public class SvgDrawContext {

    private final Map<String, ISvgNodeRenderer> namedObjects = new HashMap<>();

    private final Stack<PdfCanvas> canvases = new Stack<>();
    private final Stack<Rectangle> viewports = new Stack<>();
    private final Stack<String> useIds = new Stack<>();
    private ResourceResolver resourceResolver;
    private FontProvider fontProvider;
    private FontSet tempFonts;

    public SvgDrawContext(ResourceResolver resourceResolver, FontProvider fontProvider) {
        if (resourceResolver == null) resourceResolver = new ResourceResolver("");
        this.resourceResolver = resourceResolver;
        if (fontProvider == null) fontProvider = new BasicFontProvider();
        this.fontProvider = fontProvider;
    }

    /**
     * Retrieves the current top of the stack, without modifying the stack.
     *
     * @return the current canvas that can be used for drawing operations.
     */
    public PdfCanvas getCurrentCanvas() {
        return canvases.peek();
    }

    /**
     * Retrieves the current top of the stack, thereby taking the current item
     * off the stack.
     *
     * @return the current canvas that can be used for drawing operations.
     */
    public PdfCanvas popCanvas() {
        return canvases.pop();
    }

    /**
     * Adds a {@link PdfCanvas} to the stack (by definition its top), for use in
     * drawing operations.
     *
     * @param canvas the new top of the stack
     */
    public void pushCanvas(PdfCanvas canvas) {
        canvases.push(canvas);
    }

    /**
     * Get the current size of the stack, signifying the nesting level of the
     * XObjects.
     *
     * @return the current size of the stack.
     */
    public int size() {
        return canvases.size();
    }

    /**
     * Adds a viewbox to the context.
     *
     * @param viewPort rectangle representing the current viewbox
     */
    public void addViewPort(Rectangle viewPort) {
        this.viewports.push(viewPort);
    }

    /**
     * Get the current viewbox.
     *
     * @return the viewbox as it is currently set
     */
    public Rectangle getCurrentViewPort() {
        return this.viewports.peek();
    }

    /**
     * Remove the currently set view box.
     */
    public void removeCurrentViewPort() {
        if (this.viewports.size() > 0) {
            this.viewports.pop();
        }
    }

    /**
     * Adds a named object to the draw context. These objects can then be referenced from a different tag.
     *
     * @param name        name of the object
     * @param namedObject object to be referenced
     */
    public void addNamedObject(String name, ISvgNodeRenderer namedObject) {
        if (namedObject == null) {
            throw new SvgProcessingException(SvgLogMessageConstant.NAMED_OBJECT_NULL);
        }

        if (name == null || name.isEmpty()) {
            throw new SvgProcessingException(SvgLogMessageConstant.NAMED_OBJECT_NAME_NULL_OR_EMPTY);
        }

        if (!this.namedObjects.containsKey(name)) {
            this.namedObjects.put(name, namedObject);
        }
    }

    /**
     * Get a named object based on its name. If the name isn't listed, this method will return null.
     *
     * @param name name of the object you want to reference
     * @return the referenced object
     */
    public ISvgNodeRenderer getNamedObject(String name) {
        return this.namedObjects.get(name);
    }

    /**
     * Gets the ResourceResolver to be used during the drawing operations.
     *
     * @return resource resolver instance
     */
    public ResourceResolver getResourceResolver() {
        return resourceResolver;
    }

    /**
     * * Adds a number of named object to the draw context. These objects can then be referenced from a different tag.
     *
     * @param namedObjects Map containing the named objects keyed to their ID strings
     */
    public void addNamedObjects(Map<String, ISvgNodeRenderer> namedObjects) {
        this.namedObjects.putAll(namedObjects);
    }

    /**
     * Gets the FontProvider to be used during the drawing operations.
     *
     * @return font provider instance
     */
    public FontProvider getFontProvider() {
        return fontProvider;
    }

    /**
     * Gets list of temporary fonts from @font-face.
     *
     * @return font set instance
     */
    public FontSet getTempFonts() {
        return tempFonts;
    }

    /**
     * Sets the FontSet.
     *
     * @param tempFonts  font set to be used during drawing operations
     */
    public void setTempFonts(FontSet tempFonts) {
        this.tempFonts = tempFonts;
    }

    /**
     * Returns true when this id has been used before
     *
     * @param elementId element id to check
     * @return true if id has been encountered before through a use element
     */
    public boolean isIdUsedByUseTagBefore(String elementId) {
        return this.useIds.contains(elementId);
    }

    /**
     * Adds an ID that has been referenced by a use element.
     *
     * @param elementId referenced element ID
     */
    public void addUsedId(String elementId) {
        this.useIds.push(elementId);
    }

    /**
     * Removes an ID that has been referenced by a use element.
     *
     * @param elementId referenced element ID
     */
    public void removeUsedId(String elementId) {
        this.useIds.pop();
    }
}
