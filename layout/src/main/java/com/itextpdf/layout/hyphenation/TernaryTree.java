/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * PLEASE NOTE that implementation of "insert" function was refactored to consume less stack memory
 */

package com.itextpdf.layout.hyphenation;

import java.io.Serializable;
import java.util.Enumeration;

/**
 * <h2>Ternary Search Tree.</h2>
 * <p>
 * A ternary search tree is a hybrid between a binary tree and
 * a digital search tree (trie). Keys are limited to strings.
 * A data value of type char is stored in each leaf node.
 * It can be used as an index (or pointer) to the data.
 * Branches that only contain one key are compressed to one node
 * by storing a pointer to the trailer substring of the key.
 * This class is intended to serve as base class or helper class
 * to implement Dictionary collections or the like. Ternary trees
 * have some nice properties as the following: the tree can be
 * traversed in sorted order, partial matches (wildcard) can be
 * implemented, retrieval of all keys within a given distance
 * from the target, etc. The storage requirements are higher than
 * a binary tree but a lot less than a trie. Performance is
 * comparable with a hash table, sometimes it outperforms a hash
 * function (most of the time can determine a miss faster than a hash).
 * <p>
 * The main purpose of this java port is to serve as a base for
 * implementing TeX's hyphenation algorithm (see The TeXBook,
 * appendix H). Each language requires from 5000 to 15000 hyphenation
 * patterns which will be keys in this tree. The strings patterns
 * are usually small (from 2 to 5 characters), but each char in the
 * tree is stored in a node. Thus memory usage is the main concern.
 * We will sacrify 'elegance' to keep memory requirements to the
 * minimum. Using java's char type as pointer (yes, I know pointer
 * it is a forbidden word in java) we can keep the size of the node
 * to be just 8 bytes (3 pointers and the data char). This gives
 * room for about 65000 nodes. In my tests the english patterns
 * took 7694 nodes and the german patterns 10055 nodes,
 * so I think we are safe.
 * <p>
 * All said, this is a map with strings as keys and char as value.
 * Pretty limited!. It can be extended to a general map by
 * using the string representation of an object and using the
 * char value as an index to an array that contains the object
 * values.
 * <p>
 * This work was authored by Carlos Villegas (cav@uniscope.co.jp).
 */

public class TernaryTree implements Serializable {

    /**
     * We use 4 arrays to represent a node. I guess I should have created
     * a proper node class, but somehow Knuth's pascal code made me forget
     * we now have a portable language with virtual memory management and
     * automatic garbage collection! And now is kind of late, furthermore,
     * if it ain't broken, don't fix it.
     */

    private static final long serialVersionUID = 3175412271203716160L;

    /**
     * Pointer to low branch and to rest of the key when it is
     * stored directly in this node, we don't have unions in java!
     */
    protected char[] lo;

    /**
     * Pointer to high branch.
     */
    protected char[] hi;

    /**
     * Pointer to equal branch and to data when this node is a string terminator.
     */
    protected char[] eq;

    /**
     * The character stored in this node: splitchar.
     * Two special values are reserved:
     * <ul>
     *     <li>0x0000 as string terminator
     *     <li>0xFFFF to indicate that the branch starting at
     * this node is compressed
     * </ul>
     * This shouldn't be a problem if we give the usual semantics to
     * strings since 0xFFFF is garanteed not to be an Unicode character.
     */
    protected char[] sc;

    /**
     * This vector holds the trailing of the keys when the branch is compressed.
     */
    protected CharVector kv;

    /** root */
    protected char root;
    /** free node */
    protected char freenode;
    /** number of items in tree */
    protected int length;

    /** allocation size for arrays */
    protected static final int BLOCK_SIZE = 2048;

    /** default constructor */
    TernaryTree() {
        init();
    }

    TernaryTree(TernaryTree tt) {
        this.root = tt.root;
        this.freenode = tt.freenode;
        this.length = tt.length;
        this.lo = (char[]) tt.lo.clone();
        this.hi = (char[]) tt.hi.clone();
        this.eq = (char[]) tt.eq.clone();
        this.sc = (char[]) tt.sc.clone();
        this.kv = new CharVector(tt.kv);
    }

