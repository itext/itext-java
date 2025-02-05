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
package com.itextpdf.kernel.pdf;

import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.io.logs.IoLogMessageConstant;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract representation of a name tree structure, as used in PDF for various purposes
 * such as the Dests tree, the ID tree of structure elements and the embedded file tree.
 */
public class GenericNameTree implements IPdfNameTreeAccess {

    private static final Logger LOGGER = LoggerFactory.getLogger(GenericNameTree.class);
    private static final int NODE_SIZE = 40;

    private LinkedHashMap<PdfString, PdfObject> items = new LinkedHashMap<>();
    private final PdfDocument pdfDoc;
    private boolean modified;

    /**
     * Creates a name tree structure in the current document.
     *
     * @param pdfDoc the document in which the name tree lives
     */
    protected GenericNameTree(PdfDocument pdfDoc) {
        this.pdfDoc = pdfDoc;
    }

    /**
     * Add an entry to the name tree.
     *
     * @param key   key of the entry
     * @param value object to add
     */
    public void addEntry(PdfString key, PdfObject value) {
        addEntry(key, value, null);
    }

    /**
     * Add an entry to the name tree.
     *
     * @param key   key of the entry
     * @param value object to add
     */
    public void addEntry(String key, PdfObject value) {
        this.addEntry(new PdfString(key, null), value);
    }

