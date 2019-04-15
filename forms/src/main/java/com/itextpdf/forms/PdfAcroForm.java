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
package com.itextpdf.forms;

import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.forms.xfa.XfaForm;
import com.itextpdf.io.LogMessageConstant;
import com.itextpdf.kernel.PdfException;
import com.itextpdf.kernel.geom.AffineTransform;
import com.itextpdf.kernel.geom.Point;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfBoolean;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfObjectWrapper;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.PdfVersion;
import com.itextpdf.kernel.pdf.VersionConforming;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.tagging.StandardRoles;
import com.itextpdf.kernel.pdf.tagutils.TagReference;
import com.itextpdf.kernel.pdf.tagutils.TagTreePointer;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * This class represents the static form technology AcroForm on a PDF file.
 */
public class PdfAcroForm extends PdfObjectWrapper<PdfDictionary> {

    /**
     * To be used with {@link #setSignatureFlags}.
     * <br>
     * <blockquote>
     * If set, the document contains at least one signature field. This flag
     * allows a conforming reader to enable user interface items (such as menu
     * items or pushbuttons) related to signature processing without having to
     * scan the entire document for the presence of signature fields.
     * (ISO 32000-1, section 12.7.2 "Interactive Form Dictionary")
     * </blockquote>
     */
    public static final int SIGNATURE_EXIST = 1;

    /**
     * To be used with {@link #setSignatureFlags}.
     * <br>
     * <blockquote>
     * If set, the document contains signatures that may be invalidated if the
     * file is saved (written) in a way that alters its previous contents, as
     * opposed to an incremental update. Merely updating the file by appending
     * new information to the end of the previous version is safe. Conforming
     * readers may use this flag to inform a user requesting a full save that
     * signatures will be invalidated and require explicit confirmation before
     * continuing with the operation.
     * (ISO 32000-1, section 12.7.2 "Interactive Form Dictionary")
     * </blockquote>
     */
    public static final int APPEND_ONLY = 2;

    /**
     * Keeps track of whether or not appearances must be generated by the form
     * fields themselves, or by the PDF viewer application. Default is
     * <code>true</code>.
     */
    protected boolean generateAppearance = true;

    /**
     * A map of field names and their associated {@link PdfFormField form field}
     * objects.
     */
    protected Map<String, PdfFormField> fields = new LinkedHashMap<>();

    /**
     * The PdfDocument to which the PdfAcroForm belongs.
     */
    protected PdfDocument document;

    private PdfDictionary defaultResources;
    private Set<PdfFormField> fieldsForFlattening = new LinkedHashSet<>();
    private XfaForm xfaForm;
    private static Logger logger = LoggerFactory.getLogger(PdfAcroForm.class);

    /**
     * Creates a PdfAcroForm as a wrapper of a dictionary.
     * Also initializes an XFA form if an <code>/XFA</code> entry is present in
     * the dictionary.
     *
     * @param pdfObject the PdfDictionary to be wrapped
     */
    private PdfAcroForm(PdfDictionary pdfObject, PdfDocument pdfDocument) {
        super(pdfObject);
        document = pdfDocument;
        getFormFields();
        xfaForm = new XfaForm(pdfObject);
    }

    /**
     * Creates a PdfAcroForm from a {@link PdfArray} of fields.
     * Also initializes an empty XFA form.
     *
     * @param fields a {@link PdfArray} of {@link PdfDictionary} objects
     */
    private PdfAcroForm(PdfArray fields) {
        this(createAcroFormDictionaryByFields(fields), null);
        setForbidRelease();
    }

    /**
     * Retrieves AcroForm from the document. If there is no AcroForm in the
     * document Catalog and createIfNotExist flag is true then the AcroForm
     * dictionary will be created and added to the document.
     *
     * @param document         the document to retrieve the {@link PdfAcroForm} from
     * @param createIfNotExist when <code>true</code>, this method will create a {@link PdfAcroForm} if none exists for this document
     * @return the {@link PdfDocument document}'s AcroForm,
     * or a new one provided that <code>createIfNotExist</code> parameter is <code>true</code>, otherwise <code>null</code>.
     */
    public static PdfAcroForm getAcroForm(PdfDocument document, boolean createIfNotExist) {
        PdfDictionary acroFormDictionary = document.getCatalog().getPdfObject().getAsDictionary(PdfName.AcroForm);
        PdfAcroForm acroForm = null;
        if (acroFormDictionary == null) {
            if (createIfNotExist) {
                acroForm = new PdfAcroForm(new PdfArray());
                acroForm.makeIndirect(document);
                document.getCatalog().put(PdfName.AcroForm, acroForm.getPdfObject());
                document.getCatalog().setModified();
            }
        } else {
            acroForm = new PdfAcroForm(acroFormDictionary, document);
        }

        if (acroForm != null) {
            acroForm.defaultResources = acroForm.getDefaultResources();
            if (acroForm.defaultResources == null) {
                acroForm.defaultResources = new PdfDictionary();
            }
            acroForm.document = document;
            acroForm.xfaForm = new XfaForm(document);
        }

        return acroForm;
    }

