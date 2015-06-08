package com.itextpdf.core.pdf;

import com.itextpdf.basics.PdfException;
import com.itextpdf.core.pdf.navigation.PdfDestination;
import java.util.ArrayList;
import java.util.List;

public class PdfOutline {

    private List<PdfOutline> children = new ArrayList<PdfOutline>();
    private String title;
    private PdfDictionary content;
    private PdfDestination destination;
    private PdfOutline parent;
    private PdfDocument pdfDoc;

    /**
     * This constructor creates root outline in the document.
     * @param doc
     * @throws PdfException
     */
    public PdfOutline(PdfDocument doc) {
        content = new PdfDictionary();
        content.put(PdfName.Type, PdfName.Outlines);
        this.pdfDoc = doc;
        content.makeIndirect(doc);
        doc.getCatalog().addRootOutline(this);
    }

    public PdfOutline(String title, PdfDictionary content, PdfDocument pdfDocument){
        this.title = title;
        this.content = content;
        this.pdfDoc = pdfDocument;
    }

    public PdfOutline(String title, PdfDictionary content, PdfOutline parent) {
        this.title = title;
        this.content = content;
        this.parent = parent;
        this.pdfDoc = parent.pdfDoc;
        content.makeIndirect(parent.pdfDoc);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title){
        this.title = title;
        this.content.put(PdfName.Title, new PdfString(title));
    }

    public PdfDictionary getContent() {
        return content;
    }

    public List<PdfOutline> getAllChildren(){
        return children;
    }

    public PdfOutline getParent(){
        return parent;
    }

    public PdfDestination getDestination() {
        return destination;
    }

    public void addDestination(PdfDestination destination){
        setDestination(destination);
        content.put(PdfName.Dest, destination.getPdfObject());
    }

    /**
     * Adds an <CODE>PdfOutline</CODE> as a child to existing <CODE>PdfOutline</CODE>
     * and put it in the end of the existing <CODE>PdfOutline</CODE> children list
     * @param title an outline title
     * @return a created outline
     * @throws PdfException
     */
    public PdfOutline addOutline(String title) {
        return addOutline(title, -1);
    }

    /**
     * Adds an {@code PdfOutline} as a child to existing <CODE>PdfOutline</CODE>
     * and put it to specified position in the existing <CODE>PdfOutline</CODE> children list
     * @param title an outline title
     * @param position a position in the current outline child List where a new outline should be added.
     *                 If the position equals -1, then the outline will be put in the end of children list.
     * @return created outline
     * @throws PdfException
     */
    public PdfOutline addOutline(String title, int position) {
        if (position == -1)
            position = children.size();
        PdfDictionary dictionary = new PdfDictionary();
        PdfOutline outline = new PdfOutline(title, dictionary, this);
        dictionary.put(PdfName.Title, new PdfString(title));
        dictionary.put(PdfName.Parent, content);
        if (children.size() != 0){
            if (position != 0){
                PdfDictionary prevContent = children.get(position-1).getContent();
                dictionary.put(PdfName.Prev, prevContent);
                prevContent.put(PdfName.Next, dictionary);
            }
            if (position != children.size()) {
                PdfDictionary nextContent = children.get(position).getContent();
                dictionary.put(PdfName.Next, nextContent);
                nextContent.put(PdfName.Prev, dictionary);
            }
        }

        if (position == 0)
            content.put(PdfName.First, dictionary);
        if (position == children.size())
            content.put(PdfName.Last, dictionary);

        if (children.size() > 0){
            int count = this.content.getAsInt(PdfName.Count);
            if (count > 0)
                content.put(PdfName.Count, new PdfNumber(count++));
            else
                content.put(PdfName.Count, new PdfNumber(count--));
        }

        else
            this.content.put(PdfName.Count, new PdfNumber(-1));
        children.add(position, outline);


        return outline;
    }

    void clear(){
        children.clear();
    }

    void setDestination(PdfDestination destination){
        this.destination = destination;
    }

    /**
     * remove this outline from the document.
      * @throws PdfException
     */
    void removeOutline() {
        PdfOutline parent = this.parent;
        List<PdfOutline> children = parent.children;
        children.remove(this);
        PdfDictionary parentContent = parent.content;
        if (children.size() != 0){
            parentContent.put(PdfName.First, children.get(0).content);
            parentContent.put(PdfName.Last, children.get(children.size()-1).content);
        }

        PdfDictionary next = content.getAsDictionary(PdfName.Next);
        PdfDictionary prev = content.getAsDictionary(PdfName.Prev);
        if (prev != null){
            if (next != null){
                prev.put(PdfName.Next, next);
                next.put(PdfName.Prev, prev);
            }
            else {
                prev.remove(PdfName.Next);
            }
        }
        else if (next != null){
            next.remove(PdfName.Prev);
        }
    }

    public PdfOutline addOutline(PdfOutline outline) {
        PdfOutline newOutline = addOutline(outline.getTitle());
        newOutline.addDestination(outline.getDestination());

        List<PdfOutline> children = outline.getAllChildren();
        for(PdfOutline child : children){
            newOutline.addOutline(child);
        }

        return newOutline;
    }
}