    /**
     * Remove an entry from the name tree.
     *
     * @param key   key of the entry
     */
    public void removeEntry(PdfString key) {
        PdfObject existingVal = items.remove(key);
        // ensure that we mark the tree as modified if the key was present
        if (existingVal != null) {
            modified = true;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PdfObject getEntry(PdfString key) {
        return this.items.get(key);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PdfObject getEntry(String key) {
        return getEntry(new PdfString(key));
    }

    @Override
    public Set<PdfString> getKeys() {
        // return a copy so that the underlying tree can be modified while iterating over the keys
        return new LinkedHashSet<>(this.items.keySet());
    }

    /**
     * Check if the tree is modified.
     *
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
     * Build a {@link PdfDictionary} containing the name tree.
     *
     * @return {@link PdfDictionary} containing the name tree
     */
    public PdfDictionary buildTree() {
        final PdfString[] names = items.keySet().toArray(new PdfString[0]);

        Arrays.sort(names, new PdfStringComparator());
        if (names.length <= NODE_SIZE) {
            // This is the simple case where all entries fit into one node
            PdfDictionary dic = new PdfDictionary();
            PdfArray ar = new PdfArray();
            for (PdfString name : names) {
                ar.add(name);
                ar.add(items.get(name));
            }
            dic.put(PdfName.Names, ar);
            return dic;
        }
        PdfDictionary[] leaves = constructLeafArr(names);
        // recursively refine the tree to balance it.
        return reduceTree(names, leaves, leaves.length, NODE_SIZE * NODE_SIZE);
    }

    /**
     * Add an entry to the name tree.
     *
     * @param key   key of the entry
     * @param value object to add
     * @param onErrorAction action to perform if such entry exists
     */
    protected void addEntry(PdfString key, PdfObject value, Consumer<PdfDocument> onErrorAction) {
        final PdfObject existingVal = items.get(key);
        if (existingVal != null) {
            final PdfIndirectReference valueRef = value.getIndirectReference();
            if (valueRef != null && valueRef.equals(existingVal.getIndirectReference())) {
                return;
            } else {
                LOGGER.warn(MessageFormatUtil.format(IoLogMessageConstant.NAME_ALREADY_EXISTS_IN_THE_NAME_TREE, key));
                if (onErrorAction != null) {
                    onErrorAction.accept(pdfDoc);
                }
            }
        }
        modified = true;
        items.put(key, value);
    }

    protected final void setItems(LinkedHashMap<PdfString, PdfObject> items) {
        this.items = items;
    }

    protected final LinkedHashMap<PdfString, PdfObject> getItems() {
        return this.items;
    }

    /**
     * Read the entries in a name tree structure from a dictionary object into a linked hash map
     * with fixed order.
     *
     * @param dictionary a dictionary object
     * @return a map containing the entries in the tree
     */
    protected static LinkedHashMap<PdfString, PdfObject> readTree(PdfDictionary dictionary) {
        LinkedHashMap<PdfString, PdfObject> items = new LinkedHashMap<>();
        if (dictionary != null) {
            iterateItems(dictionary, items, null);
        }
        return items;
    }

    private PdfDictionary formatNodeWithLimits(PdfString[] names, int lower, int upper) {
        PdfDictionary dic = new PdfDictionary();
        dic.makeIndirect(this.pdfDoc);
        PdfArray limitsArr = new PdfArray();
        limitsArr.add(names[lower]);
        limitsArr.add(names[upper]);
        dic.put(PdfName.Limits, limitsArr);
        return dic;
    }

    private PdfDictionary reduceTree(PdfString[] names, PdfDictionary[] topLayer,
                                     int topLayerLen, int curNodeSpan) {
        // We group nodes of the tree until the top layer contains
        // fewer than NODE_SIZE children
        if (topLayerLen <= NODE_SIZE) {
            // We're done, just pack up the root node
            PdfArray kidsArr = new PdfArray();
            for (int i = 0; i < topLayerLen; ++i) {
                kidsArr.add(topLayer[i]);
            }
            PdfDictionary root = new PdfDictionary();
            root.put(PdfName.Kids, kidsArr);
            return root;
        }
        // Break up the nodes of the current top layer into batches
        // and turn those into the nodes of the next layer,
        // which we write to our running topLayer array
        int nextLayerLen = (names.length + curNodeSpan - 1) / curNodeSpan;
        for (int i = 0; i < nextLayerLen; ++i) {
            int lowerLimit = i * curNodeSpan;
            int upperLimit = Math.min((i + 1) * curNodeSpan, names.length) - 1;
            PdfDictionary dic = formatNodeWithLimits(names, lowerLimit, upperLimit);
            PdfArray kidsArr = new PdfArray();
            int offset = i * NODE_SIZE;
            int end = Math.min(offset + NODE_SIZE, topLayerLen);
            for (; offset < end; ++offset) {
                kidsArr.add(topLayer[offset]);
            }
            dic.put(PdfName.Kids, kidsArr);
            topLayer[i] = dic;
        }
        // and finally recurse
        return reduceTree(names, topLayer, nextLayerLen, curNodeSpan * NODE_SIZE);
    }

    private PdfDictionary[] constructLeafArr(PdfString[] names) {
        PdfDictionary[] leaves = new PdfDictionary[(names.length + NODE_SIZE - 1) / NODE_SIZE];
        for (int k = 0; k < leaves.length; ++k) {
            int offset = k * NODE_SIZE;
            int end = Math.min(offset + NODE_SIZE, names.length);
            PdfDictionary dic = formatNodeWithLimits(names, offset, end - 1);
            PdfArray namesArr = new PdfArray();
            for (; offset < end; ++offset) {
                namesArr.add(names[offset]);
                namesArr.add(items.get(names[offset]));
            }
            dic.put(PdfName.Names, namesArr);
            dic.makeIndirect(this.pdfDoc);
            leaves[k] = dic;
        }
        return leaves;
    }

    private static PdfString iterateItems(PdfDictionary dictionary,
                                          Map<PdfString, PdfObject> items,
                                          PdfString leftOver) {
        /* Maintainer note:
        The leftOver parameter originates with commit c7a832e in iText 5,
        and exists to gracefully deal with PDF files where the name tree
        contains name arrays that are broken up across multiple nodes.
         */
        PdfArray names = dictionary.getAsArray(PdfName.Names);
        PdfArray kids = dictionary.getAsArray(PdfName.Kids);
        boolean isLeafNode = names != null && names.size() > 0;
        boolean isIntermNode = kids != null && kids.size() > 0;
        if (isLeafNode) {
            return iterateLeafNode(names, items, leftOver);
        } else if (isIntermNode) {
            // Intermediate node
            PdfString curLeftOver = leftOver;
            for (int k = 0; k < kids.size(); k++) {
                PdfDictionary kid = kids.getAsDictionary(k);
                curLeftOver = iterateItems(kid, items, curLeftOver);
            }
            return curLeftOver;
        } else {
            return leftOver;
        }
    }

    private static PdfString iterateLeafNode(PdfArray names,
                                             Map<PdfString, PdfObject> items,
                                             PdfString leftOver) {

        // Recall: Names is an array of pairs:
        //  [name1 ref1 name2 ref2 ...]
        int k = 0;
        if (leftOver != null) {
            // in the leftover case, we expect the first
            // element to be a value, so go ahead and process it
            // (we know that names.size() > 0)
            items.put(leftOver, names.get(0));
            // skip the first entry and proceed as usual
            k++;
        }
        // for each (name, ref) pair, register an entry
        while (k < names.size()) {
            PdfString name = names.getAsString(k);
            k++;
            if (k == names.size()) {
                // trailing name -> bail
                return name;
            }
            if (name != null) {
                items.put(name, names.get(k));
            }
            k++;
        }
        return null;
    }

}