    /**
     * This method adds the field to the last page in the document.
     * If there's no pages, creates a new one.
     *
     * @param field the {@link PdfFormField} to be added to the form
     */
    public void addField(PdfFormField field) {
        PdfPage page;
        if (document.getNumberOfPages() == 0) {
            document.addNewPage();
        }
        page = document.getLastPage();
        addField(field, page);
    }

    /**
     * This method adds the field to a specific page.
     *
     * @param field the {@link PdfFormField} to be added to the form
     * @param page  the {@link PdfPage} on which to add the field
     */
    public void addField(PdfFormField field, PdfPage page) {
        PdfArray kids = field.getKids();

        PdfDictionary fieldDic = field.getPdfObject();
        if (kids != null) {
            processKids(kids, fieldDic, page);
        }

        getFields().add(fieldDic);
        fields.put(field.getFieldName().toUnicodeString(), field);
        if (field.getKids() != null) {
            iterateFields(field.getKids(), fields);
        }

        if (fieldDic.containsKey(PdfName.Subtype) && page != null) {
            PdfAnnotation annot = PdfAnnotation.makeAnnotation(fieldDic);
            addWidgetAnnotationToPage(page, annot);
        }
    }

    /**
     * This method merges field with its annotation and place it on the given
     * page. This method won't work if the field has no or more than one widget
     * annotations.
     *
     * @param field the {@link PdfFormField} to be added to the form
     * @param page  the {@link PdfPage} on which to add the field
     */
    public void addFieldAppearanceToPage(PdfFormField field, PdfPage page) {
        PdfDictionary fieldDict = field.getPdfObject();
        PdfArray kids = field.getKids();
        if (kids == null || kids.size() > 1) {
            return;
        }

        PdfDictionary kidDict = (PdfDictionary) kids.get(0);
        PdfName type = kidDict.getAsName(PdfName.Subtype);
        if (type != null && type.equals(PdfName.Widget)) {
            if (!kidDict.containsKey(PdfName.FT)) { // kid is not a merged field with widget
                mergeWidgetWithParentField(fieldDict, kidDict);
            }
            defineWidgetPageAndAddToIt(page, fieldDict, false);
        }
    }

    /**
     * Gets the {@link PdfFormField form field}s as a {@link Map}.
     *
     * @return a map of field names and their associated {@link PdfFormField form field} objects
     */
    public Map<String, PdfFormField> getFormFields() {
        if (fields.size() == 0) {
            fields = iterateFields(getFields());
        }
        return fields;
    }

    /**
     * Gets a collection of {@link PdfFormField form field}s, prepared for flattening using {@link #partialFormFlattening} method.
     * If returned collection is empty, all form fields will be flattened on {@link #flattenFields flattenFields} call.
     *
     * @return a collection of {@link PdfFormField form field}s for flattening
     */
    public Collection<PdfFormField> getFieldsForFlattening() {
        return Collections.unmodifiableCollection(fieldsForFlattening);
    }

    /**
     * Gets the {@link PdfDocument} this {@link PdfAcroForm} belongs to.
     *
     * @return the document of this form
     */
    public PdfDocument getPdfDocument() {
        return document;
    }

    /**
     * Sets the <code>NeedAppearances</code> boolean property on the AcroForm.
     * NeedAppearances has been deprecated in PDF 2.0.
     * <br>
     * <blockquote>
     * NeedAppearances is a flag specifying whether to construct appearance
     * streams and appearance dictionaries for all widget annotations in the
     * document.
     * (ISO 32000-1, section 12.7.2 "Interactive Form Dictionary")
     * </blockquote>
     *
     * @param needAppearances a boolean. Default value is <code>false</code>
     * @return current AcroForm.
     */
    public PdfAcroForm setNeedAppearances(boolean needAppearances) {
        if (VersionConforming.validatePdfVersionForDeprecatedFeatureLogError(document, PdfVersion.PDF_2_0, VersionConforming.DEPRECATED_NEED_APPEARANCES_IN_ACROFORM)) {
            getPdfObject().remove(PdfName.NeedAppearances);
            return this;
        } else {
            return put(PdfName.NeedAppearances, PdfBoolean.valueOf(needAppearances));
        }
    }

