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
package com.itextpdf.layout.renderer;

import com.itextpdf.layout.IPropertyContainer;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.layout.LayoutArea;
import com.itextpdf.layout.layout.LayoutContext;
import com.itextpdf.layout.layout.LayoutResult;

import java.util.List;

/**
 * Renderer for the {@link AreaBreak} layout element. Will terminate the
 * current content area and initialize a new one.
 */
public class AreaBreakRenderer implements IRenderer {

    protected AreaBreak areaBreak;

    /**
     * Creates an AreaBreakRenderer.
     *
     * @param areaBreak the {@link AreaBreak} that will be rendered by this object
     */
    public AreaBreakRenderer(AreaBreak areaBreak) {
        this.areaBreak = areaBreak;
    }

    /**
     * Throws an UnsupportedOperationException because instances of this
     * class are only used for terminating the current content area.
     *
     * @param renderer {@inheritDoc}
     */
    @Override
    public void addChild(IRenderer renderer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public LayoutResult layout(LayoutContext layoutContext) {
        return new LayoutResult(LayoutResult.NOTHING, null, null, null, this).setAreaBreak(areaBreak);
    }

    /**
     * Throws an UnsupportedOperationException because instances of this
     * class are only used for terminating the current content area.
     *
     * @param drawContext {@inheritDoc}
     */
    @Override
    public void draw(DrawContext drawContext) {
        throw new UnsupportedOperationException();
    }

    /**
     * Throws an UnsupportedOperationException because instances of this
     * class are only used for terminating the current content area.
     *
     * @return {@inheritDoc}
     */
    @Override
    public LayoutArea getOccupiedArea() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean hasProperty(int property) {
        return false;
    }

    @Override
    public boolean hasOwnProperty(int property) {
        return false;
    }

    @Override
    public <T1> T1 getProperty(int key) {
        return (T1) (Object) null;
    }

    @Override
    public <T1> T1 getOwnProperty(int property) {
        return (T1) (Object) null;
    }

    @Override
    public <T1> T1 getDefaultProperty(int property) {
        return (T1) (Object) null;
    }

    /**
     * Throws an UnsupportedOperationException because instances of this
     * class are only used for terminating the current content area.
     *
     * @param property {@inheritDoc}
     * @param defaultValue {@inheritDoc}
     * @param <T1> {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public <T1> T1 getProperty(int property, T1 defaultValue) {
        throw new UnsupportedOperationException();
    }

    /**
     * Throws an UnsupportedOperationException because instances of this
     * class are only used for terminating the current content area.
     *
     * @param property {@inheritDoc}
     * @param value {@inheritDoc}
     */
    @Override
    public void setProperty(int property, Object value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void deleteOwnProperty(int property) {
    }

    @Override
    public IRenderer setParent(IRenderer parent) {
        return this;
    }

    @Override
    public IPropertyContainer getModelElement() {
        return null;
    }

    @Override
    public IRenderer getParent() { return null; }

    @Override
    public List<IRenderer> getChildRenderers() {
        return null;
    }

    @Override
    public boolean isFlushed() {
        return false;
    }

    /**
     * Throws an UnsupportedOperationException because instances of this
     * class are only used for terminating the current content area.
     *
     * @param dx {@inheritDoc}
     * @param dy {@inheritDoc}
     */
    @Override
    public void move(float dx, float dy) {
        throw new UnsupportedOperationException();
    }

    @Override
    public IRenderer getNextRenderer() {
        return null;
    }
}
