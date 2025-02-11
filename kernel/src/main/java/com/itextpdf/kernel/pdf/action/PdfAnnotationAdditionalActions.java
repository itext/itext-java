/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2025 Apryse Group NV
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
