/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2022 iText Group NV
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
package com.itextpdf.kernel.utils.objectpathitems;

import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Direct path item (see {@link ObjectPath}, which describes transition to the
 * {@link PdfDictionary} entry which value is now a currently comparing direct object.
 */
public final class DictPathItem extends LocalPathItem {
    private final PdfName key;

    /**
     * Creates an instance of the {@link DictPathItem}.
     *
     * @param key the key which defines to which entry of the {@link PdfDictionary}
     *            the transition was performed.
     */
    public DictPathItem(PdfName key) {
        super();
        this.key = key;
    }

    @Override
    public String toString() {
        return "Dict key: " + key;
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null && obj.getClass() == getClass() && key.equals(((DictPathItem) obj).key);
    }

    /**
     * The key which defines to which entry of the {@link PdfDictionary} the transition was performed.
     * See {@link DictPathItem} for more info.
     *
     * @return a {@link PdfName} which is the key which defines
     * to which entry of the dictionary the transition was performed.
     */
    public PdfName getKey() {
        return key;
    }

    @Override
    protected Node toXmlNode(Document document) {
        final Element element = document.createElement("dictKey");
        element.appendChild(document.createTextNode(key.toString()));
        return element;
    }
}
