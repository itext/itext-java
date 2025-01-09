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