    /**
     * Gets the <code>NeedAppearances</code> boolean property on the AcroForm.
     * NeedAppearances has been deprecated in PDF 2.0.
     * <br>
     * <blockquote>
     * NeedAppearances is a flag specifying whether to construct appearance
     * streams and appearance dictionaries for all widget annotations in the
     * document.
     * (ISO 32000-1, section 12.7.2 "Interactive Form Dictionary")
     * </blockquote>
     *
     * @return the <code>NeedAppearances</code> property as a {@link PdfBoolean}. Default value is <code>false</code>
     */
    public PdfBoolean getNeedAppearances() {
        return getPdfObject().getAsBoolean(PdfName.NeedAppearances);
    }

    /**
     * Sets the <code>SigFlags</code> integer property on the AcroForm.
     * <br>
     * <blockquote>
     * SigFlags is a set of flags specifying various document-level
     * characteristics related to signature fields.
     * (ISO 32000-1, section 12.7.2 "Interactive Form Dictionary")
     * </blockquote>
     *
     * @param sigFlags an integer. Use {@link #SIGNATURE_EXIST} and/or {@link #APPEND_ONLY}.
     *                 Use bitwise OR operator to combine these values. Default value is <code>0</code>
     * @return current AcroForm.
     */
    public PdfAcroForm setSignatureFlags(int sigFlags) {
        return put(PdfName.SigFlags, new PdfNumber(sigFlags));
    }

    /**
     * Changes the <code>SigFlags</code> integer property on the AcroForm.
     * This method allows only to add flags, not to remove them.
     * <br>
     * <blockquote>
     * SigFlags is a set of flags specifying various document-level
     * characteristics related to signature fields.
     * (ISO 32000-1, section 12.7.2 "Interactive Form Dictionary")
     * </blockquote>
     *
     * @param sigFlag an integer. Use {@link #SIGNATURE_EXIST} and/or {@link #APPEND_ONLY}.
     *                Use bitwise OR operator to combine these values. Default is <code>0</code>
     * @return current AcroForm.
     */
    public PdfAcroForm setSignatureFlag(int sigFlag) {
        int flags = getSignatureFlags();
        flags = flags | sigFlag;

        return setSignatureFlags(flags);
    }

    /**
     * Gets the <code>SigFlags</code> integer property on the AcroForm.
     * <br>
     * <blockquote>
     * SigFlags is a set of flags specifying various document-level
     * characteristics related to signature fields
     * (ISO 32000-1, section 12.7.2 "Interactive Form Dictionary")
     * </blockquote>
     *
     * @return current value for <code>SigFlags</code>.
     */
    public int getSignatureFlags() {
        PdfNumber f = getPdfObject().getAsNumber(PdfName.SigFlags);
        if (f != null)
            return f.intValue();
        else
            return 0;
    }

    /**
     * Sets the <code>CO</code> array property on the AcroForm.
     * <br>
     * <blockquote>
     * <code>CO</code>, Calculation Order, is an array of indirect references to
     * field dictionaries with calculation actions, defining the calculation
     * order in which their values will be recalculated when the value of any
     * field changes
     * (ISO 32000-1, section 12.7.2 "Interactive Form Dictionary")
     * </blockquote>
     *
     * @param calculationOrder an array of indirect references
     * @return current AcroForm
     */
    public PdfAcroForm setCalculationOrder(PdfArray calculationOrder) {
        return put(PdfName.CO, calculationOrder);
    }

    /**
     * Gets the <code>CO</code> array property on the AcroForm.
     * <br>
     * <blockquote>
     * <code>CO</code>, Calculation Order, is an array of indirect references to
     * field dictionaries with calculation actions, defining the calculation
     * order in which their values will be recalculated when the value of any
     * field changes
     * (ISO 32000-1, section 12.7.2 "Interactive Form Dictionary")
     * </blockquote>
     *
     * @return an array of indirect references
     */
    public PdfArray getCalculationOrder() {
        return getPdfObject().getAsArray(PdfName.CO);
    }

    /**
     * Sets the <code>DR</code> dictionary property on the AcroForm.
     * <br>
     * <blockquote>
     * <code>DR</code> is a resource dictionary containing default resources
     * (such as fonts, patterns, or colour spaces) that shall be used by form
     * field appearance streams. At a minimum, this dictionary shall contain a
     * Font entry specifying the resource name and font dictionary of the
     * default font for displaying text.
     * (ISO 32000-1, section 12.7.2 "Interactive Form Dictionary")
     * </blockquote>
     *
     * @param defaultResources a resource dictionary
     * @return current AcroForm
     */
    public PdfAcroForm setDefaultResources(PdfDictionary defaultResources) {
        return put(PdfName.DR, defaultResources);
    }

