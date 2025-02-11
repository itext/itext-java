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

        /**
         * Construct item.
         *
         * @param i an {@link Item}
         */
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
                        // pop
                        ks.setLength(ks.length() - 1);
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
