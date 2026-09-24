/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2026 Apryse Group NV
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

public class SharpenConfigCustomMembers {
    public static final String getWebPClass;

    static {
        getWebPClass =
                "        private static Type GetWebPClass(String partialName) {\n" +
                        "            String classFullName = null;\n" +
                        "\n" +
                        "            Assembly ioAssembly = typeof(ImageDataFactory).GetAssembly();\n" +
                        "            try {\n" +
                        "                string webPVersion = ioAssembly.GetName().Version.ToString();\n" +
                        "                string format = \"{0}, Version={1}, Culture=neutral, PublicKeyToken=8354ae6d2174ddca\";\n" +
                        "                classFullName = String.Format(format, partialName, webPVersion);\n" +
                        "            } catch (Exception ignored) {\n" +
                        "            }\n" +
                        "\n" +
                        "            Type type = null;\n" +
                        "            if (classFullName != null) {\n" +
                        "                String fileLoadExceptionMessage = null;\n" +
                        "                try {\n" +
                        "                    type = System.Type.GetType(classFullName);\n" +
                        "                } catch (FileLoadException fileLoadException) {\n" +
                        "                    fileLoadExceptionMessage = fileLoadException.Message;\n" +
                        "                }\n" +
                        "                if (type == null) {\n" +
                        "                    // try to find webp-image-support assembly by it's partial name and check if it refers to current version of itext core\n" +
                        "                    try {\n" +
                        "                        type = System.Type.GetType(partialName);\n" +
                        "                    } catch {\n" +
                        "                        // ignore\n" +
                        "                    }\n" +
                        "                    if (type != null) {\n" +
                        "                        bool doesReferToCurrentVersionOfCore = false;\n" +
                        "                        foreach (AssemblyName assemblyName in type.GetAssembly().GetReferencedAssemblies()) {\n" +
                        "                            if (\"itext.io\".Equals(assemblyName.Name)) {\n" +
                        "                                doesReferToCurrentVersionOfCore = assemblyName.Version.Equals(ioAssembly.GetName().Version);\n" +
                        "                                break;\n" +
                        "                            }\n" +
                        "                        }\n" +
                        "                        if (!doesReferToCurrentVersionOfCore) {\n" +
                        "                            type = null;\n" +
                        "                        }\n" +
                        "                    }\n" +
                        "                    if (type == null && fileLoadExceptionMessage != null) {\n" +
                        "                        LazyLogger logger = new LazyLogger(typeof(ImageDataFactory));\n" +
                        "                        logger.Error(() => fileLoadExceptionMessage);\n" +
                        "                    }\n" +
                        "                }\n" +
                        "            }\n" +
                        "\n" +
                        "            return type;\n" +
                        "        }";
    }
}
