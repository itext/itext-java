package com.itextpdf.forms.xfa;

import java.util.Collection;
import java.util.HashMap;

/**
 * A class to process "classic" fields.
 */
public class AcroFieldsSearch extends Xml2Som {
    private HashMap<String, String> acroShort2LongName;

    /**
     * Creates a new instance from a Collection with the full names.
     *
     * @param items the Collection
     */
    public AcroFieldsSearch(Collection<String> items) {
        inverseSearch = new HashMap<String, InverseStore>();
        acroShort2LongName = new HashMap<String, String>();
        for (String string : items) {
            String itemName = string;
            String itemShort = getShortName(itemName);
            acroShort2LongName.put(itemShort, itemName);
            inverseSearchAdd(inverseSearch, splitParts(itemShort), itemName);
        }
    }

    /**
     * Gets the mapping from short names to long names. A long
     * name may contain the #subform name part.
     *
     * @return the mapping from short names to long names
     */
    public HashMap<String, String> getAcroShort2LongName() {
        return acroShort2LongName;
    }

    /**
     * Sets the mapping from short names to long names. A long
     * name may contain the #subform name part.
     *
     * @param acroShort2LongName the mapping from short names to long names
     */
    public void setAcroShort2LongName(HashMap<String, String> acroShort2LongName) {
        this.acroShort2LongName = acroShort2LongName;
    }
}
