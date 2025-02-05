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
package com.itextpdf.kernel.utils;

import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfIndirectReference;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfOutputStream;

import java.util.HashMap;
import java.util.Map;

/**
 * This class can be used to count the number of bytes needed when copying
 * pages from an existing PDF into a newly created PDF.
 */
public class PdfResourceCounter {

    /**
     * A map of the resources that are already taken into account
     */
    private Map<Integer, PdfObject> resources;

    /**
     * Creates a PdfResourceCounter instance to be used to count the resources
     * needed for either a page (in this case pass a page dictionary) or the
     * trailer (root and info dictionary) of a PDF file.
     *
     * @param obj the object we want to examine
     */
    public PdfResourceCounter(PdfObject obj) {
        resources = new HashMap<>();
        process(obj);
    }

    /**
     * Processes an object. If the object is indirect, it is added to the
     * list of resources. If not, it is just processed.
     *
     * @param obj the object to process
     */
    protected final void process(PdfObject obj) {
        PdfIndirectReference ref = obj.getIndirectReference();
        if (ref == null) {
            loopOver(obj);
        } else if (!resources.containsKey(ref.getObjNumber())) {
            resources.put(ref.getObjNumber(), obj);
            loopOver(obj);
        }
    }

    /**
     * In case an object is an array, a dictionary or a stream,
     * we need to loop over the entries and process them one by one.
     *
     * @param obj the object to examine
     */
    protected final void loopOver(PdfObject obj) {
        switch (obj.getType()) {
            case PdfObject.ARRAY:
                PdfArray array = (PdfArray) obj;

                for (int i = 0; i < array.size(); i++) {
                    process(array.get(i));
                }

                break;
            case PdfObject.DICTIONARY:
            case PdfObject.STREAM:
                PdfDictionary dict = (PdfDictionary) obj;

                if (PdfName.Pages.equals(dict.get(PdfName.Type))) {
                    break;
                }

                for (PdfName name : dict.keySet()) {
                    process(dict.get(name));
                }

                break;
        }
    }

    /**
     * Returns a map with the resources.
     *
     * @return the resources
     */
    public Map<Integer, PdfObject> getResources() {
        return resources;
    }

    /**
     * Returns the resources needed for the object that was used to create
     * this PdfResourceCounter. If you pass a Map with resources that were
     * already used by other objects, these objects will not be taken into
     * account.
     *
     * @param res The resources that can be excluded when counting the bytes.
     * @return The number of bytes needed for an object.
     */
    public long getLength(Map<Integer, PdfObject> res) {
        long length = 0;

        for (int ref : resources.keySet()) {
            if (res != null && res.containsKey(ref)) {
                continue;
            }

            PdfOutputStream os = new PdfOutputStream(new IdleOutputStream());

            os.write(resources.get(ref).clone());
            length += os.getCurrentPos();
        }

        return length;
    }
}
