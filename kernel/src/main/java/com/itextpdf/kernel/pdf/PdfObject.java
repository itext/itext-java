/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
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
package com.itextpdf.kernel.pdf;

import com.itextpdf.io.LogMessageConstant;
import com.itextpdf.kernel.PdfException;
import com.itextpdf.kernel.crypto.BadPasswordException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Serializable;

public abstract class PdfObject implements Serializable {

    private static final long serialVersionUID = -3852543867469424720L;

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
            // TODO DEVSIX-744: here we should take into account and log the case when object is MustBeIndirect, but has no indirect reference
//            Logger logger = LoggerFactory.getLogger(PdfObject.class);
//            if (isFlushed()) {
//                logger.warn("Meaningless call, the object has already flushed");
//            } else if (isIndirect()){
//                logger.warn("Meaningless call, the object will be transformed into indirect on closing, but at the moment it doesn't have an indirect reference and therefore couldn't be flushed. " +
//                        "To flush it now call makeIndirect(PdfDocument) method before calling flush() method.");
//            } else {
//                logger.warn("Meaningless call, the object is direct object. It will be flushed along with the indirect object that contains it.");
//            }
            return;
        }
        try {
            PdfDocument document = getIndirectReference().getDocument();
            if (document != null) {
                if (document.isAppendMode() && !isModified()) {
                    Logger logger = LoggerFactory.getLogger(PdfObject.class);
                    logger.info(LogMessageConstant.PDF_OBJECT_FLUSHING_NOT_PERFORMED);
                    return;
                }
                document.checkIsoConformance(this, IsoKey.PDF_OBJECT);
                document.flushObject(this, canBeInObjStm && getType() != STREAM
                        && getType() != INDIRECT_REFERENCE && getIndirectReference().getGenNumber() == 0);
            }
        } catch (IOException e) {
            throw new PdfException(PdfException.CannotFlushObject, e, this);
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
     * @param document a document the indirect reference will belong to.
     * @return object itself.
     */
    public PdfObject makeIndirect(PdfDocument document, PdfIndirectReference reference) {
        if (document == null || indirectReference != null) {
            return this;
        }
        if (document.getWriter() == null) {
            throw new PdfException(PdfException.ThereIsNoAssociatePdfWriterForMakingIndirects);
        }
        if (reference == null) {
            indirectReference = document.createNextIndirectReference();
            indirectReference.setRefersTo(this);
        } else {
            indirectReference = reference;
            indirectReference.setRefersTo(this);
        }
        clearState(MUST_BE_INDIRECT);
        return this;
    }

    /**
     * Marks object to be saved as indirect.
     *
     * @param document a document the indirect reference will belong to.
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
        PdfObject newObject = newInstance();
        if (indirectReference != null || checkState(MUST_BE_INDIRECT)) {
            newObject.setState(MUST_BE_INDIRECT);
        }
        newObject.copyContent(this, null);
        return newObject;
    }

    /**
     * Copies object to a specified document.
     * <br><br>
     * NOTE: Works only for objects that are read from document opened in reading mode, otherwise an exception is thrown.
     *
     * @param document document to copy object to.
     * @return copied object.
     */
    public PdfObject copyTo(PdfDocument document) {
        return copyTo(document, true);
    }

    /**
     * Copies object to a specified document.
     * <br><br>
     * NOTE: Works only for objects that are read from document opened in reading mode, otherwise an exception is thrown.
     *
     * @param document         document to copy object to.
     * @param allowDuplicating indicates if to allow copy objects which already have been copied.
     *                         If object is associated with any indirect reference and allowDuplicating is false then already existing reference will be returned instead of copying object.
     *                         If allowDuplicating is true then object will be copied and new indirect reference will be assigned.
     * @return copied object.
     */
    public PdfObject copyTo(PdfDocument document, boolean allowDuplicating) {
        if (document == null)
            throw new PdfException(PdfException.DocumentForCopyToCannotBeNull);

        if (indirectReference != null) {
            // TODO checkState(MUST_BE_INDIRECT) now is always false, because indirectReference != null. See also DEVSIX-602
            if (indirectReference.getWriter() != null || checkState(MUST_BE_INDIRECT)) {
                throw new PdfException(PdfException.CannotCopyIndirectObjectFromTheDocumentThatIsBeingWritten);
            }
            if (!indirectReference.getReader().isOpenedWithFullPermission()) {
                throw new BadPasswordException(BadPasswordException.PdfReaderNotOpenedWithOwnerPassword);
            }
        }

        return processCopying(document, allowDuplicating);
    }

    /**
     * Sets the 'modified' flag to the indirect object, the flag denotes that the object was modified since the document opening.
     * <p>
     * This flag is meaningful only if the {@link PdfDocument} is opened in append mode
     * (see {@link StampingProperties#useAppendMode()}).
     * </p>
     * <p>
     * In append mode the whole document is preserved as is, and only changes to the document are
     * appended to the end of the document file. Because of this, only modified objects need to be flushed and are
     * allowed to be flushed (i.e. to be written).
     * </p>
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
     */
    public boolean isReleaseForbidden() {
        return checkState(FORBID_RELEASE);
    }

    public void release() {
        // In case ForbidRelease flag is set, release will not be performed.
        if (isReleaseForbidden()) {
            Logger logger = LoggerFactory.getLogger(PdfObject.class);
            logger.warn(LogMessageConstant.FORBID_RELEASE_IS_SET);
        } else {
            if (indirectReference != null && indirectReference.getReader() != null
                    && !indirectReference.checkState(FLUSHED)) {
                indirectReference.refersTo = null;
                indirectReference = null;
                setState(READ_ONLY);
            }
            //TODO log reasonless call of method
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

    protected PdfObject setIndirectReference(PdfIndirectReference indirectReference) {
        this.indirectReference = indirectReference;
        return this;
    }

    /**
     * Checks state of the flag of current object.
     *
     * @param state special flag to check
     * @return true if the state was set.
     */
    protected boolean checkState(short state) {
        return (this.state & state) == state;
    }

    /**
     * Sets special states of current object.
     *
     * @param state special flag of current object
     */
    protected PdfObject setState(short state) {
        this.state |= state;
        return this;
    }

    /**
     * Clear state of the flag of current object.
     *
     * @param state special flag state to clear
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
        if (isFlushed())
            throw new PdfException(PdfException.CannotCopyFlushedObject, this);
    }

    /**
     * Processes two cases of object copying:
     * <ol>
     * <li>copying to the other document</li>
     * <li>cloning inside of the current document</li>
     * </ol>
     * <p>
     * This two cases are distinguished by the state of <code>document</code> parameter:
     * the second case is processed if <code>document</code> is <code>null</code>.
     *
     * @param documentTo       if not null: document to copy object to; otherwise indicates that object is to be cloned.
     * @param allowDuplicating indicates if to allow copy objects which already have been copied.
     *                         If object is associated with any indirect reference and allowDuplicating is false then already existing reference will be returned instead of copying object.
     *                         If allowDuplicating is true then object will be copied and new indirect reference will be assigned.
     * @return copied object.
     */
    PdfObject processCopying(PdfDocument documentTo, boolean allowDuplicating) {
        if (documentTo != null) {
            //copyTo case
            PdfWriter writer = documentTo.getWriter();
            if (writer == null)
                throw new PdfException(PdfException.CannotCopyToDocumentOpenedInReadingMode);
            return writer.copyObject(this, documentTo, allowDuplicating);

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

    static boolean equalContent(PdfObject obj1, PdfObject obj2) {
        PdfObject direct1 = obj1 != null && obj1.isIndirectReference()
                ? ((PdfIndirectReference)obj1).getRefersTo(true)
                : obj1;
        PdfObject direct2 = obj2 != null && obj2.isIndirectReference()
                ? ((PdfIndirectReference)obj2).getRefersTo(true)
                : obj2;
        return direct1 != null && direct1.equals(direct2);
    }
}
