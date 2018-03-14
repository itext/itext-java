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
