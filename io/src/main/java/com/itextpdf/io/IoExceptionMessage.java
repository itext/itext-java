/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 Apryse Group NV
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
    public static final String IMAGE_MAGICK_OUTPUT_IS_NULL = "ImageMagick process output is null.";
    public static final String IMAGE_MAGICK_PROCESS_EXECUTION_FAILED =
            "ImageMagick process execution finished with errors: ";
}
