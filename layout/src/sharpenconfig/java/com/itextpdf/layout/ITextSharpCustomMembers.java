package com.itextpdf.layout;

public class ITextSharpCustomMembers {
    public static final String getTypographyClass;
    public static final String cloneLayoutArea;
    public static final String cloneMarginsCollapse;

    static {
        getTypographyClass =
                "        private static Type GetTypographyClass(String partialName) {\n" +
                        "            String classFullName = null;\n" +
                        "\n" +
                        "            Assembly layoutAssembly = typeof(TypographyUtils).GetAssembly();\n" +
                        "            try {\n" +
                        "                Attribute customAttribute = layoutAssembly.GetCustomAttribute(typeof(TypographyVersionAttribute));\n" +
                        "                if (customAttribute is TypographyVersionAttribute) {\n" +
                        "                    string typographyVersion = ((TypographyVersionAttribute) customAttribute).TypographyVersion;\n" +
                        "                    string format = \"{0}, Version={1}, Culture=neutral, PublicKeyToken=8354ae6d2174ddca\";\n" +
                        "                    classFullName = String.Format(format, partialName, typographyVersion);\n" +
                        "                }\n" +
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
                        "                    // try to find typography assembly by it's partial name and check if it refers to current version of itext core\n" +
                        "                    try {\n" +
                        "                        type = System.Type.GetType(partialName);\n" +
                        "                    } catch {\n" +
                        "                        // ignore\n" +
                        "                    }\n" +
                        "                    if (type != null) {\n" +
                        "                        bool doesReferToCurrentVersionOfCore = false;\n" +
                        "                        foreach (AssemblyName assemblyName in type.GetAssembly().GetReferencedAssemblies()) {\n" +
                        "                            if (\"itext.io\".Equals(assemblyName.Name)) {\n" +
                        "                                doesReferToCurrentVersionOfCore = assemblyName.Version.Equals(layoutAssembly.GetName().Version);\n" +
                        "                                break;\n" +
                        "                            }\n" +
                        "                        }\n" +
                        "                        if (!doesReferToCurrentVersionOfCore) {\n" +
                        "                            type = null;\n" +
                        "                        }\n" +
                        "                    }\n" +
                        "                    if (type == null && fileLoadExceptionMessage != null) {\n" +
                        "                        ILogger logger = ITextLogManager.GetLogger(typeof(TypographyUtils));\n" +
                        "                        logger.LogError(fileLoadExceptionMessage);\n" +
                        "                    }\n" +
                        "                }\n" +
                        "            }\n" +
                        "\n" +
                        "            return type;\n" +
                        "        }";
    }

    static {
        cloneLayoutArea =
                "        /// <summary>\n" +
                        "        /// Creates a \"deep copy\" of this LayoutArea, meaning the object returned by this method will be independent\n" +
                        "        /// of the object being cloned.\n" +
                        "        /// </summary>\n" +
                        "        /// <returns>the copied LayoutArea.</returns>\n" +
                        "        public virtual iText.Layout.Layout.LayoutArea Clone() {\n" +
                        "            iText.Layout.Layout.LayoutArea clone = (iText.Layout.Layout.LayoutArea) MemberwiseClone();\n" +
                        "            clone.bBox = bBox.Clone();\n" +
                        "            return clone;\n" +
                        "        }";
    }

    static {
        cloneMarginsCollapse =
                "        /// <summary>\n" +
                        "        /// Creates a \"deep copy\" of this MarginsCollapse, meaning the object returned by this method will be independent\n" +
                        "        /// of the object being cloned.\n" +
                        "        /// </summary>\n" +
                        "        /// <returns>the copied MarginsCollapse.</returns>\n" +
                        "        public virtual MarginsCollapse Clone() {\n" +
                        "            return (iText.Layout.Margincollapse.MarginsCollapse) MemberwiseClone();\n" +
                        "        }";
    }

}
