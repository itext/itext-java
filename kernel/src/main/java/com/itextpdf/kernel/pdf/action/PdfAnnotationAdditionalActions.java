/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2022 iText Group NV
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


package com.itextpdf.kernel.pdf.action;

import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfObjectWrapper;

import java.util.ArrayList;
import java.util.List;

/**
 * A wrapper for annotations additional actions dictionaries.
 * See section 12.6.3 Table 197 of ISO 32000-1.
 * An annotation additional actions dictionary defines the event handlers for annotations
 */
public class PdfAnnotationAdditionalActions extends PdfObjectWrapper<PdfDictionary> {

    private static final PdfName[] Events = {PdfName.E, PdfName.X, PdfName.D, PdfName.U, PdfName.Fo,
            PdfName.Bl, PdfName.PO, PdfName.PC, PdfName.PV,
            PdfName.PI};

    public PdfAnnotationAdditionalActions(PdfDictionary pdfObject) {
        super(pdfObject);
    }

    /**
     * Returns the {@link PdfAction} for the OnEnter event if there is any, or null.
     *
     * @return {@link PdfAction} or null
     */
    public PdfAction getOnEnter() {
        return getPdfActionForEvent(PdfName.E);
    }

    /**
     * Sets the {@link PdfAction} to perform on the OnEnter event, or removes it when action is null.
     *
     * @param action The {@link PdfAction} to set or null to remove the action
     */
    public void setOnEnter(PdfAction action) {
        setPdfActionForEvent(PdfName.E, action);
    }

    /**
     * Returns the {@link PdfAction} for the OnExit event if there is any, or null.
     *
     * @return {@link PdfAction} or null
     */
    public PdfAction getOnExit() {
        return getPdfActionForEvent(PdfName.X);
    }

    /**
     * Sets the {@link PdfAction} to perform on the OnExit event, or removes it when action is null.
     *
     * @param action {@link PdfAction}  The action to set or null to remove the action
     */
    public void setOnExit(PdfAction action) {
        setPdfActionForEvent(PdfName.X, action);
    }

    /**
     * Returns the {@link PdfAction} for the OnMouseDown event if there is any, or null.
     *
     * @return {@link PdfAction} or null
     */
    public PdfAction getOnMouseDown() {
        return getPdfActionForEvent(PdfName.D);
    }

    /**
     * Sets the {@link PdfAction} to perform on the OnMouseDown event, or removes it when action is null.
     *
     * @param action {@link PdfAction}  The action to set or null to remove the action
     */
    public void setOnMouseDown(PdfAction action) {
        setPdfActionForEvent(PdfName.D, action);
    }

    /**
     * Returns the {@link PdfAction} for the OnMouseUp event if there is any, or null.
     *
     * @return {@link PdfAction} or null
     */
    public PdfAction getOnMouseUp() {
        return getPdfActionForEvent(PdfName.U);
    }

    /**
     * Sets the {@link PdfAction} to perform on the OnMouseUp event, or removes it when action is null.
     *
     * @param action {@link PdfAction}  The action to set or null to remove the action
     */
    public void setOnMouseUp(PdfAction action) {
        setPdfActionForEvent(PdfName.U, action);
    }

    /**
     * Returns the {@link PdfAction} for the OnFocus event if there is any, or null.
     *
     * @return {@link PdfAction} or null
     */
    public PdfAction getOnFocus() {
        return getPdfActionForEvent(PdfName.Fo);
    }

    /**
     * Sets the {@link PdfAction} to perform on the OnFocus event, or removes it when action is null.
     *
     * @param action {@link PdfAction} The action to set or null to remove the action
     */
    public void setOnFocus(PdfAction action) {
        setPdfActionForEvent(PdfName.Fo, action);
    }

    /**
     * Returns the {@link PdfAction} for the OnLostFocus event if there is any, or null.
     *
     * @return {@link PdfAction} or null
     */
    public PdfAction getOnLostFocus() {
        return getPdfActionForEvent(PdfName.Bl);
    }

