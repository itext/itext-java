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
package com.itextpdf.layout.hyphenation;

import java.util.Enumeration;
import java.util.Stack;

/**
 * An object that iterates over the {@link TernaryTree}.
 */
class TernaryTreeIterator implements Enumeration {

    /**
     * current node index
     */
    int cur;

    /**
     * current key
     */
    String curkey;

    TernaryTree tt;

    private class Item {
        /**
         * parent
         */
        char parent;
        /**
         * child
         */
        char child;

        /**
         * default constructor
         */
        public Item() {
            parent = 0;
            child = 0;
        }

        /**
         * Construct item.
         *
         * @param p a char
         * @param c a char
         */
        public Item(char p, char c) {
            parent = p;
            child = c;
        }

        public Item(Item i) {
            this.parent = i.parent;
            this.child = i.child;
        }
    }

    /**
     * Node stack
     */
    Stack ns;

    /**
     * key stack implemented with a StringBuffer
     */
    StringBuffer ks;

    /**
     * default constructor
     */
    public TernaryTreeIterator(TernaryTree tt) {
        this.tt = tt;
        cur = -1;
        ns = new Stack();
        ks = new StringBuffer();
        reset();
    }

    /**
     * Resets the Iterator to its initial state.
     */
    public void reset() {
        ns.removeAllElements();
        ks.setLength(0);
        cur = tt.root;
        run();
    }

    /**
     * @return next element
     */
    public Object nextElement() {
        String res = curkey;
        cur = up();
        run();
        return res;
    }

    /**
     * @return value
     */
    public char getValue() {
        if (cur >= 0) {
            return tt.eq[cur];
        }
        return (char) 0;
    }

    /**
     * @return true if more elements
     */
    public boolean hasMoreElements() {
        return (cur != -1);
    }

    /**
     * traverse upwards
     */
    private int up() {
        Item i = new Item();
        int res = 0;

        if (ns.size() == 0) {
            return -1;
        }

        if (cur != 0 && tt.sc[cur] == 0) {
            return tt.lo[cur];
        }

        boolean climb = true;

        while (climb) {
            i = (Item) ns.pop();
            i.child++;
            switch (i.child) {
                case (char) 1:
                    if (tt.sc[i.parent] != 0) {
                        res = tt.eq[i.parent];
                        ns.push(new Item(i));
                        ks.append(tt.sc[i.parent]);
                    } else {
                        i.child++;
                        ns.push(new Item(i));
                        res = tt.hi[i.parent];
                    }
                    climb = false;
                    break;

                case (char) 2:
                    res = tt.hi[i.parent];
                    ns.push(new Item(i));
                    if (ks.length() > 0) {
                        ks.setLength(ks.length() - 1);    // pop
                    }
                    climb = false;
                    break;

                default:
                    if (ns.size() == 0) {
                        return -1;
                    }
                    climb = true;
                    break;
            }
        }
        return res;
    }

    /**
     * traverse the tree to find next key
     */
    private int run() {
        if (cur == -1) {
            return -1;
        }

        boolean leaf = false;
        while (true) {
            // first go down on low branch until leaf or compressed branch
            while (cur != 0) {
                if (tt.sc[cur] == 0xFFFF) {
                    leaf = true;
                    break;
                }
                ns.push(new Item((char) cur, '\u0000'));
                if (tt.sc[cur] == 0) {
                    leaf = true;
                    break;
                }
                cur = tt.lo[cur];
            }
            if (leaf) {
                break;
            }
            // nothing found, go up one node and try again
            cur = up();
            if (cur == -1) {
                return -1;
            }
        }
        // The current node should be a data node and
        // the key should be in the key stack (at least partially)
        StringBuffer buf = new StringBuffer(ks.toString());
        if (tt.sc[cur] == 0xFFFF) {
            int p = tt.lo[cur];
            while (tt.kv.get(p) != 0) {
                buf.append(tt.kv.get(p++));
            }
        }
        curkey = buf.toString();
        return 0;
    }

}