    /**
     * Gets the <code>DR</code> dictionary property on the AcroForm.
     * <br>
     * <blockquote>
     * <code>DR</code> is a resource dictionary containing default resources
     * (such as fonts, patterns, or colour spaces) that shall be used by form
     * field appearance streams. At a minimum, this dictionary shall contain a
     * Font entry specifying the resource name and font dictionary of the
     * default font for displaying text.
     * (ISO 32000-1, section 12.7.2 "Interactive Form Dictionary")
     * </blockquote>
     *
     * @return a resource dictionary
     */
    public PdfDictionary getDefaultResources() {
        return getPdfObject().getAsDictionary(PdfName.DR);
    }

    /**
     * Sets the <code>DA</code> String property on the AcroForm.
     * <br>
     * This method sets a default (fallback value) for the <code>DA</code>
     * attribute of variable text {@link PdfFormField form field}s.
     *
     * @param appearance a String containing a sequence of valid PDF syntax
     * @return current AcroForm
     * @see PdfFormField#setDefaultAppearance(java.lang.String)
     */
    public PdfAcroForm setDefaultAppearance(String appearance) {
        return put(PdfName.DA, new PdfString(appearance));
    }

    /**
     * Gets the <code>DA</code> String property on the AcroForm.
     * <br>
     * This method returns the default (fallback value) for the <code>DA</code>
     * attribute of variable text {@link PdfFormField form field}s.
     *
     * @return the form-wide default appearance, as a <code>String</code>
     */
    public PdfString getDefaultAppearance() {
        return getPdfObject().getAsString(PdfName.DA);
    }

    /**
     * Sets the <code>Q</code> integer property on the AcroForm.
     * <br>
     * This method sets a default (fallback value) for the <code>Q</code>
     * attribute of variable text {@link PdfFormField form field}s.
     *
     * @param justification an integer representing a justification value
     * @return current AcroForm
     * @see PdfFormField#setJustification(int)
     */
    public PdfAcroForm setDefaultJustification(int justification) {
        return put(PdfName.Q, new PdfNumber(justification));
    }

    /**
     * Gets the <code>Q</code> integer property on the AcroForm.
     * <br>
     * This method gets the default (fallback value) for the <code>Q</code>
     * attribute of variable text {@link PdfFormField form field}s.
     *
     * @return an integer representing a justification value
     * @see PdfFormField#getJustification()
     */
    public PdfNumber getDefaultJustification() {
        return getPdfObject().getAsNumber(PdfName.Q);
    }

    /**
     * Sets the <code>XFA</code> property on the AcroForm.
     * <br>
     * <code>XFA</code> can either be a {@link PdfStream} or a {@link PdfArray}.
     * Its contents must be valid XFA.
     *
     * @param xfaResource a stream containing the XDP
     * @return current AcroForm
     */
    public PdfAcroForm setXFAResource(PdfStream xfaResource) {
        return put(PdfName.XFA, xfaResource);
    }

    /**
     * Sets the <code>XFA</code> property on the AcroForm.
     * <br>
     * <code>XFA</code> can either be a {@link PdfStream} or a {@link PdfArray}.
     * Its contents must be valid XFA.
     *
     * @param xfaResource an array of text string and stream pairs representing
     *                    the individual packets comprising the XML Data Package. (ISO 32000-1,
     *                    section 12.7.2 "Interactive Form Dictionary")
     * @return current AcroForm
     */
    public PdfAcroForm setXFAResource(PdfArray xfaResource) {
        return put(PdfName.XFA, xfaResource);
    }

    /**
     * Gets the <code>XFA</code> property on the AcroForm.
     *
     * @return an object representing the entire XDP. It can either be a
     * {@link PdfStream} or a {@link PdfArray}.
     */
    public PdfObject getXFAResource() {
        return getPdfObject().get(PdfName.XFA);
    }

    /**
     * Gets a {@link PdfFormField form field} by its name.
     *
     * @param fieldName the name of the {@link PdfFormField form field} to retrieve
     * @return the {@link PdfFormField form field}, or <code>null</code> if it
     * isn't present
     */
    public PdfFormField getField(String fieldName) {
        return fields.get(fieldName);
    }

    /**
     * Gets the attribute generateAppearance, which tells {@link #flattenFields()}
     * to generate an appearance Stream for all {@link PdfFormField form field}s
     * that don't have one.
     *
     * @return bolean value indicating if the appearances need to be generated
     */
    public boolean isGenerateAppearance() {
        return generateAppearance;
    }

    /**
     * Sets the attribute generateAppearance, which tells {@link #flattenFields()}
     * to generate an appearance Stream for all {@link PdfFormField form field}s
     * that don't have one.
     * <p>
     * Not generating appearances will speed up form flattening but the results
     * can be unexpected in Acrobat. Don't use it unless your environment is
     * well controlled. The default is <CODE>true</CODE>.
     * <p>
     * If generateAppearance is set to <code>true</code>, then
     * <code>NeedAppearances</code> is set to <code>false</code>. This does not
     * apply vice versa.
     * <p>
     * Note, this method does not change default behaviour of {@link PdfFormField#setValue(String)} method.
     *
     * @param generateAppearance a boolean
     */
    public void setGenerateAppearance(boolean generateAppearance) {
        if (generateAppearance) {
            getPdfObject().remove(PdfName.NeedAppearances);
        }
        this.generateAppearance = generateAppearance;
    }