    /** initialize */
    protected void init() {
        root = 0;
        freenode = 1;
        length = 0;
        lo = new char[BLOCK_SIZE];
        hi = new char[BLOCK_SIZE];
        eq = new char[BLOCK_SIZE];
        sc = new char[BLOCK_SIZE];
        kv = new CharVector();
    }

    /**
     * Branches are initially compressed, needing
     * one node per key plus the size of the string
     * key. They are decompressed as needed when
     * another key with same prefix
     * is inserted. This saves a lot of space,
     * specially for long keys.
     * @param key the key
     * @param val a value
     */
    public void insert(String key, char val) {
        // make sure we have enough room in the arrays
        int len = key.length()
                  + 1;    // maximum number of nodes that may be generated
        if (freenode + len > eq.length) {
            redimNodeArrays(eq.length + BLOCK_SIZE);
        }
        char[] strkey = new char[len--];
        key.getChars(0, len, strkey, 0);
        strkey[len] = 0;
        root = insert(new TreeInsertionParams(root, strkey, 0, val));
    }

    /**
     * Insert key.
     * @param key the key
     * @param start offset into key array
     * @param val a value
     */
    public void insert(char[] key, int start, char val) {
        int len = strlen(key) + 1;
        if (freenode + len > eq.length) {
            redimNodeArrays(eq.length + BLOCK_SIZE);
        }
        root = insert(new TreeInsertionParams(root, key, start, val));
    }

    // PLEASE NOTE that this function is a result of refactoring "insert" method which
    // is a modification of the original work
    // Returns null if insertion is not needed and the id of the new node if insertion was performed
    private Character insertNewBranchIfNeeded(TreeInsertionParams params) {
        char p = params.p;
        char[] key = params.key;
        int start = params.start;
        char val = params.val;
        int len = strlen(key, start);
        if (p == 0) {
            // this means there is no branch, this node will start a new branch.
            // Instead of doing that, we store the key somewhere else and create
            // only one node with a pointer to the key
            p = freenode++;
            eq[p] = val;           // holds data
            length++;
            hi[p] = 0;
            if (len > 0) {
                sc[p] = 0xFFFF;    // indicates branch is compressed
                lo[p] = (char)kv.alloc(len
                                       + 1);    // use 'lo' to hold pointer to key
                strcpy(kv.getArray(), lo[p], key, start);
            } else {
                sc[p] = 0;
                lo[p] = 0;
            }
            return p;
        } else {
            return null;
        }
    }

    // PLEASE NOTE that this function is a result of refactoring "insert" method which
    // is a modification of the original work
    private char insertIntoExistingBranch(TreeInsertionParams params) {
        char initialP = params.p;
        TreeInsertionParams paramsToInsertNext = params;
        while (paramsToInsertNext != null) {
            char p = paramsToInsertNext.p;
            // We are inserting into an existing branch hence the id must be non-zero
            assert p != 0;
            char[] key = paramsToInsertNext.key;
            int start = paramsToInsertNext.start;
            char val = paramsToInsertNext.val;
            int len = strlen(key, start);
            paramsToInsertNext = null;

            if (sc[p] == 0xFFFF) {
                // branch is compressed: need to decompress
                // this will generate garbage in the external key array
                // but we can do some garbage collection later
                char pp = freenode++;
                lo[pp] = lo[p];    // previous pointer to key
                eq[pp] = eq[p];    // previous pointer to data
                lo[p] = 0;
                if (len > 0) {
                    sc[p] = kv.get(lo[pp]);
                    eq[p] = pp;
                    lo[pp]++;
                    if (kv.get(lo[pp]) == 0) {
                        // key completly decompressed leaving garbage in key array
                        lo[pp] = 0;
                        sc[pp] = 0;
                        hi[pp] = 0;
                    } else {
                        // we only got first char of key, rest is still there
                        sc[pp] = 0xFFFF;
                    }
                } else {
                    // In this case we can save a node by swapping the new node
                    // with the compressed node
                    sc[pp] = 0xFFFF;
                    hi[p] = pp;
                    sc[p] = 0;
                    eq[p] = val;
                    length++;
                    break;
                }
            }
            char s = key[start];
            if (s < sc[p]) {
                TreeInsertionParams branchParams = new TreeInsertionParams(lo[p], key, start, val);
                Character insertNew = insertNewBranchIfNeeded(branchParams);
                if (insertNew == null) {
                    paramsToInsertNext = branchParams;
                } else {
                    lo[p] = insertNew;
                }
            } else if (s == sc[p]) {
                if (s != 0) {
                    TreeInsertionParams branchParams = new TreeInsertionParams(eq[p], key, start + 1, val);
                    Character insertNew = insertNewBranchIfNeeded(branchParams);
                    if (insertNew == null) {
                        paramsToInsertNext = branchParams;
                    } else {
                        eq[p] = insertNew;
                    }
                } else {
                    // key already in tree, overwrite data
                    eq[p] = val;
                }
            } else {
                TreeInsertionParams branchParams = new TreeInsertionParams(hi[p], key, start, val);
                Character insertNew = insertNewBranchIfNeeded(branchParams);
                if (insertNew == null) {
                    paramsToInsertNext = branchParams;
                } else {
                    hi[p] = insertNew;
                }
            }
        }
        return initialP;
    }

