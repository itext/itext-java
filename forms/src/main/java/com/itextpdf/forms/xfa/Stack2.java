package com.itextpdf.forms.xfa;

import java.util.ArrayList;
import java.util.EmptyStackException;

/**
 * Another stack implementation. The main use is to facilitate
 * the porting to other languages.
 */
public  class Stack2<T> extends ArrayList<T> {
    private static final long serialVersionUID = -7451476576174095212L;

    /**
     * Looks at the object at the top of this stack without removing it from the stack.
     *
     * @return the object at the top of this stack
     */
    public T peek() {
        if (size() == 0)
            throw new EmptyStackException();
        return get(size() - 1);
    }

    /**
     * Removes the object at the top of this stack and returns that object as the value of this function.
     *
     * @return the object at the top of this stack
     */
    public T pop() {
        if (size() == 0)
            throw new EmptyStackException();
        T ret = get(size() - 1);
        remove(size() - 1);
        return ret;
    }

    /**
     * Pushes an item onto the top of this stack.
     *
     * @param item the item to be pushed onto this stack
     * @return the <CODE>item</CODE> argument
     */
    public T push(T item) {
        add(item);
        return item;
    }

    /**
     * Tests if this stack is empty.
     *
     * @return <CODE>true</CODE> if and only if this stack contains no items; <CODE>false</CODE> otherwise
     */
    public boolean empty() {
        return size() == 0;
    }
}
