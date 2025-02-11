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
     * Process an SVG, returning the root of a renderer-tree and a list
     * of named objects wrapped in a processor result object
     *
     * @param root           Root of the INode representation of the SVG
     * @param converterProps configuration properties
     * @return root of the renderer-tree representing the SVG wrapped in {link {@link ISvgProcessorResult}}
     * @throws SvgProcessingException throws an exception if the root
     *                                node is null or if the child node being processed is null
     */
    ISvgProcessorResult process(INode root, ISvgConverterProperties converterProps) throws SvgProcessingException;
}
