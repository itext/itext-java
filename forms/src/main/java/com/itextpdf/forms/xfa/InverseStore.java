package com.itextpdf.forms.xfa;

import java.util.ArrayList;
import java.util.List;

/**
 * A structure to store each part of a SOM name and link it to the next part
 * beginning from the lower hierarchy.
 */
public  class InverseStore {
    protected List<String> part = new ArrayList<>();
    protected List<Object> follow = new ArrayList<>();

    /**
     * Gets the full name by traversing the hierarchy using only the
     * index 0.
     *
     * @return the full name
     */
    public String getDefaultName() {
        InverseStore store = this;
        while (true) {
            Object obj = store.follow.get(0);
            if (obj instanceof String)
                return (String) obj;
            store = (InverseStore) obj;
        }
    }

    /**
     * Search the current node for a similar name. A similar name starts
     * with the same name but has a different index. For example, "detail[3]"
     * is similar to "detail[9]". The main use is to discard names that
     * correspond to out of bounds records.
     *
     * @param name the name to search
     * @return <CODE>true</CODE> if a similitude was found
     */
    public boolean isSimilar(String name) {
        int idx = name.indexOf('[');
        name = name.substring(0, idx + 1);
        for (int k = 0; k < part.size(); ++k) {
            if (part.get(k).startsWith(name))
                return true;
        }
        return false;
    }
}