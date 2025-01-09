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

import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.kernel.utils.ICopyFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public abstract class PdfPrimitiveObject extends PdfObject {

	
    protected byte[] content = null;
    protected boolean directOnly;

    protected PdfPrimitiveObject() {
        super();
    }

    protected PdfPrimitiveObject(boolean directOnly) {
        super();
        this.directOnly = directOnly;
    }

    /**
     * Initialize PdfPrimitiveObject from the passed bytes.
     *
     * @param content byte content, shall not be null.
     */
    protected PdfPrimitiveObject(byte[] content) {
        this();
        assert content != null;
        this.content = content;
    }

    protected final byte[] getInternalContent() {
        if (content == null)
            generateContent();
        return content;
    }

    protected boolean hasContent() {
        return content != null;
    }

    protected abstract void generateContent();

    @Override
    public PdfObject makeIndirect(PdfDocument document, PdfIndirectReference reference) {
        if (!directOnly) {
            return super.makeIndirect(document, reference);
        } else {
            Logger logger = LoggerFactory.getLogger(PdfObject.class);
            logger.warn(IoLogMessageConstant.DIRECTONLY_OBJECT_CANNOT_BE_INDIRECT);
        }
        return this;
    }

    @Override
    public PdfObject setIndirectReference(PdfIndirectReference indirectReference) {
        if (!directOnly) {
            super.setIndirectReference(indirectReference);
        } else {
            Logger logger = LoggerFactory.getLogger(PdfObject.class);
            logger.warn(IoLogMessageConstant.DIRECTONLY_OBJECT_CANNOT_BE_INDIRECT);
        }
        return this;
    }

    @Override
    protected void copyContent(PdfObject from, PdfDocument document, ICopyFilter copyFilter) {
        super.copyContent(from, document, copyFilter);
        PdfPrimitiveObject object = (PdfPrimitiveObject) from;
        if (object.content != null)
            content = Arrays.copyOf(object.content, object.content.length);
    }

    protected int compareContent(PdfPrimitiveObject o) {
        for (int i = 0; i < Math.min(content.length, o.content.length); i++) {
            if (content[i] > o.content[i])
                return 1;
            if (content[i] < o.content[i])
                return -1;
        }
        return Integer.compare(content.length, o.content.length);
    }
}
