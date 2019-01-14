/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
    Authors: iText Software.

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
package com.itextpdf.kernel.pdf.tagging;

import com.itextpdf.io.LogMessageConstant;
import com.itextpdf.io.util.MessageFormatUtil;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfObjectWrapper;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.filespec.PdfFileSpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A wrapper for namespace dictionaries (ISO 32000-2 section 14.7.4).
 * A namespace dictionary defines a namespace within the structure tree.
 *
 * <p>This pdf entity is meaningful only for the PDF documents of version <b>2.0 and higher</b>.</p>
 */
public class PdfNamespace extends PdfObjectWrapper<PdfDictionary> {

    private static final long serialVersionUID = -4228596885910641569L;

    /**
     * Constructs namespace from the given {@link PdfDictionary} that represents namespace dictionary.
     * This method is useful for property reading in reading mode or modifying in stamping mode.
     * @param dictionary a {@link PdfDictionary} that represents namespace in the document.
     */
    public PdfNamespace(PdfDictionary dictionary) {
        super(dictionary);
        setForbidRelease();
    }

    /**
     * Constructs a namespace defined by the given namespace name.
     * @param namespaceName a {@link String} defining the namespace name (conventionally a uniform
     *                      resource identifier, or URI).
     */
    public PdfNamespace(String namespaceName) {
        this(new PdfString(namespaceName));
    }

    /**
     * Constructs a namespace defined by the given namespace name.
     * @param namespaceName a {@link PdfString} defining the namespace name (conventionally a uniform
     *                      resource identifier, or URI).
     */
    public PdfNamespace(PdfString namespaceName) {
        this(new PdfDictionary());
        put(PdfName.Type, PdfName.Namespace);
        put(PdfName.NS, namespaceName);
    }

    /**
     * Sets the string defining the namespace name.
     * @param namespaceName a {@link String} defining the namespace name (conventionally a uniform
     *                      resource identifier, or URI).
     * @return this {@link PdfNamespace} instance.
     */
    public PdfNamespace setNamespaceName(String namespaceName) {
        return setNamespaceName(new PdfString(namespaceName));
    }

    /**
     * Sets the string defining the namespace name.
     * @param namespaceName a {@link PdfString} defining the namespace name (conventionally a uniform
     *                      resource identifier, or URI).
     * @return this {@link PdfNamespace} instance.
     */
    public PdfNamespace setNamespaceName(PdfString namespaceName) {
        return put(PdfName.NS, namespaceName);
    }

    /**
     * Gets the string defining the namespace name.
     * @return a {@link String} defining the namespace name (conventionally a uniform
     *                      resource identifier, or URI).
     */
    public String getNamespaceName() {
        PdfString ns = getPdfObject().getAsString(PdfName.NS);
        return ns != null ? ns.toUnicodeString() : null;
    }

    /**
     * Sets file specification identifying the schema file, which defines this namespace.
     * @param fileSpec a {@link PdfFileSpec} identifying the schema file.
     * @return this {@link PdfNamespace} instance.
     */
    public PdfNamespace setSchema(PdfFileSpec fileSpec) {
        return put(PdfName.Schema, fileSpec.getPdfObject());
    }

    /**
     * Gets file specification identifying the schema file, which defines this namespace.
     * @return a {@link PdfFileSpec} identifying the schema file.
     */
    public PdfFileSpec getSchema() {
        PdfObject schemaObject = getPdfObject().get(PdfName.Schema);
        return PdfFileSpec.wrapFileSpecObject(schemaObject);
    }

    /**
     * A dictionary that maps the names of structure types used in the namespace to their approximate equivalents in another
     * namespace.
     * @param roleMapNs a {@link PdfDictionary} which is comprised of a set of keys representing structure element types
     *                  in the namespace defined within this namespace dictionary. The corresponding value for each of these
     *                  keys shall either be a single {@link PdfName} identifying a structure element type in the default
     *                  namespace or an {@link PdfArray} where the first value shall be a structure element type name
     *                  in a target namespace with the second value being an indirect reference to the target namespace dictionary.
     * @return this {@link PdfNamespace} instance.
     */
    public PdfNamespace setNamespaceRoleMap(PdfDictionary roleMapNs) {
        return put(PdfName.RoleMapNS, roleMapNs);
    }

    /**
     * A dictionary that maps the names of structure types used in the namespace to their approximate equivalents in another
     * namespace.
     * @return a {@link PdfDictionary} which is comprised of a set of keys representing structure element types
     * in the namespace defined within this namespace dictionary. The corresponding value for each of these
     * keys shall either be a single {@link PdfName} identifying a structure element type in the default
     * namespace or an {@link PdfArray} where the first value shall be a structure element type name
     * in a target namespace with the second value being an indirect reference to the target namespace dictionary.
     */
    public PdfDictionary getNamespaceRoleMap() {
        return getNamespaceRoleMap(false);
    }

    /**
     * Adds to the namespace role map (see {@link #setNamespaceRoleMap(PdfDictionary)}) a single role mapping to the
     * default standard structure namespace.
     * @param thisNsRole a {@link String} identifying structure element type in this namespace.
     * @param defaultNsRole a {@link String} identifying a structure element type in the default standard structure namespace.
     * @return this {@link PdfNamespace} instance.
     */
    public PdfNamespace addNamespaceRoleMapping(String thisNsRole, String defaultNsRole) {
        PdfObject prevVal = getNamespaceRoleMap(true).put(PdfStructTreeRoot.convertRoleToPdfName(thisNsRole), PdfStructTreeRoot.convertRoleToPdfName(defaultNsRole));
        logOverwritingOfMappingIfNeeded(thisNsRole, prevVal);
        setModified();
        return this;
    }

    /**
     * Adds to the namespace role map (see {@link #setNamespaceRoleMap(PdfDictionary)}) a single role mapping to the
     * target namespace.
     * @param thisNsRole a {@link String} identifying structure element type in this namespace.
     * @param targetNsRole a {@link String} identifying a structure element type in the target namespace.
     * @param targetNs a {@link PdfNamespace} identifying the target namespace.
     * @return this {@link PdfNamespace} instance.
     */
    public PdfNamespace addNamespaceRoleMapping(String thisNsRole, String targetNsRole, PdfNamespace targetNs) {
        PdfArray targetMapping = new PdfArray();
        targetMapping.add(PdfStructTreeRoot.convertRoleToPdfName(targetNsRole));
        targetMapping.add(targetNs.getPdfObject());
        PdfObject prevVal = getNamespaceRoleMap(true).put(PdfStructTreeRoot.convertRoleToPdfName(thisNsRole), targetMapping);
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

    private void logOverwritingOfMappingIfNeeded(String thisNsRole, PdfObject prevVal) {
        if (prevVal != null) {
            Logger logger = LoggerFactory.getLogger(PdfNamespace.class);
            String nsNameStr = getNamespaceName();
            if (nsNameStr == null) {
                nsNameStr = "this";
            }
            logger.warn(MessageFormatUtil.format(LogMessageConstant.MAPPING_IN_NAMESPACE_OVERWRITTEN, thisNsRole, nsNameStr));
        }
    }
}
