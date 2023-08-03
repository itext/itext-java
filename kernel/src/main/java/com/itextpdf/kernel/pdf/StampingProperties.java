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
package com.itextpdf.kernel.pdf;


public class StampingProperties extends DocumentProperties {


    protected boolean appendMode = false;
    protected boolean preserveEncryption = false;

    public StampingProperties() {
    }

    public StampingProperties(StampingProperties other) {
        super(other);
        this.appendMode = other.appendMode;
        this.preserveEncryption = other.preserveEncryption;
    }

    /**
     * Defines if the document will be edited in append mode.
     * @return this {@link StampingProperties} instance
     */
    public StampingProperties useAppendMode() {
        appendMode = true;
        return this;
    }

    /**
     * Defines if the encryption of the original document (if it was encrypted) will be preserved.
     * By default, the resultant document doesn't preserve the original encryption.
     * @return this {@link StampingProperties} instance
     */
    public StampingProperties preserveEncryption() {
        this.preserveEncryption = true;
        return this;
    }
}
