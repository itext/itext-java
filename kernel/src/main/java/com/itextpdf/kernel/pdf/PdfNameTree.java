/*
    $Id$

    This file is part of the iText (R) project.
    Copyright (c) 1998-2016 iText Group NV
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
package com.itextpdf.kernel.pdf;

import com.itextpdf.kernel.PdfException;

import java.util.*;

public class PdfNameTree {

    private static final int NodeSize = 40;

    private PdfCatalog catalog;
    private Map<String, PdfObject> items = new HashMap<>();
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


    public Map<String, PdfObject> getNames() {
        if (items.size() > 0) {
            return items;
        }

        PdfDictionary dictionary = catalog.getPdfObject().getAsDictionary(PdfName.Names);
        if (dictionary != null) {
            dictionary = dictionary.getAsDictionary(treeType);
            if (dictionary != null) {
                items = readTree(dictionary);
                for (Iterator<Map.Entry<String, PdfObject>> it = items.entrySet().iterator(); it.hasNext(); ) {
                    Map.Entry<String, PdfObject> entry = it.next();
                    PdfArray arr = getNameArray(entry.getValue());
                    if (arr != null)
                        entry.setValue(arr);
                    else
                        it.remove();
                }
            }
        }

        if (treeType.equals(PdfName.Dests)) {
            PdfDictionary destinations = catalog.getPdfObject().getAsDictionary(PdfName.Dests);
            if (destinations != null) {
                Set<PdfName> keys = destinations.keySet();
                for (PdfName key : keys) {
                    PdfArray array = getNameArray(destinations.get(key));
                    if (array == null) {
                        continue;
                    }
                    items.put(key.getValue(), array);
                }
            }
        }

        return items;
    }

    public void addEntry(String key, PdfObject value) {
        if (items.keySet().contains(key)) {
            throw new PdfException(PdfException.NameAlreadyExistsInTheNameTree);
        }
        modified = true;
        items.put(key, value);
    }

    public boolean isModified() {
        return modified;
    }

    public PdfDictionary buildTree() {
        String[] names = new String[items.size()];
        names = items.keySet().toArray(names);
        Arrays.sort(names);
        if (names.length <= NodeSize) {
            PdfDictionary dic = new PdfDictionary();
            PdfArray ar = new PdfArray();
            for (int k = 0; k < names.length; ++k) {
                ar.add(new PdfString(names[k], null));
                ar.add(items.get(names[k]));
            }
            dic.put(PdfName.Names, ar);
            return dic;
        }
        int skip = NodeSize;
        PdfDictionary[] kids = new PdfDictionary[(names.length + NodeSize - 1) / NodeSize];
        for (int k = 0; k < kids.length; ++k) {
            int offset = k * NodeSize;
            int end = Math.min(offset + NodeSize, names.length);
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
            if (top <= NodeSize) {
                PdfArray arr = new PdfArray();
                for (int k = 0; k < top; ++k)
                    arr.add(kids[k]);
                PdfDictionary dic = new PdfDictionary();
                dic.put(PdfName.Kids, arr);
                return dic;
            }
            skip *= NodeSize;
            int tt = (names.length + skip - 1 )/ skip;
            for (int k = 0; k < tt; ++k) {
                int offset = k * NodeSize;
                int end = Math.min(offset + NodeSize, top);
                PdfDictionary dic = new PdfDictionary().makeIndirect(catalog.getDocument());
                PdfArray arr = new PdfArray();
                arr.add(new PdfString(names[k * skip], null));
                arr.add(new PdfString(names[Math.min((k + 1) * skip, names.length) - 1], null));
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

    private Map<String, PdfObject> readTree(PdfDictionary dictionary) {
        Map<String, PdfObject> items = new HashMap<String, PdfObject>();
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

    private PdfArray getNameArray(PdfObject obj) {
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