    /**
     * Sets the {@link PdfAction} to perform on the OnLostFocus event, or removes it when action is null.
     *
     * @param action {@link PdfAction}  The action to set or null to remove the action
     */
    public void setOnLostFocus(PdfAction action) {
        setPdfActionForEvent(PdfName.Bl, action);
    }

    /**
     * Returns the {@link PdfAction} for the OnPageOpened event if there is any, or null.
     *
     * @return {@link PdfAction} or null
     */
    public PdfAction getOnPageOpened() {
        return getPdfActionForEvent(PdfName.PO);
    }

    /**
     * Sets the {@link PdfAction} to perform on the OnPageOpened event, or removes it when action is null.
     *
     * @param action {@link PdfAction}  The action to set or null to remove the action
     */
    public void setOnPageOpened(PdfAction action) {
        setPdfActionForEvent(PdfName.PO, action);
    }

    /**
     * Returns the {@link PdfAction} for the OnPageClosed event if there is any, or null.
     *
     * @return {@link PdfAction} or null
     */
    public PdfAction getOnPageClosed() {
        return getPdfActionForEvent(PdfName.PC);
    }

    /**
     * Sets the {@link PdfAction} to perform on the OnPageClosed event, or removes it when action is null.
     *
     * @param action {@link PdfAction}  The action to set or null to remove the action
     */
    public void setOnPageClosed(PdfAction action) {
        setPdfActionForEvent(PdfName.PC, action);
    }

    /**
     * Returns the {@link PdfAction} for the OnPageVisible event if there is any, or null.
     *
     * @return {@link PdfAction} or null
     */
    public PdfAction getOnPageVisible() {
        return getPdfActionForEvent(PdfName.PV);
    }

    /**
     * Sets the {@link PdfAction} to perform on the OnPageVisible event, or removes it when action is null.
     *
     * @param action {@link PdfAction}  The action to set or null to remove the action
     */
    public void setOnPageVisible(PdfAction action) {
        setPdfActionForEvent(PdfName.PV, action);
    }

    /**
     * Returns the {@link PdfAction} for the OnPageLostView event if there is any, or null.
     *
     * @return {@link PdfAction} or null
     */
    public PdfAction getOnPageLostView() {
        return getPdfActionForEvent(PdfName.PI);
    }

    /**
     * Sets the {@link PdfAction} to perform on the OnPageLostView event, or removes it when action is null.
     *
     * @param action {@link PdfAction}  The action to set or null to remove the action
     */
    public void setOnPageLostView(PdfAction action) {
        setPdfActionForEvent(PdfName.PI, action);
    }

    /**
     * Lists every {@link PdfAction} for all documented events for an annotation's additional actions.
     * See section 12.6.3 Table 197 of ISO 32000-1
     *
     * @return The list of actions
     */
    public List<PdfAction> getAllKnownActions() {
        final List<PdfAction> result = new ArrayList<>();
        for (final PdfName event : Events) {
            final PdfAction action = getPdfActionForEvent(event);
            if (action != null) {
                result.add(action);
            }
        }
        return result;
    }

    /**
     * If exists, returns the {@link PdfAction} for this event, otherwise returns null.
     *
     * @param eventName The {@link PdfName} for the event.
     *
     * @return the {@link PdfAction} or null
     */
    public PdfAction getPdfActionForEvent(PdfName eventName) {
        final PdfObject action = getPdfObject().get(eventName);
        if (action == null || !action.isDictionary()) {
            return null;
        }
        return new PdfAction((PdfDictionary) action);
    }

    /**
     * Sets the action for an event, or removes it when the action is null.
     *
     * @param event  the event to set or remove the action for
     * @param action the {@link PdfAction} to set or null
     */
    public void setPdfActionForEvent(PdfName event, PdfAction action) {
        if (action == null) {
            getPdfObject().remove(event);
        } else {
            getPdfObject().put(event, action.getPdfObject());
        }
        setModified();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean isWrappedObjectMustBeIndirect() {
        return true;
    }
}
