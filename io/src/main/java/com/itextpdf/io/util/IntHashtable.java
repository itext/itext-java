/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 Apryse Group NV
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
package com.itextpdf.io.util;

import com.itextpdf.commons.utils.MessageFormatUtil;

import java.util.Arrays;

/**
 * A hash map that uses primitive ints for the key rather than objects.
 * <p>
 * Note that this class is for internal optimization purposes only, and may
 * not be supported in future releases of Jakarta Commons Lang.  Utilities of
 * this sort may be included in future releases of Jakarta Commons Collections.
 *
 * @author Justin Couch
 * @author Alex Chaffee (alex@apache.org)
 * @author Stephen Colebourne
 * @author Bruno Lowagie (change Objects as keys into int values)
 * @author Paulo Soares (added extra methods)
 */
public class IntHashtable implements Cloneable {


    /***
     * The total number of entries in the hash table.
     */
    int count;

    /***
     * The hash table data.
     */
    private Entry[] table;

    /***
     * The table is rehashed when its size exceeds this threshold.  (The
     * value of this field is (int)(capacity * loadFactor).)
     *
     * @serial
     */
    private int threshold;

    /***
     * The load factor for the hashtable.
     *
     * @serial
     */
    private float loadFactor;

    /***
     * Constructs a new, empty hashtable with a default capacity and load
     * factor, which is <code>20</code> and <code>0.75</code> respectively.
     */
    public IntHashtable() {
        this(150, 0.75f);
    }

    /***
     * Constructs a new, empty hashtable with the specified initial capacity
     * and default load factor, which is <code>0.75</code>.
     *
     * @param  initialCapacity the initial capacity of the hashtable.
     * @throws IllegalArgumentException if the initial capacity is less
     *   than zero.
     */
    public IntHashtable(int initialCapacity) {
        this(initialCapacity, 0.75f);
    }

    /***
     * Constructs a new, empty hashtable with the specified initial
     * capacity and the specified load factor.
     *
     * @param initialCapacity the initial capacity of the hashtable.
     * @param loadFactor the load factor of the hashtable.
     * @throws IllegalArgumentException  if the initial capacity is less
     *             than zero, or if the load factor is nonpositive.
     */
    public IntHashtable(int initialCapacity, float loadFactor) {
        if (initialCapacity < 0) {
            throw new IllegalArgumentException(MessageFormatUtil.format("Illegal Capacity: {0}", initialCapacity));
        }
        if (loadFactor <= 0) {
            throw new IllegalArgumentException(MessageFormatUtil.format("Illegal Load: {0}", loadFactor));
        }
        if (initialCapacity == 0) {
            initialCapacity = 1;
        }
        this.loadFactor = loadFactor;
        table = new Entry[initialCapacity];
        threshold = (int) (initialCapacity * loadFactor);
    }

    public IntHashtable(IntHashtable o) {
        this(o.table.length, o.loadFactor);
    }

    /***
     * Returns the number of keys in this hashtable.
     *
     * @return  the number of keys in this hashtable.
     */
    public int size() {
        return count;
    }

    /***
     * Tests if this hashtable maps no keys to values.
     *
     * @return  <code>true</code> if this hashtable maps no keys to values;
     *          <code>false</code> otherwise.
     */
    public boolean isEmpty() {
        return count == 0;
    }

    /***
     * Tests if some key maps into the specified value in this hashtable.
     * This operation is more expensive than the <code>containsKey</code>
     * method.
     * <p>
     * Note that this method is identical in functionality to containsValue,
     * (which is part of the Map interface in the collections framework).
     *
     * @param      value   a value to search for.
     * @return     <code>true</code> if and only if some key maps to the
     *             <code>value</code> argument in this hashtable as
     *             determined by the <tt>equals</tt> method;
     *             <code>false</code> otherwise.
     * @throws  NullPointerException  if the value is <code>null</code>.
     * @see        #containsKey(int)
     * @see        #containsValue(int)
     * @see        java.util.Map
     */
    public boolean contains(int value) {

        Entry[] tab = table;
        for (int i = tab.length; i-- > 0;) {
            for (Entry e = tab[i]; e != null; e = e.next) {
                if (e.value == value) {
                    return true;
                }
            }
        }
        return false;
    }

    /***
     * Returns <code>true</code> if this HashMap maps one or more keys
     * to this value.
     * <p>
     * Note that this method is identical in functionality to contains
     * (which predates the Map interface).
     *
     * @param value value whose presence in this HashMap is to be tested.
     * @return boolean <code>true</code> if the value is contained
     * @see    java.util.Map
     */
    public boolean containsValue(int value) {
        return contains(value);
    }

    /***
     * Tests if the specified int is a key in this hashtable.
     *
     * @param  key  possible key.
     * @return <code>true</code> if and only if the specified int is a
     *    key in this hashtable, as determined by the <tt>equals</tt>
     *    method; <code>false</code> otherwise.
     * @see #contains(int)
     */
    public boolean containsKey(int key) {
        Entry[] tab = table;
        int index = (key & 0x7FFFFFFF) % tab.length;
        for (Entry e = tab[index]; e != null; e = e.next) {
            if (e.key == key) {
                return true;
            }
        }
        return false;
    }

    /***
     * Returns the value to which the specified key is mapped in this map.
     *
     * @param   key   a key in the hashtable.
     * @return  the value to which the key is mapped in this hashtable;
     *          0 if the key is not mapped to any value in
     *          this hashtable.
     * @see     #put(int, int)
     */
    public int get(int key) {
        Entry[] tab = table;
        int index = (key & 0x7FFFFFFF) % tab.length;
        for (Entry e = tab[index]; e != null; e = e.next) {
            if (e.key == key) {
                return e.value;
            }
        }
        return 0;
    }

