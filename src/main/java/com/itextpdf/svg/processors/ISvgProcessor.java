package com.itextpdf.svg.processors;


import com.itextpdf.styledxmlparser.node.INode;
import com.itextpdf.svg.exceptions.SvgProcessingException;
import com.itextpdf.svg.renderers.ISvgNodeRenderer;

/**
 * Interface for SVG processors.
 * Processors take the root {@link INode} that corresponds to a Svg element
 * and return a {@link ISvgNodeRenderer} that serves as the root for the same SVG
 */
public interface ISvgProcessor {
    /**
     * Process an SVG, returning the root of a renderer-tree
     * @param root Root of the INode representation of the SVG
     * @return root of the renderer-tree representing the SVG
     */
    ISvgNodeRenderer process(INode root) throws SvgProcessingException;

    /**
     * Process an SVG, returning the root of a renderer-tree
     * @param root Root of the INode representation of the SVG
     * @param convertorprops configuration properties
     * @return root of the renderer-tree representing the SVG
     */
    ISvgNodeRenderer process(INode root, ISvgConverterProperties convertorprops) throws SvgProcessingException;

}