    /**
     * Flattens interactive {@link PdfFormField form field}s in the document. If
     * no fields have been explicitly included via {@link #partialFormFlattening},
     * then all fields are flattened. Otherwise only the included fields are
     * flattened.
     */
    public void flattenFields() {
        if (document.isAppendMode()) {
            throw new PdfException(PdfException.FieldFlatteningIsNotSupportedInAppendMode);
        }
        Set<PdfFormField> fields;
        if (fieldsForFlattening.size() == 0) {
            this.fields.clear();
            fields = new LinkedHashSet<>(getFormFields().values());
        } else {
            fields = new LinkedHashSet<>();
            for (PdfFormField field : fieldsForFlattening) {
                fields.addAll(prepareFieldsForFlattening(field));
            }
        }

        // In case of appearance resources and page resources are the same object, it would not be possible to add
        // the xObject to the page resources. So in that case we would copy page resources and use the copy for
        // xObject, so that circular reference is avoided.
        // We copy beforehand firstly not to produce a copy every time, and secondly not to copy all the
        // xObjects that have already been added to the page resources.
        Map<Integer, PdfObject> initialPageResourceClones = new LinkedHashMap<>();
        for (int i = 1; i <= document.getNumberOfPages(); i++) {
            PdfObject resources = document.getPage(i).getPdfObject().getAsDictionary(PdfName.Resources);
            initialPageResourceClones.put(i, resources == null ? null : resources.clone());
        }

        Set<PdfPage> wrappedPages = new LinkedHashSet<>();
        PdfPage page;
        for (PdfFormField field : fields) {
            PdfDictionary fieldObject = field.getPdfObject();
            page = getFieldPage(fieldObject);
            if (page == null) {
                continue;
            }

            PdfAnnotation annotation = PdfAnnotation.makeAnnotation(fieldObject);
            TagTreePointer tagPointer = null;
            if (annotation != null && document.isTagged()) {
                tagPointer = document.getTagStructureContext().removeAnnotationTag(annotation);
            }

            PdfDictionary appDic = fieldObject.getAsDictionary(PdfName.AP);
            PdfObject asNormal = null;
            if (appDic != null) {
                asNormal = appDic.getAsStream(PdfName.N);
                if (asNormal == null) {
                    asNormal = appDic.getAsDictionary(PdfName.N);
                }
            }
            if (generateAppearance) {
                if (appDic == null || asNormal == null) {
                    field.regenerateField();
                    appDic = fieldObject.getAsDictionary(PdfName.AP);
                }
            }
            PdfObject normal = appDic != null ? appDic.get(PdfName.N) : null;
            if (null != normal) {
                PdfFormXObject xObject = null;
                if (normal.isStream()) {
                    xObject = new PdfFormXObject((PdfStream) normal);
                } else if (normal.isDictionary()) {
                    PdfName as = fieldObject.getAsName(PdfName.AS);
                    if (((PdfDictionary) normal).getAsStream(as) != null) {
                        xObject = new PdfFormXObject(((PdfDictionary) normal).getAsStream(as));
                        xObject.makeIndirect(document);
                    }
                }

                if (xObject != null) {
                    //subtype is required field for FormXObject, but can be omitted in normal appearance.
                    xObject.put(PdfName.Subtype, PdfName.Form);
                    Rectangle annotBBox = fieldObject.getAsRectangle(PdfName.Rect);
                    if (page.isFlushed()) {
                        throw new PdfException(PdfException.PageAlreadyFlushedUseAddFieldAppearanceToPageMethodBeforePageFlushing);
                    }
                    PdfCanvas canvas = new PdfCanvas(page, !wrappedPages.contains(page));
                    wrappedPages.add(page);

                    // Here we avoid circular reference which might occur when page resources and the appearance xObject's
                    // resources are the same object
                    PdfObject xObjectResources = xObject.getPdfObject().get(PdfName.Resources);
                    PdfObject pageResources = page.getResources().getPdfObject();
                    if (xObjectResources != null && xObjectResources == pageResources) {
                        xObject.getPdfObject().put(PdfName.Resources, initialPageResourceClones.get(document.getPageNumber(page)));
                    }

                    if (tagPointer != null) {
                        tagPointer.setPageForTagging(page);
                        TagReference tagRef = tagPointer.getTagReference();
                        canvas.openTag(tagRef);
                    }

                    AffineTransform at = calcFieldAppTransformToAnnotRect(xObject, annotBBox);
                    float[] m = new float[6];
                    at.getMatrix(m);
                    canvas.addXObject(xObject, m[0], m[1], m[2], m[3], m[4], m[5]);

                    if (tagPointer != null) {
                        canvas.closeTag();
                    }
                }
            } else {
                logger.error(LogMessageConstant.N_ENTRY_IS_REQUIRED_FOR_APPEARANCE_DICTIONARY);
            }

            PdfArray fFields = getFields();
            fFields.remove(fieldObject);
            if (annotation != null) {
                page.removeAnnotation(annotation);
            }
            PdfDictionary parent = fieldObject.getAsDictionary(PdfName.Parent);
            if (parent != null) {
                PdfArray kids = parent.getAsArray(PdfName.Kids);
                if(kids!=null) {
                    kids.remove(fieldObject);
                    // TODO what if parent was in it's turn the only child of it's parent (parent of parent)?
                    // shouldn't we remove them recursively? check it
                    if (kids.isEmpty()) {
                        fFields.remove(parent);
                    }
                } else {
                    fFields.remove(parent);
                }
            }
        }

        getPdfObject().remove(PdfName.NeedAppearances);
        if (fieldsForFlattening.size() == 0) {
            getFields().clear();
        }
        if (getFields().isEmpty()) {
            document.getCatalog().remove(PdfName.AcroForm);
        }
    }

