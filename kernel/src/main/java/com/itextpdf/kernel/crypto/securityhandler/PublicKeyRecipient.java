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
package com.itextpdf.kernel.crypto.securityhandler;

import java.security.cert.Certificate;

public class PublicKeyRecipient {

    private Certificate certificate = null;

    private int permission = 0;

    protected byte[] cms = null;

    public PublicKeyRecipient(Certificate certificate, int permission) {
        this.certificate = certificate;
        this.permission = permission;
    }

    public Certificate getCertificate() {
        return certificate;
    }

    public int getPermission() {
        return permission;
    }

    protected void setCms(byte[] cms) {
        this.cms = cms;
    }

    protected byte[] getCms() {
        return cms;
    }
}
