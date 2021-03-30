/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2021 iText Group NV
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
package com.itextpdf.layout.property;

/**
 * A specialized enum holding the possible values for an object-fit property
 * which define the way of fitting the image into the content box with different size.
 */
public enum ObjectFit {
    /**
     * If object-fit set to FILL, image will be sized to fill the element's content box. This can
     * change the aspect-ratio of the image.
     */
    FILL,
    /**
     * If object-fit set to CONTAIN, image will be scaled keeping its aspect ratio to fit in
     * the content box. The whole picture will be rendered in the document but some are of the
     * image container might be blank.
     */
    CONTAIN,
    /**
     * If object-fit set to COVER, image will be scaled keeping its aspect ratio to cover
     * the content box. The image will be clipped to fit the container's bounds.
     */
    COVER,
    /**
     * If object-fit set to SCALE_DOWN, image will be scaled keeping its aspect ratio to fit in
     * the content box but scaling coefficient cannot be greater than 1. If content box is greater
     * than the image, picture will be rendered in its original size leaving the rest area of
     * the container blank.
     */
    SCALE_DOWN,
    /**
     * If object-fit set to NONE, image will not be scaled. It will keep its original size. If
     * the content box is greater than image it will contain blank areas, otherwise picture will
     * be clipped to fit the container's bounds.
     */
    NONE
}
