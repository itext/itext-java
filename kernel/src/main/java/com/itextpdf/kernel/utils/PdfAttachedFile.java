/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2017 iText Group NV
    Authors: iText Software.

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
package com.itextpdf.kernel.utils;

import com.itextpdf.kernel.pdf.*;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class represents an attachment in a PDF document
 */
public class PdfAttachedFile extends File {

    // root level variables
    private PdfDocument pdfDocument = null;
    private List<PdfAttachedFile> children = new ArrayList<>();

    // leaf level variables
    private PdfAttachedFile parent = null;
    private String name = "";
    private byte[] bytes = new byte[]{};

    private PdfAttachedFile(PdfAttachedFile parent, String name, PdfObject obj)
    {
        super("");
        this.parent = parent;
        this.name = name;
        this.bytes = (obj!=null && obj.isStream()) ? ((PdfStream) obj).getBytes() : new byte[]{};
    }

    public PdfAttachedFile(PdfDocument pdfDocument)
    {
        super("");

        this.pdfDocument = pdfDocument;

        // traverse structure
        PdfDictionary rootCatalog = pdfDocument.getCatalog().getPdfObject();
        PdfDictionary names = rootCatalog.getAsDictionary(PdfName.Names);
        if(names == null)
            return;

        PdfDictionary embeddedFiles = names.getAsDictionary(PdfName.EmbeddedFiles);
        if(embeddedFiles == null)
            return;

        PdfArray namesArray = embeddedFiles.getAsArray(PdfName.Names);
        if(namesArray == null)
            return;

        // get all attachments
        Map<PdfIndirectReference, String> attachments = new HashMap<>();
        for(int i=0;i<namesArray.size();i+=2)
        {
            String name = namesArray.getAsString(i).toUnicodeString();
            PdfIndirectReference ref = namesArray.getAsDictionary(i+1).getIndirectReference();
            attachments.put(ref, name);
        }

        Map<PdfIndirectReference, PdfObject> objs = new HashMap<>();
        for(int i=0;i<pdfDocument.getNumberOfPdfObjects();i++)
        {
            PdfObject obj = pdfDocument.getPdfObject(i);
            if(obj == null)
                continue;
            PdfIndirectReference ref = obj.getIndirectReference();
            objs.put(ref, obj);
        }

        // create separate resource-files
        for(Map.Entry<PdfIndirectReference, PdfObject> en : objs.entrySet())
        {
            if(attachments.containsKey(en.getKey()))
            {
                PdfIndirectReference ref = en.getKey();
                PdfObject obj = en.getValue();
                if(obj.isDictionary())
                {
                    PdfDictionary dict = (PdfDictionary) obj;
                    if(     dict.containsKey(PdfName.EF) &&
                            dict.getAsDictionary(PdfName.EF) != null &&
                            dict.getAsDictionary(PdfName.EF).containsKey(PdfName.F))
                        obj = objs.get(dict.getAsDictionary(PdfName.EF).get(PdfName.F).getIndirectReference());
                }
                children.add(new PdfAttachedFile(this, attachments.get(ref), obj));
            }
        }
    }

    /*
     * permissions
     */

    public boolean canExecute(){ return false; }
    public boolean canRead(){ return true; }
    public boolean canWrite(){ return false; }

    /*
     * (unsupported) file operations
     */

    public boolean createNewFile(){return false;}
    public boolean delete(){return false;}
    public void deleteOnExit(){}
    public boolean mkdir(){return false;}
    public boolean mkdirs(){return false;}
    public boolean renameTo(File dest){return false;}
    public boolean setExecutable(boolean executable){return false;}
    public boolean setExecutable(boolean executable, boolean ownerOnly){return false;}
    public boolean setLastModified(long time){return false;}
    public boolean setReadable(boolean readable){return false;}
    public boolean setReadable(boolean readable, boolean ownerOnly){return false;}
    public boolean setWritable(boolean readable){return false;}
    public boolean setWritable(boolean readable, boolean ownerOnly){return false;}

    /*
     * structure
     */
    public boolean exists(){return true;}
    public boolean isDirectory(){ return false; }
    public File getAbsoluteFile(){return this;}
    public String getAbsolutePath(){return getPath();}
    public File getCanonicalFile(){return this;}
    public String getCanonicalPath(){return getPath();}
    public String getName(){return name;}
    public String getParent(){return parent == null ? null : parent.getPath();}
    public File getParentFile(){return parent;}

    public String[] list()
    {
        return list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return true;
            }
        });
    }
    public String[] list(FilenameFilter filter)
    {
        File[] fs = listFiles(filter);
        String[] names = new String[fs.length];
        for(int i=0;i<fs.length;i++)
            names[i] = fs[i].getName();
        return names;
    }
    public File[] listFiles()
    {
        return listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return true;
            }
        });
    }
    public File[] listFiles(FilenameFilter filter) {
        List<PdfAttachedFile> retval = new ArrayList<>();
        for (PdfAttachedFile child : children)
        {
            if (filter.accept(this, child.getName())) {
                retval.add(child);
            }
        }
        return retval.toArray(new File[retval.size()]);
    }

    /*
     * size
     */
    public long getFreeSpace(){return 0;}
    public long length(){return bytes.length;}

    /*
     * equality
     */

    public boolean equals(Object o)
    {
        if(o instanceof PdfAttachedFile)
        {
            PdfAttachedFile pdfFile = ((PdfAttachedFile) o);
            if(pdfFile.parent != null)
                return pdfFile.parent.equals(parent);
            else
                return pdfFile.bytes.equals(bytes);
        }
        return false;
    }

    /*
     * IO
     */
    public byte[] getBytes(){return bytes;}

    /*
     * string
     */
    public String toString(){return parent == null ? pdfDocument.toString() : parent.pdfDocument.toString() + "/" + name;}
}