    /**
     * Tries to remove the {@link PdfFormField form field} with the specified
     * name from the document.
     *
     * @param fieldName the name of the {@link PdfFormField form field} to remove
     * @return a boolean representing whether or not the removal succeeded.
     */
    public boolean removeField(String fieldName) {
        PdfFormField field = getField(fieldName);
        if (field == null) {
            return false;
        }

        PdfDictionary fieldObject = field.getPdfObject();
        PdfPage page = getFieldPage(fieldObject);

        PdfAnnotation annotation = PdfAnnotation.makeAnnotation(fieldObject);
        if (page != null && annotation != null) {
            page.removeAnnotation(annotation);
        }

        PdfDictionary parent = field.getParent();
        if (parent != null) {
            parent.getAsArray(PdfName.Kids).remove(fieldObject);
            fields.remove(fieldName);
            return true;
        }

        PdfArray fieldsPdfArray = getFields();
        if (fieldsPdfArray.contains(fieldObject)) {
            fieldsPdfArray.remove(fieldObject);
            this.fields.remove(fieldName);
            return true;
        }
        return false;
    }

    /**
     * Adds a {@link PdfFormField form field}, identified by name, to the list of fields to be flattened.
     * Does not perform a flattening operation in itself.
     *
     * @param fieldName the name of the {@link PdfFormField form field} to be flattened
     */
    public void partialFormFlattening(String fieldName) {
        PdfFormField field = getFormFields().get(fieldName);
        if (field != null) {
            fieldsForFlattening.add(field);
        }
    }

    /**
     * Changes the identifier of a {@link PdfFormField form field}.
     *
     * @param oldName the current name of the field
     * @param newName the new name of the field. Must not be used currently.
     */
    public void renameField(String oldName, String newName) {
        Map<String, PdfFormField> fields = getFormFields();
        if (fields.containsKey(newName)) {
            return;
        }
        PdfFormField field = fields.get(oldName);
        if (field != null) {
            field.setFieldName(newName);
            fields.remove(oldName);
            fields.put(newName, field);
        }
    }

    /**
     * Creates an in-memory copy of a {@link PdfFormField}. This new field is
     * not added to the document.
     *
     * @param name the name of the {@link PdfFormField form field} to be copied
     * @return a clone of the original {@link PdfFormField}
     */
    public PdfFormField copyField(String name) {
        PdfFormField oldField = getField(name);
        if (oldField != null) {
            return new PdfFormField((PdfDictionary) oldField.getPdfObject().clone().makeIndirect(document));
        }
        return null;
    }

    /**
     * Replaces the {@link PdfFormField} of a certain name with another
     * {@link PdfFormField}.
     *
     * @param name  the name of the {@link PdfFormField form field} to be replaced
     * @param field the new {@link PdfFormField}
     */
    public void replaceField(String name, PdfFormField field) {
        removeField(name);
        addField(field);
    }

    /**
     * Gets all AcroForm fields in the document.
     *
     * @return a {@link PdfArray} of field dictionaries
     */
    protected PdfArray getFields() {
        PdfArray fields = getPdfObject().getAsArray(PdfName.Fields);
        if (fields == null) {
            logger.warn(LogMessageConstant.NO_FIELDS_IN_ACROFORM);
            fields = new PdfArray();
            getPdfObject().put(PdfName.Fields, fields);
        }
        return fields;
    }

