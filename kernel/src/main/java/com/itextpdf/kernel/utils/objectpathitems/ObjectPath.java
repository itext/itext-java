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
package com.itextpdf.kernel.utils.objectpathitems;

import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.kernel.pdf.PdfIndirectReference;
import com.itextpdf.kernel.pdf.PdfName;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Class that helps to find two corresponding objects in the compared documents and also keeps track of the
 * already met during comparing process parent indirect objects.
 * <p>
 * You could say that CompareObjectPath instance consists of two parts: direct path and indirect path.
 * Direct path defines path to the currently comparing objects in relation to base objects. It could be empty,
 * which would mean that currently comparing objects are base objects themselves. Base objects are the two indirect
 * objects from the comparing documents which are in the same position in the pdf trees. Another part, indirect path,
 * defines which indirect objects were met during comparison process to get to the current base objects. Indirect path
 * is needed to avoid infinite loops during comparison.
 */
public class ObjectPath {
    protected Stack<LocalPathItem> path = new Stack<>();
    private PdfIndirectReference baseCmpObject;
    private PdfIndirectReference baseOutObject;
    private Stack<IndirectPathItem> indirects = new Stack<>();

    /**
     * Creates empty ObjectPath.
     */
    public ObjectPath() {
    }

    /**
     * Creates an {@link ObjectPath} object from another {@link ObjectPath} object, passed as argument.
     *
     * @param objectPath an {@link ObjectPath} object to create from.
     */
    public ObjectPath(ObjectPath objectPath) {
        this.baseCmpObject = objectPath.getBaseCmpObject();
        this.baseOutObject = objectPath.getBaseOutObject();
        this.path = (Stack<LocalPathItem>) objectPath.getLocalPath();
        this.indirects = (Stack<IndirectPathItem>) objectPath.getIndirectPath();
    }

    /**
     * Creates CompareObjectPath with corresponding base objects in two documents.
     *
     * @param baseCmpObject base object in cmp document.
     * @param baseOutObject base object in out document.
     */
    public ObjectPath(PdfIndirectReference baseCmpObject, PdfIndirectReference baseOutObject) {
        this.baseCmpObject = baseCmpObject;
        this.baseOutObject = baseOutObject;
        indirects.push(new IndirectPathItem(baseCmpObject, baseOutObject));
    }

    /**
     * Creates CompareObjectPath with corresponding base objects in two documents.
     *
     * @param baseCmpObject base object in cmp document
     * @param baseOutObject base object in out document
     * @param path          local path that denotes sequence of the path items from base object
     *                      to the comparing direct object
     * @param indirects     indirect path which denotes sequence of the indirect references that
     *                      were passed in comparing process to get to the current base objects
     */
    public ObjectPath(PdfIndirectReference baseCmpObject, PdfIndirectReference baseOutObject,
                      Stack<LocalPathItem> path, Stack<IndirectPathItem> indirects) {
        this.baseCmpObject = baseCmpObject;
        this.baseOutObject = baseOutObject;
        this.path = (Stack<LocalPathItem>) path.clone();
        this.indirects = (Stack<IndirectPathItem>) indirects.clone();
    }

    /**
     * Creates a new ObjectPath instance with two new given base objects, which are supposed to be nested in the base
     * objects of the current instance of the ObjectPath. This method is used to avoid infinite loop in case of
     * circular references in pdf documents objects structure.
     * <p>
     * Basically, this method creates copy of the current CompareObjectPath instance, but resets
     * information of the direct paths, and also adds current CompareObjectPath instance base objects to the indirect
     * references chain that denotes a path to the new base objects.
     *
     * @param baseCmpObject new base object in cmp document.
     * @param baseOutObject new base object in out document.
     * @return new ObjectPath instance, which stores chain of the indirect references
     * which were already met to get to the new base objects.
     */
    public ObjectPath resetDirectPath(PdfIndirectReference baseCmpObject, PdfIndirectReference baseOutObject) {
        final ObjectPath newPath = new ObjectPath(baseCmpObject, baseOutObject,
                new Stack<LocalPathItem>(), (Stack<IndirectPathItem>) indirects.clone());
        newPath.indirects.push(new IndirectPathItem(baseCmpObject, baseOutObject));
        return newPath;
    }

