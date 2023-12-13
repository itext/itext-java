/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 Apryse Group NV
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
package com.itextpdf.kernel.log;

/**
 * Interface that can be implemented if you want to count the number of documents
 * that are being processed by iText.
 * <p>
 * Implementers may use this method to record actual system usage for licensing purposes
 * (e.g. count the number of documents or the volumne in bytes in the context of a SaaS license).
 * @deprecated will be removed in next major release, please use {@link com.itextpdf.kernel.counter.EventCounter} instead.
 */
@Deprecated
public interface ICounter {

    /**
     * This method gets triggered if a document is read.
     *
     * @param size the length of the document that was read
     */
    void onDocumentRead(long size);

    /**
     * This method gets triggered if a document is written.
     *
     * @param size the length of the document that was written
     */
    void onDocumentWritten(long size);

}
