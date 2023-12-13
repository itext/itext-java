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
package com.itextpdf.kernel.pdf;

import com.itextpdf.io.LogMessageConstant;
import com.itextpdf.io.util.MessageFormatUtil;

import java.util.LinkedHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PdfNameTree implements Serializable {

    private static final int NODE_SIZE = 40;
    private static final long serialVersionUID = 8153711383828989907L;

    private PdfCatalog catalog;
    private Map<String, PdfObject> items = new LinkedHashMap<>();
    private PdfName treeType;
    private boolean modified;

    /**
     * Creates the NameTree of current Document
     *
     * @param catalog  Document catalog
     * @param treeType the type of tree. Dests Tree, AP Tree etc.
     */
    public PdfNameTree(PdfCatalog catalog, PdfName treeType) {
        this.treeType = treeType;
        this.catalog = catalog;
        items = getNames();
    }

    /**
     * Retrieves the names stored in the name tree
     *
     * @return Map containing the PdfObjects stored in the tree
     */
    public Map<String, PdfObject> getNames() {
        if (items.size() > 0) {
            return items;
        }

        PdfDictionary dictionary = catalog.getPdfObject().getAsDictionary(PdfName.Names);
        if (dictionary != null) {
            dictionary = dictionary.getAsDictionary(treeType);
            if (dictionary != null) {
                items = readTree(dictionary);
                // A separate collection for keys is used for auto porting to C#, because in C#
                // it is impossible to change the collection which you iterate in for loop
                Set<String> keys = new HashSet<>();
                keys.addAll(items.keySet());
                for (String key : keys) {
                    if (treeType.equals(PdfName.Dests)) {
                        PdfArray arr = getDestArray(items.get(key));
                        if (arr != null) {
                            items.put(key, arr);
                        } else
                            items.remove(key);
                    } else if (items.get(key) == null)
                        items.remove(key);
                }
            }
        }

        if (treeType.equals(PdfName.Dests)) {
            PdfDictionary destinations = catalog.getPdfObject().getAsDictionary(PdfName.Dests);
            if (destinations != null) {
                Set<PdfName> keys = destinations.keySet();
                for (PdfName key : keys) {
                    PdfArray array = getDestArray(destinations.get(key));
                    if (array == null) {
                        continue;
                    }
                    items.put(key.getValue(), array);
                }
            }
        }
        return items;
    }

    /**
     * Add an entry to the name tree
     *
     * @param key   key of the entry
     * @param value object to add
     */
    public void addEntry(String key, PdfObject value) {
        PdfObject existingVal = items.get(key);
        if (existingVal != null) {
            if (value.getIndirectReference() != null && value.getIndirectReference().equals(existingVal.getIndirectReference())) {
                return;
            } else {
                Logger logger = LoggerFactory.getLogger(PdfNameTree.class);
                logger.warn(MessageFormatUtil.format(LogMessageConstant.NAME_ALREADY_EXISTS_IN_THE_NAME_TREE, key));
            }
        }
        modified = true;
        items.put(key, value);
    }

    /**
     * @return True if the object has been modified, false otherwise.
     */
    public boolean isModified() {
        return modified;
    }

    /**
     * Sets the modified flag to true. It means that the object has been modified.
     */
    public void setModified() {
        modified = true;
    }

    /**
     * Build a PdfDictionary containing the name tree
     *
     * @return PdfDictionary containing the name tree
     */
    public PdfDictionary buildTree() {
        String[] names = new String[items.size()];
        names = items.keySet().toArray(names);
        Arrays.sort(names);
        if (names.length <= NODE_SIZE) {
            PdfDictionary dic = new PdfDictionary();
            PdfArray ar = new PdfArray();
            for (String name : names) {
                ar.add(new PdfString(name, null));
                ar.add(items.get(name));
            }
            dic.put(PdfName.Names, ar);
            return dic;
        }
        int skip = NODE_SIZE;
        PdfDictionary[] kids = new PdfDictionary[(names.length + NODE_SIZE - 1) / NODE_SIZE];
        for (int k = 0; k < kids.length; ++k) {
            int offset = k * NODE_SIZE;
            int end = Math.min(offset + NODE_SIZE, names.length);
            PdfDictionary dic = new PdfDictionary();
            PdfArray arr = new PdfArray();
            arr.add(new PdfString(names[offset], null));
            arr.add(new PdfString(names[end - 1], null));
            dic.put(PdfName.Limits, arr);
            arr = new PdfArray();
            for (; offset < end; ++offset) {
                arr.add(new PdfString(names[offset], null));
                arr.add(items.get(names[offset]));
            }
            dic.put(PdfName.Names, arr);
            dic.makeIndirect(catalog.getDocument());
            kids[k] = dic;
        }
        int top = kids.length;
        while (true) {
            if (top <= NODE_SIZE) {
                PdfArray arr = new PdfArray();
                for (int i = 0; i < top; ++i)
                    arr.add(kids[i]);
                PdfDictionary dic = new PdfDictionary();
                dic.put(PdfName.Kids, arr);
                return dic;
            }
            skip *= NODE_SIZE;
            int tt = (names.length + skip - 1) / skip;
            for (int i = 0; i < tt; ++i) {
                int offset = i * NODE_SIZE;
                int end = Math.min(offset + NODE_SIZE, top);
                PdfDictionary dic = (PdfDictionary) new PdfDictionary().makeIndirect(catalog.getDocument());
                PdfArray arr = new PdfArray();
                arr.add(new PdfString(names[i * skip], null));
                arr.add(new PdfString(names[Math.min((i + 1) * skip, names.length) - 1], null));
                dic.put(PdfName.Limits, arr);
                arr = new PdfArray();
                for (; offset < end; ++offset) {
                    arr.add(kids[offset]);
                }
                dic.put(PdfName.Kids, arr);
                kids[i] = dic;
            }
            top = tt;
        }
    }

    private Map<String, PdfObject> readTree(PdfDictionary dictionary) {
        Map<String, PdfObject> items = new LinkedHashMap<>();
        if (dictionary != null) {
            iterateItems(dictionary, items, null);
        }
        return items;
    }

    private PdfString iterateItems(PdfDictionary dictionary, Map<String, PdfObject> items, PdfString leftOver) {
        PdfArray names = dictionary.getAsArray(PdfName.Names);
        if (names != null) {
            for (int k = 0; k < names.size(); k++) {
                PdfString name;
                if (leftOver == null)
                    name = names.getAsString(k++);
                else {
                    name = leftOver;
                    leftOver = null;
                }
                if (k < names.size()) {
                    items.put(name.toUnicodeString(), names.get(k));
                } else {
                    return name;
                }
            }
        } else if ((names = dictionary.getAsArray(PdfName.Kids)) != null) {
            for (int k = 0; k < names.size(); k++) {
                PdfDictionary kid = names.getAsDictionary(k);
                leftOver = iterateItems(kid, items, leftOver);
            }
        }
        return null;
    }

    private PdfArray getDestArray(PdfObject obj) {
        if (obj == null)
            return null;
        if (obj.isArray())
            return (PdfArray) obj;
        else if (obj.isDictionary()) {
            PdfArray arr = ((PdfDictionary) obj).getAsArray(PdfName.D);
            return arr;
        }
        return null;
    }
}
