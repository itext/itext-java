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
package com.itextpdf.styledxmlparser.css.selector.item;

import com.itextpdf.styledxmlparser.node.ICustomElementNode;
import com.itextpdf.styledxmlparser.node.IDocumentNode;
import com.itextpdf.styledxmlparser.node.IElementNode;
import com.itextpdf.styledxmlparser.node.INode;
import java.util.List;

class CssPseudoClassNthSelectorItem extends CssPseudoClassChildSelectorItem {
    /**
     * The nth A.
     */
    private int nthA;

    /**
     * The nth B.
     */
    private int nthB;

    CssPseudoClassNthSelectorItem(String pseudoClass, String arguments) {
        super(pseudoClass, arguments);
        getNthArguments();
    }

    @Override
    public boolean matches(INode node) {
        List<INode> children = getAllSiblings(node);
        return !children.isEmpty() && resolveNth(node, children);
    }

    /**
     * Gets the nth arguments.
     */
    protected void getNthArguments() {
        if (arguments.matches("((-|\\+)?[0-9]*n(\\s*(-|\\+)\\s*[0-9]+)?|(-|\\+)?[0-9]+|odd|even)")) {
            if (arguments.equals("odd")) {
                this.nthA = 2;
                this.nthB = 1;
            } else if (arguments.equals("even")) {
                this.nthA = 2;
                this.nthB = 0;
            } else {
                int indexOfN = arguments.indexOf('n');
                if (indexOfN == -1) {
                    this.nthA = 0;
                    this.nthB = Integer.parseInt(arguments);
                } else {
                    String aParticle = arguments.substring(0, indexOfN).trim();
                    if (aParticle.isEmpty())
                        this.nthA = 0;
                    else if (aParticle.length() == 1 && !Character.isDigit(aParticle.charAt(0)))
                        this.nthA = aParticle.equals("+") ? 1 : -1;
                    else
                        this.nthA = Integer.parseInt(aParticle);
                    String bParticle = arguments.substring(indexOfN + 1).trim();
                    if (!bParticle.isEmpty())
                        this.nthB = Integer.parseInt(bParticle.charAt(0) + bParticle.substring(1).trim());
                    else
                        this.nthB = 0;
                }
            }
        } else {
            this.nthA = 0;
            this.nthB = 0;
        }
    }

    /**
     * Resolves the nth.
     *
     * @param node     a node
     * @param children the children
     * @return true, if successful
     */
    protected boolean resolveNth(INode node, List<INode> children) {
        if (!children.contains(node))
            return false;
        if (this.nthA > 0) {
            int temp = children.indexOf(node) + 1 - this.nthB;
            return temp >= 0 && temp % this.nthA == 0;
        } else if (this.nthA < 0) {
            int temp = children.indexOf(node) + 1 - this.nthB;
            return temp <= 0 && temp % this.nthA == 0;
        } else
            return (children.indexOf(node) + 1) - this.nthB == 0;
    }
}
