package com.itextpdf.core.pdf;

import com.itextpdf.basics.PdfException;
import com.itextpdf.core.font.PdfFont;
import com.itextpdf.core.pdf.colorspace.PdfColorSpace;
import com.itextpdf.core.pdf.colorspace.PdfPattern;
import com.itextpdf.core.pdf.colorspace.PdfShading;
import com.itextpdf.core.pdf.extgstate.PdfExtGState;
import com.itextpdf.core.pdf.xobject.PdfFormXObject;
import com.itextpdf.core.pdf.xobject.PdfImageXObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class PdfResources extends PdfObjectWrapper<PdfDictionary> {

    private static final String F = "F";
    private static final String Im = "Im";
    private static final String Fm = "Fm";
    private static final String Gs = "Gs";
    private static final String Pr = "Pr";
    private static final String Cs = "Cs";
    private static final String P = "P";
    private static final String Sh = "Sh";


    private Map<PdfObject, PdfName> resourceToName = new HashMap<PdfObject, PdfName>();
    private Map<PdfName, Map<PdfName, PdfObject>> nameToResource = new HashMap<PdfName, Map<PdfName, PdfObject>>();

    /** The font value counter for the fonts in the document. */
    private ResourceNumber fontNumber = new ResourceNumber();
    private ResourceNumber imageNumber = new ResourceNumber();
    private ResourceNumber formNumber = new ResourceNumber();
    private ResourceNumber egsNumber = new ResourceNumber();
    private ResourceNumber propNumber = new ResourceNumber();
    private ResourceNumber csNumber = new ResourceNumber();
    private ResourceNumber patternNumber = new ResourceNumber();
    private ResourceNumber shadingNumber = new ResourceNumber();

    public PdfResources(PdfDictionary pdfObject) throws PdfException {
        super(pdfObject);
        buildResources(pdfObject);
    }

    public PdfResources() throws PdfException {
        this(new PdfDictionary());
    }

    public PdfName addFont(PdfFont font) throws PdfException {
        font.getDocument().getDocumentFonts().add(font);
        return addResource(font, PdfName.Font, F, fontNumber);
    }

    public PdfName addImage(PdfImageXObject image) throws PdfException {
        return addResource(image, PdfName.XObject, Im, imageNumber);
    }

    public PdfName addImage(PdfObject image) throws PdfException {
        return addResource(image, PdfName.XObject, Im, imageNumber);
    }

    public PdfName addForm(PdfFormXObject form) throws PdfException {
        return addResource(form, PdfName.XObject, Fm, formNumber);
    }

    public PdfName addForm(PdfObject form) throws PdfException {
        return addResource(form, PdfName.XObject, Fm, formNumber);
    }

    public PdfName addExtGState(PdfExtGState extGState) throws PdfException {
        return addResource(extGState, PdfName.ExtGState, Gs, egsNumber);
    }

    public PdfName addExtGState(PdfObject extGState) throws PdfException {
        return addResource(extGState, PdfName.ExtGState, Gs, egsNumber);
    }

    public PdfName addProperties(PdfObject properties) throws PdfException {
        return addResource(properties, PdfName.Properties, Pr, propNumber);
    }

    public PdfName addColorSpace(PdfColorSpace cs) throws PdfException {
        return addResource(cs, PdfName.ColorSpace, Cs, csNumber);
    }

    public PdfName addColorSpace(PdfObject colorSpace) throws PdfException {
        return addResource(colorSpace, PdfName.ColorSpace, Cs, csNumber);
    }

    public PdfName addPattern(PdfPattern pattern) throws PdfException {
        return addResource(pattern, PdfName.Pattern, P, patternNumber);
    }

    public PdfName addPattern(PdfObject pattern) throws PdfException {
        return addResource(pattern, PdfName.Pattern, P, patternNumber);
    }

    public PdfName addShading(PdfShading shading) throws PdfException {
        return addResource(shading, PdfName.Shading, Sh, shadingNumber);
    }

    public PdfName addShading(PdfObject shading) throws PdfException {
        return addResource(shading, PdfName.Shading, Sh, shadingNumber);
    }

    /**
     * Sets the default color space.
     *
     * @param defaultCsKey
     * @param defaultCsValue
     * @throws PdfException
     */
    public void setDefaultColorSpace(PdfName defaultCsKey, PdfColorSpace defaultCsValue) throws PdfException {
        addResource(defaultCsValue.getPdfObject(), PdfName.ColorSpace, defaultCsKey);
    }

    public void setDefaultGray(PdfColorSpace defaultCs) throws PdfException {
        setDefaultColorSpace(PdfName.DefaultGray, defaultCs);
    }

    public void setDefaultRgb(PdfColorSpace defaultCs) throws PdfException {
        setDefaultColorSpace(PdfName.DefaultRGB, defaultCs);
    }

    public void setDefaultCmyk(PdfColorSpace defaultCs) throws PdfException {
        setDefaultColorSpace(PdfName.DefaultCMYK, defaultCs);
    }

    public PdfName getResourceName(PdfObjectWrapper resource) {
        return resourceToName.get(resource.getPdfObject());
    }

    public PdfName getResourceName(PdfObject resource) {
        PdfName resName = resourceToName.get(resource);
        if (resName == null)
            resName = resourceToName.get(resource.getIndirectReference());
        return resName;
    }

    public Set<PdfName> getResourceNames() {
        Set<PdfName> names = new TreeSet<PdfName>();
        for (PdfName resType : nameToResource.keySet()) {
            names.addAll(getResourceNames(resType));
        }
        return names;
    }

    public PdfArray getProcSet() throws PdfException {
        return getPdfObject().getAsArray(PdfName.ProcSet);
    }

    public void setProcSet(PdfArray array) {
        getPdfObject().put(PdfName.ProcSet, array);
    }

    public Set<PdfName> getResourceNames(PdfName resType) {
        Map<PdfName, PdfObject> resourceCategory = nameToResource.get(resType);
        return resourceCategory == null ? new TreeSet<PdfName>() : resourceCategory.keySet();
    }

    protected PdfName addResource(PdfObjectWrapper resource, PdfName resType, String resPrefix, ResourceNumber resNumber) throws PdfException {
        return addResource(resource.getPdfObject(), resType, resPrefix, resNumber);
    }

    protected void addResource(PdfObject resource, PdfName resType, PdfName resName) throws PdfException {
        if (nameToResource.containsKey(resType) && nameToResource.get(resType).containsKey(resName))
            return;
        resourceToName.put(resource, resName);
        Map<PdfName, PdfObject> resourceCategory = nameToResource.get(resType);
        if (resourceCategory == null) {
            nameToResource.put(resType, resourceCategory = new HashMap<PdfName, PdfObject>());
        }
        resourceCategory.put(resName, resource);
        PdfDictionary resDictionary = (PdfDictionary) getPdfObject().get(resType);
        if (resDictionary == null) {
            getPdfObject().put(resType, resDictionary = new PdfDictionary());
        }
        resDictionary.put(resName, resource);
    }

    protected PdfName addResource(PdfObject resource, PdfName resType, String resPrefix, ResourceNumber resNumber) throws PdfException {
        PdfName resName = getResourceName(resource);
        if (resName == null) {
            resName = new PdfName(resPrefix + resNumber.increment());
            addResource(resource, resType, resName);
        }
        return resName;
    }

    protected void buildResources(PdfDictionary dictionary) throws PdfException {
        for (PdfName resourceType : dictionary.keySet()) {
            if (nameToResource.get(resourceType) == null) {
                nameToResource.put(resourceType, new HashMap<PdfName, PdfObject>());
            }
            PdfDictionary resources = dictionary.getAsDictionary(resourceType);
            if (resources == null)
                continue;
            for (PdfName resourceName : resources.keySet()) {
                PdfObject resource = resources.get(resourceName, false);
                resourceToName.put(resource, resourceName);
                nameToResource.get(resourceType).put(resourceName, resource);
            }
        }
        Set<PdfName> names = getResourceNames();
        fontNumber = getAvailableNumber(names, F);
        imageNumber = getAvailableNumber(names, Im);
        formNumber = getAvailableNumber(names, Fm);
        egsNumber = getAvailableNumber(names, Gs);
        propNumber = getAvailableNumber(names, Pr);
        csNumber = getAvailableNumber(names, Cs);
    }

    private ResourceNumber getAvailableNumber(Set<PdfName> names, final String resPrefix) {
        int resNumber = 0;
        for (PdfName name : names) {
            String nameStr = name.getValue();
            if (nameStr.startsWith(resPrefix)) {
                nameStr = nameStr.replace(resPrefix, "");
                try {
                    int number = Integer.parseInt(nameStr);
                    if (number > resNumber)
                        resNumber = number;
                } catch (NumberFormatException e) {

                }
            }
        }
        return new ResourceNumber(resNumber);
    }

    static private class ResourceNumber {
        private int value;

        public ResourceNumber(int value) {
            this.value = value;
        }

        public ResourceNumber() {
            this(0);
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }

        public int increment() {
            return ++value;
        }
    }

}
