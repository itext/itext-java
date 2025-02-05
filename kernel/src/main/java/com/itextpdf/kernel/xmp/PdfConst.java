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
package com.itextpdf.kernel.xmp;

/**
 * Class that contains several constants.
 */
public class PdfConst {
    /** External Contributors to the resource (other than the authors). */
    public static final String Contributor = "contributor";
    /** The extent or scope of the resource. */
    public static final String Coverage = "coverage";
    /** The authors of the resource (listed in order of precedence, if significant). */
    public static final String Creator = "creator";
    /** Date(s) that something interesting happened to the resource. */
    public static final String Date = "date";
    /** A textual description of the content of the resource. Multiple values may be present for different languages. */
    public static final String Description = "description";
    /** The file format used when saving the resource. Tools and applications should set this property to the save format of the data. It may include appropriate qualifiers. */
    public static final String Format = "format";
    /** An unordered array of text strings that unambiguously identify the resource within a given context. */
    public static final String Identifier = "identifier";
    /** An unordered array specifying the languages used in the	resource. */
    public static final String Language = "language";
    /** Publishers. */
    public static final String Publisher = "publisher";
    /** Relationships to other documents. */
    public static final String Relation = "relation";
    /** Informal rights statement, selected by language. */
    public static final String Rights = "rights";
    /** Unique identifier of the work from which this resource was derived. */
    public static final String Source = "source";
    /** An unordered array of descriptive phrases or keywords that specify the topic of the content of the resource. */
    public static final String Subject = "subject";
    /** The title of the document, or the name given to the resource. Typically, it will be a name by which the resource is formally known. */
    public static final String Title = "title";
    /** A document type; for example, novel, poem, or working paper. */
    public static final String Type = "type";
    /** Keywords. */
    public static final String Keywords = "Keywords";
    /** The PDF file version (for example: 1.0, 1.3, and so on). */
    public static final String Version = "PDFVersion";
    /** The Producer. */
    public static final String Producer = "Producer";
    /** The part */
    public static final String Part = "part";
    /** An unordered array specifying properties that were edited outside the authoring application. Each item should contain a single namespace and XPath separated by one ASCII space (U+0020). */
    public static final String Advisory = "Advisory";
    /** The base URL for relative URLs in the document content. If this document contains Internet links, and those links are relative, they are relative to this base URL. This property provides a standard way for embedded relative URLs to be interpreted by tools. Web authoring tools should set the value based on their notion of where URLs will be interpreted. */
    public static final String BaseURL = "BaseURL";
    /** The date and time the resource was originally created. */
    public static final String CreateDate = "CreateDate";
    /** The name of the first known tool used to create the resource. If history is present in the metadata, this value should be equivalent to that of xmpMM:History's softwareAgent property. */
    public static final String CreatorTool = "CreatorTool";
    /** The date and time that any metadata for this resource was last changed. */
    public static final String MetadataDate = "MetadataDate";
    /** The date and time the resource was last modified. */
    public static final String ModifyDate = "ModifyDate";
    /** A short informal name for the resource. */
    public static final String Nickname = "Nickname";
    /** An alternative array of thumbnail images for a file, which can differ in characteristics such as size or image encoding. */
    public static final String Thumbnails = "Thumbnails";
    /** Indicates whether the document has been modified to include trapping information */
    public static final String Trapped = "Trapped";

}
