package com.itextpdf.io.util;

import java.util.ArrayList;
import java.util.List;

public class GenericArray<T> {

    private List<T> array;

    public GenericArray(int size) {
        array = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            array.add(null);
        }
    }

    public T get(int index) {
        return array.get(index);
    }

    public T set(int index, T element) {
        return array.set(index, element);
    }
}
