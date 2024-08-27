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
