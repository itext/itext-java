/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
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
package com.itextpdf.kernel.utils;

import com.itextpdf.io.util.IdelOutputStream;
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

            PdfOutputStream os = new PdfOutputStream(new IdelOutputStream());

            os.write(resources.get(ref).clone());
            length += os.getCurrentPos();
        }

        return length;
    }
}
