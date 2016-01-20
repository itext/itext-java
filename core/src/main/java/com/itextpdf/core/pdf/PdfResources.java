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

    private Map<PdfObject, PdfName> resourceToName = new HashMap<>();

    private ResourceNameGenerator fontNamesGen = new ResourceNameGenerator(PdfName.Font, F);
    private ResourceNameGenerator imageNamesGen = new ResourceNameGenerator(PdfName.XObject, Im);
    private ResourceNameGenerator formNamesGen = new ResourceNameGenerator(PdfName.XObject, Fm);
    private ResourceNameGenerator egsNamesGen = new ResourceNameGenerator(PdfName.ExtGState, Gs);
    private ResourceNameGenerator propNamesGen = new ResourceNameGenerator(PdfName.Properties, Pr);
    private ResourceNameGenerator csNamesGen = new ResourceNameGenerator(PdfName.ColorSpace, Cs);
    private ResourceNameGenerator patternNamesGen = new ResourceNameGenerator(PdfName.Pattern, P);
    private ResourceNameGenerator shadingNamesGen = new ResourceNameGenerator(PdfName.Shading, Sh);

    private boolean readOnly = false;
    private boolean isModified = false;

    public PdfResources(PdfDictionary pdfObject) {
        super(pdfObject);
        buildResources(pdfObject);
    }


    public PdfResources() {
        this(new PdfDictionary());
    }

    /**
     * Add font to resources and register PdfFont in the document for further flushing.
     * @return font resource name.
     */
    public PdfName addFont(PdfDocument pdfDocument, PdfFont font) {
        pdfDocument.getDocumentFonts().add(font);
        return addResource(font, fontNamesGen);
    }

    public PdfName addImage(PdfImageXObject image) {
        return addResource(image, imageNamesGen);
    }

    public PdfName addImage(PdfObject image) {
        return addResource(image, imageNamesGen);
    }

    public PdfName addForm(PdfFormXObject form) {
        return addResource(form, formNamesGen);
    }

    public PdfName addForm(PdfObject form) {
        return addResource(form, formNamesGen);
    }

    /**
     * Adds the given Form XObject to the current instance of {@link PdfResources}.
     * @param form Form XObject.
     * @param name Preferred name for the given Form XObject.
     * @return the {@link PdfName} of the newly added resource
     */
    public PdfName addForm(PdfFormXObject form, PdfName name) {
        if (getResourceNames(PdfName.XObject).contains(name)) {
           name = addResource(form, formNamesGen);
        } else {
           addResource(form.getPdfObject(), PdfName.XObject, name);
        }

        return name;
    }

    public PdfName addExtGState(PdfExtGState extGState) {
        return addResource(extGState, egsNamesGen);
    }

    public PdfName addExtGState(PdfObject extGState) {
        return addResource(extGState, egsNamesGen);
    }

    public PdfName addProperties(PdfObject properties) {
        return addResource(properties, propNamesGen);
    }

    public PdfName addColorSpace(PdfColorSpace cs) {
        return addResource(cs, csNamesGen);
    }

    public PdfName addColorSpace(PdfObject colorSpace) {
        return addResource(colorSpace, csNamesGen);
    }

    public PdfName addPattern(PdfPattern pattern) {
        return addResource(pattern, patternNamesGen);
    }

    public PdfName addPattern(PdfObject pattern) {
        return addResource(pattern, patternNamesGen);
    }

    public PdfName addShading(PdfShading shading) {
        return addResource(shading, shadingNamesGen);
    }

    public PdfName addShading(PdfObject shading) {
        return addResource(shading, shadingNamesGen);
    }

    protected boolean isReadOnly() {
        return readOnly;
    }

    protected void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    protected boolean isModified() {
        return isModified;
    }

    protected void setModified(boolean isModified) {
        this.isModified = isModified;
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
        Set<PdfName> names = new TreeSet<>(); // TODO: isn't it better to use HashSet? Do we really need certain order?
        for (PdfName resType : getPdfObject().keySet()) {
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
        PdfDictionary resourceCategory = getPdfObject().getAsDictionary(resType);
        return resourceCategory == null ? new TreeSet<PdfName>() : resourceCategory.keySet(); // TODO: TreeSet...
    }

    public PdfDictionary getResource(PdfName pdfName) {
        return getPdfObject().getAsDictionary(pdfName);
    }

//    public List<PdfDictionary> getFonts(boolean updateFonts) throws IOException {
//        if (updateFonts) {
//            getPdfObject().remove(PdfName.Font);
//            PdfDictionary fMap = getResource(PdfName.Font);
//            if (fMap != null) {
//                addFont(fMap.entrySet());
//            }
//            PdfDictionary xMap = getResource(PdfName.XObject);
//            if (xMap != null && !xMap.isEmpty()) {
//                callXObjectFont(xMap.entrySet(), new HashSet<PdfDictionary>());
//            }
//        }
//        List<PdfDictionary> fonts = new ArrayList<>();
//        for (PdfObject fontDict : getPdfObject().getAsDictionary(PdfName.Font).values()) {
//            if (fontDict.isDictionary()) {
//                fonts.add((PdfDictionary) fontDict);
//            }
//        }
//        return fonts;
//    }


    protected PdfName addResource(PdfObjectWrapper resource, ResourceNameGenerator nameGen) {
        return addResource(resource.getPdfObject(), nameGen);
    }

    protected void addResource(PdfObject resource, PdfName resType, PdfName resName) {
        if (readOnly) {
            setPdfObject(new PdfDictionary(getPdfObject()));
            buildResources(getPdfObject());
            isModified = true;
            readOnly = false;
        }
        if (getPdfObject().containsKey(resType) && getPdfObject().getAsDictionary(resType).containsKey(resName))
            return;
        resourceToName.put(resource, resName);
        PdfDictionary resourceCategory = getPdfObject().getAsDictionary(resType);
        if (resourceCategory == null) {
            getPdfObject().put(resType, resourceCategory = new PdfDictionary());
        }
        resourceCategory.put(resName, resource);
        PdfDictionary resDictionary = (PdfDictionary) getPdfObject().get(resType);
        if (resDictionary == null) {
            getPdfObject().put(resType, resDictionary = new PdfDictionary());
        }
        resDictionary.put(resName, resource);
    }

    protected PdfName addResource(PdfObject resource, ResourceNameGenerator nameGen) {
        PdfName resName = getResourceName(resource);

        if (resName == null) {
            resName = nameGen.generate();
            addResource(resource, nameGen.getResourceType(), resName);
        }

        return resName;
    }

    protected void buildResources(PdfDictionary dictionary) {
        for (PdfName resourceType : dictionary.keySet()) {
            if (getPdfObject().get(resourceType) == null) {
                getPdfObject().put(resourceType, new PdfDictionary());
            }

            PdfDictionary resources = dictionary.getAsDictionary(resourceType);

            if (resources == null) {
                continue;
            }

            for (PdfName resourceName : resources.keySet()) {
                PdfObject resource = resources.get(resourceName, false);
                resourceToName.put(resource, resourceName);
                getPdfObject().getAsDictionary(resourceType).put(resourceName, resource);
            }
        }
    }

//    private void addFont(Collection<PdfObject> entrySet) throws IOException {
//        for (PdfObject entry : entrySet) {
//            PdfDictionary fonts = getPdfObject().getAsDictionary(PdfName.Font);
//            if (entry.isIndirectReference() && !fonts.containsValue(entry)) {
//                fonts.put((PdfIndirectReference) entry.getValue(),
//                        PdfFont.createFont((PdfDictionary) ((PdfIndirectReference) entry.getValue()).getRefersTo()));
//            } else if (entry.getValue().isDictionary()) {
//                PdfFont font = PdfFont.createFont((PdfDictionary) entry.getValue());
//                fontsMap.put(font.getPdfObject().getIndirectReference(), font);
//            }
//        }
//    }

//    private void addFontFromXObject(Set<Map.Entry<PdfName, PdfObject>> entrySet, Set<PdfDictionary> visitedResources) throws IOException {
//        PdfDictionary xObject = new PdfDictionary(entrySet);
//        PdfDictionary resources = xObject.getAsDictionary(PdfName.Resources);
//        if (resources == null)
//            return;
//        PdfDictionary font = resources.getAsDictionary(PdfName.Font);
//
//        if (font != null) {
//            addFont(font.values());
//        }
//        PdfDictionary xobj = resources.getAsDictionary(PdfName.XObject);
//        if (xobj != null) {
//            if (visitedResources.add(xobj)) {
//                callXObjectFont(xobj.entrySet(), visitedResources);
//                visitedResources.remove(xobj);
//            } else {
//                throw new PdfException(PdfException.IllegalResourceTree);
//            }
//        }
//    }

//    private void callXObjectFont(Set<Map.Entry<PdfName, PdfObject>> entrySet, Set<PdfDictionary> visitedResources) throws IOException {
//        for (Map.Entry<PdfName, PdfObject> entry : entrySet) {
//            if (entry.getValue().isIndirectReference()) {
//                if (((PdfIndirectReference) entry.getValue()).getRefersTo().isStream()) {
//                    addFontFromXObject(((PdfStream) ((PdfIndirectReference) entry.getValue()).getRefersTo()).entrySet(), visitedResources);
//                }
//            }
//        }
//    }

    /**
     * Represents a resource name generator. The generator takes into account
     * the names of already existing resources thus providing us a unique name.
     * The name consists of the following parts: prefix (literal) and number.
     */
    private class ResourceNameGenerator {

        private PdfName resourceType;
        private int counter;
        private String prefix;

        /**
         * Constructs an instance of {@link ResourceNameGenerator} class.
         * @param resourceType Type of resource ({@link PdfName#XObject}, {@link PdfName#Font} etc).
         * @param prefix Prefix used for generating names.
         * @param seed Seed for the value which is appended to the number each time
         *             new name is generated.
         */
        public ResourceNameGenerator(PdfName resourceType, String prefix, int seed) {
            this.prefix = prefix;
            this.resourceType = resourceType;
            this.counter = seed;
        }

        /**
         * Constructs an instance of {@link ResourceNameGenerator} class.
         * @param resourceType Type of resource ({@link PdfName#XObject}, {@link PdfName#Font} etc).
         * @param prefix Prefix used for generating names.
         */
        public ResourceNameGenerator(PdfName resourceType, String prefix) {
            this(resourceType, prefix, 1);
        }

        public PdfName getResourceType() {
            return resourceType;
        }

        /**
         * Generates new (unique) resource name.
         * @return New (unique) resource name.
         */
        public PdfName generate() {
            PdfName newName = new PdfName(prefix + counter++);

            if (getPdfObject().containsKey(resourceType)) {
                while (getPdfObject().getAsDictionary(resourceType).containsKey(newName)) {
                    newName = new PdfName(prefix + counter++);
                }
            }

            return newName;
        }
    }
}
