package com.itextpdf.layout.hyphenation;

import java.util.Enumeration;
import java.util.Stack;

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
        /** parent */
        char parent;
        /** child */
        char child;

        /** default constructor */
        public Item() {
            parent = 0;
            child = 0;
        }

        /**
         * Construct item.
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

    /** default constructor */
    public TernaryTreeIterator(TernaryTree tt) {
        this.tt = tt;
        cur = -1;
        ns = new Stack();
        ks = new StringBuffer();
        rewind();
    }

    /** rewind iterator */
    public void rewind() {
        ns.removeAllElements();
        ks.setLength(0);
        cur = tt.root;
        run();
    }

    /** @return next element */
    public Object nextElement() {
        String res = curkey;
        cur = up();
        run();
        return res;
    }

    /** @return value */
    public char getValue() {
        if (cur >= 0) {
            return tt.eq[cur];
        }
        return 0;
    }

    /** @return true if more elements */
    public boolean hasMoreElements() {
        return (cur != -1);
    }

    /**
     * traverse upwards
     */
    private int up() {
        Item i = new Item();
        int res = 0;

        if (ns.empty()) {
            return -1;
        }

        if (cur != 0 && tt.sc[cur] == 0) {
            return tt.lo[cur];
        }

        boolean climb = true;

        while (climb) {
            i = (Item)ns.pop();
            i.child++;
            switch (i.child) {
                case 1:
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

                case 2:
                    res = tt.hi[i.parent];
                    ns.push(new Item(i));
                    if (ks.length() > 0) {
                        ks.setLength(ks.length() - 1);    // pop
                    }
                    climb = false;
                    break;

                default:
                    if (ns.empty()) {
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
                ns.push(new Item((char)cur, '\u0000'));
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
