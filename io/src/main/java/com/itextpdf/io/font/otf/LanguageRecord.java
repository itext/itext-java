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
package com.itextpdf.io.font.otf;

public class LanguageRecord {
    private String tag;
    private int featureRequired;
    private int[] features;

    /**
     * Retrieves the tag of the language record.
     *
     * @return tag of record
     */
    public String getTag() {
        return tag;
    }

    /**
     * Sets the tag of the language record.
     *
     * @param tag tag of record
     */
    public void setTag(String tag) {
        this.tag = tag;
    }

    /**
     * Retrieves the feature required of the language record.
     *
     * @return feature required
     */
    public int getFeatureRequired() {
        return featureRequired;
    }

    /**
     * Sets the feature required of the language record.
     *
     * @param featureRequired feature required
     */
    public void setFeatureRequired(int featureRequired) {
        this.featureRequired = featureRequired;
    }

    /**
     * Retrieves the features of the language record.
     *
     * @return features
     */
    public int[] getFeatures() {
        return features;
    }

    /**
     * Sets the features of the language record.
     *
     * @param features features
     */
    public void setFeatures(int[] features) {
        this.features = features;
    }
}
