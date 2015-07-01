package com.itextpdf.core.pdf;

import com.itextpdf.basics.PdfException;
import com.itextpdf.core.font.PdfFont;
import com.itextpdf.core.pdf.colorspace.PdfColorSpace;
import com.itextpdf.core.pdf.colorspace.PdfPattern;
import com.itextpdf.core.pdf.colorspace.PdfShading;
import com.itextpdf.core.pdf.extgstate.PdfExtGState;
import com.itextpdf.core.pdf.xobject.PdfFormXObject;
import com.itextpdf.core.pdf.xobject.PdfImageXObject;

import java.io.IOException;
import java.util.*;

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
    private Map<PdfIndirectReference, PdfFont> fontsMap = new HashMap<PdfIndirectReference, PdfFont>();
    /**
     * The font value counter for the fonts in the document.
     */
    private ResourceNumber fontNumber = new ResourceNumber();
    private ResourceNumber imageNumber = new ResourceNumber();
    private ResourceNumber formNumber = new ResourceNumber();
    private ResourceNumber egsNumber = new ResourceNumber();
    private ResourceNumber propNumber = new ResourceNumber();
    private ResourceNumber csNumber = new ResourceNumber();
    private ResourceNumber patternNumber = new ResourceNumber();
    private ResourceNumber shadingNumber = new ResourceNumber();

    public PdfResources(PdfDictionary pdfObject) {
        super(pdfObject);
        buildResources(pdfObject);
    }


    public PdfResources() {
        this(new PdfDictionary());
    }

    public PdfName addFont(PdfFont font) {
        font.getDocument().getDocumentFonts().add(font);
        fontsMap.put(font.getPdfObject().getIndirectReference(), font);
        return addResource(font, PdfName.Font, F, fontNumber);
    }

    public PdfName addImage(PdfImageXObject image) {
        return addResource(image, PdfName.XObject, Im, imageNumber);
    }

    public PdfName addImage(PdfObject image) {
        return addResource(image, PdfName.XObject, Im, imageNumber);
    }

    public PdfName addForm(PdfFormXObject form) {
        return addResource(form, PdfName.XObject, Fm, formNumber);
    }

    public PdfName addForm(PdfObject form) {
        return addResource(form, PdfName.XObject, Fm, formNumber);
    }

    public PdfName addExtGState(PdfExtGState extGState) {
        return addResource(extGState, PdfName.ExtGState, Gs, egsNumber);
    }

    public PdfName addExtGState(PdfObject extGState) {
        return addResource(extGState, PdfName.ExtGState, Gs, egsNumber);
    }

    public PdfName addProperties(PdfObject properties) {
        return addResource(properties, PdfName.Properties, Pr, propNumber);
    }

    public PdfName addColorSpace(PdfColorSpace cs) {
        return addResource(cs, PdfName.ColorSpace, Cs, csNumber);
    }

    public PdfName addColorSpace(PdfObject colorSpace) {
        return addResource(colorSpace, PdfName.ColorSpace, Cs, csNumber);
    }

    public PdfName addPattern(PdfPattern pattern) {
        return addResource(pattern, PdfName.Pattern, P, patternNumber);
    }

    public PdfName addPattern(PdfObject pattern) {
        return addResource(pattern, PdfName.Pattern, P, patternNumber);
    }

    public PdfName addShading(PdfShading shading) {
        return addResource(shading, PdfName.Shading, Sh, shadingNumber);
    }

    public PdfName addShading(PdfObject shading) {
        return addResource(shading, PdfName.Shading, Sh, shadingNumber);
    }

    /**
     * Sets the default color space.
     *
     * @param defaultCsKey
     * @param defaultCsValue
     * @throws PdfException
     */
    public void setDefaultColorSpace(PdfName defaultCsKey, PdfColorSpace defaultCsValue) {
        addResource(defaultCsValue.getPdfObject(), PdfName.ColorSpace, defaultCsKey);
    }

    public void setDefaultGray(PdfColorSpace defaultCs) {
        setDefaultColorSpace(PdfName.DefaultGray, defaultCs);
    }

    public void setDefaultRgb(PdfColorSpace defaultCs) {
        setDefaultColorSpace(PdfName.DefaultRGB, defaultCs);
    }

    public void setDefaultCmyk(PdfColorSpace defaultCs) {
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

    public PdfArray getProcSet() {
        return getPdfObject().getAsArray(PdfName.ProcSet);
    }

    public void setProcSet(PdfArray array) {
        getPdfObject().put(PdfName.ProcSet, array);
    }

    public Set<PdfName> getResourceNames(PdfName resType) {
        Map<PdfName, PdfObject> resourceCategory = nameToResource.get(resType);
        return resourceCategory == null ? new TreeSet<PdfName>() : resourceCategory.keySet();
    }

    public Map<PdfName, PdfObject> getResource(PdfName pdfName) {
        return nameToResource.get(pdfName);
    }

    public Collection<PdfFont> getFonts(boolean updateFonts) throws IOException {
        if (!updateFonts) {
            return fontsMap.values();
        }
        fontsMap.clear();
        Map<PdfName, PdfObject> fMap = getResource(PdfName.Font);
        if (fMap != null) {
            addFont(fMap.entrySet());
        }
        Map<PdfName, PdfObject> xMap = getResource(PdfName.XObject);
        if (xMap != null && !xMap.isEmpty()) {
               callXObjectFont(xMap,new HashSet<PdfDictionary>());
        }
        return fontsMap.values();
    }



    protected PdfName addResource(PdfObjectWrapper resource, PdfName resType, String resPrefix, ResourceNumber resNumber) {
        return addResource(resource.getPdfObject(), resType, resPrefix, resNumber);
    }

    protected void addResource(PdfObject resource, PdfName resType, PdfName resName) {
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

    protected PdfName addResource(PdfObject resource, PdfName resType, String resPrefix, ResourceNumber resNumber) {
        PdfName resName = getResourceName(resource);
        if (resName == null) {
            resName = new PdfName(resPrefix + resNumber.increment());
            addResource(resource, resType, resName);
        }
        return resName;
    }

    protected void buildResources(PdfDictionary dictionary) {
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

    private void addFont(Set<Map.Entry<PdfName, PdfObject>> entrySet) throws IOException {
        for (Map.Entry<PdfName, PdfObject> entry : entrySet) {
            if (entry.getValue().isIndirectReference() && !fontsMap.containsKey(entry.getValue())) {
                fontsMap.put((PdfIndirectReference) entry.getValue(),
                        PdfFont.createFont(getDocument(), (PdfDictionary) ((PdfIndirectReference) entry.getValue()).getRefersTo()));
            } else if (entry.getValue().isDictionary()) {
                PdfFont font = PdfFont.createFont(getDocument(), (PdfDictionary) entry.getValue());
                fontsMap.put(font.getPdfObject().getIndirectReference(), font);
            }
        }
    }

    private void addFontFromXObject(Map<PdfName, PdfObject> xMap, HashSet<PdfDictionary> visitedResources) throws IOException {
        PdfDictionary xObject = new PdfDictionary(xMap);
        PdfDictionary resources = xObject.getAsDictionary(PdfName.Resources);
        if (resources == null)
            return;
        PdfDictionary font = resources.getAsDictionary(PdfName.Font);

        if (font != null) {
            addFont(font.entrySet());
        }
        PdfDictionary xobj = resources.getAsDictionary(PdfName.XObject);
        if (xobj != null) {
            if (visitedResources.add(xobj)) {
                callXObjectFont(xobj.getMap(),visitedResources);
                visitedResources.remove(xobj);
            } else {
                throw new PdfException(PdfException.IllegalResourceTree);
            }
        }
    }

    private void callXObjectFont(Map<PdfName, PdfObject> xMap, HashSet<PdfDictionary> visitedResources) throws IOException {
        for(Map.Entry<PdfName, PdfObject> entry : xMap.entrySet()){
            if(entry.getValue().isIndirectReference()){
                if(((PdfIndirectReference)entry.getValue()).getRefersTo().isStream()){
                    addFontFromXObject(((PdfStream) ((PdfIndirectReference) entry.getValue()).getRefersTo()).getMap(), visitedResources);
                }
            }
        }
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
