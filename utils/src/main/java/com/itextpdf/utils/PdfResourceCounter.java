package com.itextpdf.utils;

import com.itextpdf.core.pdf.PdfArray;
import com.itextpdf.core.pdf.PdfDictionary;
import com.itextpdf.core.pdf.PdfIndirectReference;
import com.itextpdf.core.pdf.PdfName;
import com.itextpdf.core.pdf.PdfObject;
import com.itextpdf.core.pdf.PdfOutputStream;

import java.io.IOException;
import java.io.OutputStream;
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
    protected Map<Integer, PdfObject> resources;

    /**
     * Creates a PdfResourceCounter instance to be used to count the resources
     * needed for either a page (in this case pass a page dictionary) or the
     * trailer (root and info dictionary) of a PDF file.
     *
     * @param obj the object we want to examine
     */
    public PdfResourceCounter(PdfObject obj) {
        resources = new HashMap<Integer, PdfObject>();
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

        if (ref == null || resources.put(ref.getObjNumber(), obj) == null) {
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
            case PdfObject.Array:
                PdfArray array = (PdfArray) obj;

                for (int i = 0; i < array.size(); i++) {
                    process(array.get(i));
                }

                break;
            case PdfObject.Dictionary:
            case PdfObject.Stream:
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
     * @throws java.io.IOException
     */
    public long getLength(Map<Integer, PdfObject> res) {
        long length = 0;

        for (int ref : resources.keySet()) {
            if (res != null && res.containsKey(ref)) {
                continue;
            }

            PdfOutputStream os = new PdfOutputStream(new OutputStream() {
                @Override
                public void write(int b) throws IOException {
                }
            });

            os.write((PdfObject)resources.get(ref).clone());
            length += os.getCurrentPos() - 1;
        }

        return length;
    }
}
