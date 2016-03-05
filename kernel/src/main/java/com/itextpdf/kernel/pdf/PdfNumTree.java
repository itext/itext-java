package com.itextpdf.kernel.pdf;

import java.io.Serializable;
import java.util.*;

public class PdfNumTree implements Serializable{
    private static final int NodeSize = 40;

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

    public void addEntry(Integer key, PdfObject value) {
        items.put(key, value);
    }

    public PdfDictionary buildTree() {
        Integer[] numbers = new Integer[items.size()];
        numbers = items.keySet().toArray(numbers);
        Arrays.sort(numbers);
        if (numbers.length <= NodeSize) {
            PdfDictionary dic = new PdfDictionary();
            PdfArray ar = new PdfArray();
            for (int k = 0; k < numbers.length; ++k) {
                ar.add(new PdfNumber(numbers[k]));
                ar.add(items.get(numbers[k]));
            }
            dic.put(PdfName.Nums, ar);
            return dic;
        }
        int skip = NodeSize;
        PdfDictionary[] kids = new PdfDictionary[(numbers.length + NodeSize - 1) / NodeSize];
        for (int k = 0; k < kids.length; ++k) {
            int offset = k * NodeSize;
            int end = Math.min(offset + NodeSize, numbers.length);
            PdfDictionary dic = new PdfDictionary();
            PdfArray arr = new PdfArray();
            arr.add(new PdfNumber(numbers[offset]));
            arr.add(new PdfNumber(numbers[end - 1]));
            dic.put(PdfName.Limits, arr);
            arr = new PdfArray();
            for (; offset < end; ++offset) {
                arr.add(new PdfNumber(numbers[offset]));
                arr.add(items.get(numbers[offset]));
            }
            dic.put(PdfName.Nums, arr);
            dic.makeIndirect(catalog.getDocument());
            kids[k] = dic;
        }
        int top = kids.length;
        while (true) {
            if (top <= NodeSize) {
                PdfArray arr = new PdfArray();
                for (int k = 0; k < top; ++k)
                    arr.add(kids[k]);
                PdfDictionary dic = new PdfDictionary();
                dic.put(PdfName.Kids, arr);
                return dic;
            }
            skip *= NodeSize;
            int tt = (numbers.length + skip - 1 )/ skip;
            for (int k = 0; k < tt; ++k) {
                int offset = k * NodeSize;
                int end = Math.min(offset + NodeSize, top);
                PdfDictionary dic = new PdfDictionary().makeIndirect(catalog.getDocument());
                PdfArray arr = new PdfArray();
                arr.add(new PdfNumber(numbers[k * skip]));
                arr.add(new PdfNumber(numbers[Math.min((k + 1) * skip, numbers.length) - 1]));
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
                    items.put(number.getIntValue(), nums.get(k));
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
