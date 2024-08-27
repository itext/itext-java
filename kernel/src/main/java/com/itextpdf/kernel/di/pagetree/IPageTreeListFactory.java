package com.itextpdf.kernel.di.pagetree;


import com.itextpdf.commons.datastructures.ISimpleList;
import com.itextpdf.kernel.pdf.PdfDictionary;

/**
 * This interface is used to create a list of pages from a pages dictionary.
 */
public interface IPageTreeListFactory {

    /**
     * Creates a list based on the  value of the pages dictionary.
     * If null, it means we are dealing with document creation. In other cases the pdf document pages
     * dictionary will be passed.
     *
     * @param pagesDictionary The pages dictionary
     * @param <T>             The type of the list
     * @return The list
     */
    <T> ISimpleList<T> createList(PdfDictionary pagesDictionary);
}

