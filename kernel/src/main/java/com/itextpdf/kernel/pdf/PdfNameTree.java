/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 iText Group NV
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
