/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2021 iText Group NV
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
package com.itextpdf.io;

import com.itextpdf.io.util.GhostscriptHelper;
import com.itextpdf.io.util.ImageMagickHelper;

/**
 * Class containing constants to be used in exceptions in the IO module.
 */
public final class IoExceptionMessage {
    public static final String COMPARE_COMMAND_IS_NOT_SPECIFIED =
            "ImageMagick comparison command is not specified. Set the "
                    + ImageMagickHelper.MAGICK_COMPARE_ENVIRONMENT_VARIABLE
                    + " environment variable with the CLI command which can run the ImageMagic comparison. See BUILDING.MD in the root of the repository for more details.";
    public static final String COMPARE_COMMAND_SPECIFIED_INCORRECTLY =
            "ImageMagick comparison command specified incorrectly. Set the "
                    + ImageMagickHelper.MAGICK_COMPARE_ENVIRONMENT_VARIABLE
                    + " environment variable with the CLI command which can run the ImageMagic comparison. See BUILDING.MD in the root of the repository for more details.";
    public static final String GS_ENVIRONMENT_VARIABLE_IS_NOT_SPECIFIED =
            "Ghostscript command is not specified or specified incorrectly. Set the " + GhostscriptHelper.GHOSTSCRIPT_ENVIRONMENT_VARIABLE
                    + " environment variable to a CLI command that can run the Ghostscript application. See BUILDING.MD in the root of the repository for more details.";
    public static final String GHOSTSCRIPT_FAILED = "GhostScript failed for <filename>";
    public static final String CANNOT_OPEN_OUTPUT_DIRECTORY = "Cannot open output directory for <filename>";

}
