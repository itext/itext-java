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
package com.itextpdf.io.font;

public class FontIdentification {


    // name ID 5
    private String ttfVersion;
    // name ID 3
    private String ttfUniqueId;
    // /UniqueID
    private Integer type1Xuid;
    // OS/2.panose
    private String panose;

    public String getTtfVersion() {
        return ttfVersion;
    }

    public String getTtfUniqueId() {
        return ttfUniqueId;
    }

    public Integer getType1Xuid() {
        return type1Xuid;
    }

    public String getPanose() {
        return panose;
    }

    protected void setTtfVersion(String ttfVersion) {
        this.ttfVersion = ttfVersion;
    }

    protected void setTtfUniqueId(String ttfUniqueId) {
        this.ttfUniqueId = ttfUniqueId;
    }

    protected void setType1Xuid(Integer type1Xuid) {
        this.type1Xuid = type1Xuid;
    }

    protected void setPanose(byte[] panose) {
        this.panose = new String(panose);
    }

    protected void setPanose(String panose) {
        this.panose = panose;
    }
}
