/*
    $Id$

    This file is part of the iText (R) project.
    Copyright (c) 1998-2016 iText Group NV
    Authors: Bruno Lowagie, Paulo Soares, et al.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.

    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.

    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
 */
package com.itextpdf.forms.xfa;

import java.util.ArrayList;
import java.util.EmptyStackException;

/**
 * Another stack implementation. The main use is to facilitate
 * the porting to other languages.
 */
public class Stack2<T> extends ArrayList<T> {
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