    /**
     * The actual insertion function, recursive version.
     * PLEASE NOTE that the implementation has been adapted to consume less stack memory
     */
    private char insert(TreeInsertionParams params) {
        Character newBranch = insertNewBranchIfNeeded(params);
        if (newBranch == null) {
            return insertIntoExistingBranch(params);
        } else {
            return (char)newBranch;
        }
    }

    /**
     * Compares 2 null terminated char arrays
     * @param a a character array
     * @param startA an index into character array
     * @param b a character array
     * @param startB an index into character array
     * @return an integer
     */
    public static int strcmp(char[] a, int startA, char[] b, int startB) {
        for (; a[startA] == b[startB]; startA++, startB++) {
            if (a[startA] == 0) {
                return 0;
            }
        }
        return a[startA] - b[startB];
    }

    /**
     * Compares a string with null terminated char array
     * @param str a string
     * @param a a character array
     * @param start an index into character array
     * @return an integer
     */
    public static int strcmp(String str, char[] a, int start) {
        int i;
        int d;
        int len = str.length();
        for (i = 0; i < len; i++) {
            d = (int)str.charAt(i) - a[start + i];
            if (d != 0) {
                return d;
            }
            if (a[start + i] == 0) {
                return d;
            }
        }
        if (a[start + i] != 0) {
            return -a[start + i];
        }
        return 0;

    }

    /**
     * @param dst a character array
     * @param di an index into character array
     * @param src a character array
     * @param si an index into character array
     */
    public static void strcpy(char[] dst, int di, char[] src, int si) {
        while (src[si] != 0) {
            dst[di++] = src[si++];
        }
        dst[di] = 0;
    }

    /**
     * @param a a character array
     * @param start an index into character array
     * @return an integer
     */
    public static int strlen(char[] a, int start) {
        int len = 0;
        for (int i = start; i < a.length && a[i] != 0; i++) {
            len++;
        }
        return len;
    }

    /**
     * @param a a character array
     * @return an integer
     */
    public static int strlen(char[] a) {
        return strlen(a, 0);
    }

    /**
     * Find key.
     * @param key the key
     * @return result
     */
    public int find(String key) {
        int len = key.length();
        char[] strkey = new char[len + 1];
        key.getChars(0, len, strkey, 0);
        strkey[len] = 0;

        return find(strkey, 0);
    }

    /**
     * Find key.
     * @param key the key
     * @param start offset into key array
     * @return result
     */
    public int find(char[] key, int start) {
        int d;
        char p = root;
        int i = start;
        char c;

        while (p != 0) {
            if (sc[p] == 0xFFFF) {
                if (strcmp(key, i, kv.getArray(), lo[p]) == 0) {
                    return eq[p];
                } else {
                    return -1;
                }
            }
            c = key[i];
            d = c - sc[p];
            if (d == 0) {
                if (c == 0) {
                    return eq[p];
                }
                i++;
                p = eq[p];
            } else if (d < 0) {
                p = lo[p];
            } else {
                p = hi[p];
            }
        }
        return -1;
    }

