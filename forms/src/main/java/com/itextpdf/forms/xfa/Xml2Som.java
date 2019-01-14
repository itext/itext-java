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
package com.itextpdf.forms.xfa;

import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.w3c.dom.Node;

/**
 * A class for some basic SOM processing.
 */
class Xml2Som {
    /**
     * The order the names appear in the XML, depth first.
     */
    protected List<String> order;
    /**
     * The mapping of full names to nodes.
     */
    protected Map<String, Node> name2Node;
    /**
     * The data to do a search from the bottom hierarchy.
     */
    protected Map<String, InverseStore> inverseSearch;
    /**
     * A stack to be used when parsing.
     */
    protected Stack<String> stack;
    /**
     * A temporary store for the repetition count.
     */
    protected int anform;

    /**
     * Escapes a SOM string fragment replacing "." with "\.".
     *
     * @param s the unescaped string
     * @return the escaped string
     */
    public static String escapeSom(String s) {
        if (s == null)
            return "";
        int idx = s.indexOf('.');
        if (idx < 0)
            return s;
        StringBuilder sb = new StringBuilder();
        int last = 0;
        while (idx >= 0) {
            sb.append(s.substring(last, idx));
            sb.append('\\');
            last = idx;
            idx = s.indexOf('.', idx + 1);
        }
        sb.append(s.substring(last));
        return sb.toString();
    }

    /**
     * Unescapes a SOM string fragment replacing "\." with ".".
     *
     * @param s the escaped string
     * @return the unescaped string
     */
    public static String unescapeSom(String s) {
        int idx = s.indexOf('\\');
        if (idx < 0)
            return s;
        StringBuilder sb = new StringBuilder();
        int last = 0;
        while (idx >= 0) {
            sb.append(s.substring(last, idx));
            last = idx + 1;
            idx = s.indexOf('\\', idx + 1);
        }
        sb.append(s.substring(last));
        return sb.toString();
    }

    /**
     * Outputs the stack as the sequence of elements separated
     * by '.'.
     *
     * @return the stack as the sequence of elements separated by '.'
     */
    protected String printStack() {
        if (stack.size() == 0) {
            return "";
        }
        StringBuilder s = new StringBuilder();
        for (int k = 0; k < stack.size(); ++k)
            s.append('.').append(stack.get(k));
        return s.substring(1);
    }

    /**
     * Gets the name with the <CODE>#subform</CODE> removed.
     *
     * @param s the long name
     * @return the short name
     */
    public static String getShortName(String s) {
        int idx = s.indexOf(".#subform[");
        if (idx < 0)
            return s;
        int last = 0;
        StringBuilder sb = new StringBuilder();
        while (idx >= 0) {
            sb.append(s.substring(last, idx));
            idx = s.indexOf("]", idx + 10);
            if (idx < 0)
                return sb.toString();
            last = idx + 1;
            idx = s.indexOf(".#subform[", last);
        }
        sb.append(s.substring(last));
        return sb.toString();
    }

    /**
     * Adds a SOM name to the search node chain.
     *
     * @param unstack the SOM name
     */
    public void inverseSearchAdd(String unstack) {
        inverseSearchAdd(inverseSearch, stack, unstack);
    }

    /**
     * Adds a SOM name to the search node chain.
     *
     * @param inverseSearch the start point
     * @param stack         the stack with the separated SOM parts
     * @param unstack       the full name
     */
    public static void inverseSearchAdd(Map<String, InverseStore> inverseSearch, Stack<String> stack, String unstack) {
        String last = stack.peek();
        InverseStore store = inverseSearch.get(last);
        if (store == null) {
            store = new InverseStore();
            inverseSearch.put(last, store);
        }
        for (int k = stack.size() - 2; k >= 0; --k) {
            last = stack.get(k);
            InverseStore store2;
            int idx = store.part.indexOf(last);
            if (idx < 0) {
                store.part.add(last);
                store2 = new InverseStore();
                store.follow.add(store2);
            } else
                store2 = (InverseStore) store.follow.get(idx);
            store = store2;
        }
        store.part.add("");
        store.follow.add(unstack);
    }

    /**
     * Searches the SOM hierarchy from the bottom.
     *
     * @param parts the SOM parts
     * @return the full name or <CODE>null</CODE> if not found
     */
    public String inverseSearchGlobal(List<String> parts) {
        if (parts.size() == 0) {
            return null;
        }
        InverseStore store = inverseSearch.get(parts.get(parts.size() - 1));
        if (store == null)
            return null;
        for (int k = parts.size() - 2; k >= 0; --k) {
            String part = parts.get(k);
            int idx = store.part.indexOf(part);
            if (idx < 0) {
                if (store.isSimilar(part))
                    return null;
                return store.getDefaultName();
            }
            store = (InverseStore) store.follow.get(idx);
        }
        return store.getDefaultName();
    }

    /**
     * Splits a SOM name in the individual parts.
     *
     * @param name the full SOM name
     * @return the split name
     */
    public static Stack<String> splitParts(String name) {
        while (name.startsWith("."))
            name = name.substring(1);
        Stack<String> parts = new Stack<>();
        int last = 0;
        int pos = 0;
        String part;
        while (true) {
            pos = last;
            while (true) {
                pos = name.indexOf('.', pos);
                if (pos < 0)
                    break;
                if (name.charAt(pos - 1) == '\\')
                    ++pos;
                else
                    break;
            }
            if (pos < 0)
                break;
            part = name.substring(last, pos);
            if (!part.endsWith("]"))
                part += "[0]";
            parts.add(part);
            last = pos + 1;
        }
        part = name.substring(last);
        if (!part.endsWith("]"))
            part += "[0]";
        parts.add(part);
        return parts;
    }

    /**
     * Gets the order the names appear in the XML, depth first.
     *
     * @return the order the names appear in the XML, depth first
     */
    public List<String> getOrder() {
        return order;
    }

    /**
     * Sets the order the names appear in the XML, depth first
     *
     * @param order the order the names appear in the XML, depth first
     */
    public void setOrder(List<String> order) {
        this.order = order;
    }

    /**
     * Gets the mapping of full names to nodes.
     *
     * @return the mapping of full names to nodes
     */
    public Map<String, Node> getName2Node() {
        return name2Node;
    }

    /**
     * Sets the mapping of full names to nodes.
     *
     * @param name2Node the mapping of full names to nodes
     */
    public void setName2Node(Map<String, Node> name2Node) {
        this.name2Node = name2Node;
    }

    /**
     * Gets the data to do a search from the bottom hierarchy.
     *
     * @return the data to do a search from the bottom hierarchy
     */
    public Map<String, InverseStore> getInverseSearch() {
        return inverseSearch;
    }

    /**
     * Sets the data to do a search from the bottom hierarchy.
     *
     * @param inverseSearch the data to do a search from the bottom hierarchy
     */
    public void setInverseSearch(Map<String, InverseStore> inverseSearch) {
        this.inverseSearch = inverseSearch;
    }
}