    /***
     * Returns thes value to which the specified key is mapped in this map.
     *
     * @param   key   a key in the hashtable.
     * @return  the values to which the key is mapped in this hashtable;
     *          <code>null</code> if the key is not mapped to any value in
     *          this hashtable.
     * @see     #put(int, int)
    public ArrayList<Integer> getValues(int key) {
    Entry[] tab = table;
    int index = (key & 0x7FFFFFFF) % tab.length;
    for (Entry e = tab[index]; e != null; e = e.next) {
    if (e.key == key) {
    return e.values;
    }
    }
    return null;
    }
     */

    /***
     * Increases the capacity of and internally reorganizes this
     * hashtable, in order to accommodate and access its entries more
     * efficiently.
     * <p>
     * This method is called automatically when the number of keys
     * in the hashtable exceeds this hashtable's capacity and load
     * factor.
     */
    protected void rehash() {
        int oldCapacity = table.length;
        Entry[] oldMap = table;

        int newCapacity = oldCapacity * 2 + 1;
        Entry[] newMap = new Entry[newCapacity];

        threshold = (int) (newCapacity * loadFactor);
        table = newMap;

        for (int i = oldCapacity; i-- > 0;) {
            for (Entry old = oldMap[i]; old != null;) {
                Entry e = old;
                old = old.next;

                int index = (e.key & 0x7FFFFFFF) % newCapacity;
                e.next = newMap[index];
                newMap[index] = e;
            }
        }
    }

    /***
     * Maps the specified <code>key</code> to the specified
     * <code>value</code> in this hashtable. The key cannot be
     * <code>null</code>.
     * <p>
     * The value can be retrieved by calling the <code>get</code> method
     * with a key that is equal to the original key.
     *
     * @param key     the hashtable key.
     * @param value   the value.
     * @return the previous value of the specified key in this hashtable,
     *         or <code>null</code> if it did not have one.
     * @throws  NullPointerException  if the key is <code>null</code>.
     * @see     #get(int)
     */
    public int put(int key, int value) {
        // Makes sure the key is not already in the hashtable.
        Entry[] tab = table;
        int index = (key & 0x7FFFFFFF) % tab.length;
        for (Entry e = tab[index]; e != null; e = e.next) {
            if (e.key == key) {
                int old = e.value;
                //e.addValue(old);
                e.value = value;
                return old;
            }
        }

        if (count >= threshold) {
            // Rehash the table if the threshold is exceeded
            rehash();

            tab = table;
            index = (key & 0x7FFFFFFF) % tab.length;
        }

        // Creates the new entry.
        Entry e = new Entry(key, value, tab[index]);
        tab[index] = e;
        count++;
        return 0;
    }

    /***
     * Removes the key (and its corresponding value) from this
     * hashtable.
     * <p>
     * This method does nothing if the key is not present in the
     * hashtable.
     *
     * @param   key   the key that needs to be removed.
     * @return  the value to which the key had been mapped in this hashtable,
     *          or <code>null</code> if the key did not have a mapping.
     */
    public int remove(int key) {
        Entry[] tab = table;
        int index = (key & 0x7FFFFFFF) % tab.length;
        Entry e;
        Entry prev;
        for (e = tab[index], prev = null; e != null; prev = e, e = e.next) {
            if (e.key == key) {
                if (prev != null) {
                    prev.next = e.next;
                } else {
                    tab[index] = e.next;
                }
                count--;
                int oldValue = e.value;
                e.value = 0;
                return oldValue;
            }
        }
        return 0;
    }

    /***
     * Clears this hashtable so that it contains no keys.
     */
    public void clear() {
        Entry[] tab = table;
        for (int index = tab.length; --index >= 0;) {
            tab[index] = null;
        }
        count = 0;
    }

    /***
     * Innerclass that acts as a datastructure to create a new entry in the
     * table.
     */
    public static class Entry {
        int key;
        int value;
        //ArrayList<Integer> values = new ArrayList<Integer>();
        Entry next;

        /**
         * Create a new entry with the given values.
         *
         * @param key The key used to enter this in the table
         * @param value The value for this key
         * @param next A reference to the next entry in the table
         */
        Entry(int key, int value, Entry next) {
            this.key = key;
            this.value = value;
            this.next = next;
            //values.add(value);
        }

        // extra methods for inner class Entry by Paulo
        public int getKey() {
            return key;
        }

        public int getValue() {
            return value;
        }

        @Override
        protected Object clone() {
            return new Entry(key, value, next != null ? (Entry)next.clone() : null);
        }

        @Override
        public String toString() {
            return MessageFormatUtil.format("{0}={1}", key, value);
        }
    }

    public int[] toOrderedKeys() {
        int[] res = getKeys();
        Arrays.sort(res);
        return res;
    }

    public int[] getKeys() {
        int[] res = new int[count];
        int ptr = 0;
        int index = table.length;
        Entry entry = null;
        while (true) {
            if (entry == null)
                while (index-- > 0 && (entry = table[index]) == null);
            if (entry == null)
                break;
            Entry e = entry;
            entry = e.next;
            res[ptr++] = e.key;
        }
        return res;
    }

    public int getOneKey() {
        if (count == 0)
            return 0;
        int index = table.length;
        Entry entry = null;
        while (index-- > 0 && (entry = table[index]) == null);
        if (entry == null)
            return 0;
        return entry.key;
    }

    @Override
    public Object clone() {
        IntHashtable t = new IntHashtable(this);
        t.table = new Entry[table.length];
        for (int i = table.length ; i-- > 0 ; ) {
            t.table[i] = table[i] != null
                    ? (Entry)table[i].clone() : null;
        }
        t.count = count;
        return t;
    }
}

