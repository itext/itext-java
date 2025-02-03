package com.itextpdf.kernel;

public class ITextSharpCustomMembers {
    public static final String enumeratorToList;
    public static final String deviceRgbDrawingConstructor;
    public static final String encryptionPropertiesRandomBytes;
    public static final String cloneRectangle;
    public static final String cloneAffineTransform;
    public static final String getEnumerator;

    static {
        getEnumerator =
                "        /// <summary><inheritDoc/></summary>\n" +
                        "        IEnumerator IEnumerable.GetEnumerator() {\n" +
                        "            return GetEnumerator();\n" +
                        "        }\n";
    }

    static {
        cloneRectangle =
                "        /// <summary>\n" +
                        "        /// Creates a \"deep copy\" of this rectangle, meaning the object returned by this method will be independent\n" +
                        "        /// of the object being cloned.\n" +
                        "        /// </summary>\n" +
                        "        /// <returns>the copied rectangle.</returns>\n" +
                        "        public virtual iText.Kernel.Geom.Rectangle Clone() {\n" +
                        "            return (iText.Kernel.Geom.Rectangle) MemberwiseClone();\n" +
                        "        }";
    }


    static {
        cloneAffineTransform =
                "        /// <summary>\n" +
                        "        /// Creates a \"deep copy\" of this AffineTransform, meaning the object returned by this method will be independent\n" +
                        "        /// of the object being cloned.\n" +
                        "        /// </summary>\n" +
                        "        /// <returns>the copied AffineTransform.</returns>\n" +
                        "        public virtual iText.Kernel.Geom.AffineTransform Clone() {\n" +
                        "            return (iText.Kernel.Geom.AffineTransform) MemberwiseClone();\n" +
                        "        }";
    }

    static {
        enumeratorToList =
                "        private static IList<T> EnumeratorToList<T>(IEnumerator<T> enumerator) {\n" +
                        "            IList<T> list = new List<T>();\n" +
                        "            while (enumerator.MoveNext()) {\n" +
                        "                list.Add(enumerator.Current);\n" +
                        "            }\n" +
                        "            return list;\n" +
                        "        }";
    }

    static {
        deviceRgbDrawingConstructor =
                "        /// <summary>\n" +
                        "        /// Create DeviceRGB color from R, G, B values of System.Drawing.Color\n" +
                        "        /// <br/>\n" +
                        "        /// Note, that alpha chanel is ignored, but opacity still can be achieved\n" +
                        "        /// in some places by using 'setOpacity' method or 'TransparentColor' class.\n" +
                        "        /// </summary>\n" +
                        "        /// <param name=\"color\">the color which RGB values are used</param>\n" +
                        "        public DeviceRgb(System.Drawing.Color color)\n" +
                        "            : this(color.R, color.G, color.B) {\n" +
                        "            if (color.A != 255) {\n" +
                        "                ILogger LOGGER = ITextLogManager.GetLogger(typeof(iText.Kernel.Colors.DeviceRgb));\n" +
                        "                LOGGER.LogWarning(MessageFormatUtil.Format(iText.IO.Logs.IoLogMessageConstant.COLOR_ALPHA_CHANNEL_IS_IGNORED, color.A));\n" +
                        "            }\n" +
                        "        }";
    }

    static {
        encryptionPropertiesRandomBytes =
                "        private static void RandomBytes(byte[] bytes) {\n" +
                        "            RandomNumberGenerator.Create().GetBytes(bytes);\n" +
                        "        }";
    }
}
