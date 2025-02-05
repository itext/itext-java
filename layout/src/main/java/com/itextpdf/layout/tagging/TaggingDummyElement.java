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
package com.itextpdf.layout.tagging;

import com.itextpdf.kernel.pdf.tagutils.AccessibilityProperties;
import com.itextpdf.kernel.pdf.tagutils.DefaultAccessibilityProperties;
import com.itextpdf.layout.IPropertyContainer;
import com.itextpdf.layout.properties.Property;

/**
 * Instances of the class are used for {@link TaggingHintKey} which don't have model element
 * e.g. TR or THEAD in the table. Nobody will call {@link LayoutTaggingHelper#finishTaggingHint(IPropertyContainer)}
 * for them, it is why they should be handled separately.
 */
public class TaggingDummyElement implements IAccessibleElement, IPropertyContainer {
    private DefaultAccessibilityProperties properties;

    private Object id;

    /**
     * Instantiate a new {@link TaggingDummyElement} instance.
     *
     * @param role the role.
     */
    public TaggingDummyElement(String role) {
        this.properties = new DefaultAccessibilityProperties(role);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AccessibilityProperties getAccessibilityProperties() {
        return properties;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T1> T1 getProperty(int property) {
        if (property == Property.TAGGING_HINT_KEY) {
            return (T1) id;
        }
        return (T1) (Object) null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setProperty(int property, Object value) {
        if (property == Property.TAGGING_HINT_KEY) {
            this.id = value;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasProperty(int property) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasOwnProperty(int property) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T1> T1 getOwnProperty(int property) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T1> T1 getDefaultProperty(int property) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteOwnProperty(int property) {
        throw new UnsupportedOperationException();
    }
}
