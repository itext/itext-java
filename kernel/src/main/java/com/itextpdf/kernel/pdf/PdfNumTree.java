/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2024 Apryse Group NV
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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public class PdfNumTree {


    private static final int NODE_SIZE = 40;

    private PdfCatalog catalog;
    private Map<Integer, PdfObject> items = new HashMap<>();
    private PdfName treeType;

    /**
     * Creates the NumberTree of current Document
     *
     * @param catalog  Document catalog
     * @param treeType the type of tree. ParentTree or PageLabels.
     *
     */
    public PdfNumTree(PdfCatalog catalog, PdfName treeType) {
        this.treeType = treeType;
        this.catalog = catalog;
    }


    public Map<Integer, PdfObject> getNumbers() {
        if (items.size() > 0) {
            return items;
        }

        PdfDictionary numbers = null;
        if (treeType.equals(PdfName.PageLabels)) {
            numbers = catalog.getPdfObject().getAsDictionary(PdfName.PageLabels);
        } else if (treeType.equals(PdfName.ParentTree)) {
            PdfDictionary structTreeRoot = catalog.getPdfObject().getAsDictionary(PdfName.StructTreeRoot);
            if (structTreeRoot != null) {
                numbers = structTreeRoot.getAsDictionary(PdfName.ParentTree);
            }
        }

        if (numbers != null) {
            readTree(numbers);
        }

        return items;
    }

    public void addEntry(int key, PdfObject value) { items.put(new Integer(key), value); }

    public PdfDictionary buildTree() {
        Integer[] numbers = new Integer[items.size()];
        numbers = items.keySet().toArray(numbers);
        Arrays.sort(numbers);
        if (numbers.length <= NODE_SIZE) {
            PdfDictionary dic = new PdfDictionary();
            PdfArray ar = new PdfArray();
            for (Integer number : numbers) {
                ar.add(new PdfNumber((int) number));
                ar.add(items.get(number));
            }
            dic.put(PdfName.Nums, ar);
            return dic;
        }
        int skip = NODE_SIZE;
        PdfDictionary[] kids = new PdfDictionary[(numbers.length + NODE_SIZE - 1) / NODE_SIZE];
        for (int i = 0; i < kids.length; ++i) {
            int offset = i * NODE_SIZE;
            int end = Math.min(offset + NODE_SIZE, numbers.length);
            PdfDictionary dic = new PdfDictionary();
            PdfArray arr = new PdfArray();
            arr.add(new PdfNumber((int) numbers[offset]));
            arr.add(new PdfNumber((int) numbers[end - 1]));
            dic.put(PdfName.Limits, arr);
            arr = new PdfArray();
            for (; offset < end; ++offset) {
                arr.add(new PdfNumber((int) numbers[offset]));
                arr.add(items.get(numbers[offset]));
            }
            dic.put(PdfName.Nums, arr);
            dic.makeIndirect(catalog.getDocument());
            kids[i] = dic;
        }
        int top = kids.length;
        while (true) {
            if (top <= NODE_SIZE) {
                PdfArray arr = new PdfArray();
                for (int k = 0; k < top; ++k)
                    arr.add(kids[k]);
                PdfDictionary dic = new PdfDictionary();
                dic.put(PdfName.Kids, arr);
                return dic;
            }
            skip *= NODE_SIZE;
            int tt = (numbers.length + skip - 1 )/ skip;
            for (int k = 0; k < tt; ++k) {
                int offset = k * NODE_SIZE;
                int end = Math.min(offset + NODE_SIZE, top);
                PdfDictionary dic = (PdfDictionary) new PdfDictionary().makeIndirect(catalog.getDocument());
                PdfArray arr = new PdfArray();
                arr.add(new PdfNumber((int) numbers[k * skip]));
                arr.add(new PdfNumber((int) numbers[Math.min((k + 1) * skip, numbers.length) - 1]));
                dic.put(PdfName.Limits, arr);
                arr = new PdfArray();
                for (; offset < end; ++offset) {
                    arr.add(kids[offset]);
                }
                dic.put(PdfName.Kids, arr);
                kids[k] = dic;
            }
            top = tt;
        }
    }

    private void readTree(PdfDictionary dictionary) {
        if (dictionary != null) {
            iterateItems(dictionary, null);
        }
    }

    private PdfNumber iterateItems(PdfDictionary dictionary, PdfNumber leftOver) {
        PdfArray nums = dictionary.getAsArray(PdfName.Nums);
        if (nums != null) {
            for (int k = 0; k < nums.size(); k++) {
                PdfNumber number;
                if (leftOver == null)
                    number = nums.getAsNumber(k++);
                else {
                    number = leftOver;
                    leftOver = null;
                }
                if (k < nums.size()) {
                    items.put(number.intValue(), nums.get(k));
                } else {
                    return number;
                }
            }
        } else if ((nums = dictionary.getAsArray(PdfName.Kids)) != null) {
            for (int k = 0; k < nums.size(); k++) {
                PdfDictionary kid = nums.getAsDictionary(k);
                leftOver = iterateItems(kid, leftOver);
            }
        }
        return null;
    }
}