    /**
     * This method is used to define if given objects were already met in the path to the current base objects.
     * If this method returns true it basically means that we found a loop in the objects structure and that we
     * already compared these objects.
     *
     * @param cmpObject cmp object to check if it was already met in base objects path.
     * @param outObject out object to check if it was already met in base objects path.
     * @return true if given objects are contained in the path and therefore were already compared.
     */
    public boolean isComparing(PdfIndirectReference cmpObject, PdfIndirectReference outObject) {
        return indirects.contains(new IndirectPathItem(cmpObject, outObject));
    }

    /**
     * Adds array item to the direct path. See {@link ArrayPathItem}.
     *
     * @param index index in the array of the direct object to be compared.
     */
    public void pushArrayItemToPath(int index) {
        path.push(new ArrayPathItem(index));
    }

    /**
     * Adds dictionary item to the direct path. See {@link DictPathItem}.
     *
     * @param key key in the dictionary to which corresponds direct object to be compared.
     */
    public void pushDictItemToPath(PdfName key) {
        path.push(new DictPathItem(key));
    }

    /**
     * Adds offset item to the direct path. See {@link OffsetPathItem}.
     *
     * @param offset offset to the specific byte in the stream that is compared.
     */
    public void pushOffsetToPath(int offset) {
        path.push(new OffsetPathItem(offset));
    }

    /**
     * Removes the last path item from the direct path.
     */
    public void pop() {
        path.pop();
    }

    /**
     * Gets local (or direct) path that denotes sequence of the path items from base object to the comparing
     * direct object.
     *
     * @return direct path to the comparing object.
     */
    public Stack<LocalPathItem> getLocalPath() {
        return (Stack<LocalPathItem>) path.clone();
    }

    /**
     * Gets indirect path which denotes sequence of the indirect references that were passed in comparing process
     * to get to the current base objects.
     *
     * @return indirect path to the current base objects.
     */
    public Stack<IndirectPathItem> getIndirectPath() {
        return (Stack<IndirectPathItem>) indirects.clone();
    }

    /**
     * Method returns current base {@link PdfIndirectReference} object in the cmp document.
     *
     * @return current base {@link PdfIndirectReference} object in the cmp document.
     */
    public PdfIndirectReference getBaseCmpObject() {
        return baseCmpObject;
    }

    /**
     * Method returns current base {@link PdfIndirectReference} object in the out document.
     *
     * @return current base object in the out document.
     */
    public PdfIndirectReference getBaseOutObject() {
        return baseOutObject;
    }

    /**
     * Creates an xml node that describes a direct path stored in this ObjectPath instance.
     *
     * @param document xml document, to which this xml node will be added.
     * @return an xml node describing direct path.
     */
    public Node toXmlNode(Document document) {
        final Element element = document.createElement("path");
        final Element baseNode = document.createElement("base");
        baseNode.setAttribute("cmp", MessageFormatUtil.format("{0} {1} obj", baseCmpObject.getObjNumber(),
                baseCmpObject.getGenNumber()));
        baseNode.setAttribute("out", MessageFormatUtil.format("{0} {1} obj", baseOutObject.getObjNumber(),
                baseOutObject.getGenNumber()));
        element.appendChild(baseNode);
        final Stack<LocalPathItem> pathClone = (Stack<LocalPathItem>) path.clone();
        final List<LocalPathItem> localPathItems = new ArrayList<>(path.size());
        for (int i = 0; i < path.size(); ++i) {
            localPathItems.add(pathClone.pop());
        }

        for (int i = localPathItems.size() - 1; i >= 0; --i) {
            element.appendChild(localPathItems.get(i).toXmlNode(document));
        }
        return element;
    }

