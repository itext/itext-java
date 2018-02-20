package com.itextpdf.svg.processors.impl;

import com.itextpdf.svg.renderers.ISvgNodeRenderer;

import java.util.Stack;

/**
 * Internal ProcessorState representation for {@link DefaultSvgProcessor}
 */
public class ProcessorState {
    private Stack<ISvgNodeRenderer> stack;

    public ProcessorState() {
        stack = new Stack<>();
    }

    public Stack<ISvgNodeRenderer> getStack() {
        return stack;
    }

    public void push(ISvgNodeRenderer svgElement) {
        stack.push(svgElement);
    }

    public ISvgNodeRenderer pop() {
        return stack.pop();
    }

    public ISvgNodeRenderer top() {
        return stack.peek();
    }

    public boolean empty() {
        return stack.empty();
    }
}
