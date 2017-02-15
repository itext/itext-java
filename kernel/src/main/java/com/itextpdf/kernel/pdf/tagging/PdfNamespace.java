package com.itextpdf.kernel.pdf.tagging;

import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfObjectWrapper;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.filespec.PdfFileSpec;

public class PdfNamespace extends PdfObjectWrapper<PdfDictionary> {
    public PdfNamespace(PdfDictionary pdfObject) {
        super(pdfObject);
    }
    
    public PdfNamespace(String namespaceName) {
        this(new PdfDictionary());
        put(PdfName.Type, PdfName.Namespace);
        put(PdfName.NS, new PdfString(namespaceName));
    }
    
    public PdfNamespace setNamespaceName(PdfString namespaceName) {
        return put(PdfName.NS, namespaceName);
    }
    
    public PdfString getNamespaceName() {
        return getPdfObject().getAsString(PdfName.NS);
    }
    
    public PdfNamespace setSchema(PdfFileSpec fileSpec) {
        return put(PdfName.Schema, fileSpec.getPdfObject());
    }
    
    public PdfFileSpec getSchema() {
        PdfObject schemaObject = getPdfObject().get(PdfName.Schema);
        return PdfFileSpec.wrapFileSpecObject(schemaObject);
    }

    public PdfNamespace setNamespaceRoleMap(PdfDictionary roleMapNs) {
        return put(PdfName.RoleMapNS, roleMapNs);
    }

    public PdfDictionary getNamespaceRoleMap() {
        return getPdfObject().getAsDictionary(PdfName.RoleMapNS);
    }
    
    public PdfNamespace addNamespaceRoleMapping(PdfName thisNsRole, PdfName defaultNsRole) {
        getNamespaceRoleMap().put(thisNsRole, defaultNsRole);
        setModified();
        return this;
    }

    public PdfNamespace addNamespaceRoleMapping(PdfName thisNsRole, PdfName targetNsRole, PdfNamespace targetNs) {
        PdfArray targetMapping = new PdfArray();
        targetMapping.add(targetNsRole);
        targetMapping.add(targetNs.getPdfObject());
        getNamespaceRoleMap().put(thisNsRole, targetMapping);
        setModified();
        return this;
    }

    @Override
    protected boolean isWrappedObjectMustBeIndirect() {
        return true;
    }

    private PdfNamespace put(PdfName key, PdfObject value) {
        getPdfObject().put(key, value);
        setModified();
        return this;
    }
}
