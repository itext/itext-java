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
        if (!(node instanceof IElementNode) || node instanceof ICustomElementNode || node instanceof IDocumentNode) {
            return false;
        }
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
