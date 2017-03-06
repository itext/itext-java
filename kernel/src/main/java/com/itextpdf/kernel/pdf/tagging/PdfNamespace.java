package com.itextpdf.kernel.pdf.tagging;

import com.itextpdf.io.LogMessageConstant;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfObjectWrapper;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.filespec.PdfFileSpec;
import java.text.MessageFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PdfNamespace extends PdfObjectWrapper<PdfDictionary> {
    public PdfNamespace(PdfDictionary pdfObject) {
        super(pdfObject);
        setForbidRelease();
    }

    public PdfNamespace(String namespaceName) {
        this(new PdfString(namespaceName));
    }

    public PdfNamespace(PdfString namespaceName) {
        this(new PdfDictionary());
        put(PdfName.Type, PdfName.Namespace);
        put(PdfName.NS, namespaceName);
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
        return getNamespaceRoleMap(false);
    }

    public PdfNamespace addNamespaceRoleMapping(PdfName thisNsRole, PdfName defaultNsRole) {
        PdfObject prevVal = getNamespaceRoleMap(true).put(thisNsRole, defaultNsRole);
        logOverwritingOfMappingIfNeeded(thisNsRole, prevVal);
        setModified();
        return this;
    }

    public PdfNamespace addNamespaceRoleMapping(PdfName thisNsRole, PdfName targetNsRole, PdfNamespace targetNs) {
        PdfArray targetMapping = new PdfArray();
        targetMapping.add(targetNsRole);
        targetMapping.add(targetNs.getPdfObject());
        PdfObject prevVal = getNamespaceRoleMap(true).put(thisNsRole, targetMapping);
        logOverwritingOfMappingIfNeeded(thisNsRole, prevVal);
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

    private PdfDictionary getNamespaceRoleMap(boolean createIfNotExist) {
        PdfDictionary roleMapNs = getPdfObject().getAsDictionary(PdfName.RoleMapNS);
        if (createIfNotExist && roleMapNs == null) {
            roleMapNs = new PdfDictionary();
            put(PdfName.RoleMapNS, roleMapNs);
        }
        return roleMapNs;
    }

    private void logOverwritingOfMappingIfNeeded(PdfName thisNsRole, PdfObject prevVal) {
        if (prevVal != null) {
            Logger logger = LoggerFactory.getLogger(PdfNamespace.class);
            PdfString nsName = getNamespaceName();
            String nsNameStr = nsName != null ? nsName.toUnicodeString() : "this";
            logger.warn(MessageFormat.format(LogMessageConstant.MAPPING_IN_NAMESPACE_OVERWRITTEN, thisNsRole, nsNameStr));
        }
    }
}
