/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2026 Apryse Group NV
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

/**
 * Holds identification strings extracted from a font program.
 */
public class FontIdentification {


    // name ID 5
    private String ttfVersion;
    // name ID 3
    private String ttfUniqueId;
    // /UniqueID
    private Integer type1Xuid;
    // OS/2.panose
    private String panose;

    /**
     * Returns the TrueType version string.
     *
     * @return the version string, or {@code null} when unavailable
     */
    public String getTtfVersion() {
        return ttfVersion;
    }

    /**
     * Returns the TrueType unique identifier.
     *
     * @return the unique identifier, or {@code null} when unavailable
     */
    public String getTtfUniqueId() {
        return ttfUniqueId;
    }

    /**
     * Returns the Type 1 unique ID.
     *
     * @return the identifier, or {@code null} when unavailable
     */
    public Integer getType1Xuid() {
        return type1Xuid;
    }

    /**
     * Returns the PANOSE classification.
     *
     * @return the classification string, or {@code null} when unavailable
     */
    public String getPanose() {
        return panose;
    }

    /**
     * Sets the TrueType version.
     *
     * @param ttfVersion the version string
     */
    protected void setTtfVersion(String ttfVersion) {
        this.ttfVersion = ttfVersion;
    }

    /**
     * Sets the TrueType unique identifier.
     *
     * @param ttfUniqueId the identifier
     */
    protected void setTtfUniqueId(String ttfUniqueId) {
        this.ttfUniqueId = ttfUniqueId;
    }

    /**
     * Sets the Type 1 unique ID.
     *
     * @param type1Xuid the unique ID
     */
    protected void setType1Xuid(Integer type1Xuid) {
        this.type1Xuid = type1Xuid;
    }

    /**
     * Sets the PANOSE classification from raw bytes.
     *
     * @param panose the PANOSE bytes
     */
    protected void setPanose(byte[] panose) {
        this.panose = new String(panose);
    }

    /**
     * Sets the PANOSE classification string.
     *
     * @param panose the classification string
     */
    protected void setPanose(String panose) {
        this.panose = panose;
    }
}
