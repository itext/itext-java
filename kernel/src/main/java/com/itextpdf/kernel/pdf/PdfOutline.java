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
package com.itextpdf.kernel.pdf;

import com.itextpdf.kernel.PdfException;
import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.pdf.action.PdfAction;
import com.itextpdf.kernel.pdf.navigation.PdfDestination;

import java.util.ArrayList;
import java.util.List;

public class PdfOutline {

    public static int FLAG_ITALIC = 1;
    public static int FLAG_BOLD = 2;

    private List<PdfOutline> children = new ArrayList<>();
    private String title;
    private PdfDictionary content;
    private PdfDestination destination;
    private PdfOutline parent;
    private PdfDocument pdfDoc;

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

    /**
     * This constructor creates root outline in the document.
     * @param doc
     * @throws PdfException
     */
    protected PdfOutline(PdfDocument doc) {
        content = new PdfDictionary();
        content.put(PdfName.Type, PdfName.Outlines);
        this.pdfDoc = doc;
        content.makeIndirect(doc);
        doc.getCatalog().addRootOutline(this);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title){
        this.title = title;
        this.content.put(PdfName.Title, new PdfString(title));
    }

    public void setColor(Color color) {
        content.put(PdfName.C, new PdfArray(color.getColorValue()));
    }

    public void setStyle(int style) {
        if (style == FLAG_BOLD || style == FLAG_ITALIC ) {
            content.put(PdfName.F, new PdfNumber(style));
        }
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

    //@TODO implement adding actions
    public void addAction(PdfAction action) {
        content.put(PdfName.A, action.getPdfObject());
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
        if (!children.isEmpty()){
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
        PdfName type = content.getAsName(PdfName.Type);
        if (type != null && type.equals(PdfName.Outlines)) {
            pdfDoc.getCatalog().remove(PdfName.Outlines);
            return;
        }
        PdfOutline parent = this.parent;
        List<PdfOutline> children = parent.children;
        children.remove(this);
        PdfDictionary parentContent = parent.content;
        if (!children.isEmpty()){
            parentContent.put(PdfName.First, children.get(0).content);
            parentContent.put(PdfName.Last, children.get(children.size()-1).content);
        } else {
            parent.removeOutline();
            return;
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
