/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 Apryse Group NV
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
package com.itextpdf.kernel.pdf;

import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.kernel.exceptions.BadPasswordException;
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.utils.ICopyFilter;
import com.itextpdf.kernel.utils.NullCopyFilter;

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class PdfObject {


    public static final byte ARRAY = 1;
    public static final byte BOOLEAN = 2;
    public static final byte DICTIONARY = 3;
    public static final byte LITERAL = 4;
    public static final byte INDIRECT_REFERENCE = 5;
    public static final byte NAME = 6;
    public static final byte NULL = 7;
    public static final byte NUMBER = 8;
    public static final byte STREAM = 9;
    public static final byte STRING = 10;

    /**
     * Indicates if the object has been flushed.
     */
    protected static final short FLUSHED = 1;

    /**
     * Indicates that the indirect reference of the object could be reused or have to be marked as free.
     */
    protected static final short FREE = 1 << 1;

    /**
     * Indicates that definition of the indirect reference of the object still not found (e.g. keys in XRefStm).
     */
    protected static final short READING = 1 << 2;

    /**
     * Indicates that object changed (is used in append mode).
     */
    protected static final short MODIFIED = 1 << 3;

    /**
     * Indicates that the indirect reference of the object represents ObjectStream from original document.
     * When PdfReader read ObjectStream reference marked as OriginalObjectStream
     * to avoid further reusing.
     */
    protected static final short ORIGINAL_OBJECT_STREAM = 1 << 4;

    /**
     * For internal usage only. Marks objects that shall be written to the output document.
     * Option is needed to build the correct PDF objects tree when closing the document.
     * As a result it avoids writing unused (removed) objects.
     */
    protected static final short MUST_BE_FLUSHED = 1 << 5;

    /**
     * Indicates that the object shall be indirect when it is written to the document.
     * It is used to postpone the creation of indirect reference for the objects that shall be indirect,
     * so it is possible to create such objects without PdfDocument instance.
     */
    protected static final short MUST_BE_INDIRECT = 1 << 6;

    /**
     * Indicates that the object is highly sensitive and we do not want to release it even if release() is called.
     * This flag can be set in stamping mode in object wrapper constructors and is automatically set when setModified
     * flag is set (we do not want to release changed objects).
     * The flag is set automatically for some wrappers that need document even in reader mode (FormFields etc).
     */
    protected static final short FORBID_RELEASE = 1 << 7;

    /**
     * Indicates that we do not want this object to be ever written into the resultant document
     * (because of multiple objects read from the same reference inconsistency).
     */
    protected static final short READ_ONLY = 1 << 8;

    /**
     * Indicates that this object is not encrypted in the encrypted document.
     * E.g. digital signature dictionary /Contents entry shall not be encrypted.
     */
    protected static final short UNENCRYPTED = 1 << 9;

    /**
     * If object is flushed the indirect reference is kept here.
     */
    protected PdfIndirectReference indirectReference = null;

    /**
     * Indicate same special states of PdfIndirectObject or PdfObject like @see Free, @see Reading, @see Modified.
     */
    private short state;

    /**
     * Gets object type.
     *
     * @return object type.
     */
    public abstract byte getType();

    /**
     * Flushes the object to the document.
     */
    public final void flush() {
        flush(true);
    }

    /**
     * Flushes the object to the document.
     *
     * @param canBeInObjStm indicates whether object can be placed into object stream.
     */
    public final void flush(boolean canBeInObjStm) {
        if (isFlushed() || getIndirectReference() == null || getIndirectReference().isFree()) {
// TODO DEVSIX-744: here we should take into account and log the case when object is MustBeIndirect,
//  but has no indirect reference
//            Logger logger = LoggerFactory.getLogger(PdfObject.class);
//            if (isFlushed()) {
//                logger.warn("Meaningless call, the object has already flushed");
//            } else if (isIndirect()){
//                logger.warn("Meaningless call, the object will be transformed into indirect on closing," +
//                " but at the moment it doesn't have an indirect reference and therefore couldn't be flushed. " +
//                        "To flush it now call makeIndirect(PdfDocument) method before calling flush() method.");
//            } else {
//                logger.warn("Meaningless call, the object is direct object. It will be flushed along with" +
//                " the indirect object that contains it.");
//            }
            return;
        }
        try {
            PdfDocument document = getIndirectReference().getDocument();
            if (document != null) {
                if (document.isAppendMode() && !isModified()) {
                    Logger logger = LoggerFactory.getLogger(PdfObject.class);
                    logger.info(IoLogMessageConstant.PDF_OBJECT_FLUSHING_NOT_PERFORMED);
                    return;
                }
                document.checkIsoConformance(this, IsoKey.PDF_OBJECT);
                document.flushObject(this, canBeInObjStm && getType() != STREAM
                        && getType() != INDIRECT_REFERENCE && getIndirectReference().getGenNumber() == 0);
            }
        } catch (IOException e) {
            throw new PdfException(KernelExceptionMessageConstant.CANNOT_FLUSH_OBJECT, e, this);
        }
    }

    /**
     * Gets the indirect reference associated with the object.
     * The indirect reference is used when flushing object to the document.
     *
     * @return indirect reference.
     */
    public PdfIndirectReference getIndirectReference() {
        return indirectReference;
    }

    /**
     * Checks if object is indirect.
     * <br>
     * Note:
     * Return value {@code true} doesn't necessarily mean that indirect reference of this object
     * is not null at the moment. Object could be marked as indirect and
     * be transformed to indirect on flushing.
     * <br>
     * E.g. all PdfStreams are transformed to indirect objects when they are written, but they don't always
     * have indirect references at any given moment.
     *
     * @return returns {@code true} if object is indirect or is to be indirect in the resultant document.
     */
    public boolean isIndirect() {
        return indirectReference != null || checkState(PdfObject.MUST_BE_INDIRECT);
    }

    /**
     * Marks object to be saved as indirect.
     *
     * @param document  a document the indirect reference will belong to.
     * @param reference indirect reference which will be associated with this document
     *
     * @return object itself.
     */
    public PdfObject makeIndirect(PdfDocument document, PdfIndirectReference reference) {
        if (document == null || indirectReference != null) {
            return this;
        }
        if (document.getWriter() == null) {
            throw new PdfException(
                    KernelExceptionMessageConstant.THERE_IS_NO_ASSOCIATE_PDF_WRITER_FOR_MAKING_INDIRECTS);
        }
        if (reference == null) {
            indirectReference = document.createNextIndirectReference();
            indirectReference.setRefersTo(this);
        } else {
            reference.setState(MODIFIED);
            indirectReference = reference;
            indirectReference.setRefersTo(this);
        }
        setState(FORBID_RELEASE);
        clearState(MUST_BE_INDIRECT);
        return this;
    }

    /**
     * Marks object to be saved as indirect.
     *
     * @param document a document the indirect reference will belong to.
     *
     * @return object itself.
     */
    public PdfObject makeIndirect(PdfDocument document) {
        return makeIndirect(document, null);
    }

    /**
     * Indicates is the object has been flushed or not.
     *
     * @return true if object has been flushed, otherwise false.
     */
    public boolean isFlushed() {
        PdfIndirectReference indirectReference = getIndirectReference();
        return (indirectReference != null && indirectReference.checkState(FLUSHED));
    }

    /**
     * Indicates is the object has been set as modified or not. Useful for incremental updates (e.g. appendMode).
     *
     * @return true is object has been set as modified, otherwise false.
     */
    public boolean isModified() {
        PdfIndirectReference indirectReference = getIndirectReference();
        return (indirectReference != null && indirectReference.checkState(MODIFIED));
    }

    /**
     * Creates clone of the object which belongs to the same document as original object.
     * New object shall not be used in other documents.
     *
     * @return cloned object.
     */
    @SuppressWarnings("CloneDoesntCallSuperClone")
    @Override
    public PdfObject clone() {
        return clone(NullCopyFilter.getInstance());
    }

    /**
     * Creates clone of the object which belongs to the same document as original object.
     * New object shall not be used in other documents.
     *
     * @param filter Filter what will be copied or not
     *
     * @return cloned object.
     */
    public PdfObject clone(ICopyFilter filter) {
        PdfObject newObject = newInstance();
        if (indirectReference != null || checkState(MUST_BE_INDIRECT)) {
            newObject.setState(MUST_BE_INDIRECT);
        }
        newObject.copyContent(this, null, filter);
        return newObject;
    }

    /**
     * Copies object to a specified document.
     * <br><br>
     * NOTE: Works only for objects that are read from document opened in reading mode,
     * otherwise an exception is thrown.
     *
     * @param document document to copy object to.
     *
     * @return copied object.
     */
    public PdfObject copyTo(PdfDocument document) {
        return copyTo(document, true, NullCopyFilter.getInstance());
    }

    /**
     * Copies object to a specified document.
     * <br><br>
     * NOTE: Works only for objects that are read from document opened in reading mode,
     * otherwise an exception is thrown.
     *
     * @param document         document to copy object to.
     * @param allowDuplicating indicates if to allow copy objects which already have been copied.
     *                         If object is associated with any indirect reference and allowDuplicating is
     *                         false then already existing reference will be returned instead of copying object.
     *                         If allowDuplicating is true then object will be copied and new indirect
     *                         reference will be assigned.
     *
     * @return copied object.
     */
    public PdfObject copyTo(PdfDocument document, boolean allowDuplicating) {
        return copyTo(document, allowDuplicating, NullCopyFilter.getInstance());
    }

    /**
     * Copies object to a specified document.
     * <br><br>
     * NOTE: Works only for objects that are read from document opened in reading mode,
     * otherwise an exception is thrown.
     *
     * @param document   document to copy object to.
     * @param copyFilter {@link  ICopyFilter} a filter to apply while copying arrays and dictionaries
     *                   Use {@link NullCopyFilter} for no filtering
     *
     * @return copied object.
     */

    public PdfObject copyTo(PdfDocument document, ICopyFilter copyFilter) {
        return copyTo(document, true, copyFilter);
    }

    /**
     * Copies object to a specified document.
     * <br><br>
     * NOTE: Works only for objects that are read from document opened in reading mode,
     * otherwise an exception is thrown.
     *
     * @param document         document to copy object to.
     * @param allowDuplicating indicates if to allow copy objects which already have been copied.
     *                         If object is associated with any indirect reference and allowDuplicating is false
     *                         then already existing reference will be returned instead of copying object.
     *                         If allowDuplicating is true then object will be copied and new indirect reference
     *                         will be assigned.
     * @param copyFilter       {@link  ICopyFilter} a filter to apply while copying arrays and dictionaries
     *                         Use {@link NullCopyFilter} for no filtering
     *
     * @return copied object.
     */

    public PdfObject copyTo(PdfDocument document, boolean allowDuplicating, ICopyFilter copyFilter) {
        if (document == null)
            throw new PdfException(KernelExceptionMessageConstant.DOCUMENT_FOR_COPY_TO_CANNOT_BE_NULL);

        if (indirectReference != null) {
            // TODO checkState(MUST_BE_INDIRECT) now is always false, because indirectReference != null. See also
            //  DEVSIX-602
            if (indirectReference.getWriter() != null || checkState(MUST_BE_INDIRECT)) {
                throw new PdfException(
                        KernelExceptionMessageConstant.CANNOT_COPY_INDIRECT_OBJECT_FROM_THE_DOCUMENT_THAT_IS_BEING_WRITTEN);
            }
            if (!indirectReference.getReader().isOpenedWithFullPermission()) {
                throw new BadPasswordException(BadPasswordException.PdfReaderNotOpenedWithOwnerPassword);
            }
        }

        return processCopying(document, allowDuplicating, copyFilter);
    }

    /**
     * Sets the 'modified' flag to the indirect object, the flag denotes that the object was modified since
     * the document opening.
     * It is recommended to set this flag after changing any PDF object.
     * <p>
     * For example flag is used in the append mode (see {@link StampingProperties#useAppendMode()}).
     * In append mode the whole document is preserved as is, and only changes to the document are
     * appended to the end of the document file. Because of this, only modified objects need to be flushed and are
     * allowed to be flushed (i.e. to be written).
     *
     * @return this {@link PdfObject} instance.
     */
    public PdfObject setModified() {
        if (indirectReference != null) {
            indirectReference.setState(MODIFIED);
            setState(FORBID_RELEASE);
        }
        return this;
    }

    /**
     * Checks if it's forbidden to release this {@link PdfObject} instance.
     * Some objects are vital for the living period of {@link PdfDocument} or may be
     * prevented from releasing by high-level entities dealing with the objects.
     * Also it's not possible to release the objects that have been modified.
     *
     * @return true if releasing this object is forbidden, otherwise false
     */
    public boolean isReleaseForbidden() {
        return checkState(FORBID_RELEASE);
    }

    public void release() {
        // In case ForbidRelease flag is set, release will not be performed.
        if (isReleaseForbidden()) {
            Logger logger = LoggerFactory.getLogger(PdfObject.class);
            logger.warn(IoLogMessageConstant.FORBID_RELEASE_IS_SET);
        } else {
            if (indirectReference != null && indirectReference.getReader() != null
                    && !indirectReference.checkState(FLUSHED)) {
                indirectReference.refersTo = null;
                indirectReference = null;
                setState(READ_ONLY);
            }
            // TODO DEVSIX-4020. Log reasonless call of method
        }
    }

    /**
     * Checks if this <CODE>PdfObject</CODE> is of the type
     * <CODE>PdfNull</CODE>.
     *
     * @return <CODE>true</CODE> or <CODE>false</CODE>
     */
    public boolean isNull() {
        return getType() == NULL;
    }

    /**
     * Checks if this <CODE>PdfObject</CODE> is of the type
     * <CODE>PdfBoolean</CODE>.
     *
     * @return <CODE>true</CODE> or <CODE>false</CODE>
     */
    public boolean isBoolean() {
        return getType() == BOOLEAN;
    }

    /**
     * Checks if this <CODE>PdfObject</CODE> is of the type
     * <CODE>PdfNumber</CODE>.
     *
     * @return <CODE>true</CODE> or <CODE>false</CODE>
     */
    public boolean isNumber() {
        return getType() == NUMBER;
    }

    /**
     * Checks if this <CODE>PdfObject</CODE> is of the type
     * <CODE>PdfString</CODE>.
     *
     * @return <CODE>true</CODE> or <CODE>false</CODE>
     */
    public boolean isString() {
        return getType() == STRING;
    }

    /**
     * Checks if this <CODE>PdfObject</CODE> is of the type
     * <CODE>PdfName</CODE>.
     *
     * @return <CODE>true</CODE> or <CODE>false</CODE>
     */
    public boolean isName() {
        return getType() == NAME;
    }

    /**
     * Checks if this <CODE>PdfObject</CODE> is of the type
     * <CODE>PdfArray</CODE>.
     *
     * @return <CODE>true</CODE> or <CODE>false</CODE>
     */
    public boolean isArray() {
        return getType() == ARRAY;
    }

    /**
     * Checks if this <CODE>PdfObject</CODE> is of the type
     * <CODE>PdfDictionary</CODE>.
     *
     * @return <CODE>true</CODE> or <CODE>false</CODE>
     */
    public boolean isDictionary() {
        return getType() == DICTIONARY;
    }

    /**
     * Checks if this <CODE>PdfObject</CODE> is of the type
     * <CODE>PdfStream</CODE>.
     *
     * @return <CODE>true</CODE> or <CODE>false</CODE>
     */
    public boolean isStream() {
        return getType() == STREAM;
    }

    /**
     * Checks if this <CODE>PdfObject</CODE> is of the type
     * <CODE>PdfIndirectReference</CODE>.
     *
     * @return <CODE>true</CODE> if this is an indirect reference,
     * otherwise <CODE>false</CODE>
     */
    public boolean isIndirectReference() {
        return getType() == INDIRECT_REFERENCE;
    }

    protected PdfObject setIndirectReference(PdfIndirectReference indirectReference) {
        this.indirectReference = indirectReference;
        return this;
    }

    /**
     * Checks if this <CODE>PdfObject</CODE> is of the type
     * <CODE>PdfLiteral</CODE>.
     *
     * @return <CODE>true</CODE> if this is a literal,
     * otherwise <CODE>false</CODE>
     */
    public boolean isLiteral() {
        return getType() == LITERAL;
    }

    /**
     * Creates new instance of object.
     *
     * @return new instance of object.
     */
    protected abstract PdfObject newInstance();

    /**
     * Checks state of the flag of current object.
     *
     * @param state special flag to check
     *
     * @return true if the state was set.
     */
    protected boolean checkState(short state) {
        return (this.state & state) == state;
    }

    /**
     * Sets special states of current object.
     *
     * @param state special flag of current object
     *
     * @return this {@link PdfObject}
     */
    protected PdfObject setState(short state) {
        this.state |= state;
        return this;
    }

    /**
     * Clear state of the flag of current object.
     *
     * @param state special flag state to clear
     *
     * @return this {@link PdfObject}
     */
    protected PdfObject clearState(short state) {
        this.state &= (short) ~state;
        return this;
    }

    /**
     * Copies object content from object 'from'.
     *
     * @param from     object to copy content from.
     * @param document document to copy object to.
     */
    protected void copyContent(PdfObject from, PdfDocument document) {
        copyContent(from, document, NullCopyFilter.getInstance());
    }

    /**
     * Copies object content from object 'from'.
     *
     * @param from     object to copy content from.
     * @param document document to copy object to.
     * @param filter   {@link ICopyFilter} a filter that will apply on dictionaries and array
     *                 Use {@link NullCopyFilter} for no filtering
     */
    protected void copyContent(PdfObject from, PdfDocument document, ICopyFilter filter) {
        if (isFlushed())
            throw new PdfException(KernelExceptionMessageConstant.CANNOT_COPY_FLUSHED_OBJECT, this);
    }

    static boolean equalContent(PdfObject obj1, PdfObject obj2) {
        PdfObject direct1 = obj1 != null && obj1.isIndirectReference()
                ? ((PdfIndirectReference) obj1).getRefersTo(true)
                : obj1;
        PdfObject direct2 = obj2 != null && obj2.isIndirectReference()
                ? ((PdfIndirectReference) obj2).getRefersTo(true)
                : obj2;
        return direct1 != null && direct1.equals(direct2);
    }

    /**
     * Processes two cases of object copying:
     * <ol>
     * <li>copying to the other document
     * <li>cloning inside of the current document
     * </ol>
     * <p>
     * This two cases are distinguished by the state of {@code document} parameter:
     * the second case is processed if {@code document} is {@code null}.
     *
     * @param documentTo       if not null: document to copy object to; otherwise indicates that object is to be cloned.
     * @param allowDuplicating indicates if to allow copy objects which already have been copied.
     *                         If object is associated with any indirect reference and allowDuplicating is false then
     *                         already existing reference will be returned instead of copying object.
     *                         If allowDuplicating is true then object will be copied and new indirect
     *                         reference will be assigned.
     *
     * @return copied object.
     */
    PdfObject processCopying(PdfDocument documentTo, boolean allowDuplicating) {
        return processCopying(documentTo, allowDuplicating, NullCopyFilter.getInstance());
    }

    /**
     * Processes two cases of object copying:
     * <ol>
     * <li>copying to the other document
     * <li>cloning inside of the current document
     * </ol>
     * <p>
     * This two cases are distinguished by the state of {@code document} parameter:
     * the second case is processed if {@code document} is {@code null}.
     *
     * @param documentTo       if not null: document to copy object to; otherwise indicates that object is to be cloned.
     * @param allowDuplicating indicates if to allow copy objects which already have been copied.
     *                         If object is associated with any indirect reference and allowDuplicating is false then
     *                         already existing reference will be returned instead of copying object.
     *                         If allowDuplicating is true then object will be copied and new indirect reference will
     *                         be assigned.
     * @param filter           filters what will be copies or not
     *
     * @return copied object.
     */

    PdfObject processCopying(PdfDocument documentTo, boolean allowDuplicating, ICopyFilter filter) {
        if (documentTo != null) {
            //copyTo case
            PdfWriter writer = documentTo.getWriter();
            if (writer == null)
                throw new PdfException(KernelExceptionMessageConstant.CANNOT_COPY_TO_DOCUMENT_OPENED_IN_READING_MODE);
            return writer.copyObject(this, documentTo, allowDuplicating, filter);

        } else {
            //clone case
            PdfObject obj = this;
            if (obj.isIndirectReference()) {
                PdfObject refTo = ((PdfIndirectReference) obj).getRefersTo();
                obj = refTo != null ? refTo : obj;
            }
            if (obj.isIndirect() && !allowDuplicating) {
                return obj;
            }
            return obj.clone();
        }
    }
}