    /**
     * Method returns a string representation of the direct path stored in this {@link ObjectPath} instance.
     *
     * @return a string representation of the direct path.
     */
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(MessageFormatUtil.format("Base cmp object: {0} obj. Base out object: {1} obj", baseCmpObject,
                baseOutObject));

        final Stack<LocalPathItem> pathClone = (Stack<LocalPathItem>) path.clone();
        final List<LocalPathItem> localPathItems = new ArrayList<>(path.size());
        for (int i = 0; i < path.size(); ++i) {
            localPathItems.add(pathClone.pop());
        }
        for (int i = localPathItems.size() - 1; i >= 0; --i) {
            sb.append('\n');
            sb.append(localPathItems.get(i).toString());
        }
        return sb.toString();
    }

    /**
     * Method returns a hash code of this {@link ObjectPath} instance.
     *
     * @return a int hash code of this {@link ObjectPath} instance.
     */
    @Override
    public int hashCode() {
        // TODO: DEVSIX-4756 indirect reference hashCode should use hashCode method of indirect
        //  reference. For now we need to write custom logic as some tests rely on sequential
        //  reopening of the same document which affects with not equal indirect reference
        //  hashCodes (after the update which starts counting the document in indirect reference
        //  hashCode)
        int baseCmpObjectHashCode = 0;
        if (baseCmpObject != null) {
            baseCmpObjectHashCode = baseCmpObject.getObjNumber() * 31 + baseCmpObject.getGenNumber();
        }
        int baseOutObjectHashCode = 0;
        if (baseOutObject != null) {
            baseOutObjectHashCode = baseOutObject.getObjNumber() * 31 + baseOutObject.getGenNumber();
        }

        int hashCode = baseCmpObjectHashCode * 31 + baseOutObjectHashCode;
        for (LocalPathItem pathItem : path) {
            hashCode *= 31;
            hashCode += pathItem.hashCode();
        }
        return hashCode;
    }

    /**
     * Method returns true if this {@link ObjectPath} instance equals to the passed object.
     *
     * @return true - if this {@link ObjectPath} instance equals to the passed object.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        ObjectPath that = (ObjectPath) obj;

        // TODO: DEVSIX-4756 indirect reference comparing should use equals method of indirect
        //  reference. For now we need to write custom logic as some tests rely on sequential
        //  reopening of the same document which affects with not equal indirect references
        //  (after the update which starts counting the document in indirect reference equality)
        boolean isBaseCmpObjectEqual;
        if (baseCmpObject == that.baseCmpObject) {
            isBaseCmpObjectEqual = true;
        } else if (baseCmpObject == null
                || that.baseCmpObject == null
                || baseCmpObject.getClass() != that.baseCmpObject.getClass()) {
            isBaseCmpObjectEqual = false;
        } else {
            isBaseCmpObjectEqual = baseCmpObject.getObjNumber() == that.baseCmpObject.getObjNumber()
                    && baseCmpObject.getGenNumber() == that.baseCmpObject.getGenNumber();
        }
        boolean isBaseOutObjectEqual;
        if (baseOutObject == that.baseOutObject) {
            isBaseOutObjectEqual = true;
        } else if (baseOutObject == null
                || that.baseOutObject == null
                || baseOutObject.getClass() != that.baseOutObject.getClass()) {
            isBaseOutObjectEqual = false;
        } else {
            isBaseOutObjectEqual = baseOutObject.getObjNumber() == that.baseOutObject.getObjNumber()
                    && baseOutObject.getGenNumber() == that.baseOutObject.getGenNumber();
        }

        return isBaseCmpObjectEqual && isBaseOutObjectEqual && path.equals(((ObjectPath) obj).path);
    }
}
