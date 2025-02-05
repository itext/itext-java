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

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class PdfNameTree extends GenericNameTree {

    private final PdfCatalog catalog;
    private final PdfName treeType;

    /**
     * Creates the NameTree of current Document
     *
     * @param catalog  Document catalog
     * @param treeType the type of tree. Dests Tree, AP Tree etc.
     */
    public PdfNameTree(PdfCatalog catalog, PdfName treeType) {
        super(catalog.getDocument());
        this.treeType = treeType;
        this.catalog = catalog;
        this.setItems(readFromCatalog());
    }

    /**
     * Retrieves the names stored in the name tree
     *
     * <p>
     * When non-textual names are required, use
     *
     * @return Map containing the PdfObjects stored in the tree
     */
    public Map<PdfString, PdfObject> getNames() {
        return this.getItems();
    }

    private LinkedHashMap<PdfString, PdfObject> readFromCatalog() {
        PdfDictionary namesDict = catalog.getPdfObject().getAsDictionary(PdfName.Names);

        PdfDictionary treeRoot = namesDict == null ? null : namesDict.getAsDictionary(treeType);

        LinkedHashMap<PdfString, PdfObject> items;
        if (treeRoot == null) {
            items = new LinkedHashMap<>();
        } else {
            // readTree() guarantees that the map contains no nulls
            items = readTree(treeRoot);
        }

        if (treeType.equals(PdfName.Dests)) {
            normalizeDestinations(items);
            insertDestsEntriesFromCatalog(items);
        }

        return items;
    }

    private static void normalizeDestinations(Map<PdfString, PdfObject> items) {
        // normalise dest entries to arrays

        // A separate collection for keys is used for auto porting to C#, because in C#
        // it is impossible to change the collection which you iterate in for loop
        Set<PdfString> keys = new HashSet<>(items.keySet());
        for (PdfString key : keys) {
            PdfArray arr = getDestArray(items.get(key));
            if (arr == null) {
                items.remove(key);
            } else {
                items.put(key, arr);
            }
        }
    }

    private void insertDestsEntriesFromCatalog(Map<PdfString, PdfObject> items) {
        // make sure that destinations in the Catalog/Dests dictionary are listed
        // in the destination name tree (if that's what we're working on)
        PdfDictionary destinations = catalog.getPdfObject().getAsDictionary(PdfName.Dests);
        if (destinations != null) {
            Set<PdfName> keys = destinations.keySet();
            for (PdfName key : keys) {
                PdfArray array = getDestArray(destinations.get(key));
                if (array == null) {
                    continue;
                }
                items.put(new PdfString(key.getValue()), array);
            }
        }
    }

    private static PdfArray getDestArray(PdfObject obj) {
        if (obj == null) {
            return null;
        } else if (obj.isArray()) {
            return (PdfArray) obj;
        } else if (obj.isDictionary()) {
            return ((PdfDictionary) obj).getAsArray(PdfName.D);
        } else {
            return null;
        }
    }
}
