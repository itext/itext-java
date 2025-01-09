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
package com.itextpdf.svg.processors.impl;

import com.itextpdf.svg.renderers.ISvgNodeRenderer;

import java.util.Stack;

/**
 * Internal ProcessorState representation for {@link DefaultSvgProcessor}
 */
public class ProcessorState {

    private Stack<ISvgNodeRenderer> stack;

    /**
     * Instantiates the processor state.
     */
    public ProcessorState() {
        this.stack = new Stack<>();
    }

    /**
     * Returns the amount of ISvgNodeRenderers being processed.
     *
     * @return amount of ISvgNodeRenderers
     */
    public int size() {
        return this.stack.size();
    }

    /**
     * Adds an ISvgNodeRenderer to the processor's state.
     *
     * @param svgNodeRenderer renderer to be added to the state
     */
    public void push(ISvgNodeRenderer svgNodeRenderer) {
        this.stack.push(svgNodeRenderer);
    }

    /**
     * Removes and returns the first renderer of the processor state.
     *
     * @return the removed ISvgNodeRenderer object
     */
    public ISvgNodeRenderer pop() {
        return this.stack.pop();
    }

    /**
     * Returns the first ISvgNodeRenderer object without removing it.
     *
     * @return the first ISvgNodeRenderer
     */
    public ISvgNodeRenderer top() {
        return this.stack.peek();
    }

    /**
     * Returns true when the processorstate is empty, false when there is at least one ISvgNodeRenderer in the state.
     *
     * @return true if empty, false if not empty
     */
    public boolean empty() {
        return this.stack.size() == 0;
    }
}