    @Override
    protected boolean isWrappedObjectMustBeIndirect() {
        return false;
    }

    private Map<String, PdfFormField> iterateFields(PdfArray array, Map<String, PdfFormField> fields) {
        int index = 1;
        for (PdfObject field : array) {
            if (field.isFlushed()) {
                logger.info(LogMessageConstant.FORM_FIELD_WAS_FLUSHED);
                continue;
            }
            PdfFormField formField = PdfFormField.makeFormField(field, document);
            PdfString fieldName = formField.getFieldName();
            String name;
            if (fieldName == null) {
                PdfFormField parentField = PdfFormField.makeFormField(formField.getParent(), document);
                while (fieldName == null) {
                    fieldName = parentField.getFieldName();
                    if (fieldName == null) {
                        parentField = PdfFormField.makeFormField(parentField.getParent(), document);
                    }
                }
                name = fieldName.toUnicodeString() + "." + index;
                index++;
            } else {
                name = fieldName.toUnicodeString();
            }
            fields.put(name, formField);
            if (formField.getKids() != null) {
                iterateFields(formField.getKids(), fields);
            }
        }

        return fields;
    }

    private Map<String, PdfFormField> iterateFields(PdfArray array) {
        return iterateFields(array, new LinkedHashMap<String, PdfFormField>());
    }

    private PdfDictionary processKids(PdfArray kids, PdfDictionary parent, PdfPage page) {
        if (kids.size() == 1) {
            PdfDictionary kidDict = (PdfDictionary) kids.get(0);
            PdfName type = kidDict.getAsName(PdfName.Subtype);
            if (type != null && type.equals(PdfName.Widget)) {
                if (!kidDict.containsKey(PdfName.FT)) { // kid is not merged field with widget
                    mergeWidgetWithParentField(parent, kidDict);
                    defineWidgetPageAndAddToIt(page, parent, true);
                } else {
                    defineWidgetPageAndAddToIt(page, kidDict, true);
                }
            } else {
                PdfArray otherKids = (kidDict).getAsArray(PdfName.Kids);
                if (otherKids != null) {
                    processKids(otherKids, kidDict, page);
                }
            }
        } else {
            for (int i = 0; i < kids.size(); i++) {
                PdfObject kid = kids.get(i);
                PdfArray otherKids = ((PdfDictionary) kid).getAsArray(PdfName.Kids);
                if (otherKids != null) {
                    processKids(otherKids, (PdfDictionary) kid, page);
                }
            }
        }

        return parent;
    }

    private void mergeWidgetWithParentField(PdfDictionary parent, PdfDictionary widgetDict) {
        parent.remove(PdfName.Kids);
        widgetDict.remove(PdfName.Parent);
        parent.mergeDifferent(widgetDict);
    }

    private void defineWidgetPageAndAddToIt(PdfPage currentPage, PdfDictionary mergedFieldAndWidget, boolean warnIfPageFlushed) {
        PdfAnnotation annot = PdfAnnotation.makeAnnotation(mergedFieldAndWidget);
        PdfDictionary pageDic = annot.getPageObject();
        if (pageDic != null) {
            if (warnIfPageFlushed && pageDic.isFlushed()) {
                throw new PdfException(PdfException.PageAlreadyFlushedUseAddFieldAppearanceToPageMethodBeforePageFlushing);
            }
            PdfDocument doc = pageDic.getIndirectReference().getDocument();
            PdfPage widgetPage = doc.getPage(pageDic);
            addWidgetAnnotationToPage(widgetPage, annot);
        } else {
            addWidgetAnnotationToPage(currentPage, annot);
        }
    }

    private void addWidgetAnnotationToPage(PdfPage page, PdfAnnotation annot) {
        if (page.containsAnnotation(annot)) {
            return;
        }

        TagTreePointer tagPointer = null;
        boolean tagged = page.getDocument().isTagged();
        if (tagged) {
            tagPointer = page.getDocument().getTagStructureContext().getAutoTaggingPointer();
            //TODO attributes?
            tagPointer.addTag(StandardRoles.FORM);
        }

        page.addAnnotation(annot);

        if (tagged) {
            tagPointer.moveToParent();
        }
    }

    /**
     * Determines whether the AcroForm contains XFA data.
     *
     * @return a boolean
     */
    public boolean hasXfaForm() {
        return xfaForm != null && xfaForm.isXfaPresent();
    }

    /**
     * Gets the {@link XfaForm} atribute.
     *
     * @return the XFA form object
     */
    public XfaForm getXfaForm() {
        return xfaForm;
    }

    /**
     * Removes the XFA stream from the document.
     */
    public void removeXfaForm() {
        if (hasXfaForm()) {
            PdfDictionary root = document.getCatalog().getPdfObject();
            PdfDictionary acroform = root.getAsDictionary(PdfName.AcroForm);
            acroform.remove(PdfName.XFA);
            xfaForm = null;
        }
    }

