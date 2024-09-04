/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2024 Apryse Group NV
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
package com.itextpdf.kernel.di.pagetree;

import com.itextpdf.commons.datastructures.ISimpleList;
import com.itextpdf.commons.datastructures.NullUnlimitedList;
import com.itextpdf.commons.datastructures.SimpleArrayList;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;

/**
 * This class is a default implementation of {@link IPageTreeListFactory} that is used as a default.
 * <p>
 * This class will create an arraylist when in creation mode.
 * In reading and editing mode, it will create a NullUnlimitedList if the count is greater than the
 * maxEntriesBeforeSwitchingToNullUnlimitedList. This is to prevent potential OOM exceptions when loading a document
 * with a large number of pages where only a few pages are needed.
 */
public class DefaultPageTreeListFactory implements IPageTreeListFactory {

    private final int maxEntriesBeforeSwitchingToNullUnlimitedList;

    /**
     * Creates a new instance of DefaultPageTreeListFactory.
     *
     * @param maxEntriesBeforeSwitchingToNullUnlimitedList the maximum number of entries before switching to
     *                                                     a NullUnlimitedList.
     */
    public DefaultPageTreeListFactory(int maxEntriesBeforeSwitchingToNullUnlimitedList) {
        this.maxEntriesBeforeSwitchingToNullUnlimitedList = maxEntriesBeforeSwitchingToNullUnlimitedList;
    }

    /**
     * Creates a list based on the count value in the pages dictionary. If the count value is greater than the
     * maxEntriesBeforeSwitchingToNullUnlimitedList, a NullUnlimitedList is created. This is to optimize memory usage
     * when loading a document with a large number of pages where only a few pages are needed.
     *
     * @param pagesDictionary The pages dictionary
     * @param <T>             The type of the list
     * @return The list
     */
    @Override
    public <T> ISimpleList<T> createList(PdfDictionary pagesDictionary) {
        //If dictionary is null, it means we are dealing with document creation.
        if (pagesDictionary == null) {
            return new SimpleArrayList<>();
        }
        PdfNumber count = pagesDictionary.getAsNumber(PdfName.Count);
        if (count == null) {
            //If count is null, it means we are dealing with a possible corrupted document.
            //In this case we use NullUnlimitedList to avoid creating a huge list.
            return new NullUnlimitedList<>();
        }
        int countValue = count.intValue();
        if (countValue > maxEntriesBeforeSwitchingToNullUnlimitedList) {
            return new NullUnlimitedList<>();
        }
        if (countValue < 0) {
            //If count is negative, it means we are dealing with a possible corrupted document.
            return new NullUnlimitedList<>();
        }
        //Initial capacity is set to count value to avoid resizing of the list.
        return new SimpleArrayList<>(countValue);
    }
}