    /**
     * @param key a key
     * @return trye if key present
     */
    public boolean knows(String key) {
        return (find(key) >= 0);
    }

    // redimension the arrays
    private void redimNodeArrays(int newsize) {
        int len = newsize < lo.length ? newsize : lo.length;
        char[] na = new char[newsize];
        System.arraycopy(lo, 0, na, 0, len);
        lo = na;
        na = new char[newsize];
        System.arraycopy(hi, 0, na, 0, len);
        hi = na;
        na = new char[newsize];
        System.arraycopy(eq, 0, na, 0, len);
        eq = na;
        na = new char[newsize];
        System.arraycopy(sc, 0, na, 0, len);
        sc = na;
    }

    /** @return length */
    public int size() {
        return length;
    }

    /**
     * Recursively insert the median first and then the median of the
     * lower and upper halves, and so on in order to get a balanced
     * tree. The array of keys is assumed to be sorted in ascending
     * order.
     * @param k array of keys
     * @param v array of values
     * @param offset where to insert
     * @param n count to insert
     */
    protected void insertBalanced(String[] k, char[] v, int offset, int n) {
        int m;
        if (n < 1) {
            return;
        }
        m = n >> 1;

        insert(k[m + offset], v[m + offset]);
        insertBalanced(k, v, offset, m);

        insertBalanced(k, v, offset + m + 1, n - m - 1);
    }


    /**
     * Balance the tree for best search performance
     */
    public void balance() {
        // System.out.print("Before root splitchar = "); System.out.println(sc[root]);

        int i = 0;
        int n = length;
        String[] k = new String[n];
        char[] v = new char[n];
        TernaryTreeIterator iter = new TernaryTreeIterator(this);
        while (iter.hasMoreElements()) {
            v[i] = iter.getValue();
            k[i++] = (String)iter.nextElement();
        }
        init();
        insertBalanced(k, v, 0, n);

        // With uniform letter distribution sc[root] should be around 'm'
        // System.out.print("After root splitchar = "); System.out.println(sc[root]);
    }

    /**
     * Each node stores a character (splitchar) which is part of
     * some key(s). In a compressed branch (one that only contain
     * a single string key) the trailer of the key which is not
     * already in nodes is stored  externally in the kv array.
     * As items are inserted, key substrings decrease.
     * Some substrings may completely  disappear when the whole
     * branch is totally decompressed.
     * The tree is traversed to find the key substrings actually
     * used. In addition, duplicate substrings are removed using
     * a map (implemented with a TernaryTree!).
     *
     */
    public void trimToSize() {
        // first balance the tree for best performance
        balance();

        // redimension the node arrays
        redimNodeArrays(freenode);

        // ok, compact kv array
        CharVector kx = new CharVector();
        kx.alloc(1);
        TernaryTree map = new TernaryTree();
        compact(kx, map, root);
        kv = kx;
        kv.trimToSize();
    }

    private void compact(CharVector kx, TernaryTree map, char p) {
        int k;
        if (p == 0) {
            return;
        }
        if (sc[p] == 0xFFFF) {
            k = map.find(kv.getArray(), lo[p]);
            if (k < 0) {
                k = kx.alloc(strlen(kv.getArray(), lo[p]) + 1);
                strcpy(kx.getArray(), k, kv.getArray(), lo[p]);
                map.insert(kx.getArray(), k, (char)k);
            }
            lo[p] = (char)k;
        } else {
            compact(kx, map, lo[p]);
            if (sc[p] != 0) {
                compact(kx, map, eq[p]);
            }
            compact(kx, map, hi[p]);
        }
    }

    /** @return the keys */
    public Enumeration keys() {
        return new TernaryTreeIterator(this);
    }

    // PLEASE NOTE that this is a helper class that was added as a result of the file modification
    // and is not a part of the original file
    private static class TreeInsertionParams {
        char p;
        char[] key;
        int start;
        char val;

        public TreeInsertionParams(char p, char[] key, int start, char val) {
            this.p = p;
            this.key = key;
            this.start = start;
            this.val = val;
        }
    }
}