    public PdfAcroForm put(PdfName key, PdfObject value) {
        getPdfObject().put(key, value);
        return this;
    }

    /**
     * Releases underlying pdf object and other pdf entities used by wrapper.
     * This method should be called instead of direct call to {@link PdfObject#release()} if the wrapper is used.
     */
    public void release() {
        unsetForbidRelease();
        getPdfObject().release();
        for (PdfFormField field : fields.values()) {
            field.release();
        }
        fields = null;
    }

    private static PdfDictionary createAcroFormDictionaryByFields(PdfArray fields) {
        PdfDictionary dictionary = new PdfDictionary();
        dictionary.put(PdfName.Fields, fields);
        return dictionary;
    }

    private PdfPage getFieldPage(PdfDictionary annotDic) {
        PdfDictionary pageDic = annotDic.getAsDictionary(PdfName.P);
        if (pageDic != null) {
            return document.getPage(pageDic);
        }
        for (int i = 1; i <= document.getNumberOfPages(); i++) {
            PdfPage page = document.getPage(i);
            if (!page.isFlushed()) {
                PdfAnnotation annotation = PdfAnnotation.makeAnnotation(annotDic);
                if (annotation != null && page.containsAnnotation(annotation)) {
                    return page;
                }
            }
        }
        return null;
    }

    private Set<PdfFormField> prepareFieldsForFlattening(PdfFormField field) {
        Set<PdfFormField> preparedFields = new LinkedHashSet<>();
        preparedFields.add(field);
        PdfArray kids = field.getKids();
        if (kids != null) {
            for (PdfObject kid : kids) {
                PdfFormField kidField = new PdfFormField((PdfDictionary) kid);
                preparedFields.add(kidField);
                if (kidField.getKids() != null) {
                    preparedFields.addAll(prepareFieldsForFlattening(kidField));
                }
            }
        }
        return preparedFields;
    }

    private AffineTransform calcFieldAppTransformToAnnotRect(PdfFormXObject xObject, Rectangle annotBBox) {
        PdfArray bBox = xObject.getBBox();
        if (bBox.size() != 4) {
            bBox = new PdfArray(new Rectangle(0, 0));
            xObject.setBBox(bBox);
        }
        float[] xObjBBox = bBox.toFloatArray();

        PdfArray xObjMatrix = xObject.getPdfObject().getAsArray(PdfName.Matrix);
        Rectangle transformedRect;
        if (xObjMatrix != null && xObjMatrix.size() == 6) {
            Point[] xObjRectPoints = new Point[]{
                    new Point(xObjBBox[0], xObjBBox[1]),
                    new Point(xObjBBox[0], xObjBBox[3]),
                    new Point(xObjBBox[2], xObjBBox[1]),
                    new Point(xObjBBox[2], xObjBBox[3])
            };
            Point[] transformedAppBoxPoints = new Point[xObjRectPoints.length];
            new AffineTransform(xObjMatrix.toDoubleArray()).transform(xObjRectPoints, 0, transformedAppBoxPoints, 0, xObjRectPoints.length);

            float[] transformedRectArr = new float[] {
                    Float.MAX_VALUE, Float.MAX_VALUE,
                    -Float.MAX_VALUE, -Float.MAX_VALUE,
            };
            for (Point p : transformedAppBoxPoints) {
                transformedRectArr[0] = (float) Math.min(transformedRectArr[0], p.x);
                transformedRectArr[1] = (float) Math.min(transformedRectArr[1], p.y);
                transformedRectArr[2] = (float) Math.max(transformedRectArr[2], p.x);
                transformedRectArr[3] = (float) Math.max(transformedRectArr[3], p.y);
            }

            transformedRect = new Rectangle(transformedRectArr[0], transformedRectArr[1], transformedRectArr[2] - transformedRectArr[0], transformedRectArr[3] - transformedRectArr[1]);
        } else {
            transformedRect = new Rectangle(0, 0).setBbox(xObjBBox[0], xObjBBox[1], xObjBBox[2], xObjBBox[3]);
        }

        AffineTransform at = AffineTransform.getTranslateInstance(-transformedRect.getX(), -transformedRect.getY());
        float scaleX = transformedRect.getWidth() == 0 ? 1 : annotBBox.getWidth() / transformedRect.getWidth();
        float scaleY = transformedRect.getHeight() == 0 ? 1 : annotBBox.getHeight() / transformedRect.getHeight();
        at.preConcatenate(AffineTransform.getScaleInstance(scaleX, scaleY));
        at.preConcatenate(AffineTransform.getTranslateInstance(annotBBox.getX(), annotBBox.getY()));

        return at;
    }
}
